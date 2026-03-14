# 🧪 Postman Testing Guide - JWT Security

## 📥 Import Postman Collection

Tạo một Postman Collection với các requests sau:

---

## 1️⃣ LOGIN - Get JWT Token

**Request:**
```
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response (Success - 200):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJST0xFX0FETUlOIiwiaWF0IjoxNzEwNDI5NjAwLCJleHAiOjE3MTA1MTYwMDB9.xxxxx",
    "username": "admin",
    "role": "ROLE_ADMIN"
  }
}
```

**Save token to environment variable:**
1. Click "Tests" tab
2. Add:
```javascript
pm.environment.set("jwt_token", pm.response.json().data.token);
```

---

## 2️⃣ VERIFY TOKEN

**Request:**
```
GET http://localhost:8080/auth/verify
Authorization: Bearer {{jwt_token}}
```

**Response (Success - 200):**
```json
{
  "success": true,
  "message": "Token is valid",
  "data": "User: admin"
}
```

---

## 3️⃣ CREATE BOOKING (CUSTOMER ROLE)

**Login as Customer first:**
```
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "customer",
  "password": "customer123"
}
```

**Then Create Booking:**
```
POST http://localhost:8080/api/bookings
Authorization: Bearer {{jwt_token}}
Content-Type: application/json

{
  "vehicleId": 1,
  "customerId": 1,
  "startTime": "2026-03-20T09:00:00",
  "endTime": "2026-03-22T18:00:00"
}
```

**Expected Response (201):**
```json
{
  "success": true,
  "message": "Booking created successfully",
  "data": {
    "id": 1,
    "vehicleId": 1,
    "customerId": 1,
    "status": "PENDING_APPROVAL",
    "startTime": "2026-03-20T09:00:00",
    "endTime": "2026-03-22T18:00:00"
  }
}
```

---

## 4️⃣ GET ALL BOOKINGS

**Request:**
```
GET http://localhost:8080/api/bookings
Authorization: Bearer {{jwt_token}}
```

**Response (200):**
```json
{
  "success": true,
  "message": "All bookings retrieved successfully",
  "data": [
    {
      "id": 1,
      "vehicleId": 1,
      "customerId": 1,
      "status": "PENDING_APPROVAL"
    }
  ]
}
```

---

## 5️⃣ APPROVE BOOKING (CAR_OWNER ROLE)

**Login as Car Owner:**
```
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "car_owner",
  "password": "owner123"
}
```

**Then Approve Booking:**
```
PATCH http://localhost:8080/api/bookings/1/approve
Authorization: Bearer {{jwt_token}}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Booking approved successfully",
  "data": {
    "id": 1,
    "status": "APPROVED"
  }
}
```

---

## 6️⃣ REJECT BOOKING (CAR_OWNER ROLE)

```
PATCH http://localhost:8080/api/bookings/1/reject?reason=Vehicle+not+available
Authorization: Bearer {{jwt_token}}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Booking rejected successfully",
  "data": {
    "id": 1,
    "status": "REJECTED",
    "rejectionReason": "Vehicle not available"
  }
}
```

---

## 7️⃣ UPDATE BOOKING STATUS

```
PATCH http://localhost:8080/api/bookings/1/status?status=ACTIVE
Authorization: Bearer {{jwt_token}}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Booking status updated successfully",
  "data": {
    "id": 1,
    "status": "ACTIVE"
  }
}
```

---

## 8️⃣ ACTIVATE BOOKING (START RENTAL)

```
PATCH http://localhost:8080/api/bookings/1/activate
Authorization: Bearer {{jwt_token}}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Booking activated successfully (Rental Started)",
  "data": {
    "id": 1,
    "status": "ACTIVE"
  }
}
```

---

## 🔑 Test Cases - Authorization Errors

### ❌ Test 1: No Token

```
GET http://localhost:8080/api/bookings
```

**Response (403 Forbidden):**
```json
{
  "success": false,
  "message": "Access Denied"
}
```

---

### ❌ Test 2: Invalid Token

```
GET http://localhost:8080/api/bookings
Authorization: Bearer invalid_token_xyz
```

**Response (403 Forbidden):**
```json
{
  "success": false,
  "message": "Access Denied"
}
```

---

### ❌ Test 3: Wrong Role

**Login as Customer, then try to approve booking:**

```
PATCH http://localhost:8080/api/bookings/1/approve
Authorization: Bearer {{customer_jwt_token}}
```

**Response (403 Forbidden):**
```
Access Denied - Insufficient permissions
```

---

### ❌ Test 4: Expired Token

Update token expiration to 1 second in `application.properties`:
```properties
jwt.expiration=1000
```

Wait 1+ second, then use old token:
```
GET http://localhost:8080/api/bookings
Authorization: Bearer {{expired_token}}
```

**Response (403 Forbidden):**
```
Token validation failed
```

---

## 📌 Postman Environment Variables

Create these variables in Postman environment:

```json
{
  "base_url": "http://localhost:8080",
  "jwt_token": "",
  "admin_username": "admin",
  "admin_password": "admin123",
  "car_owner_username": "car_owner",
  "car_owner_password": "owner123",
  "customer_username": "customer",
  "customer_password": "customer123"
}
```

Use in requests:
```
{{base_url}}/auth/login
{{base_url}}/api/bookings
Authorization: Bearer {{jwt_token}}
```

---

## 📝 Pre-request Script (Postman)

Auto-login and set token before each request:

```javascript
// Only run if token is empty
if (!pm.environment.get('jwt_token') || pm.environment.get('jwt_token') === '') {
    const postRequest = {
        url: pm.environment.get('base_url') + '/auth/login',
        method: 'POST',
        header: 'Content-Type: application/json',
        body: {
            mode: 'raw',
            raw: JSON.stringify({
                username: pm.environment.get('admin_username'),
                password: pm.environment.get('admin_password')
            })
        }
    };
    
    pm.sendRequest(postRequest, function (err, response) {
        if (!err) {
            const jsonData = response.json();
            pm.environment.set('jwt_token', jsonData.data.token);
        }
    });
}
```

---

## 🧪 Full Test Workflow

1. **Login as Admin**
   - Request: `POST /auth/login`
   - Save token to `{{jwt_token}}`

2. **Verify Token**
   - Request: `GET /auth/verify`
   - Should return 200

3. **Create Booking (as Customer)**
   - Login as customer first
   - Request: `POST /api/bookings`
   - Should return 201 with booking data

4. **Get Booking**
   - Request: `GET /api/bookings/1`
   - Should return 200

5. **Approve Booking (as Car Owner)**
   - Login as car_owner first
   - Request: `PATCH /api/bookings/1/approve`
   - Should return 200

6. **Test Authorization Failure**
   - Try to create booking without token
   - Should return 403

---

## 📊 Request Headers Reference

### Public Endpoint (No Auth Required)
```
POST /auth/login
Content-Type: application/json
```

### Protected Endpoint (Auth Required)
```
GET /api/bookings
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

### With Role-Based Protection
```
PATCH /api/bookings/1/approve
Authorization: Bearer {jwt_token_with_car_owner_role}
Content-Type: application/json
```

---

## 🎯 Quick Test Commands

Copy & paste into Postman:

**Login:**
```
POST http://localhost:8080/auth/login
Content-Type: application/json

{"username":"admin","password":"admin123"}
```

**Get Bookings:**
```
GET http://localhost:8080/api/bookings
Authorization: Bearer YOUR_TOKEN_HERE
```

**Create Booking:**
```
POST http://localhost:8080/api/bookings
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json

{"vehicleId":1,"customerId":1,"startTime":"2026-03-20T09:00:00","endTime":"2026-03-22T18:00:00"}
```

---

## ✅ Expected Status Codes

| Scenario | Status | Response |
|----------|--------|----------|
| Valid request, valid auth | 200 | Data returned |
| Create success | 201 | Created data |
| No token | 403 | Access Denied |
| Invalid token | 403 | Access Denied |
| Wrong role | 403 | Insufficient permissions |
| Bad request | 400 | Error message |
| Not found | 404 | Not found |
| Server error | 500 | Error message |

---

**Happy Testing! 🚀**

