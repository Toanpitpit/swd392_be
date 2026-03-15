# AWS S3 Configuration Checklist

Complete this checklist to resolve the "403 Signature Mismatch" error.

---

## Phase 1: AWS Account Setup

- [ ] **Have AWS Account**
  - [ ] Go to https://aws.amazon.com
  - [ ] Sign up or sign in
  - [ ] Verify payment method is added

- [ ] **Create IAM User**
  - [ ] Go to AWS Console → IAM → Users
  - [ ] Click "Create user"
  - [ ] Name: `car-rental-app-user`
  - [ ] Go to "Security credentials" tab
  - [ ] Note: You can create access keys without a password

- [ ] **Create Access Keys**
  - [ ] Click "Create access key"
  - [ ] Choose "Application running outside AWS"
  - [ ] Copy Access Key ID (looks like: AKIA...)
  - [ ] Copy Secret Access Key (looks like: wJalr...)
  - [ ] **⚠️ Save these immediately - you won't see them again!**

- [ ] **Add S3 Permissions to IAM User**
  - [ ] In IAM, select your user
  - [ ] Click "Add permissions" → "Attach policies directly"
  - [ ] Search for "S3"
  - [ ] Select "AmazonS3FullAccess"
  - [ ] Click "Attach policy"

---

## Phase 2: S3 Bucket Setup

- [ ] **Create S3 Bucket**
  - [ ] Go to AWS Console → S3 → Buckets
  - [ ] Click "Create bucket"
  - [ ] Name: `car-rental-bucket` (or similar - must be globally unique)
  - [ ] Region: `us-east-1` (or your preferred region - **remember this!**)
  - [ ] Leave other settings as default
  - [ ] Click "Create bucket"

- [ ] **Verify Bucket Exists**
  - [ ] Go to AWS S3 console
  - [ ] Confirm your bucket name appears in the list
  - [ ] Confirm the region matches what you set

---

## Phase 3: Environment Variables Setup

Choose ONE of these methods:

### Method A: PowerShell Permanent Variables (Windows) ⭐ EASIEST
- [ ] **Set via System Environment Variables**
  - [ ] Open: Settings → System → About → "Advanced system settings"
  - [ ] Click "Environment Variables..."
  - [ ] Under "System variables" click "New"
  - [ ] Create variable 1:
    - [ ] Variable name: `AWS_S3_ACCESS_KEY`
    - [ ] Variable value: `AKIA...` (your access key)
  - [ ] Create variable 2:
    - [ ] Variable name: `AWS_S3_SECRET_KEY`
    - [ ] Variable value: `wJalr...` (your secret key)
  - [ ] Create variable 3:
    - [ ] Variable name: `AWS_S3_REGION`
    - [ ] Variable value: `us-east-1`
  - [ ] Create variable 4:
    - [ ] Variable name: `AWS_S3_BUCKET_NAME`
    - [ ] Variable value: `your-bucket-name`
  - [ ] Click "OK" and close all windows
  - [ ] **Restart your IDE and application**

### Method B: Session Variables (PowerShell) - Per Session
- [ ] **Set via PowerShell (must be done every session)**
  ```powershell
  $env:AWS_S3_ACCESS_KEY="AKIA..."
  $env:AWS_S3_SECRET_KEY="wJalr..."
  $env:AWS_S3_REGION="us-east-1"
  $env:AWS_S3_BUCKET_NAME="your-bucket-name"
  ```
- [ ] Verify variables are set:
  ```powershell
  $env:AWS_S3_ACCESS_KEY
  # Should print: AKIA...
  ```

### Method C: Using .env File
- [ ] **Create .env file in project root**
  - [ ] File path: `D:\Semester 6\FA_Subject_ASM\MoocProject\Backend\car_rental_management\.env`
  - [ ] Contents:
    ```properties
    AWS_S3_ACCESS_KEY=AKIA...
    AWS_S3_SECRET_KEY=wJalr...
    AWS_S3_REGION=us-east-1
    AWS_S3_BUCKET_NAME=your-bucket-name
    ```
  - [ ] Save file
  - [ ] Run: `.\run-app.ps1`

---

## Phase 4: Application Launch

- [ ] **Using Smart Startup Script (Recommended)**
  ```powershell
  cd "D:\Semester 6\FA_Subject_ASM\MoocProject\Backend\car_rental_management"
  Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
  .\run-app.ps1
  ```

- [ ] **OR Manual Launch**
  ```powershell
  cd "D:\Semester 6\FA_Subject_ASM\MoocProject\Backend\car_rental_management"
  .\mvnw.cmd clean spring-boot:run
  ```

---

## Phase 5: Verification

- [ ] **Check Application Startup Logs**
  - [ ] Look for these SUCCESS messages:
    ```
    ✓ AWS S3 credentials loaded successfully
    ✓ AWS Region: us-east-1
    ```
  - [ ] ❌ If you see WARNING messages about credentials not configured:
    - [ ] Environment variables not properly set
    - [ ] Try Method A (System Environment Variables)
    - [ ] Restart IDE after setting

- [ ] **Verify Credentials Are Not Placeholders**
  - [ ] In application logs, should NOT see:
    ```
    AWS_S3_ACCESS_KEY: YOUR_AWS_ACCESS_KEY
    AWS_S3_SECRET_KEY: YOUR_AWS_SECRET_KEY
    ```

- [ ] **Check Application Started Successfully**
  - [ ] Look for:
    ```
    Started CarRentalManagementApplication
    ```
  - [ ] No exceptions or errors related to S3

---

## Phase 6: Test File Upload

- [ ] **Get JWT Token**
  ```
  POST http://localhost:8080/api/auth/login
  Content-Type: application/json
  
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```
  - [ ] Copy the `token` from response

- [ ] **Upload Test File**
  ```
  POST http://localhost:8080/api/uploads/file
  Authorization: Bearer {token-from-above}
  Content-Type: multipart/form-data
  
  Body:
    file: (select any small file)
    folderPath: test (optional)
  ```
  - [ ] Expected response:
    ```json
    {
      "success": true,
      "message": "File uploaded successfully",
      "data": {
        "fileUrl": "test/uuid-filename.ext",
        "fileName": "original-filename.ext",
        "fileSize": "12345"
      }
    }
    ```

- [ ] **Verify File in S3**
  - [ ] Go to AWS S3 Console
  - [ ] Open your bucket
  - [ ] Confirm file appears in the list

---

## Phase 7: Troubleshooting (If Issues Occur)

- [ ] **Error: "403 Signature Mismatch"**
  - [ ] Check Access Key is correct (copy from AWS console again)
  - [ ] Check Secret Key is correct
  - [ ] Verify no typos or extra spaces
  - [ ] Try creating new access keys

- [ ] **Error: "Bucket not found" (404)**
  - [ ] Check bucket name spelling exactly matches
  - [ ] Verify bucket exists in S3 console
  - [ ] Check region matches bucket location

- [ ] **Error: "Access Denied" (403 with different message)**
  - [ ] Verify IAM user has S3 permissions
  - [ ] Check AmazonS3FullAccess policy is attached
  - [ ] Wait 2-5 minutes for IAM changes to propagate
  - [ ] Create new access keys

- [ ] **Error: "Could not resolve placeholder 'SPRING_APPLICATION_NAME'"**
  - [ ] Add environment variable:
    ```powershell
    $env:SPRING_APPLICATION_NAME="car_rental_management"
    ```

- [ ] **Credentials Not Loading**
  - [ ] Environment variables must be set BEFORE starting app
  - [ ] If you set them in PowerShell, restart the app
  - [ ] If using system variables, restart IDE

---

## Success Criteria ✓

- [ ] Application starts without errors
- [ ] Logs show "✓ AWS S3 credentials loaded successfully"
- [ ] File upload returns success response
- [ ] Files appear in S3 bucket console

---

## Quick Debug

**In PowerShell, verify credentials are accessible:**
```powershell
$env:AWS_S3_ACCESS_KEY           # Should show your access key
$env:AWS_S3_SECRET_KEY            # Should show your secret key
$env:AWS_S3_BUCKET_NAME           # Should show your bucket name
$env:AWS_S3_REGION                # Should show: us-east-1
```

**If any are empty or show placeholder values, go back to Phase 3!**

---

## Reference Files

- `S3_FIX_README.md` - Main setup guide
- `S3_SETUP_TROUBLESHOOTING.md` - Detailed troubleshooting
- `QUICK_REFERENCE.md` - Quick reference card
- `run-app.ps1` - Smart startup script

---

## Important Notes

⚠️ **Keep your AWS credentials safe!**
- Never commit `.env` file to Git
- Never share access keys
- Add `.env` to `.gitignore`

⚠️ **Environment variables must be set BEFORE app starts**
- Setting them after the app started won't work
- You need to restart the application

⚠️ **Access keys are like passwords**
- Treat them as sensitive information
- If leaked, rotate immediately in AWS console

---

**Last Updated:** March 15, 2026  
**Status:** Ready to use ✓

