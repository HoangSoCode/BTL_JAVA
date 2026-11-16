# – Web tìm kiếm nhà hàng lân cận (Spring Boot)

Tìm nhà hàng theo **mức giá** và **khoảng cách** (Haversine), kèm lọc theo ẩm thực, đánh giá, từ khoá;
sắp xếp, phân trang, bản đồ Leaflet/OpenStreetMap, trang chi tiết.

## Công nghệ
Java 17+ · Spring Boot 4.1.1 · Spring Data JPA (Specification) · Thymeleaf · H2 / MySQL · Vanilla JS + Leaflet

## Cấu trúc
```
src/main/java/com/restaurant/BTL_JAVA
├── BtlJavaApplication.java
├── config/       AppProperties, AppConfig, DataSeeder (dữ liệu mẫu)
├── controller/   WebController (trang HTML), RestaurantApiController (REST)
├── dto/          RestaurantSearchRequest, RestaurantResponse, PageResponse, ApiError
├── entity/       Restaurant
├── exception/    ResourceNotFoundException, GlobalExceptionHandler
├── repository/   RestaurantRepository, RestaurantSpecifications
├── service/      RestaurantService + impl/RestaurantServiceImpl
└── util/         GeoUtils (Haversine, bounding box)
src/main/resources
├── templates/    index.html, restaurant-detail.html, fragments/layout.html, error/404.html
├── static/       css/style.css, js/app.js
└── application.properties, application-mysql.properties
```

## Chạy (Windows)
Yêu cầu: **JDK 17 trở lên** (kiểm tra: `java -version`) và có Internet ở lần chạy đầu để Maven tải thư viện.

Cách dễ nhất: **bấm đúp `run.bat`**. Hoặc trong PowerShell, đứng đúng thư mục chứa `pom.xml`:
```
.\mvnw.cmd spring-boot:run
```
Khi thấy dòng `Started BtlJavaApplication`, mở http://localhost:8080
(Mac/Linux: `./run.sh`)

## Cơ sở dữ liệu
| Chế độ | Cách chạy | Ghi chú |
|---|---|---|
| **H2 (mặc định)** | `run.bat` | Không cần cài gì. Dữ liệu lưu trong thư mục `data/` nên tắt bật lại vẫn còn. Xoá thư mục `data/` để về dữ liệu mẫu ban đầu. |
| **MySQL** | `run-mysql.bat` | Cần MySQL chạy ở `localhost:3306`. DB `restaurant_db` tự tạo. Đặt biến môi trường `DB_USERNAME`, `DB_PASSWORD` (hoặc sửa `application-mysql.properties`). |

## API
| Method | URL | Mô tả |
|---|---|---|
| GET | `/api/restaurants/search` | Tìm kiếm |
| GET | `/api/restaurants/{id}` | Chi tiết |
| GET | `/api/restaurants/cuisines` | Danh sách loại ẩm thực |

Tham số `search`: `keyword, cuisine, minPrice, maxPrice, radiusKm, lat, lng, minRating, sort(distance|price_asc|price_desc|rating), page, size`

Ví dụ: `/api/restaurants/search?lat=21.2731&lng=106.1946&radiusKm=3&minPrice=50000&maxPrice=200000&sort=distance`

## Thuật toán tìm kiếm
1. DB lọc thô bằng `Specification`: giá (khoảng giá quán giao với khoảng người dùng chọn), ẩm thực, đánh giá, **bounding box** toạ độ.
2. Service tính khoảng cách **Haversine** chính xác, lọc theo bán kính, sắp xếp, phân trang.

## Tuỳ chỉnh
- Vị trí mặc định / bán kính: `app.search.*` trong `application.properties` (dữ liệu mẫu tự dịch theo vị trí này).
- Tắt dữ liệu mẫu: `app.seed.enabled=false`, rồi thêm nhà hàng thật vào bảng `restaurants`.
- Dữ liệu mẫu là **hư cấu**, chỉ để demo.

## Test
`.\\mvnw.cmd test` (GeoUtilsTest, RestaurantServiceTest dùng H2 in-memory riêng).
