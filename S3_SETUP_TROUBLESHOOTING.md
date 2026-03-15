# AWS S3 Upload Error - Complete Resolution Guide

## Error Details
```
S3Exception: The request signature we calculated does not match the signature you provided. 
Check your key and signing method. (Status Code: 403)
```

## Root Cause Analysis

This error occurs because:
1. **Invalid AWS Credentials** - Access Key or Secret Key is incorrect
2. **Expired Credentials** - AWS credentials have been revoked or expired
3. **Missing Environment Variables** - Using placeholder values like `YOUR_AWS_ACCESS_KEY`
4. **Wrong AWS Region** - Region mismatch between client and S3 bucket
5. **Insufficient IAM Permissions** - User lacks S3 PutObject permission

---

## Quick Fix Steps

### Step 1: Get Your AWS Credentials

1. Go to [AWS IAM Console](https://console.aws.amazon.com/iam/)
2. Click **Users** in the left sidebar
3. Click on your username (or create a new user)
4. Go to **Security credentials** tab
5. Click **Create access key**
6. Choose **Application running outside AWS**
7. **Save your credentials:**
   - Copy **Access Key ID**
   - Copy **Secret Access Key**

### Step 2: Create/Verify S3 Bucket

1. Go to [AWS S3 Console](https://console.aws.amazon.com/s3/)
2. Click **Create bucket** (or use existing one)
3. Note the **bucket name** and **region** (e.g., us-east-1)

### Step 3: Set Environment Variables

**For Windows PowerShell (Recommended):**

```powershell
# Open PowerShell as Administrator
# Set environment variables
$env:AWS_S3_ACCESS_KEY="AKIAIOSFODNN7EXAMPLE"
$env:AWS_S3_SECRET_KEY="wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
$env:AWS_S3_REGION="us-east-1"
$env:AWS_S3_BUCKET_NAME="my-car-rental-bucket"

# Verify they're set
$env:AWS_S3_ACCESS_KEY
$env:AWS_S3_SECRET_KEY
$env:AWS_S3_BUCKET_NAME

# Start the application
cd "D:\Semester 6\FA_Subject_ASM\MoocProject\Backend\car_rental_management"
.\mvnw.cmd clean spring-boot:run
```

**For Windows System Environment Variables (Persistent):**

1. Search for "Environment Variables" in Windows Search
2. Click "Edit the system environment variables"
3. Click "Environment Variables..." button
4. Under "System variables" click "New"
5. Add these variables:
   - `AWS_S3_ACCESS_KEY` = your access key
   - `AWS_S3_SECRET_KEY` = your secret key
   - `AWS_S3_REGION` = us-east-1
   - `AWS_S3_BUCKET_NAME` = your bucket name
6. Click "OK" and restart your IDE

### Step 4: Add IAM Policy to Your User

1. Go to [AWS IAM Console](https://console.aws.amazon.com/iam/)
2. Click **Users** → your username
3. Click **Add permissions** → **Attach policies directly**
4. Search for `S3`
5. Select **AmazonS3FullAccess** or attach this custom policy:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:*"
            ],
            "Resource": "*"
        }
    ]
}
```

### Step 5: Restart Your Application

Once environment variables are set, restart the application:

```bash
# Stop the current application
# Then restart it:
.\mvnw.cmd clean spring-boot:run
```

Check the logs for:
```
✓ AWS S3 credentials loaded successfully
✓ AWS Region: us-east-1
```

---

## Verification Checklist

- [ ] AWS Access Key ID is set and correct
- [ ] AWS Secret Access Key is set and correct
- [ ] S3 bucket exists in the specified region
- [ ] IAM user has S3 permissions
- [ ] Region matches S3 bucket location
- [ ] Environment variables are set before app starts
- [ ] Application logs show "credentials loaded successfully"
- [ ] No placeholder values like "YOUR_AWS_ACCESS_KEY" in use

---

## Testing the Upload

1. **Get JWT Token:**
   ```
   POST /api/auth/login
   {
     "email": "user@example.com",
     "password": "password"
   }
   ```
   Copy the `token` from response

2. **Upload a File:**
   ```
   POST /api/uploads/file
   Headers:
     Authorization: Bearer {your-jwt-token}
   
   Form Data:
     file: (select a small test file)
     folderPath: test-uploads
   ```

3. **Expected Success Response:**
   ```json
   {
     "success": true,
     "message": "File uploaded successfully",
     "data": {
       "fileUrl": "test-uploads/filename-uuid.ext",
       "fileName": "original-filename.ext",
       "fileSize": "12345"
     }
   }
   ```

---

## Troubleshooting

### Error: "Could not resolve placeholder 'SPRING_APPLICATION_NAME'"
```
Solution: Add to environment:
set SPRING_APPLICATION_NAME=car_rental_management
```

### Error: "NoSuchBucket: The specified bucket does not exist"
```
Solution:
1. Verify bucket name in AWS_S3_BUCKET_NAME
2. Check bucket exists in AWS S3 console
3. Verify region matches (AWS_S3_REGION)
```

### Error: "Access Denied"
```
Solution:
1. Check IAM policy is attached to user
2. Verify credentials are for correct AWS user
3. Wait 5 minutes for IAM policy to propagate
```

### Error: "Invalid AccessKeyId"
```
Solution:
1. Copy access key again from AWS console
2. Verify no extra spaces in AWS_S3_ACCESS_KEY
3. Check credentials haven't been rotated
```

---

## Using .env File (Alternative Method)

1. Create `.env` file in project root:
   ```properties
   AWS_S3_ACCESS_KEY=your-access-key
   AWS_S3_SECRET_KEY=your-secret-key
   AWS_S3_REGION=us-east-1
   AWS_S3_BUCKET_NAME=your-bucket-name
   DB_URL=jdbc:mysql://localhost:3306/carrentaldb
   DB_USERNAME=root
   DB_PASSWORD=12345
   JWT_SECRET=your-secret-key-32-chars-min
   ```

2. Use the run script:
   ```bash
   .\run-app.bat
   ```

---

## Environment Variable Priority

The application loads variables in this order (highest to lowest priority):

1. **System Environment Variables** ← Set these first!
2. **Runtime Environment Variables**
3. **Application.properties defaults** ← Fallback values

---

## Production Deployment

For production environments:

1. **Use AWS IAM Roles** instead of Access Keys (if on EC2)
2. **Rotate credentials regularly**
3. **Never commit credentials to Git**
4. **Use AWS Secrets Manager** for credential management
5. **Enable S3 bucket versioning**
6. **Enable S3 encryption at rest**
7. **Enable access logging**

---

## Additional Resources

- [AWS Access Keys Documentation](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html)
- [AWS S3 Permissions](https://docs.aws.amazon.com/AmazonS3/latest/userguide/access-control-overview.html)
- [Spring Boot Environment Variables](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/create-deploy-spring-boot.html)

---

## Support

If issue persists:
1. Check application logs for exact error message
2. Verify each credential independently
3. Test with AWS CLI: `aws s3 ls --profile default`
4. Check AWS Console for bucket and IAM user existence

