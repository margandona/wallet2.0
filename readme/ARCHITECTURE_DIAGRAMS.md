# 🏗️ Diagramas de Arquitectura - Sistema Wallet

Diagramas visuales de la arquitectura del Sistema de Billetera Digital.

---

## 📐 1. Arquitectura de 5 Capas

```
┌─────────────────────────────────────────────────────────┐
│                  PRESENTATION LAYER                     │
│           (Console UI, User Interaction)                │
│                                                         │
│  - ConsoleMenuController                              │
│  - Menu management                                     │
│  - User input/output                                   │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ (Controllers)
                     ▼
┌─────────────────────────────────────────────────────────┐
│                 APPLICATION LAYER                       │
│         (Use Cases, Business Logic)                     │
│                                                         │
│  - Use Cases (10+ implementations)                     │
│  - DTOs & Mappers                                      │
│  - ValidatorUtil (10 methods)                          │
│  - Services                                             │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ (Use Case interfaces)
                     ▼
┌─────────────────────────────────────────────────────────┐
│                   DOMAIN LAYER                          │
│         (Entities, Value Objects, Rules)               │
│                                                         │
│  - Entities: Usuario, Cuenta, Transaccion             │
│  - Value Objects: Email, Documento, Moneda            │
│  - 8 Exception Classes (Fase 7)                        │
│  - Repository Interfaces (abstract contracts)          │
│  - Domain Services                                      │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ (Repository interfaces)
                     ▼
┌─────────────────────────────────────────────────────────┐
│              INFRASTRUCTURE LAYER                       │
│      (Repositories, Database Access)                    │
│                                                         │
│  - UsuarioJPARepository (9 methods)                    │
│  - CuentaJPARepository (8 methods)                     │
│  - TransaccionJPARepository (7 methods)               │
│  - Exception conversion & handling                     │
│  - OperationLogger integration                         │
│  - HikariCP Connection Pool                           │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ (JPA/Hibernate)
                     ▼
┌─────────────────────────────────────────────────────────┐
│              PERSISTENCE LAYER                          │
│         (Database, ORM Mapping)                         │
│                                                         │
│  - Hibernate 6.4.4.Final ORM                          │
│  - SQLite 3.44.0.0 Database                           │
│  - USUARIOS, CUENTAS, TRANSACCIONES tables            │
│  - Constraints, Indices, Transactions                  │
└─────────────────────────────────────────────────────────┘
```

---

## 🔄 2. Flujo de Validación (4 Capas)

```
USER INPUT
    ↓
┌──────────────────────────────────────┐
│ LAYER 1: PRESENTATION                │
│ - Format check (not empty, length)   │
│ - Basic type validation              │
│ - IOException handling               │
└──────────────────────────────────────┘
    ↓ (valid input)
┌──────────────────────────────────────┐
│ LAYER 2: APPLICATION (ValidatorUtil) │
│ - Business logic validation          │
│ - ValidatorUtil methods              │
│ - InvalidEmailFormatException        │
│ - InvalidDocumentoFormatException    │
│ - InvalidSaldoException              │
│ - InvalidMontoException              │
└──────────────────────────────────────┘
    ↓ (valid data)
┌──────────────────────────────────────┐
│ LAYER 3: DOMAIN (Value Objects)      │
│ - Entity constructor validation      │
│ - Email VO validation (regex)        │
│ - Documento VO validation            │
│ - Domain exceptions                  │
└──────────────────────────────────────┘
    ↓ (valid entity)
┌──────────────────────────────────────┐
│ LAYER 4: INFRASTRUCTURE (Repository) │
│ - Duplicate checking (DB query)      │
│ - DuplicateEmailException            │
│ - DuplicateDocumentoException        │
│ - DuplicateCuentaException           │
│ - Database constraints               │
│ - OperationLogger.logOperation()     │
└──────────────────────────────────────┘
    ↓ (valid & persisted)
SUCCESS
```

---

## 🚨 3. Jerarquía de Excepciones (Fase 7)

```
                    Throwable
                        │
                        ▼
                  Exception
                        │
         ┌──────────────┴──────────────┐
         ▼                             ▼
    Checked Ex.          RuntimeException
                                │
                                ▼
                      RepositoryException
                        │ (Base exception)
                        │
         ┌──────┬─────┬──┬──┬─────────┬──────┬──────────┐
         ▼      ▼     ▼  ▼  ▼         ▼      ▼          ▼
    Duplicate Duplicate Duplicate Invalid Invalid Invalid Invalid
    EmailEx   DocumentoEx CuentaEx  EmailEx DocumentoEx SaldoEx MontoEx
    
    ├─ User email          ├─ User doc      ├─ Account #
    │  already exists      │  already exists │  already exists
    │                      │                │
    └─ Thrown by:          └─ Thrown by:    └─ Thrown by:
       UsuarioRepository      UsuarioRepository CuentaRepository
```

### Conversión de Excepciones

```
JPA/Hibernate Exceptions          Domain Exceptions
        │                                 │
        ├─ EntityExistsException ───────→ DuplicateEmailException
        │                                 DuplicateDocumentoException
        │                                 DuplicateCuentaException
        │
        ├─ ConstraintViolationException ─→ InvalidSaldoException
        │                                 InvalidMontoException
        │
        └─ PersistenceException ────────→ RepositoryException
```

---

## 🗄️ 4. Estructura de Base de Datos

```
wallet.db (SQLite)
│
├── USUARIOS
│   ├─ id (UUID, PRIMARY KEY)
│   ├─ nombre (VARCHAR 50, NOT NULL)
│   ├─ apellido (VARCHAR 50, NOT NULL)
│   ├─ email (VARCHAR 100, UNIQUE, NOT NULL)
│   ├─ tipo_documento (VARCHAR 20, NOT NULL)
│   ├─ numero_documento (VARCHAR 20, UNIQUE, NOT NULL)
│   ├─ estado (BOOLEAN, DEFAULT true)
│   ├─ fecha_creacion (TIMESTAMP)
│   └─ INDEX: idx_email, idx_documento
│
├── CUENTAS
│   ├─ id (UUID, PRIMARY KEY)
│   ├─ usuario_id (UUID, FOREIGN KEY → USUARIOS, CASCADE)
│   ├─ numero_cuenta (VARCHAR 20, UNIQUE, NOT NULL)
│   ├─ saldo (DECIMAL 19.2, CHECK >= 0)
│   ├─ moneda (VARCHAR 3, DEFAULT 'PEN')
│   ├─ estado (BOOLEAN, DEFAULT true)
│   ├─ fecha_creacion (TIMESTAMP)
│   └─ INDEX: idx_usuario_id, idx_numero_cuenta
│
└── TRANSACCIONES
    ├─ id (UUID, PRIMARY KEY)
    ├─ cuenta_id (UUID, FOREIGN KEY → CUENTAS, CASCADE)
    ├─ tipo (VARCHAR 20, NOT NULL)  -- DEPOSITO, RETIRO, TRANSFERENCIA
    ├─ monto (DECIMAL 19.2, CHECK > 0)
    ├─ saldo_anterior (DECIMAL 19.2)
    ├─ saldo_nuevo (DECIMAL 19.2)
    ├─ descripcion (VARCHAR 255)
    ├─ fecha (TIMESTAMP)
    └─ INDEX: idx_cuenta_id, idx_tipo
```

---

## 📦 5. Estructura de Repositorios (24 Métodos)

```
IUsuarioRepository (Interface)
├── guardar() ···→ UsuarioJPARepository ·· Implementación
├── buscarPorId()
├── buscarPorEmail()
├── buscarPorDocumento()
├── obtenerTodos()
├── obtenerActivos()
├── eliminar()
├── existePorEmail()
└── existePorDocumento()
   (9 métodos)

ICuentaRepository (Interface)
├── guardar() ···→ CuentaJPARepository ·· Implementación
├── buscarPorId()
├── buscarPorNumeroCuenta()
├── buscarPorUsuarioId()
├── obtenerActivasPorUsuario()
├── obtenerTodas()
├── eliminar()
└── existeNumeroCuenta()
   (8 métodos)

ITransaccionRepository (Interface)
├── guardar() ···→ TransaccionJPARepository · Implementación
├── buscarPorId()
├── buscarPorCuentaId()
├── buscarPorCuentaIdYTipo()
├── obtenerPorCuentaYFechas()
├── obtenerTodas()
└── obtenerUltimasPorCuenta()
   (7 métodos)
```

---

## 🔐 6. Patrón de Exception Handling en Repositorios

```
Método: guardar(Entity entity)
│
├─ TRY BLOCK
│  ├─ Create EntityManager
│  ├─ Begin Transaction
│  ├─ PRE-VALIDATION
│  │  ├─ existePorEmail() → DuplicateEmailException
│  │  └─ existePorDocumento() → DuplicateDocumentoException
│  ├─ em.persist(entity)
│  ├─ Commit Transaction
│  ├─ logger.logOperation("CREATE", entity)
│  └─ Return entity
│
├─ CATCH DomainException
│  ├─ Rollback if active
│  ├─ logger.logError()
│  └─ Rethrow
│
├─ CATCH PersistenceException
│  ├─ Rollback if active
│  ├─ logger.logError()
│  └─ Throw new RepositoryException()
│
└─ FINALLY BLOCK
   └─ em.close()
```

---

## 📊 7. Flujo de Operación Completa (Ejemplo: Crear Usuario)

```
┌──────────────────────────────────────────────────────────────┐
│ USER: Ingresa datos del usuario                              │
└──────────────────────────────────┬───────────────────────────┘
                                   │
                ┌──────────────────▼──────────────────┐
                │ PRESENTATION LAYER                   │
                │ ConsoleMenuController.crearUsuario() │
                │ - Solicita nombre, apellido, email   │
                │ - Valida no vacío                    │
                └──────────────────┬──────────────────┘
                                   │
                ┌──────────────────▼──────────────────┐
                │ APPLICATION LAYER                    │
                │ BuscarUsuarioUseCase.execute()       │
                │ - ValidatorUtil.validarEmail()       │
                │ - Crea Usuario entity                │
                └──────────────────┬──────────────────┘
                                   │
                ┌──────────────────▼──────────────────┐
                │ DOMAIN LAYER                         │
                │ Usuario constructor                  │
                │ - new Email(email)                   │
                │ - Email VO valida regex              │
                │ - new Documento(tipo, numero)        │
                └──────────────────┬──────────────────┘
                                   │
                ┌──────────────────▼──────────────────┐
                │ INFRASTRUCTURE LAYER                 │
                │ UsuarioJPARepository.guardar()       │
                │ ┌─ Verifica existePorEmail()         │
                │ │  → DuplicateEmailException        │
                │ ├─ Verifica existePorDocumento()     │
                │ │  → DuplicateDocumentoException    │
                │ ├─ em.persist()                      │
                │ ├─ tx.commit()                       │
                │ ├─ logger.logOperation()             │
                │ └─ Return Usuario                    │
                └──────────────────┬──────────────────┘
                                   │
                ┌──────────────────▼──────────────────┐
                │ PERSISTENCE LAYER                    │
                │ SQLite Database                      │
                │ ┌─ INSERT into USUARIOS table        │
                │ ├─ Check constraints                 │
                │ └─ Commit transaction                │
                └──────────────────┬──────────────────┘
                                   │
                ┌──────────────────▼──────────────────┐
                │ wallet_operations.log                │
                │ [2025-01-15 14:23:45] CREATE:       │
                │ Usuario (ID: usr-xxxx...)            │
                └──────────────────┬──────────────────┘
                                   │
                        ┌──────────▼────────────┐
                        │ SUCCESS ✓             │
                        │ Usuario created       │
                        └───────────────────────┘
```

---

## 🧪 8. Flujo de Testing

```
Test Runner (JUnit 5)
    │
    ├─ Unit Tests (32 tests)
    │  ├─ UsuarioJPARepositoryTest (11)
    │  │  ├─ testGuardarUsuario()
    │  │  ├─ testBuscarPorId()
    │  │  ├─ testDuplicateEmail()
    │  │  └─ ... (8 more)
    │  │
    │  ├─ CuentaJPARepositoryTest (11)
    │  │  ├─ testGuardarCuenta()
    │  │  ├─ testValidarSaldo()
    │  │  └─ ... (9 more)
    │  │
    │  ├─ TransaccionJPARepositoryTest (10)
    │  │  ├─ testGuardarTransaccion()
    │  │  ├─ testValidarMonto()
    │  │  └─ ... (8 more)
    │  │
    │  └─ Use Case Tests (5)
    │     └─ BuscarUsuarioUseCaseTest
    │
    ├─ Integration Tests
    │  └─ FlujoComipletoIntegrationTest (5)
    │     ├─ testCrearUsuario()
    │     ├─ testCrearCuenta()
    │     ├─ testRealizarDeposito()
    │     ├─ testRealizarRetiro()
    │     └─ testRealizarTransferencia()
    │
    └─ Total: 42 tests
       └─ Coverage: 100% of repository methods
```

---

## 📝 9. Validadores Centralizados (ValidatorUtil)

```
ValidatorUtil
│
├── validarEmail(String email)
│   └─ Regex: RFC 5322 format
│      └─ Throws: InvalidEmailFormatException
│
├── validarDocumento(String doc, TipoDocumento tipo)
│   ├─ CEDULA: 7-10 dígitos
│   ├─ PASAPORTE: 6-9 chars
│   ├─ LICENCIA: 7-12 dígitos
│   └─ Throws: InvalidDocumentoFormatException
│
├── validarSaldo(BigDecimal saldo)
│   └─ Check: >= 0
│      └─ Throws: InvalidSaldoException
│
├── validarMonto(BigDecimal monto)
│   └─ Check: > 0
│      └─ Throws: InvalidMontoException
│
├── validarCuenta(Cuenta cuenta)
│   └─ Check: exists and active
│
├── validarTransferencia(Cuenta origen, Cuenta destino, BigDecimal monto)
│   ├─ Check: same currency
│   ├─ Check: sufficient balance
│   └─ Check: not same account
│
└─ Plus 4 additional validation methods
```

---

## 🔍 10. Logging Operacional

```
wallet_operations.log
│
├─ [2025-01-15 14:23:45] CREATE: Usuario (ID: usr-xxxx-xxxx)
├─ [2025-01-15 14:25:30] READ: Usuario (ID: usr-xxxx-xxxx)
├─ [2025-01-15 14:30:45] CREATE: Cuenta (ID: cta-xxxx-xxxx)
├─ [2025-01-15 14:35:20] UPDATE: Transaccion (ID: trx-xxxx-xxxx)
│
└─ Statistics:
   ├─ Total operations: 1,234
   ├─ By type: CREATE(400) READ(600) UPDATE(150) DELETE(84)
   └─ By entity: Usuario(200) Cuenta(300) Transaccion(734)

wallet_errors.log
│
├─ [2025-01-15 14:40:15] ERROR: DuplicateEmailException
│   └─ Message: Email ya existe: juan@example.com
│
├─ [2025-01-15 14:45:30] ERROR: InvalidSaldoException
│   └─ Message: Saldo no puede ser negativo
│
└─ [2025-01-15 14:50:00] ERROR: RepositoryException
   └─ Message: Error al conectar base de datos
```

---

## 🚀 11. Flujo de Deployment

```
Development Environment
    │
    ├─ mvn clean install
    │  └─ Compile + Test
    │
    ├─ mvn exec:java
    │  └─ Run locally
    │
    └─ All tests pass ✓
         │
         ▼
Package Environment
    │
    ├─ mvn clean package
    │  └─ Create JAR
    │
    ├─ target/wallet-1.0.0.jar
    │  ├─ Includes all dependencies
    │  └─ wallet.db (embedded)
    │
    └─ Ready for distribution
         │
         ▼
Production Environment
    │
    ├─ java -jar wallet-1.0.0.jar
    │  └─ Start application
    │
    ├─ wallet.db
    │  └─ Persisted data
    │
    └─ wallet_operations.log
       └─ Operation tracking
```

---

## 📋 12. Matriz de Métodos y Excepciones

```
Method                  | Exceptions Thrown              | Logged?
─────────────────────────────────────────────────────────────
Usuario.guardar()       | DuplicateEmailException        | ✓
                        | DuplicateDocumentoException    |
                        | InvalidEmailFormatException    |
                        | RepositoryException            |
─────────────────────────────────────────────────────────────
Usuario.buscarPorId()   | RepositoryException            | ✓
─────────────────────────────────────────────────────────────
Usuario.eliminar()      | RepositoryException            | ✓
─────────────────────────────────────────────────────────────
Cuenta.guardar()        | InvalidSaldoException          | ✓
                        | DuplicateCuentaException       |
                        | RepositoryException            |
─────────────────────────────────────────────────────────────
Transaccion.guardar()   | InvalidMontoException          | ✓
                        | RepositoryException            |
─────────────────────────────────────────────────────────────
... (21 more methods)
```

---

**Última actualización**: Enero 2025
**Versión**: 1.0.0
**Estado**: Completo
