# 📤 Upload API - Complete Documentation Summary

## 🎯 What Has Been Created

### 1. **UploadController.java** ✅
- REST controller with 7 endpoints for file/video uploads
- Location: `src/main/java/fa/training/car_rental_management/controllers/UploadController.java`
- Handles all upload operations with proper error handling

### 2. **Postman Collection** ✅
- Complete collection with all 7 upload endpoints
- Location: `postman/Upload_API_Collection.json`
- Includes test scripts and automatic variable management

### 3. **Postman Environment** ✅
- Environment template for Postman
- Location: `postman/Upload_API_Environment.json`
- Pre-configured variables for easy testing

### 4. **Documentation** ✅
- **UPLOAD_POSTMAN_GUIDE.md** - Detailed Postman guide
- **UPLOAD_CURL_EXAMPLES.md** - cURL command examples & scripts
- Complete examples for all use cases

---

## 📋 API Endpoints Summary

| # | Method | Endpoint | Purpose |
|---|--------|----------|---------|
| 1 | POST | `/api/uploads/file` | Upload single file |
| 2 | POST | `/api/uploads/files` | Upload multiple files |
| 3 | POST | `/api/uploads/video` | Upload single video |
| 4 | POST | `/api/uploads/videos` | Upload multiple videos |
| 5 | DELETE | `/api/uploads/delete` | Delete file from S3 |
| 6 | GET | `/api/uploads/presigned-url` | Generate presigned URL |
| 7 | GET | `/api/uploads/exists` | Check file existence |

---

## 🚀 Quick Start - Testing Upload API

### Step 1: Setup Postman
```
1. Open Postman
2. Click Import
3. Select: postman/Upload_API_Collection.json
4. Select: postman/Upload_API_Environment.json
5. Collection is ready to use
```

### Step 2: Set Environment Variables
```
1. Select "Car Rental - Upload API Environment"
2. Set these variables:
   - baseUrl: http://localhost:8080/api
   - token: <your_jwt_token_from_login>
   - bucketName: <your-s3-bucket-name>
```

### Step 3: Start Testing
```
1. Select any request from collection
2. Click "Send"
3. View response
4. Variables auto-update for next requests
```

---

## 📝 File/Video Configuration

### From `.env` File

```env
# File Upload
AWS_S3_MAX_FILE_SIZE=52428800  # 50MB
AWS_S3_ALLOWED_FILE_TYPES=jpg,jpeg,png,pdf,doc,docx,xls,xlsx,txt,zip

# Video Upload
AWS_S3_MAX_VIDEO_SIZE=5368709120  # 5GB
AWS_S3_ALLOWED_VIDEO_TYPES=mp4,avi,mov,mkv,flv,wmv,webm,3gp
```

---

## 🔒 Security Features

✅ **JWT Authentication** - All endpoints require valid token
✅ **File Validation** - Type and size checks
✅ **Presigned URLs** - Time-limited access to files
✅ **AWS S3** - Secure cloud storage
✅ **Error Handling** - Comprehensive error messages

---

## 📚 Documentation Files

### For Postman Testing
- **UPLOAD_POSTMAN_GUIDE.md**
  - Complete Postman guide
  - Import instructions
  - Endpoint documentation
  - Error troubleshooting

### For cURL Testing
- **UPLOAD_CURL_EXAMPLES.md**
  - All cURL command examples
  - Batch upload scripts
  - Workflow automation
  - Error handling examples

---

## 🧪 Testing Workflows

### Workflow 1: Single File Upload
```
1. Call "Upload Single File"
2. Automatically stores fileUrl in {{uploadedFileUrl}}
3. Use that URL in next operations
```

### Workflow 2: Complete File Lifecycle
```
1. Upload file → Get fileUrl
2. Check if exists → Verify file uploaded
3. Generate presigned URL → Get temporary link
4. Delete file → Remove from S3
```

### Workflow 3: Batch Upload & Management
```
1. Upload multiple files → Get array of URLs
2. Generate presigned URLs for each
3. Share temporary URLs
4. Delete after expiration
```

---

## 💡 Usage Examples

### Postman - Upload Single File
```
1. Select "Upload Single File" request
2. Go to Body tab
3. Select a PDF or JPG file
4. Set folderPath (e.g., "documents")
5. Click Send
6. See fileUrl in response
```

### cURL - Upload Single File
```bash
curl --request POST 'http://localhost:8080/api/uploads/file' \
  --header 'Authorization: Bearer <token>' \
  --form 'file=@/path/to/file.pdf' \
  --form 'folderPath=documents'
```

### Postman - Generate Presigned URL
```
1. Select "Generate Presigned URL" request
2. Set fileKey to uploaded file path
3. Set expirationMinutes (e.g., 60)
4. Click Send
5. Copy presignedUrl and share
```

---

## ⚙️ Configuration

### Application Properties
All values use environment variables from `.env`:
```properties
aws.s3.bucket-name=${AWS_S3_BUCKET_NAME}
aws.s3.access-key=${AWS_S3_ACCESS_KEY}
aws.s3.secret-key=${AWS_S3_SECRET_KEY}
```

### Update `.env` for Your Setup
```env
AWS_S3_ACCESS_KEY=your_actual_key
AWS_S3_SECRET_KEY=your_actual_secret
AWS_S3_REGION=us-east-1
AWS_S3_BUCKET_NAME=your-bucket-name
```

---

## 🐛 Troubleshooting

### Issue: "Token not set"
**Solution:**
1. Login first (use Auth API)
2. Copy JWT token from response
3. Set in Postman environment: {{token}}

### Issue: "InvalidAccessKeyId"
**Solution:**
1. Check AWS credentials in `.env`
2. Verify in AWS console
3. Update and restart application

### Issue: "File type not allowed"
**Solution:**
- Check `AWS_S3_ALLOWED_FILE_TYPES` in `.env`
- Only use allowed extensions

### Issue: "File size exceeds maximum"
**Solution:**
- For files: Max 50MB (check `AWS_S3_MAX_FILE_SIZE`)
- For videos: Max 5GB (check `AWS_S3_MAX_VIDEO_SIZE`)

---

## 📊 Response Examples

### Success Response (200 OK)
```json
{
  "success": true,
  "message": "File uploaded successfully",
  "data": {
    "fileUrl": "documents/550e8400-e29b-41d4-a716-446655440000.pdf",
    "fileName": "document.pdf",
    "fileSize": "2048000"
  }
}
```

### Error Response (400 Bad Request)
```json
{
  "success": false,
  "message": "Validation error: File type not allowed: exe",
  "data": null
}
```

### Error Response (401 Unauthorized)
```json
{
  "success": false,
  "message": "Invalid or expired token",
  "data": null
}
```

---

## 📂 File Structure

```
car_rental_management/
├── src/
│   └── main/
│       └── java/
│           └── fa/training/car_rental_management/
│               ├── controllers/
│               │   └── UploadController.java          ✅ NEW
│               └── services/
│                   └── impl/
│                       └── UploadServiceImpl.java
├── postman/
│   ├── Upload_API_Collection.json                   ✅ NEW
│   └── Upload_API_Environment.json                  ✅ NEW
├── UPLOAD_POSTMAN_GUIDE.md                          ✅ NEW
├── UPLOAD_CURL_EXAMPLES.md                          ✅ NEW
└── ...other files...
```

---

## 🔗 Integration with Other Services

### Vehicle Photo Upload
```
Use: POST /api/uploads/file
Folder: vehicle-photos/{vehicleId}
Allowed: jpg, jpeg, png
```

### Vehicle Document Upload
```
Use: POST /api/uploads/file
Folder: vehicle-documents/{vehicleId}
Allowed: pdf, doc, docx
```

### Inspection Photos
```
Use: POST /api/uploads/files
Folder: inspections/{inspectionId}
Allowed: jpg, jpeg, png
```

### User Documents
```
Use: POST /api/uploads/file
Folder: user-documents/{userId}
Allowed: pdf, jpg, png
```

---

## ✨ Best Practices

✅ **DO:**
- Validate files before upload
- Use appropriate folder paths
- Set reasonable presigned URL expiration
- Log upload activities
- Handle errors gracefully

❌ **DON'T:**
- Disable authentication
- Allow unlimited file sizes
- Store credentials in code
- Upload malicious files
- Share presigned URLs publicly for long

---

## 📞 Support

### Documentation
- **UPLOAD_POSTMAN_GUIDE.md** - Step-by-step Postman guide
- **UPLOAD_CURL_EXAMPLES.md** - cURL examples & scripts
- **This file** - Quick reference

### Testing Files
- Use small test files (< 1MB) for initial testing
- Test with different file types
- Verify error handling with invalid files

### Need Help?
1. Check the relevant documentation file
2. Look at example requests in Postman collection
3. Try cURL examples to understand API behavior

---

## 🎉 You're All Set!

Your upload API is ready to use:
- ✅ UploadController with 7 endpoints
- ✅ Postman collection for testing
- ✅ Environment template configured
- ✅ Complete documentation
- ✅ cURL examples and scripts

**Start testing now!** Import the Postman collection and begin uploading files to AWS S3. 🚀

---

**Last Updated:** March 15, 2026
**Version:** 1.0
**Status:** Ready for Production

