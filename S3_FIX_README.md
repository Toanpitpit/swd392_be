# AWS S3 Integration - Fix Summary

## Problem
Your application is receiving a **403 Signature Mismatch Error** when uploading files to AWS S3. This means the AWS credentials are either:
- Invalid or incorrect
- Using placeholder values
- Missing from environment variables

## Solution Overview

We've provided you with several files and updates to fix this issue:

### Files Created/Updated:

1. **S3Config.java** (Updated)
   - Added validation and logging
   - Better error messages if credentials are missing
   - Helps identify configuration problems at startup

2. **UploadServiceImpl.java** (Updated)
   - Improved error handling with specific AWS error detection
   - Better logging for debugging
   - Distinguishes between 403 (auth), 404 (bucket not found), and other errors

3. **run-app.ps1** ⭐ (New - Recommended)
   - PowerShell script to set up and launch the application
   - Validates all credentials before starting
   - Loads .env file automatically

4. **run-app.bat** (New)
   - Batch script alternative for Windows CMD

5. **AWS_S3_SETUP_GUIDE.md** (New)
   - Step-by-step AWS setup instructions
   - How to get credentials
   - How to create IAM user and S3 bucket

6. **S3_SETUP_TROUBLESHOOTING.md** (New)
   - Complete troubleshooting guide
   - FAQ and common issues
   - Environment variable priority explanation

## Quick Start (Easiest Method)

### Option 1: Using PowerShell Script (Recommended) ⭐

```powershell
# 1. Open PowerShell as Administrator
# 2. Navigate to project directory:
cd "D:\Semester 6\FA_Subject_ASM\MoocProject\Backend\car_rental_management"

# 3. Set execution policy (one-time):
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# 4. Run the startup script:
.\run-app.ps1
```

### Option 2: Manual Environment Variables (In PowerShell)

```powershell
# Set each variable:
$env:AWS_S3_ACCESS_KEY="your-actual-access-key"
$env:AWS_S3_SECRET_KEY="your-actual-secret-key"
$env:AWS_S3_REGION="us-east-1"
$env:AWS_S3_BUCKET_NAME="your-s3-bucket-name"

# Then start the app:
cd "D:\Semester 6\FA_Subject_ASM\MoocProject\Backend\car_rental_management"
.\mvnw.cmd clean spring-boot:run
```

### Option 3: Using .env File

1. Create `.env` file in project root:
   ```properties
   AWS_S3_ACCESS_KEY=your-access-key
   AWS_S3_SECRET_KEY=your-secret-key
   AWS_S3_REGION=us-east-1
   AWS_S3_BUCKET_NAME=your-bucket-name
   ```

2. Run: `.\run-app.ps1`

---

## Getting AWS Credentials (If You Don't Have Them)

### Step 1: Create AWS Account
- Go to [AWS Console](https://console.aws.amazon.com/)
- Sign in or create account

### Step 2: Create IAM User
1. Go to **IAM** → **Users**
2. Click **Create user**
3. Enter username: `car-rental-app-user`
4. Click **Next**
5. Click **Attach policies directly**
6. Search and select: **AmazonS3FullAccess**
7. Click **Create user**

### Step 3: Generate Access Keys
1. Click on the user you just created
2. Go to **Security credentials** tab
3. Click **Create access key**
4. Choose **Application running outside AWS**
5. Click **Create access key**
6. **Copy and save:**
   - Access Key ID
   - Secret Access Key

### Step 4: Create S3 Bucket
1. Go to **S3** → **Buckets**
2. Click **Create bucket**
3. Enter name: `car-rental-bucket-{your-initials}`
4. Select region: `us-east-1` (or your preferred region)
5. Click **Create bucket**

### Step 5: Update Your Application
- Set `AWS_S3_ACCESS_KEY` = your Access Key ID
- Set `AWS_S3_SECRET_KEY` = your Secret Access Key
- Set `AWS_S3_BUCKET_NAME` = your bucket name
- Set `AWS_S3_REGION` = your bucket region

---

## Verification Checklist

- [ ] AWS credentials are real (not placeholder values like YOUR_AWS_ACCESS_KEY)
- [ ] S3 bucket exists and is accessible
- [ ] IAM user has S3 permissions (AmazonS3FullAccess)
- [ ] Environment variables are set BEFORE starting the app
- [ ] Region matches your S3 bucket location
- [ ] Application startup logs show "✓ AWS S3 credentials loaded successfully"

---

## What Changed in the Code

### S3Config.java
```java
// Now validates credentials on startup:
if (accessKey == null || accessKey.equals("YOUR_AWS_ACCESS_KEY")) {
    log.warn("⚠️ AWS_S3_ACCESS_KEY not configured...");
}
```

### UploadServiceImpl.java
```java
// Better error handling:
catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
    if (e.statusCode() == 403) {
        throw new IOException("S3 Access Denied (403): Check AWS credentials and permissions");
    } else if (e.statusCode() == 404) {
        throw new IOException("S3 Bucket not found: Verify bucket name exists");
    }
}
```

---

## Testing the Upload After Setup

### 1. Get JWT Token
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  ...
}
```

### 2. Upload a File
```
POST /api/uploads/file
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: multipart/form-data

file: [select a test file]
folderPath: test-uploads (optional)

Response:
{
  "success": true,
  "message": "File uploaded successfully",
  "data": {
    "fileUrl": "test-uploads/uuid-filename.ext",
    "fileName": "original-filename.ext",
    "fileSize": "12345"
  }
}
```

---

## Troubleshooting

### Error: "The request signature we calculated does not match..."
```
Solution:
1. Verify AWS_S3_ACCESS_KEY value (no typos, no extra spaces)
2. Verify AWS_S3_SECRET_KEY value (same as above)
3. Check credentials in AWS Console haven't been rotated
4. Try creating new access keys
```

### Error: "Bucket not found" or 404
```
Solution:
1. Verify AWS_S3_BUCKET_NAME spelling matches exactly
2. Check bucket exists in AWS S3 Console
3. Verify region in AWS_S3_REGION matches bucket location
```

### Error: "Access Denied" or 403 (but credentials seem correct)
```
Solution:
1. Verify IAM user has S3 permissions
2. Attach "AmazonS3FullAccess" policy to IAM user
3. Wait 2-5 minutes for IAM changes to propagate
4. Try creating new access keys
```

### Startup Error: "Could not resolve placeholder 'SPRING_APPLICATION_NAME'"
```
Solution:
Add to environment:
$env:SPRING_APPLICATION_NAME="car_rental_management"
```

---

## Environment Variable Priority

The application checks for credentials in this order:

1. **System Environment Variables** ← Set these first! (highest priority)
2. **Process Environment Variables** ← What run-app.ps1 does
3. **Application.properties defaults** ← fallback values (lowest priority)

**Important:** Always set environment variables BEFORE starting the application!

---

## Next Steps

1. **Set up your AWS credentials** (follow "Getting AWS Credentials" section)
2. **Run the application** using `.\run-app.ps1`
3. **Check the logs** for "✓ AWS S3 credentials loaded successfully"
4. **Test file upload** using the endpoints above
5. **Verify files appear** in your AWS S3 bucket

---

## Additional Help

- **AWS_S3_SETUP_GUIDE.md** - Detailed AWS setup instructions
- **S3_SETUP_TROUBLESHOOTING.md** - Comprehensive troubleshooting guide
- **run-app.ps1** - Smart startup script with validation

---

## Security Notes

⚠️ **Never commit credentials to Git!**

1. Add `.env` to `.gitignore`
2. Never hardcode credentials in code
3. Use environment variables for all sensitive data
4. Consider AWS IAM Roles for production
5. Rotate credentials periodically

---

## Support

If you're still experiencing issues:

1. **Check all environment variables are set:**
   ```powershell
   $env:AWS_S3_ACCESS_KEY
   $env:AWS_S3_SECRET_KEY
   $env:AWS_S3_BUCKET_NAME
   ```

2. **Verify in application logs** - look for:
   - "✓ AWS S3 credentials loaded successfully"
   - "✓ AWS Region: us-east-1"

3. **Test AWS CLI** (if installed):
   ```powershell
   aws s3 ls --profile default
   ```

4. **Read the detailed guides** provided in the project root

---

**Version:** 1.0  
**Last Updated:** March 15, 2026

