# ============================================================================
# Car Rental Management - AWS S3 Setup and Launch Script
# ============================================================================
# This script helps you configure AWS credentials and start the application
# Run as Administrator for best results
# ============================================================================

param(
    [switch]$NoWait = $false
)

function Write-Header {
    Clear-Host
    Write-Host "═══════════════════════════════════════════════════════════════════════════════" -ForegroundColor Cyan
    Write-Host "  CAR RENTAL MANAGEMENT - AWS S3 Setup & Launch" -ForegroundColor Cyan
    Write-Host "═══════════════════════════════════════════════════════════════════════════════" -ForegroundColor Cyan
    Write-Host ""
}

function Write-Success {
    param([string]$Message)
    Write-Host "✓ " -ForegroundColor Green -NoNewline
    Write-Host $Message -ForegroundColor White
}

function Write-Error {
    param([string]$Message)
    Write-Host "✗ " -ForegroundColor Red -NoNewline
    Write-Host $Message -ForegroundColor White
}

function Write-Warning {
    param([string]$Message)
    Write-Host "⚠ " -ForegroundColor Yellow -NoNewline
    Write-Host $Message -ForegroundColor White
}

function Write-Info {
    param([string]$Message)
    Write-Host "ℹ " -ForegroundColor Cyan -NoNewline
    Write-Host $Message -ForegroundColor White
}

Write-Header

# ============================================================================
# Check for .env file
# ============================================================================
Write-Host ""
Write-Host "Step 1: Checking for .env file..." -ForegroundColor Yellow
Write-Host "─────────────────────────────────────────────────────────────────────────────────"

$envFilePath = Join-Path (Get-Location) ".env"

if (Test-Path $envFilePath) {
    Write-Success ".env file found"
    Write-Info "Loading environment variables from .env file..."

    $envContent = Get-Content $envFilePath | ForEach-Object {
        if ($_ -match '^\s*[^#=]+=[^=]+$') { $_ }
    }

    $envCount = 0
    foreach ($line in $envContent) {
        $key, $value = $line -split '=', 2
        $key = $key.Trim()
        $value = $value.Trim()

        if ($key -and $value) {
            [System.Environment]::SetEnvironmentVariable($key, $value, [System.EnvironmentVariableTarget]::Process)
            $envCount++
        }
    }

    Write-Success "Loaded $envCount environment variables from .env"
} else {
    Write-Warning ".env file not found in current directory"
    Write-Info "Current directory: $(Get-Location)"
    Write-Info "Create a .env file with your AWS credentials in the project root"
}

# ============================================================================
# Validate AWS Credentials
# ============================================================================
Write-Host ""
Write-Host "Step 2: Validating AWS S3 Configuration..." -ForegroundColor Yellow
Write-Host "─────────────────────────────────────────────────────────────────────────────────"

$accessKey = [System.Environment]::GetEnvironmentVariable("AWS_S3_ACCESS_KEY")
$secretKey = [System.Environment]::GetEnvironmentVariable("AWS_S3_SECRET_KEY")
$region = [System.Environment]::GetEnvironmentVariable("AWS_S3_REGION")
$bucketName = [System.Environment]::GetEnvironmentVariable("AWS_S3_BUCKET_NAME")

$isValid = $true

if (-not $accessKey -or $accessKey -eq "YOUR_AWS_ACCESS_KEY") {
    Write-Error "AWS_S3_ACCESS_KEY not configured"
    Write-Info "Set it via: `$env:AWS_S3_ACCESS_KEY = 'your-access-key'"
    $isValid = $false
} else {
    Write-Success "AWS_S3_ACCESS_KEY is set"
}

if (-not $secretKey -or $secretKey -eq "YOUR_AWS_SECRET_KEY") {
    Write-Error "AWS_S3_SECRET_KEY not configured"
    Write-Info "Set it via: `$env:AWS_S3_SECRET_KEY = 'your-secret-key'"
    $isValid = $false
} else {
    Write-Success "AWS_S3_SECRET_KEY is set"
}

if (-not $region) {
    Write-Warning "AWS_S3_REGION not configured, using default: us-east-1"
    $region = "us-east-1"
    [System.Environment]::SetEnvironmentVariable("AWS_S3_REGION", $region, [System.EnvironmentVariableTarget]::Process)
} else {
    Write-Success "AWS_S3_REGION: $region"
}

if (-not $bucketName -or $bucketName -eq "your-bucket-name") {
    Write-Error "AWS_S3_BUCKET_NAME not configured"
    Write-Info "Set it via: `$env:AWS_S3_BUCKET_NAME = 'your-bucket-name'"
    $isValid = $false
} else {
    Write-Success "AWS_S3_BUCKET_NAME: $bucketName"
}

# ============================================================================
# Check other required credentials
# ============================================================================
Write-Host ""
Write-Host "Step 3: Validating Database Configuration..." -ForegroundColor Yellow
Write-Host "─────────────────────────────────────────────────────────────────────────────────"

$dbUrl = [System.Environment]::GetEnvironmentVariable("DB_URL") -or "jdbc:mysql://localhost:3306/carrentaldb"
$dbUsername = [System.Environment]::GetEnvironmentVariable("DB_USERNAME") -or "root"
$dbPassword = [System.Environment]::GetEnvironmentVariable("DB_PASSWORD")

Write-Success "Database URL: $dbUrl"
Write-Success "Database Username: $dbUsername"

# ============================================================================
# Check JWT Secret
# ============================================================================
Write-Host ""
Write-Host "Step 4: Validating JWT Configuration..." -ForegroundColor Yellow
Write-Host "─────────────────────────────────────────────────────────────────────────────────"

$jwtSecret = [System.Environment]::GetEnvironmentVariable("JWT_SECRET")

if (-not $jwtSecret -or $jwtSecret.Length -lt 32) {
    Write-Warning "JWT_SECRET not properly configured (must be 32+ characters)"
    Write-Info "A default will be used, but set JWT_SECRET for production"
} else {
    Write-Success "JWT_SECRET is configured"
}

# ============================================================================
# Summary and Decision
# ============================================================================
Write-Host ""
Write-Host "Step 5: Configuration Summary" -ForegroundColor Yellow
Write-Host "─────────────────────────────────────────────────────────────────────────────────"

if ($isValid) {
    Write-Success "All required AWS S3 credentials are configured!"
    Write-Host ""
    Write-Host "Configuration Details:" -ForegroundColor Cyan
    Write-Host "  AWS Access Key: $($accessKey.Substring(0,4))...***" -ForegroundColor Gray
    Write-Host "  AWS Region: $region" -ForegroundColor Gray
    Write-Host "  S3 Bucket: $bucketName" -ForegroundColor Gray
    Write-Host "  Database: $dbUrl" -ForegroundColor Gray
} else {
    Write-Error "Configuration incomplete - missing required credentials"
    Write-Host ""
    Write-Host "Please configure the missing variables and try again." -ForegroundColor Red
    Write-Host ""
    Write-Host "Quick Setup Guide:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "1. Get AWS credentials:"
    Write-Host "   - Go to AWS Console → IAM → Users → Security credentials" -ForegroundColor Gray
    Write-Host "   - Click 'Create access key'" -ForegroundColor Gray
    Write-Host "   - Copy Access Key ID and Secret Access Key" -ForegroundColor Gray
    Write-Host ""
    Write-Host "2. Set environment variables in PowerShell:" -ForegroundColor Gray
    Write-Host "   " -NoNewline
    Write-Host "`$env:AWS_S3_ACCESS_KEY = 'AKIA...'" -ForegroundColor Magenta
    Write-Host "   " -NoNewline
    Write-Host "`$env:AWS_S3_SECRET_KEY = 'wJalr...'" -ForegroundColor Magenta
    Write-Host "   " -NoNewline
    Write-Host "`$env:AWS_S3_BUCKET_NAME = 'my-bucket'" -ForegroundColor Magenta
    Write-Host ""

    if (-not $NoWait) {
        Read-Host "Press Enter to exit"
    }
    exit 1
}

# ============================================================================
# Start Application
# ============================================================================
Write-Host ""
Write-Host "Step 6: Starting Application..." -ForegroundColor Yellow
Write-Host "─────────────────────────────────────────────────────────────────────────────────"
Write-Host ""

Write-Info "Launching Spring Boot application..."
Write-Info "This may take 30-60 seconds to start..."
Write-Host ""

# Run Maven clean and spring-boot:run
& .\mvnw.cmd clean spring-boot:run

if ($LASTEXITCODE -ne 0) {
    Write-Error "Application failed to start (exit code: $LASTEXITCODE)"
} else {
    Write-Success "Application stopped normally"
}

if (-not $NoWait) {
    Write-Host ""
    Read-Host "Press Enter to exit"
}

