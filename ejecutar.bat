@echo off
REM ============================================================================
REM   SCRIPT DE EJECUCION - WALLET APPLICATION
REM ============================================================================

setlocal enabledelayedexpansion

set WORKSPACE=%cd%
set JAR_FILE=%WORKSPACE%\target\wallet-app-1.0.0-jar-with-dependencies.jar

echo.
echo ============================================================================
echo  EJECUTAR WALLET APPLICATION
echo ============================================================================
echo.

REM Verificar que el JAR existe
if not exist "%JAR_FILE%" (
    echo [ERROR] No se encontro el archivo JAR: %JAR_FILE%
    echo.
    echo Necesitas compilar la aplicacion primero.
    echo.
    echo Ejecuta: compilar.bat
    echo.
    exit /b 1
)

echo [OK] Archivo JAR encontrado
echo   Ubicacion: %JAR_FILE%
echo.

REM Ejecutar
echo Iniciando aplicacion...
echo.
echo ============================================================================
echo.

java -jar "%JAR_FILE%"
set EXIT_CODE=%errorlevel%

echo.
echo ============================================================================
echo.

if %EXIT_CODE% equ 0 (
    echo [OK] Aplicacion cerrada exitosamente
) else (
    echo [ERROR] La aplicacion termino con codigo de error: %EXIT_CODE%
)

echo.

endlocal
exit /b %EXIT_CODE%
