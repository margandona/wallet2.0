# Script de pruebas completas de gestión de cuentas
# Este script realiza una serie de operaciones en la aplicación wallet

Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   PRUEBAS DE GESTIÓN DE CUENTAS - WALLET APPLICATION      ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

Write-Host "📋 INSTRUCCIONES DE PRUEBA MANUAL:" -ForegroundColor Yellow
Write-Host ""
Write-Host "  PARTE 1: CREAR USUARIO"
Write-Host "  ─────────────────────"
Write-Host "  1. Seleccionar opción: 1 (Crear Usuario)"
Write-Host "  2. Ingresar datos:"
Write-Host "     Nombre: juan"
Write-Host "     Apellido: perez"
Write-Host "     Email: juan@perez.cl"
Write-Host "     CEDULA: 987654321"
Write-Host ""

Write-Host "  PARTE 2: CREAR CUENTA"
Write-Host "  ────────────────────"
Write-Host "  1. Seleccionar opción: 3 (Gestión de Cuentas)"
Write-Host "  2. Seleccionar opción: 1 (Crear Cuenta)"
Write-Host "  3. Email: juan@perez.cl"
Write-Host ""

Write-Host "  PARTE 3: DEPOSITAR DINERO"
Write-Host "  ─────────────────────────"
Write-Host "  1. Seleccionar opción: 3 (Gestión de Cuentas)"
Write-Host "  2. Seleccionar opción: 3 (Depositar Dinero)"
Write-Host "  3. Número de cuenta: (se muestra en pantalla)"
Write-Host "  4. Monto: 3000000"
Write-Host "  5. Descripción: deposito inicial"
Write-Host ""

Write-Host "  PARTE 4: RETIRAR DINERO"
Write-Host "  ──────────────────────"
Write-Host "  1. Seleccionar opción: 3 (Gestión de Cuentas)"
Write-Host "  2. Seleccionar opción: 4 (Retirar Dinero)"
Write-Host "  3. Número de cuenta: (mismo)"
Write-Host "  4. Monto: 500000"
Write-Host "  5. Descripción: retiro parcial"
Write-Host ""

Write-Host "  PARTE 5: CREAR SEGUNDO USUARIO Y TRANSFERENCIA"
Write-Host "  ─────────────────────────────────────────────"
Write-Host "  1. Crear otro usuario: carlos, carlos@carlos.cl, 111222333"
Write-Host "  2. Crear cuenta para carlos"
Write-Host "  3. Ir a Gestión de Cuentas → Transferir Dinero"
Write-Host "  4. Cuenta origen: primera cuenta de juan"
Write-Host "  5. Cuenta destino: cuenta de carlos"
Write-Host "  6. Monto: 1000000"
Write-Host ""

Write-Host "  PARTE 6: CONSULTAR CUENTAS Y TRANSACCIONES"
Write-Host "  ───────────────────────────────────────────"
Write-Host "  1. Buscar cuenta por número"
Write-Host "  2. Verificar saldos finales"
Write-Host "  3. Ver historial de transacciones"
Write-Host ""

Write-Host "  PARTE 7: SALIR"
Write-Host "  ──────────────"
Write-Host "  Seleccionar opción: 0 (Salir)"
Write-Host ""

Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
Write-Host "🚀 INICIANDO APLICACIÓN..." -ForegroundColor Green
Write-Host ""

# Start the application
java -jar target/wallet-app-1.0.0-jar-with-dependencies.jar

Write-Host ""
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "📊 VERIFICANDO DATOS EN BASE DE DATOS..." -ForegroundColor Green
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# Run verification script
python verify_db.py
