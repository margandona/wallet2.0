# Test persistence fix
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$workDir = "c:\Users\marga\Desktop\NeekWorld\boot android\wallet"

Write-Host "Testing persistence..." -ForegroundColor Green

# Remove old DB
Remove-Item "$workDir\wallet.db" -ErrorAction SilentlyContinue

# Start app with simulated input
$appProcess = Start-Process -FilePath "java" `
    -ArgumentList "-jar", "$workDir\target\wallet-app-1.0.0-jar-with-dependencies.jar" `
    -WorkingDirectory $workDir `
    -NoNewWindow `
    -PassThru

# Wait for app to start
Start-Sleep -Seconds 8

# Send inputs: 1 (users), 1 (create), values, 0 (exit), 0 (main exit)
$commands = @("1", "1", "Test User", "test@email.com", "12345678", "0", "0")

foreach ($cmd in $commands) {
    try {
        $appProcess.StandardInput.WriteLine($cmd)
        Start-Sleep -Milliseconds 500
    } catch {
        Write-Host "Error sending input: $_"
    }
}

# Wait for app to finish
Start-Sleep -Seconds 3
$appProcess.Kill()

# Check database
Write-Host "`nChecking database..." -ForegroundColor Green
if (Test-Path "$workDir\wallet.db") {
    Write-Host "✓ Database file created" -ForegroundColor Green
    
    # List database tables
    Write-Host "`nDatabase content:" -ForegroundColor Cyan
    
    # Note: This requires sqlite3 to be installed
    Write-Host "Database file size: $(Get-Item $workDir\wallet.db | % {$_.Length / 1KB}KB"
} else {
    Write-Host "✗ Database file not found!" -ForegroundColor Red
}

Write-Host "`nTest completed" -ForegroundColor Green
