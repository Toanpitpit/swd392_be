# 🚗 Vehicle Search API - Postman Test Guide

## 📋 Overview

This guide provides comprehensive instructions for testing the Vehicle Search API endpoints using Postman.

**Base URL:** `http://localhost:8080`

---

## 🔑 API Endpoints

### 1. GET /api/vehicles/available
**Lấy danh sách xe có status = AVAILABLE (với phân trang)**

**URL:** 
```
GET http://localhost:8080/api/vehicles/available?page=0&size=10
```

**Description:**
Truy vấn tất cả xe có trạng thái AVAILABLE, hỗ trợ phân trang

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| page | integer | false | 0 | Trang số (bắt đầu từ 0) |
| size | integer | false | 10 | Số xe trên 1 trang |

**Example Requests:**

#### Request 1: Lấy 10 xe đầu tiên
```
GET /api/vehicles/available?page=0&size=10
```

#### Request 2: Lấy trang thứ 2, mỗi trang 5 xe
```
GET /api/vehicles/available?page=1&size=5
```

#### Request 3: Lấy 20 xe trên trang đầu
```
GET /api/vehicles/available?page=0&size=20
```

**Response Success (200 OK):**
```json
{
  "status": "success",
  "message": "Available vehicles retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "vin": "ABCD1234567890ABC",
        "licensePlate": "50A-12345",
        "make": "Toyota",
        "model": "Camry 2024",
        "year": 2024,
        "transmissionType": "AUTOMATIC",
        "fuelType": "PETROL",
        "seatCount": 5,
        "description": "Luxury sedan",
        "status": "AVAILABLE",
        "basePrice": 100.0,
        "ownerId": 1,
        "city": "Hanoi"
      },
      {
        "id": 2,
        "vin": "WXYZ9876543210XYZ",
        "licensePlate": "51B-54321",
        "make": "Honda",
        "model": "Accord 2023",
        "year": 2023,
        "transmissionType": "AUTOMATIC",
        "fuelType": "PETROL",
        "seatCount": 5,
        "description": "Premium sedan",
        "status": "AVAILABLE",
        "basePrice": 85.0,
        "ownerId": 2,
        "city": "Ho Chi Minh"
      }
    ],
    "totalElements": 15,
    "totalPages": 2,
    "currentPage": 0,
    "pageSize": 10,
    "hasNext": true
  }
}
```

**Response Error (500 Internal Server Error):**
```json
{
  "status": "error",
  "message": "Failed to fetch available vehicles: Database connection error"
}
```

---

### 2. GET /api/vehicles/search
**Search xe với multiple filters (model, city, fuelType, status)**

**URL:**
```
GET http://localhost:8080/api/vehicles/search?model=Camry&city=Hanoi&fuelType=PETROL&status=AVAILABLE&page=0&size=10
```

**Description:**
Tìm kiếm xe dựa trên các filter tùy chọn. Hỗ trợ JOIN với bảng Address để tìm theo city.

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| model | string | false | - | Tìm kiếm model (LIKE search, case-insensitive) |
| city | string | false | - | Tìm kiếm thành phố (JOIN Address table) |
| fuelType | enum | false | - | Loại nhiên liệu: PETROL, DIESEL, HYBRID, ELECTRIC |
| status | enum | false | AVAILABLE | Trạng thái: AVAILABLE, BOOKED, MAINTENANCE |
| page | integer | false | 0 | Trang số |
| size | integer | false | 10 | Số xe trên 1 trang |

**Fuel Type Options:**
- `PETROL` - Xăng
- `DIESEL` - Dầu
- `HYBRID` - Lai
- `ELECTRIC` - Điện

**Status Options:**
- `AVAILABLE` - Sẵn có
- `BOOKED` - Đã đặt
- `MAINTENANCE` - Bảo dưỡng

---

## 📝 Test Scenarios

### Scenario 1: Tìm xe model "Camry" ở Hà Nội
**URL:**
```
GET /api/vehicles/search?model=Camry&city=Hanoi&page=0&size=10
```

**Curl:**
```bash
curl -X GET "http://localhost:8080/api/vehicles/search?model=Camry&city=Hanoi&page=0&size=10" \
  -H "Content-Type: application/json"
```

**Postman Steps:**
1. Method: `GET`
2. URL: `http://localhost:8080/api/vehicles/search`
3. Params tab:
   - Key: `model`, Value: `Camry`
   - Key: `city`, Value: `Hanoi`
   - Key: `page`, Value: `0`
   - Key: `size`, Value: `10`
4. Click Send

**Expected Response:**
```json
{
  "status": "success",
  "message": "Search completed successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "model": "Camry 2024",
        "city": "Hanoi",
        "status": "AVAILABLE",
        "basePrice": 100.0,
        ...
      }
    ],
    "totalElements": 3,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10,
    "hasNext": false
  }
}
```

---

### Scenario 2: Tìm xe Diesel ở TP. HCM
**URL:**
```
GET /api/vehicles/search?fuelType=DIESEL&city=Ho%20Chi%20Minh&page=0&size=10
```

**Curl:**
```bash
curl -X GET "http://localhost:8080/api/vehicles/search?fuelType=DIESEL&city=Ho%20Chi%20Minh&page=0&size=10" \
  -H "Content-Type: application/json"
```

**Postman Steps:**
1. Method: `GET`
2. URL: `http://localhost:8080/api/vehicles/search`
3. Params tab:
   - Key: `fuelType`, Value: `DIESEL`
   - Key: `city`, Value: `Ho Chi Minh`
   - Key: `page`, Value: `0`
   - Key: `size`, Value: `10`
4. Click Send

---

### Scenario 3: Tìm xe Electric có status AVAILABLE
**URL:**
```
GET /api/vehicles/search?fuelType=ELECTRIC&status=AVAILABLE&page=0&size=5
```

**Curl:**
```bash
curl -X GET "http://localhost:8080/api/vehicles/search?fuelType=ELECTRIC&status=AVAILABLE&page=0&size=5" \
  -H "Content-Type: application/json"
```

**Postman Steps:**
1. Method: `GET`
2. URL: `http://localhost:8080/api/vehicles/search`
3. Params tab:
   - Key: `fuelType`, Value: `ELECTRIC`
   - Key: `status`, Value: `AVAILABLE`
   - Key: `page`, Value: `0`
   - Key: `size`, Value: `5`
4. Click Send

---

### Scenario 4: Search toàn bộ (tất cả filter)
**URL:**
```
GET /api/vehicles/search?model=Accord&city=Hanoi&fuelType=PETROL&status=AVAILABLE&page=0&size=10
```

**Curl:**
```bash
curl -X GET "http://localhost:8080/api/vehicles/search?model=Accord&city=Hanoi&fuelType=PETROL&status=AVAILABLE&page=0&size=10" \
  -H "Content-Type: application/json"
```

**Postman Steps:**
1. Method: `GET`
2. URL: `http://localhost:8080/api/vehicles/search`
3. Params tab:
   - Key: `model`, Value: `Accord`
   - Key: `city`, Value: `Hanoi`
   - Key: `fuelType`, Value: `PETROL`
   - Key: `status`, Value: `AVAILABLE`
   - Key: `page`, Value: `0`
   - Key: `size`, Value: `10`
4. Click Send

---

### Scenario 5: Pagination - Lấy trang thứ 2
**URL:**
```
GET /api/vehicles/search?fuelType=PETROL&page=1&size=10
```

**Curl:**
```bash
curl -X GET "http://localhost:8080/api/vehicles/search?fuelType=PETROL&page=1&size=10" \
  -H "Content-Type: application/json"
```

**Note:** 
- `page=0` = Trang đầu tiên
- `page=1` = Trang thứ 2
- `size=10` = 10 xe trên 1 trang

---

### Scenario 6: Tìm xe mà model chứa "2024"
**URL:**
```
GET /api/vehicles/search?model=2024&page=0&size=10
```

**Curl:**
```bash
curl -X GET "http://localhost:8080/api/vehicles/search?model=2024&page=0&size=10" \
  -H "Content-Type: application/json"
```

**Note:** Model search dùng LIKE pattern, nên "2024" sẽ match "Camry 2024", "Accord 2024", etc.

---

## 🔍 Response Structure

### Success Response (200 OK)
```json
{
  "status": "success",
  "message": "Search completed successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "vin": "VIN_STRING",
        "licensePlate": "50A-12345",
        "make": "Toyota",
        "model": "Camry 2024",
        "year": 2024,
        "transmissionType": "AUTOMATIC",
        "fuelType": "PETROL",
        "seatCount": 5,
        "description": "Description",
        "status": "AVAILABLE",
        "basePrice": 100.0,
        "ownerId": 1,
        "city": "Hanoi"
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10,
    "hasNext": false
  }
}
```

### Response Fields
| Field | Type | Description |
|-------|------|-------------|
| status | string | "success" hoặc "error" |
| message | string | Chi tiết thao tác |
| data | object | Dữ liệu Page chứa vehicles |
| content | array | Danh sách VehicleDTO |
| totalElements | number | Tổng số xe khớp filter |
| totalPages | number | Tổng số trang |
| currentPage | number | Trang hiện tại |
| pageSize | number | Số xe trên 1 trang |
| hasNext | boolean | Có trang tiếp theo không |

### VehicleDTO Fields
| Field | Type | Description |
|-------|------|-------------|
| id | integer | ID xe |
| vin | string | VIN code |
| licensePlate | string | Biển số xe |
| make | string | Hãng xe (Toyota, Honda, etc.) |
| model | string | Model xe |
| year | integer | Năm sản xuất |
| transmissionType | string | AUTOMATIC, MANUAL |
| fuelType | string | PETROL, DIESEL, HYBRID, ELECTRIC |
| seatCount | integer | Số chỗ ngồi |
| description | string | Mô tả |
| status | string | AVAILABLE, BOOKED, MAINTENANCE |
| basePrice | number | Giá thuê/ngày |
| ownerId | integer | ID chủ xe |
| city | string | Thành phố (từ Address) |

---

### Error Response (500 Internal Server Error)
```json
{
  "status": "error",
  "message": "Failed to search vehicles: Database connection error"
}
```

---

## 📊 Implementation Details

### JOIN Query Logic
Khi search theo `city` parameter:
```sql
SELECT DISTINCT v.* 
FROM vehicles v
JOIN address a ON v.id = a.vehicle_id
WHERE v.status = 'AVAILABLE'
  AND a.city LIKE '%Hanoi%'
  AND (other filters...)
```

### Specification Pattern
- Sử dụng `JpaSpecificationExecutor` cho dynamic queries
- Xây dựng predicate dựa trên các filter tham số
- Support LIKE search cho model (case-insensitive)
- Default status = AVAILABLE nếu không truyền

### DTO Conversion
- Vehicle Entity → VehicleDTO
- Fetch Address thông qua `addressRepository.findByVehicleId(vehicleId)`
- Extract `city` từ Address entity

---

## ✅ Test Checklist

- [ ] Test /available endpoint với default parameters
- [ ] Test /available endpoint với custom page size
- [ ] Test /search endpoint chỉ với model filter
- [ ] Test /search endpoint chỉ với city filter
- [ ] Test /search endpoint chỉ với fuelType filter
- [ ] Test /search endpoint chỉ với status filter
- [ ] Test /search endpoint với tất cả filters
- [ ] Test pagination - page=0, size=5
- [ ] Test pagination - page=1, size=10
- [ ] Test case-insensitive search (Model uppercase/lowercase)
- [ ] Test LIKE search (model chứa từng phần)
- [ ] Test khi không có kết quả
- [ ] Test error handling

---

## 🛠️ Troubleshooting

### Issue: Empty results
**Solution:** Kiểm tra:
- Có xe nào có status = AVAILABLE không
- Spelling city name chính xác
- Model name chính xác

### Issue: JOIN không hoạt động
**Solution:**
- Kiểm tra Address record có tồn tại không
- Verify `vehicleId` trong Address table khớp với Vehicle.id

### Issue: Database error
**Solution:**
- Kiểm tra Spring Data JPA configuration
- Verify `JpaSpecificationExecutor` được extend trong `VehicleRepository`

---

## 🚀 Performance Tips

1. **Limit page size:** Không nên size > 100
2. **Use filters:** Càng many filters thì query càng nhanh
3. **Add indices:** Dùng `@Column(name = "city", index = true)` trên Address
4. **Lazy loading:** Vehicle sử dụng `FetchType.LAZY` cho relationships

---

**Created:** March 16, 2026
**Last Updated:** March 16, 2026

