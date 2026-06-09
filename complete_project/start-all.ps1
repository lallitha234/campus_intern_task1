# ============================================================
#  START ALL - Campus Intern Task 1
#  Double-click this file OR run: powershell -File start-all.ps1
#  Automatically fixes JAVA_HOME spaces issue
# ============================================================

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "   CAMPUS INTERN TASK 1 - Starting All Apps" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Find Java and fix JAVA_HOME (removes spaces using short path)
try {
    $javaExe = (Get-Command java -ErrorAction Stop).Source
    $javaBin  = Split-Path $javaExe -Parent
    $javaFull = Split-Path $javaBin -Parent

    # Convert to 8.3 short path (removes spaces like "Program Files")
    $fso       = New-Object -ComObject Scripting.FileSystemObject
    $shortPath = $fso.GetFolder($javaFull).ShortPath

    $env:JAVA_HOME = $shortPath
    Write-Host "Java found and JAVA_HOME fixed: $shortPath" -ForegroundColor Green
} catch {
    Write-Host "ERROR: Java not found in PATH." -ForegroundColor Red
    Write-Host "Install Java 17 from: https://adoptium.net" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

$root = $PSScriptRoot

# Step 2: Define all 4 apps
$apps = @(
    @{ name="URL Shortener API";        folder="url-shortener-api";        port=8081 },
    @{ name="Email Validation API";     folder="email-validation-api";     port=8082 },
    @{ name="Password Validation API";  folder="password-validation-api";  port=8083 },
    @{ name="API Monitor Dashboard";    folder="api-monitor";              port=9090 }
)

# Step 3: Start each app in its own PowerShell window
foreach ($app in $apps) {
    $appPath = Join-Path $root $app.folder

    if (-not (Test-Path $appPath)) {
        Write-Host "SKIP: Folder not found - $($app.folder)" -ForegroundColor Red
        continue
    }

    Write-Host "Starting $($app.name) on port $($app.port)..." -ForegroundColor Yellow

    $cmd = "`$env:JAVA_HOME='$shortPath'; " +
           "Write-Host 'Starting $($app.name)...' -ForegroundColor Cyan; " +
           "cd '$appPath'; " +
           ".\mvnw.cmd spring-boot:run"

    Start-Process powershell -ArgumentList "-NoExit", "-Command", $cmd
    Start-Sleep -Seconds 3
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  All apps launching in separate windows!" -ForegroundColor Green
Write-Host ""
Write-Host "  Wait 2-3 mins, then open:" -ForegroundColor White
Write-Host "  http://localhost:9090  (Dashboard)" -ForegroundColor Green
Write-Host "  http://localhost:8081/actuator/health" -ForegroundColor Gray
Write-Host "  http://localhost:8082/actuator/health" -ForegroundColor Gray
Write-Host "  http://localhost:8083/actuator/health" -ForegroundColor Gray
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Read-Host "Press Enter to close this window"
