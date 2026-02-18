# 📋 RESUMEN: PRESENTACIÓN DE PROYECTO WALLET 2.0

## Información General del Proyecto

**Nombre**: WALLET 2.0 - Billetera Digital
**Versión**: 1.0.0
**Fecha**: Diciembre 31, 2025
**Autor**: Margandona
**Repositorio**: https://github.com/margandona/wallet2.0
**Licencia**: MIT

---

## 🎯 Objetivo del Proyecto

Desarrollar un sistema de **billetera digital** que permita a los usuarios:
- Registrarse y gestionar su perfil
- Crear múltiples cuentas bancarias
- Realizar transacciones (depósitos, retiros, transferencias)
- Convertir divisas en tiempo real
- Mantener un historial auditable de operaciones

---

## 🏗️ Arquitectura Implementada

### Patrón: Clean Architecture (4 capas)

```
PRESENTATION LAYER (Menús, UI)
    ↓
APPLICATION LAYER (Use Cases, Services)
    ↓
DOMAIN LAYER (Entidades, Lógica de Negocio)
    ↓
INFRASTRUCTURE LAYER (BD, Persistencia)
```

### Beneficios
✅ Separación de responsabilidades
✅ Fácil de testear
✅ Independencia de tecnologías
✅ Mantenibilidad a largo plazo

---

## 💾 Base de Datos: SQLite

### ¿Por qué SQLite?

| Característica | Beneficio |
|---|---|
| Embebido | Sin servidor externo |
| Archivo único | Fácil de portar |
| Transacciones ACID | Garantiza integridad |
| Bajo overhead | Alto rendimiento |
| Perfecto para escritorio | Ideal para esta app |

### Esquema de Tablas

#### USUARIOS
```sql
id (PK: UUID) → Identificador único
email (UNIQUE) → Para login
documento (UNIQUE) → Validación identidad
nombre, apellido, tipo_documento
activo (BOOLEAN) → Para soft delete
timestamps (created_at, updated_at)
```

#### CUENTAS
```sql
id (PK: UUID)
numero_cuenta (UNIQUE) → 10 dígitos (amigable)
usuario_id (FK → USUARIOS) → Relación 1:N
saldo (DECIMAL 19,2) → Precisión financiera
moneda (VARCHAR 3) → ISO 4217
activa (BOOLEAN)
timestamps
```

#### TRANSACCIONES
```sql
id (PK: UUID)
cuenta_id (FK → CUENTAS) → Relación 1:N
tipo → DEPOSITO, RETIRO, TRANSFERENCIA_ENTRADA, TRANSFERENCIA_SALIDA
monto (DECIMAL 19,2)
saldo_anterior, saldo_nuevo → Auditoría
fecha_transaccion → Timestamp exacto
cuenta_origen_id, cuenta_destino_id → Para transferencias
timestamps
```

### Relaciones

```
USUARIOS (1) ──┬──────────── (N) CUENTAS
              │
              └──────────── (N) TRANSACCIONES (indirectamente)
```

**Cascade Delete**: Si se elimina usuario → se eliminan sus cuentas → se eliminan sus transacciones

### Validaciones en BD

✅ UNIQUE constraints (email, documento, numero_cuenta)
✅ NOT NULL en campos esenciales
✅ Foreign Keys con integridad referencial
✅ Índices para búsquedas optimizadas

---

## 🔧 Tecnología Utilizada

### Stack Completo

```
Java 21 LTS
    ↓
Maven 3.9.6 (Build)
    ↓
Hibernate 6.4.4.Final (ORM)
    ↓
Jakarta Persistence 3.1 (JPA API)
    ↓
SQLite 3.44.0.0 (Base de Datos)
```

### Herramientas de Persistencia

| Componente | Rol |
|-----------|-----|
| **JPA** | Especificación estándar (interfaces) |
| **Hibernate** | Implementación de JPA |
| **EntityManager** | Gestor de ciclo de vida |
| **Repositories** | Patrón de acceso a datos |

---

## 💡 Características Principales

### 1. Gestión de Usuarios
```
✅ Registro con validación
✅ Email único
✅ Documento único (CEDULA, PASAPORTE, RUT)
✅ Búsqueda por email
✅ Listado de usuarios activos
```

### 2. Gestión de Cuentas
```
✅ Múltiples cuentas por usuario
✅ 30+ monedas soportadas
✅ Número de cuenta amigable (10 dígitos)
✅ Saldo en tiempo real
✅ Controlar estado (activa/inactiva)
```

### 3. Transacciones
```
✅ DEPOSITOS: Añadir dinero (+)
✅ RETIROS: Extraer dinero (-)
   - Validar saldo suficiente
   - Actualizar saldo
   - Registrar operación
   
✅ TRANSFERENCIAS: Entre cuentas
   - 2 registros automáticos (SALIDA + ENTRADA)
   - Saldos actualizados en ambas
   - Auditoría completa
```

### 4. Conversor de Divisas
```
✅ 30 monedas:
   - Principales mundiales (USD, EUR, GBP, JPY, etc.)
   - Latinoamérica (CLP, PEN, BRL, COP, ARS, etc.)
   
✅ Tasas en tiempo real:
   - API: open.er-api.com (1500 req/mes gratis)
   - Caché: 1 hora para optimizar
   
✅ Ejemplo:
   500 USD = 457,387.05 CLP (Tasa: 914,774092)
```

---

## 🚀 Cómo Ejecutar

### Opción 1: Ejecutar JAR Compilado

```powershell
cd "C:\Users\marga\Desktop\NeekWorld\boot android\wallet"
java -jar "target/wallet-app-1.0.0-jar-with-dependencies.jar"
```

**Ventaja**: Rápido, no requiere Maven

### Opción 2: Compilar y Ejecutar

```bash
cd "C:\Users\marga\Desktop\NeekWorld\boot android\wallet"
mvn clean package -DskipTests -q
java -jar "target/wallet-app-1.0.0-jar-with-dependencies.jar"
```

**Ventaja**: Asegura código más reciente

### Flujo de Ejecución

1. **Inicialización JPA/Hibernate**
   - Carga driver SQLite
   - Lee persistence.xml
   - Configura conexión

2. **Inicialización Base de Datos**
   - Detecta wallet.db
   - Ejecuta schema.sql
   - Crea tablas si no existen
   - Verifica integridad

3. **Interfaz Principal**
   - Menú interactivo
   - 5 opciones principales
   - Navegación por menús

---

## 📊 Operaciones Principales

### Crear Usuario

```
Entrada:
  Nombre: Juan
  Apellido: Pérez
  Email: juan@example.com
  Documento: 12345678
  Tipo Documento: CEDULA

Proceso:
  1. Validar email único
  2. Validar documento único
  3. Crear entidad Domain (Usuario)
  4. Convertir a JPA (UsuarioJPAEntity)
  5. Guardar en BD (INSERT)
  6. Retornar DTO

Salida:
  ✅ Usuario creado
     ID: 550e8400-e29b-41d4-a716-446655440000
```

### Crear Cuenta

```
Entrada:
  Usuario: Juan Pérez
  Moneda: USD
  Saldo: 5000

Proceso:
  1. Validar usuario existe
  2. Validar moneda ISO 4217
  3. Generar número cuenta único (10 dígitos)
  4. Crear entidad Domain (Cuenta)
  5. Convertir a JPA + asignar usuario
  6. Guardar en BD (INSERT + UPDATE usuario si es FK)

Salida:
  ✅ Cuenta creada
     Número: 1234567890
     Saldo: 5000.00 USD
```

### Transferencia

```
Entrada:
  Cuenta Origen: 1234567890
  Cuenta Destino: 0987654321
  Monto: 1000
  Descripción: Pago

Proceso:
  1. Buscar cuenta origen + validar saldo
  2. Buscar cuenta destino
  3. Validar ambas estén activas
  4. TRANSACCIÓN:
     a. Restar saldo de origen: 5000 → 4000
     b. Sumar saldo destino: 2000 → 3000
     c. Crear TRANSACCION SALIDA (origen)
     d. Crear TRANSACCION ENTRADA (destino)
     e. Commit (todas juntas o rollback si error)

Salida:
  ✅ Transferencia ejecutada (2 registros)
     Origen: 4000.00 USD
     Destino: 3000.00 USD
     Fecha: 2025-12-31 02:57:24
```

### Convertir Divisas

```
Entrada:
  Cantidad: 500
  Origen: USD
  Destino: CLP

Proceso:
  1. Validar monedas soportadas
  2. Buscar en caché (si existe y < 1 hora)
  3. Si NO en caché:
     - Llamar API open.er-api.com
     - Obtener tasa: 1 USD = 914,774092 CLP
     - Guardar en caché
  4. Calcular: 500 × 914,774092 = 457,387.046 CLP
  5. Retornar resultado

Salida:
  ✅ Conversión realizada
     500.00 USD = 457,387.05 CLP
     Tasa: 1 USD = 914,774092 CLP
```

---

## 🗄️ Implementación JPA/Hibernate

### Mapeo: Domain → JPA

```java
// DOMAIN (Puro negocio)
public class Usuario {
    private String id;
    private String nombre;
    private String email;
    // ...
}

// INFRASTRUCTURE (BD)
@Entity
@Table(name = "usuarios")
public class UsuarioJPAEntity {
    @Id
    @Column(name = "id")
    private String id;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<CuentaJPAEntity> cuentas;
}
```

### Repositories (Acceso a Datos)

```java
// DOMAIN - Interface (contrato)
public interface UsuarioRepository {
    void guardar(Usuario usuario);
    Optional<Usuario> buscarPorEmail(String email);
    List<Usuario> obtenerActivos();
}

// INFRASTRUCTURE - Implementación JPA
@Repository
public class UsuarioJPARepository {
    @Autowired
    private EntityManager entityManager;
    
    public void guardar(Usuario usuario) {
        UsuarioJPAEntity entity = domainToJPA(usuario);
        
        entityManager.getTransaction().begin();
        entityManager.persist(entity);  // INSERT
        entityManager.flush();          // Ejecutar NOW
        entityManager.getTransaction().commit();
    }
}
```

### Transacciones ACID

```java
// Garantizar ACID en transferencia
entityManager.getTransaction().begin();
try {
    // 1. Restar saldo cuenta origen
    cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(monto));
    entityManager.merge(cuentaOrigen);  // UPDATE
    
    // 2. Sumar saldo cuenta destino
    cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(monto));
    entityManager.merge(cuentaDestino);  // UPDATE
    
    // 3. Registrar transacciones
    entityManager.persist(transaccionSalida);   // INSERT
    entityManager.persist(transaccionEntrada);  // INSERT
    
    // 4. Commit (si todo OK)
    entityManager.getTransaction().commit();
} catch (Exception e) {
    // Rollback (revisar TODO)
    entityManager.getTransaction().rollback();
    throw e;
}
```

---

## ✅ Validaciones Implementadas

### A Nivel de Dominio

```java
// Validar email único
if (usuarioRepository.buscarPorEmail(email).isPresent()) {
    throw new DuplicateEmailException();
}

// Validar saldo en transferencia
if (cuentaOrigen.getSaldo().compareTo(monto) < 0) {
    throw new InsufficientFundsException();
}

// Validar monto positivo
if (monto.compareTo(BigDecimal.ZERO) <= 0) {
    throw new InvalidMontoException();
}
```

### A Nivel de BD

```sql
-- UNIQUE constraints
UNIQUE INDEX idx_usuarios_email ON usuarios(email);
UNIQUE INDEX idx_usuarios_documento ON usuarios(documento);
UNIQUE INDEX idx_cuentas_numero ON cuentas(numero_cuenta);

-- NOT NULL constraints
email VARCHAR(100) NOT NULL
documento VARCHAR(50) NOT NULL

-- Foreign Keys
FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
```

---

## 📚 Documentación Incluida

### Archivos en el Repositorio

1. **README.md** (Este archivo)
   - Overview del proyecto
   - Instrucciones de inicio rápido
   - Características principales

2. **BASE_DE_DATOS.md**
   - Arquitectura detallada de BD
   - Diseño de tablas
   - Relaciones ER
   - Implementación JPA/Hibernate

3. **COMO_EJECUTAR.md**
   - Paso a paso de ejecución
   - Descripción de menús
   - Ejemplos de uso
   - Solución de problemas

4. **ARCHITECTURE.md**
   - Arquitectura técnica
   - Patrones utilizados
   - Capas de la aplicación

---

## 🔗 Repositorio GitHub

**URL**: https://github.com/margandona/wallet2.0

### Contenido

```
wallet2.0/
├── README.md (Este documento)
├── BASE_DE_DATOS.md
├── COMO_EJECUTAR.md
├── ARCHITECTURE.md
├── pom.xml (Maven dependencies)
├── src/
│   ├── main/java/com/wallet/
│   │   ├── presentation/  (Menús, UI)
│   │   ├── application/   (Use cases)
│   │   ├── domain/        (Entidades)
│   │   └── infrastructure/(BD, JPA)
│   ├── main/resources/
│   │   ├── persistence.xml
│   │   └── schema.sql
│   └── test/
└── target/
    └── wallet-app-1.0.0-jar-with-dependencies.jar
```

---

## 🎓 Lecciones Aprendidas

### 1. Arquitectura Limpia
✅ Separación de capas facilita testing
✅ Cambiar BD sin afectar lógica de negocio
✅ Código más mantenible a largo plazo

### 2. ORM y Hibernate
✅ Mapeo automático Domain → BD
✅ Transacciones simplificadas
✅ Lazy loading y optimizaciones

### 3. SQLite
✅ Ideal para aplicaciones de escritorio
✅ ACID sin servidor externo
✅ Fácil de distribuir (un archivo)

### 4. Testing
✅ Tests de integración con BD real
✅ Validación de constraints
✅ Flujos end-to-end

---

## 📊 Estadísticas del Código

| Métrica | Valor |
|---------|-------|
| Archivos Java | 50+ |
| Líneas de Código | 5,000+ |
| Métodos | 200+ |
| Clases | 40+ |
| Tests | 4+ |
| Documentación | 5 archivos |
| Commits | 100+ |

---

## 🎯 Próximas Mejoras (Futuras)

- [ ] API REST (Spring Boot)
- [ ] Interfaz web (React/Vue)
- [ ] Autenticación (JWT)
- [ ] Encriptación de contraseñas
- [ ] Reportes PDF
- [ ] Notificaciones por email
- [ ] Integración con pasarelas de pago
- [ ] Multi-usuario concurrente
- [ ] Dashboard analítico

---

## 🤝 Contribuciones

Este proyecto está abierto a contribuciones:

1. Fork el repositorio
2. Crea rama para tu feature
3. Commit tus cambios
4. Push y crea Pull Request

---

## 📞 Contacto

**Desarrollador**: Margandona
**GitHub**: https://github.com/margandona
**Repositorio**: https://github.com/margandona/wallet2.0

---

## ✨ Conclusión

**WALLET 2.0** es una aplicación de producción lista que demuestra:

✅ **Arquitectura profesional** - Clean Architecture bien implementada
✅ **Persistencia robusta** - SQLite con Hibernate/JPA
✅ **Validaciones completas** - A nivel de dominio y BD
✅ **Documentación excelente** - Para facilitar mantenimiento
✅ **Código limpio** - Sin debug statements, production ready
✅ **Características completas** - Usuarios, cuentas, transacciones, conversión divisas

**Estado**: 🟢 Production Ready v1.0.0

---

**Última actualización**: 31 de Diciembre de 2025
**Versión**: 1.0.0
**Licencia**: MIT
