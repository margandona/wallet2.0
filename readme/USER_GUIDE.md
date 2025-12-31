# 👤 Guía de Usuario - Sistema Wallet

Guía completa para usar el Sistema de Billetera Digital Wallet.

---

## 📋 Contenido

1. [Primeros Pasos](#primeros-pasos)
2. [Gestión de Usuarios](#gestión-de-usuarios)
3. [Gestión de Cuentas](#gestión-de-cuentas)
4. [Operaciones Financieras](#operaciones-financieras)
5. [Consultas y Reportes](#consultas-y-reportes)
6. [Manejo de Errores](#manejo-de-errores)
7. [Preguntas Frecuentes](#preguntas-frecuentes)

---

## Primeros Pasos

### 1. Iniciar la Aplicación

```powershell
# Con Maven (Recomendado)
mvn clean install
mvn exec:java -Dexec.mainClass="com.wallet.Main"

# O con el script PowerShell
.\run.ps1
```

### 2. Menú Principal

Al iniciar, verás el menú principal:

```
═══════════════════════════════════
  💳 SISTEMA DE BILLETERA DIGITAL
═══════════════════════════════════

1. Gestión de Usuarios
2. Gestión de Cuentas
3. Operaciones Financieras
4. Consultas y Reportes
5. Salir
```

---

## Gestión de Usuarios

### Crear Usuario Nuevo

**Ruta**: Menú Principal → 1. Gestión de Usuarios → 1. Crear Usuario

**Datos Requeridos**:
- **Nombre**: 2-100 caracteres (ej: Juan)
- **Apellido**: 2-100 caracteres (ej: Pérez)
- **Email**: Formato válido (ej: juan@example.com)
  - Debe contener @ y dominio válido
  - Debe ser único en el sistema
- **Tipo de Documento**: CEDULA, PASAPORTE, LICENCIA
- **Número de Documento**: Según tipo
  - CEDULA: 7-10 dígitos
  - PASAPORTE: 6-9 caracteres alfanuméricos
  - LICENCIA: 7-12 dígitos

**Ejemplo**:
```
Ingrese nombre: Juan
Ingrese apellido: Pérez
Ingrese email: juan@example.com
Seleccione tipo de documento (CEDULA/PASAPORTE/LICENCIA): CEDULA
Ingrese número de documento: 12345678

✓ Usuario creado exitosamente con ID: usr-550e8400-e29b-41d4-a716-446655440000
```

**Errores Posibles**:
- ❌ "El email tiene un formato inválido" - Verificar formato de email
- ❌ "El email ya está registrado" - Usar otro email
- ❌ "El documento ya está registrado" - Verificar duplicados
- ❌ "El documento tiene un formato inválido" - Verificar formato según tipo

---

### Buscar Usuario

**Ruta**: Menú Principal → 1. Gestión de Usuarios → 2. Buscar Usuario

**Opciones**:
1. Por ID (UUID asignado al crear)
2. Por Email
3. Por Documento

**Ejemplo - Búsqueda por Email**:
```
Ingrese el email: juan@example.com

Usuario encontrado:
├─ ID: usr-550e8400-e29b-41d4-a716-446655440000
├─ Nombre: Juan Pérez
├─ Email: juan@example.com
├─ Documento: 12345678 (CEDULA)
└─ Estado: Activo
```

---

### Listar Usuarios

**Ruta**: Menú Principal → 1. Gestión de Usuarios → 3. Listar Todos los Usuarios

Muestra tabla con:
- ID
- Nombre Completo
- Email
- Documento
- Estado (Activo/Inactivo)
- Fecha de Creación

**Ejemplo**:
```
Total de usuarios: 3

┌──────────────────────────────────────────────────────────┐
│ ID          │ Nombre    │ Email              │ Estado    │
├──────────────────────────────────────────────────────────┤
│ usr-xxxx... │ Juan Pérez│ juan@example.com   │ ✓ Activo  │
│ usr-yyyy... │ Ana López │ ana@example.com    │ ✓ Activo  │
│ usr-zzzz... │ Carlos... │ carlos@example.com │ ✗ Inactivo│
└──────────────────────────────────────────────────────────┘
```

---

## Gestión de Cuentas

### Crear Cuenta

**Ruta**: Menú Principal → 2. Gestión de Cuentas → 1. Crear Cuenta

**Datos Requeridos**:
- **ID del Usuario**: Usuario debe existir en el sistema
- **Número de Cuenta**: 10-20 dígitos (debe ser único)
- **Saldo Inicial**: Cantidad no negativa (default: 0)
- **Moneda**: PEN (Soles Peruanos), USD (Dólares), EUR (Euros)

**Ejemplo**:
```
Ingrese el ID del usuario: usr-550e8400-e29b-41d4-a716-446655440000
Ingrese el número de cuenta: 1234567890
Ingrese el saldo inicial: 1000.00
Seleccione moneda (PEN/USD/EUR): PEN

✓ Cuenta creada exitosamente
├─ ID: cta-e29b-41d4-a716-446655440000
├─ Número: 1234567890
├─ Saldo: S/ 1,000.00
└─ Moneda: PEN
```

**Errores Posibles**:
- ❌ "Usuario no encontrado" - Verificar ID del usuario
- ❌ "El número de cuenta ya existe" - Usar otro número
- ❌ "El saldo no puede ser negativo" - Usar cantidad >= 0

---

### Consultar Saldo

**Ruta**: Menú Principal → 2. Gestión de Cuentas → 2. Consultar Saldo

**Datos Requeridos**:
- Número de Cuenta o ID de Cuenta

**Ejemplo**:
```
Ingrese el número de cuenta: 1234567890

Información de Cuenta:
├─ Número: 1234567890
├─ Propietario: Juan Pérez
├─ Saldo: S/ 1,000.00
├─ Moneda: PEN
├─ Estado: Activa
└─ Última actualización: 2025-01-15 14:23:45
```

---

### Listar Cuentas

**Ruta**: Menú Principal → 2. Gestión de Cuentas → 3. Listar Cuentas

Opciones:
1. Todas las cuentas del sistema
2. Cuentas de un usuario específico
3. Solo cuentas activas

**Ejemplo**:
```
Total de cuentas: 5

┌────────────────────────────────────────────────┐
│ Número    │ Titular   │ Saldo      │ Estado   │
├────────────────────────────────────────────────┤
│ 1234567890│ Juan Pérez│ S/ 1,000.00│ ✓ Activa │
│ 0987654321│ Ana López │ S/   500.00│ ✓ Activa │
│ 1111111111│ Carlos... │ $   200.00 │ ✗ Inactiv│
└────────────────────────────────────────────────┘
```

---

## Operaciones Financieras

### 💰 Realizar Depósito

**Ruta**: Menú Principal → 3. Operaciones Financieras → 1. Depositar

**Datos Requeridos**:
- **Número de Cuenta**: Cuenta destino
- **Monto**: Cantidad positiva (> 0)

**Validaciones**:
- ✓ Cuenta debe existir y estar activa
- ✓ Monto debe ser positivo
- ✓ Moneda debe coincidir con la de la cuenta

**Ejemplo**:
```
Ingrese el número de cuenta: 1234567890
Ingrese el monto a depositar: 500.00

✓ Depósito realizado exitosamente
├─ Monto: S/ 500.00
├─ Saldo anterior: S/ 1,000.00
├─ Saldo nuevo: S/ 1,500.00
└─ Fecha: 2025-01-15 14:25:30
```

**Errores Posibles**:
- ❌ "Cuenta no encontrada" - Verificar número de cuenta
- ❌ "Cuenta no está activa" - Activar cuenta primero
- ❌ "El monto debe ser mayor a 0" - Ingresar monto positivo

---

### 🏧 Realizar Retiro

**Ruta**: Menú Principal → 3. Operaciones Financieras → 2. Retirar

**Datos Requeridos**:
- **Número de Cuenta**: Cuenta origen
- **Monto**: Cantidad positiva (> 0)

**Validaciones**:
- ✓ Cuenta debe existir y estar activa
- ✓ Monto debe ser positivo
- ✓ Saldo disponible >= Monto a retirar

**Ejemplo**:
```
Ingrese el número de cuenta: 1234567890
Ingrese el monto a retirar: 300.00

✓ Retiro realizado exitosamente
├─ Monto: S/ 300.00
├─ Saldo anterior: S/ 1,500.00
├─ Saldo nuevo: S/ 1,200.00
└─ Fecha: 2025-01-15 14:30:45
```

**Errores Posibles**:
- ❌ "Saldo insuficiente" - Saldo disponible es menor al monto
- ❌ "El monto debe ser mayor a 0" - Ingresar monto positivo
- ❌ "Cuenta no está activa" - Activar cuenta primero

---

### 💸 Realizar Transferencia

**Ruta**: Menú Principal → 3. Operaciones Financieras → 3. Transferir

**Datos Requeridos**:
- **Número de Cuenta Origen**: Cuenta de la que sale el dinero
- **Número de Cuenta Destino**: Cuenta que recibe el dinero
- **Monto**: Cantidad positiva (> 0)

**Validaciones**:
- ✓ Ambas cuentas deben existir y estar activas
- ✓ Monto debe ser positivo
- ✓ Saldo origen >= Monto
- ✓ Cuentas deben tener la misma moneda (o convertir)

**Ejemplo**:
```
Ingrese número de cuenta origen: 1234567890
Ingrese número de cuenta destino: 0987654321
Ingrese monto a transferir: 200.00

✓ Transferencia realizada exitosamente
├─ Monto: S/ 200.00
├─ De: Juan Pérez (1234567890)
├─ Para: Ana López (0987654321)
├─ Fecha: 2025-01-15 14:35:20
└─ Transacción ID: trx-abcd-1234

Nuevo saldo origen: S/ 1,000.00
Nuevo saldo destino: S/ 700.00
```

**Errores Posibles**:
- ❌ "Saldo insuficiente" - No hay suficientes fondos
- ❌ "No se puede transferir a la misma cuenta" - Origen ≠ Destino
- ❌ "Monedas no coinciden" - Ambas deben ser PEN, USD o EUR

---

## Consultas y Reportes

### Historial de Transacciones

**Ruta**: Menú Principal → 4. Consultas y Reportes → 1. Ver Historial

**Opciones**:
1. Últimas 10 transacciones
2. Transacciones del último mes
3. Transacciones por tipo (Depósitos, Retiros, Transferencias)
4. Transacciones en rango de fechas

**Ejemplo - Últimas Transacciones**:
```
Historial de Transacciones - Cuenta 1234567890

┌────────────────────────────────────────────────────┐
│ Fecha      │ Tipo         │ Monto   │ Saldo      │
├────────────────────────────────────────────────────┤
│ 2025-01-15 │ TRANSFERENCIA│-S/ 200 │ S/ 1,000   │
│ 2025-01-15 │ DEPOSITO     │+S/ 500 │ S/ 1,200   │
│ 2025-01-15 │ RETIRO       │-S/ 300 │ S/ 700     │
│ 2025-01-14 │ DEPOSITO     │+S/ 800 │ S/ 1,000   │
└────────────────────────────────────────────────────┘

Total de transacciones: 4
```

---

### Resumen de Cuenta

**Ruta**: Menú Principal → 4. Consultas y Reportes → 2. Resumen de Cuenta

**Información Mostrada**:
- Datos del titular
- Información de la cuenta
- Saldo actual
- Total depositado (último mes)
- Total retirado (último mes)
- Transacciones recientes

**Ejemplo**:
```
═══════════════════════════════════════════
              RESUMEN DE CUENTA
═══════════════════════════════════════════

TITULAR
├─ Nombre: Juan Pérez
├─ Email: juan@example.com
└─ Documento: 12345678 (CEDULA)

CUENTA
├─ Número: 1234567890
├─ Estado: Activa
├─ Moneda: Soles Peruanos (PEN)
└─ Creada: 2025-01-01

MOVIMIENTOS (Última 30 días)
├─ Depósitos: S/ 3,500.00 (7 transacciones)
├─ Retiros: S/ 2,000.00 (4 transacciones)
├─ Transferencias enviadas: S/ 500.00 (2)
└─ Transferencias recibidas: S/ 1,000.00 (3)

SALDO ACTUAL: S/ 2,000.00
```

---

### Reporte de Todas las Cuentas

**Ruta**: Menú Principal → 4. Consultas y Reportes → 3. Reporte de Cuentas

Muestra resumen consolidado de todas las cuentas.

---

## Manejo de Errores

### Errores Comunes y Soluciones

#### Error: "El email ya está registrado"
**Causa**: Email duplicado en el sistema
**Solución**: 
- Usar email diferente para nuevo usuario
- O buscar usuario existente con ese email

#### Error: "El número de cuenta ya existe"
**Causa**: Número de cuenta duplicado
**Solución**:
- Generar número de cuenta único
- Usar formato: fecha+aleatorio o secuencial

#### Error: "Saldo insuficiente"
**Causa**: No hay suficiente dinero en cuenta
**Solución**:
- Realizar depósito primero
- O usar cuenta con saldo disponible

#### Error: "Cuenta no está activa"
**Causa**: Cuenta fue desactivada
**Solución**:
- Reactivar cuenta (si tienes permisos)
- O usar otra cuenta activa

#### Error: "No se puede transferir a la misma cuenta"
**Causa**: Intento de transferencia a sí mismo
**Solución**:
- Especificar cuenta destino diferente
- O realizar retiro si deseas extraer dinero

---

## Preguntas Frecuentes

### ¿Cómo cambio el email de un usuario?
**Respuesta**: El sistema actual no permite editar usuarios. Debes:
1. Buscar el usuario por su ID
2. Anotar la información
3. Crear nuevo usuario con email correcto
4. (Opcional) Eliminar usuario anterior

### ¿Puedo transferir dinero entre monedas diferentes?
**Respuesta**: No. El sistema valida que ambas cuentas tengan la misma moneda. Opciones:
- Retirar dinero de una cuenta
- Depositar en cuenta con otra moneda
- En futuro: agregar conversión automática

### ¿Cuál es el monto máximo que puedo transferir?
**Respuesta**: No hay límite técnico. Solo limitado por:
- Saldo disponible en cuenta origen
- Precisión: 2 decimales (ej: 1000.50)

### ¿Dónde se guardan los datos?
**Respuesta**: 
- Base de Datos SQLite: `wallet.db` (en raíz del proyecto)
- Logs operacionales: `wallet_operations.log`

### ¿Puedo ver el historial de transacciones de otros usuarios?
**Respuesta**: El sistema actual permite ver transacciones de cualquier cuenta si tienes el número de cuenta. En futuro: agregar control de acceso.

### ¿Qué pasa si desactivo una cuenta?
**Respuesta**: 
- No se pueden realizar operaciones en ella
- Los datos se conservan
- Se puede reactivar
- Al eliminar usuario, se eliminan todas sus cuentas

### ¿Cómo hago una copia de seguridad?
**Respuesta**: Hacer backup del archivo `wallet.db`:
```powershell
Copy-Item wallet.db wallet.db.backup
```

### ¿Cuántos usuarios y cuentas puedo crear?
**Respuesta**: No hay límite técnico. Limitado por:
- Espacio en disco (para BD)
- Memoria disponible
- Performance de SQLite

### ¿Qué datos de transacción se registran?
**Respuesta**: Para cada transacción se registra:
- ID único
- Tipo (Depósito, Retiro, Transferencia)
- Monto
- Saldo anterior
- Saldo nuevo
- Descripción (opcional)
- Fecha y hora
- Cuentas origen/destino (si aplica)

### ¿Puedo revertir una transacción?
**Respuesta**: No directamente. El sistema registra transacciones como historial. Opciones:
- Realizar transacción inversa manual (retiro si fue depósito, etc.)
- En futuro: agregar función de reversión

---

## Accesos Rápidos

```powershell
# Ejecutar aplicación
.\run.ps1

# Compilar
.\compile.ps1

# Tests
.\test.ps1

# Descargar dependencias
.\download-dependencies.ps1

# Build y ejecución conjunta
.\build-and-run.ps1
```

---

## Soporte

Para problemas:
1. Revisar esta guía (Preguntas Frecuentes)
2. Consultar logs: `wallet_operations.log`
3. Revisar documentación en `/readme`
4. Crear issue en repositorio

---

**Versión**: 1.0.0  
**Última actualización**: Enero 2025  
**Base de datos**: SQLite  
**Estado**: ✅ Operacional
