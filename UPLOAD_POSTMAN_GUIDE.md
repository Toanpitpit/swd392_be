# 📤 Upload API - Postman Testing Guide

## Postman Collection Import

### Step 1: Import Collection
1. Open Postman
2. Click **Import** button (top left)
3. Choose **Upload_API_Collection.json** file
4. Collection will be added to your workspace

---

## Environment Setup

### Prerequisites
Before testing, you need to:
1. **Start the application**: `./mvnw.cmd spring-boot:run`
2. **Configure AWS S3**:
   - Update `.env` file with AWS credentials
   - Ensure S3 bucket exists
3. **Login first**: Get a JWT token from auth endpoint

### Set Environment Variables in Postman

#### Method 1: Manual Setup
1. Create a new **Environment** named "Car Rental Dev"
2. Add these variables:
   ```
   baseUrl: localhost:8080/api
   token: <your_jwt_token_here>
   ```

#### Method 2: Via Login
1. First, login using Auth API to get token
2. Copy the token from response
3. Set it in environment: `{{token}}`

---

## API Endpoints Guide

### 1️⃣ Upload Single File
```
POST /api/uploads/file
```

**Parameters:**
- `file` (form-data): The file to upload (required)
- `folderPath` (form-data): Folder in S3 bucket (default: "files")

**Example Request:**
```bash
curl --request POST 'http://localhost:8080/api/uploads/file' \
  --header 'Authorization: Bearer <token>' \
  --form 'file=@/path/to/file.pdf' \
  --form 'folderPath=documents'
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "File uploaded successfully",
  "data": {
    "fileUrl": "documents/550e8400-e29b-41d4-a716-446655440000.pdf",
    "fileName": "file.pdf",
    "fileSize": "2048000"
  }
}
```

**Error Response (400):**
```json
{
  "success": false,
  "message": "Validation error: File type not allowed: exe",
  "data": null
}
```

---

### 2️⃣ Upload Multiple Files
```
POST /api/uploads/files
```

**Parameters:**
- `files` (form-data): Multiple files (required)
- `folderPath` (form-data): Folder in S3 bucket (default: "files")

**Postman Steps:**
1. Select the **Upload Multiple Files** request
2. Go to **Body** tab
3. Add multiple files:
   - Key: `files`
   - Type: File
   - Select multiple files using Ctrl+Click

**Success Response (200):**
```json
{
  "success": true,
  "message": "Files uploaded successfully",
  "data": {
    "fileUrls": [
      "images/550e8400-e29b-41d4-a716-446655440000.jpg",
      "images/550e8400-e29b-41d4-a716-446655440001.png"
    ],
    "totalFiles": 2
  }
}
```

---

### 3️⃣ Upload Single Video
```
POST /api/uploads/video
```

**Parameters:**
- `video` (form-data): Video file to upload (required)
- `folderPath` (form-data): Folder in S3 bucket (default: "videos")

**Supported Formats:** mp4, avi, mov, mkv, flv, wmv, webm, 3gp

**Max Size:** 5GB (5368709120 bytes)

**Success Response (200):**
```json
{
  "success": true,
  "message": "Video uploaded successfully",
  "data": {
    "videoUrl": "videos/550e8400-e29b-41d4-a716-446655440000.mp4",
    "videoName": "intro.mp4",
    "videoSize": "104857600"
  }
}
```

---

### 4️⃣ Upload Multiple Videos
```
POST /api/uploads/videos
```

**Parameters:**
- `videos` (form-data): Multiple video files (required)
- `folderPath` (form-data): Folder in S3 bucket (default: "videos")

**Success Response (200):**
```json
{
  "success": true,
  "message": "Videos uploaded successfully",
  "data": {
    "videoUrls": [
      "videos/550e8400-e29b-41d4-a716-446655440000.mp4",
      "videos/550e8400-e29b-41d4-a716-446655440001.mov"
    ],
    "totalVideos": 2
  }
}
```

---

### 5️⃣ Generate Presigned URL
```
GET /api/uploads/presigned-url?fileKey=documents/file.pdf&expirationMinutes=60
```

**Query Parameters:**
- `fileKey` (required): Path to file in S3 bucket
- `expirationMinutes` (optional): URL expiration time (default: 60)

**Use Cases:**
- Generate temporary download links
- Share files without exposing full S3 URL
- Time-limited access to sensitive files

**Success Response (200):**
```json
{
  "success": true,
  "message": "Presigned URL generated successfully",
  "data": {
    "presignedUrl": "https://your-bucket.s3.amazonaws.com/documents/file.pdf?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=...",
    "expirationMinutes": "60"
  }
}
```

**How to Use Presigned URL:**
1. Copy the `presignedUrl` from response
2. Paste in browser to download file
3. URL expires after specified minutes

---

### 6️⃣ Check File Exists
```
GET /api/uploads/exists?fileKey=documents/file.pdf
```

**Query Parameters:**
- `fileKey` (required): Path to file in S3 bucket

**Success Response (200):**
```json
{
  "success": true,
  "message": "File exists",
  "data": {
    "fileKey": "documents/file.pdf",
    "exists": true
  }
}
```

---

### 7️⃣ Delete File
```
DELETE /api/uploads/delete?fileKey=documents/file.pdf
```

**Query Parameters:**
- `fileKey` (required): Path to file to delete

**Success Response (200):**
```json
{
  "success": true,
  "message": "File deleted successfully",
  "data": null
}
```

**Important:** This action is irreversible!

---

## Testing Workflow

### Complete Upload Workflow

#### Step 1: Login and Get Token
```
1. Go to Auth API collection
2. Call Login endpoint
3. Copy JWT token from response
4. Set token in environment variables: {{token}}
```

#### Step 2: Upload Single File
```
1. Select "Upload Single File" request
2. Go to Body → Select file
3. Click Send
4. Check response and get fileUrl
5. fileUrl automatically stored in {{uploadedFileUrl}}
```

#### Step 3: Generate Presigned URL
```
1. Select "Generate Presigned URL" request
2. Change fileKey to your uploaded file
3. Set expirationMinutes (e.g., 30)
4. Click Send
5. Copy presignedUrl and test in browser
```

#### Step 4: Verify File Exists
```
1. Select "Check File Exists" request
2. Verify file exists with "exists": true
```

#### Step 5: Delete File (Optional)
```
1. Select "Delete File" request
2. Click Send
3. File is permanently deleted from S3
```

---

## File Size & Type Restrictions

### Regular Files
**Max Size:** 50MB (52428800 bytes)

**Allowed Types:**
- Documents: pdf, doc, docx, xls, xlsx, txt
- Images: jpg, jpeg, png, gif
- Archives: zip

**Configuration:** See `.env` file
```
AWS_S3_MAX_FILE_SIZE=52428800
AWS_S3_ALLOWED_FILE_TYPES=jpg,jpeg,png,pdf,doc,docx,xls,xlsx,txt,zip
```

### Videos
**Max Size:** 5GB (5368709120 bytes)

**Allowed Types:** mp4, avi, mov, mkv, flv, wmv, webm, 3gp

**Configuration:** See `.env` file
```
AWS_S3_MAX_VIDEO_SIZE=5368709120
AWS_S3_ALLOWED_VIDEO_TYPES=mp4,avi,mov,mkv,flv,wmv,webm,3gp
```

---

## Common Errors & Solutions

### ❌ Error: "File is empty"
**Cause:** Selected file size is 0 bytes
**Solution:** Choose a valid file with content

### ❌ Error: "File type not allowed: exe"
**Cause:** File extension not in allowed list
**Solution:** Use supported file types only

### ❌ Error: "File size exceeds maximum allowed size"
**Cause:** File is larger than limit
**Solution:** 
- For files: Max 50MB
- For videos: Max 5GB

### ❌ Error: "Invalid or expired token"
**Cause:** JWT token missing or expired
**Solution:** 
1. Login again to get new token
2. Update {{token}} in environment

### ❌ Error: "NoSuchBucket"
**Cause:** AWS S3 bucket name incorrect
**Solution:** 
1. Check `.env` file
2. Verify bucket exists in AWS console
3. Ensure AWS credentials are correct

### ❌ Error: "InvalidAccessKeyId"
**Cause:** AWS credentials invalid
**Solution:**
1. Check AWS Access Key & Secret Key in `.env`
2. Regenerate credentials in AWS console
3. Update `.env` file

---

## Testing Tips

### 1. Use Test Scripts
Pre-request and test scripts are included in collection:
- Automatically set environment variables
- Log responses to console
- Validate response structure

### 2. Check Console Output
```
Postman → View → Show Postman Console
```
View all requests/responses and logs here

### 3. Save Responses
Right-click response → Save as example
Use for documentation and debugging

### 4. Postman Variables
Current variables in collection:
- `{{baseUrl}}`: API base URL
- `{{token}}`: JWT authentication token
- `{{uploadedFileUrl}}`: Last uploaded file path
- `{{uploadedVideoUrl}}`: Last uploaded video path
- `{{presignedUrl}}`: Last generated presigned URL

### 5. Test Different File Types
Test with:
- Small files (< 1MB)
- Large files (> 10MB)
- Different formats (pdf, jpg, mp4, etc.)

---

## Performance Notes

### Upload Speed Factors
1. **File Size**: Larger files take longer
2. **Network**: Internet speed affects transfer
3. **AWS S3**: Region latency
4. **Folder Structure**: Deeper paths may be slightly slower

### Optimization Tips
- Upload multiple files in batch (faster than one-by-one)
- Use closest AWS region
- Compress files before upload if possible
- Use multipart upload for large files (>100MB)

---

## Security Best Practices

✅ **DO:**
- Use JWT token for authentication
- Set appropriate expiration for presigned URLs
- Validate file types on server (already done)
- Limit file sizes (already configured)
- Use HTTPS in production

❌ **DON'T:**
- Commit AWS credentials to Git
- Share presigned URLs publicly
- Upload malicious files
- Disable authentication
- Allow unlimited file sizes

---

## Reference

### API Documentation
- Base URL: `http://localhost:8080/api`
- API Context: `/uploads`
- Full URLs: `http://localhost:8080/api/uploads/*`

### Environment File Location
- `.env` - Local environment variables
- `.env.example` - Template file

### Collection File
- Location: `postman/Upload_API_Collection.json`
- Import into Postman using Import button

---

**Happy Testing! 🚀**

