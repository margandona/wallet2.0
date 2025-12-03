# Plan de Desarrollo - Proyecto Wallet (Billetera Digital)

## Arquitectura y Principios

Este proyecto seguirá:
- **Clean Architecture** (Capas: Domain, Application, Infrastructure, Presentation)
- **Principios SOLID**
- **Las 4 Reglas del Diseño Simple**
- **Buenas Prácticas POO**

---

## 🎯 ETAPA 1: Configuración Inicial y Estructura del Proyecto ✅ COMPLETADA

### Objetivos
- Crear la estructura base del proyecto
- Configurar herramientas de build
- Establecer estándares de código

### Tareas
1. **Estructura de directorios** ✅
   ```
   wallet/
   ├── src/
   │   ├── main/
   │   │   └── java/
   │   │       └── com/wallet/
   │   │           ├── domain/          # Entidades y reglas de negocio
   │   │           ├── application/     # Casos de uso
   │   │           ├── infrastructure/  # Implementaciones técnicas
   │   │           └── presentation/    # UI/Controladores
   │   └── test/
   │       └── java/
   └── pom.xml / build.gradle
   ```

2. **Configuración de Maven/Gradle** ✅
   - Dependencias necesarias (JUnit 5, Mockito, AssertJ)
   - Plugins de calidad de código (JaCoCo)
   - Configuración de tests

3. **Configuración de herramientas** ✅
   - JaCoCo (cobertura de tests)
   - Scripts PowerShell para compilar sin Maven
   - Configuración de Git (.gitignore)

### Entregables
- ✅ Estructura de carpetas creada
- ✅ Archivos de configuración (pom.xml)
- ✅ README.md inicial con documentación completa
- ✅ .gitignore configurado
- ✅ Main.java funcional
- ✅ Scripts de compilación y ejecución (compile.ps1, run.ps1, build-and-run.ps1)

### Duración Real: 1 día ✅

**Estado**: ✅ **COMPLETADA** - Todas las tareas finalizadas exitosamente

---

## 🎯 ETAPA 2: Capa de Dominio (Domain Layer) ✅ COMPLETADA

### Objetivos
- Definir las entidades del negocio
- Crear value objects
- Establecer interfaces de repositorios
- Definir excepciones de dominio

### Tareas

1. **Entidades Core (Entities)** ✅
   ```java
   - Usuario ✅
   - Cuenta (Account) ✅
   - Transacción (Transaction) ✅
   ```

2. **Value Objects** ✅
   ```java
   - Email ✅
   - DocumentoIdentidad ✅
   - Dinero (Money) - con moneda ✅
   - TipoTransaccion (Enum) ✅
   ```

3. **Interfaces de Repositorio (Ports)** ✅
   ```java
   - IUsuarioRepository ✅
   - ICuentaRepository ✅
   - ITransaccionRepository ✅
   ```

4. **Excepciones de Dominio** ✅
   ```java
   - SaldoInsuficienteException ✅
   - CuentaNoEncontradaException ✅
   - UsuarioNoEncontradoException ✅
   - OperacionNoValidaException ✅
   ```

### Principios Aplicados
- **SRP**: Cada entidad tiene una única responsabilidad ✅
- **OCP**: Entidades cerradas a modificación, abiertas a extensión ✅
- **DIP**: Dependencias hacia abstracciones (interfaces) ✅

### Entregables
- ✅ Entidades del dominio implementadas (3 entidades)
- ✅ Value objects con validaciones (4 value objects)
- ✅ Interfaces de repositorios (3 interfaces)
- ✅ Tests unitarios de entidades (52 tests, 100% exitosos, >95% cobertura)

### Duración Real: 2 días ✅

**Estado**: ✅ **COMPLETADA** - Ver ETAPA_2_COMPLETADA.md para detalles completos

---

## 🎯 ETAPA 3: Capa de Aplicación (Application Layer)

### Objetivos
- Implementar casos de uso
- Definir DTOs
- Crear servicios de aplicación

### Tareas

1. **Casos de Uso (Use Cases)**
   ```java
   - CrearUsuarioUseCase
   - CrearCuentaUseCase
   - RealizarDepositoUseCase
   - RealizarRetiroUseCase
   - RealizarTransferenciaUseCase
   - ConsultarSaldoUseCase
   - ConsultarHistorialUseCase
   ```

2. **DTOs (Data Transfer Objects)**
   ```java
   - UsuarioDTO
   - CuentaDTO
   - TransaccionDTO
   - RequestDTO / ResponseDTO para cada operación
   ```

3. **Validadores**
   ```java
   - ValidadorTransferencia
   - ValidadorMontos
   - ValidadorDatosUsuario
   ```

4. **Interfaces de Servicios Externos**
   ```java
   - INotificacionService
   - IValidacionIdentidadService
   ```

### Principios Aplicados
- **SRP**: Un caso de uso por responsabilidad
- **ISP**: Interfaces segregadas
- **DIP**: Dependencia en abstracciones

### Entregables
- ✅ Casos de uso implementados
- ✅ DTOs creados
- ✅ Tests unitarios de casos de uso
- ✅ Documentación de flujos

### Duración Estimada: 4-5 días

---

## 🎯 ETAPA 4: Capa de Infraestructura (Infrastructure Layer)

### Objetivos
- Implementar repositorios
- Configurar persistencia
- Implementar servicios externos

### Tareas

1. **Implementación de Repositorios**
   ```java
   - UsuarioRepositoryImpl (puede ser In-Memory o DB)
   - CuentaRepositoryImpl
   - TransaccionRepositoryImpl
   ```

2. **Configuración de Persistencia**
   - Opción A: En memoria (HashMap, ArrayList)
   - Opción B: Base de datos (H2, MySQL, PostgreSQL)
   - Configuración de JPA/Hibernate (si aplica)

3. **Implementación de Servicios**
   ```java
   - NotificacionServiceImpl (email, SMS)
   - LoggerServiceImpl
   - ValidacionIdentidadServiceImpl
   ```

4. **Configuración**
   ```java
   - DatabaseConfig
   - ApplicationConfig
   - DependencyInjectionConfig
   ```

### Principios Aplicados
- **DIP**: Implementaciones concretas de interfaces
- **OCP**: Fácil cambio de implementaciones
- **LSP**: Sustitución de implementaciones

### Entregables
- ✅ Repositorios implementados
- ✅ Persistencia configurada
- ✅ Servicios implementados
- ✅ Tests de integración

### Duración Estimada: 3-4 días

---

## 🎯 ETAPA 5: Capa de Presentación (Presentation Layer)

### Objetivos
- Crear interfaz de usuario
- Implementar controladores
- Gestionar entrada/salida

### Tareas

1. **Interfaz de Usuario**
   - Opción A: Consola (CLI)
   - Opción B: GUI (Swing/JavaFX)
   - Opción C: REST API (Spring Boot)

2. **Controladores/Presentadores**
   ```java
   - MenuPrincipalController
   - UsuarioController
   - CuentaController
   - TransaccionController
   ```

3. **Manejo de Entrada/Salida**
   ```java
   - InputValidator
   - OutputFormatter
   - ExceptionHandler
   ```

4. **Menús y Navegación**
   ```
   - Menú Principal
   - Menú de Cuenta
   - Menú de Transacciones
   - Menú de Consultas
   ```

### Principios Aplicados
- **SRP**: Separación UI/lógica
- **DIP**: Controllers dependen de casos de uso
- **ISP**: Interfaces específicas

### Entregables
- ✅ Interfaz funcional
- ✅ Controladores implementados
- ✅ Navegación completa
- ✅ Tests de UI

### Duración Estimada: 3-4 días

---

## 🎯 ETAPA 6: Testing Integral

### Objetivos
- Completar cobertura de tests
- Realizar pruebas de integración
- Pruebas end-to-end

### Tareas

1. **Tests Unitarios**
   - Dominio: >90% cobertura
   - Aplicación: >85% cobertura
   - Infraestructura: >70% cobertura

2. **Tests de Integración**
   - Flujos completos
   - Integración BD
   - Servicios externos

3. **Tests End-to-End**
   - Casos de uso completos
   - Escenarios de usuario real

4. **Tests de Casos Límite**
   - Validaciones
   - Excepciones
   - Concurrencia (si aplica)

### Herramientas
- JUnit 5
- Mockito
- AssertJ
- TestContainers (si usa BD)

### Entregables
- ✅ Suite completa de tests
- ✅ Reporte de cobertura
- ✅ Documentación de tests

### Duración Estimada: 2-3 días

---

## 🎯 ETAPA 7: Documentación y Refinamiento

### Objetivos
- Documentar código
- Crear documentación de usuario
- Refactorizar según necesidad

### Tareas

1. **Documentación Técnica**
   - JavaDoc completo
   - Diagramas UML
   - Diagrama de arquitectura
   - Flujo de datos

2. **Documentación de Usuario**
   - Manual de usuario
   - Guía de instalación
   - Ejemplos de uso

3. **Code Review y Refactoring**
   - Aplicar las 4 reglas del diseño simple:
     1. Pasa todos los tests
     2. Revela intención
     3. Sin duplicación
     4. Mínimo de elementos
   - Eliminar code smells
   - Optimizar rendimiento

4. **README.md Completo**
   - Descripción del proyecto
   - Instrucciones de instalación
   - Uso básico
   - Arquitectura
   - Contribución

### Entregables
- ✅ Código documentado
- ✅ Manuales completos
- ✅ Diagramas actualizados
- ✅ README completo

### Duración Estimada: 2-3 días

---

## 🎯 ETAPA 8: Entrega y Presentación

### Objetivos
- Preparar entrega final
- Crear presentación
- Realizar demo

### Tareas

1. **Empaquetado**
   - JAR ejecutable
   - Scripts de inicio
   - Archivos de configuración

2. **Presentación**
   - Slides de presentación
   - Demo en vivo
   - Video tutorial (opcional)

3. **Repositorio Final**
   - Código limpio
   - Tags de versión
   - Releases

4. **Checklist Final**
   - [ ] Todos los requisitos cumplidos
   - [ ] Tests pasando
   - [ ] Documentación completa
   - [ ] Código limpio y refactorizado
   - [ ] Sin warnings ni errores
   - [ ] README actualizado

### Entregables
- ✅ Proyecto empaquetado
- ✅ Presentación lista
- ✅ Demo funcional
- ✅ Repositorio organizado

### Duración Estimada: 1-2 días

---

## 📊 Resumen de Duración

| Etapa | Duración Estimada | Acumulado |
|-------|------------------|-----------|
| 1. Configuración Inicial | 1-2 días | 1-2 días |
| 2. Capa de Dominio | 3-4 días | 4-6 días |
| 3. Capa de Aplicación | 4-5 días | 8-11 días |
| 4. Capa de Infraestructura | 3-4 días | 11-15 días |
| 5. Capa de Presentación | 3-4 días | 14-19 días |
| 6. Testing Integral | 2-3 días | 16-22 días |
| 7. Documentación | 2-3 días | 18-25 días |
| 8. Entrega y Presentación | 1-2 días | 19-27 días |

**Total: 3-4 semanas**

---

## 🏗️ Principios y Patrones Aplicados

### SOLID

1. **Single Responsibility Principle (SRP)**
   - Cada clase tiene una única razón para cambiar
   - Separación clara entre capas

2. **Open/Closed Principle (OCP)**
   - Entidades abiertas a extensión
   - Cerradas a modificación

3. **Liskov Substitution Principle (LSP)**
   - Implementaciones intercambiables
   - Contratos bien definidos

4. **Interface Segregation Principle (ISP)**
   - Interfaces específicas y cohesivas
   - No forzar dependencias innecesarias

5. **Dependency Inversion Principle (DIP)**
   - Dependencias hacia abstracciones
   - Inversión de control

### Las 4 Reglas del Diseño Simple

1. **Pasa todos los tests**
   - Cobertura >80%
   - Tests automatizados

2. **Revela intención**
   - Nombres significativos
   - Código autodocumentado

3. **Sin duplicación**
   - DRY (Don't Repeat Yourself)
   - Reutilización

4. **Mínimo de elementos**
   - YAGNI (You Aren't Gonna Need It)
   - Simplicidad

### Clean Architecture

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Controllers, UI, API Endpoints)       │
├─────────────────────────────────────────┤
│         Application Layer               │
│    (Use Cases, DTOs, Validators)        │
├─────────────────────────────────────────┤
│         Domain Layer                    │
│  (Entities, Value Objects, Interfaces)  │
├─────────────────────────────────────────┤
│       Infrastructure Layer              │
│ (Repositories, DB, External Services)   │
└─────────────────────────────────────────┘
```

**Regla de Dependencia**: Las capas internas NO dependen de las externas

---

## 📋 Checklist de Buenas Prácticas

### Código
- [ ] Nombres descriptivos y significativos
- [ ] Métodos pequeños y enfocados
- [ ] Evitar magic numbers
- [ ] Comentarios solo cuando necesario
- [ ] Sin código muerto
- [ ] Formateo consistente

### Diseño
- [ ] Alta cohesión
- [ ] Bajo acoplamiento
- [ ] Separación de responsabilidades
- [ ] Inmutabilidad donde sea posible
- [ ] Fail-fast

### Testing
- [ ] Tests unitarios
- [ ] Tests de integración
- [ ] Tests legibles (Given-When-Then)
- [ ] Un assert por test
- [ ] Tests independientes

### Git
- [ ] Commits atómicos
- [ ] Mensajes descriptivos
- [ ] Branches por feature
- [ ] Pull requests con review
- [ ] Gitignore configurado

---

## 🚀 Próximos Pasos

1. Revisar los requisitos específicos del PDF
2. Ajustar las etapas según necesidades
3. Comenzar con la Etapa 1
4. Seguir metodología iterativa
5. Revisar progreso cada 2-3 días

---

## 📞 Notas

- Este plan es flexible y puede ajustarse según requisitos específicos
- Se recomienda hacer commits frecuentes
- Cada etapa debe completarse antes de pasar a la siguiente
- Mantener comunicación sobre bloqueos o dudas

**¡Estamos listos para comenzar! 🎯**
