# 🔧 DEVELOPMENT GUIDE - Sistema Wallet

Guía completa para desarrolladores que deseen contribuir o extender el sistema Wallet.

---

## 📋 Requisitos de Desarrollo

### Herramientas Necesarias

- **JDK 21**: Java Development Kit
- **PowerShell 5.1+**: Para scripts de compilación
- **Git**: Control de versiones
- **Editor de Código**: IntelliJ IDEA, VS Code, o similar

### Dependencias del Proyecto

```
lib/
├── junit-platform-console-standalone-1.10.1.jar  # Testing
├── mockito-core-5.8.0.jar                        # Mocking
├── byte-buddy-1.14.11.jar                        # Mockito dependency
├── byte-buddy-agent-1.14.11.jar                  # Mockito dependency
└── objenesis-3.3.jar                             # Mockito dependency
```

---

## 🚀 Setup Inicial

### 1. Clonar y Preparar

```powershell
# Clonar repositorio
git clone <repository-url>
cd wallet

# Descargar dependencias
.\download-dependencies.ps1

# Compilar proyecto
.\compile.ps1

# Ejecutar tests
.\test.ps1

# Ejecutar aplicación
.\run.ps1
```

### 2. Estructura de Carpetas

```
wallet/
├── src/
│   ├── main/java/com/wallet/      # Código fuente
│   └── test/java/com/wallet/      # Tests
├── target/                        # Archivos compilados
├── lib/                          # Dependencias JAR
└── *.ps1                         # Scripts de automatización
```

---

## 📝 Convenciones de Código

### Naming Conventions

```java
// Clases: PascalCase
public class Usuario { }
public class CrearUsuarioUseCase { }

// Métodos: camelCase
public void ejecutar() { }
public Usuario buscarPorId(String id) { }

// Constantes: UPPER_SNAKE_CASE
public static final String MONEDA_DEFAULT = "PEN";
public static final int MAX_LONGITUD_NOMBRE = 100;

// Paquetes: minúsculas
package com.wallet.domain.entities;
package com.wallet.application.usecases;

// Variables: camelCase
private String nombre;
private BigDecimal saldoInicial;
```

### Organización de Imports

```java
// 1. java.*
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// 2. javax.*
import javax.validation.constraints.NotNull;

// 3. Librerías externas
import org.junit.jupiter.api.Test;

// 4. Paquetes del proyecto
import com.wallet.domain.entities.Usuario;
import com.wallet.domain.valueobjects.Email;
```

### Formato de Código

```java
// Llaves en la misma línea
public class Usuario {
    // ...
}

// Indentación: 4 espacios
public void metodo() {
    if (condicion) {
        // código
    }
}

// Líneas: máximo 100 caracteres
public UsuarioDTO crearUsuario(String nombre, String apellido, 
                                Email email, DocumentoIdentidad doc) {
    // ...
}

// Comentarios Javadoc para clases y métodos públicos
/**
 * Crea un nuevo usuario en el sistema.
 * 
 * @param request Datos del usuario a crear
 * @return DTO con la información del usuario creado
 * @throws OperacionNoValidaException si el email ya existe
 */
public UsuarioDTO ejecutar(CrearUsuarioRequest request) {
    // ...
}
```

---

## 🏗️ Agregar Nuevas Características

### 1. Agregar Nueva Entidad

**Paso 1**: Crear entidad en `domain/entities/`

```java
package com.wallet.domain.entities;

public class Tarjeta {
    private final String id;
    private final String numeroCuenta;
    private String numero;
    private LocalDate fechaExpiracion;
    private boolean activa;
    
    public Tarjeta(String numeroCuenta) {
        this.id = UUID.randomUUID().toString();
        this.numeroCuenta = numeroCuenta;
        this.numero = generarNumero();
        this.activa = true;
    }
    
    // Lógica de negocio
    public void activar() { this.activa = true; }
    public void bloquear() { this.activa = false; }
    
    // Getters
}
```

**Paso 2**: Crear value objects si es necesario

**Paso 3**: Crear excepciones específicas

**Paso 4**: Crear tests de dominio

```java
package com.wallet.domain.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TarjetaTest {
    @Test
    void deberiaCrearTarjetaActiva() {
        Tarjeta tarjeta = new Tarjeta("12345");
        assertTrue(tarjeta.isActiva());
        assertNotNull(tarjeta.getNumero());
    }
}
```

---

### 2. Agregar Nuevo Use Case

**Paso 1**: Crear interfaz de repositorio (si no existe)

```java
// domain/repositories/ITarjetaRepository.java
package com.wallet.domain.repositories;

public interface ITarjetaRepository {
    Tarjeta guardar(Tarjeta tarjeta);
    Optional<Tarjeta> buscarPorId(String id);
    List<Tarjeta> buscarPorCuenta(String numeroCuenta);
}
```

**Paso 2**: Crear DTO

```java
// application/dtos/TarjetaDTO.java
package com.wallet.application.dtos;

public class TarjetaDTO {
    private String id;
    private String numero;
    private String numeroCuenta;
    private boolean activa;
    
    // Constructor, getters, setters
}
```

**Paso 3**: Crear request DTO

```java
// application/dtos/requests/CrearTarjetaRequest.java
package com.wallet.application.dtos.requests;

public class CrearTarjetaRequest {
    private String numeroCuenta;
    
    // Constructor, getters, setters
}
```

**Paso 4**: Crear mapper

```java
// application/mappers/TarjetaMapper.java
package com.wallet.application.mappers;

public class TarjetaMapper {
    public static TarjetaDTO toDTO(Tarjeta tarjeta) {
        return new TarjetaDTO(
            tarjeta.getId(),
            tarjeta.getNumero(),
            tarjeta.getNumeroCuenta(),
            tarjeta.isActiva()
        );
    }
}
```

**Paso 5**: Crear use case

```java
// application/usecases/CrearTarjetaUseCase.java
package com.wallet.application.usecases;

public class CrearTarjetaUseCase {
    private final ITarjetaRepository tarjetaRepository;
    private final ICuentaRepository cuentaRepository;
    
    public CrearTarjetaUseCase(ITarjetaRepository tarjetaRepository,
                                ICuentaRepository cuentaRepository) {
        this.tarjetaRepository = tarjetaRepository;
        this.cuentaRepository = cuentaRepository;
    }
    
    public TarjetaDTO ejecutar(CrearTarjetaRequest request) {
        // 1. Validar request
        if (request.getNumeroCuenta() == null) {
            throw new IllegalArgumentException("Número de cuenta requerido");
        }
        
        // 2. Verificar que la cuenta existe
        Cuenta cuenta = cuentaRepository
            .buscarPorNumero(request.getNumeroCuenta())
            .orElseThrow(() -> new CuentaNoEncontradaException(
                request.getNumeroCuenta()
            ));
        
        // 3. Verificar reglas de negocio
        if (!cuenta.isActiva()) {
            throw new OperacionNoValidaException(
                "No se puede crear tarjeta en cuenta inactiva"
            );
        }
        
        // 4. Crear entidad
        Tarjeta tarjeta = new Tarjeta(cuenta.getNumeroCuenta());
        
        // 5. Guardar
        Tarjeta guardada = tarjetaRepository.guardar(tarjeta);
        
        // 6. Retornar DTO
        return TarjetaMapper.toDTO(guardada);
    }
}
```

**Paso 6**: Crear tests con Mockito

```java
// test/.../usecases/CrearTarjetaUseCaseTest.java
package com.wallet.application.usecases;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class CrearTarjetaUseCaseTest {
    private ITarjetaRepository tarjetaRepository;
    private ICuentaRepository cuentaRepository;
    private CrearTarjetaUseCase useCase;
    
    @BeforeEach
    void setUp() {
        tarjetaRepository = mock(ITarjetaRepository.class);
        cuentaRepository = mock(ICuentaRepository.class);
        useCase = new CrearTarjetaUseCase(
            tarjetaRepository, cuentaRepository
        );
    }
    
    @Test
    void debeCrearTarjetaExitosamente() {
        // Arrange
        Cuenta cuenta = new Cuenta("usr-123");
        when(cuentaRepository.buscarPorNumero("12345"))
            .thenReturn(Optional.of(cuenta));
        when(tarjetaRepository.guardar(any(Tarjeta.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        
        CrearTarjetaRequest request = 
            new CrearTarjetaRequest("12345");
        
        // Act
        TarjetaDTO resultado = useCase.ejecutar(request);
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isActiva());
        verify(tarjetaRepository).guardar(any(Tarjeta.class));
    }
}
```

**Paso 7**: Implementar repositorio

```java
// infrastructure/repositories/TarjetaRepositoryInMemory.java
package com.wallet.infrastructure.repositories;

public class TarjetaRepositoryInMemory implements ITarjetaRepository {
    private final Map<String, Tarjeta> tarjetas = 
        new ConcurrentHashMap<>();
    
    @Override
    public Tarjeta guardar(Tarjeta tarjeta) {
        tarjetas.put(tarjeta.getId(), tarjeta);
        return tarjeta;
    }
    
    @Override
    public Optional<Tarjeta> buscarPorId(String id) {
        return Optional.ofNullable(tarjetas.get(id));
    }
    
    @Override
    public List<Tarjeta> buscarPorCuenta(String numeroCuenta) {
        return tarjetas.values().stream()
            .filter(t -> t.getNumeroCuenta().equals(numeroCuenta))
            .collect(Collectors.toList());
    }
    
    public void limpiar() {
        tarjetas.clear();
    }
}
```

**Paso 8**: Actualizar RepositoryFactory

```java
// infrastructure/factory/RepositoryFactory.java
private final ITarjetaRepository tarjetaRepository;

private RepositoryFactory() {
    // ...
    this.tarjetaRepository = new TarjetaRepositoryInMemory();
}

public ITarjetaRepository getTarjetaRepository() {
    return tarjetaRepository;
}
```

**Paso 9**: Crear servicio (opcional)

```java
// infrastructure/services/TarjetaService.java
public class TarjetaService {
    private final CrearTarjetaUseCase crearTarjetaUseCase;
    
    public TarjetaService() {
        RepositoryFactory factory = RepositoryFactory.getInstance();
        this.crearTarjetaUseCase = new CrearTarjetaUseCase(
            factory.getTarjetaRepository(),
            factory.getCuentaRepository()
        );
    }
    
    public TarjetaDTO crearTarjeta(String numeroCuenta) {
        return crearTarjetaUseCase.ejecutar(
            new CrearTarjetaRequest(numeroCuenta)
        );
    }
}
```

**Paso 10**: Agregar controller y menú

```java
// presentation/controllers/TarjetaController.java
public class TarjetaController {
    private final TarjetaService tarjetaService;
    
    public void crearTarjeta() {
        try {
            String numeroCuenta = ConsoleUtils.readLine(
                "Número de cuenta: "
            );
            
            TarjetaDTO tarjeta = tarjetaService.crearTarjeta(numeroCuenta);
            
            ConsoleUtils.printSuccess(
                "Tarjeta creada: " + tarjeta.getNumero()
            );
        } catch (Exception e) {
            ConsoleUtils.printError("Error: " + e.getMessage());
        }
    }
}
```

---

## 🧪 Testing

### Estructura de Tests

```
test/java/com/wallet/
├── domain/
│   ├── entities/           # Tests de entidades
│   └── valueobjects/       # Tests de value objects
├── application/
│   └── usecases/           # Tests con Mockito
└── infrastructure/
    ├── repositories/       # Tests de integración
    └── integration/        # Tests end-to-end
```

### Escribir Tests Efectivos

**Tests de Dominio:**
```java
@Test
@DisplayName("Debe lanzar excepción si email es inválido")
void debeLanzarExcepcionSiEmailInvalido() {
    assertThrows(EmailInvalidoException.class, 
        () -> new Email("invalido"));
}
```

**Tests de Use Cases:**
```java
@Test
@DisplayName("Debe crear usuario exitosamente")
void debeCrearUsuarioExitosamente() {
    // Arrange
    when(usuarioRepository.existePorEmail(any()))
        .thenReturn(false);
    when(usuarioRepository.guardar(any()))
        .thenAnswer(inv -> inv.getArgument(0));
    
    // Act
    UsuarioDTO resultado = useCase.ejecutar(request);
    
    // Assert
    assertNotNull(resultado);
    verify(usuarioRepository).guardar(any(Usuario.class));
}
```

**Tests de Integración:**
```java
@Test
@DisplayName("Debe guardar y recuperar usuario")
void debeGuardarYRecuperarUsuario() {
    // Arrange
    Usuario usuario = new Usuario(...);
    
    // Act
    Usuario guardado = repository.guardar(usuario);
    Optional<Usuario> recuperado = repository.buscarPorId(
        guardado.getId()
    );
    
    // Assert
    assertTrue(recuperado.isPresent());
    assertEquals(guardado.getId(), recuperado.get().getId());
}
```

---

## 🐛 Debugging

### Logging

Usar la clase Logger para depuración:

```java
import com.wallet.infrastructure.logging.Logger;

Logger.info("Creando usuario: " + email);
Logger.debug("Validando datos...");
Logger.error("Error al crear usuario: " + e.getMessage());
Logger.warning("Email ya existe: " + email);
```

### Puntos de Breakpoint Comunes

1. **Use Cases**: Inicio del método `ejecutar()`
2. **Repositorios**: Métodos `guardar()` y `buscarPorId()`
3. **Controllers**: Manejo de excepciones
4. **Entities**: Métodos de validación

---

## 📦 Build y Deploy

### Compilar Proyecto

```powershell
# Compilar código fuente
.\compile.ps1

# Compilar y ejecutar tests
.\test.ps1

# Limpiar compilados
Remove-Item -Recurse target\
```

### Empaquetar Aplicación

```powershell
# Crear JAR ejecutable (futuro)
jar cvfe wallet.jar com.wallet.presentation.Main -C target/classes .

# Ejecutar JAR
java -jar wallet.jar
```

---

## 🔄 Git Workflow

### Branching Strategy

```
main (producción)
  ↓
develop (desarrollo)
  ↓
feature/nueva-caracteristica
feature/fix-bug
```

### Commits

```bash
# Formato de commit
git commit -m "tipo: descripción breve

Descripción detallada si es necesario.

Relacionado con: #123"

# Tipos:
# feat: Nueva característica
# fix: Corrección de bug
# refactor: Refactorización
# test: Agregar/modificar tests
# docs: Documentación
# style: Formato de código
```

### Pull Requests

1. Crear branch desde `develop`
2. Hacer cambios y commits
3. Ejecutar tests: `.\test.ps1`
4. Push y crear PR
5. Code review
6. Merge a `develop`

---

## 📚 Recursos

### Documentación

- [README.md](README.md): Guía general
- [ARCHITECTURE.md](ARCHITECTURE.md): Arquitectura detallada
- [ETAPA_*.txt](ETAPA_2_COMPLETADA.txt): Documentación de etapas

### Referencias

- **Clean Architecture**: Robert C. Martin
- **Domain-Driven Design**: Eric Evans
- **Effective Java**: Joshua Bloch
- **Java 21 Documentation**: https://docs.oracle.com/en/java/

---

## ❓ FAQ

**Q: ¿Cómo agrego una nueva dependencia?**
A: Descargar JAR y agregarlo a `lib/`, actualizar `compile.ps1` y `test.ps1`

**Q: ¿Cómo cambio el almacenamiento a base de datos?**
A: Crear nueva implementación de repositorios, actualizar RepositoryFactory

**Q: ¿Puedo usar Maven/Gradle?**
A: Sí, crear `pom.xml` o `build.gradle` manteniendola estructura de carpetas

**Q: ¿Cómo agrego una API REST?**
A: Agregar Spring Boot, crear controllers REST que usen los mismos Use Cases

---

**Última actualización**: 2025-12-01
**Versión**: 1.0
