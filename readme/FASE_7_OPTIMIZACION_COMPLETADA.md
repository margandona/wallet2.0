# FASE 7 - OPTIMIZATION: EXCEPTION HANDLING & VALIDATION - COMPLETADA ✅

## Resumen Ejecutivo

Se ha completado exitosamente la Fase 7 del proyecto Wallet, enfocada en optimización mediante:
- **Manejo Robusto de Excepciones**: Conversión automática de excepciones JPA a excepciones de dominio
- **Validación de Entrada Completa**: Validador centralizado para todos los tipos de datos
- **Logging Operacional Integrado**: Registro de auditoría thread-safe en todas las operaciones CRUD

## 1. EXCEPCIONES DE DOMINIO CREADAS

Se crearon 7 nuevas clases de excepción especializadas en `src/main/java/com/wallet/domain/exceptions/`:

### a) RepositoryException.java
```java
// Excepción base para errores de repositorio
public class RepositoryException extends RuntimeException
// Métodos:
- RepositoryException(String mensaje)
- RepositoryException(String mensaje, Throwable causa)
- static RepositoryException operacionFallida(String entidad, String operacion, String razon)
```
**Uso**: Envuelve PersistenceException y otros errores JPA

### b) DuplicateEmailException.java
```java
// Se lanza cuando se intenta guardar usuario con email duplicado
public class DuplicateEmailException extends RuntimeException
// Ejemplo: "El email 'user@example.com' ya está registrado en el sistema."
```

### c) DuplicateDocumentoException.java
```java
// Se lanza cuando se intenta guardar usuario con documento duplicado
public class DuplicateDocumentoException extends RuntimeException
// Ejemplo: "El documento '123456789' (CEDULA) ya está registrado en el sistema."
```

### d) DuplicateCuentaException.java
```java
// Se lanza cuando se intenta crear cuenta con número duplicado
public class DuplicateCuentaException extends RuntimeException
// Ejemplo: "El número de cuenta '1234567890' ya existe en el sistema."
```

### e) InvalidEmailFormatException.java
```java
// Se lanza cuando formato de email es inválido
public class InvalidEmailFormatException extends RuntimeException
// Valida según regex: ^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$
```

### f) InvalidDocumentoFormatException.java
```java
// Se lanza cuando formato de documento es inválido según su tipo
public class InvalidDocumentoFormatException extends RuntimeException
// Valida: CEDULA (7-10 dígitos), PASAPORTE (6-9 caracteres), LICENCIA (7-12 dígitos)
```

### g) InvalidSaldoException.java
```java
// Se lanza cuando saldo es negativo
public class InvalidSaldoException extends RuntimeException
// Validación: saldo >= 0
```

### h) InvalidMontoException.java
```java
// Se lanza cuando monto de transacción es inválido
public class InvalidMontoException extends RuntimeException
// Validación: monto > 0 (estrictamente positivo)
```

## 2. CLASE ValidatorUtil CREADA

**Ubicación**: `src/main/java/com/wallet/infrastructure/validation/ValidatorUtil.java`

Clase utilitaria con métodos estáticos para validación centralizada:

### Validaciones Implementadas:

```java
// Email validation
validarEmail(String email)
- Regex: ^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$
- Lanza: InvalidEmailFormatException

// Documento validation por tipo
validarDocumento(String documento, String tipoDocumento)
- CEDULA: 7-10 dígitos
- PASAPORTE: 6-9 caracteres alfanuméricos
- LICENCIA: 7-12 dígitos
- Lanza: InvalidDocumentoFormatException

// Saldo validation
validarSaldo(BigDecimal saldo)
- Validación: saldo >= 0
- Lanza: InvalidSaldoException

// Monto validation
validarMonto(BigDecimal monto)
- Validación: monto > 0
- Lanza: InvalidMontoException

// Saldo sufficiency
validarSaldoSuficiente(BigDecimal saldoActual, BigDecimal montoRequerido)
- Validación: saldoActual >= montoRequerido
- Lanza: SaldoInsuficienteException

// Nombre validation
validarNombre(String nombre)
- Mínimo 2 caracteres
- Lanza: IllegalArgumentException

// Número de cuenta validation
validarNumeroCuenta(String numeroCuenta)
- 10-20 dígitos
- Lanza: IllegalArgumentException

// Descripción validation
validarDescripcion(String descripcion)
- Máximo 500 caracteres
- Lanza: IllegalArgumentException
```

## 3. ACTUALIZACIÓN: UsuarioJPARepository

**Ubicación**: `src/main/java/com/wallet/infrastructure/repositories/UsuarioJPARepository.java`

### Cambios Principales:

#### 1. Imports Agregados:
```java
import com.wallet.domain.exceptions.*;
import com.wallet.infrastructure.logging.OperationLogger;
import com.wallet.infrastructure.validation.ValidatorUtil;
import jakarta.persistence.PersistenceException;
```

#### 2. Método `guardar()` - Mejorado
```java
@Override
public Usuario guardar(Usuario usuario) {
    // VALIDACIÓN PRE-OPERACIÓN
    ValidatorUtil.validarEmail(usuario.getEmail().getValor());
    ValidatorUtil.validarDocumento(
        usuario.getDocumentoIdentidad().getNumero(),
        usuario.getDocumentoIdentidad().getTipo().name()
    );
    
    // VERIFICACIÓN DE DUPLICADOS
    if (usuario.getId() == null || usuario.getId().isEmpty()) {
        if (existePorEmail(usuario.getEmail())) {
            OperationLogger.logWarn("Usuario", "nuevo", 
                "Intento de crear usuario con email duplicado: " + usuario.getEmail().getValor());
            throw new DuplicateEmailException(usuario.getEmail().getValor());
        }
        
        if (existePorDocumento(usuario.getDocumentoIdentidad().getNumero())) {
            OperationLogger.logWarn("Usuario", "nuevo",
                "Intento de crear usuario con documento duplicado: " + ...);
            throw new DuplicateDocumentoException(...);
        }
    }
    
    // OPERACIÓN CON MANEJO DE EXCEPCIONES
    try {
        // ... operación JPA ...
        OperationLogger.logCreate("Usuario", usuarioResultado.getId(),
            String.format("Usuario %s %s creado", ...));
        return usuarioResultado;
        
    } catch (InvalidEmailFormatException | InvalidDocumentoFormatException |
             DuplicateEmailException | DuplicateDocumentoException e) {
        // Excepciones de validación - propagar directamente
        if (em != null && em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        throw e;
    } catch (PersistenceException e) {
        // Excepciones JPA - convertir a dominio
        if (em != null && em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        OperationLogger.logError("Usuario", usuario.getId(), 
            "Error de persistencia al guardar", e);
        throw RepositoryException.operacionFallida("Usuario", "guardar", e.getMessage());
    } catch (Exception e) {
        // Excepciones inesperadas
        if (em != null && em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        OperationLogger.logError("Usuario", usuario.getId(), 
            "Error inesperado al guardar", e);
        throw RepositoryException.operacionFallida("Usuario", "guardar", e.getMessage());
    } finally {
        if (em != null) {
            em.close();
        }
    }
}
```

#### 3. Todos los métodos mejorados:
- `buscarPorId()` - OperationLogger en READ
- `buscarPorEmail()` - Manejo de excepciones
- `buscarPorDocumento()` - Manejo de excepciones
- `obtenerTodos()` - Logging con cantidad
- `obtenerActivos()` - Logging con cantidad
- `eliminar()` - Logging de DELETE
- `existePorEmail()` - Logging de verificación
- `existePorDocumento()` - Logging de verificación

**Patrón de Manejo de Excepciones** aplicado a TODOS los métodos:
```java
EntityManager em = null;
try {
    // Operación
} catch (PersistenceException e) {
    OperationLogger.logError(...);
    throw RepositoryException.operacionFallida(...);
} catch (Exception e) {
    OperationLogger.logError(...);
    throw RepositoryException.operacionFallida(...);
} finally {
    if (em != null) {
        em.close();
    }
}
```

## 4. ACTUALIZACIÓN: CuentaJPARepository

**Ubicación**: `src/main/java/com/wallet/infrastructure/repositories/CuentaJPARepository.java`

### Cambios Principales:

#### 1. Imports Agregados:
```java
import com.wallet.domain.exceptions.*;
import com.wallet.infrastructure.logging.OperationLogger;
import com.wallet.infrastructure.validation.ValidatorUtil;
import jakarta.persistence.PersistenceException;
```

#### 2. Validaciones en `guardar()`:
```java
// Pre-validación
ValidatorUtil.validarSaldo(cuenta.getSaldo().getMonto());
ValidatorUtil.validarNumeroCuenta(cuenta.getNumeroCuenta());

// Verificación de duplicados
if (existeNumeroCuenta(cuenta.getNumeroCuenta())) {
    OperationLogger.logWarn("Cuenta", "nueva", ...);
    throw new DuplicateCuentaException(cuenta.getNumeroCuenta());
}
```

#### 3. Excepciones Capturadas:
- `InvalidSaldoException` - Saldo negativo
- `DuplicateCuentaException` - Número de cuenta ya existe
- `PersistenceException` - Errores JPA convertidos a RepositoryException

#### 4. Logging Integrado:
- CREATE: guardar cuenta nueva
- READ: búsquedas por ID, número, usuario
- UPDATE: implícito en merge
- DELETE: eliminación de cuenta

#### 5. Todos los métodos mejorados:
- `guardar()` - Validación + Logging
- `buscarPorId()` - Manejo de excepciones
- `buscarPorNumeroCuenta()` - Manejo de excepciones
- `buscarPorUsuarioId()` - Logging con cantidad
- `obtenerActivasPorUsuario()` - Logging con cantidad
- `obtenerTodas()` - Logging con cantidad
- `eliminar()` - Logging de DELETE
- `existeNumeroCuenta()` - Logging de verificación

## 5. ACTUALIZACIÓN: TransaccionJPARepository

**Ubicación**: `src/main/java/com/wallet/infrastructure/repositories/TransaccionJPARepository.java`

### Cambios Principales:

#### 1. Imports Agregados:
```java
import com.wallet.domain.exceptions.*;
import com.wallet.infrastructure.logging.OperationLogger;
import com.wallet.infrastructure.validation.ValidatorUtil;
import jakarta.persistence.PersistenceException;
```

#### 2. Validaciones en `guardar()`:
```java
// Pre-validación de monto
ValidatorUtil.validarMonto(transaccion.getMonto().getMonto());
```

#### 3. Excepciones Capturadas:
- `InvalidMontoException` - Monto <= 0
- `PersistenceException` - Errores JPA convertidos a RepositoryException

#### 4. Logging Integrado:
- CREATE: nueva transacción con tipo y monto
- READ: búsquedas por ID, cuenta, tipo, fechas
- DELETE: operación no aplicable (transacciones inmutables)

#### 5. Todos los métodos mejorados:
- `guardar()` - Validación de monto + Logging
- `buscarPorId()` - Manejo de excepciones
- `buscarPorCuentaId()` - Logging con cantidad
- `buscarPorCuentaIdYTipo()` - Logging con tipo
- `obtenerPorCuentaYFechas()` - Logging con rango
- `obtenerTodas()` - Logging con cantidad
- `obtenerUltimasPorCuenta()` - Logging con límite

## 6. CARACTERÍSTICAS DE MANEJO DE ERRORES

### Principios Aplicados:

#### a) Conversión de Excepciones (Exception Translation Pattern)
```
JPA Exception (PersistenceException)
        ↓
RepositoryException (Domain Exception)
        ↓
Capa de Aplicación maneja de manera consistente
```

#### b) Validación en Capas
```
Layer 1: ValidatorUtil - Validación de entrada
    ↓ (lanza InvalidXXXException)
Layer 2: Repository - Validación de negocio (duplicados, relaciones)
    ↓ (lanza DuplicateXXXException)
Layer 3: JPA - Operación de BD
    ↓ (PersistenceException capturada)
Layer 4: RepositoryException propagada a aplicación
```

#### c) Transacción Segura
```java
EntityManager em = null;
try {
    // Operación protegida
} catch (...) {
    if (em != null && em.getTransaction().isActive()) {
        em.getTransaction().rollback(); // Rollback automático en error
    }
} finally {
    if (em != null) {
        em.close(); // Siempre limpia recursos
    }
}
```

#### d) Logging Estratégico
```
- logCreate(): Nueva entidad creada
- logRead(): Búsqueda exitosa
- logUpdate(): Entidad actualizada
- logDelete(): Entidad eliminada
- logWarn(): Intento de operación no permitida (ej: duplicado)
- logError(): Excepción capturada con stack trace
```

## 7. FLUJO DE EXCEPCIÓN - EJEMPLO REAL

### Escenario: Crear Usuario con Email Duplicado

```
Usuario UI
    ↓ (crea Usuario con email duplicado)
UsuarioJPARepository.guardar()
    ├─ ValidatorUtil.validarEmail() ✓ OK
    ├─ ValidatorUtil.validarDocumento() ✓ OK
    ├─ existePorEmail(email) → true
    ├─ OperationLogger.logWarn("Usuario", "nuevo", "Intento con email duplicado")
    └─ throw new DuplicateEmailException("email@example.com")
    ↓
Capa de Aplicación captura DuplicateEmailException
    ├─ log "Email ya registrado"
    └─ mostrar mensaje al usuario
```

### Escenario: Error de BD en Guardar

```
Usuario UI
    ↓
UsuarioJPARepository.guardar()
    ├─ Validaciones OK
    ├─ em.merge(usuarioJPA)
    ├─ ERROR: BD no disponible
    ├─ throw PersistenceException
    ├─ catch (PersistenceException e)
    ├─ OperationLogger.logError("Usuario", usuarioId, "Error de persistencia", e)
    ├─ em.getTransaction().rollback()
    └─ throw RepositoryException.operacionFallida(...)
    ↓
Capa de Aplicación captura RepositoryException
    ├─ log "Error al guardar usuario en BD"
    └─ mostrar mensaje de error genérico
```

## 8. LOGGING OPERACIONAL

### Formato de Logs

```
[2025-01-15 14:23:45] CREATE | Usuario | usr-123 | Usuario Juan Pérez creado
[2025-01-15 14:24:10] READ   | Usuario | usr-123 | Usuario encontrado
[2025-01-15 14:25:30] WARN   | Usuario | nuevo   | Intento de crear usuario con email duplicado: john@example.com
[2025-01-15 14:26:45] ERROR  | Usuario | usr-123 | Error de persistencia al guardar
  Causa: org.hibernate.exception.DataException: could not execute statement
```

### Archivo de Log

- **Ubicación**: `wallet_operations.log` en raíz del proyecto
- **Rotación**: No (archivo crece indefinidamente, usar `OperationLogger.clearLog()` para reset)
- **Thread-Safe**: Usa `ReentrantReadWriteLock` para acceso concurrente
- **Estadísticas**: Método `OperationLogger.printStatistics()` cuenta operaciones por tipo

## 9. INTEGRACIÓN CON OperationLogger

Todo el logging está integrado mediante `OperationLogger.java` (ya existente):

```java
// En cada operación:
OperationLogger.logCreate("Usuario", id, "Usuario creado");
OperationLogger.logRead("Usuario", id, "Usuario encontrado");
OperationLogger.logError("Usuario", id, "Error descripción", exception);

// Ver estadísticas:
OperationLogger.printStatistics();
// Output:
// CREATE: 42 operaciones
// READ:   128 operaciones
// UPDATE: 15 operaciones
// DELETE: 3 operaciones
```

## 10. CORRESPONDENCIA CON schema.sql

Las validaciones en código **coinciden** con las restricciones en BD:

```
schema.sql                          ↔ ValidatorUtil
────────────────────────────────────────────────────
CHECK (saldo >= 0)                  ↔ validarSaldo()
CHECK (monto > 0)                   ↔ validarMonto()
UNIQUE (email)                      ↔ existePorEmail() + DuplicateEmailException
UNIQUE (documento)                  ↔ existePorDocumento() + DuplicateDocumentoException
UNIQUE (numero_cuenta)              ↔ existeNumeroCuenta() + DuplicateCuentaException
INDEX (email, documento, ...)       ↔ Búsquedas optimizadas
```

## 11. ESTADO DE COMPILACIÓN

**Nota Importante**: El proyecto requiere Maven para compilar:

```powershell
# Instalar Maven (si no está disponible):
powershell -ExecutionPolicy Bypass -File .\install-maven.ps1

# Compilar:
.\compile.ps1
# o
mvn clean compile

# Ejecutar tests:
mvn test

# Correr aplicación:
.\run.ps1
```

## 12. VERIFICACIÓN DE CAMBIOS

### Archivos Creados:
1. ✅ `src/main/java/com/wallet/domain/exceptions/RepositoryException.java`
2. ✅ `src/main/java/com/wallet/domain/exceptions/DuplicateEmailException.java`
3. ✅ `src/main/java/com/wallet/domain/exceptions/DuplicateDocumentoException.java`
4. ✅ `src/main/java/com/wallet/domain/exceptions/DuplicateCuentaException.java`
5. ✅ `src/main/java/com/wallet/domain/exceptions/InvalidEmailFormatException.java`
6. ✅ `src/main/java/com/wallet/domain/exceptions/InvalidDocumentoFormatException.java`
7. ✅ `src/main/java/com/wallet/domain/exceptions/InvalidSaldoException.java`
8. ✅ `src/main/java/com/wallet/domain/exceptions/InvalidMontoException.java`
9. ✅ `src/main/java/com/wallet/infrastructure/validation/ValidatorUtil.java`

### Archivos Mejorados:
1. ✅ `src/main/java/com/wallet/infrastructure/repositories/UsuarioJPARepository.java`
   - Manejo de excepciones: 9 métodos
   - Validación de entrada: 2 campos
   - Logging integrado: 9 métodos

2. ✅ `src/main/java/com/wallet/infrastructure/repositories/CuentaJPARepository.java`
   - Manejo de excepciones: 8 métodos
   - Validación de entrada: 2 campos
   - Logging integrado: 8 métodos

3. ✅ `src/main/java/com/wallet/infrastructure/repositories/TransaccionJPARepository.java`
   - Manejo de excepciones: 7 métodos
   - Validación de entrada: 1 campo
   - Logging integrado: 7 métodos

## 13. PRÓXIMOS PASOS (Fase 8)

- [ ] Compilación exitosa con Maven
- [ ] Ejecución de suite de tests (37 tests)
- [ ] Verificación de logs en wallet_operations.log
- [ ] Documentación de uso de APIs
- [ ] Deployment a producción

---

**Fase 7 Completada**: ✅
- Manejo robusto de excepciones ✅
- Validación centralizada ✅
- Logging operacional integrado ✅
- Código limpio y mantenible ✅
- Alineado con Clean Architecture ✅

**Responsable**: GitHub Copilot
**Fecha**: Enero 2025
**Versión**: 1.0.0
