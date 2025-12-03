# 🏗️ ARCHITECTURE - Sistema Wallet

## Tabla de Contenidos

- [Visión General](#visión-general)
- [Clean Architecture](#clean-architecture)
- [Capas del Sistema](#capas-del-sistema)
- [Patrones de Diseño](#patrones-de-diseño)
- [Principios SOLID](#principios-solid)
- [Flujo de Datos](#flujo-de-datos)
- [Decisiones de Diseño](#decisiones-de-diseño)

---

## Visión General

El sistema Wallet implementa **Clean Architecture** (Arquitectura Limpia) propuesta por Robert C. Martin (Uncle Bob), que organiza el código en capas concéntricas con dependencias apuntando hacia adentro.

### Objetivos de la Arquitectura

1. **Independencia de Frameworks**: No dependemos de Maven/Gradle
2. **Testabilidad**: 85 tests automatizados (100% success)
3. **Independencia de UI**: La lógica no depende de la consola
4. **Independencia de BD**: Fácil cambio de almacenamiento en memoria a BD
5. **Independencia de Agentes Externos**: El dominio no conoce detalles externos

---

## Clean Architecture

### Diagrama de Capas

```
                 ┌──────────────────────┐
                 │   PRESENTATION       │
                 │   (UI, Controllers)  │
                 └──────────┬───────────┘
                            │
                 ┌──────────▼───────────┐
                 │   APPLICATION        │
                 │   (Use Cases, DTOs)  │
                 └──────────┬───────────┘
                            │
                 ┌──────────▼───────────┐
                 │   INFRASTRUCTURE     │
                 │   (Repos, Services)  │
                 └──────────┬───────────┘
                            │
                 ┌──────────▼───────────┐
                 │      DOMAIN          │
                 │  (Entities, V.O.)    │
                 └──────────────────────┘
```

### Regla de Dependencia

**Las dependencias solo apuntan hacia adentro:**
- Presentation → Application → Infrastructure → Domain
- Domain NO conoce ninguna otra capa
- Las interfaces están en las capas internas

---

## Capas del Sistema

### 1. Domain Layer (Núcleo del Negocio)

**Responsabilidad**: Contiene la lógica de negocio pura, independiente de cualquier framework o tecnología.

#### Componentes

##### 📦 Entities (Entidades)

Objetos con identidad única que contienen lógica de negocio.

```java
// Usuario.java
public class Usuario {
    private final String id;
    private String nombre;
    private String apellido;
    private Email email;
    private DocumentoIdentidad documentoIdentidad;
    private boolean activo;
    
    // Lógica de negocio
    public void actualizar(String nombre, String apellido) {
        validarNombre(nombre);
        validarApellido(apellido);
        this.nombre = nombre;
        this.apellido = apellido;
    }
    
    public void activar() { this.activo = true; }
    public void desactivar() { this.activo = false; }
}
```

**Entidades implementadas:**
- `Usuario`: Representa un usuario del sistema
- `Cuenta`: Representa una cuenta bancaria
- `Transaccion`: Representa un movimiento financiero

##### 💎 Value Objects (Objetos de Valor)

Objetos inmutables sin identidad, definidos por sus atributos.

```java
// Dinero.java
public final class Dinero {
    private final BigDecimal cantidad;
    private final Moneda moneda;
    
    // Inmutable: operaciones retornan nuevos objetos
    public Dinero sumar(Dinero otro) {
        validarMismaMoneda(otro);
        return new Dinero(
            this.cantidad.add(otro.cantidad),
            this.moneda
        );
    }
    
    // Value Objects se comparan por valor
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Dinero)) return false;
        Dinero otro = (Dinero) obj;
        return cantidad.equals(otro.cantidad) && 
               moneda == otro.moneda;
    }
}
```

**Value Objects implementados:**
- `Dinero`: Representa cantidades monetarias
- `Email`: Valida y encapsula emails
- `DocumentoIdentidad`: DNI, Pasaporte, RUC, etc.
- `Moneda`: Enum para PEN, USD, EUR

##### 🔌 Repository Interfaces

Contratos para acceso a datos (implementados en Infrastructure).

```java
// IUsuarioRepository.java
public interface IUsuarioRepository {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorId(String id);
    Optional<Usuario> buscarPorEmail(Email email);
    boolean existePorEmail(Email email);
    boolean existePorDocumento(DocumentoIdentidad documento);
    List<Usuario> listarActivos();
}
```

##### ⚠️ Domain Exceptions

Excepciones específicas del negocio.

```java
public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(Dinero saldo, Dinero monto) {
        super(String.format(
            "Saldo insuficiente. Saldo: %s, Requerido: %s",
            saldo, monto
        ));
    }
}
```

**Excepciones implementadas:**
- `UsuarioNoEncontradoException`
- `CuentaNoEncontradaException`
- `EmailInvalidoException`
- `OperacionNoValidaException`
- `DominioException` (base)

---

### 2. Application Layer (Casos de Uso)

**Responsabilidad**: Orquesta el flujo de datos desde/hacia las entidades, coordinando la lógica de negocio.

#### Componentes

##### 🎯 Use Cases

Representan las acciones que un usuario puede realizar.

```java
// CrearUsuarioUseCase.java
public class CrearUsuarioUseCase {
    private final IUsuarioRepository usuarioRepository;
    
    public UsuarioDTO ejecutar(CrearUsuarioRequest request) {
        // 1. Validar entrada
        validarRequest(request);
        
        // 2. Verificar reglas de negocio
        if (usuarioRepository.existePorEmail(email)) {
            throw new OperacionNoValidaException("Email duplicado");
        }
        
        // 3. Crear entidad
        Usuario usuario = new Usuario(
            request.getNombre(),
            request.getApellido(),
            email,
            documento
        );
        
        // 4. Persistir
        Usuario guardado = usuarioRepository.guardar(usuario);
        
        // 5. Retornar DTO
        return UsuarioMapper.toDTO(guardado);
    }
}
```

**Use Cases implementados (8):**

**Gestión de Usuarios:**
1. `CrearUsuarioUseCase`: Registrar nuevo usuario
2. `BuscarUsuarioPorEmailUseCase`: Buscar por email
3. `ListarUsuariosUseCase`: Listar usuarios activos

**Gestión de Cuentas:**
4. `CrearCuentaUseCase`: Crear cuenta bancaria
5. `ConsultarSaldoUseCase`: Ver saldo de cuenta

**Operaciones Financieras:**
6. `DepositarDineroUseCase`: Depositar fondos
7. `RetirarDineroUseCase`: Retirar fondos
8. `TransferirDineroUseCase`: Transferir entre cuentas

##### 📋 DTOs (Data Transfer Objects)

Objetos simples para transferir datos entre capas.

```java
// UsuarioDTO.java
public class UsuarioDTO {
    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private String tipoDocumento;
    private String numeroDocumento;
    private boolean activo;
    
    // Solo getters y setters, sin lógica
}
```

**Estructura de DTOs:**
- `dtos/`: DTOs principales (UsuarioDTO, CuentaDTO, TransaccionDTO)
- `dtos/requests/`: DTOs de entrada (CrearUsuarioRequest, etc.)
- `dtos/responses/`: DTOs de salida (futuro)

##### 🔄 Mappers

Convierten entre Entities y DTOs.

```java
// UsuarioMapper.java
public class UsuarioMapper {
    public static UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getEmail().getValor(),
            usuario.getDocumentoIdentidad().getTipo().name(),
            usuario.getDocumentoIdentidad().getNumero(),
            usuario.isActivo()
        );
    }
    
    // No hay toEntity porque la creación de entidades
    // es responsabilidad de los constructores del dominio
}
```

---

### 3. Infrastructure Layer (Detalles Técnicos)

**Responsabilidad**: Implementa las interfaces definidas en Domain, provee servicios técnicos.

#### Componentes

##### 💾 Repository Implementations

Implementaciones concretas de los repositorios.

```java
// UsuarioRepositoryInMemory.java
public class UsuarioRepositoryInMemory implements IUsuarioRepository {
    // Thread-safe storage
    private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();
    
    @Override
    public Usuario guardar(Usuario usuario) {
        usuarios.put(usuario.getId(), usuario);
        return usuario;
    }
    
    @Override
    public Optional<Usuario> buscarPorId(String id) {
        return Optional.ofNullable(usuarios.get(id));
    }
    
    @Override
    public boolean existePorEmail(Email email) {
        return usuarios.values().stream()
            .anyMatch(u -> u.getEmail().equals(email));
    }
    
    // ... más métodos
}
```

**Repositorios implementados:**
- `UsuarioRepositoryInMemory`
- `CuentaRepositoryInMemory`
- `TransaccionRepositoryInMemory`

##### 🏭 Repository Factory (Singleton)

Crea y provee instancias únicas de repositorios.

```java
// RepositoryFactory.java
public class RepositoryFactory {
    private static RepositoryFactory instance;
    private final IUsuarioRepository usuarioRepository;
    private final ICuentaRepository cuentaRepository;
    private final ITransaccionRepository transaccionRepository;
    
    private RepositoryFactory() {
        this.usuarioRepository = new UsuarioRepositoryInMemory();
        this.cuentaRepository = new CuentaRepositoryInMemory();
        this.transaccionRepository = new TransaccionRepositoryInMemory();
    }
    
    public static RepositoryFactory getInstance() {
        if (instance == null) {
            synchronized (RepositoryFactory.class) {
                if (instance == null) {
                    instance = new RepositoryFactory();
                }
            }
        }
        return instance;
    }
    
    public IUsuarioRepository getUsuarioRepository() {
        return usuarioRepository;
    }
    
    // Utilidad para testing
    public static void limpiarTodos() {
        // Reinicia todos los repositorios
    }
}
```

##### 🎭 Services (Facade Pattern)

Simplifican operaciones complejas exponiendo APIs más simples.

```java
// UsuarioService.java
public class UsuarioService {
    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final BuscarUsuarioPorEmailUseCase buscarPorEmailUseCase;
    private final ListarUsuariosUseCase listarUsuariosUseCase;
    
    public UsuarioService() {
        IUsuarioRepository repository = 
            RepositoryFactory.getInstance().getUsuarioRepository();
        
        this.crearUsuarioUseCase = new CrearUsuarioUseCase(repository);
        this.buscarPorEmailUseCase = new BuscarUsuarioPorEmailUseCase(repository);
        this.listarUsuariosUseCase = new ListarUsuariosUseCase(repository);
    }
    
    public UsuarioDTO registrarUsuario(CrearUsuarioRequest request) {
        return crearUsuarioUseCase.ejecutar(request);
    }
    
    // ... más métodos fachada
}
```

##### 📝 Logger

Sistema simple de logging con niveles.

```java
// Logger.java
public class Logger {
    public enum Nivel {
        INFO, WARNING, ERROR, DEBUG
    }
    
    public static void info(String mensaje) {
        log(Nivel.INFO, mensaje);
    }
    
    public static void error(String mensaje) {
        log(Nivel.ERROR, mensaje);
    }
    
    private static void log(Nivel nivel, String mensaje) {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.printf("%s [%-7s] %s%n", timestamp, nivel, mensaje);
    }
}
```

---

### 4. Presentation Layer (Interfaz de Usuario)

**Responsabilidad**: Interactúa con el usuario, delega a Application Layer.

#### Componentes

##### 🎮 Controllers (MVC Pattern)

Coordinan entre UI y servicios.

```java
// UsuarioController.java
public class UsuarioController {
    private final UsuarioService usuarioService;
    
    public void registrarUsuario() {
        try {
            // 1. Obtener input del usuario
            String nombre = ConsoleUtils.readLine("Nombre: ");
            String apellido = ConsoleUtils.readLine("Apellido: ");
            String email = ConsoleUtils.readLine("Email: ");
            
            // 2. Crear request
            CrearUsuarioRequest request = new CrearUsuarioRequest(
                nombre, apellido, email, tipoDoc, numeroDoc
            );
            
            // 3. Llamar al servicio
            UsuarioDTO usuario = usuarioService.registrarUsuario(request);
            
            // 4. Mostrar resultado
            ConsoleUtils.printSuccess("Usuario creado: " + usuario.getId());
            
        } catch (Exception e) {
            ConsoleUtils.printError("Error: " + e.getMessage());
        }
    }
}
```

**Controllers implementados:**
- `UsuarioController`: Gestión de usuarios
- `CuentaController`: Gestión de cuentas
- `TransaccionController`: Operaciones financieras

##### 📱 Menus (Command Pattern)

Menús interactivos que encapsulan acciones.

```java
// MenuUsuarios.java
public class MenuUsuarios {
    private final UsuarioController controller;
    
    public void mostrar() {
        while (true) {
            printMenu();
            int opcion = ConsoleUtils.readInt("Opción: ");
            
            switch (opcion) {
                case 1 -> controller.registrarUsuario();
                case 2 -> controller.buscarPorEmail();
                case 3 -> controller.listarUsuarios();
                case 0 -> { return; }
                default -> ConsoleUtils.printError("Opción inválida");
            }
        }
    }
}
```

##### 🛠️ Console Utils

Utilidades para I/O y validación.

```java
// ConsoleUtils.java
public class ConsoleUtils {
    private static final Scanner scanner = new Scanner(System.in);
    
    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    public static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            try {
                String input = readLine(prompt);
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                printError("Número inválido");
            }
        }
    }
    
    public static void printHeader(String titulo) {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("  " + titulo);
        System.out.println("═".repeat(50));
    }
    
    public static String formatMoney(BigDecimal amount) {
        return String.format("PEN %,.2f", amount);
    }
}
```

---

## Patrones de Diseño

### 1. Repository Pattern

**Problema**: Acceso directo a datos acopla el código a la tecnología de persistencia.

**Solución**: Interfaces de repositorio en Domain, implementaciones en Infrastructure.

```java
// Domain define el contrato
public interface IUsuarioRepository {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorId(String id);
}

// Infrastructure implementa el detalle
public class UsuarioRepositoryInMemory implements IUsuarioRepository {
    private Map<String, Usuario> storage = new ConcurrentHashMap<>();
    // Implementación...
}
```

**Beneficios:**
- Fácil cambio de almacenamiento (memoria → DB)
- Testeable con mocks
- Código de negocio desacoplado

---

### 2. Factory Pattern (Singleton)

**Problema**: Crear múltiples instancias de repositorios causa inconsistencias.

**Solución**: RepositoryFactory como Singleton que provee instancias únicas.

```java
public class RepositoryFactory {
    private static RepositoryFactory instance;
    
    private RepositoryFactory() {
        // Constructor privado
    }
    
    public static RepositoryFactory getInstance() {
        if (instance == null) {
            synchronized (RepositoryFactory.class) {
                if (instance == null) {
                    instance = new RepositoryFactory();
                }
            }
        }
        return instance;
    }
}
```

**Beneficios:**
- Control centralizado de creación
- Instancias compartidas (datos consistentes)
- Thread-safe (double-checked locking)

---

### 3. Facade Pattern

**Problema**: Use Cases múltiples complican el código cliente.

**Solución**: Servicios que encapsulan y simplifican operaciones.

```java
public class CuentaService {
    // Encapsula múltiples use cases
    private final CrearCuentaUseCase crearCuentaUseCase;
    private final ConsultarSaldoUseCase consultarSaldoUseCase;
    private final ListarCuentasUseCase listarCuentasUseCase;
    
    // API simplificada
    public CuentaDTO crearCuenta(String usuarioId) { ... }
    public CuentaDTO consultarSaldo(String numeroCuenta) { ... }
}
```

**Beneficios:**
- API más simple para Presentation Layer
- Oculta complejidad de coordinación
- Punto único de acceso

---

### 4. Value Object Pattern

**Problema**: Validaciones primitivas dispersas en el código.

**Solución**: Encapsular valores con validación en objetos inmutables.

```java
public final class Email {
    private final String valor;
    
    public Email(String valor) {
        validar(valor);
        this.valor = valor.toLowerCase().trim();
    }
    
    private void validar(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new EmailInvalidoException(email);
        }
    }
    
    // Inmutable: no hay setters
    public String getValor() { return valor; }
}
```

**Beneficios:**
- Validación centralizada
- Imposible tener valores inválidos
- Semántica rica en el dominio

---

### 5. DTO Pattern

**Problema**: Exponer entidades de dominio causa acoplamiento.

**Solución**: Objetos de transferencia sin lógica.

```java
// Entity (Domain)
public class Usuario {
    private String id;
    private Email email;  // Value Object complejo
    // + lógica de negocio
}

// DTO (Application)
public class UsuarioDTO {
    private String id;
    private String email;  // String simple
    // Solo getters/setters
}
```

**Beneficios:**
- Desacopla capas
- Serialización simple
- Versionamiento independiente

---

## Principios SOLID

### S - Single Responsibility Principle

Cada clase tiene una única responsabilidad.

```java
// ✅ CORRECTO: Una responsabilidad por clase
public class Usuario {
    // Solo lógica de usuario
}

public class IUsuarioRepository {
    // Solo persistencia de usuarios
}

public class CrearUsuarioUseCase {
    // Solo el caso de uso de crear usuario
}
```

### O - Open/Closed Principle

Abierto para extensión, cerrado para modificación.

```java
// ✅ Extensible vía interfaces
public interface IUsuarioRepository {
    Usuario guardar(Usuario usuario);
}

// Podemos agregar implementaciones sin modificar código existente
public class UsuarioRepositoryInMemory implements IUsuarioRepository { }
public class UsuarioRepositoryMySQL implements IUsuarioRepository { }  // Futuro
```

### L - Liskov Substitution Principle

Subtipos deben ser sustituibles por sus tipos base.

```java
// ✅ Cualquier IUsuarioRepository puede usarse
IUsuarioRepository repo = new UsuarioRepositoryInMemory();
// O cambiar a:
IUsuarioRepository repo = new UsuarioRepositoryMySQL();
// El código cliente no cambia
```

### I - Interface Segregation Principle

Interfaces específicas mejor que una general.

```java
// ✅ CORRECTO: Interfaces específicas
public interface IUsuarioRepository {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorId(String id);
}

public interface ICuentaRepository {
    Cuenta guardar(Cuenta cuenta);
    Optional<Cuenta> buscarPorId(String id);
}

// ❌ INCORRECTO: Una interfaz gigante
// public interface IRepository {
//     Usuario guardarUsuario(Usuario u);
//     Cuenta guardarCuenta(Cuenta c);
//     ...
// }
```

### D - Dependency Inversion Principle

Depender de abstracciones, no de concreciones.

```java
// ✅ CORRECTO: Use Case depende de abstracción
public class CrearUsuarioUseCase {
    private final IUsuarioRepository repository;  // Interfaz
    
    public CrearUsuarioUseCase(IUsuarioRepository repository) {
        this.repository = repository;
    }
}

// ❌ INCORRECTO: Depender de implementación concreta
// public class CrearUsuarioUseCase {
//     private final UsuarioRepositoryInMemory repository;
// }
```

---

## Flujo de Datos

### Ejemplo: Crear Usuario

```
1. USER INPUT (Presentation)
   └─> MenuUsuarios.mostrar()
       └─> UsuarioController.registrarUsuario()

2. REQUEST CREATION (Presentation)
   └─> new CrearUsuarioRequest(nombre, apellido, email, ...)

3. SERVICE CALL (Infrastructure)
   └─> UsuarioService.registrarUsuario(request)

4. USE CASE EXECUTION (Application)
   └─> CrearUsuarioUseCase.ejecutar(request)
       ├─> Validar request
       ├─> Verificar email no existe (Repository)
       ├─> Crear entidad Usuario (Domain)
       ├─> Guardar en repositorio (Repository)
       └─> Mapear a DTO (Application)

5. RESPONSE (Application → Presentation)
   └─> UsuarioDTO retornado
       └─> Controller muestra resultado
           └─> ConsoleUtils.printSuccess()
```

### Diagrama de Secuencia

```
User → Menu → Controller → Service → UseCase → Repository → Entity
                                                    ↓
User ← Menu ← Controller ← Service ← UseCase ← DTO ← Mapper
```

---

## Decisiones de Diseño

### 1. ¿Por qué Clean Architecture?

**Decisión**: Usar Clean Architecture en lugar de arquitectura en capas tradicional.

**Razones:**
- ✅ Independencia de frameworks (sin Maven)
- ✅ Testeable (85 tests, 100% success)
- ✅ Mantenible (separación clara de responsabilidades)
- ✅ Escalable (fácil agregar nuevos use cases)

**Trade-offs:**
- ⚠️ Más archivos/carpetas
- ⚠️ Curva de aprendizaje inicial
- ⚠️ Requiere disciplina del equipo

---

### 2. ¿Por qué Almacenamiento en Memoria?

**Decisión**: Usar ConcurrentHashMap en lugar de base de datos.

**Razones:**
- ✅ Simplicidad (no requiere DB externa)
- ✅ Performance (operaciones en milisegundos)
- ✅ Testing (setup/teardown rápido)
- ✅ Thread-safe (ConcurrentHashMap)

**Trade-offs:**
- ⚠️ Datos volátiles (se pierden al cerrar)
- ⚠️ Limitado por RAM
- ⚠️ No hay persistencia

**Mitigación:**
- 🔄 Fácil cambio a DB (Repository Pattern)
- 🔄 Solo cambiar implementaciones en Infrastructure
- 🔄 Use Cases y Domain no cambian

---

### 3. ¿Por qué Value Objects Inmutables?

**Decisión**: Value Objects sin setters, completamente inmutables.

**Razones:**
- ✅ Thread-safe (sin mutaciones)
- ✅ No hay estados inválidos
- ✅ Fácil razonamiento (sin side effects)
- ✅ Cacheable (hash consistente)

**Trade-offs:**
- ⚠️ Crear nuevos objetos en cada operación
- ⚠️ Mayor uso de memoria (GC)

**Ejemplo:**
```java
// ✅ Inmutable
Dinero saldoInicial = Dinero.de("100.00");
Dinero saldoFinal = saldoInicial.sumar(Dinero.de("50.00"));
// saldoInicial sigue siendo 100.00

// ❌ Mutable (NO usado)
// saldoInicial.setSuma(50.00);
// saldoInicial ahora es 150.00 (side effect)
```

---

### 4. ¿Por qué DTOs en lugar de Entities?

**Decisión**: Use Cases retornan DTOs, no Entities.

**Razones:**
- ✅ Desacopla Presentation de Domain
- ✅ Control sobre qué datos se exponen
- ✅ Serialización simple (JSON futuro)
- ✅ Versionamiento independiente

**Trade-offs:**
- ⚠️ Código adicional (Mappers)
- ⚠️ Duplicación de estructuras

**Mitigación:**
- 🔄 Mappers centralizados
- 🔄 Considerar Lombok (futuro)

---

### 5. ¿Por qué PowerShell Scripts?

**Decisión**: Scripts .ps1 en lugar de Maven/Gradle.

**Razones:**
- ✅ Transparencia total (javac visible)
- ✅ Sin archivos XML/Groovy
- ✅ Control fino de classpath
- ✅ Educativo (entender compilación)

**Trade-offs:**
- ⚠️ No portable a Linux/Mac
- ⚠️ Sin gestión automática de dependencias
- ⚠️ No hay repositorio central (Maven Central)

**Mitigación:**
- 🔄 Considerar Bash scripts (Linux)
- 🔄 Migrar a Maven (futuro)

---

### 6. ¿Por qué Console UI?

**Decisión**: Interfaz de consola en lugar de REST API o GUI.

**Razones:**
- ✅ Simplicidad (no requiere servidor)
- ✅ Focus en arquitectura (no en UI)
- ✅ Portable (cualquier terminal)
- ✅ Educativo (lógica visible)

**Trade-offs:**
- ⚠️ No multiusuario
- ⚠️ No escalable
- ⚠️ UX limitada

**Extensión Futura:**
- 🔄 REST API (mismos Use Cases)
- 🔄 Web UI (mismos Services)
- 🔄 Mobile App (mismos DTOs)

---

## Thread Safety

### ConcurrentHashMap en Repositorios

```java
// Thread-safe storage
private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();

// Operación atómica
public Usuario guardar(Usuario usuario) {
    usuarios.put(usuario.getId(), usuario);
    return usuario;
}

// Operación compuesta (verificar + guardar)
public Usuario guardarSiNoExiste(Usuario usuario) {
    return usuarios.putIfAbsent(usuario.getId(), usuario);
}
```

**Garantías:**
- ✅ Put/Get son atómicos
- ✅ No hay ConcurrentModificationException
- ✅ Iteradores débilmente consistentes

---

## Testing Strategy

### Pirámide de Tests

```
          /\
         /  \        4 Integration Tests
        /────\
       /      \      15 Infrastructure Tests
      /────────\
     /          \    14 Application Tests (Mockito)
    /────────────\
   /              \  52 Domain Tests
  /________________\
```

### Tipos de Tests

**Unit Tests (Domain + Application):**
- Sin dependencias externas
- Rápidos (milisegundos)
- Mockito para aislar

**Integration Tests (Infrastructure):**
- Con implementaciones reales
- Prueban interacción entre componentes
- Sin mocks

**End-to-End Tests:**
- Flujos completos de usuario
- Todos los componentes juntos
- Escenarios reales

---

## Conclusión

Esta arquitectura provee:

✅ **Mantenibilidad**: Código organizado, fácil de entender
✅ **Testabilidad**: 85 tests automatizados
✅ **Escalabilidad**: Fácil agregar features
✅ **Flexibilidad**: Cambio de tecnologías sin afectar negocio
✅ **Calidad**: Principios SOLID, patrones, clean code

---

**Última actualización**: 2025-12-01
**Versión**: 1.0
