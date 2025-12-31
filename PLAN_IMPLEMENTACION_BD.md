# 📋 PLAN DE IMPLEMENTACIÓN - INTEGRACIÓN BD EN WALLET

## 📌 RESUMEN EJECUTIVO

Este plan describe la integración de una base de datos al proyecto Wallet, migrando desde almacenamiento en memoria a persistencia real. Utilizaremos **Hibernate/JPA** como ORM y **SQLite** como base de datos.

---

## 🎯 RECOMENDACIONES INICIALES

### 1. **ORM Seleccionado: Hibernate/JPA**
   - ✅ **Ventaja**: Es el ORM más robusto en Java, compatible con Clean Architecture
   - ✅ **Razón**: Ya tienes independencia de implementación con interfaces Repository
   - ✅ **Compatibilidad**: Funciona con MySQL y SQLite sin cambios de código
   - ✅ **Integración VS Code**: Excelente soporte con SQLTools

### 2. **Base de Datos: SQLite**
   - ✅ **Ventaja**: No requiere servidor separado
   - ✅ **Simpleza**: Archivo único `wallet.db` - fácil de versionear
   - ✅ **Desarrollo**: Perfecta para desarrollo y pruebas
   - ✅ **Escalabilidad**: Si crece, migramos a MySQL con cambios mínimos
   - ❌ **Limitación**: No es multi-usuario en tiempo real (ok para inicio)

### 3. **SQLTools Recomendado: SQLite Extension**
   - Extensión: **`mtxr.sqltools-sqlite`**
   - Por qué: 
     - Interfaz visual para explorar BD
     - Ejecutar queries directamente en VS Code
     - Manage migrations y schema fácilmente

---

## 🏗️ DIAGRAMA DE ARQUITECTURA ACTUAL → NUEVA

```
╔═══════════════════════════════════════════════════════════╗
║                   PRESENTACIÓN (UI)                       ║
║              Controllers/Menus (sin cambios)              ║
╚═════════════════════════╤═════════════════════════════════╝
                          │
┌─────────────────────────▼──────────────────────────────────┐
│              APLICACIÓN (Use Cases)                         │
│         DTOs / Mappers (sin cambios mayores)               │
└─────────────────────────┬──────────────────────────────────┘
                          │
┌─────────────────────────▼──────────────────────────────────┐
│            INFRAESTRUCTURA (NEW LAYER)                     │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  JPA Configuration                                   │  │
│  │  - persistence.xml                                  │  │
│  │  - EntityManager                                    │  │
│  │  - Transactions                                     │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Repository Implementations (MODIFIED)               │  │
│  │  - UsuarioRepository → UsuarioJPARepository         │  │
│  │  - CuentaRepository → CuentaJPARepository           │  │
│  │  - TransaccionRepository → TransaccionJPARepository │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Entities (JPA) con anotaciones                      │  │
│  │  - @Entity, @Table, @Column                         │  │
│  │  - @GeneratedValue para IDs                         │  │
│  │  - @OneToMany, @ManyToOne para relaciones           │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────┬──────────────────────────────────┘
                          │
┌─────────────────────────▼──────────────────────────────────┐
│              DOMINIO (sin cambios)                         │
│        Entities, Value Objects, Exceptions                │
└─────────────────────────────────────────────────────────────┘
                          │
┌─────────────────────────▼──────────────────────────────────┐
│              BASE DE DATOS SQLite                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  wallet.db (archivo único)                           │  │
│  │  - tabla: usuarios                                   │  │
│  │  - tabla: cuentas                                    │  │
│  │  - tabla: transacciones                              │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 📊 DIAGRAMA ENTIDAD-RELACIÓN (ER)

```sql
┌─────────────────────┐
│     USUARIOS        │
├─────────────────────┤
│ id (PK)             │◄─────┐
│ nombre              │       │
│ apellido            │       │
│ email (UNIQUE)      │       │
│ documento (UNIQUE)  │       │
│ activo              │       │
│ created_at          │       │
│ updated_at          │       │
└─────────────────────┘       │
                              │ 1:N
                              │
┌─────────────────────┐       │
│     CUENTAS         │       │
├─────────────────────┤       │
│ id (PK)             │       │
│ numero (UNIQUE)     │       │
│ usuario_id (FK)─────┼───────┘
│ saldo               │
│ moneda              │
│ activa              │
│ created_at          │
│ updated_at          │◄─────┐
└─────────────────────┘       │
                              │ 1:N
                              │
┌─────────────────────┐       │
│  TRANSACCIONES      │       │
├─────────────────────┤       │
│ id (PK)             │       │
│ cuenta_id (FK)──────┼───────┘
│ tipo                │
│ monto               │
│ descripcion         │
│ fecha_transaccion   │
│ created_at          │
└─────────────────────┘
```

---

## 🔄 FLUJO DE CAMBIOS POR CAPAS

### ❌ NO CAMBIA (Domain Layer)
```
- Entidades de dominio (Usuario, Cuenta, Transaccion)
- Value Objects (Email, DocumentoIdentidad, Moneda)
- Interfaces Repository (contratos)
- Excepciones
- Use Cases (lógica de negocio)
```

### ⚠️ CAMBIA IMPLEMENTACIÓN (Infrastructure Layer)
```
- InMemoryRepositories → JPARepositories
- Cambios en persistence config
- Nuevas Entity classes JPA con anotaciones
```

### ✅ SIN CAMBIOS (Application & Presentation Layers)
```
- DTOs (igual)
- Mappers (igual)
- Controllers/Menus (igual)
- Use Cases (igual)
```

---

## 📋 FASES DE IMPLEMENTACIÓN

### **FASE 1: CONFIGURACIÓN INICIAL (2-3 horas)**
**Objetivo**: Preparar proyecto para JPA y SQLite

#### Tareas:
- [ ] Añadir dependencias a `pom.xml`:
  - Hibernate JPA (version 6.4.x)
  - SQLite Driver JDBC
  - Jakarta Persistence API (JPA 3.1)
  
- [ ] Crear archivo `persistence.xml` en `src/main/resources/META-INF/`
  
- [ ] Crear clase `JPAConfiguration.java` para:
  - EntityManagerFactory
  - EntityManager
  - Transaction handling
  
- [ ] Instalar extensión SQLTools en VS Code

#### Deliverables:
- `pom.xml` actualizado
- `persistence.xml` configurado
- `JPAConfiguration.java` creada
- SQLite extension instalada

---

### **FASE 2: MAPEO DE ENTIDADES JPA (3-4 horas)**
**Objetivo**: Crear clases JPA Entity anotadas

#### Tareas:
- [ ] Crear `UsuarioJPAEntity.java`:
  - Anotaciones: @Entity, @Table, @Id, @Column
  - Relación @OneToMany con CuentaJPAEntity
  - Constructores (default + parametrizado)
  
- [ ] Crear `CuentaJPAEntity.java`:
  - Anotaciones: @Entity, @Table, @Id, @Column
  - @ManyToOne relación con Usuario
  - @OneToMany relación con Transaccion
  
- [ ] Crear `TransaccionJPAEntity.java`:
  - Anotaciones: @Entity, @Table, @Id, @Column
  - @ManyToOne relación con Cuenta
  - Enum para tipo de transacción

- [ ] Crear `MonedaJPAEnum.java`:
  - Convertidor de moneda a string en BD

#### Deliverables:
- 4 Entity classes con anotaciones JPA
- Relaciones configuradas correctamente
- Scripts SQL para crear tablas (como referencia)

---

### **FASE 3: REPOSITORIOS JPA (4-5 horas)**
**Objetivo**: Implementar Repositories con JPA

#### Tareas:
- [ ] Crear `UsuarioJPARepository.java`:
  - Implementar interfaz `UsuarioRepository`
  - Métodos: guardar, buscarPorId, buscarPorEmail, buscarPorDocumento, listar
  - Usar @Query JPQL si es necesario
  
- [ ] Crear `CuentaJPARepository.java`:
  - Implementar interfaz `CuentaRepository`
  - Métodos: guardar, buscarPorId, buscarPorNumero, listarPorUsuario, actualizar
  
- [ ] Crear `TransaccionJPARepository.java`:
  - Implementar interfaz `TransaccionRepository`
  - Métodos: guardar, buscarPorId, listarPorCuenta
  
- [ ] Crear transacciones en cada operación (BEGIN, COMMIT, ROLLBACK)

#### Deliverables:
- 3 Repository implementations con JPA
- Gestión de transacciones integrada
- Tests unitarios para repositorios

---

### **FASE 4: MAPEO DE CONVERSIÓN (2 horas)**
**Objetivo**: Convertir entre Domain Entities y JPA Entities

#### Tareas:
- [ ] Actualizar `UsuarioMapper.java`:
  - Método: domainToJPA(Usuario → UsuarioJPAEntity)
  - Método: jpaToDomain(UsuarioJPAEntity → Usuario)
  
- [ ] Actualizar `CuentaMapper.java`:
  - Conversión bidireccional
  - Manejo de relaciones
  
- [ ] Actualizar `TransaccionMapper.java`:
  - Conversión con timestamps

#### Deliverables:
- Mappers actualizados
- Tests de mapeo

---

### **FASE 5: CONFIGURACIÓN E INICIALIZACIÓN (2 horas)**
**Objetivo**: Preparar base de datos para primera ejecución

#### Tareas:
- [ ] Crear script SQL `schema.sql`:
  - CREATE TABLE usuarios
  - CREATE TABLE cuentas
  - CREATE TABLE transacciones
  - Índices en campos clave (email, documento, numero_cuenta)
  
- [ ] Crear clase `DatabaseInitializer.java`:
  - Detectar si BD existe
  - Crear tablas si no existen
  - Cargar datos iniciales de prueba (opcional)
  
- [ ] Actualizar `Main.java`:
  - Inicializar JPAConfiguration antes de usar repositorios
  - Llamar a DatabaseInitializer

#### Deliverables:
- `schema.sql` con estructura completa
- `DatabaseInitializer.java`
- `Main.java` actualizado

---

### **FASE 6: PRUEBAS INTEGRACIÓN (3-4 horas)**
**Objetivo**: Validar que todo funciona correctamente

#### Tareas:
- [ ] Tests de repositorios:
  - Insert usuario → Verify en BD
  - Update cuenta → Verify cambios
  - Delete transaccion → Verify eliminación
  
- [ ] Tests de flujo completo:
  - Crear usuario → crear cuenta → hacer transacción
  - Verificar integridad de datos
  
- [ ] Tests de transacciones:
  - Rollback en caso de error
  - Consistencia de datos
  
- [ ] Pruebas manuales en VS Code:
  - Usar SQLTools para ver datos
  - Ejecutar operaciones desde menú
  - Validar BD

#### Deliverables:
- Suite de tests completa
- Reporte de cobertura
- Documentación de casos de prueba

---

### **FASE 7: MEJORAS Y OPTIMIZACIÓN (2-3 horas)**
**Objetivo**: Pulir y optimizar

#### Tareas:
- [ ] Añadir índices a BD
  - PK en ids
  - UNIQUE en email, documento, numero_cuenta
  - Index en usuario_id, cuenta_id
  
- [ ] Validaciones duplicadas:
  - Email único verificado en BD
  - Documento único verificado en BD
  
- [ ] Logging de operaciones:
  - Registrar inserts, updates, deletes
  
- [ ] Manejo de excepciones:
  - Converter excepciones JPA a excepciones del dominio

#### Deliverables:
- BD optimizada
- Logging completo
- Excepciones manejadas

---

### **FASE 8: DOCUMENTACIÓN (1-2 horas)**
**Objetivo**: Documentar todo el proceso

#### Tareas:
- [ ] Actualizar ARCHITECTURE.md:
  - Diagrama con nueva capa de persistencia
  - Explicar configuración JPA
  
- [ ] Crear GUIA_BD.md:
  - Cómo conectar BD desde VS Code
  - Queries útiles
  - Troubleshooting
  
- [ ] Crear MIGRACION.md:
  - Paso a paso para replicar proceso
  - Decisiones tomadas
  
- [ ] Actualizar README.md:
  - Mencionar persistencia en BD
  - Requisitos: SQLite (incluido en JDBC)

#### Deliverables:
- Documentación completa
- Guías de uso
- Troubleshooting guide

---

## 📊 TABLA DE RESUMEN DE FASES

| Fase | Nombre | Duración | Prioridad | Estado |
|------|--------|----------|-----------|--------|
| 1 | Configuración Inicial | 2-3h | 🔴 CRÍTICA | ⏳ Not Started |
| 2 | Mapeo Entidades JPA | 3-4h | 🔴 CRÍTICA | ⏳ Not Started |
| 3 | Repositorios JPA | 4-5h | 🔴 CRÍTICA | ⏳ Not Started |
| 4 | Mapeo Conversión | 2h | 🟡 ALTA | ⏳ Not Started |
| 5 | Config Inicialización | 2h | 🟡 ALTA | ⏳ Not Started |
| 6 | Pruebas Integración | 3-4h | 🟡 ALTA | ⏳ Not Started |
| 7 | Mejoras Optimización | 2-3h | 🟢 MEDIA | ⏳ Not Started |
| 8 | Documentación | 1-2h | 🟢 MEDIA | ⏳ Not Started |

**Total estimado: 19-26 horas**

---

## 🛠️ TOOLS Y RECURSOS

### VS Code Extensions
```
1. SQLTools (mtxr.sqltools)
2. SQLite3 (mtxr.sqltools-sqlite)
3. Extension Pack for Java (microsoft)
4. Maven for Java (vscjava.vscode-maven)
```

### Dependencias Maven
```xml
<!-- JPA/Hibernate -->
hibernate-core
hibernate-entitymanager
jakarta.persistence-api
jakarta.transaction-api

<!-- SQLite -->
org.xerial:sqlite-jdbc

<!-- Connection Pool -->
hikaricp
```

### Archivos Clave a Crear
```
src/main/resources/META-INF/persistence.xml
src/main/java/.../infrastructure/config/JPAConfiguration.java
src/main/java/.../infrastructure/entities/UsuarioJPAEntity.java
src/main/java/.../infrastructure/entities/CuentaJPAEntity.java
src/main/java/.../infrastructure/entities/TransaccionJPAEntity.java
src/main/java/.../infrastructure/repositories/UsuarioJPARepository.java
src/main/java/.../infrastructure/repositories/CuentaJPARepository.java
src/main/java/.../infrastructure/repositories/TransaccionJPARepository.java
src/main/java/.../infrastructure/persistence/DatabaseInitializer.java
src/main/resources/schema.sql
docs/GUIA_BD.md
docs/MIGRACION.md
```

---

## ⚠️ CONSIDERACIONES IMPORTANTES

### Seguridad
- [ ] No guardar contraseñas en texto plano (preparación para Fase futura)
- [ ] Validar input en repositorios
- [ ] Usar prepared statements (JPA lo hace automáticamente)

### Performance
- [ ] Índices en campos de búsqueda frecuente
- [ ] Lazy loading configurado correctamente
- [ ] Connection pooling con HikariCP

### Mantenibilidad
- [ ] Mantener Domain Entities sin anotaciones JPA
- [ ] Crear JPA Entities separadas
- [ ] Uso de Mappers para conversión

### Testing
- [ ] Tests con BD en memoria (H2) para CI/CD
- [ ] Tests de integración completos
- [ ] Cobertura mínima del 85%

---

## 📞 PRÓXIMOS PASOS

**Cuando hayas entendido este plan:**
1. Confirma que el plan es correcto
2. Solicita la **FASE 1** para comenzar
3. Una vez completada, pide la **FASE 2**
4. Continúa secuencialmente

⏸️ **ESPERO INSTRUCCIÓN PARA COMENZAR CUALQUIER FASE**

---

**Documento generado**: 2024
**Última actualización**: Pendiente de aprobación
**Estado**: 📋 Esperando validación
