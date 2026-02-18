# Script combinado: Compilar y Ejecutar
# Compila y ejecuta el proyecto Wallet en un solo comando

Write-Host "╔════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   Build & Run - Proyecto Wallet       ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Compilar
. "$PSScriptRoot\compile.ps1"

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "════════════════════════════════════════" -ForegroundColor Cyan
    Write-Host "         🚀 EJECUTANDO APLICACIÓN       " -ForegroundColor Cyan
    Write-Host "════════════════════════════════════════" -ForegroundColor Cyan
    
    # Ejecutar
    . "$PSScriptRoot\run.ps1"
}
else {
    Write-Host "❌ No se puede ejecutar debido a errores de compilación" -ForegroundColor Red
    exit 1
}
