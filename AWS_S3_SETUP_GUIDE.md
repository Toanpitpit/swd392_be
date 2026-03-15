# AWS S3 Configuration Guide

## Error Fix: 403 Signature Mismatch

The error **"The request signature we calculated does not match the signature you provided"** indicates that AWS credentials are invalid or not properly configured.

## Solution Steps

### 1. Configure Environment Variables

Set these environment variables on your system:

```bash
# Windows PowerShell
$env:AWS_S3_ACCESS_KEY="your-actual-access-key"
$env:AWS_S3_SECRET_KEY="your-actual-secret-key"
$env:AWS_S3_REGION="us-east-1"
$env:AWS_S3_BUCKET_NAME="your-actual-bucket-name"

# Or set them permanently in System Environment Variables
```

### 2. Create `.env` File (Optional - For Local Development)

Create a file named `.env` in the project root:

```properties
AWS_S3_ACCESS_KEY=your-actual-access-key
AWS_S3_SECRET_KEY=your-actual-secret-key
AWS_S3_REGION=us-east-1
AWS_S3_BUCKET_NAME=your-actual-bucket-name
DB_URL=jdbc:mysql://localhost:3306/carrentaldb
DB_USERNAME=root
DB_PASSWORD=12345
JWT_SECRET=your-secret-key-min-32-chars-long-for-hs256-algorithm
SPRING_APPLICATION_NAME=car_rental_management
```

### 3. Get AWS Credentials

1. Go to AWS Console → IAM → Users
2. Create a new user or use existing one
3. Go to "Security credentials" tab
4. Click "Create access key"
5. Choose "Application running outside AWS"
6. Copy the Access Key and Secret Access Key

### 4. Create S3 Bucket

1. Go to AWS Console → S3
2. Create a new bucket with name like `car-rental-management-{your-name}`
3. Block public access settings (keep default or adjust as needed)
4. Make note of the bucket name

### 5. Set Bucket Permissions (IAM Policy)

Attach this policy to your IAM user:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:GetObject",
                "s3:PutObject",
                "s3:DeleteObject",
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::your-bucket-name",
                "arn:aws:s3:::your-bucket-name/*"
            ]
        }
    ]
}
```

### 6. Application Properties Configuration

Your `application.properties` is correctly set up with environment variable fallbacks:

```properties
aws.s3.access-key=${AWS_S3_ACCESS_KEY:YOUR_AWS_ACCESS_KEY}
aws.s3.secret-key=${AWS_S3_SECRET_KEY:YOUR_AWS_SECRET_KEY}
aws.s3.region=${AWS_S3_REGION:us-east-1}
aws.s3.bucket-name=${AWS_S3_BUCKET_NAME:your-bucket-name}
```

Just ensure your environment variables are set before starting the application.

### 7. Testing with Postman

**Upload File Endpoint:**
```
POST /api/uploads/file
Headers:
  - Authorization: Bearer {your-jwt-token}
  - Content-Type: multipart/form-data

Body (form-data):
  - file: (select your file)
  - folderPath: files (optional, default: files)
```

**Upload Video Endpoint:**
```
POST /api/uploads/video
Headers:
  - Authorization: Bearer {your-jwt-token}
  - Content-Type: multipart/form-data

Body (form-data):
  - video: (select your video file)
  - folderPath: videos (optional, default: videos)
```

### 8. Troubleshooting

| Error | Cause | Solution |
|-------|-------|----------|
| 403 Signature Mismatch | Invalid credentials | Verify Access Key and Secret Key |
| 403 Access Denied | Insufficient permissions | Check IAM policy attached to user |
| Bucket not found | Wrong bucket name | Verify bucket name in properties |
| Region error | Wrong region | Check S3 bucket region matches config |
| File too large | Exceeds max size | Increase `AWS_S3_MAX_FILE_SIZE` |

### 9. Environment Variable Priority

1. System Environment Variables (highest)
2. Environment variables set at runtime
3. Application.properties default values (lowest)

Make sure to restart the application after setting environment variables!

## Quick Test

Run this in PowerShell to verify setup:

```powershell
$env:AWS_S3_ACCESS_KEY="test-key"
$env:AWS_S3_SECRET_KEY="test-secret"
$env:AWS_S3_BUCKET_NAME="test-bucket"

# Then start your Spring Boot application
mvnw.cmd spring-boot:run
```

Check the startup logs to confirm properties are loaded correctly.

