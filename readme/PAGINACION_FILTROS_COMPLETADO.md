# Implementación de Paginación y Filtros - Fase 4

## 📋 Resumen ejecutivo
Se ha implementado paginación simple y filtros en los dos listados principales de la aplicación web: historial de transacciones y listado de usuarios.

## ✅ Funcionalidades implementadas

### 1. Historial de transacciones (`/historial`)

**Paginación:**
- Parámetros: `pagina` (número de página, por defecto 1) y `tamano` (items por página, por defecto 10, máximo 20)
- Controles: Botones "← Anterior" y "Siguiente →" que aparecen solo cuando hay múltiples páginas
- Metadata: Muestra "Página X de Y" y total de transacciones

**Filtros:**
- **Tipo de transacción**: Selector con opciones TODOS, DEPOSITO, RETIRO, TRANSFERENCIA_ENVIADA, TRANSFERENCIA_RECIBIDA
- **Rango de fechas**: Campos datetime-local para "Desde" y "Hasta"
- Los filtros se mantienen al cambiar de página (hidden inputs en formularios de paginación)

**Validación:**
- El número de cuenta es obligatorio
- Las fechas inválidas son ignoradas silenciosamente
- Página/tamaño fuera de rango se ajustan automáticamente

### 2. Listado de usuarios (`/usuarios/lista`)

**Paginación:**
- Parámetros: `pagina` (número de página, por defecto 1) y `tamano` (items por página, por defecto 10, máximo 20)
- Controles: Botones "← Anterior" y "Siguiente →" que aparecen solo cuando hay múltiples páginas
- Metadata: Muestra "Página X de Y" y total de usuarios

**Filtros:**
- **Email**: Campo de texto para búsqueda por contención (case-insensitive)
- **Estado**: Selector con opciones TODOS, ACTIVOS, INACTIVOS
- Los filtros se mantienen al cambiar de página

**Validación:**
- Sin campos obligatorios (carga todos los usuarios por defecto)
- Email vacío no aplica filtro
- Estado "TODOS" muestra activos e inactivos

## 🏗️ Arquitectura de la implementación

### Cambios en servlets

**HistorialServlet.java:**
```java
- parsePagina(HttpServletRequest): int
  → Extrae y valida parámetro 'pagina', por defecto 1, mínimo 1

- parseTamano(HttpServletRequest): int
  → Extrae y valida parámetro 'tamano', por defecto 10, mínimo 1, máximo 20

- aplicarFiltros(List<TransaccionDTO>, String, String, String): List<TransaccionDTO>
  → Filtra por tipo de transacción
  → Filtra por fecha inicio (>=)
  → Filtra por fecha fin (<=)
  → Usa Java Streams y LocalDateTime para comparaciones

- doPost(...): void
  → Obtiene todas las transacciones del servicio
  → Aplica filtros
  → Calcula paginación in-memory (subList)
  → Establece atributos: transacciones, numeroCuenta, paginaActual, tamano, totalTransacciones, totalPaginas, filtros
```

**UsuarioListarServlet.java:**
```java
- procesarListado(HttpServletRequest, HttpServletResponse): void
  → Unifica lógica de doGet y doPost

- parsePagina(HttpServletRequest): int
  → Extrae y valida parámetro 'pagina'

- parseTamano(HttpServletRequest): int
  → Extrae y valida parámetro 'tamano'

- aplicarFiltros(List<UsuarioDTO>, String, String): List<UsuarioDTO>
  → Filtra por email contenido (case-insensitive)
  → Filtra por estado (ACTIVOS/INACTIVOS según campo isActivo)
  → Usa Java Streams

- doGet/doPost(...): void
  → Llaman a procesarListado()
  → Obtiene todos los usuarios del servicio
  → Aplica filtros
  → Calcula paginación in-memory (subList)
  → Establece atributos: usuarios, paginaActual, tamano, totalUsuarios, totalPaginas, filtros
```

### Cambios en vistas JSP

**historial.jsp:**
- Formulario principal con campos de filtrado (tipo, fechaInicio, fechaFin, tamano)
- Campo hidden "pagina" con valor 1 (resetea al filtrar)
- Preserva valor de numeroCuenta en el input
- Tabla con mensaje "No hay transacciones para mostrar" cuando lista vacía
- Sección de paginación con dos formularios (Anterior/Siguiente):
  - Cada uno envía pagina±1, tamano, y todos los filtros como hidden inputs
  - Solo aparecen si `totalPaginas > 1`
  - Botón Anterior solo si `paginaActual > 1`
  - Botón Siguiente solo si `paginaActual < totalPaginas`

**usuarios_lista.jsp:**
- Formulario con campos de filtrado (email, estado, tamano)
- Campo hidden "pagina" con valor 1 (resetea al filtrar)
- Tabla con mensaje "No se encontraron usuarios" cuando lista vacía
- Sección de paginación con dos formularios (Anterior/Siguiente):
  - Cada uno envía pagina±1, tamano, email, estado como hidden inputs
  - Solo aparecen si `totalPaginas > 1`
  - Botones condicionales según página actual

## 🔧 Decisiones técnicas

### Paginación in-memory vs. base de datos
**Enfoque seleccionado:** Paginación in-memory usando `List.subList()`

**Justificación:**
- Los servicios ya devuelven listas completas (`consultarHistorial()`, `obtenerTodos()`)
- Para datasets pequeños/medianos (< 1000 registros), el overhead es mínimo
- No requiere modificar capa de repositorio/queries JPA
- Mantiene separación de responsabilidades: servlet maneja presentación
- Prototipo rápido que funciona inmediatamente

**Trade-offs conocidos:**
- ❌ No escala para miles de registros (cargaría todos en memoria)
- ❌ Ordenamiento no configurable (orden devuelto por servicio)
- ✅ Implementación simple y rápida
- ✅ No rompe servicios existentes
- ✅ Fácil de migrar a DB pagination más adelante

### Filtrado mediante Streams
**Enfoque:** Filtros aplicados con `stream().filter().collect()` post-consulta

**Justificación:**
- Permite combinar múltiples filtros de forma declarativa
- Reutiliza DTOs sin mapeos adicionales
- Consistente con enfoque in-memory de paginación

### Preservación de filtros en navegación
**Enfoque:** Hidden inputs en formularios de paginación

**Ventajas:**
- Mantiene estado sin sesión
- Permite copiar/pegar URLs con filtros (si convertimos a GET)
- No requiere JavaScript

## 📊 Límites y validaciones

| Parámetro | Mínimo | Máximo | Por defecto |
|-----------|--------|--------|-------------|
| pagina    | 1      | ∞      | 1           |
| tamano    | 1      | 20     | 10          |

**Validaciones automáticas:**
- Página < 1 → ajustada a 1
- Tamaño > 20 → ajustado a 20
- Tamaño < 1 → ajustado a 1
- Página > totalPaginas → muestra lista vacía (sin error)

## 🧪 Testing manual

### Historial de transacciones
1. ✅ Acceder a http://localhost:8090/wallet/historial
2. ✅ Ingresar número de cuenta existente
3. ✅ Cambiar "Resultados por página" a 20
4. ✅ Seleccionar tipo "DEPOSITO"
5. ✅ Verificar filtrado correcto
6. ✅ Navegar con botones Anterior/Siguiente
7. ✅ Verificar que filtros se mantienen entre páginas
8. ✅ Agregar rango de fechas y verificar filtrado combinado

### Listado de usuarios
1. ✅ Acceder a http://localhost:8090/wallet/usuarios/lista
2. ✅ Dejar filtros vacíos y presionar "Buscar" → muestra todos
3. ✅ Escribir parte de un email en "Buscar por email"
4. ✅ Verificar filtrado case-insensitive
5. ✅ Cambiar estado a "Solo activos" o "Solo inactivos"
6. ✅ Verificar filtrado por estado
7. ✅ Combinar ambos filtros
8. ✅ Navegar páginas manteniendo filtros

## 🚀 Servidor en ejecución

**Puerto:** 8090  
**URL base:** http://localhost:8090/wallet/

**Rutas con paginación/filtros:**
- `/historial` - POST con numeroCuenta, pagina, tamano, tipo, fechaInicio, fechaFin
- `/usuarios/lista` - GET/POST con pagina, tamano, email, estado

**Script de inicio:**
```powershell
.\start-web.ps1
# O directamente:
mvn jetty:run -DskipTests
```

## 📝 Próximos pasos sugeridos (fuera de alcance actual)

### Mejoras UX
- [ ] Agregar ordenamiento por columna en tablas
- [ ] Selector de página directa (ir a página N)
- [ ] Resaltar términos de búsqueda en resultados
- [ ] Exportar resultados a CSV/PDF

### Mejoras técnicas
- [ ] Migrar a paginación a nivel de base de datos (JPA Pageable)
- [ ] Agregar caché de consultas frecuentes
- [ ] Convertir formularios POST a GET para URLs con estado
- [ ] Agregar tests automatizados para servlets

### Mejoras funcionales
- [ ] Filtro por monto (rango min/max) en historial
- [ ] Filtro por nombre en usuarios
- [ ] Búsqueda full-text en descripciones

## 📄 Archivos modificados

```
src/main/java/com/wallet/presentation/web/
  ├── HistorialServlet.java          [MODIFICADO] - Agregada paginación y filtros
  └── UsuarioListarServlet.java      [MODIFICADO] - Agregada paginación y filtros

src/main/webapp/WEB-INF/views/
  ├── historial.jsp                  [MODIFICADO] - Controles UI paginación/filtros
  └── usuarios_lista.jsp             [MODIFICADO] - Controles UI paginación/filtros

start-web.ps1                        [NUEVO] - Script de inicio rápido
```

## ✨ Resumen de capacidades

**Antes:**
- Historial: Mostraba todas las transacciones sin límite
- Usuarios: Mostraba solo usuarios activos, sin filtros ni paginación

**Después:**
- Historial: Paginación configurable (10/20), filtros por tipo y fechas, navegación fluida
- Usuarios: Paginación configurable (10/20), filtros por email y estado, carga todos por defecto

**Experiencia de usuario:**
- Formularios intuitivos con selectores dropdown
- Botones de navegación solo cuando son necesarios
- Metadata informativa (página actual, total)
- Filtros persistentes entre páginas
- Sin JavaScript requerido

---

**Fecha de implementación:** 17 de febrero de 2026  
**Estado:** ✅ Completado y servidor corriendo en puerto 8090
