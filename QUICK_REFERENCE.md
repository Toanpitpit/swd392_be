# AWS S3 Configuration - Quick Reference Card

## The Problem
```
S3Exception: The request signature we calculated does not match the signature you provided.
Status Code: 403
```
**Cause:** Invalid AWS credentials or not configured at startup

---

## The Solution - 3 Steps

### Step 1: Get AWS Credentials ✓
```
AWS Console → IAM → Users → Security Credentials → Create access key
```
You'll get:
- **Access Key ID** (looks like: AKIAIOSFODNN7EXAMPLE)
- **Secret Access Key** (looks like: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY)

### Step 2: Create S3 Bucket ✓
```
AWS Console → S3 → Create bucket
Name: car-rental-bucket (must be unique)
Region: us-east-1 (or your choice)
```

### Step 3: Set Environment Variables ✓
**PowerShell (Recommended):**
```powershell
$env:AWS_S3_ACCESS_KEY="AKIAIOSFODNN7EXAMPLE"
$env:AWS_S3_SECRET_KEY="wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
$env:AWS_S3_REGION="us-east-1"
$env:AWS_S3_BUCKET_NAME="car-rental-bucket"
```

**Or use run-app.ps1:**
```powershell
cd "D:\Semester 6\FA_Subject_ASM\MoocProject\Backend\car_rental_management"
.\run-app.ps1
```

---

## Verify Setup
Look for this in application startup logs:
```
✓ AWS S3 credentials loaded successfully
✓ AWS Region: us-east-1
```

---

## Test Upload
```
POST /api/uploads/file
Authorization: Bearer {jwt-token}
Body: file (multipart/form-data)
```

---

## Common Errors & Fixes

| Error | Fix |
|-------|-----|
| 403 Signature Mismatch | Check access key and secret key are correct |
| Bucket not found (404) | Verify bucket name spelling and region |
| Access Denied (403) | Add S3 permissions to IAM user |
| Credentials not loading | Set environment variables BEFORE starting app |

---

## Files Provided

| File | Purpose |
|------|---------|
| `S3_FIX_README.md` | Main setup guide (start here) |
| `S3_SETUP_TROUBLESHOOTING.md` | Detailed troubleshooting |
| `AWS_S3_SETUP_GUIDE.md` | AWS setup instructions |
| `run-app.ps1` | Smart startup script ⭐ |
| `run-app.bat` | Windows batch alternative |
| `S3Config.java` | Updated with better logging |
| `UploadServiceImpl.java` | Updated with better error handling |

---

## Recommended Setup Flow

1. Read: **S3_FIX_README.md**
2. Get AWS credentials
3. Create S3 bucket
4. Run: **run-app.ps1**
5. Check logs for success message
6. Test file upload

---

## Still Having Issues?

1. **Check credentials are real** (not placeholder values)
2. **Verify environment variables are set** before starting app
3. **Check IAM user has S3 permissions**
4. **Wait 2-5 minutes** for IAM policy changes to propagate
5. **Try creating new access keys**
6. **Read S3_SETUP_TROUBLESHOOTING.md**

---

## Key Points to Remember

✓ Set environment variables BEFORE starting application  
✓ Use real AWS credentials (not placeholder values)  
✓ Ensure bucket exists in same region as configured  
✓ Verify IAM user has S3 permissions  
✓ Check application logs for configuration status  

---

**TL;DR:**
1. Get AWS credentials and S3 bucket
2. Set 4 environment variables
3. Run app
4. Check logs for success
5. Test upload

Done! 🎉

