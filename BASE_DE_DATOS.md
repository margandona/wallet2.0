# Documentación: Base de Datos y Arquitectura de WALLET

## Índice
1. [Introducción](#introducción)
2. [Arquitectura de Base de Datos](#arquitectura-de-base-de-datos)
3. [Diseño de Tablas](#diseño-de-tablas)
4. [Relaciones y Restricciones](#relaciones-y-restricciones)
5. [Implementación con JPA/Hibernate](#implementación-con-jpahibernate)
6. [Inicialización y Migraciones](#inicialización-y-migraciones)
7. [Cómo Ejecutar la Aplicación](#cómo-ejecutar-la-aplicación)

---

## Introducción

WALLET es una aplicación de billetera digital desarrollada con **Java 21 LTS** que implementa un sistema de gestión de usuarios, cuentas y transacciones. La persistencia de datos se realiza mediante **SQLite** con **Hibernate ORM** como framework de mapeo objeto-relacional.

### Tecnología Utilizada
- **Base de Datos**: SQLite 3.44.0.0
- **ORM**: Hibernate 6.4.4.Final
- **Jakarta Persistence**: 3.1
- **Lenguaje**: Java 21 LTS
- **Build Tool**: Maven 3.9.6
- **Patrón de Arquitectura**: Clean Architecture (Capas: Domain, Application, Infrastructure, Presentation)

---

## Arquitectura de Base de Datos

### Tipo de Base de Datos
**SQLite** fue elegido por:
- ✅ Ser embebido (sin servidor externo requerido)
- ✅ Ideal para aplicaciones de escritorio
- ✅ Bajo overhead, alto rendimiento
- ✅ Fácil portabilidad (un único archivo wallet.db)
- ✅ Soporte completo de transacciones ACID

### Ubicación del Archivo
```
wallet.db
```
Se crea automáticamente en el directorio raíz de ejecución de la aplicación.

### Configuración de Conexión
Archivo: `persistence.xml`
```xml
<property name="hibernate.connection.url" value="jdbc:sqlite:wallet.db" />
<property name="hibernate.dialect" value="org.hibernate.community.dialect.SQLiteDialect" />
<property name="hibernate.hbm2ddl.auto" value="validate" />
```

---

## Diseño de Tablas

### 1. Tabla: USUARIOS

**Propósito**: Almacenar información de usuarios registrados en el sistema.

**Estructura**:
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

CREATE UNIQUE INDEX idx_usuarios_email ON usuarios(email);
CREATE UNIQUE INDEX idx_usuarios_documento ON usuarios(documento);
```

**Campos**:
| Campo | Tipo | Restricción | Descripción |
|-------|------|-------------|-------------|
| id | VARCHAR(36) | PK | UUID único del usuario |
| nombre | VARCHAR(100) | NOT NULL | Nombre del usuario |
| apellido | VARCHAR(100) | NOT NULL | Apellido del usuario |
| email | VARCHAR(100) | NOT NULL, UNIQUE | Correo electrónico único |
| documento | VARCHAR(50) | NOT NULL, UNIQUE | Número de documento único |
| tipo_documento | VARCHAR(20) | NOT NULL | Tipo (CEDULA, PASAPORTE, RUT) |
| activo | BOOLEAN | NOT NULL, DEFAULT 1 | Estado del usuario |
| created_at | TIMESTAMP | NOT NULL | Fecha de creación |
| updated_at | TIMESTAMP | NOT NULL | Última actualización |

**Entidad JPA**: `UsuarioJPAEntity` (en `infrastructure/persistence`)

---

### 2. Tabla: CUENTAS

**Propósito**: Almacenar las cuentas bancarias de cada usuario.

**Estructura**:
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
    CONSTRAINT fk_cuentas_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_cuentas_numero ON cuentas(numero_cuenta);
```

**Campos**:
| Campo | Tipo | Restricción | Descripción |
|-------|------|-------------|-------------|
| id | VARCHAR(36) | PK | UUID único de cuenta |
| numero_cuenta | VARCHAR(50) | NOT NULL, UNIQUE | Número de cuenta (10 dígitos) |
| usuario_id | VARCHAR(36) | FK, NOT NULL | ID del usuario propietario |
| saldo | DECIMAL(19, 2) | NOT NULL | Saldo en la moneda especificada |
| moneda | VARCHAR(3) | NOT NULL | Código ISO 4217 (USD, CLP, PEN, etc.) |
| activa | BOOLEAN | NOT NULL, DEFAULT 1 | Estado de cuenta |
| created_at | TIMESTAMP | NOT NULL | Fecha de creación |
| updated_at | TIMESTAMP | NOT NULL | Última actualización |

**Entidad JPA**: `CuentaJPAEntity` (en `infrastructure/persistence`)

**Relación**: Muchas cuentas por usuario (1:N)

---

### 3. Tabla: TRANSACCIONES

**Propósito**: Registro de todas las operaciones (depósitos, retiros, transferencias).

**Estructura**:
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
    CONSTRAINT fk_transacciones_cuenta FOREIGN KEY (cuenta_id) 
        REFERENCES cuentas(id) ON DELETE CASCADE
);
```

**Campos**:
| Campo | Tipo | Restricción | Descripción |
|-------|------|-------------|-------------|
| id | VARCHAR(36) | PK | UUID único de transacción |
| cuenta_id | VARCHAR(36) | FK, NOT NULL | Cuenta afectada |
| tipo | VARCHAR(30) | NOT NULL | DEPOSITO, RETIRO, TRANSFERENCIA_ENTRADA, TRANSFERENCIA_SALIDA |
| monto | DECIMAL(19, 2) | NOT NULL | Cantidad en la transacción |
| descripcion | VARCHAR(255) | NULL | Descripción de la operación |
| saldo_anterior | DECIMAL(19, 2) | NOT NULL | Saldo antes de transacción |
| saldo_nuevo | DECIMAL(19, 2) | NOT NULL | Saldo después de transacción |
| fecha_transaccion | TIMESTAMP | NOT NULL | Cuándo ocurrió la transacción |
| cuenta_origen_id | VARCHAR(36) | NULL | Cuenta origen (en transferencias) |
| cuenta_destino_id | VARCHAR(36) | NULL | Cuenta destino (en transferencias) |
| created_at | TIMESTAMP | NOT NULL | Fecha de registro |

**Entidad JPA**: `TransaccionJPAEntity` (en `infrastructure/persistence`)

**Relación**: Muchas transacciones por cuenta (1:N)

---

## Relaciones y Restricciones

### Diagrama de Relaciones ER

```
┌─────────────────────────────────────────────────────────┐
│                    USUARIOS                             │
│                  (id: PK)                               │
│  ┌─────────────────────────────────────────────────────┤
│  │ id (PK)       │ nombre    │ email   │ documento    │
│  │ apellido      │ tipo_doc  │ activo  │ timestamps   │
│  └─────────────────────────────────────────────────────┤
│                        │
│                    1:N │ (usuario_id)
│                        │
│                        ▼
│  ┌─────────────────────────────────────────────────────┐
│  │                   CUENTAS                           │
│  │                (id: PK)                             │
│  ├─────────────────────────────────────────────────────┤
│  │ id (PK)       │ numero_cuenta  │ usuario_id (FK)  │
│  │ saldo         │ moneda         │ activa           │
│  │ timestamps    │                │                  │
│  └─────────────────────────────────────────────────────┤
│                        │
│                    1:N │ (cuenta_id)
│                        │
│                        ▼
│  ┌─────────────────────────────────────────────────────┐
│  │               TRANSACCIONES                         │
│  │               (id: PK)                              │
│  ├─────────────────────────────────────────────────────┤
│  │ id (PK)       │ tipo           │ monto            │
│  │ cuenta_id (FK)│ saldo_anterior │ saldo_nuevo      │
│  │ cuenta_origen │ cuenta_destino │ timestamps       │
│  └─────────────────────────────────────────────────────┘

```

### Restricciones Implementadas

#### 1. Claves Foráneas (Foreign Keys)
- `CUENTAS.usuario_id` → `USUARIOS.id` (ON DELETE CASCADE)
- `TRANSACCIONES.cuenta_id` → `CUENTAS.id` (ON DELETE CASCADE)

**Implicación**: Si se elimina un usuario, se elimina automáticamente todas sus cuentas y transacciones.

#### 2. Unicidad
- `USUARIOS.email` (UNIQUE)
- `USUARIOS.documento` (UNIQUE)
- `CUENTAS.numero_cuenta` (UNIQUE)

#### 3. NOT NULL
- Campos esenciales: nombre, apellido, email, documento, tipo_documento, número de cuenta, saldo, moneda, monto, saldo_anterior, saldo_nuevo

---

## Implementación con JPA/Hibernate

### Capas de Persistencia

#### 1. **Capa Domain (Dominio)**
Clases de entidad del negocio:
- `Usuario.java` - Objeto de dominio
- `Cuenta.java` - Objeto de dominio
- `Transaccion.java` - Objeto de dominio

Estos son los objetos puros del negocio, sin dependencias de BD.

#### 2. **Capa Infrastructure (Infraestructura)**

**Entidades JPA** (Mapeo a BD):
```java
// src/main/java/com/wallet/infrastructure/persistence/

@Entity
@Table(name = "usuarios")
public class UsuarioJPAEntity {
    @Id
    private String id;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    // ... más campos mapeados
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<CuentaJPAEntity> cuentas;
}
```

**Repositorios JPA**:
```java
public interface UsuarioJPARepository extends JpaRepository<UsuarioJPAEntity, String> {
    Optional<UsuarioJPAEntity> findByEmail(String email);
    Optional<UsuarioJPAEntity> findByDocumento(String documento);
    List<UsuarioJPAEntity> findAllByActivoTrue();
}

public interface CuentaJPARepository extends JpaRepository<CuentaJPAEntity, String> {
    Optional<CuentaJPAEntity> findByNumeroCuenta(String numeroCuenta);
    List<CuentaJPAEntity> findByUsuarioId(String usuarioId);
}

public interface TransaccionJPARepository extends JpaRepository<TransaccionJPAEntity, String> {
    List<TransaccionJPAEntity> findByCuentaIdOrderByFechaTransaccionDesc(String cuentaId);
}
```

#### 3. **Mapeo Domain ↔ JPA**

```java
// Convertir de Domain Entity a JPA Entity
public static UsuarioJPAEntity domainToJPA(Usuario usuario) {
    UsuarioJPAEntity entity = new UsuarioJPAEntity();
    entity.setId(usuario.getId());
    entity.setNombre(usuario.getNombre());
    entity.setEmail(usuario.getEmail());
    // ...
    return entity;
}

// Convertir de JPA Entity a Domain Entity
public static Usuario jpaTooDomain(UsuarioJPAEntity entity) {
    return new Usuario(
        entity.getId(),
        entity.getNombre(),
        entity.getEmail(),
        // ...
    );
}
```

### Flujo de Operación: Guardar Usuario

```
1. Controller → UsuarioService.registrar(usuarioDTO)
   ↓
2. UsuarioService → ValidarUsuarioUseCase.ejecutar(usuario)
   ↓
3. ValidarUsuarioUseCase → Valida reglas de negocio
   ↓
4. UsuarioService → RepositoryFactory.getUsuarioRepository().guardar(usuario)
   ↓
5. RepositoryFactory → UsuarioJPARepository.guardar(usuario)
   ↓
6. UsuarioJPARepository:
   - Inicia transacción (EntityManager.getTransaction().begin())
   - Convierte Domain → JPA (Usuario → UsuarioJPAEntity)
   - Ejecuta: entityManager.persist(usuarioJPA)
   - Ejecuta: entityManager.flush() (fuerza INSERT)
   - Commit de transacción
   ↓
7. SQLite: INSERT INTO usuarios (id, nombre, email, ...)
   ↓
8. ✅ Usuario persistido en wallet.db
```

---

## Inicialización y Migraciones

### Proceso de Inicialización

1. **Detección de BD Existente**
   - Al iniciar, la aplicación verifica si `wallet.db` existe
   - Si NO existe, se crea automáticamente

2. **Creación de Tablas**
   - Se ejecuta el script `schema.sql` (ubicado en `src/main/resources/`)
   - Las tablas se crean si no existen (CREATE TABLE IF NOT EXISTS)
   - Se crean índices para optimizar búsquedas

3. **Verificación de Integridad**
   - Se validan que todas las tablas requeridas existan
   - Se verifican las foreign keys
   - Se confirma que el schema es válido

### Archivo schema.sql

```sql
-- schema.sql (src/main/resources/)
-- Script de inicialización automática de BD

CREATE TABLE IF NOT EXISTS usuarios (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    -- ... todos los campos ...
);

CREATE TABLE IF NOT EXISTS cuentas (
    id VARCHAR(36) PRIMARY KEY,
    -- ... todos los campos ...
    CONSTRAINT fk_cuentas_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transacciones (
    -- ... todos los campos ...
    CONSTRAINT fk_transacciones_cuenta FOREIGN KEY (cuenta_id) 
        REFERENCES cuentas(id) ON DELETE CASCADE
);
```

### Clase: DatabaseInitializer

```java
public class DatabaseInitializer {
    public static void initialize(EntityManager entityManager) throws Exception {
        // 1. Detectar si BD existe
        detectarBDExistente();
        
        // 2. Ejecutar schema.sql
        ejecutarSchemaSQL(entityManager);
        
        // 3. Crear tablas manualmente como fallback
        crearTablasManualmente(entityManager);
        
        // 4. Verificar integridad
        verificarIntegridad(entityManager);
    }
}
```

---

## Cómo Ejecutar la Aplicación

### Requisitos Previos
- Java 21 JDK instalado
- Maven 3.9.6 o superior
- Sistema Operativo: Windows, macOS o Linux

### Paso 1: Compilar la Aplicación

```bash
cd "C:\Users\marga\Desktop\NeekWorld\boot android\wallet"
mvn clean package -DskipTests -q
```

**Qué hace**:
- Limpia build anterior (`clean`)
- Compila código fuente (`package`)
- Salta ejecución de tests (`-DskipTests`)
- Modo silencioso (`-q`)
- Genera `wallet-app-1.0.0-jar-with-dependencies.jar`

**Tiempo**: ~30-60 segundos

### Paso 2: Ejecutar la Aplicación

```bash
java -jar "target/wallet-app-1.0.0-jar-with-dependencies.jar"
```

**Qué sucede**:
1. Se inicia la JVM (Java Virtual Machine)
2. Se carga la aplicación
3. Se inicializa la BD (o se conecta a existente)
4. Aparece el menú principal

### Interfaz Principal

```
════════════════════════════════════════
? WALLET - SISTEMA DE BILLETERA DIGITAL
════════════════════════════════════════

?? Inicializando aplicación...
? Inicializando base de datos...
? Inicialización completada. Iniciando interfaz...

════════════════════════════════════════
  MENU PRINCIPAL
════════════════════════════════════════

1. Gestion de Usuarios
2. Gestion de Cuentas
3. Transacciones
4. Consultas
5. Conversor de Divisas
0. Salir

Seleccione una opcion: _
```

### Operaciones Principales

#### Crear Usuario
```
Seleccione una opcion: 1
Seleccione una opcion: 1 (Registrar Nuevo Usuario)
Ingrese nombre: Juan
Ingrese apellido: Pérez
Ingrese email: juan@example.com
Ingrese documento: 123456789
...
```

#### Crear Cuenta
```
Seleccione una opcion: 2 (Gestion de Cuentas)
Seleccione una opcion: 1 (Crear Nueva Cuenta)
Seleccione usuario: 1
Ingrese moneda: USD
Saldo inicial: 1000
...
```

#### Realizar Transferencia
```
Seleccione una opcion: 3 (Transacciones)
Seleccione una opcion: 1 (Transferir Dinero)
Numero de cuenta origen: 1234567890
Numero de cuenta destino: 0987654321
Monto: 500
...
```

#### Conversor de Divisas
```
Seleccione una opcion: 5 (Conversor de Divisas)
Seleccione una opcion: 1 (Convertir divisa)
Cantidad: 500
Moneda origen: USD
Moneda destino: CLP
...
RESULTADO: 500 USD = 457,387.05 CLP
```

### Salir de la Aplicación
```
Seleccione una opcion: 0
```

---

## Características de la Base de Datos Implementadas

### ✅ ACID Compliance
- **Atomicidad**: Las transacciones completas se registran o se revierten
- **Consistencia**: Las restricciones FK se validan automáticamente
- **Aislamiento**: Las transacciones se aíslan entre sí
- **Durabilidad**: Los datos persisten en disco (wallet.db)

### ✅ Transacciones
- Cada operación (transferencia, depósito) es una transacción ACID
- Si falla, se revierte automáticamente
- Se registran dos transacciones en transferencias: SALIDA + ENTRADA

### ✅ Historial Completo
- Todas las operaciones se registran en `TRANSACCIONES`
- Se guarda saldo anterior y saldo nuevo
- Se registra fecha/hora exacta
- Se identifica si fue depósito, retiro o transferencia

### ✅ Índices de Optimización
```sql
CREATE UNIQUE INDEX idx_usuarios_email ON usuarios(email);
CREATE UNIQUE INDEX idx_usuarios_documento ON usuarios(documento);
CREATE UNIQUE INDEX idx_cuentas_numero ON cuentas(numero_cuenta);
```

### ✅ Validaciones en BD
- Unicidad de email (no duplicados)
- Unicidad de documento (no duplicados)
- Unicidad de número de cuenta (no duplicados)
- Saldos siempre positivos o cero
- Campos requeridos no nulos

---

## Conclusión

La base de datos de WALLET fue diseñada siguiendo principios de:
- ✅ **Normalización**: Elimina redundancia (3NF)
- ✅ **Integridad Referencial**: Mantiene consistencia con FKs
- ✅ **Escalabilidad**: Índices optimizan consultas
- ✅ **Seguridad**: Transacciones ACID protegen datos
- ✅ **Mantenibilidad**: Esquema limpio y documentado

Esta arquitectura garantiza que la billetera digital mantenga la integridad de datos financieros de manera confiable.
