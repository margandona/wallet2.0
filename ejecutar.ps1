# ╔══════════════════════════════════════════════════════════════════════════════╗
# ║                                                                              ║
# ║                    ▶️  SCRIPT DE EJECUCIÓN - WALLET APP                     ║
# ║                                                                              ║
# ║  Este script ejecuta la aplicación Wallet.                                 ║
# ║  Verifica que la compilación existe y luego inicia la app.                ║
# ║                                                                              ║
# ╚══════════════════════════════════════════════════════════════════════════════╝

# Configuración
$WORKSPACE = Split-Path -Parent $MyInvocation.MyCommand.Path
$JAR_FILE = "$WORKSPACE\target\wallet-app-1.0.0-jar-with-dependencies.jar"

# Colores
$Color_Header = "Cyan"
$Color_Success = "Green"
$Color_Error = "Red"
$Color_Info = "White"

# ═══════════════════════════════════════════════════════════════════════════════
function Show-Header {
    Write-Host ""
    Write-Host "╔" -NoNewline -ForegroundColor $Color_Header
    Write-Host "═" * 78 -NoNewline -ForegroundColor $Color_Header
    Write-Host "╗" -ForegroundColor $Color_Header
    Write-Host "║" -NoNewline -ForegroundColor $Color_Header
    Write-Host " ▶️  EJECUTAR WALLET APPLICATION " -ForegroundColor $Color_Header -NoNewline
    Write-Host " " * 46 -NoNewline
    Write-Host "║" -ForegroundColor $Color_Header
    Write-Host "╚" -NoNewline -ForegroundColor $Color_Header
    Write-Host "═" * 78 -NoNewline -ForegroundColor $Color_Header
    Write-Host "╝" -ForegroundColor $Color_Header
    Write-Host ""
}

function Show-Error {
    param([string]$Message)
    Write-Host "  ✗ $Message" -ForegroundColor $Color_Error
}

function Show-Info {
    param([string]$Message)
    Write-Host "  ℹ $Message" -ForegroundColor $Color_Info
}

function Show-Success {
    param([string]$Message)
    Write-Host "  ✓ $Message" -ForegroundColor $Color_Success
}

# ═══════════════════════════════════════════════════════════════════════════════

Show-Header

# Verificar que el JAR existe
Show-Info "Buscando archivo ejecutable..."
if (-not (Test-Path $JAR_FILE)) {
    Show-Error "No se encontró el archivo JAR: $JAR_FILE"
    Write-Host ""
    Write-Host "  ¿Necesitas compilar la aplicación?" -ForegroundColor $Color_Info
    Write-Host ""
    Write-Host "  Ejecuta: .\compilar.ps1" -ForegroundColor $Color_Success
    Write-Host ""
    exit 1
}

Show-Success "Archivo JAR encontrado"
$jarSize = (Get-Item $JAR_FILE).Length / 1MB
Write-Host "  Tamaño: $([Math]::Round($jarSize, 2)) MB"
Write-Host ""

# Ejecutar
Show-Info "Iniciando aplicación..."
Write-Host ""
Write-Host "═" * 80 -ForegroundColor $Color_Header
Write-Host ""

try {
    java -jar $JAR_FILE
    $exitCode = $LASTEXITCODE
} catch {
    Show-Error "Error al ejecutar la aplicación: $_"
    exit 1
}

Write-Host ""
Write-Host "═" * 80 -ForegroundColor $Color_Header
Write-Host ""

if ($exitCode -eq 0) {
    Show-Success "Aplicación cerrada exitosamente"
} else {
    Show-Error "La aplicación terminó con código de error: $exitCode"
}

Write-Host ""
