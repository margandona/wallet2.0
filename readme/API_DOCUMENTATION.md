# 📖 API Documentation - Wallet System

## Repository Interfaces & Implementation

Documentación completa de las interfaces de repositorio y su implementación con JPA.

---

## Table of Contents

1. [IUsuarioRepository](#iusuariorepository)
2. [ICuentaRepository](#icuentarepository)
3. [ITransaccionRepository](#itransaccionrepository)
4. [Exception Handling](#exception-handling)
5. [Validation Framework](#validation-framework)
6. [Examples](#ejemplos-de-uso)

---

## IUsuarioRepository

Interface para gestión de usuarios en el repositorio.

**Ubicación**: `src/main/java/com/wallet/domain/repositories/IUsuarioRepository.java`

**Implementación**: `src/main/java/com/wallet/infrastructure/repositories/UsuarioJPARepository.java`

### Métodos

#### 1. `guardar(Usuario usuario): Usuario`

Guarda un usuario nuevo o actualiza uno existente.

**Parámetros**:
- `usuario` (Usuario): Entidad de usuario a guardar

**Retorna**: Usuario guardado con ID asignado

**Excepciones Lanzadas**:
- `InvalidEmailFormatException` - Si el email no tiene formato válido
- `InvalidDocumentoFormatException` - Si el documento no tiene formato válido
- `DuplicateEmailException` - Si el email ya está registrado
- `DuplicateDocumentoException` - Si el documento ya está registrado
- `RepositoryException` - Si ocurre error en persistencia

**Validaciones**:
- Email debe cumplir regex: `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$`
- Documento debe cumplir formato según tipo (CEDULA: 7-10 dígitos, etc.)

**Ejemplo**:
```java
Usuario usuario = new Usuario(
    null,  // ID null para nuevo usuario
    "Juan",
    "Pérez",
    new Email("juan@example.com"),
    new DocumentoIdentidad("12345678", DocumentoIdentidad.TipoDocumento.CEDULA),
    true
);

Usuario guardado = usuarioRepository.guardar(usuario);
System.out.println("Usuario guardado con ID: " + guardado.getId());
```

**Logging**:
```
[2025-01-15 14:23:45] CREATE | Usuario | usr-123 | Usuario Juan Pérez creado
```

---

#### 2. `buscarPorId(String id): Optional<Usuario>`

Busca un usuario por su ID.

**Parámetros**:
- `id` (String): ID del usuario (UUID)

**Retorna**: Optional con usuario si existe, Optional.empty() si no

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Ejemplo**:
```java
Optional<Usuario> usuario = usuarioRepository.buscarPorId("usr-123");
if (usuario.isPresent()) {
    System.out.println("Usuario: " + usuario.get().getNombre());
} else {
    System.out.println("Usuario no encontrado");
}
```

---

#### 3. `buscarPorEmail(Email email): Optional<Usuario>`

Busca un usuario por su email.

**Parámetros**:
- `email` (Email): Valor objeto Email del usuario

**Retorna**: Optional con usuario si existe

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Ejemplo**:
```java
Email email = new Email("juan@example.com");
Optional<Usuario> usuario = usuarioRepository.buscarPorEmail(email);
usuario.ifPresent(u -> System.out.println("Encontrado: " + u.getNombre()));
```

---

#### 4. `buscarPorDocumento(String numeroDocumento): Optional<Usuario>`

Busca un usuario por su número de documento.

**Parámetros**:
- `numeroDocumento` (String): Número del documento de identidad

**Retorna**: Optional con usuario si existe

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Ejemplo**:
```java
Optional<Usuario> usuario = usuarioRepository.buscarPorDocumento("12345678");
```

---

#### 5. `obtenerTodos(): List<Usuario>`

Obtiene todos los usuarios registrados.

**Retorna**: Lista de todos los usuarios

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Ejemplo**:
```java
List<Usuario> usuarios = usuarioRepository.obtenerTodos();
System.out.println("Total de usuarios: " + usuarios.size());
```

---

#### 6. `obtenerActivos(): List<Usuario>`

Obtiene solo los usuarios activos.

**Retorna**: Lista de usuarios con estado activo = true

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Ejemplo**:
```java
List<Usuario> activos = usuarioRepository.obtenerActivos();
```

---

#### 7. `eliminar(String id): boolean`

Elimina un usuario por su ID.

**Parámetros**:
- `id` (String): ID del usuario a eliminar

**Retorna**: `true` si se eliminó, `false` si no existía

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Nota**: Al eliminar usuario, todas sus cuentas y transacciones se eliminan en cascada (CASCADE DELETE en BD).

**Ejemplo**:
```java
boolean eliminado = usuarioRepository.eliminar("usr-123");
if (eliminado) {
    System.out.println("Usuario eliminado");
}
```

---

#### 8. `existePorEmail(Email email): boolean`

Verifica si existe un usuario con el email dado.

**Parámetros**:
- `email` (Email): Email a verificar

**Retorna**: `true` si existe, `false` si no

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Ejemplo**:
```java
Email email = new Email("juan@example.com");
if (usuarioRepository.existePorEmail(email)) {
    System.out.println("Email ya está registrado");
}
```

---

#### 9. `existePorDocumento(String numeroDocumento): boolean`

Verifica si existe un usuario con el documento dado.

**Parámetros**:
- `numeroDocumento` (String): Número de documento a verificar

**Retorna**: `true` si existe, `false` si no

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Ejemplo**:
```java
if (!usuarioRepository.existePorDocumento("12345678")) {
    // Crear nuevo usuario
}
```

---

## ICuentaRepository

Interface para gestión de cuentas.

**Ubicación**: `src/main/java/com/wallet/domain/repositories/ICuentaRepository.java`

**Implementación**: `src/main/java/com/wallet/infrastructure/repositories/CuentaJPARepository.java`

### Métodos

#### 1. `guardar(Cuenta cuenta): Cuenta`

Guarda una cuenta nueva o actualiza una existente.

**Parámetros**:
- `cuenta` (Cuenta): Entidad de cuenta a guardar

**Retorna**: Cuenta guardada con ID asignado

**Excepciones Lanzadas**:
- `InvalidSaldoException` - Si el saldo es negativo
- `DuplicateCuentaException` - Si el número de cuenta ya existe
- `RepositoryException` - Si ocurre error en persistencia

**Validaciones**:
- Saldo debe ser >= 0
- Número de cuenta debe tener 10-20 dígitos

**Ejemplo**:
```java
Cuenta cuenta = new Cuenta(
    null,  // ID null para nueva cuenta
    "1234567890",  // Número de cuenta único
    usuarioId,
    new Dinero(new BigDecimal("1000.00"), "PEN"),
    true  // activa
);

Cuenta guardada = cuentaRepository.guardar(cuenta);
System.out.println("Cuenta creada: " + guardada.getNumeroCuenta());
```

---

#### 2. `buscarPorId(String id): Optional<Cuenta>`

Busca una cuenta por su ID.

**Parámetros**:
- `id` (String): ID de la cuenta

**Retorna**: Optional con cuenta si existe

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

---

#### 3. `buscarPorNumeroCuenta(String numeroCuenta): Optional<Cuenta>`

Busca una cuenta por su número único.

**Parámetros**:
- `numeroCuenta` (String): Número de la cuenta (10-20 dígitos)

**Retorna**: Optional con cuenta si existe

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Ejemplo**:
```java
Optional<Cuenta> cuenta = cuentaRepository.buscarPorNumeroCuenta("1234567890");
cuenta.ifPresent(c -> System.out.println("Saldo: " + c.getSaldo()));
```

---

#### 4. `buscarPorUsuarioId(String usuarioId): List<Cuenta>`

Obtiene todas las cuentas de un usuario específico.

**Parámetros**:
- `usuarioId` (String): ID del usuario propietario

**Retorna**: Lista de cuentas del usuario

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Ejemplo**:
```java
List<Cuenta> cuentas = cuentaRepository.buscarPorUsuarioId("usr-123");
System.out.println("Cuentas del usuario: " + cuentas.size());
```

---

#### 5. `obtenerActivasPorUsuario(String usuarioId): List<Cuenta>`

Obtiene solo las cuentas activas de un usuario.

**Parámetros**:
- `usuarioId` (String): ID del usuario

**Retorna**: Lista de cuentas activas (estado = true)

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

---

#### 6. `obtenerTodas(): List<Cuenta>`

Obtiene todas las cuentas del sistema.

**Retorna**: Lista de todas las cuentas

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

---

#### 7. `eliminar(String id): boolean`

Elimina una cuenta por su ID.

**Parámetros**:
- `id` (String): ID de la cuenta

**Retorna**: `true` si se eliminó, `false` si no existía

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Nota**: Al eliminar cuenta, todas sus transacciones se eliminan en cascada.

---

#### 8. `existeNumeroCuenta(String numeroCuenta): boolean`

Verifica si existe una cuenta con el número dado.

**Parámetros**:
- `numeroCuenta` (String): Número de cuenta a verificar

**Retorna**: `true` si existe, `false` si no

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

---

## ITransaccionRepository

Interface para gestión de transacciones.

**Ubicación**: `src/main/java/com/wallet/domain/repositories/ITransaccionRepository.java`

**Implementación**: `src/main/java/com/wallet/infrastructure/repositories/TransaccionJPARepository.java`

### Métodos

#### 1. `guardar(Transaccion transaccion): Transaccion`

Guarda una nueva transacción.

**Parámetros**:
- `transaccion` (Transaccion): Entidad de transacción a guardar

**Retorna**: Transacción guardada con ID asignado

**Excepciones Lanzadas**:
- `InvalidMontoException` - Si el monto es <= 0
- `RepositoryException` - Si ocurre error en persistencia

**Validaciones**:
- Monto debe ser > 0 (estrictamente positivo)

**Ejemplo**:
```java
Transaccion transaccion = new Transaccion(
    null,  // ID null para nueva transacción
    cuentaId,
    TipoTransaccion.DEPOSITO,
    new Dinero(new BigDecimal("500.00"), "PEN"),
    "Depósito de cliente",
    new BigDecimal("1000.00"),  // Saldo anterior
    new BigDecimal("1500.00")   // Saldo nuevo
);

Transaccion guardada = transaccionRepository.guardar(transaccion);
```

---

#### 2. `buscarPorId(String id): Optional<Transaccion>`

Busca una transacción por su ID.

**Parámetros**:
- `id` (String): ID de la transacción

**Retorna**: Optional con transacción si existe

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

---

#### 3. `buscarPorCuentaId(String cuentaId): List<Transaccion>`

Obtiene todas las transacciones de una cuenta.

**Parámetros**:
- `cuentaId` (String): ID de la cuenta

**Retorna**: Lista de transacciones de la cuenta (más recientes primero)

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Ejemplo**:
```java
List<Transaccion> transacciones = transaccionRepository.buscarPorCuentaId("cta-123");
transacciones.forEach(t -> 
    System.out.println(t.getTipo() + ": " + t.getMonto())
);
```

---

#### 4. `buscarPorCuentaIdYTipo(String cuentaId, TipoTransaccion tipo): List<Transaccion>`

Obtiene transacciones de una cuenta filtradas por tipo.

**Parámetros**:
- `cuentaId` (String): ID de la cuenta
- `tipo` (TipoTransaccion): Tipo de transacción (DEPOSITO, RETIRO, TRANSFERENCIA)

**Retorna**: Lista de transacciones filtradas

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Ejemplo**:
```java
List<Transaccion> depositos = transaccionRepository
    .buscarPorCuentaIdYTipo("cta-123", TipoTransaccion.DEPOSITO);
```

---

#### 5. `obtenerPorCuentaYFechas(String cuentaId, LocalDateTime fechaInicio, LocalDateTime fechaFin): List<Transaccion>`

Obtiene transacciones en un rango de fechas.

**Parámetros**:
- `cuentaId` (String): ID de la cuenta
- `fechaInicio` (LocalDateTime): Inicio del rango (inclusive)
- `fechaFin` (LocalDateTime): Fin del rango (inclusive)

**Retorna**: Lista de transacciones en el rango

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Ejemplo**:
```java
LocalDateTime inicio = LocalDateTime.now().minusDays(30);
LocalDateTime fin = LocalDateTime.now();

List<Transaccion> del30dias = transaccionRepository
    .obtenerPorCuentaYFechas("cta-123", inicio, fin);
```

---

#### 6. `obtenerTodas(): List<Transaccion>`

Obtiene todas las transacciones del sistema.

**Retorna**: Lista de todas las transacciones

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

---

#### 7. `obtenerUltimasPorCuenta(String cuentaId, int limite): List<Transaccion>`

Obtiene las últimas N transacciones de una cuenta.

**Parámetros**:
- `cuentaId` (String): ID de la cuenta
- `limite` (int): Cantidad máxima de transacciones a retornar

**Retorna**: Lista de hasta `limite` transacciones más recientes

**Excepciones Lanzadas**:
- `RepositoryException` - Si ocurre error en persistencia

**Ejemplo**:
```java
List<Transaccion> ultimas10 = transaccionRepository
    .obtenerUltimasPorCuenta("cta-123", 10);
```

---

## Exception Handling

### Jerarquía de Excepciones

```
RuntimeException
├── DuplicateEmailException
├── DuplicateDocumentoException
├── DuplicateCuentaException
├── InvalidEmailFormatException
├── InvalidDocumentoFormatException
├── InvalidSaldoException
├── InvalidMontoException
├── RepositoryException
└── SaldoInsuficienteException (pre-existente)
```

### Conversión de Excepciones

```
PersistenceException (JPA)
    ↓ Capturada en Repository
    ↓ Convertida a
RepositoryException (Dominio)
    ↓ Propagada a
Capa de Aplicación/Presentación
```

### Patrón de Manejo

```java
try {
    // Validación de entrada
    ValidatorUtil.validarEmail(email);
    
    // Validación de negocio
    if (repositorio.existePorEmail(email)) {
        throw new DuplicateEmailException(email.getValor());
    }
    
    // Operación de persistencia
    EntityManager em = JPAConfiguration.getEntityManager();
    em.getTransaction().begin();
    
    UsuarioJPAEntity usuarioGuardado = em.merge(usuarioJPA);
    em.getTransaction().commit();
    
} catch (InvalidEmailFormatException e) {
    // Excepción de validación de formato - propagar
    OperationLogger.logWarn("Usuario", "nuevo", e.getMessage());
    throw e;
} catch (DuplicateEmailException e) {
    // Excepción de negocio - propagar
    OperationLogger.logWarn("Usuario", "nuevo", e.getMessage());
    throw e;
} catch (PersistenceException e) {
    // Excepción de BD - convertir
    OperationLogger.logError("Usuario", usuarioId, "Error de persistencia", e);
    throw RepositoryException.operacionFallida("Usuario", "guardar", e.getMessage());
} finally {
    if (em != null) {
        em.close();
    }
}
```

---

## Validation Framework

### ValidatorUtil - Validaciones Centralizadas

```java
// Email validation
ValidatorUtil.validarEmail("user@example.com");
// Lanza: InvalidEmailFormatException

// Documento validation
ValidatorUtil.validarDocumento("12345678", "CEDULA");
// Lanza: InvalidDocumentoFormatException

// Saldo validation
ValidatorUtil.validarSaldo(new BigDecimal("1000.00"));
// Lanza: InvalidSaldoException

// Monto validation
ValidatorUtil.validarMonto(new BigDecimal("50.00"));
// Lanza: InvalidMontoException
```

### Formatos Válidos

| Campo | Formato | Ejemplo |
|-------|---------|---------|
| Email | `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$` | juan@example.com |
| CEDULA | 7-10 dígitos | 12345678 |
| PASAPORTE | 6-9 caracteres alfanuméricos | ABC123456 |
| LICENCIA | 7-12 dígitos | 1234567890 |
| Número Cuenta | 10-20 dígitos | 1234567890 |
| Saldo | >= 0 | 1000.50 |
| Monto | > 0 | 50.75 |

---

## Ejemplos de Uso

### Ejemplo 1: Crear Usuario Completo

```java
// 1. Validar entrada
try {
    String emailStr = "juan@example.com";
    String documento = "12345678";
    
    ValidatorUtil.validarEmail(emailStr);
    ValidatorUtil.validarDocumento(documento, "CEDULA");
    
    // 2. Crear entidad
    Email email = new Email(emailStr);
    DocumentoIdentidad doc = new DocumentoIdentidad(
        documento,
        DocumentoIdentidad.TipoDocumento.CEDULA
    );
    
    Usuario usuario = new Usuario(
        null,
        "Juan",
        "Pérez",
        email,
        doc,
        true
    );
    
    // 3. Guardar en repositorio
    Usuario guardado = usuarioRepository.guardar(usuario);
    System.out.println("Usuario creado: " + guardado.getId());
    
} catch (InvalidEmailFormatException e) {
    System.out.println("Email inválido: " + e.getMessage());
} catch (DuplicateEmailException e) {
    System.out.println("Email ya registrado: " + e.getMessage());
} catch (RepositoryException e) {
    System.out.println("Error de base de datos: " + e.getMessage());
}
```

---

### Ejemplo 2: Crear Cuenta y Depositar

```java
try {
    // 1. Buscar usuario
    Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorId("usr-123");
    if (!usuarioOpt.isPresent()) {
        throw new RuntimeException("Usuario no encontrado");
    }
    
    // 2. Crear cuenta
    Dinero saldoInicial = new Dinero(new BigDecimal("0.00"), "PEN");
    Cuenta cuenta = new Cuenta(
        null,
        "1234567890",
        usuarioOpt.get().getId(),
        saldoInicial,
        true
    );
    
    Cuenta cuentaCreada = cuentaRepository.guardar(cuenta);
    
    // 3. Realizar depósito
    BigDecimal montoDeposito = new BigDecimal("1000.00");
    ValidatorUtil.validarMonto(montoDeposito);
    
    Dinero montoTransaccion = new Dinero(montoDeposito, "PEN");
    Dinero nuevoSaldo = cuentaCreada.getSaldo().sumar(montoTransaccion);
    
    Cuenta cuentaActualizada = new Cuenta(
        cuentaCreada.getId(),
        cuentaCreada.getNumeroCuenta(),
        cuentaCreada.getUsuarioId(),
        nuevoSaldo,
        true
    );
    
    cuentaRepository.guardar(cuentaActualizada);
    
    // 4. Registrar transacción
    Transaccion transaccion = new Transaccion(
        null,
        cuentaCreada.getId(),
        TipoTransaccion.DEPOSITO,
        montoTransaccion,
        "Depósito inicial",
        saldoInicial.getMonto(),
        nuevoSaldo.getMonto()
    );
    
    Transaccion transaccionGuardada = transaccionRepository.guardar(transaccion);
    System.out.println("Depósito realizado: " + transaccionGuardada.getId());
    
} catch (InvalidMontoException e) {
    System.out.println("Monto inválido: " + e.getMessage());
} catch (DuplicateCuentaException e) {
    System.out.println("Número de cuenta ya existe: " + e.getMessage());
} catch (RepositoryException e) {
    System.out.println("Error de persistencia: " + e.getMessage());
}
```

---

### Ejemplo 3: Consultar Transacciones del Último Mes

```java
try {
    String cuentaId = "cta-123";
    
    // 1. Calcular rango de fechas
    LocalDateTime ahora = LocalDateTime.now();
    LocalDateTime hace30Dias = ahora.minusDays(30);
    
    // 2. Obtener transacciones
    List<Transaccion> transacciones = transaccionRepository
        .obtenerPorCuentaYFechas(cuentaId, hace30Dias, ahora);
    
    // 3. Procesar resultados
    System.out.println("Transacciones del último mes: " + transacciones.size());
    
    BigDecimal totalDepositado = BigDecimal.ZERO;
    BigDecimal totalRetirado = BigDecimal.ZERO;
    
    for (Transaccion t : transacciones) {
        if (t.getTipo() == TipoTransaccion.DEPOSITO) {
            totalDepositado = totalDepositado.add(t.getMonto().getMonto());
        } else if (t.getTipo() == TipoTransaccion.RETIRO) {
            totalRetirado = totalRetirado.add(t.getMonto().getMonto());
        }
    }
    
    System.out.println("Total depositado: " + totalDepositado);
    System.out.println("Total retirado: " + totalRetirado);
    
} catch (RepositoryException e) {
    System.out.println("Error al obtener transacciones: " + e.getMessage());
}
```

---

## Logging Operacional

### Operaciones Registradas

```
[Timestamp] LEVEL | Entity | ID | Description

[2025-01-15 14:23:45] CREATE | Usuario | usr-123 | Usuario Juan Pérez creado
[2025-01-15 14:24:10] READ   | Usuario | usr-123 | Usuario encontrado
[2025-01-15 14:24:15] WARN   | Usuario | nuevo   | Intento de crear usuario con email duplicado
[2025-01-15 14:25:30] ERROR  | Usuario | usr-123 | Error de persistencia al guardar
```

### Ver Estadísticas

```java
// Imprimir estadísticas de operaciones
OperationLogger.printStatistics();

// Output:
// CREATE: 42 operaciones
// READ:   128 operaciones
// UPDATE: 15 operaciones
// DELETE: 3 operaciones
// TOTAL:  188 operaciones
```

### Archivo de Log

- **Ubicación**: `wallet_operations.log` (raíz del proyecto)
- **Formato**: CSV con timestamp, level, entity, id, description
- **Thread-Safe**: Usa ReentrantReadWriteLock para acceso concurrente

---

## Contact & Support

Para preguntas sobre la API:
- Revisar ejemplos en `src/test/java/com/wallet/infrastructure/repositories/`
- Consultar tests en `FlujoComipletoIntegrationTest.java`
- Ver documentación de dominio en `readme/ARCHITECTURE.md`

---

**API Version**: 1.0.0  
**Database**: SQLite with Hibernate/JPA  
**Java Version**: 21+  
**Last Updated**: Enero 2025
