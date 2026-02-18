# 🚀 Guía de instalación y despliegue

## ✅ Proyecto completado y subido a GitHub

**Repositorio:** https://github.com/margandona/wallet2.0

---

## 📦 Clonar el proyecto

```bash
git clone https://github.com/margandona/wallet2.0.git
cd wallet2.0
```

---

## 🛠️ Requisitos previos

### Obligatorios
- **Java 21 LTS** ([Descargar](https://www.oracle.com/java/technologies/downloads/#java21))
- **Maven 3.9.6+** ([Descargar](https://maven.apache.org/download.cgi))

### Verificar instalación
```bash
java -version   # Debe mostrar Java 21
mvn -version    # Debe mostrar Maven 3.9+
```

---

## 🌐 Opción 1: Interfaz Web (Recomendado)

### Paso 1: Compilar el proyecto
```bash
mvn clean compile -DskipTests
```

### Paso 2: Iniciar servidor web
```bash
# Opción A: Maven directo
mvn jetty:run -DskipTests

# Opción B: Script PowerShell (Windows)
.\start-web.ps1
```

### Paso 3: Acceder a la aplicación
Abrir navegador en: **http://localhost:8090/wallet/**

### Funcionalidades disponibles
- ✅ Consultar saldo
- ✅ Depositar fondos
- ✅ Retirar fondos
- ✅ Transferir entre cuentas
- ✅ **Historial con paginación y filtros** (por tipo, fechas)
- ✅ Crear usuarios
- ✅ Buscar usuarios
- ✅ **Listar usuarios con paginación y filtros** (por email, estado)

**Stack web:** Jakarta EE 10, Servlet 6.0, JSP 3.1, Jetty 12

---

## 💻 Opción 2: Aplicación de Consola

### Paso 1: Compilar JAR ejecutable
```bash
mvn clean package -DskipTests
```

### Paso 2: Ejecutar
```bash
java -jar target/wallet-app-1.0.0-jar-with-dependencies.jar
```

### Navegación en consola
El sistema presenta menús interactivos para:
- Gestión de usuarios
- Gestión de cuentas
- Operaciones de fondos
- Historial de transacciones

---

## 📊 Base de datos

El sistema utiliza **SQLite** con persistencia automática:
- Archivo: `wallet.db` (se crea automáticamente)
- ORM: JPA/Hibernate 6.4.4
- No requiere instalación de servidor de BD

### Ver datos (opcional)
```bash
# Instalar SQLite Viewer o usar comando:
sqlite3 wallet.db ".tables"
```

---

## 🧪 Ejecutar tests

```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=UsuarioJPARepositoryTest
```

---

## 📁 Estructura del proyecto

```
wallet/
├── src/
│   ├── main/
│   │   ├── java/com/wallet/
│   │   │   ├── domain/           # Entidades de negocio
│   │   │   ├── application/      # Casos de uso (DTOs, UseCase)
│   │   │   ├── infrastructure/   # Implementaciones (JPA, Services)
│   │   │   └── presentation/     # Interfaces (consola, web)
│   │   │       ├── console/      # Menús de consola
│   │   │       └── web/          # Servlets + JSP
│   │   ├── webapp/               # Interfaz web
│   │   │   ├── WEB-INF/
│   │   │   │   ├── views/        # JSP templates
│   │   │   │   └── web.xml       # Descriptor web
│   │   │   ├── assets/           # CSS, JS
│   │   │   └── index.jsp
│   │   └── resources/
│   │       ├── schema.sql
│   │       └── META-INF/persistence.xml
│   └── test/                     # Tests unitarios e integración
├── pom.xml                       # Dependencias Maven
└── README.md
```

---

## 🎯 Casos de uso de ejemplo

### Web: Consultar historial con filtros
1. Acceder a http://localhost:8090/wallet/historial
2. Ingresar número de cuenta: `00001`
3. Seleccionar tipo: `DEPOSITO`
4. Ingresar rango de fechas (desde/hasta)
5. Seleccionar resultados por página: `20`
6. Presionar "Consultar"
7. Navegar con botones "Anterior" / "Siguiente"

### Web: Listar usuarios con búsqueda
1. Acceder a http://localhost:8090/wallet/usuarios/lista
2. Escribir parte del email en "Buscar por email": `juan`
3. Seleccionar estado: `Solo activos`
4. Presionar "Buscar"
5. Navegar páginas con filtros mantenidos

---

## 🔧 Solución de problemas

### Puerto 8090 ocupado
```bash
# Cambiar puerto en pom.xml, buscar:
<httpConnector>
  <port>8090</port>  <!-- Cambiar a otro puerto -->
</httpConnector>
```

### Error de compilación Maven
```bash
# Limpiar caché y recompilar
mvn clean install -U -DskipTests
```

### Base de datos corrupta
```bash
# Eliminar y regenerar
rm wallet.db wallet.db-shm wallet.db-wal
# Volver a ejecutar la aplicación
```

---

## 📚 Documentación adicional

- **[ARCHITECTURE.md](./ARCHITECTURE.md)** - Diseño de capas y patrones
- **[BASE_DE_DATOS.md](./BASE_DE_DATOS.md)** - Esquema de BD y relaciones
- **[PLAN_INTEGRACION_REQUERIMIENTOS.md](./PLAN_INTEGRACION_REQUERIMIENTOS.md)** - Plan de desarrollo web
- **[PAGINACION_FILTROS_COMPLETADO.md](./PAGINACION_FILTROS_COMPLETADO.md)** - Implementación de paginación

---

## 🤝 Contribuir

1. Fork el repositorio
2. Crear rama feature: `git checkout -b feature/nueva-funcionalidad`
3. Commit cambios: `git commit -m 'feat: Agregar X'`
4. Push a la rama: `git push origin feature/nueva-funcionalidad`
5. Crear Pull Request

---

## 📄 Licencia

Este proyecto es de código abierto bajo licencia MIT.

---

## 👨‍💻 Autor

**Proyecto académico** - Billetera Digital con Clean Architecture

**GitHub:** https://github.com/margandona/wallet2.0

---

## 🎉 Estado del proyecto

✅ **Versión 1.0.0 - Producción**
- Interfaz de consola funcional
- Interfaz web MVC completa
- Paginación y filtros implementados
- Persistencia JPA/Hibernate con SQLite
- Tests unitarios y de integración
- Documentación completa
- Subido a GitHub
