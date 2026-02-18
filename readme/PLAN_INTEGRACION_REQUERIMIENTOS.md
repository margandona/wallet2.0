# PLAN DE INTEGRACION DE REQUERIMIENTOS - AlkeWallet

Este documento integra los requerimientos nuevos con el estado actual del proyecto y propone fases de trabajo coherentes y consistentes para migrar a un backend web MVC con JSP.

---

## 1) Requerimientos recibidos (resumen)

**Requerimientos generales**
- Administracion de fondos: ver saldo, realizar depositos y retiros.

**Requerimientos tecnicos/especificos**
- Backend: Dynamic Web Project Java con patron MVC para desarrollar y gestionar la logica del negocio.
- Frontend: JSP para desarrollar la interfaz de usuario (vistas).

---

## 2) Estado actual del proyecto (resumen tecnico)

**Lo que ya existe**
- Casos de uso para depositos, retiros, transferencias y consultas de saldo.
- Persistencia con JPA/Hibernate y SQLite.
- Arquitectura limpia con capas Domain, Application, Infrastructure y Presentation.
- UI actual: consola interactiva (menus y controllers de consola).

**Lo que falta para cumplir requerimientos**
- Capa web (servlets/controladores MVC) y vistas JSP.
- Empaquetado web (WAR) y estructura de Dynamic Web Project.
- Flujo web para usuarios, cuentas y transacciones.
- Validaciones y manejo de errores en la capa web.

---

## 3) Estrategia de integracion

- Mantener el nucleo de negocio (Domain + Application) intacto.
- Reemplazar/expandir la capa de presentacion hacia MVC web.
- Agregar una capa web con servlets como Controllers y JSP como Views.
- Adaptar la configuracion del proyecto a Dynamic Web Project (WAR).

---

## 4) Fases de trabajo

### Fase 0 - Alineacion tecnica y alcance
**Objetivo**: Definir el alcance minimo para web MVC y preparar el plan de migracion.
- Confirmar flujos obligatorios: saldo, deposito, retiro.
- Definir alcance opcional: transferencias, historial, usuarios.
- Definir servidor web objetivo (Tomcat/Jetty) y version.
- Validar si se mantiene Maven o se migra a estructura Web Project.

**Entregables**
- Alcance aprobado.
- Decision de servidor y empaquetado (WAR).

---

### Fase 1 - Base de proyecto web MVC
**Objetivo**: Habilitar la aplicacion como proyecto web MVC.
- Configurar `pom.xml` para empaquetado `war`.
- Estructura web: `src/main/webapp/WEB-INF`.
- Agregar `web.xml` y configuracion de servlets.
- Crear controlador base y pagina JSP inicial.

**Entregables**
- Aplicacion web desplegable en servidor.
- Pagina inicial accesible.

---

### Fase 2 - Funcionalidad base de fondos
**Objetivo**: Implementar las funciones obligatorias en web.
- Vista JSP para consultar saldo.
- Vista JSP para deposito.
- Vista JSP para retiro.
- Controllers MVC y mapeo de rutas.
- Validaciones basicas y mensajes de error.

**Entregables**
- Flujos de saldo, deposito y retiro funcionando en web.

---

### Fase 3 - Funcionalidad extendida
**Objetivo**: Completar el alcance opcional o recomendado.
- Transferencias entre cuentas (si se confirma en alcance).
- Historial de transacciones.
- Gestion basica de usuarios (buscar/crear).

**Entregables**
- Flujos extendidos implementados y navegables.

---

### Fase 4 - Calidad, seguridad y consistencia
**Objetivo**: Asegurar estabilidad y consistencia en la app web.
- Manejo de errores en MVC (404/500).
- Validaciones de entrada en formularios.
- Prevencion basica de errores comunes (monto negativo, cuenta invalida, etc.).
- Revisar mensajes de error y confirmacion.

**Entregables**
- Experiencia consistente y robusta para usuario.

---

### Fase 5 - Documentacion y cierre
**Objetivo**: Dejar documentado el uso y despliegue web.
- Guia de despliegue web (Tomcat/Jetty).
- Actualizar README con modo web.
- Actualizar guias de uso para la interfaz JSP.

**Entregables**
- Documentacion completa de despliegue y uso web.

---

## 5) Criterios de aceptacion (minimos)

- Consultar saldo desde interfaz JSP.
- Depositar fondos desde interfaz JSP con validaciones.
- Retirar fondos desde interfaz JSP con validaciones.
- Aplicacion web desplegable en servidor.
- Mensajes claros de exito y error.

---

## 6) Notas y dependencias

- Mantener la arquitectura limpia para no reescribir el nucleo.
- La capa web se considera una nueva Presentacion MVC.
- La UI de consola puede mantenerse como alternativa (opcional).
