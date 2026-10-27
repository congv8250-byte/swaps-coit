<p align="center"><img src="docs/logo.png" width="160" alt="Swaps Coit"></p>

# Swaps Coit

Mod **Fabric** client-side cho **Minecraft Java 26.2**.

Nhấn một phím → nếu trong **hotbar** có **giáo (spear)** thì đổi sang giáo ngay để lướt/đánh.
Dùng xong (thả chuột) mod **tự động quay lại ô đang cầm trước đó** sau khoảng 0.3 giây.
Nếu đổi sang giáo mà không dùng, tự quay lại sau 1.5 giây. Nhấn phím lần nữa để quay lại ngay.

- Phím mặc định: **R** (đổi trong *Options → Controls → Key Binds → Swaps Coit*)
- Hỗ trợ mọi loại giáo (gỗ, đá, đồng, sắt, vàng, kim cương, netherite)
- Nếu có nhiều giáo, chọn giáo gần ô đang cầm nhất
- Chỉ cần cài ở client, không cần cài lên server

## Yêu cầu
- Minecraft Java 26.2
- Fabric Loader ≥ 0.19.3
- Fabric API cho 26.2
- Java 25

## Build bằng GitHub
1. Tạo repo mới trên GitHub, đẩy toàn bộ thư mục này lên (nhánh `main`).
2. Vào tab **Actions** → workflow **build** tự chạy → tải file `.jar` ở mục **Artifacts**.
3. Muốn tạo Release có sẵn file jar: 
   ```
   git tag v1.0.0
   git push origin v1.0.0
   ```
   workflow **release** sẽ build và đính kèm jar vào trang Releases.

## Build trên máy
```
gradle wrapper --gradle-version 9.3.1   # chạy 1 lần để tạo gradlew
./gradlew build
```
File jar nằm ở `build/libs/` (dùng file không có `-sources`).

## Thử trong game
```
./gradlew runClient
```

## Giấy phép
MIT
