# 📄 INFORMACIÓN PARA DOCUMENTO WORD - WALLET 2.0

## ✅ INFORMACIÓN COMPLETA PARA TU DOCUMENTO

Este archivo contiene TODA la información que necesitas para tu documento Word con instrucciones claras.

---

## 🎯 ESTRUCTURA DEL DOCUMENTO WORD

Tu documento debe tener estas secciones en orden:

```
1. PORTADA
2. INTRODUCCIÓN
3. DIAGRAMA ER (IMAGEN)
4. SCRIPTS SQL (CÓDIGO ORDENADO)
5. CAPTURAS DE PANTALLA
6. EXPLICACIÓN DE FUNCIONAMIENTO
7. CONCLUSIÓN
```

---

## 1️⃣ INTRODUCCIÓN (Copia esto a Word)

---

### Introducción

**Proyecto**: WALLET 2.0 - Billetera Digital
**Versión**: 1.0.0
**Fecha**: Diciembre 31, 2025
**Lenguaje**: Java 21 LTS
**Base de Datos**: SQLite

#### Objetivo

Desarrollar un sistema de billetera digital que permita a los usuarios:

- Registrarse con email y documento únicos
- Crear múltiples cuentas bancarias en diferentes monedas
- Realizar transacciones (depósitos, retiros, transferencias)
- Consultar historial de operaciones con auditoría completa
- Convertir divisas en tiempo real (30+ monedas soportadas)

#### Alcance

El sistema implementa las siguientes funcionalidades:

- **Gestión de Usuarios**: Registro, búsqueda, listado
- **Gestión de Cuentas**: Crear, activar, consultar saldo
- **Transacciones**: Depósitos, retiros, transferencias
- **Conversor de Divisas**: API de tasas en tiempo real
- **Persistencia**: Base de datos SQLite con Hibernate ORM
- **Auditoría**: Historial completo de operaciones

#### Tecnología

- **Lenguaje**: Java 21 LTS
- **Framework ORM**: Hibernate 6.4.4.Final
- **API de Persistencia**: Jakarta Persistence 3.1
- **Base de Datos**: SQLite 3.44.0.0
- **Herramienta de Build**: Maven 3.9.6
- **Patrón de Arquitectura**: Clean Architecture (4 capas)

---

## 2️⃣ DIAGRAMA ER (Copiar a Word como IMAGEN)

---

### Diagrama Entidad-Relación

```
┌─────────────────────────────┐
│         USUARIOS            │
├─────────────────────────────┤
│ PK: id (UUID)              │
│ UQ: email                  │
│ UQ: documento              │
│ nombre VARCHAR(100)        │
│ apellido VARCHAR(100)      │
│ tipo_documento VARCHAR(20) │
│ activo BOOLEAN             │
│ created_at TIMESTAMP       │
│ updated_at TIMESTAMP       │
└────────────┬────────────────┘
             │
             │ 1:N (usuario_id FK)
             │
             ▼
┌─────────────────────────────┐
│         CUENTAS             │
├─────────────────────────────┤
│ PK: id (UUID)              │
│ UQ: numero_cuenta          │
│ FK: usuario_id → USUARIOS  │
│ saldo DECIMAL(19,2)        │
│ moneda VARCHAR(3)          │
│ activa BOOLEAN             │
│ created_at TIMESTAMP       │
│ updated_at TIMESTAMP       │
└────────────┬────────────────┘
             │
             │ 1:N (cuenta_id FK)
             │
             ▼
┌─────────────────────────────┐
│    TRANSACCIONES            │
├─────────────────────────────┤
│ PK: id (UUID)              │
│ FK: cuenta_id → CUENTAS    │
│ tipo VARCHAR(30)           │
│ monto DECIMAL(19,2)        │
│ descripcion VARCHAR(255)   │
│ saldo_anterior DECIMAL(19,2)
│ saldo_nuevo DECIMAL(19,2)  │
│ fecha_transaccion TIMESTAMP│
│ cuenta_origen_id VARCHAR   │
│ cuenta_destino_id VARCHAR  │
│ created_at TIMESTAMP       │
└─────────────────────────────┘

LEYENDA:
PK = Primary Key (Clave Primaria)
FK = Foreign Key (Clave Foránea)
UQ = Unique (Único)
1:N = Relación uno a muchos
```

### Explicación de Relaciones

**USUARIOS (1) ─── (N) CUENTAS**

- Un usuario puede tener múltiples cuentas
- Al eliminar usuario → Se eliminan sus cuentas (CASCADE)

**CUENTAS (1) ─── (N) TRANSACCIONES**

- Una cuenta puede tener múltiples transacciones
- Al eliminar cuenta → Se eliminan sus transacciones (CASCADE)

---

## 3️⃣ SCRIPTS SQL (COPIA EXACTA A WORD)

---

### Scripts SQL - Creación de Tablas

#### Tabla: USUARIOS

```sql
CREATE TABLE IF NOT EXISTS usuarios (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    documento VARCHAR(50) NOT NULL UNIQUE,
    tipo_documento VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Índices para optimización
CREATE UNIQUE INDEX idx_usuarios_email ON usuarios(email);
CREATE UNIQUE INDEX idx_usuarios_documento ON usuarios(documento);
```

**Descripción**: Almacena los datos de los usuarios registrados. Email y documento deben ser únicos para evitar duplicados.

---

#### Tabla: CUENTAS

```sql
CREATE TABLE IF NOT EXISTS cuentas (
    id VARCHAR(36) PRIMARY KEY,
    numero_cuenta VARCHAR(50) NOT NULL UNIQUE,
    usuario_id VARCHAR(36) NOT NULL,
    saldo DECIMAL(19, 2) NOT NULL,
    moneda VARCHAR(3) NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_cuentas_usuario 
        FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id) 
        ON DELETE CASCADE
);

-- Índice para búsqueda por número de cuenta
CREATE UNIQUE INDEX idx_cuentas_numero ON cuentas(numero_cuenta);
```

**Descripción**: Almacena las cuentas bancarias. Cada cuenta está vinculada a un usuario. El número de cuenta es un identificador amigable (10 dígitos) distinto al UUID interno.

---

#### Tabla: TRANSACCIONES

```sql
CREATE TABLE IF NOT EXISTS transacciones (
    id VARCHAR(36) PRIMARY KEY,
    cuenta_id VARCHAR(36) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    monto DECIMAL(19, 2) NOT NULL,
    descripcion VARCHAR(255),
    saldo_anterior DECIMAL(19, 2) NOT NULL,
    saldo_nuevo DECIMAL(19, 2) NOT NULL,
    fecha_transaccion TIMESTAMP NOT NULL,
    cuenta_origen_id VARCHAR(36),
    cuenta_destino_id VARCHAR(36),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_transacciones_cuenta 
        FOREIGN KEY (cuenta_id) 
        REFERENCES cuentas(id) 
        ON DELETE CASCADE
);
```

**Descripción**: Registro de todas las operaciones realizadas. Campos saldo_anterior y saldo_nuevo permiten auditoría completa. Para transferencias, se crean 2 registros automáticamente.

---

### Scripts SQL - Consultas Principales

#### Consulta 1: Obtener usuario con todas sus cuentas

```sql
SELECT 
    u.id,
    u.nombre,
    u.apellido,
    u.email,
    u.documento,
    COUNT(c.id) as total_cuentas,
    SUM(CASE WHEN c.activa = 1 THEN 1 ELSE 0 END) as cuentas_activas
FROM usuarios u
LEFT JOIN cuentas c ON u.id = c.usuario_id
WHERE u.activo = 1
GROUP BY u.id, u.nombre, u.apellido, u.email, u.documento
ORDER BY u.nombre, u.apellido;
```

---

#### Consulta 2: Saldo total de un usuario

```sql
SELECT 
    u.nombre,
    u.apellido,
    c.numero_cuenta,
    c.moneda,
    c.saldo,
    c.saldo * 1.0 as saldo_float
FROM usuarios u
JOIN cuentas c ON u.id = c.usuario_id
WHERE u.email = 'juan@example.com'
    AND c.activa = 1
ORDER BY c.created_at;
```

---

#### Consulta 3: Historial de transacciones de una cuenta

```sql
SELECT 
    t.id,
    t.tipo,
    t.monto,
    t.descripcion,
    t.saldo_anterior,
    t.saldo_nuevo,
    t.fecha_transaccion,
    t.created_at
FROM transacciones t
WHERE t.cuenta_id = (
    SELECT id FROM cuentas 
    WHERE numero_cuenta = '1234567890'
)
ORDER BY t.fecha_transaccion DESC
LIMIT 10;
```

---

#### Consulta 4: Últimas transferencias realizadas

```sql
SELECT 
    t.id,
    t.tipo,
    t.monto,
    t.descripcion,
    c_origen.numero_cuenta as cuenta_origen,
    c_destino.numero_cuenta as cuenta_destino,
    t.fecha_transaccion
FROM transacciones t
LEFT JOIN cuentas c_origen ON t.cuenta_origen_id = c_origen.id
LEFT JOIN cuentas c_destino ON t.cuenta_destino_id = c_destino.id
WHERE t.tipo IN ('TRANSFERENCIA_SALIDA', 'TRANSFERENCIA_ENTRADA')
ORDER BY t.fecha_transaccion DESC
LIMIT 20;
```

---

#### Consulta 5: Movimientos por tipo en última semana

```sql
SELECT 
    t.tipo,
    COUNT(*) as cantidad,
    SUM(t.monto) as monto_total,
    AVG(t.monto) as monto_promedio,
    MIN(t.monto) as monto_minimo,
    MAX(t.monto) as monto_maximo
FROM transacciones t
WHERE t.fecha_transaccion >= datetime('now', '-7 days')
GROUP BY t.tipo
ORDER BY cantidad DESC;
```

---

## 4️⃣ INSTRUCCIONES PARA CAPTURAS (PASO A PASO)

---

### ¿Cómo Tomar Capturas de Pantalla?

#### Captura 1: BD Creada (wallet.db) con python verify_db.py

![1767161609282](image/INSTRUCCIONES_DOCUMENTO_WORD/1767161609282.png)

![1767161644279](image/INSTRUCCIONES_DOCUMENTO_WORD/1767161644279.png)

![1767161673723](image/INSTRUCCIONES_DOCUMENTO_WORD/1767161673723.png)

#### Captura 2: Esquema de Tablas

En SQLite Browser:

1. Click en cada tabla
2. Muestra "Create SQL" → Verás el CREATE TABLE
3. Toma captura del esquema

#### Captura 3: Datos en Tablas

1. Abre tabla USUARIOS
2. Muestra registros (si existen)
3. Toma captura
4. Repite para CUENTAS
5. Repite para TRANSACCIONES

#### Captura 4: Consultas Ejecutadas

1. Copia cada script SQL del punto 3️⃣
2. Pégalo en SQLite Browser (pestaña "Execute SQL")
3. Haz click en "Execute"
4. Toma captura del resultado

---

## 5️⃣ EXPLICACIÓN DE FUNCIONAMIENTO

---

### Flujo de Operaciones Principal

#### 1. Crear Usuario

```
Usuario ingresa datos:
  - Nombre: Juan
  - Apellido: Pérez
  - Email: juan@example.com
  - Documento: 12345678
  - Tipo: CEDULA

VALIDACIONES:
  ✓ Email no exista (UNIQUE)
  ✓ Documento no exista (UNIQUE)
  ✓ Campos no vacíos

OPERACIÓN EN BD:
  INSERT INTO usuarios VALUES(
    UUID(),
    'Juan',
    'Pérez',
    'juan@example.com',
    '12345678',
    'CEDULA',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )

RESULTADO:
  ✅ Usuario creado con ID único (UUID)
```

#### 2. Crear Cuenta

```
Usuario selecciona su cuenta:
  - Moneda: USD
  - Saldo inicial: 5000

VALIDACIONES:
  ✓ Usuario existe
  ✓ Moneda válida (ISO 4217)
  ✓ Saldo >= 0

OPERACIÓN EN BD:
  INSERT INTO cuentas VALUES(
    UUID(),
    '1234567890',  -- Número generado (10 dígitos)
    [usuario_id],
    5000.00,
    'USD',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )

RESULTADO:
  ✅ Cuenta creada
  Número: 1234567890
  Saldo: $5,000.00 USD
```

#### 3. Realizar Transferencia

```
Usuario ingresa:
  - Cuenta origen: 1234567890
  - Cuenta destino: 0987654321
  - Monto: 1000

TRANSACCIÓN ACID:
  1. Buscar cuenta origen → Validar saldo >= 1000 ✓
  2. Buscar cuenta destino → Validar existe ✓
  3. BEGIN TRANSACTION
   
     a) Actualizar cuenta origen:
        UPDATE cuentas 
        SET saldo = saldo - 1000
        WHERE numero_cuenta = '1234567890'
      
     b) Insertar transacción SALIDA:
        INSERT INTO transacciones
        VALUES (UUID, [cuenta_origen_id], 'TRANSFERENCIA_SALIDA', 1000, ...)
      
     c) Actualizar cuenta destino:
        UPDATE cuentas 
        SET saldo = saldo + 1000
        WHERE numero_cuenta = '0987654321'
      
     d) Insertar transacción ENTRADA:
        INSERT INTO transacciones
        VALUES (UUID, [cuenta_destino_id], 'TRANSFERENCIA_ENTRADA', 1000, ...)
      
  4. COMMIT (si todo OK)
     O ROLLBACK (si hay error)

RESULTADO:
  ✅ Transferencia completada
  - 2 transacciones registradas automáticamente
  - Saldos actualizados en ambas cuentas
  - Auditoría completa disponible
```

#### 4. Consultar Historial

```
Usuario selecciona cuenta: 1234567890

CONSULTA:
  SELECT * FROM transacciones
  WHERE cuenta_id = [id_cuenta]
  ORDER BY fecha_transaccion DESC

RESULTADO:
  ✅ Historial mostrando:
  - Fecha y hora exacta
  - Tipo (DEPOSITO, RETIRO, TRANSFERENCIA)
  - Monto
  - Saldo anterior → Saldo nuevo
  - Descripción (si existe)
```

---

### Características de Seguridad

#### 1. Integridad Referencial

```
Si se elimina usuario:
  → Se eliminan TODAS sus cuentas (CASCADE)
  → Se eliminan TODAS sus transacciones (CASCADE)

Protección: No quedan datos huérfanos
```

#### 2. Validaciones UNIQUE

```
email       → No puede haber 2 usuarios con mismo email
documento   → No puede haber 2 usuarios con mismo documento
numero_cuenta → No puede haber 2 cuentas con mismo número
```

#### 3. Transacciones ACID

```
A = Atomicidad   → Todo o nada (no transacciones parciales)
C = Consistencia → Los datos siempre válidos
I = Aislamiento  → Transacciones concurrentes no interfieren
D = Durabilidad  → Los datos persisten en disco
```

---

## 6️⃣ CÓMO GENERAR LAS CAPTURAS FÁCILMENTE

---

### Opción A: Usar DB Browser for SQLite (RECOMENDADO)

1. **Descargar**: https://sqlitebrowser.org/
2. **Abrir BD**:

   - Archivo → Abrir Base de Datos
   - Navega a: `C:\Users\marga\Desktop\NeekWorld\boot android\wallet\wallet.db`
3. **Ver Tablas**:

   - Pestaña "Database Structure"
   - Verás las 3 tablas: usuarios, cuentas, transacciones
4. **Ver Datos**:

   - Haz clic en tabla
   - Pestaña "Browse Data"
   - Verás los registros
5. **Ejecutar SQL**:

   - Pestaña "Execute SQL"
   - Pega los scripts del punto 3️⃣
   - Click "Execute"
   - Toma captura

---

### Opción B: Usar VS Code (Si tienes extensión SQLite)

1. Abre VS Code en el proyecto
2. Instala extensión "SQLite" (cjh8467287455)
3. Click en wallet.db
4. Verás las tablas
5. Click derecho → Run Query
6. Ejecuta scripts

---

## 7️⃣ INSTRUCCIONES PARA CREAR EL DOCUMENTO WORD

---

### Paso a Paso en Microsoft Word

**1. Crear Portada**

```
PROYECTO WALLET 2.0
Billetera Digital con SQLite

Autor: [Tu Nombre]
Fecha: 31 de Diciembre de 2025
Versión: 1.0.0
```

**2. Introducción**

- Copia la sección "1️⃣ INTRODUCCIÓN" arriba

**3. Diagrama ER**

- Opción A: Copia el texto del diagrama como imagen
- Opción B: Usa herramienta online para generar diagrama:
  - Lucidchart (https://www.lucidchart.com)
  - Draw.io (https://draw.io)
  - Creately (https://creately.com)

**4. Scripts SQL**

- Copia la sección "3️⃣ SCRIPTS SQL" exactamente como está

**5. Capturas de Pantalla**

- Inserta 5-6 capturas de:
  - Estructura de tablas
  - Datos en BD
  - Resultados de consultas

**6. Explicación de Funcionamiento**

- Copia la sección "5️⃣ EXPLICACIÓN DE FUNCIONAMIENTO"

**7. Conclusión**

```
Este proyecto demuestra la implementación de un sistema
de persistencia con SQLite y Hibernate ORM siguiendo
Clean Architecture. Implementa todas las características
de una billetera digital funcional con auditoría completa.

Tecnologías utilizadas:
- Java 21 LTS
- Hibernate 6.4.4.Final
- SQLite 3.44.0.0
- Maven 3.9.6

Estado: Production Ready ✅
```

---

## ✅ CHECKLIST - LO QUE DEBE TENER TU DOCUMENTO

```
☐ Portada con información del proyecto
☐ Introducción (objetivo, alcance, tecnología)
☐ Diagrama ER de las 3 tablas
☐ Scripts SQL de CREATE TABLE (3 tablas)
☐ Scripts SQL de consultas principales (5 queries)
☐ Captura: estructura de BD (wallet.db)
☐ Captura: esquema de tablas en SQLite
☐ Captura: datos en tabla USUARIOS
☐ Captura: datos en tabla CUENTAS
☐ Captura: datos en tabla TRANSACCIONES
☐ Captura: resultado de consulta SQL
☐ Explicación: Flujo de crear usuario
☐ Explicación: Flujo de crear cuenta
☐ Explicación: Flujo de transferencia
☐ Explicación: Características de seguridad
☐ Conclusión
```

---

## 🎓 ALTERNATIVA: SI USAS CHATGPT PARA WORD

---

### Prompt para GPT (Copiar y pegar exactamente)

```
Necesito que crees un documento Word profesional para 
mi proyecto de base de datos SQLite con Java.

CONTENIDO A INCLUIR:

1. PORTADA
   - Título: WALLET 2.0 - Billetera Digital
   - Autor: [Margandona]
   - Fecha: 31 de Diciembre de 2025
   - Versión: 1.0.0

2. INTRODUCCIÓN (aquí inserta el texto de la sección 1️⃣)

3. DIAGRAMA ER (crea uno similar a este en formato texto ASCII art)
   [INSERTA EL DIAGRAMA DE LA SECCIÓN 2️⃣]

4. SCRIPTS SQL (en bloques de código formateado)
   [INSERTA LOS SCRIPTS DE LA SECCIÓN 3️⃣]

5. EXPLICACIÓN DE FUNCIONAMIENTO
   [INSERTA EL CONTENIDO DE LA SECCIÓN 5️⃣]

6. CONCLUSIÓN
   [ESCRIBE UNA CONCLUSIÓN BREVE]

Formato: Microsoft Word (.docx)
Estilo: Profesional con:
  - Títulos en azul
  - Subtítulos en negrita
  - Código en fuente Courier New
  - Márgenes 2.5cm
  - Numeración de páginas
```

---

## 📌 RESUMEN DE ARCHIVOS NECESARIOS

Lo que ya tienes disponible:

✅ PRESENTACION.md - Toda la información técnica
✅ BASE_DE_DATOS.md - Documentación de BD
✅ wallet.db - Base de datos SQLite real
✅ Código fuente completo
✅ JAR ejecutable

Lo que necesitas:

📝 Documento Word con estructura anterior
📸 5-6 capturas de pantalla
🖼️ Diagrama ER (puede ser ASCII art o imagen)

---

## 🔗 HERRAMIENTAS RECOMENDADAS

**Para BD SQLite:**

- DB Browser for SQLite (https://sqlitebrowser.org/) ⭐ MEJOR
- DBeaver (https://dbeaver.io/)
- VS Code + SQLite Extension

**Para crear Diagrama ER:**

- Draw.io (https://draw.io) - GRATIS
- Lucidchart (https://lucidchart.com)
- Creately (https://creately.com)

**Para crear Word:**

- Microsoft Word (Online o Desktop)
- Google Docs (exporta a Word)
- LibreOffice Writer

---

## 💡 TIPS IMPORTANTES

1. **Estructura**: Sigue el orden exacto del checklist
2. **Capturas**: Haz capturas claras sin ruido visual
3. **Código SQL**: Usa monospace font (Courier New)
4. **Márgenes**: 2.5cm en todos lados (estándar)
5. **Interlineado**: 1.5 o doble (según instrucciones)
6. **Numeración**: Añade números de página en pie

---

**¡Listo! Con esta información tienes TODO para crear tu documento Word profesional!**

Si necesitas ayuda adicional o tienes preguntas, avísame.
