@echo off
REM =============================================================================
REM Script to run Car Rental Management application with AWS S3 Configuration
REM This script sets up all required environment variables before running the app
REM =============================================================================

echo.
echo ============================================================
echo   CAR RENTAL MANAGEMENT - Startup Script
echo ============================================================
echo.

REM Check if .env file exists in project root
if exist ".env" (
    echo Loading environment variables from .env file...
    for /f "usebackq tokens=1,2 delims==" %%A in (".env") do (
        if not "%%B"=="" (
            set "%%A=%%B"
            echo   - %%A loaded
        )
    )
    echo.
) else (
    echo NOTE: .env file not found
    echo Create .env file with your AWS credentials in project root
    echo.
)

REM Set default values if not already set
if not defined AWS_S3_REGION set AWS_S3_REGION=us-east-1
if not defined SERVER_PORT set SERVER_PORT=8080

echo ============================================================
echo   Current Configuration:
echo ============================================================
echo   Spring Application Name: %SPRING_APPLICATION_NAME%
echo   Server Port: %SERVER_PORT%
echo   AWS Region: %AWS_S3_REGION%
echo   AWS S3 Bucket: %AWS_S3_BUCKET_NAME%
echo   Database URL: %DB_URL%
echo ============================================================
echo.

REM Verify essential credentials are set
if "%AWS_S3_ACCESS_KEY%"=="" (
    echo [ERROR] AWS_S3_ACCESS_KEY not set!
    echo Please set it via:
    echo   set AWS_S3_ACCESS_KEY=your-access-key
    echo Or create .env file in project root
    pause
    exit /b 1
)

if "%AWS_S3_SECRET_KEY%"=="" (
    echo [ERROR] AWS_S3_SECRET_KEY not set!
    echo Please set it via:
    echo   set AWS_S3_SECRET_KEY=your-secret-key
    echo Or create .env file in project root
    pause
    exit /b 1
)

if "%AWS_S3_BUCKET_NAME%"=="" (
    echo [ERROR] AWS_S3_BUCKET_NAME not set!
    echo Please set it via:
    echo   set AWS_S3_BUCKET_NAME=your-bucket-name
    echo Or create .env file in project root
    pause
    exit /b 1
)

echo [OK] All required AWS S3 credentials are configured
echo.
echo Starting Spring Boot application...
echo.

REM Run the application with Maven
mvnw.cmd clean spring-boot:run

pause

