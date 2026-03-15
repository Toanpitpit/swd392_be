# AWS S3 Integration - Visual Guide

## Problem Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Your Application                         │
│  (Car Rental Management - File Upload Feature)             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ Try to upload file
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              S3Client (AWS SDK)                             │
│  - Creates request with credentials                         │
│  - Signs request using Access Key + Secret Key             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ PUT /bucket/file.pdf
                     │ Authorization: signed with (bad keys?)
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                   AWS S3 (Cloud)                            │
│  - Receives request                                         │
│  - Tries to verify signature using AWS records             │
│  - ❌ Signatures DON'T MATCH!                              │
│  - Returns: 403 Forbidden - Signature Mismatch             │
└─────────────────────────────────────────────────────────────┘
                     │
                     │ 403 Error Response
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              Your Application (ERROR!)                      │
│  "The request signature we calculated does not match       │
│   the signature you provided. Check your key."             │
└─────────────────────────────────────────────────────────────┘
```

---

## Solution Diagram

```
┌─────────────────────────────────────────────────────────────┐
│              AWS Console                                    │
│  1. Create IAM User                                         │
│  2. Generate Access Keys:                                   │
│     - Access Key ID      (AKIA...)                         │
│     - Secret Access Key  (wJalr...)                        │
│  3. Create S3 Bucket                                        │
│  4. Attach S3 Permissions                                   │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   │ Copy credentials
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│         Windows Environment Variables                       │
│  AWS_S3_ACCESS_KEY = "AKIA..."                             │
│  AWS_S3_SECRET_KEY = "wJalr..."                            │
│  AWS_S3_REGION = "us-east-1"                               │
│  AWS_S3_BUCKET_NAME = "my-bucket"                          │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   │ Set BEFORE starting app
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│          Spring Boot Application Starts                     │
│  S3Config.java:                                             │
│  - Reads environment variables                              │
│  - Creates S3Client with credentials                        │
│  - ✓ Logs: "AWS S3 credentials loaded successfully"        │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   │ User requests file upload
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│             UploadServiceImpl.java                           │
│  - Receives file                                            │
│  - Uses S3Client with VALID credentials                     │
│  - Signs request with correct keys                          │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   │ PUT /bucket/file.pdf
                   │ Authorization: signed with VALID keys ✓
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│                   AWS S3 (Cloud)                            │
│  - Receives request                                         │
│  - Verifies signature using AWS records                     │
│  - ✓ Signatures MATCH!                                     │
│  - Stores file in S3                                        │
│  - Returns: 200 OK - Upload successful                      │
└─────────────────────────────────────────────────────────────┘
                   │
                   │ Success Response
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│              Your Application (SUCCESS!)                    │
│  {                                                          │
│    "success": true,                                         │
│    "message": "File uploaded successfully",                 │
│    "data": {                                                │
│      "fileUrl": "bucket/file.pdf",                          │
│      "fileName": "file.pdf"                                 │
│    }                                                        │
│  }                                                          │
└─────────────────────────────────────────────────────────────┘
```

---

## Setup Flow Chart

```
                    START
                     │
                     ▼
         ┌─────────────────────────┐
         │  Have AWS Account?      │
         └────────┬────────────────┘
                  │
         NO ◄─────┴─────► YES
         │                 │
         ▼                 ▼
    Create AWS        AWS Console
    Account           ▼ IAM → Users
         │            ▼ Create User
         │            ▼ Security credentials
         │            ▼ Create access key
         └─────►│                          ▼
                └─ Save Access Key ID
                   & Secret Access Key
                     │
                     ▼
              ┌──────────────────────┐
              │ Create S3 Bucket     │
              │ AWS Console → S3     │
              │ Remember:            │
              │ - Bucket name        │
              │ - Region (us-east-1?)
              └──────────┬───────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │ Set Environment      │
              │ Variables:           │
              │ AWS_S3_ACCESS_KEY   │
              │ AWS_S3_SECRET_KEY   │
              │ AWS_S3_REGION       │
              │ AWS_S3_BUCKET_NAME  │
              └──────────┬───────────┘
                         │
                    BEFORE STARTING APP!
                         │
                         ▼
              ┌──────────────────────┐
              │ Start Application    │
              │ .\run-app.ps1        │
              └──────────┬───────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │ Check Logs           │
              │ ✓ Credentials loaded?│
              └────────┬─────────────┘
                       │
              YES ◄────┴────► NO
              │               │
              ▼               ▼
           SUCCESS        Check .md files
              │               │
              │               ▼
              │         S3_SETUP_
              │         TROUBLESHOOTING
              │         .md
              │
              ▼
        ┌───────────────┐
        │ Test Upload   │
        │ POST /uploads │
        └───────┬───────┘
                │
                ▼
        ┌──────────────┐
        │   SUCCESS!   │
        │   File in S3 │
        └──────────────┘
```

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                     Spring Boot Application                      │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                  UploadController.java                   │   │
│  │  POST /uploads/file                                      │   │
│  │  POST /uploads/video                                     │   │
│  │  POST /uploads/files (multiple)                          │   │
│  │  DELETE /uploads/delete                                  │   │
│  └────────────────────┬─────────────────────────────────────┘   │
│                       │                                          │
│                       ▼                                          │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                 UploadService (Interface)                │   │
│  │  - uploadFile()                                          │   │
│  │  - uploadMultipleFiles()                                 │   │
│  │  - uploadVideo()                                         │   │
│  │  - deleteFile()                                          │   │
│  │  - generatePresignedUrl()                                │   │
│  └────────────────────┬─────────────────────────────────────┘   │
│                       │                                          │
│                       ▼                                          │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │            UploadServiceImpl (Implementation)             │   │
│  │  - Validates files/videos                                │   │
│  │  - Generates unique filenames                            │   │
│  │  - Calls S3Client for uploads                            │   │
│  │  - ✓ Better error handling (NEW!)                        │   │
│  └────────────────────┬─────────────────────────────────────┘   │
│                       │                                          │
│                       ▼                                          │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                   S3Config.java (NEW!)                   │   │
│  │  - Reads AWS credentials from environment               │   │
│  │  - Creates S3Client Bean                                 │   │
│  │  - Validates credentials at startup                      │   │
│  │  - ✓ Better logging and validation                       │   │
│  └────────────────────┬─────────────────────────────────────┘   │
│                       │                                          │
└───────────────────────┼──────────────────────────────────────────┘
                        │
                        ▼
        ┌───────────────────────────────┐
        │    AWS SDK for Java           │
        │  S3Client                      │
        │  - PutObjectRequest            │
        │  - DeleteObjectRequest         │
        │  - GetPresignedUrl             │
        └───────────┬───────────────────┘
                    │
                    ▼
        ┌───────────────────────────────┐
        │  INTERNET / HTTPS              │
        │  (Encrypted Connection)        │
        └───────────┬───────────────────┘
                    │
                    ▼
        ┌───────────────────────────────┐
        │  AWS S3 Bucket (Cloud)         │
        │  Region: us-east-1             │
        │  Stores: Uploaded files        │
        └───────────────────────────────┘
```

---

## Environment Variable Flow

```
┌─────────────────────────────────────────────────────────────────┐
│             AWS Console (Human Interface)                       │
│  1. Create IAM User                                             │
│  2. Generate Access Keys                                        │
│  3. Create S3 Bucket                                            │
└────────────────┬────────────────────────────────────────────────┘
                 │
                 │ Copy credentials
                 │
                 ▼
    ┌────────────────────────────┐
    │  Windows Settings          │
    │  Environment Variables     │
    │  (Persistent)              │
    │                            │
    │  AWS_S3_ACCESS_KEY=AKIA... │
    │  AWS_S3_SECRET_KEY=wJalr..│
    │  AWS_S3_REGION=us-east-1  │
    │  AWS_S3_BUCKET_NAME=bucket │
    └────────────┬───────────────┘
                 │
                 │ Read at app startup
                 │
                 ▼
    ┌────────────────────────────┐
    │  application.properties    │
    │  ${AWS_S3_ACCESS_KEY}      │
    │  ${AWS_S3_SECRET_KEY}      │
    │  ${AWS_S3_REGION}          │
    │  ${AWS_S3_BUCKET_NAME}     │
    └────────────┬───────────────┘
                 │
                 │ Spring resolves variables
                 │
                 ▼
    ┌────────────────────────────┐
    │  S3Config.java             │
    │  @Value injected           │
    │  - accessKey               │
    │  - secretKey               │
    │  - region                  │
    │  - bucketName              │
    └────────────┬───────────────┘
                 │
                 │ Creates beans
                 │
                 ▼
    ┌────────────────────────────┐
    │  S3Client Bean             │
    │  S3Presigner Bean          │
    │  Ready for use!            │
    └────────────────────────────┘
```

---

## Error Resolution Tree

```
                        ❌ Error Occurs
                              │
                              ▼
                    ┌─────────────────────┐
                    │ What's the error?   │
                    └─────────┬───────────┘
                              │
                ┌─────────────┼─────────────┐
                │             │             │
        403 ◄───┘        404 ◄─┴──►     500/Other
    Unauthorized      Not Found
        │                 │             │
        ▼                 ▼             ▼
   ┌────────────┐    ┌──────────┐   ┌──────────┐
   │ Check:     │    │ Check:   │   │ Check:   │
   │ - Access   │    │ - Bucket │   │ - Logs   │
   │   Key      │    │   name   │   │ - Format │
   │ - Secret   │    │ - Region │   │ - File   │
   │   Key      │    │ - Exists │   │   size   │
   │ - IAM      │    │ - Perms  │   │          │
   │   Policy   │    │          │   │          │
   └─────┬──────┘    └────┬─────┘   └────┬─────┘
         │                │              │
         ▼                ▼              ▼
    Try new keys    Fix bucket      Check limits
         │           or region           │
         │                │              │
         └─────────┬──────┴──────────────┘
                   │
                   ▼
        ┌─────────────────────┐
        │ Retry Application   │
        └──────────┬──────────┘
                   │
           ┌───────┴────────┐
           │                │
       Success          Still Failing
           │                │
           ▼                ▼
         ✓ DONE   Read troubleshooting
                  guide for help
```

---

## Quick Reference - Credential Sources

```
Priority Order (Highest to Lowest):

┌──────────────────────────────────────────────────────────┐
│  1. SYSTEM ENVIRONMENT VARIABLES (Windows Settings)      │
│     - Persistent across sessions                         │
│     - Set in: Settings → System → Env Vars              │
│     - Survives IDE restart                               │
│     ⭐ USE THIS FOR DEVELOPMENT                          │
└──────────────────────────────────────────────────────────┘
                          ▲
                          │
                          │
┌──────────────────────────────────────────────────────────┐
│  2. SESSION VARIABLES ($env: in PowerShell)              │
│     - Lost when PowerShell closes                        │
│     - Set via: $env:KEY=value                           │
│     - Temporary workaround                               │
└──────────────────────────────────────────────────────────┘
                          ▲
                          │
                          │
┌──────────────────────────────────────────────────────────┐
│  3. .env FILE (Parsed by run-app.ps1)                   │
│     - Convenient for development                         │
│     - Must NOT be committed to Git                       │
│     - Good for team onboarding                           │
└──────────────────────────────────────────────────────────┘
                          ▲
                          │
                          │
┌──────────────────────────────────────────────────────────┐
│  4. application.properties DEFAULTS (FALLBACK)           │
│     - Placeholder values (YOUR_AWS_ACCESS_KEY)          │
│     - Should NEVER be used with real credentials        │
│     - Just for error messages                            │
└──────────────────────────────────────────────────────────┘
```

---

**Tip:** Print this guide and keep it handy! It helps visualize the entire process.

