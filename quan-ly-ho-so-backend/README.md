# quan-ly-ho-so-khoa-hoc
Hệ thống quản lý hồ sơ khoa học - Đồ án nhóm

# Quan ly ho so khoa hoc backend

Project Spring Boot Java 8.

## Chay project

1. Tao database MySQL ten `quan_ly_ho_so`.
2. Import file `src/main/resources/database/quanlihoso-with-council-attachment.sql`.
3. Sua username/password MySQL trong `src/main/resources/application.properties`.
4. Chay:

```bash
mvn clean install
mvn spring-boot:run
```

## API chinh

- POST `/api/councils`: Tao hoi dong.
- GET `/api/councils`: Lay danh sach hoi dong.
- GET `/api/councils/{id}/members`: Lay thanh vien hoi dong.
- POST `/api/councils/{councilId}/assignments`: Gan de tai/project cho hoi dong.
- POST `/api/councils/evaluations`: Cham diem va nhan xet.
- POST `/api/attachments/upload`: Upload file PDF.
- GET `/api/attachments/{attachmentId}`: Xem file PDF tren browser.
- GET `/api/attachments/{attachmentId}/download`: Tai file PDF.
