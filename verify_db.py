import sqlite3
from datetime import datetime

conn = sqlite3.connect('wallet.db')
c = conn.cursor()

# Get tables
c.execute("SELECT name FROM sqlite_master WHERE type='table'")
tables = [t[0] for t in c.fetchall()]
print('=' * 80)
print('📊 VERIFICACIÓN COMPLETA DE BASE DE DATOS - wallet.db')
print('=' * 80)
print(f'Tablas encontradas: {", ".join(tables)}\n')

# ==================== USUARIOS ====================
if 'usuarios' in tables:
    c.execute('SELECT COUNT(*) FROM usuarios')
    count = c.fetchone()[0]
    print('👤 USUARIOS')
    print(f'   Total: {count}')
    
    if count > 0:
        c.execute('''SELECT id, nombre, apellido, email, documento, tipo_documento, activo, created_at 
                     FROM usuarios ORDER BY created_at DESC''')
        for row in c.fetchall():
            print(f'   ├─ ID: {row[0]}')
            print(f'   │  Nombre: {row[1]} {row[2]}')
            print(f'   │  Email: {row[3]}')
            print(f'   │  Documento: {row[4]} ({row[5]})')
            print(f'   │  Activo: {"✓" if row[6] else "✗"}')
            print(f'   │  Creado: {row[7]}\n')
else:
    print('✗ Tabla usuarios no existe\n')

# ==================== CUENTAS ====================
if 'cuentas' in tables:
    c.execute('SELECT COUNT(*) FROM cuentas')
    count = c.fetchone()[0]
    print('🏦 CUENTAS')
    print(f'   Total: {count}')
    
    if count > 0:
        c.execute('''SELECT c.id, c.numero_cuenta, c.saldo, c.moneda, c.activa, c.usuario_id, u.nombre 
                     FROM cuentas c 
                     LEFT JOIN usuarios u ON c.usuario_id = u.id 
                     ORDER BY c.created_at DESC''')
        for row in c.fetchall():
            print(f'   ├─ ID: {row[0]}')
            print(f'   │  Número: {row[1]}')
            print(f'   │  Saldo: ${row[2]:,.2f} {row[3]}')
            print(f'   │  Activa: {"✓" if row[4] else "✗"}')
            print(f'   │  Usuario: {row[6] if row[6] else "N/A"} (ID: {row[5]})\n')
    else:
        print('   (ninguna cuenta creada)\n')
else:
    print('✗ Tabla cuentas no existe\n')

# ==================== TRANSACCIONES ====================
if 'transacciones' in tables:
    c.execute('SELECT COUNT(*) FROM transacciones')
    count = c.fetchone()[0]
    print('💱 TRANSACCIONES')
    print(f'   Total: {count}')
    
    if count > 0:
        c.execute('''SELECT t.id, t.tipo, t.monto, t.saldo_anterior, t.saldo_nuevo, 
                            t.descripcion, t.fecha_transaccion, c.numero_cuenta
                     FROM transacciones t
                     LEFT JOIN cuentas c ON t.cuenta_id = c.id
                     ORDER BY t.created_at DESC LIMIT 10''')
        for idx, row in enumerate(c.fetchall(), 1):
            print(f'   ├─ #{idx} ID: {row[0]}')
            print(f'   │  Tipo: {row[1]}')
            print(f'   │  Monto: ${row[2]:,.2f}')
            print(f'   │  Saldo Anterior: ${row[3]:,.2f} → Nuevo: ${row[4]:,.2f}')
            print(f'   │  Descripción: {row[5] if row[5] else "N/A"}')
            print(f'   │  Fecha: {row[6]}')
            print(f'   │  Cuenta: {row[7]}\n')
    else:
        print('   (ninguna transacción registrada)\n')
else:
    print('✗ Tabla transacciones no existe\n')

# ==================== VALIDACIONES ====================
print('✓ VALIDACIONES DE INTEGRIDAD')
if 'usuarios' in tables and 'cuentas' in tables:
    c.execute('''SELECT u.nombre, COUNT(c.id) as cuenta_count 
                 FROM usuarios u 
                 LEFT JOIN cuentas c ON u.id = c.usuario_id 
                 GROUP BY u.id''')
    for row in c.fetchall():
        print(f'   ├─ {row[0]}: {row[1]} cuenta(s)')

print('\n' + '=' * 80)
conn.close()
