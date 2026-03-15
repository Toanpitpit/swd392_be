# AWS S3 403 Error - Fix Summary & Action Items

## 🔴 Problem Statement
Application throwing **403 Signature Mismatch Error** when attempting to upload files to AWS S3:
```
software.amazon.awssdk.services.s3.model.S3Exception: The request signature we calculated 
does not match the signature you provided. Check your key and signing method. 
(Service: S3, Status Code: 403, Request ID: H4G41G2XG1YZY19X)
```

---

## 🟢 Root Cause
The AWS S3 credentials (Access Key / Secret Key) are either:
1. Invalid or incorrectly copied
2. Not set in environment variables
3. Using placeholder values like `YOUR_AWS_ACCESS_KEY`
4. Expired or revoked in AWS console

---

## ✅ What Was Fixed

### 1. Code Improvements

#### S3Config.java (Enhanced)
- Added `@Slf4j` for logging
- Added default values to `@Value` annotations with `:` syntax
- Added `validateCredentials()` method to check credentials at startup
- Better logging to identify configuration issues
- Clear warning messages if credentials are missing

**Key improvements:**
```java
@Value("${aws.s3.access-key:YOUR_AWS_ACCESS_KEY}")  // Now has default
private String accessKey;

// Validates at startup:
if (accessKey == null || accessKey.equals("YOUR_AWS_ACCESS_KEY")) {
    log.warn("⚠️ AWS_S3_ACCESS_KEY not configured...");
}
```

#### UploadServiceImpl.java (Enhanced)
- Added specific S3Exception catch block
- Distinguishes between 403 (auth), 404 (bucket not found), and other errors
- Better error messages for debugging
- Logs detailed AWS error information

**Key improvements:**
```java
catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
    if (e.statusCode() == 403) {
        throw new IOException("S3 Access Denied (403): Check AWS credentials");
    } else if (e.statusCode() == 404) {
        throw new IOException("S3 Bucket not found (404): Verify bucket name");
    }
}
```

### 2. Helper Scripts & Documentation

Created 7 new comprehensive documents:

#### Scripts
1. **run-app.ps1** ⭐ (PowerShell - Recommended)
   - Validates all credentials before starting
   - Loads `.env` file automatically
   - Provides detailed error messages
   - Cross-platform compatible

2. **run-app.bat** (Windows Batch)
   - Alternative batch script
   - Parses `.env` file
   - Validates credentials

#### Documentation
3. **S3_FIX_README.md** (Main Guide)
   - Overview of problem and solution
   - Quick start instructions
   - AWS credential setup
   - Testing and verification
   - Security notes

4. **S3_SETUP_TROUBLESHOOTING.md** (Troubleshooting)
   - Detailed error explanations
   - Root cause analysis
   - Step-by-step fixes
   - FAQ and common issues
   - Environment variable priority
   - Production deployment tips

5. **AWS_S3_SETUP_GUIDE.md** (AWS Setup)
   - How to get AWS credentials
   - How to create S3 bucket
   - IAM policy configuration
   - File upload examples

6. **QUICK_REFERENCE.md** (Quick Card)
   - TL;DR version
   - Common errors and fixes
   - Quick reference table
   - Key points to remember

7. **COMPLETE_SETUP_CHECKLIST.md** (Step-by-Step)
   - 7-phase checklist
   - Checkboxes for each step
   - Multiple setup methods
   - Verification steps
   - Debug commands

---

## 🚀 How to Fix (Quick Steps)

### Option 1: Recommended - Use PowerShell Script
```powershell
# 1. Navigate to project
cd "D:\Semester 6\FA_Subject_ASM\MoocProject\Backend\car_rental_management"

# 2. Allow PowerShell scripts (one time)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# 3. Run the smart startup script
.\run-app.ps1

# Script will:
# - Load .env file if it exists
# - Validate all credentials
# - Show clear error messages if something is missing
# - Start the application if all is OK
```

### Option 2: Manual Environment Variables
```powershell
# Get AWS credentials from AWS Console first!

# Set variables (must be done before starting app)
$env:AWS_S3_ACCESS_KEY="AKIA..."
$env:AWS_S3_SECRET_KEY="wJalr..."
$env:AWS_S3_REGION="us-east-1"
$env:AWS_S3_BUCKET_NAME="your-bucket-name"

# Verify they're set
$env:AWS_S3_ACCESS_KEY

# Then start app
cd "D:\Semester 6\FA_Subject_ASM\MoocProject\Backend\car_rental_management"
.\mvnw.cmd clean spring-boot:run
```

### Option 3: Use .env File
```
1. Create file: .env in project root
2. Contents:
   AWS_S3_ACCESS_KEY=your-key
   AWS_S3_SECRET_KEY=your-secret
   AWS_S3_REGION=us-east-1
   AWS_S3_BUCKET_NAME=your-bucket

3. Run: .\run-app.ps1
```

---

## 📋 To Get AWS Credentials

1. **AWS Console → IAM → Users**
2. **Security credentials tab**
3. **Create access key**
4. **Copy Access Key ID and Secret Access Key**

Note: You'll see these only once! Save them immediately.

---

## ✓ Verification

After starting the application, check logs for:
```
✓ AWS S3 credentials loaded successfully
✓ AWS Region: us-east-1
```

If you see warnings about placeholders or missing credentials, environment variables aren't set properly.

---

## 📁 Files Changed/Created

### Modified Files
```
src/main/java/fa/training/car_rental_management/config/S3Config.java
src/main/java/fa/training/car_rental_management/services/impl/UploadServiceImpl.java
```

### New Helper Files
```
run-app.ps1                         # Smart PowerShell startup script
run-app.bat                         # Batch startup script
S3_FIX_README.md                   # Main setup guide
S3_SETUP_TROUBLESHOOTING.md        # Troubleshooting guide
AWS_S3_SETUP_GUIDE.md              # AWS setup instructions
QUICK_REFERENCE.md                 # Quick reference card
COMPLETE_SETUP_CHECKLIST.md        # Step-by-step checklist
```

---

## 🔍 Key Concepts to Remember

### Environment Variable Priority
1. **System Environment Variables** (Windows Settings) ← Set here first!
2. **Process Environment Variables** (PowerShell $env:)
3. **Application.properties** (fallback defaults)

### Important Notes
- ⚠️ Environment variables MUST be set BEFORE app starts
- ⚠️ Never hardcode credentials in source code
- ⚠️ Never commit .env file to Git
- ⚠️ Credentials are like passwords - keep them safe
- ⚠️ If credentials leak, rotate immediately in AWS console

---

## 🧪 Testing After Setup

```
1. Get JWT token via POST /api/auth/login
2. Upload file via POST /api/uploads/file
3. Check file appears in AWS S3 console
```

---

## ❓ Common Issues & Quick Fixes

| Issue | Fix |
|-------|-----|
| 403 Signature Mismatch | Verify access key and secret key are correct |
| Bucket not found (404) | Check bucket name spelling and region |
| Access Denied | Add AmazonS3FullAccess policy to IAM user |
| Credentials not loading | Set env vars BEFORE starting app |
| Can't run .ps1 scripts | Run: `Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser` |

---

## 📚 Documentation Map

```
START HERE → S3_FIX_README.md
             ↓
     Quick Start or
     COMPLETE_SETUP_CHECKLIST.md
             ↓
     Having Issues?
     → S3_SETUP_TROUBLESHOOTING.md
     → QUICK_REFERENCE.md
     
     Need AWS Help?
     → AWS_S3_SETUP_GUIDE.md
```

---

## ✨ Next Actions

1. **Read:** `S3_FIX_README.md` (5 min read)
2. **Get:** AWS credentials from AWS Console (10 min)
3. **Create:** S3 bucket in AWS (5 min)
4. **Run:** `.\run-app.ps1` script (1 min)
5. **Verify:** Check logs for success (1 min)
6. **Test:** Upload a file (2 min)

**Total time: ~25 minutes**

---

## 💡 Pro Tips

✓ Use `run-app.ps1` - it validates everything for you  
✓ Keep AWS credentials in `.env` file (add to .gitignore)  
✓ Check application logs - they tell you what's wrong  
✓ IAM policy changes take 2-5 minutes to propagate  
✓ If stuck, read COMPLETE_SETUP_CHECKLIST.md step-by-step  

---

## 📞 Support Resources

- **AWS IAM Docs:** https://docs.aws.amazon.com/iam/
- **AWS S3 Docs:** https://docs.aws.amazon.com/s3/
- **Spring Boot Docs:** https://spring.io/projects/spring-boot
- **AWS SDK Java:** https://aws.amazon.com/sdk-for-java/

---

**Status:** ✅ Ready to use  
**Last Updated:** March 15, 2026  
**All files validated:** ✓ No compilation errors

