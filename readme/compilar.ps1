# ╔══════════════════════════════════════════════════════════════════════════════╗
# ║                                                                              ║
# ║                    🔨 SCRIPT DE COMPILACIÓN - WALLET APP                    ║
# ║                                                                              ║
# ║  Este script compila la aplicación Wallet usando Maven.                     ║
# ║  Genera un JAR ejecutable con todas las dependencias incluidas.            ║
# ║                                                                              ║
# ╚══════════════════════════════════════════════════════════════════════════════╝

# Configuración
$WORKSPACE = Split-Path -Parent $MyInvocation.MyCommand.Path
$TARGET_DIR = "$WORKSPACE\target"
$JAR_FILE = "$TARGET_DIR\wallet-app-1.0.0-jar-with-dependencies.jar"

# Colores para output
$Color_Header = "Cyan"
$Color_Success = "Green"
$Color_Warning = "Yellow"
$Color_Error = "Red"
$Color_Info = "White"

# ═══════════════════════════════════════════════════════════════════════════════
# FUNCIÓN: Mostrar encabezado
# ═══════════════════════════════════════════════════════════════════════════════
function Show-Header {
    Write-Host ""
    Write-Host "╔" -NoNewline -ForegroundColor $Color_Header
    Write-Host "═" * 78 -NoNewline -ForegroundColor $Color_Header
    Write-Host "╗" -ForegroundColor $Color_Header
    Write-Host "║" -NoNewline -ForegroundColor $Color_Header
    Write-Host " 🔨 COMPILACIÓN DE WALLET APPLICATION " -ForegroundColor $Color_Header -NoNewline
    Write-Host " " * 40 -NoNewline
    Write-Host "║" -ForegroundColor $Color_Header
    Write-Host "╚" -NoNewline -ForegroundColor $Color_Header
    Write-Host "═" * 78 -NoNewline -ForegroundColor $Color_Header
    Write-Host "╝" -ForegroundColor $Color_Header
    Write-Host ""
}

# ═══════════════════════════════════════════════════════════════════════════════
# FUNCIÓN: Mostrar paso
# ═══════════════════════════════════════════════════════════════════════════════
function Show-Step {
    param(
        [string]$Number,
        [string]$Description
    )
    Write-Host "  [$Number]" -ForegroundColor $Color_Info -NoNewline
    Write-Host " $Description" -ForegroundColor $Color_Info
}

# ═══════════════════════════════════════════════════════════════════════════════
# FUNCIÓN: Mostrar éxito
# ═══════════════════════════════════════════════════════════════════════════════
function Show-Success {
    param([string]$Message)
    Write-Host "  ✓ $Message" -ForegroundColor $Color_Success
}

# ═══════════════════════════════════════════════════════════════════════════════
# FUNCIÓN: Mostrar error
# ═══════════════════════════════════════════════════════════════════════════════
function Show-Error {
    param([string]$Message)
    Write-Host "  ✗ $Message" -ForegroundColor $Color_Error
}

# ═══════════════════════════════════════════════════════════════════════════════
# FUNCIÓN: Mostrar advertencia
# ═══════════════════════════════════════════════════════════════════════════════
function Show-Warning {
    param([string]$Message)
    Write-Host "  ⚠ $Message" -ForegroundColor $Color_Warning
}

# ═══════════════════════════════════════════════════════════════════════════════
# FUNCIÓN: Mostrar información
# ═══════════════════════════════════════════════════════════════════════════════
function Show-Info {
    param([string]$Message)
    Write-Host "  ℹ $Message" -ForegroundColor $Color_Info
}

# ═══════════════════════════════════════════════════════════════════════════════
# INICIO DEL SCRIPT
# ═══════════════════════════════════════════════════════════════════════════════

Show-Header

# Verificar que estamos en el directorio correcto
Write-Host "📁 Ubicación del proyecto:" -ForegroundColor $Color_Info
Write-Host "   $WORKSPACE" -ForegroundColor $Color_Info
Write-Host ""

# Verificar que existe pom.xml
if (-not (Test-Path "$WORKSPACE\pom.xml")) {
    Show-Error "No se encontró pom.xml en el directorio actual"
    Write-Host ""
    exit 1
}
Show-Success "Archivo pom.xml encontrado"

# Verificar Maven
Write-Host ""
Show-Step "1/4" "Verificando Maven..."
$mvn = mvn -version 2>&1
if ($LASTEXITCODE -ne 0) {
    Show-Error "Maven no está instalado o no está en el PATH"
    Write-Host ""
    exit 1
}
$mvnVersion = ($mvn | Select-Object -First 1)
Show-Success "Maven disponible: $mvnVersion"

# Mostrar información de compilación
Write-Host ""
Show-Step "2/4" "Preparando compilación..."
Write-Host "   Workspace:  $WORKSPACE"
Write-Host "   Target Dir: $TARGET_DIR"
Write-Host "   JAR File:   $JAR_FILE"

# Limpiar compilaciones anteriores (opcional)
Write-Host ""
Show-Step "3/4" "Limpiando compilaciones anteriores..."
Write-Host "   Ejecutando: mvn clean"
mvn clean -q
if ($LASTEXITCODE -eq 0) {
    Show-Success "Limpieza completada"
} else {
    Show-Warning "La limpieza no se completó correctamente"
}

# Compilar
Write-Host ""
Show-Step "4/4" "Compilando aplicación..."
Write-Host "   Ejecutando: mvn package -DskipTests -q"
Write-Host ""

$startTime = Get-Date
mvn package -DskipTests -q
$compilationExitCode = $LASTEXITCODE
$endTime = Get-Date
$duration = $endTime - $startTime

Write-Host ""

# Verificar resultado
if ($compilationExitCode -eq 0) {
    Show-Success "¡Compilación completada exitosamente!"
    
    # Verificar que existe el JAR
    if (Test-Path $JAR_FILE) {
        $jarSize = (Get-Item $JAR_FILE).Length / 1MB
        Show-Success "JAR generado: wallet-app-1.0.0-jar-with-dependencies.jar"
        Write-Host "   Tamaño:     $([Math]::Round($jarSize, 2)) MB"
        Write-Host "   Ubicación:  $JAR_FILE"
    } else {
        Show-Error "El archivo JAR no se encontró después de la compilación"
        exit 1
    }
} else {
    Show-Error "La compilación falló. Verifica los errores arriba."
    Write-Host ""
    exit 1
}

# Mostrar resumen
Write-Host ""
Write-Host "═" * 80 -ForegroundColor $Color_Header
Write-Host "📊 RESUMEN" -ForegroundColor $Color_Header
Write-Host "═" * 80 -ForegroundColor $Color_Header
Write-Host "  Tiempo de compilación: $([Math]::Round($duration.TotalSeconds, 2))s"
Write-Host "  Status:                ✓ EXITOSO"
Write-Host "  JAR ejecutable:        ✓ Disponible"
Write-Host ""

# Mostrar opciones
Write-Host "═" * 80 -ForegroundColor $Color_Header
Write-Host "🚀 PRÓXIMOS PASOS" -ForegroundColor $Color_Header
Write-Host "═" * 80 -ForegroundColor $Color_Header
Write-Host ""
Write-Host "  Para ejecutar la aplicación:"
Write-Host ""
Write-Host "  java -jar target/wallet-app-1.0.0-jar-with-dependencies.jar" -ForegroundColor $Color_Success
Write-Host ""
Write-Host "  O usa el script:"
Write-Host ""
Write-Host "  .\ejecutar.ps1" -ForegroundColor $Color_Success
Write-Host ""

Write-Host "═" * 80 -ForegroundColor $Color_Header
Write-Host ""
