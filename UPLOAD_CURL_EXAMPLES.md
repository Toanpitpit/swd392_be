# 📝 Upload API - cURL Testing Examples

## Quick Reference - cURL Commands

### Prerequisites
```bash
# Make sure application is running
http://localhost:8080/api

# Get a valid JWT token first (from login endpoint)
TOKEN="your_jwt_token_here"
BUCKET="your-s3-bucket-name"
```

---

## 1️⃣ Upload Single File

### Simple File Upload
```bash
curl --request POST 'http://localhost:8080/api/uploads/file' \
  --header "Authorization: Bearer $TOKEN" \
  --form 'file=@/path/to/your/document.pdf' \
  --form 'folderPath=documents'
```

### Upload to Root
```bash
curl --request POST 'http://localhost:8080/api/uploads/file' \
  --header "Authorization: Bearer $TOKEN" \
  --form 'file=@/path/to/your/image.jpg'
```

### Response Example
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

---

## 2️⃣ Upload Multiple Files

### Using Multiple -F Flags
```bash
curl --request POST 'http://localhost:8080/api/uploads/files' \
  --header "Authorization: Bearer $TOKEN" \
  --form 'files=@/path/to/file1.jpg' \
  --form 'files=@/path/to/file2.png' \
  --form 'files=@/path/to/file3.pdf' \
  --form 'folderPath=mixed-files'
```

### Response Example
```json
{
  "success": true,
  "message": "Files uploaded successfully",
  "data": {
    "fileUrls": [
      "mixed-files/550e8400-e29b-41d4-a716-446655440000.jpg",
      "mixed-files/550e8400-e29b-41d4-a716-446655440001.png",
      "mixed-files/550e8400-e29b-41d4-a716-446655440002.pdf"
    ],
    "totalFiles": 3
  }
}
```

---

## 3️⃣ Upload Single Video

### Video Upload
```bash
curl --request POST 'http://localhost:8080/api/uploads/video' \
  --header "Authorization: Bearer $TOKEN" \
  --form 'video=@/path/to/your/intro.mp4' \
  --form 'folderPath=videos'
```

### Custom Folder
```bash
curl --request POST 'http://localhost:8080/api/uploads/video' \
  --header "Authorization: Bearer $TOKEN" \
  --form 'video=@/path/to/tutorial.mov' \
  --form 'folderPath=tutorials'
```

### Response Example
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

## 4️⃣ Upload Multiple Videos

### Multiple Video Upload
```bash
curl --request POST 'http://localhost:8080/api/uploads/videos' \
  --header "Authorization: Bearer $TOKEN" \
  --form 'videos=@/path/to/video1.mp4' \
  --form 'videos=@/path/to/video2.mov' \
  --form 'videos=@/path/to/video3.webm' \
  --form 'folderPath=content'
```

### Response Example
```json
{
  "success": true,
  "message": "Videos uploaded successfully",
  "data": {
    "videoUrls": [
      "content/550e8400-e29b-41d4-a716-446655440000.mp4",
      "content/550e8400-e29b-41d4-a716-446655440001.mov",
      "content/550e8400-e29b-41d4-a716-446655440002.webm"
    ],
    "totalVideos": 3
  }
}
```

---

## 5️⃣ Generate Presigned URL

### Basic Presigned URL (60 minutes expiration)
```bash
curl --request GET 'http://localhost:8080/api/uploads/presigned-url' \
  --header "Authorization: Bearer $TOKEN" \
  -G \
  --data-urlencode 'fileKey=documents/file.pdf' \
  --data-urlencode 'expirationMinutes=60'
```

### With Custom Expiration (30 minutes)
```bash
curl --request GET 'http://localhost:8080/api/uploads/presigned-url' \
  --header "Authorization: Bearer $TOKEN" \
  -G \
  --data-urlencode 'fileKey=videos/intro.mp4' \
  --data-urlencode 'expirationMinutes=30'
```

### URL Encoding (Alternative)
```bash
FILE_KEY="videos/intro.mp4"
ENCODED_KEY=$(echo -n "$FILE_KEY" | jq -sRr @uri)

curl --request GET "http://localhost:8080/api/uploads/presigned-url?fileKey=$ENCODED_KEY&expirationMinutes=60" \
  --header "Authorization: Bearer $TOKEN"
```

### Response Example
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

### Download Using Presigned URL
```bash
# Save to file
curl -o downloaded-file.pdf "https://your-bucket.s3.amazonaws.com/documents/file.pdf?X-Amz-Algorithm=..."

# Stream to stdout
curl "https://your-bucket.s3.amazonaws.com/documents/file.pdf?X-Amz-Algorithm=..."
```

---

## 6️⃣ Check File Exists

### Check Existence
```bash
curl --request GET 'http://localhost:8080/api/uploads/exists' \
  --header "Authorization: Bearer $TOKEN" \
  -G \
  --data-urlencode 'fileKey=documents/file.pdf'
```

### Response Example - File Exists
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

### Response Example - File Not Found
```json
{
  "success": true,
  "message": "File does not exist",
  "data": {
    "fileKey": "documents/missing.pdf",
    "exists": false
  }
}
```

### Check Multiple Files
```bash
# Create a script
for file in "documents/file1.pdf" "documents/file2.pdf" "documents/file3.pdf"; do
  echo "Checking: $file"
  curl --request GET 'http://localhost:8080/api/uploads/exists' \
    --header "Authorization: Bearer $TOKEN" \
    -G \
    --data-urlencode "fileKey=$file"
  echo ""
done
```

---

## 7️⃣ Delete File

### Delete Single File
```bash
curl --request DELETE 'http://localhost:8080/api/uploads/delete' \
  --header "Authorization: Bearer $TOKEN" \
  -G \
  --data-urlencode 'fileKey=documents/file.pdf'
```

### Response Example
```json
{
  "success": true,
  "message": "File deleted successfully",
  "data": null
}
```

### Delete Multiple Files (Loop)
```bash
# Bash script to delete multiple files
delete_files() {
  local files=("$@")
  for file in "${files[@]}"; do
    echo "Deleting: $file"
    curl --request DELETE 'http://localhost:8080/api/uploads/delete' \
      --header "Authorization: Bearer $TOKEN" \
      -G \
      --data-urlencode "fileKey=$file"
    echo ""
  done
}

# Usage
delete_files \
  "documents/file1.pdf" \
  "documents/file2.pdf" \
  "images/photo.jpg"
```

---

## Complete Workflow Script

### Save as `upload_workflow.sh`
```bash
#!/bin/bash

# Configuration
BASE_URL="http://localhost:8080/api"
TOKEN="your_jwt_token_here"

# Color output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== Upload API Workflow ===${NC}\n"

# Step 1: Upload file
echo -e "${BLUE}Step 1: Uploading file...${NC}"
UPLOAD_RESPONSE=$(curl -s --request POST "$BASE_URL/uploads/file" \
  --header "Authorization: Bearer $TOKEN" \
  --form 'file=@./test-file.pdf' \
  --form 'folderPath=documents')

echo "Response: $UPLOAD_RESPONSE"

# Extract file URL
FILE_URL=$(echo $UPLOAD_RESPONSE | jq -r '.data.fileUrl')
echo -e "${GREEN}✓ File uploaded: $FILE_URL${NC}\n"

# Step 2: Check if file exists
echo -e "${BLUE}Step 2: Checking if file exists...${NC}"
EXISTS_RESPONSE=$(curl -s --request GET "$BASE_URL/uploads/exists" \
  --header "Authorization: Bearer $TOKEN" \
  -G \
  --data-urlencode "fileKey=$FILE_URL")

echo "Response: $EXISTS_RESPONSE"
EXISTS=$(echo $EXISTS_RESPONSE | jq -r '.data.exists')
echo -e "${GREEN}✓ File exists: $EXISTS${NC}\n"

# Step 3: Generate presigned URL
echo -e "${BLUE}Step 3: Generating presigned URL...${NC}"
PRESIGNED_RESPONSE=$(curl -s --request GET "$BASE_URL/uploads/presigned-url" \
  --header "Authorization: Bearer $TOKEN" \
  -G \
  --data-urlencode "fileKey=$FILE_URL" \
  --data-urlencode 'expirationMinutes=60')

echo "Response: $PRESIGNED_RESPONSE"
PRESIGNED_URL=$(echo $PRESIGNED_RESPONSE | jq -r '.data.presignedUrl')
echo -e "${GREEN}✓ Presigned URL generated${NC}"
echo "URL: $PRESIGNED_URL\n"

# Step 4: Delete file
echo -e "${BLUE}Step 4: Deleting file...${NC}"
DELETE_RESPONSE=$(curl -s --request DELETE "$BASE_URL/uploads/delete" \
  --header "Authorization: Bearer $TOKEN" \
  -G \
  --data-urlencode "fileKey=$FILE_URL")

echo "Response: $DELETE_RESPONSE"
echo -e "${GREEN}✓ File deleted${NC}\n"

echo -e "${BLUE}=== Workflow Complete ===${NC}"
```

### Run the script
```bash
chmod +x upload_workflow.sh
./upload_workflow.sh
```

---

## Batch Upload Script

### Save as `batch_upload.sh`
```bash
#!/bin/bash

# Configuration
BASE_URL="http://localhost:8080/api"
TOKEN="your_jwt_token_here"
UPLOAD_DIR="./files-to-upload"
FOLDER_PATH="batch-upload"

echo "Starting batch upload from: $UPLOAD_DIR"

# Upload all files in directory
for file in "$UPLOAD_DIR"/*; do
  if [ -f "$file" ]; then
    echo "Uploading: $(basename $file)"
    
    curl --request POST "$BASE_URL/uploads/file" \
      --header "Authorization: Bearer $TOKEN" \
      --form "file=@$file" \
      --form "folderPath=$FOLDER_PATH"
    
    echo ""
  fi
done

echo "Batch upload complete!"
```

### Run batch upload
```bash
mkdir files-to-upload
cp /path/to/your/files/* files-to-upload/

chmod +x batch_upload.sh
./batch_upload.sh
```

---

## Debugging Tips

### Verbose Output (See Headers)
```bash
curl -v --request POST 'http://localhost:8080/api/uploads/file' \
  --header "Authorization: Bearer $TOKEN" \
  --form 'file=@/path/to/file.pdf'
```

### Pretty Print JSON
```bash
curl -s 'http://localhost:8080/api/uploads/exists?fileKey=file.pdf' \
  --header "Authorization: Bearer $TOKEN" | jq '.'
```

### Save Response to File
```bash
curl 'http://localhost:8080/api/uploads/exists?fileKey=file.pdf' \
  --header "Authorization: Bearer $TOKEN" > response.json
```

### Check Response Headers
```bash
curl -i --request GET 'http://localhost:8080/api/uploads/exists?fileKey=file.pdf' \
  --header "Authorization: Bearer $TOKEN"
```

---

## Error Handling Examples

### Catch and Handle Errors
```bash
RESPONSE=$(curl -s 'http://localhost:8080/api/uploads/file' \
  --header "Authorization: Bearer $TOKEN" \
  --form 'file=@/path/to/file.pdf')

# Check if successful
if echo "$RESPONSE" | jq -e '.success == true' > /dev/null; then
  echo "Success!"
  FILE_URL=$(echo "$RESPONSE" | jq -r '.data.fileUrl')
  echo "File URL: $FILE_URL"
else
  echo "Error!"
  ERROR_MSG=$(echo "$RESPONSE" | jq -r '.message')
  echo "Error message: $ERROR_MSG"
fi
```

---

## Environment Variables Setup

### Create .env.local for testing
```bash
# .env.local
BASE_URL="http://localhost:8080/api"
TOKEN="your_jwt_token"
BUCKET_NAME="your-s3-bucket"
```

### Use in scripts
```bash
source .env.local

curl --request POST "$BASE_URL/uploads/file" \
  --header "Authorization: Bearer $TOKEN" \
  --form 'file=@/path/to/file.pdf'
```

---

**All API endpoints are now ready for testing with cURL! 🚀**

