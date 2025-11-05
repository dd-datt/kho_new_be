# DỰ ÁN: QUẢN LÝ KHO

### 🧩 **Base URL:** `http://localhost:8080`

---

## 📘 Tổng quan

**Dự án QUẢN LÝ KHO** là hệ thống backend được xây dựng trên framework Spring Boot (Gradle) nhằm quản lý toàn bộ quy trình **nhập - xuất - tồn kho**, bao gồm:

- Quản lý phiếu nhập, phiếu xuất
- Quản lý sản phẩm và tồn kho
- Quản lý nhà cung cấp
- Quản lý người dùng (vai trò, quyền truy cập)
- Xác thực, phân quyền và xuất báo cáo PDF

Ứng dụng được thiết kế theo kiến trúc RESTful API, hỗ trợ **Swagger UI**

---

## 🧭 Mục lục

1. [Cấu trúc Modules](#cấu-trúc-modules)
2. [Tài liệu API](#tài-liệu-api)
3. [Quản lý Phiếu Nhập](#1-quản-lý-phiếu-nhập-apiimport-slips)
4. [Quản lý Phiếu Xuất](#2-quản-lý-phiếu-xuất-apiexport-slips)
5. [Quản lý Sản Phẩm](#3-quản-lý-sản-phẩm-apiproducts)
6. [Quản lý Nhà Cung Cấp](#4-quản-lý-nhà-cung-cấp-apisuppliers)
7. [Quản lý Người Dùng](#5-quản-lý-người-dùng-apiusers)
8. [Xác Thực & Đăng Nhập](#6-xác-thực--đăng-nhập-apiauth)
9. [Schemas Chính](#schemas-chính)

---

## 🧩 Cấu trúc Modules

| Module                     | Chức năng chính                                                  |
| -------------------------- | ---------------------------------------------------------------- |
| **Import Slip Management** | Quản lý phiếu nhập hàng, lọc theo tháng/năm, xuất PDF            |
| **Export Slip Management** | Quản lý phiếu xuất, lý do xuất, lọc theo tháng/năm               |
| **Product Management**     | Quản lý danh sách sản phẩm, tồn kho, tìm kiếm, cảnh báo tồn thấp |
| **Supplier Management**    | Quản lý thông tin nhà cung cấp, kiểm tra mã/email trùng lặp      |
| **User Management**        | Quản lý người dùng, phân quyền, kích hoạt/vô hiệu hóa tài khoản  |
| **Auth Controller**        | Đăng nhập, đăng ký, xác thực token JWT                           |

---

## 📄 Tài liệu API

- **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 1️⃣ Quản lý Phiếu Nhập (`/api/import-slips`)

| Method | Endpoint                               | Mô tả                              |
| ------ | -------------------------------------- | ---------------------------------- |
| GET    | `/api/import-slips`                    | Lấy tất cả phiếu nhập (phân trang) |
| GET    | `/api/import-slips/{id}`               | Lấy phiếu nhập theo ID             |
| POST   | `/api/import-slips`                    | Tạo phiếu nhập mới                 |
| PUT    | `/api/import-slips/{id}`               | Cập nhật phiếu nhập                |
| DELETE | `/api/import-slips/{id}`               | Xóa phiếu nhập                     |
| GET    | `/api/import-slips/{id}/export-pdf`    | Xuất phiếu nhập ra PDF             |
| GET    | `/api/import-slips/search`             | Tìm kiếm & lọc nâng cao            |
| GET    | `/api/import-slips/search/by-supplier` | Tìm kiếm theo nhà cung cấp         |
| GET    | `/api/import-slips/filter/by-year`     | Lọc theo năm                       |
| GET    | `/api/import-slips/filter/by-month`    | Lọc theo tháng và năm              |

---

## 2️⃣ Quản lý Phiếu Xuất (`/api/export-slips`)

| Method | Endpoint                             | Mô tả                              |
| ------ | ------------------------------------ | ---------------------------------- |
| GET    | `/api/export-slips`                  | Lấy tất cả phiếu xuất (phân trang) |
| GET    | `/api/export-slips/{id}`             | Lấy phiếu xuất theo ID             |
| POST   | `/api/export-slips`                  | Tạo phiếu xuất mới                 |
| PUT    | `/api/export-slips/{id}`             | Cập nhật phiếu xuất                |
| DELETE | `/api/export-slips/{id}`             | Xóa phiếu xuất                     |
| GET    | `/api/export-slips/{id}/export-pdf`  | Xuất phiếu xuất ra PDF             |
| GET    | `/api/export-slips/search`           | Tìm kiếm & lọc nâng cao            |
| GET    | `/api/export-slips/search/by-reason` | Tìm kiếm theo lý do                |
| GET    | `/api/export-slips/filter/by-year`   | Lọc theo năm                       |
| GET    | `/api/export-slips/filter/by-month`  | Lọc theo tháng và năm              |

---

## 3️⃣ Quản lý Sản Phẩm (`/api/products`)

| Method | Endpoint                              | Mô tả                               |
| ------ | ------------------------------------- | ----------------------------------- |
| GET    | `/api/products`                       | Lấy danh sách sản phẩm (phân trang) |
| GET    | `/api/products/{id}`                  | Lấy sản phẩm theo ID                |
| POST   | `/api/products`                       | Thêm sản phẩm mới                   |
| PUT    | `/api/products/{id}`                  | Cập nhật thông tin sản phẩm         |
| DELETE | `/api/products/{id}`                  | Xóa sản phẩm                        |
| PATCH  | `/api/products/{id}/stock`            | Cập nhật tồn kho                    |
| GET    | `/api/products/supplier/{supplierId}` | Lấy sản phẩm theo nhà cung cấp      |
| GET    | `/api/products/search`                | Tìm kiếm theo tên hoặc mã SKU       |
| GET    | `/api/products/low-stock`             | Lấy sản phẩm có tồn kho thấp        |

---

## 4️⃣ Quản lý Nhà Cung Cấp (`/api/suppliers`)

| Method | Endpoint                     | Mô tả                               |
| ------ | ---------------------------- | ----------------------------------- |
| GET    | `/api/suppliers`             | Lấy danh sách nhà cung cấp          |
| GET    | `/api/suppliers/{id}`        | Lấy thông tin nhà cung cấp theo ID  |
| POST   | `/api/suppliers`             | Tạo nhà cung cấp mới                |
| PUT    | `/api/suppliers/{id}`        | Cập nhật nhà cung cấp               |
| DELETE | `/api/suppliers/{id}`        | Xóa nhà cung cấp                    |
| GET    | `/api/suppliers/search`      | Tìm kiếm nhà cung cấp               |
| GET    | `/api/suppliers/check-email` | Kiểm tra email đã tồn tại           |
| GET    | `/api/suppliers/check-code`  | Kiểm tra mã nhà cung cấp đã tồn tại |

---

## 5️⃣ Quản lý Người Dùng (`/api/users`)

| Method | Endpoint                     | Mô tả                             |
| ------ | ---------------------------- | --------------------------------- |
| GET    | `/api/users`                 | Lấy danh sách người dùng          |
| GET    | `/api/users/{id}`            | Lấy người dùng theo ID            |
| POST   | `/api/users`                 | Tạo người dùng mới                |
| PUT    | `/api/users/{id}`            | Cập nhật thông tin người dùng     |
| DELETE | `/api/users/{id}`            | Xóa người dùng                    |
| PATCH  | `/api/users/{id}/role`       | Thay đổi vai trò                  |
| PATCH  | `/api/users/{id}/activate`   | Kích hoạt người dùng              |
| PATCH  | `/api/users/{id}/deactivate` | Vô hiệu hóa người dùng            |
| GET    | `/api/users/search`          | Tìm kiếm theo username hoặc email |
| GET    | `/api/users/role/{role}`     | Lọc người dùng theo vai trò       |

---

## 6️⃣ Xác Thực & Đăng Nhập (`/api/auth`)

| Method | Endpoint             | Mô tả                 |
| ------ | -------------------- | --------------------- |
| POST   | `/api/auth/register` | Đăng ký tài khoản     |
| POST   | `/api/auth/login`    | Đăng nhập             |
| POST   | `/api/auth/logout`   | Đăng xuất             |
| GET    | `/api/auth/validate` | Xác thực token hợp lệ |

---

## 🧱 Schemas Chính

| Tên Schema                                                                 | Mô tả                                 |
| -------------------------------------------------------------------------- | ------------------------------------- |
| `UserDTO`                                                                  | Thông tin người dùng                  |
| `SupplierDTO`                                                              | Thông tin nhà cung cấp                |
| `ProductDTO`                                                               | Dữ liệu sản phẩm                      |
| `ImportSlipRequestDTO`, `ImportSlipResponseDTO`                            | Phiếu nhập                            |
| `ExportSlipRequestDTO`, `ExportSlipResponseDTO`                            | Phiếu xuất                            |
| `RegisterRequest`, `LoginRequest`                                          | Dữ liệu xác thực                      |
| `PageProductDTO`, `PageImportSlipResponseDTO`, `PageExportSlipResponseDTO` | Phân trang dữ liệu                    |
| `PageableObject`, `SortObject`                                             | Cấu trúc hỗ trợ phân trang và sắp xếp |

---

## ⚙️ Công nghệ sử dụng

- **Java Spring Boot**
- **Spring Data JPA + Hibernate**
- **MySQL**
- **Swagger / OpenAPI 3**
- **JasperReports (Xuất PDF)**
