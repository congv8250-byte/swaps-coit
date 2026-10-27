package com.swapscoit;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

/**
 * Swaps Coit - nhấn phím 1 lần: chuyển sang ô có giáo (spear) trong hotbar để lướt/đánh,
 * dùng xong thì tự động quay lại ô đang cầm trước đó.
 */
public class SwapsCoitClient implements ClientModInitializer {
    public static final String MOD_ID = "swapscoit";
    private static final int HOTBAR_SIZE = 9;

    /** Số tick chờ sau khi dùng xong giáo rồi mới quay lại (6 tick = 0.3 giây). */
    private static final int RETURN_DELAY_TICKS = 6;
    /** Nếu đổi sang giáo mà không dùng gì, tự quay lại sau số tick này (30 tick = 1.5 giây). */
    private static final int IDLE_TIMEOUT_TICKS = 30;

    private KeyMapping swapKey;

    private boolean active = false;
    private int returnSlot = -1;
    private int spearSlot = -1;
    private boolean used = false;
    private int counter = 0;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "main"));

        swapKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.swapscoit.swap_spear",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                category));

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) {
            reset();
            return;
        }
        Inventory inventory = player.getInventory();

        while (swapKey.consumeClick()) {
            if (active) {
                // Nhấn lần nữa khi đang trong chế độ giáo -> quay lại ngay
                finish(inventory);
            } else {
                start(inventory);
            }
        }

        if (active) {
            tickActive(client, player, inventory);
        }
    }

    private void start(Inventory inventory) {
        int current = inventory.getSelectedSlot();
        if (isSpear(inventory.getItem(current))) {
            return; // đang cầm giáo rồi, không có ô nào để quay lại
        }
        int target = findSpearSlot(inventory, current);
        if (target == -1) {
            return;
        }
        returnSlot = current;
        spearSlot = target;
        used = false;
        counter = 0;
        active = true;
        inventory.setSelectedSlot(target);
    }

    private void tickActive(Minecraft client, LocalPlayer player, Inventory inventory) {
        // Người chơi tự đổi ô khác -> hủy, không ép quay lại
        if (inventory.getSelectedSlot() != spearSlot) {
            reset();
            return;
        }

        boolean busy = player.isUsingItem()
                || client.options.keyAttack.isDown()
                || client.options.keyUse.isDown();

        if (busy) {
            used = true;
            counter = 0;
            return;
        }

        counter++;
        int limit = used ? RETURN_DELAY_TICKS : IDLE_TIMEOUT_TICKS;
        if (counter >= limit) {
            finish(inventory);
        }
    }

    private void finish(Inventory inventory) {
        if (returnSlot >= 0 && returnSlot < HOTBAR_SIZE) {
            inventory.setSelectedSlot(returnSlot);
        }
        reset();
    }

    private void reset() {
        active = false;
        returnSlot = -1;
        spearSlot = -1;
        used = false;
        counter = 0;
    }

    /** Tìm ô có giáo gần ô hiện tại nhất (ưu tiên bên phải khi bằng khoảng cách). */
    private static int findSpearSlot(Inventory inventory, int current) {
        for (int offset = 1; offset < HOTBAR_SIZE; offset++) {
            int right = (current + offset) % HOTBAR_SIZE;
            if (isSpear(inventory.getItem(right))) {
                return right;
            }
            int left = Math.floorMod(current - offset, HOTBAR_SIZE);
            if (isSpear(inventory.getItem(left))) {
                return left;
            }
        }
        return -1;
    }

    /** Mọi loại giáo (wooden_spear ... netherite_spear) đều có id kết thúc bằng "_spear". */
    private static boolean isSpear(ItemStack stack) {
        return !stack.isEmpty()
                && BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().endsWith("_spear");
    }
}
