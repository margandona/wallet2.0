@echo off
REM ============================================================================
REM   SCRIPT DE COMPILACION - WALLET APPLICATION
REM ============================================================================

setlocal enabledelayedexpansion

set WORKSPACE=%cd%
set TARGET_DIR=%WORKSPACE%\target
set JAR_FILE=%TARGET_DIR%\wallet-app-1.0.0-jar-with-dependencies.jar

echo.
echo ============================================================================
echo  COMPILACION DE WALLET APPLICATION
echo ============================================================================
echo.

REM Verificar pom.xml
if not exist "%WORKSPACE%\pom.xml" (
    echo [ERROR] No se encontro pom.xml en el directorio actual
    echo.
    exit /b 1
)
echo [OK] Archivo pom.xml encontrado

REM Verificar Maven
echo.
echo Verificando Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven no esta instalado o no esta en el PATH
    echo.
    exit /b 1
)
for /f "tokens=3" %%i in ('mvn -version ^| findstr /R "Apache Maven"') do set MVN_VERSION=%%i
echo [OK] Maven disponible: %MVN_VERSION%

REM Mostrar informacion
echo.
echo Informacion de compilacion:
echo   Workspace:  %WORKSPACE%
echo   Target Dir: %TARGET_DIR%
echo   JAR File:   %JAR_FILE%
echo.

REM Limpiar compilaciones anteriores
echo Limpiando compilaciones anteriores...
mvn clean -q
if errorlevel 1 (
    echo [ADVERTENCIA] La limpieza no se completo correctamente
) else (
    echo [OK] Limpieza completada
)

REM Compilar
echo.
echo Compilando aplicacion...
echo   Ejecutando: mvn package -DskipTests -q
echo.

mvn package -DskipTests -q
if errorlevel 1 (
    echo.
    echo [ERROR] La compilacion fallo
    echo.
    exit /b 1
)

echo.
echo [OK] Compilacion completada exitosamente!

REM Verificar JAR
if exist "%JAR_FILE%" (
    for /F %%A in ('powershell -Command "(Get-Item '%JAR_FILE%').Length / 1MB"') do set JAR_SIZE=%%A
    echo [OK] JAR generado: wallet-app-1.0.0-jar-with-dependencies.jar
    echo   Tamano:     %JAR_SIZE:~0,5% MB
    echo   Ubicacion:  %JAR_FILE%
) else (
    echo [ERROR] El archivo JAR no se encontro despues de la compilacion
    exit /b 1
)

echo.
echo ============================================================================
echo  PROXIMOS PASOS
echo ============================================================================
echo.
echo Para ejecutar la aplicacion:
echo.
echo   java -jar target/wallet-app-1.0.0-jar-with-dependencies.jar
echo.
echo O usa el script:
echo.
echo   ejecutar.bat
echo.
echo ============================================================================
echo.

endlocal
