# Script de compilación para Windows (PowerShell)
# Compila el proyecto Wallet usando Maven

Write-Host "Compilando Proyecto Wallet..." -ForegroundColor Cyan
Write-Host ""

# Intentar encontrar Maven
$mvnPath = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $mvnPath) {
    # Buscar en MAVEN_HOME
    if (Test-Path env:MAVEN_HOME) {
        $mvnExe = Join-Path $env:MAVEN_HOME "bin\mvn.cmd"
        if (Test-Path $mvnExe) {
            Write-Host "Maven encontrado en MAVEN_HOME" -ForegroundColor Green
            & $mvnExe clean compile
        } else {
            Write-Host "Maven no encontrado en MAVEN_HOME" -ForegroundColor Red
            exit 1
        }
    } else {
        Write-Host "Maven no está instalado o no está en el PATH" -ForegroundColor Red
        Write-Host "Por favor instala Maven o configura MAVEN_HOME" -ForegroundColor Yellow
        exit 1
    }
} else {
    Write-Host "Usando Maven: $($mvnPath.Source)" -ForegroundColor Green
    mvn clean compile
}

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "Compilacion exitosa!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Archivos compilados en: target\classes" -ForegroundColor Green
    Write-Host ""
    Write-Host "Para ejecutar la aplicacion, usa:" -ForegroundColor Cyan
    Write-Host "   .\run.ps1" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "Error en la compilacion" -ForegroundColor Red
    exit 1
}
