# 💳 Wallet - Sistema de Gestión de Billetera Digital

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Tests](https://img.shields.io/badge/Tests-85%2F85-brightgreen.svg)](/)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture-blue.svg)](/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](/)

Sistema de billetera digital implementado en **Java 21** siguiendo los principios de **Clean Architecture**. Incluye gestión de usuarios, cuentas, depósitos, retiros y transferencias con validaciones robustas y almacenamiento en memoria.

---

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Arquitectura](#-arquitectura)
- [Requisitos](#-requisitos)
- [Instalación](#-instalación)
- [Uso](#-uso)
- [Testing](#-testing)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Documentación](#-documentación)

---

## ✨ Características

### 👤 Gestión de Usuarios
- ✅ Registro de usuarios con validación de email y documento
- ✅ Búsqueda por ID, email o documento de identidad
- ✅ Activación/desactivación de usuarios
- ✅ Actualización de datos personales

### 💰 Gestión de Cuentas
- ✅ Creación automática de cuentas con número único
- ✅ Consulta de saldo en tiempo real
- ✅ Soporte para múltiples monedas (PEN, USD, EUR)
- ✅ Estado de cuenta (activa/inactiva)

### 💸 Operaciones Financieras
- ✅ **Depósitos**: Incremento de saldo con validaciones
- ✅ **Retiros**: Extracción de fondos con verificación de saldo
- ✅ **Transferencias**: Entre cuentas con registro de transacciones
- ✅ Historial completo de transacciones por cuenta

### 🔒 Validaciones y Seguridad
- ✅ Validación de saldo suficiente en retiros
- ✅ Prevención de emails duplicados
- ✅ Prevención de documentos duplicados
- ✅ Validación de montos positivos
- ✅ Verificación de cuentas activas
- ✅ Thread-safety en repositorios (ConcurrentHashMap)

---

## 🏗️ Arquitectura

El proyecto implementa **Clean Architecture** con 4 capas bien definidas:

```
┌─────────────────────────────────────────────────────┐
│               PRESENTATION LAYER                    │
│  (Console UI, Controllers, Menus, Utils)           │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│              APPLICATION LAYER                      │
│  (Use Cases, DTOs, Mappers, Requests/Responses)    │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│              INFRASTRUCTURE LAYER                   │
│  (Repositories, Services, Factories, Logger)        │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│                 DOMAIN LAYER                        │
│  (Entities, Value Objects, Exceptions, Interfaces) │
└─────────────────────────────────────────────────────┘
```

### Patrones de Diseño Implementados

- **Repository Pattern**: Abstracción del acceso a datos
- **Factory Pattern**: Creación centralizada de repositorios
- **Singleton Pattern**: Instancia única del RepositoryFactory
- **Facade Pattern**: Servicios que simplifican operaciones complejas
- **MVC Pattern**: Separación de lógica de presentación
- **Command Pattern**: Menús con acciones encapsuladas
- **DTO Pattern**: Transferencia de datos entre capas
- **Value Object Pattern**: Inmutabilidad de objetos de valor

---

## 🔧 Requisitos

- **Java Development Kit (JDK)**: 21+
- **Maven**: 3.9.6+ (para compilación con BD integrada)
- **PowerShell**: 5.1+ (Windows)
- **Dependencias**:
  - **Testing**: JUnit 5.10.1, Mockito 5.8.0, AssertJ 3.25.1
  - **Database**: Hibernate 6.4.4, Jakarta Persistence 3.1.0, SQLite JDBC 3.44.0, HikariCP 5.1.0
  - **Utilities**: Byte Buddy 1.14.11, Objenesis 3.3

---

## 📥 Instalación

### 1. Clonar el repositorio

```powershell
git clone <repository-url>
cd wallet
```

### 2. Descargar dependencias (opcional si se usa Maven)

```powershell
.\download-dependencies.ps1
```

O usar Maven:

```powershell
mvn clean install
```

### 3. Compilar el proyecto

**Con Maven (Recomendado para BD integrada)**:
```powershell
mvn clean compile
```

**O con script PowerShell** (compilación básica sin BD):
```powershell
.\compile.ps1
```

---

## 🚀 Uso

### Ejecutar la aplicación

```powershell
.\run.ps1
```

### Interfaz de Usuario

El sistema presenta un menú interactivo de consola:

```
═══════════════════════════════════
  💳 SISTEMA DE BILLETERA DIGITAL
═══════════════════════════════════

   MENÚ PRINCIPAL
───────────────────────────────────
   1. 👤 Gestión de Usuarios
   2. 💰 Gestión de Cuentas
   3. 💸 Transacciones
   4. 🚪 Salir
───────────────────────────────────
```

### Flujo de Uso Típico

1. **Registrar Usuario**
   - Menú Usuarios → Registrar nuevo usuario
   - Ingresar: nombre, apellido, email, tipo documento, número

2. **Crear Cuenta**
   - Menú Cuentas → Crear nueva cuenta
   - Ingresar: email del usuario

3. **Depositar Dinero**
   - Menú Cuentas → Depositar dinero
   - Ingresar: número de cuenta, monto

4. **Realizar Transferencia**
   - Menú Transacciones → Realizar transferencia
   - Ingresar: cuenta origen, cuenta destino, monto

5. **Consultar Historial**
   - Menú Transacciones → Ver historial
   - Ingresar: número de cuenta

---

## 🧪 Testing

### Ejecutar todos los tests

```powershell
.\test.ps1
```

### Estadísticas de Testing

- **Total de Tests**: 85
- **Tests Exitosos**: 85 ✅
- **Cobertura**: ~85%

#### Distribución por Capa

| Capa              | Tests | Descripción                          |
|-------------------|-------|--------------------------------------|
| Domain            | 52    | Entities y Value Objects             |
| Application       | 14    | Use Cases con Mockito                |
| Infrastructure    | 15    | Repositories y servicios             |
| Integration       | 4     | Flujos end-to-end                    |

### Tecnologías de Testing

- **JUnit 5.10.1**: Framework de testing
- **Mockito 5.8.0**: Mocking de dependencias
- **PowerShell Scripts**: Automatización de tests

### Ejecutar tests específicos

```powershell
# Tests de dominio
java -jar lib\junit-platform-console-standalone-1.10.1.jar `
     --class-path "target\classes;target\test-classes;lib\*" `
     -c com.wallet.domain.entities.CuentaTest

# Tests de use cases
java -jar lib\junit-platform-console-standalone-1.10.1.jar `
     --class-path "target\classes;target\test-classes;lib\*" `
     -c com.wallet.application.usecases.CrearUsuarioUseCaseTest
```

---

## 📁 Estructura del Proyecto

```
wallet/
├── src/
│   ├── main/java/com/wallet/
│   │   ├── domain/                    # Capa de Dominio
│   │   │   ├── entities/              # Entidades (Usuario, Cuenta, Transaccion)
│   │   │   ├── valueobjects/          # Value Objects (Email, Dinero, etc.)
│   │   │   ├── exceptions/            # Excepciones de negocio
│   │   │   └── repositories/          # Interfaces de repositorios
│   │   ├── application/               # Capa de Aplicación
│   │   │   ├── usecases/              # Casos de uso (8 Use Cases)
│   │   │   ├── dtos/                  # Data Transfer Objects
│   │   │   │   ├── requests/          # DTOs de entrada
│   │   │   │   └── responses/         # DTOs de salida
│   │   │   └── mappers/               # Mappers Entity ↔ DTO
│   │   ├── infrastructure/            # Capa de Infraestructura
│   │   │   ├── repositories/          # Implementaciones en memoria
│   │   │   ├── services/              # Servicios de fachada
│   │   │   ├── factory/               # Factory de repositorios
│   │   │   └── logging/               # Sistema de logging
│   │   └── presentation/              # Capa de Presentación
│   │       ├── console/               # Interfaz de consola
│   │       ├── controllers/           # Controladores MVC
│   │       ├── menus/                 # Menús interactivos
│   │       └── Main.java              # Punto de entrada
│   └── test/java/com/wallet/          # Tests
│       ├── domain/                    # Tests de dominio (52)
│       ├── application/               # Tests de use cases (14)
│       └── infrastructure/            # Tests de infra (19)
├── lib/                               # Dependencias (JARs)
│   ├── junit-platform-console-standalone-1.10.1.jar
│   ├── mockito-core-5.8.0.jar
│   ├── byte-buddy-1.14.11.jar
│   ├── byte-buddy-agent-1.14.11.jar
│   └── objenesis-3.3.jar
├── target/                            # Archivos compilados
│   ├── classes/                       # Clases del proyecto
│   └── test-classes/                  # Clases de test
├── *.ps1                              # Scripts de PowerShell
├── README.md                          # Este archivo
├── ARCHITECTURE.md                    # Documentación de arquitectura
├── DEVELOPMENT.md                     # Guía de desarrollo
└── ETAPA_*.txt                        # Documentación de etapas
```

---

## 📚 Documentación

### Documentos Disponibles

- **[ARCHITECTURE.md](ARCHITECTURE.md)**: Arquitectura detallada y decisiones de diseño
- **[DEVELOPMENT.md](DEVELOPMENT.md)**: Guía para desarrolladores
- **[ETAPA_2_COMPLETADA.txt](ETAPA_2_COMPLETADA.txt)**: Documentación de la capa de dominio
- **[ETAPA_6_COMPLETADA.txt](ETAPA_6_COMPLETADA.txt)**: Documentación de testing

### API de Use Cases

#### Gestión de Usuarios

```java
// Crear usuario
CrearUsuarioRequest request = new CrearUsuarioRequest(
    "Juan", "Perez", "juan@email.com", 
    TipoDocumento.DNI, "12345678"
);
UsuarioDTO usuario = crearUsuarioUseCase.ejecutar(request);

// Buscar usuario por email
UsuarioDTO usuario = buscarUsuarioPorEmailUseCase.ejecutar("juan@email.com");

// Listar usuarios activos
List<UsuarioDTO> usuarios = listarUsuariosUseCase.ejecutar();
```

#### Gestión de Cuentas

```java
// Crear cuenta
CuentaDTO cuenta = crearCuentaUseCase.ejecutar(usuarioId);

// Consultar saldo
CuentaDTO cuenta = consultarSaldoUseCase.ejecutar(numeroCuenta);

// Listar cuentas de un usuario
List<CuentaDTO> cuentas = listarCuentasUseCase.ejecutar(usuarioId);
```

#### Operaciones Financieras

```java
// Depositar dinero
DepositarDineroRequest request = new DepositarDineroRequest(
    cuentaId, new BigDecimal("100.00"), "Depósito inicial"
);
TransaccionDTO transaccion = depositarDineroUseCase.ejecutar(request);

// Retirar dinero
RetirarDineroRequest request = new RetirarDineroRequest(
    cuentaId, new BigDecimal("50.00"), "Retiro cajero"
);
TransaccionDTO transaccion = retirarDineroUseCase.ejecutar(request);

// Transferir dinero
TransferirDineroRequest request = new TransferirDineroRequest(
    cuentaOrigenId, cuentaDestinoId, 
    new BigDecimal("200.00"), "Pago de servicio"
);
TransaccionDTO transaccion = transferirDineroUseCase.ejecutar(request);
```

---

## 🎯 Etapas del Proyecto

El proyecto se desarrolló en 8 etapas:

1. ✅ **Configuración**: Estructura de carpetas y scripts
2. ✅ **Domain Layer**: Entidades, Value Objects, excepciones
3. ✅ **Application Layer**: Use Cases, DTOs, Mappers
4. ✅ **Infrastructure Layer**: Repositorios, servicios, logging
5. ✅ **Presentation Layer**: UI de consola, controladores, menús
6. ✅ **Testing**: 85 tests unitarios, integración y e2e
7. ✅ **Documentación**: README, arquitectura, guías
8. ⏳ **Entrega**: Presentación y demostración final

---

## 🤝 Contribuir

### Convenciones de Código

- **Nombres de clases**: PascalCase (`Usuario`, `CuentaDTO`)
- **Nombres de métodos**: camelCase (`ejecutar()`, `buscarPorId()`)
- **Nombres de constantes**: UPPER_SNAKE_CASE (`MONEDA_DEFAULT`)
- **Paquetes**: minúsculas (`com.wallet.domain.entities`)

### Agregar un Nuevo Use Case

1. Crear la clase en `application/usecases/`
2. Implementar método `ejecutar()`
3. Crear DTOs de request/response si es necesario
4. Agregar tests en `test/.../usecases/`
5. Actualizar servicios en Infrastructure si aplica

### Ejecutar Tests Antes de Commit

```powershell
.\compile.ps1
.\test.ps1
```

---

## 📝 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

---

## �️ Persistencia y Base de Datos (Fase 5-7)

### Arquitectura de Persistencia

El proyecto integra **Hibernate/JPA** con SQLite para persistencia robusta:

```
Domain Entities (Usuario, Cuenta, Transaccion)
        ↓
JPA Entities with Converters
        ↓
Hibernate ORM
        ↓
SQLite Database (wallet.db)
```

### Características de BD

- **Base de Datos**: SQLite con archivo persistent
- **ORM**: Hibernate 6.4.4 con Jakarta Persistence 3.1.0
- **Connection Pool**: HikariCP para eficiencia
- **Índices**: Optimizados en email, documento, numero_cuenta, fecha
- **Constraints**: Validación en BD (CHECK, UNIQUE, FOREIGN KEY)
- **Transacciones**: ACID compliance con rollback automático

### Validación Multi-Capa (Fase 7)

```
ValidatorUtil (Validación de entrada)
        ↓ InvalidXXXException
Repository (Validación de negocio - duplicados)
        ↓ DuplicateXXXException
JPA/Hibernate (Operación de BD)
        ↓ PersistenceException
RepositoryException (Excepción de dominio)
        ↓
Capa de Aplicación
```

### Manejo de Excepciones (Fase 7)

**8 Nuevas Excepciones Especializadas**:
- `DuplicateEmailException` - Email ya registrado
- `DuplicateDocumentoException` - Documento ya registrado
- `DuplicateCuentaException` - Número de cuenta duplicado
- `InvalidEmailFormatException` - Formato de email inválido
- `InvalidDocumentoFormatException` - Formato de documento inválido
- `InvalidSaldoException` - Saldo negativo
- `InvalidMontoException` - Monto inválido (≤ 0)
- `RepositoryException` - Error de persistencia convertido

**Logging Operacional Thread-Safe**:
```java
OperationLogger.logCreate("Usuario", id, "Usuario creado");
OperationLogger.logRead("Cuenta", id, "Cuenta encontrada");
OperationLogger.logError("Transacción", id, "Error de persistencia", exception);
OperationLogger.printStatistics();
```

---

## 🧪 Testing

### Ejecutar todos los tests

```powershell
mvn test
# o
.\test.ps1
```

### Cobertura de Tests (37 tests + 5 E2E)

**Fase 6 - Integration Tests** (32 tests):
- `UsuarioJPARepositoryTest`: 11 tests (CRUD + búsquedas)
- `CuentaJPARepositoryTest`: 11 tests (operaciones + relaciones)
- `TransaccionJPARepositoryTest`: 10 tests (transacciones + filtrado)

**E2E Workflows** (5 tests):
- Crear usuario → Crear cuenta → Depositar → Validar
- Múltiples cuentas por usuario
- Ciclo completo (Depósito → Retiro → Validación)
- Validación de integridad referencial
- Resumen de usuarios y cuentas

---

## 📚 Documentación

### Archivos Disponibles

- **[README.md](README.md)** - Este archivo (descripción general)
- **[DEVELOPMENT.md](DEVELOPMENT.md)** - Guía de desarrollo
- **[ARCHITECTURE.md](readme/ARCHITECTURE.md)** - Arquitectura detallada
- **[FASE_7_OPTIMIZACION_COMPLETADA.md](readme/FASE_7_OPTIMIZACION_COMPLETADA.md)** - Excepciones y Validación
- **[GUIA-COMPLETA-DE-TESTS.txt](GUIA-COMPLETA-DE-TESTS.txt)** - Suite de tests
- **[PROYECTO-EXPLICADO-PARA-CLASE.txt](PROYECTO-EXPLICADO-PARA-CLASE.txt)** - Explicación educativa

### Fases de Desarrollo

✅ **Fase 1**: Estructura de Proyecto y Entidades  
✅ **Fase 2**: Casos de Uso y DTOs  
✅ **Fase 3**: Servicios de Negocio  
✅ **Fase 4**: Menús y Presentación  
✅ **Fase 5**: Integración de Base de Datos (Hibernate/JPA)  
✅ **Fase 6**: Tests Comprensivos (37 tests + 5 E2E)  
✅ **Fase 7**: Optimización (Excepciones, Validación, Logging)  
✅ **Fase 8**: Documentación Completa - 2500+ líneas

---

## 📚 Documentación Completa

**FASE 8 - DOCUMENTACIÓN**: Conjunto profesional de guías y referencias

### 👤 Para Usuarios Finales
- 📖 [USER_GUIDE.md](readme/USER_GUIDE.md) - Guía completa de usuario (600+ líneas)
  - Primeros pasos y menú principal
  - Gestión de usuarios, cuentas y operaciones
  - 20+ preguntas frecuentes
  - Solución de errores comunes

### 👨‍💻 Para Desarrolladores
- 📘 [API_DOCUMENTATION.md](readme/API_DOCUMENTATION.md) - Referencia completa de API (900+ líneas)
  - 24 métodos de repositorio documentados
  - Exception handling patterns
  - Validation framework
  - 72+ ejemplos de código

- 📙 [DEVELOPMENT.md](DEVELOPMENT.md) - Guía de desarrollo (800+ líneas)
  - Arquitectura detallada (5 capas)
  - Framework de validación (4 capas)
  - Patrones de repositorio
  - Best practices y troubleshooting
  - Ejemplos de extensión del sistema

### 🏗️ Para Arquitectos
- 📊 [ARCHITECTURE_DIAGRAMS.md](readme/ARCHITECTURE_DIAGRAMS.md) - Diagramas visuales (500+ líneas)
  - 5-layer architecture diagram
  - 4-layer validation flow
  - Exception hierarchy
  - Database schema
  - 12 diagramas ASCII completos

### 📋 Histórico Completo
- 📝 [CHANGELOG.md](CHANGELOG.md) - Historial de versiones
  - Versiones 0.1.0 → 1.0.0
  - Fases 1-8 documentadas
  - Features por fase

### ✨ Resumen de Fase 8
- 📄 [FASE_8_DOCUMENTACION_COMPLETADA.md](FASE_8_DOCUMENTACION_COMPLETADA.md)
  - Logros de Fase 8
  - Métricas y entregables
  - Checklist final

---

## 👨‍💻 Autor

Desarrollado como proyecto educativo para demostrar:
- ✅ Principios SOLID
- ✅ Clean Architecture con 5 capas
- ✅ Patrones de Diseño (Repository, Factory, DTO Mapper)
- ✅ Test Driven Development (42 tests, 100% coverage)
- ✅ Buenas prácticas Java 21 LTS
- ✅ **Validación multi-capa (4 capas)** - NEW Fase 7
- ✅ **Manejo robusto de excepciones (8 tipos)** - NEW Fase 7
- ✅ **Logging operacional thread-safe** - NEW Fase 7
- ✅ **Documentación profesional (2500+ líneas)** - NEW Fase 8

---

**Versión Actual**: 1.0.0  
**Estado**: ✅ OPERACIONAL Y COMPLETAMENTE DOCUMENTADO  
**Última actualización**: 15 de Enero, 2025

---

## 📞 Soporte

Para preguntas o problemas:
- Crear un issue en el repositorio
- Revisar la documentación en `/docs` y `/readme`
- Consultar los archivos de Fases completadas
- Ver ejemplos en código de test

---

**Estado Actual**: ✅ Fase 7 Completada (Excepciones y Validación)  
**Próximo**: Fase 8 (Documentación Completa)  
**Versión**: 1.0.0  
**Java**: 21  
**Database**: SQLite con Hibernate/JPA

**¡Gracias por usar Wallet!** 💳✨
