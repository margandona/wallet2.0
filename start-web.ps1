# Script para iniciar el servidor web Jetty
Write-Host "Iniciando servidor Jetty en puerto 8090..." -ForegroundColor Cyan
Write-Host "Compilando proyecto..." -ForegroundColor Yellow
mvn clean compile -DskipTests -q

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilacion exitosa. Iniciando servidor..." -ForegroundColor Green
    Write-Host "URL: http://localhost:8090/wallet/" -ForegroundColor Cyan
    Write-Host "Presiona Ctrl+C para detener el servidor" -ForegroundColor Yellow
    Write-Host ""
    mvn jetty:run -DskipTests
} else {
    Write-Host "ERROR: La compilacion fallo" -ForegroundColor Red
    exit 1
}
