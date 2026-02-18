# 🛠️ Guía de Desarrollo - Sistema Wallet (Fase 7-8)

Guía para desarrolladores que deseen entender, extender o mantener el Sistema de Billetera Digital.

**Estado Actual**: ✅ Fase 7 (Optimización) Completada | 🔄 Fase 8 (Documentación) en Progreso

---

## 📋 Contenido

1. [Requisitos de Desarrollo](#requisitos-de-desarrollo)
2. [Arquitectura General](#arquitectura-general)
3. [Estructura del Proyecto](#estructura-del-proyecto)
4. [Fases de Desarrollo](#fases-de-desarrollo)
5. [Framework de Validación (Fase 7)](#framework-de-validación-fase-7)
6. [Manejo de Excepciones (Fase 7)](#manejo-de-excepciones-fase-7)
7. [Patrones de Repositorio](#patrones-de-repositorio)
8. [Testing](#testing)
9. [Logging Operacional](#logging-operacional)
10. [Extending el Sistema](#extending-el-sistema)

---

## Requisitos de Desarrollo

### Herramientas Necesarias

- **JDK 21 LTS**: Java Development Kit (recomendado)
- **Maven 3.9.6+**: Build tool (recomendado para BD)
- **PowerShell 5.1+**: Para scripts
- **Git**: Control de versiones
- **SQLite 3.44.0.0+**: Base de datos

### Setup Rápido

```powershell
# 1. Clonar y setup
git clone <repository-url>
cd wallet
.\download-dependencies.ps1

# 2. Compilar (Maven)
mvn clean install

# 3. Ejecutar
mvn exec:java -Dexec.mainClass="com.wallet.Main"

# O con PowerShell
.\run.ps1
```

---

## Arquitectura General

### Diagrama de Capas

```
┌─────────────────────────────────────┐
│   Presentation Layer                │
│   - ConsoleMenuController           │
│   - User interaction via console    │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│   Application Layer                 │
│   - Use Cases (Business logic)      │
│   - DTOs & Mappers                  │
│   - ValidatorUtil (10 methods)      │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│   Domain Layer                      │
│   - Entities: Usuario, Cuenta...    │
│   - Value Objects: Email, Documento │
│   - 8 Exception classes (Fase 7)    │
│   - Repository interfaces           │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│   Infrastructure Layer              │
│   - 3 JPA Repositories (24 métodos) │
│   - Exception handling & logging    │
│   - Database configuration          │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│   Persistence Layer                 │
│   - Hibernate ORM                   │
│   - SQLite + HikariCP               │
│   - Database constraints            │
└─────────────────────────────────────┘
```

### Stack Tecnológico (Fase 7+)

```
Frontend:     Console (PowerShell / Terminal)
              ↓
Application:  Java 21 LTS + Maven 3.9.6+
              ↓
Framework:    Hibernate 6.4.4 / Jakarta Persistence 3.1.0
              ↓
Database:     SQLite 3.44.0.0 + HikariCP 5.1.0
              
Testing:      JUnit 5.10.1 + Mockito 5.8.0 + AssertJ 3.25.1
Validation:   ValidatorUtil (10 métodos centralizado)
Exceptions:   8 Domain exception classes
Logging:      OperationLogger (thread-safe)
```

---

## Estructura del Proyecto

### Árbol Completo

```
wallet/
├── src/
│   ├── main/java/com/wallet/
│   │   ├── Main.java                             (Entry point)
│   │   ├── application/
│   │   │   ├── dtos/                            (Data Transfer Objects)
│   │   │   │   ├── UsuarioDTO.java
│   │   │   │   ├── CuentaDTO.java
│   │   │   │   ├── TransaccionDTO.java
│   │   │   │   ├── ConversionDivisaDTO.java
│   │   │   │   └── requests/
│   │   │   ├── mappers/                         (DTO ↔ Entity mappers)
│   │   │   │   ├── UsuarioMapper.java
│   │   │   │   ├── CuentaMapper.java
│   │   │   │   └── TransaccionMapper.java
│   │   │   ├── services/                        (Business services)
│   │   │   └── usecases/                        (Use case implementations)
│   │   │       ├── BuscarUsuarioUseCase.java
│   │   │       ├── CrearCuentaUseCase.java
│   │   │       └── ... otros use cases
│   │   ├── domain/
│   │   │   ├── entities/                        (Core domain objects)
│   │   │   │   ├── Usuario.java
│   │   │   │   ├── Cuenta.java
│   │   │   │   └── Transaccion.java
│   │   │   ├── exceptions/                      (Fase 7: Domain exceptions)
│   │   │   │   ├── RepositoryException.java
│   │   │   │   ├── DuplicateEmailException.java
│   │   │   │   ├── DuplicateDocumentoException.java
│   │   │   │   ├── DuplicateCuentaException.java
│   │   │   │   ├── InvalidEmailFormatException.java
│   │   │   │   ├── InvalidDocumentoFormatException.java
│   │   │   │   ├── InvalidSaldoException.java
│   │   │   │   └── InvalidMontoException.java
│   │   │   ├── repositories/                    (Repository interfaces)
│   │   │   │   ├── IUsuarioRepository.java
│   │   │   │   ├── ICuentaRepository.java
│   │   │   │   └── ITransaccionRepository.java
│   │   │   ├── services/                        (Domain services)
│   │   │   └── valueobjects/                    (Value objects)
│   │   │       ├── Email.java
│   │   │       ├── Documento.java
│   │   │       └── Moneda.java
│   │   ├── infrastructure/
│   │   │   ├── factories/                       (Factory patterns)
│   │   │   ├── logging/                         (Logging system)
│   │   │   │   ├── OperationLogger.java
│   │   │   │   └── wallet_operations.log
│   │   │   ├── persistence/                     (JPA/Hibernate config)
│   │   │   │   └── PersistenceConfig.java
│   │   │   ├── repositories/                    (JPA implementations)
│   │   │   │   ├── UsuarioJPARepository.java    (9 methods)
│   │   │   │   ├── CuentaJPARepository.java     (8 methods)
│   │   │   │   └── TransaccionJPARepository.java (7 methods)
│   │   │   ├── services/                        (Infrastructure services)
│   │   │   └── validation/                      (Fase 7: Validation)
│   │   │       └── ValidatorUtil.java           (10 validation methods)
│   │   └── presentation/
│   │       ├── controllers/                     (Console controllers)
│   │       ├── menus/                           (Menu UI)
│   │       ├── ui/                              (UI components)
│   │       └── utils/                           (Presentation utils)
│   └── test/java/com/wallet/
│       ├── application/
│       │   └── usecases/                        (Use case tests)
│       ├── domain/
│       │   ├── entities/                        (Entity tests)
│       │   └── valueobjects/                    (Value object tests)
│       └── infrastructure/
│           ├── repositories/                    (Repository tests)
│           │   ├── UsuarioJPARepositoryTest.java (11 tests)
│           │   ├── CuentaJPARepositoryTest.java (11 tests)
│           │   └── TransaccionJPARepositoryTest.java (10 tests)
│           └── integration/
│               └── FlujoComipletoIntegrationTest.java (5 E2E tests)
├── pom.xml                                      (Maven configuration)
├── wallet.db                                    (SQLite database)
├── wallet_operations.log                        (Operation logs)
├── README.md                                    (Main documentation)
├── DEVELOPMENT.md                               (Developer guide - this file)
└── readme/
    ├── API_DOCUMENTATION.md                     (API reference - 900+ lines)
    ├── USER_GUIDE.md                            (User guide)
    ├── ARCHITECTURE.md                          (Architecture details)
    ├── PLAN_DESARROLLO.md                       (Development plan)
    └── FASE_7_OPTIMIZACION_COMPLETADA.md       (Phase 7 summary)
```

---

## Fases de Desarrollo

### Fase 1: Setup Inicial ✅
- Maven project setup
- Basic project structure
- Main.java entry point

### Fase 2: Domain Layer ✅
- Entities: Usuario, Cuenta, Transaccion
- Value Objects: Email, Documento, Moneda
- Repository interfaces

### Fase 3: Infrastructure - JPA ✅
- Hibernate configuration
- ORM mapping
- Repository implementations

### Fase 4: Database Setup ✅
- SQLite integration
- Database schema
- HikariCP connection pooling

### Fase 5: Use Cases & Services ✅
- Business logic implementation
- DTOs and mappers
- Service layer

### Fase 6: Testing ✅
- 37 unit/integration tests
- 5 end-to-end tests
- 100% repository method coverage

### Fase 7: Optimización ⭐ COMPLETADA
- **8 Domain Exception Classes**
  - `RepositoryException` (base)
  - `DuplicateEmailException`
  - `DuplicateDocumentoException`
  - `DuplicateCuentaException`
  - `InvalidEmailFormatException`
  - `InvalidDocumentoFormatException`
  - `InvalidSaldoException`
  - `InvalidMontoException`

- **ValidatorUtil (10 Centralized Methods)**
  - `validarEmail(String)` - RFC 5322 regex
  - `validarDocumento(String, TipoDocumento)` - Type-specific validation
  - `validarSaldo(BigDecimal)` - >= 0 requirement
  - `validarMonto(BigDecimal)` - > 0 requirement
  - Plus 6 more utility methods

- **24 Repository Methods Enhanced**
  - UsuarioJPARepository: 9 methods (all updated)
  - CuentaJPARepository: 8 methods (all updated)
  - TransaccionJPARepository: 7 methods (all updated)
  - Exception handling on all
  - OperationLogger integrated

- **Logging Integration**
  - Thread-safe OperationLogger
  - All CRUD operations logged
  - wallet_operations.log file

### Fase 8: Documentación 🔄 EN PROGRESO
- ✅ Updated README.md (+150 lines)
- ✅ API_DOCUMENTATION.md (900+ lines, 24 methods documented)
- ✅ USER_GUIDE.md (comprehensive user manual)
- 🟡 DEVELOPMENT.md (updated - this file)
- 🟡 Architecture diagrams
- 🟡 CHANGELOG

---

## Framework de Validación (Fase 7)

### 4-Layer Validation Pattern

```
Layer 1: Presentation (Console Input)
    └─ Format validation, non-null checks
    
Layer 2: Application (Use Cases)
    └─ Business logic validation via ValidatorUtil
    
Layer 3: Domain (Entities/Value Objects)
    └─ Domain constraints validation in constructors
    
Layer 4: Infrastructure (Repositories)
    └─ Database constraints & exception handling
```

### ValidatorUtil (10 Methods)

**Location**: `src/main/java/com/wallet/infrastructure/validation/ValidatorUtil.java`

```java
public class ValidatorUtil {
    
    // Email validation with RFC 5322 pattern
    public static void validarEmail(String email) 
        throws InvalidEmailFormatException
    
    // Document type-specific validation
    public static void validarDocumento(String doc, TipoDocumento tipo)
        throws InvalidDocumentoFormatException
    
    // Balance >= 0 validation
    public static void validarSaldo(BigDecimal saldo)
        throws InvalidSaldoException
    
    // Amount > 0 validation  
    public static void validarMonto(BigDecimal monto)
        throws InvalidMontoException
    
    // Plus 6 more utility methods...
}
```

### Format Specifications

| Format | Validation | Example |
|--------|-----------|---------|
| Email | RFC 5322 regex | `user@domain.com` |
| CEDULA | 7-10 digits | `12345678` |
| PASAPORTE | 6-9 alphanumeric | `AB123456` |
| LICENCIA | 7-12 digits | `1234567890` |
| Account Number | 10-20 digits | `1234567890` |
| Saldo | >= 0 | `1000.00` |
| Monto | > 0 | `500.50` |
| Descripción | 0-255 chars | `Deposit` |

### Usage Example

```java
// Application layer (use case)
public void crearUsuario(UsuarioDTO dto) throws RepositoryException {
    // Layer 2: Application validation
    ValidatorUtil.validarEmail(dto.email);
    ValidatorUtil.validarDocumento(dto.documento, dto.tipoDoc);
    
    // Create domain object (Layer 3 validation in constructor)
    Usuario usuario = new Usuario(
        dto.nombre,
        dto.apellido,
        new Email(dto.email),           // Email VO validates
        new Documento(dto.tipoDoc, dto.documento)  // Documento VO validates
    );
    
    // Layer 4: Repository handles duplicates
    usuarioRepository.guardar(usuario);  // May throw DuplicateEmailException
}
```

---

## Manejo de Excepciones (Fase 7)

### Exception Hierarchy

```
RuntimeException
    └── RepositoryException
        ├── DuplicateEmailException
        ├── DuplicateDocumentoException
        ├── DuplicateCuentaException
        ├── InvalidEmailFormatException
        ├── InvalidDocumentoFormatException
        ├── InvalidSaldoException
        └── InvalidMontoException
```

### Exception Conversion Flow

```
JPA/Hibernate Layer
    ├─ EntityExistsException
    ├─ ConstraintViolationException
    └─ PersistenceException
            ↓
Infrastructure (Repository)
    └─ Catch and convert to Domain Exception
            ↓
Domain Layer
    ├─ DuplicateEmailException
    ├─ InvalidEmailFormatException
    └─ RepositoryException (fallback)
            ↓
Application Layer (Use Case)
    └─ Catch and handle
            ↓
Presentation Layer
    └─ Display to user
```

### Implementation Pattern

All 3 JPA repositories use this pattern:

```java
@Override
public Usuario guardar(Usuario usuario) throws RepositoryException {
    EntityManager em = null;
    try {
        em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        tx.begin();
        
        // Pre-persistence validation
        if (existePorEmail(usuario.getEmail())) {
            throw new DuplicateEmailException(
                "Email ya existe: " + usuario.getEmail().getValor()
            );
        }
        
        em.persist(usuario);
        tx.commit();
        
        // Log successful operation
        logger.logOperation("CREATE", "Usuario", usuario.getId());
        return usuario;
        
    } catch (DuplicateEmailException e) {
        if (em != null && em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        logger.logError("Email duplicado: " + e.getMessage(), e);
        throw e;
        
    } catch (PersistenceException e) {
        if (em != null && em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        logger.logError("Error de persistencia: " + e.getMessage(), e);
        throw new RepositoryException("Error al guardar usuario", e);
        
    } finally {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}
```

---

## Patrones de Repositorio

### Repository Interface (Domain)

```java
// IUsuarioRepository.java
public interface IUsuarioRepository {
    Usuario guardar(Usuario usuario) throws RepositoryException;
    Optional<Usuario> buscarPorId(String id) throws RepositoryException;
    Optional<Usuario> buscarPorEmail(Email email) throws RepositoryException;
    Optional<Usuario> buscarPorDocumento(String documento) throws RepositoryException;
    List<Usuario> obtenerTodos() throws RepositoryException;
    List<Usuario> obtenerActivos() throws RepositoryException;
    void eliminar(String id) throws RepositoryException;
    boolean existePorEmail(Email email) throws RepositoryException;
    boolean existePorDocumento(String documento) throws RepositoryException;
}
```

### 9 Methods - UsuarioJPARepository

1. `guardar()` - Create/update with email+documento duplicate checks
2. `buscarPorId()` - Find by UUID
3. `buscarPorEmail()` - Find by Email VO
4. `buscarPorDocumento()` - Find by documento string
5. `obtenerTodos()` - List all with count logging
6. `obtenerActivos()` - Filter active users
7. `eliminar()` - Delete with cascade
8. `existePorEmail()` - Duplicate check
9. `existePorDocumento()` - Duplicate check

### 8 Methods - CuentaJPARepository

1. `guardar()` - Create/update with saldo >= 0 validation
2. `buscarPorId()` - Find by UUID
3. `buscarPorNumeroCuenta()` - Find by account number
4. `buscarPorUsuarioId()` - Get all user accounts
5. `obtenerActivasPorUsuario()` - Get active accounts
6. `obtenerTodas()` - List all
7. `eliminar()` - Delete with cascade
8. `existeNumeroCuenta()` - Duplicate check

### 7 Methods - TransaccionJPARepository

1. `guardar()` - Create with monto > 0 validation
2. `buscarPorId()` - Find by UUID
3. `buscarPorCuentaId()` - Get all account transactions
4. `buscarPorCuentaIdYTipo()` - Filter by transaction type
5. `obtenerPorCuentaYFechas()` - Date range query
6. `obtenerTodas()` - List all
7. `obtenerUltimasPorCuenta()` - Get last N transactions

---

## Testing

### Test Coverage (42 Total Tests)

```
Unit/Integration Tests: 37
├── Application usecases: 5 tests
├── Domain entities: 12 tests  
├── Domain value objects: 8 tests
└── Infrastructure repositories: 32 tests (updated Phase 7)

End-to-End Tests: 5
└── FlujoComipletoIntegrationTest: 5 workflow tests

Coverage: 100% of repository methods
```

### Execute Tests

```powershell
# All tests
mvn test

# Specific test class
mvn test -Dtest=UsuarioJPARepositoryTest

# Specific test method
mvn test -Dtest=UsuarioJPARepositoryTest#testGuardarUsuario

# With coverage report
mvn clean test jacoco:report

# View coverage
start target/site/jacoco/index.html
```

### Test Pattern with Mocking (Phase 7)

```java
@Test
void testGuardarUsuarioConValidacion() throws RepositoryException {
    // Arrange
    OperationLogger mockLogger = mock(OperationLogger.class);
    EntityManagerFactory mockEmf = mock(EntityManagerFactory.class);
    
    Usuario usuario = new Usuario(
        "Juan", "Pérez",
        new Email("juan@test.com"),
        new Documento(TipoDocumento.CEDULA, "12345678")
    );
    
    // Act
    UsuarioJPARepository repository = new UsuarioJPARepository(mockEmf, mockLogger);
    Usuario guardado = repository.guardar(usuario);
    
    // Assert
    assertNotNull(guardado.getId());
    
    // Verify logging
    verify(mockLogger).logOperation("CREATE", "Usuario", guardado.getId());
}
```

### Exception Testing

```java
@Test
void testGuardarUsuarioDuplicado() {
    Usuario usuario1 = createTestUsuario("juan@test.com");
    Usuario usuario2 = createTestUsuario("juan@test.com");  // Same email
    
    repository.guardar(usuario1);
    
    // Assert
    assertThrows(DuplicateEmailException.class, () -> {
        repository.guardar(usuario2);
    });
}
```

---

## Logging Operacional

### OperationLogger Features

- Thread-safe (using synchronized methods)
- Centralized logging location
- Operation tracking (CREATE, READ, UPDATE, DELETE)
- Error logging

### Location

```
wallet/
├── wallet_operations.log     (Operation log)
└── wallet_errors.log         (Error log)
```

### Log Entry Format

```
[2025-01-15 14:23:45] CREATE: Usuario (ID: usr-550e8400-e29b-41d4-a716-446655440000)
[2025-01-15 14:25:30] CREATE: Cuenta (ID: cta-e29b-41d4-a716-446655440000)
[2025-01-15 14:30:45] READ: Usuario (ID: usr-550e8400-e29b-41d4-a716-446655440000)
[2025-01-15 14:35:20] UPDATE: Transaccion (ID: trx-abcd-1234)
[2025-01-15 14:40:15] DELETE: Usuario (ID: usr-550e8400-e29b-41d4-a716-446655440000)
```

### Usage in Repository

```java
public Usuario guardar(Usuario usuario) throws RepositoryException {
    try {
        // ... persistence logic ...
        logger.logOperation("CREATE", "Usuario", usuario.getId());
    } catch (Exception e) {
        logger.logError("Error al guardar usuario", e);
        throw new RepositoryException("Error", e);
    }
}
```

---

## Extending el Sistema

### Example: Add New Entity

#### 1. Create Domain Entity

```java
// domain/entities/Beneficiario.java
public class Beneficiario {
    private String id;
    private String nombre;
    private Email email;
    private String cuentaBeneficiario;
    private Usuario usuario;
    
    public Beneficiario(String nombre, Email email, String cuentaBeneficiario) {
        ValidatorUtil.validarEmail(email.getValor());
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.email = email;
        this.cuentaBeneficiario = cuentaBeneficiario;
    }
    // getters, setters, equals, hashCode...
}
```

#### 2. Create Repository Interface

```java
// domain/repositories/IBeneficiarioRepository.java
public interface IBeneficiarioRepository {
    Beneficiario guardar(Beneficiario beneficiario) throws RepositoryException;
    Optional<Beneficiario> buscarPorId(String id) throws RepositoryException;
    List<Beneficiario> obtenerPorUsuario(String usuarioId) throws RepositoryException;
    void eliminar(String id) throws RepositoryException;
}
```

#### 3. Implement JPA Repository

```java
// infrastructure/repositories/BeneficiarioJPARepository.java
@Entity
@Table(name = "beneficiarios")
public class BeneficiarioJPA {
    @Id
    private String id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioJPA usuario;
}

public class BeneficiarioJPARepository implements IBeneficiarioRepository {
    private EntityManagerFactory emf;
    private OperationLogger logger;
    
    public Beneficiario guardar(Beneficiario beneficiario) throws RepositoryException {
        // Follow the pattern from UsuarioJPARepository
    }
}
```

#### 4. Create Use Case

```java
// application/usecases/AgregarBeneficiarioUseCase.java
public class AgregarBeneficiarioUseCase {
    private IBeneficiarioRepository repository;
    private IUsuarioRepository usuarioRepository;
    
    public void execute(String usuarioId, BeneficiarioDTO dto) 
        throws RepositoryException {
        
        ValidatorUtil.validarEmail(dto.email);
        
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
            .orElseThrow(() -> new RepositoryException("Usuario no encontrado"));
        
        Beneficiario beneficiario = new Beneficiario(
            dto.nombre,
            new Email(dto.email),
            dto.cuentaBeneficiario
        );
        
        repository.guardar(beneficiario);
    }
}
```

### Example: Add New Exception

```java
// domain/exceptions/InsufficientFundsException.java
public class InsufficientFundsException extends RepositoryException {
    public InsufficientFundsException(String message) {
        super(message);
    }
    
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### Example: Add New Validator

```java
// In ValidatorUtil
public static void validarCuentaBeneficiario(String cuenta) 
    throws InvalidAccountNumberException {
    if (cuenta == null || cuenta.length() < 10 || cuenta.length() > 20) {
        throw new InvalidAccountNumberException(
            "Account number must be 10-20 characters"
        );
    }
}
```

---

## Best Practices

### 1. Exception Handling

✅ **DO**:
```java
try {
    Usuario usuario = repository.buscarPorId(id);
} catch (RepositoryException ex) {
    logger.logError("Error buscando usuario", ex);
    throw new ApplicationException("No se pudo buscar usuario", ex);
}
```

❌ **DON'T**:
```java
try {
    Usuario usuario = repository.buscarPorId(id);
} catch (Exception ex) {
    ex.printStackTrace();  // Silent failure
}
```

### 2. Validation Layers

✅ **DO**: Validate in Use Cases before repository call
❌ **DON'T**: Trust database constraints only

### 3. Logging

✅ **DO**: Use `OperationLogger` for all operations
❌ **DON'T**: Use `System.out.println()`

### 4. Resource Management

✅ **DO**: Use try-finally to close EntityManager
❌ **DON'T**: Leave resources open

### 5. Transactions

✅ **DO**: Use EntityTransaction for CRUD operations
❌ **DON'T**: Rely on auto-commit

### 6. Testing

✅ **DO**: Mock external dependencies, test single responsibility
❌ **DON'T**: Create test database dependencies

### 7. Documentation

✅ **DO**: Document exceptions thrown by methods
❌ **DON'T**: Leave method contracts unclear

---

## Build & Deploy

### Local Development

```powershell
# Build
mvn clean install

# Run
mvn exec:java -Dexec.mainClass="com.wallet.Main"

# Test
mvn test
```

### Package for Distribution

```powershell
# Create JAR
mvn clean package

# Run JAR
java -jar target/wallet-1.0.0.jar
```

### Database Backup

```powershell
# Backup database
Copy-Item wallet.db wallet.db.backup_$(Get-Date -Format "yyyy-MM-dd")
```

---

## Troubleshooting

### Issue: Tests failing with "Database locked"

**Solution**: Ensure only one test instance running
```powershell
mvn test -DforkCount=1
```

### Issue: EntityManager already closed

**Solution**: Check finally blocks close EntityManager properly

### Issue: Duplicate key exception on startup

**Solution**: Delete wallet.db to recreate schema
```powershell
Remove-Item wallet.db
```

---

## References

- [API_DOCUMENTATION.md](readme/API_DOCUMENTATION.md) - Complete API reference
- [USER_GUIDE.md](readme/USER_GUIDE.md) - User documentation
- [README.md](README.md) - Main project documentation
- [FASE_7_OPTIMIZACION_COMPLETADA.md](readme/FASE_7_OPTIMIZACION_COMPLETADA.md) - Phase 7 details

---

**Versión**: 1.0.0  
**Última actualización**: Enero 2025  
**Fase actual**: 7 (Completada) | 8 (Documentación en progreso)  
**Estado**: ✅ Operacional | 📚 En documentación
