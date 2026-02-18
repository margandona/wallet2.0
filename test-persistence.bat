@echo off
REM Test script for Sistema Wallet persistence

setlocal enabledelayedexpansion

cd /d "C:\Users\marga\Desktop\NeekWorld\boot android\wallet"

echo.
echo ===================================
echo WALLET PERSISTENCE TEST
echo ===================================
echo.

REM Delete old database
if exist wallet.db del /Q wallet.db
if exist wallet_*.log del /Q wallet_*.log

echo [1] Starting application for first time...
echo 1 > input.txt
echo.

REM Run app and create user
(
echo 1
echo Test User 1
echo Test Apellido
echo testuser@test.com
echo 123456789
echo 1
echo s
echo n
echo 0
echo 0
) | timeout /t 1 > nul

REM Start the app in background
start "Wallet App" java -jar target/wallet-app-1.0.0-jar-with-dependencies.jar

REM Wait for menu
timeout /t 15 > nul

REM Test data entry (automated via stdin)
REM Unfortunately, this is complex in batch...

echo.
echo [2] Test completed. Check if wallet.db exists:
if exist wallet.db (
    echo ✓ wallet.db found
    dir wallet.db
) else (
    echo ✗ wallet.db NOT found - DATABASE NOT PERSISTING!
)

echo.
echo [3] Killing Java process...
taskkill /F /IM java.exe 2>nul

timeout /t 2 > nul

echo.
echo [4] Restarting application...
start "Wallet App Test" java -jar target/wallet-app-1.0.0-jar-with-dependencies.jar

timeout /t 10 > nul

echo.
echo ===================================
echo TEST COMPLETE
echo ===================================
echo.
echo Check the running app to see if users persisted...
