# 🎓 Hệ thống Diễn đàn Trao đổi Kiến thức Chuyên ngành

## 📋 Thông tin đồ án
- **Sinh viên:** Nguyễn Thanh Thiên
- **MSSV:** 110122161
- **Lớp:** DA22TTC
- **GVHD:** Phan Thị Phương Nam
- **Thời gian thực hiện:** 03/11/2025 - 28/12/2025

## 🛠️ Công nghệ sử dụng

| Thành phần | Công nghệ |
|------------|-----------|
| Backend | Java 17 + Spring Boot 3.2.0 |
| Template Engine | Thymeleaf |
| Database | MongoDB (NoSQL) |
| Security | Spring Security |
| Frontend | Bootstrap 5.3.2, Bootstrap Icons |
| Build Tool | Maven |

## 📁 Cấu trúc dự án

```
DEMO_CN/
├── pom.xml                                    # Cấu hình Maven
├── src/
│   └── main/
│       ├── java/com/example/forum/
│       │   ├── ForumApplication.java          # Main class
│       │   ├── config/
│       │   │   ├── SecurityConfig.java        # Cấu hình Spring Security
│       │   │   └── DataInitializer.java       # Khởi tạo dữ liệu mẫu
│       │   ├── controller/
│       │   │   ├── HomeController.java        # Trang chủ, tìm kiếm
│       │   │   ├── AuthController.java        # Đăng nhập, đăng ký
│       │   │   ├── CauHoiController.java      # Quản lý câu hỏi
│       │   │   ├── HoSoController.java        # Hồ sơ người dùng
│       │   │   └── AdminController.java       # Quản trị viên
│       │   ├── model/
│       │   │   ├── NguoiDung.java             # Collection Nguoidung
│       │   │   ├── CauHoi.java                # Collection Cauhoi
│       │   │   ├── CauTraLoi.java             # Collection Cautraloi
│       │   │   ├── VaiTro.java                # Embedded document vai trò
│       │   │   ├── ChuDe.java                 # Embedded document chủ đề
│       │   │   ├── ChuyenNganhEmbed.java      # Embedded document chuyên ngành (trong câu hỏi)
│       │   │   └── DinhKem.java               # Embedded document đính kèm
│       │   ├── repository/
│       │   │   ├── NguoiDungRepository.java
│       │   │   ├── CauHoiRepository.java
│       │   │   └── CauTraLoiRepository.java
│       │   ├── service/
│       │   │   ├── NguoiDungService.java
│       │   │   ├── CauHoiService.java
│       │   │   └── CauTraLoiService.java
│       │   └── security/
│       │       └── CustomUserDetailsService.java
│       └── resources/
│           ├── application.properties         # Cấu hình ứng dụng
│           └── templates/                     # Thymeleaf templates
│               ├── layout.html
│               ├── home.html
│               ├── dang-nhap.html
│               ├── dang-ky.html
│               ├── tim-kiem.html
│               ├── cau-hoi/
│               ├── ho-so/
│               └── admin/
```

## 🗄️ Thiết kế MongoDB

### Database: CSDL_CN

### Collection: Nguoidung
```json
{
    "_id": "ObjectId",
    "manguoidung": "string",
    "tendangnhap": "string (unique)",
    "matkhauhash": "string",
    "email": "string (unique)",
    "hoten": "string",
    "anhdaidien": "string",
    "gioithieu": "string",
    "trangthai": "string (hoatdong/bikhoa)",
    "ngaytao": "datetime",
    "lanhoatdongcuoi": "datetime",
    "vaitro": {
        "mavaitro": "string",
        "tenvaitro": "string"
    }
}
```

### Collection: Cauhoi
```json
{
    "_id": "ObjectId",
    "macauhoi": "string",
    "tieude": "string",
    "noidung": "string",
    "manguoidung": "string",
    "tennguoidung": "string",
    "ngaydang": "datetime",
    "ngaycapnhat": "datetime",
    "luotxem": "number",
    "daduocduyet": "boolean",
    "soluongbinhluan": "number",
    "dinhkem": [{
        "tenfile": "string",
        "duongdan": "string",
        "loaifile": "string",
        "kichthuoc": "number"
    }],
    "chude": {
        "machude": "string",
        "tenchude": "string"
    },
    "chuyennganh": {
        "machuyennganh": "string",
        "tenchuyennganh": "string"
    }
}
```

### Collection: Cautraloi
```json
{
    "_id": "ObjectId",
    "macautraloi": "string",
    "macauhoi": "string",
    "manguoidung": "string",
    "tennguoidung": "string",
    "noidung": "string",
    "ngaytraloi": "datetime"
}
```

## ⚙️ Cài đặt và Chạy

### Yêu cầu hệ thống
- Java 17+
- Maven 3.6+
- MongoDB 6.0+

### Bước 1: Cài đặt MongoDB
1. Tải và cài đặt MongoDB từ https://www.mongodb.com/try/download/community
2. Khởi động MongoDB service

### Bước 2: Clone và chạy dự án
```bash
# Di chuyển vào thư mục dự án
cd D:\DEMO_CN

# Chạy ứng dụng
mvn spring-boot:run
```

### Bước 3: Truy cập ứng dụng
- **URL:** http://localhost:8080
- **Tài khoản Admin mặc định:**
  - Username: `admin`
  - Password: `admin123`

## 🔑 Chức năng chính

### 👤 Người dùng
- ✅ Đăng ký tài khoản
- ✅ Đăng nhập / Đăng xuất
- ✅ Quản lý hồ sơ cá nhân
- ✅ Đổi mật khẩu

### ❓ Câu hỏi
- ✅ Xem danh sách câu hỏi
- ✅ Đặt câu hỏi mới
- ✅ Sửa câu hỏi của mình
- ✅ Xem chi tiết câu hỏi
- ✅ Tìm kiếm câu hỏi

### 💬 Trả lời
- ✅ Trả lời câu hỏi
- ✅ Xóa câu trả lời của mình

### 🔧 Quản trị (Admin)
- ✅ Dashboard thống kê
- ✅ Quản lý người dùng (khóa/mở khóa/xóa)
- ✅ Quản lý câu hỏi (duyệt/xóa)

## 📊 API Endpoints

| Method | URL | Mô tả |
|--------|-----|-------|
| GET | / | Trang chủ |
| GET | /dang-nhap | Trang đăng nhập |
| POST | /dang-nhap | Xử lý đăng nhập |
| GET | /dang-ky | Trang đăng ký |
| POST | /dang-ky | Xử lý đăng ký |
| POST | /dang-xuat | Đăng xuất |
| GET | /tim-kiem?q= | Tìm kiếm câu hỏi |
| GET | /cau-hoi/{id} | Xem chi tiết câu hỏi |
| GET | /cau-hoi/dang-moi | Form đăng câu hỏi |
| POST | /cau-hoi/dang-moi | Đăng câu hỏi mới |
| GET | /cau-hoi/{id}/sua | Form sửa câu hỏi |
| POST | /cau-hoi/{id}/sua | Cập nhật câu hỏi |
| POST | /cau-hoi/{id}/tra-loi | Trả lời câu hỏi |
| GET | /ho-so/{username} | Trang cá nhân |
| GET | /ho-so/chinh-sua | Chỉnh sửa hồ sơ |
| POST | /ho-so/chinh-sua | Lưu hồ sơ |
| GET | /admin | Dashboard admin |
| GET | /admin/nguoi-dung | Quản lý người dùng |
| GET | /admin/cau-hoi | Quản lý câu hỏi |
| GET | /admin/cau-hoi/cho-duyet | Câu hỏi chờ duyệt |

## 🎨 Screenshots

*(Thêm screenshots của ứng dụng sau khi chạy)*

## 📝 License

Dự án này được thực hiện cho mục đích học tập tại Trường Đại học.

---

© 2024 Nguyễn Thanh Thiên - DA22TTC
