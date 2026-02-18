# 💳 WALLET 2.0 - Billetera Digital

> **Sistema de gestión de billetera digital con persistencia en SQLite, arquitectura limpia, conversor de divisas multicurrencia e interfaz web MVC.**

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-red.svg)](https://jakarta.ee/)
[![Database](https://img.shields.io/badge/Database-SQLite-green.svg)](https://www.sqlite.org/)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture-blue.svg)](/BASE_DE_DATOS.md)
[![Version](https://img.shields.io/badge/Version-1.0.0-blue.svg)](/)

---

## 🌐 Interfaz Web (NUEVO)

El proyecto ahora incluye una interfaz web completa con Jakarta EE 10:

### Iniciar servidor web
```powershell
mvn jetty:run -DskipTests
# Acceder a: http://localhost:8090/wallet/
```

### Funcionalidades web
- ✅ Consultar saldo de cuenta
- ✅ Depositar y retirar fondos
- ✅ Transferencias entre cuentas
- ✅ **Historial de transacciones** con paginación y filtros (tipo, fechas)
- ✅ Crear, buscar y listar usuarios
- ✅ **Listado de usuarios** con paginación y filtros (email, estado)
- ✅ Validaciones de formulario y manejo de errores

**Stack tecnológico web:**
- Jakarta Servlet 6.0 + JSP 3.1
- Jetty 12 (EE10)
- CSS responsivo
- Arquitectura MVC

---

## 🚀 Inicio Rápido

### Opción 1: Interfaz Web (Recomendado)
```powershell
mvn jetty:run -DskipTests
# Abrir: http://localhost:8090/wallet/
```

### Opción 2: Aplicación de Consola

```powershell
cd "C:\Users\marga\Desktop\NeekWorld\boot android\wallet"
java -jar "target/wallet-app-1.0.0-jar-with-dependencies.jar"
```

**O compilar desde código:**

```bash
mvn clean package -DskipTests -q
java -jar "target/wallet-app-1.0.0-jar-with-dependencies.jar"
```

---

## 📖 Documentación

| Documento | Descripción |
|-----------|-------------|
| **[BASE_DE_DATOS.md](./BASE_DE_DATOS.md)** | 📊 Arquitectura, diseño de tablas, relaciones ER, implementación JPA/Hibernate |
| **[COMO_EJECUTAR.md](./COMO_EJECUTAR.md)** | ▶️ Guía paso a paso de ejecución de consola, menús, ejemplos de uso |
| **[ARCHITECTURE.md](./ARCHITECTURE.md)** | 🏗️ Arquitectura técnica, capas, patrones |
| **[PLAN_INTEGRACION_REQUERIMIENTOS.md](./PLAN_INTEGRACION_REQUERIMIENTOS.md)** | 🔄 Plan de integración de interfaz web MVC |
| **[PAGINACION_FILTROS_COMPLETADO.md](./PAGINACION_FILTROS_COMPLETADO.md)** | 📄 Documentación técnica de paginación y filtros |

---

## ✨ Características

### 👤 Gestión de Usuarios
```
✅ Registro con validación de email y documento
✅ Búsqueda por email
✅ Listado de usuarios activos
✅ Documento único por usuario (CEDULA, PASAPORTE, RUT)
```

### 🏦 Gestión de Cuentas
```
✅ Múltiples cuentas por usuario
✅ 30+ monedas soportadas (USD, EUR, CLP, PEN, BRL, etc.)
✅ Número de cuenta único (10 dígitos amigables)
✅ Control de saldo en tiempo real
```

### 💰 Transacciones
```
✅ Depósitos - Añadir dinero a cuenta
✅ Retiros - Extraer con validación de saldo
✅ Transferencias - Entre cuentas (2 registros: SALIDA + ENTRADA)
✅ Historial completo - Auditoría de todas las operaciones
✅ Trazabilidad - Saldo anterior, saldo nuevo, timestamps
```

### 💱 Conversor de Divisas
```
✅ 30 monedas principales del mundo + Latinoamérica
✅ Tasas de cambio en tiempo real (API open.er-api.com)
✅ Caché de tasas (1 hora) para optimizar
✅ Verificación de disponibilidad de servicio
```

### 🗄️ Persistencia
```
✅ Base de datos SQLite embebida
✅ Transacciones ACID completas
✅ Integridad referencial con Foreign Keys
✅ Inicialización automática de esquema
✅ Índices para búsquedas optimizadas
```

---

## 📦 Requisitos

- **Java 21 JDK** o superior - [Descargar](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.9.6** o superior - [Descargar](https://maven.apache.org/download.cgi)
- **Windows / macOS / Linux**

### Verificar Instalación

```bash
java -version
mvn -version
```

---

## 🔧 Instalación

### Paso 1: Clonar Repositorio

```bash
git clone https://github.com/margandona/wallet2.0.git
cd wallet2.0
```

### Paso 2: Compilar

```bash
mvn clean package -DskipTests -q
```

**Resultado**: `target/wallet-app-1.0.0-jar-with-dependencies.jar` (32 MB)

**Tiempo**: 30-60 segundos

### Paso 3: Ejecutar

```bash
java -jar "target/wallet-app-1.0.0-jar-with-dependencies.jar"
```

---

## 📊 Base de Datos

### Tablas Principales

```
USUARIOS (10+ campos)
└─ email (UNIQUE)
└─ documento (UNIQUE)

CUENTAS (9 campos)
├─ usuario_id (FK → USUARIOS)
└─ numero_cuenta (UNIQUE)

TRANSACCIONES (11 campos)
└─ cuenta_id (FK → CUENTAS)
```

### Características

✅ **ACID Compliance** - Transacciones atómicas y durables
✅ **Integridad Referencial** - FK con CASCADE delete
✅ **Índices** - Para email, documento, número cuenta
✅ **Validaciones** - NOT NULL en campos esenciales

**Ver documentación completa**: [BASE_DE_DATOS.md](./BASE_DE_DATOS.md)

---

## 💡 Uso

### Crear Usuario

```
Menú Principal → 1. Gestión de Usuarios → 1. Registrar

Entrada:
  Nombre: Juan
  Apellido: Pérez
  Email: juan@example.com
  Documento: 12345678
  Tipo: CEDULA

Resultado:
  ✅ Usuario creado con ID único
```

### Crear Cuenta

```
Menú Principal → 2. Gestión de Cuentas → 1. Crear Nueva Cuenta

Entrada:
  Usuario: Juan Pérez
  Moneda: USD
  Saldo inicial: 5000

Resultado:
  ✅ Cuenta: 1234567890 (USD) - Saldo: $5,000.00
```

### Transferir Dinero

```
Menú Principal → 3. Transacciones → 1. Transferir

Entrada:
  Número cuenta origen: 1234567890
  Número cuenta destino: 0987654321
  Monto: 1000

Resultado:
  ✅ Transferencia ejecutada
  ✅ Dos transacciones registradas:
     - TRANSFERENCIA_SALIDA en cuenta origen
     - TRANSFERENCIA_ENTRADA en cuenta destino
```

### Convertir Divisas

```
Menú Principal → 5. Conversor de Divisas → 1. Convertir

Entrada:
  Cantidad: 500
  Origen: USD (Dólar Estadounidense)
  Destino: CLP (Peso Chileno)

Resultado:
  500 USD = 457,387.05 CLP
```

---

## 🏗️ Arquitectura

### Patrón: Clean Architecture

```
┌─────────────────────────────────────────────────────┐
│       PRESENTATION LAYER                            │
│  MenuPrincipal, MenuUsuarios, MenuCuentas, etc    │
└─────────────────────────┬───────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────┐
│      APPLICATION LAYER                              │
│  UseCases, Services, DTOs, Mappers                 │
└─────────────────────────┬───────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────┐
│        DOMAIN LAYER                                 │
│  Entities, ValueObjects, Repositories (I)          │
└─────────────────────────┬───────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────┐
│     INFRASTRUCTURE LAYER                            │
│  JPA Entities, Repositories (Impl), Database       │
└─────────────────────────────────────────────────────┘
```

### Stack Tecnológico

| Componente | Tecnología |
|-----------|-----------|
| **Language** | Java 21 LTS |
| **Build Tool** | Maven 3.9.6 |
| **ORM** | Hibernate 6.4.4.Final |
| **Persistence API** | Jakarta Persistence 3.1 |
| **Database** | SQLite 3.44.0.0 |
| **Testing** | JUnit 5 |
| **Architecture** | Clean Architecture |

---

## 🌍 Monedas Soportadas (30 total)

### Principales Mundiales (15)
```
USD - Dólar Estadounidense
EUR - Euro
GBP - Libra Esterlina
JPY - Yen Japonés
CHF - Franco Suizo
CNY - Yuan Chino
SGD - Dólar Singapur
HKD - Dólar Hong Kong
AUD - Dólar Australiano
CAD - Dólar Canadiense
NZD - Dólar Nueva Zelanda
INR - Rupia India
KRW - Won Coreano
AED - Dirham EAU
ZAR - Rand Sudáfrica
```

### Latinoamérica (15)
```
MXN - Peso Mexicano
BRL - Real Brasileño
PEN - Sol Peruano
CLP - Peso Chileno ⭐
COP - Peso Colombiano
ARS - Peso Argentino
UYU - Peso Uruguayo
PYG - Guaraní Paraguayo
BOB - Boliviano
VES - Bolívar Venezolano
GTQ - Quetzal Guatemalteco
HNL - Lempira Hondureño
CRC - Colón Costarricense
PAN - Balboa Panameño
```

---

## 📁 Estructura del Proyecto

```
wallet/
│
├── src/main/java/com/wallet/
│   ├── Main.java                      (Punto de entrada)
│   ├── domain/                        (Lógica de negocio)
│   │   ├── entities/                  (Usuario, Cuenta, Transaccion)
│   │   ├── repositories/              (Interfaces)
│   │   ├── services/                  (Servicios de dominio)
│   │   └── exceptions/                (Excepciones de negocio)
│   ├── application/                   (Casos de uso)
│   │   ├── usecases/                  (BuscarUsuarioUseCase, etc.)
│   │   ├── services/                  (UsuarioService, CuentaService, etc.)
│   │   ├── dtos/                      (Objetos de transferencia)
│   │   └── mappers/                   (Entity → DTO)
│   ├── infrastructure/                (Implementación técnica)
│   │   ├── config/                    (JPAConfiguration)
│   │   ├── persistence/               (DatabaseInitializer)
│   │   ├── repositories/              (JPA Repositories)
│   │   ├── entities/                  (JPA Entities)
│   │   └── services/                  (API Integration)
│   └── presentation/                  (Interfaz de usuario)
│       ├── controllers/               (TransaccionController, etc.)
│       ├── menus/                     (MenuPrincipal, MenuUsuarios, etc.)
│       └── utils/                     (ConsoleUtils)
│
├── src/main/resources/
│   ├── persistence.xml                (Configuración JPA)
│   └── schema.sql                     (Inicialización BD)
│
├── src/test/java/                     (Tests unitarios e integración)
│
├── pom.xml                            (Dependencias Maven)
├── README.md                          (Este archivo)
├── BASE_DE_DATOS.md                   (Documentación BD)
├── COMO_EJECUTAR.md                   (Guía de ejecución)
└── wallet.db                          (BD SQLite - Creada en ejecución)
```

---

## 🧪 Testing

### Ejecutar Tests

```bash
mvn test
```

### Tests Incluidos

- **UsuarioJPARepositoryTest** - Persistencia de usuarios
- **CuentaJPARepositoryTest** - Persistencia de cuentas
- **TransaccionJPARepositoryTest** - Persistencia de transacciones
- **FlujoComipletoIntegrationTest** - Flujo end-to-end

---

## 🔐 Validaciones

### Usuario
✅ Email único y válido
✅ Documento único
✅ Nombre y apellido no vacíos

### Cuenta
✅ Número de cuenta único
✅ Saldo no negativo
✅ Moneda válida (ISO 4217)

### Transacción
✅ Saldo suficiente
✅ Monto positivo
✅ Cuentas activas
✅ Saldo actualizado correctamente

---

## 🛠️ Comandos Útiles

### Compilar

```bash
# Compilación completa con empaquetado
mvn clean package -DskipTests -q

# Solo compilar sin empaquetar
mvn compile

# Compilar e instalar en repositorio local
mvn install -DskipTests -q
```

### Ejecutar

```bash
# Ejecutar la aplicación
java -jar "target/wallet-app-1.0.0-jar-with-dependencies.jar"

# Ejecutar con argumentos
java -Xmx512m -jar "target/wallet-app-1.0.0-jar-with-dependencies.jar"
```

### Testing

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar test específico
mvn test -Dtest=UsuarioJPARepositoryTest

# Ejecutar con cobertura
mvn test jacoco:report
```

---

## 📊 Estadísticas

| Métrica | Valor |
|---------|-------|
| **Archivos Java** | 50+ |
| **Líneas de Código** | 5,000+ |
| **Métodos** | 200+ |
| **Tests** | 4+ |
| **Documentación** | 5 archivos MD |
| **Monedas Soportadas** | 30+ |

---

## 🤝 Contribución

### Reportar Bugs

1. Abre un [Issue](https://github.com/margandona/wallet2.0/issues)
2. Describe el problema
3. Incluye pasos para reproducir
4. Tu versión de Java y SO

### Proponer Mejoras

1. Fork el repositorio
2. Crea rama: `git checkout -b feature/mi-mejora`
3. Commit: `git commit -m 'Add: descripción'`
4. Push: `git push origin feature/mi-mejora`
5. Abre Pull Request

---

## 📝 Licencia

Este proyecto está bajo licencia **MIT** - ver [LICENSE](LICENSE) para más detalles.

---

## 👤 Autor

**Margandona**
- 🔗 GitHub: [@margandona](https://github.com/margandona)
- 📦 Repositorio: [wallet2.0](https://github.com/margandona/wallet2.0)
- 📧 Email: [margandona@example.com]

---

## 📞 Soporte

¿Preguntas o problemas?

- 📖 Consulta la [documentación completa](./BASE_DE_DATOS.md)
- 💬 Abre un [Issue](https://github.com/margandona/wallet2.0/issues)
- 📚 Lee la [guía de ejecución](./COMO_EJECUTAR.md)

---

## 🎉 Agradecimientos

Gracias a:
- **Hibernate ORM** - Mapeo objeto-relacional
- **SQLite** - Base de datos embebida
- **Maven** - Gestión de dependencias
- **Java Community** - Soporte y librerías

---

**Última actualización**: Diciembre 31, 2025
**Versión**: 1.0.0
**Estado**: ✅ **Production Ready**

---

### ⭐ Si te gusta este proyecto, no olvides dejar una estrella en GitHub!
