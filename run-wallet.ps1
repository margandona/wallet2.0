# Script para ejecutar la aplicación Sistema Wallet en Windows

Write-Host "════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "? WALLET - SISTEMA DE BILLETERA DIGITAL" -ForegroundColor Green
Write-Host "════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
Write-Host "🚀 Iniciando aplicación..." -ForegroundColor Yellow
Write-Host ""

# Cambiar al directorio del script
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptPath

# Configurar variables de entorno
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# Ejecutar la aplicación JAR
java -jar target/wallet-app-1.0.0-jar-with-dependencies.jar

Write-Host ""
Write-Host "✅ Aplicación terminada" -ForegroundColor Green
