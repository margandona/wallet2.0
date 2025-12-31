# 🤖 PROMPTS PARA CHATGPT - CREAR DOCUMENTO WORD

## Usa estos prompts directamente en ChatGPT para que te ayude

---

## PROMPT 1: Crear documento Word completo

```
Eres un experto en documentación técnica. Necesito que crees 
un documento Word profesional para un proyecto de base de datos.

ESTRUCTURA REQUERIDA:

1. PORTADA
   - Título: WALLET 2.0 - Billetera Digital
   - Subtítulo: Sistema de Gestión de Billetera Digital con SQLite
   - Autor: Margandona
   - Fecha: 31 de Diciembre de 2025
   - Versión: 1.0.0

2. TABLA DE CONTENIDOS

3. INTRODUCCIÓN
   Objetivo: Desarrollar un sistema de billetera digital que permita 
   a los usuarios registrarse, crear múltiples cuentas bancarias, 
   realizar transacciones (depósitos, retiros, transferencias), 
   consultar historial y convertir divisas.
   
   Alcance:
   - Gestión de Usuarios (Registro, búsqueda, listado)
   - Gestión de Cuentas (Crear, activar, consultar saldo)
   - Transacciones (Depósitos, retiros, transferencias)
   - Conversor de Divisas (30+ monedas soportadas)
   - Persistencia (Base de datos SQLite)
   - Auditoría (Historial completo de operaciones)
   
   Tecnología:
   - Lenguaje: Java 21 LTS
   - Framework ORM: Hibernate 6.4.4.Final
   - API de Persistencia: Jakarta Persistence 3.1
   - Base de Datos: SQLite 3.44.0.0
   - Herramienta de Build: Maven 3.9.6
   - Patrón: Clean Architecture (4 capas)

4. DIAGRAMA ER (ENTIDAD-RELACIÓN)
   Mostrar las 3 tablas:
   - USUARIOS (id, email, documento, nombre, apellido, tipo_documento, activo, timestamps)
   - CUENTAS (id, numero_cuenta, usuario_id FK, saldo, moneda, activa, timestamps)
   - TRANSACCIONES (id, cuenta_id FK, tipo, monto, saldo_anterior, saldo_nuevo, 
                     fecha_transaccion, cuenta_origen_id, cuenta_destino_id, timestamps)
   
   Con relaciones 1:N

5. SCRIPTS SQL
   Incluir:
   a) CREATE TABLE USUARIOS (con descripciones)
   b) CREATE TABLE CUENTAS (con descripciones)
   c) CREATE TABLE TRANSACCIONES (con descripciones)
   d) 5 consultas SELECT principales

6. EXPLICACIÓN DE FUNCIONAMIENTO
   - Flujo de crear usuario (paso a paso)
   - Flujo de crear cuenta (paso a paso)
   - Flujo de transferencia (paso a paso)
   - Características de seguridad (Integridad referencial, UNIQUE, ACID)

7. ESPACIO PARA CAPTURAS DE PANTALLA
   [Placeholder para insertar capturas de:
   - Estructura de BD en SQLite Browser
   - Esquema de tablas
   - Datos en tabla USUARIOS
   - Datos en tabla CUENTAS
   - Resultados de consultas]

8. CONCLUSIÓN
   Resumen de lo realizado, tecnologías utilizadas, y estado del proyecto.

FORMATO REQUERIDO:
- Tipo: Microsoft Word (.docx)
- Fuente: Arial, tamaño 11
- Títulos: Tamaño 14, Negrita, Color Azul
- Subtítulos: Tamaño 12, Negrita
- Código SQL: Courier New, tamaño 10, fondo gris claro
- Márgenes: 2.5 cm en todos lados
- Numeración de páginas: Pie de página
- Interlineado: 1.5

ESTILO: Profesional, académico, fácil de leer.

Genera el documento en HTML que pueda convertirse a Word,
o proporciona instrucciones paso a paso para crear en Word.
```

---

## PROMPT 2: Crear solo diagrama ER

```
Necesito un diagrama Entidad-Relación profesional para 
una base de datos SQLite con las siguientes 3 tablas:

TABLA 1: USUARIOS
Campos:
- id (VARCHAR 36) - Primary Key - UUID
- nombre (VARCHAR 100)
- apellido (VARCHAR 100)
- email (VARCHAR 100) - UNIQUE
- documento (VARCHAR 50) - UNIQUE
- tipo_documento (VARCHAR 20)
- activo (BOOLEAN)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

TABLA 2: CUENTAS
Campos:
- id (VARCHAR 36) - Primary Key - UUID
- numero_cuenta (VARCHAR 50) - UNIQUE
- usuario_id (VARCHAR 36) - Foreign Key → USUARIOS.id
- saldo (DECIMAL 19,2)
- moneda (VARCHAR 3)
- activa (BOOLEAN)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

TABLA 3: TRANSACCIONES
Campos:
- id (VARCHAR 36) - Primary Key - UUID
- cuenta_id (VARCHAR 36) - Foreign Key → CUENTAS.id
- tipo (VARCHAR 30)
- monto (DECIMAL 19,2)
- descripcion (VARCHAR 255)
- saldo_anterior (DECIMAL 19,2)
- saldo_nuevo (DECIMAL 19,2)
- fecha_transaccion (TIMESTAMP)
- cuenta_origen_id (VARCHAR 36)
- cuenta_destino_id (VARCHAR 36)
- created_at (TIMESTAMP)

RELACIONES:
- USUARIOS (1) a CUENTAS (N) - usuario_id FK - ON DELETE CASCADE
- CUENTAS (1) a TRANSACCIONES (N) - cuenta_id FK - ON DELETE CASCADE

Crea un diagrama profesional que muestre:
1. Cada tabla con sus campos y tipos
2. Claves primarias (PK)
3. Claves foráneas (FK)
4. Restricciones UNIQUE
5. Cardinalidad de relaciones (1:N)

Formato: Código para Lucidchart o DrawIO (URL que pueda copiar)
O bien: Código ASCII art bien formateado

Que sea claro y profesional para incluir en un documento Word.
```

---

## PROMPT 3: Crear scripts SQL ordenados

```
Necesito que generes los scripts SQL completos y bien documentados 
para una base de datos de billetera digital.

Incluye:

1. SCRIPTS DE CREACIÓN
   - CREATE TABLE usuarios (con restricciones)
   - CREATE TABLE cuentas (con FK y restricciones)
   - CREATE TABLE transacciones (con FK y restricciones)
   - CREATE INDEX para campos que se buscan frecuentemente

2. COMENTARIOS EN SQL
   Cada tabla debe tener comentario explicando su propósito

3. EJEMPLOS DE INSERT
   - Insert usuario de ejemplo
   - Insert cuenta de ejemplo
   - Insert transacción de ejemplo

4. CONSULTAS SELECT PRINCIPALES
   - Obtener usuario con sus cuentas
   - Consultar saldo de una cuenta específica
   - Historial de transacciones de una cuenta
   - Últimas transferencias realizadas
   - Movimientos por tipo (resumen)

5. FORMATO
   - Indentación clara
   - Palabras clave en MAYÚSCULAS
   - Comentarios explicativos
   - Listo para copiar en DB Browser o SQLite

Genera los scripts de forma que sean fáciles de copiar
y ejecutar en SQLite Browser.
```

---

## PROMPT 4: Explicación paso a paso de operaciones

```
Necesito explicaciones detalladas de cómo funcionan las operaciones 
principales en la billetera digital. Usa este formato:

OPERACIÓN: [Nombre]

ENTRADA DEL USUARIO:
[Datos que ingresa]

VALIDACIONES APLICADAS:
[Lista de validaciones]

PROCESO EN LA BASE DE DATOS:
[Paso a paso de las operaciones SQL]

GARANTÍAS ACID:
[Cómo se asegura atomicidad, consistencia, etc.]

SALIDA/RESULTADO:
[Lo que ve el usuario]

---

Genera para estas operaciones:

1. Crear Usuario
2. Crear Cuenta
3. Realizar Transferencia
4. Consultar Historial de Transacciones
5. Convertir Divisas

Usa lenguaje claro, profesional, apto para un documento académico.
```

---

## PROMPT 5: Crear conclusión del documento

```
Necesito una conclusión profesional y concisa para un documento 
sobre un proyecto de base de datos de billetera digital.

La conclusión debe incluir:

1. RESUMEN DE LOGROS
   - Sistema completo de billetera digital implementado
   - Base de datos robusta con Hibernate/JPA
   - Todas las características funcionan correctamente
   - Código limpio y documentado

2. TECNOLOGÍAS UTILIZADAS
   - Java 21 LTS
   - Hibernate ORM
   - SQLite
   - Maven

3. CARACTERÍSTICAS DESTACADAS
   - Clean Architecture
   - Transacciones ACID
   - Integridad referencial
   - Auditoría completa

4. ESTADO DEL PROYECTO
   - Production Ready
   - Código deployable

5. POSIBLES MEJORAS FUTURAS
   - API REST
   - Interfaz web
   - Autenticación
   - Reportes

Que sea de 200-300 palabras, profesional y apta para documento académico.
```

---

## PROMPT 6: Crear tabla comparativa de características

```
Crea una tabla profesional que compare las características 
de WALLET 2.0 vs otros sistemas de billetera digital.

Incluye:
- Usuarios
- Cuentas múltiples
- Transacciones
- Conversor de divisas
- BD local
- Auditoría
- Tecnología
- Escalabilidad

Formato: Tabla HTML o Markdown que pueda convertirse a Word

Haz que se vea clara y profesional.
```

---

## PROMPT 7: Crear guía de instalación y ejecución

```
Necesito una guía breve y clara para ejecutar la aplicación WALLET 2.0.

Debe incluir:

1. REQUISITOS PREVIOS
   - Java 21 JDK
   - Maven 3.9.6
   - SQLite

2. PASOS DE INSTALACIÓN
   - Descargar/clonar proyecto
   - Compilar con Maven
   - Generar JAR

3. EJECUCIÓN
   - Comando exacto para ejecutar
   - Qué esperar que vea el usuario
   - Cómo navegar por menús

4. SOLUCIÓN DE PROBLEMAS
   - Error común: "Java no encontrado"
   - Error: "Maven no instalado"
   - Error: "Puerto en uso"

Que sea concisa (máximo 1 página) y fácil de seguir.
```

---

## CÓMO USAR ESTOS PROMPTS:

### Paso 1: Acceder a ChatGPT
- Ve a https://chat.openai.com
- Inicia sesión

### Paso 2: Seleccionar el Prompt
- Elige uno de los prompts anteriores según necesites
- Cópialo completamente

### Paso 3: Pegar en ChatGPT
- Abre un nuevo chat
- Pega el prompt
- Presiona Enter

### Paso 4: Esperar Resultado
- ChatGPT generará el contenido
- Si necesitas ajustes, pide modificaciones

### Paso 5: Exportar a Word
- Copia el resultado
- Abre Microsoft Word
- Pega el contenido
- Ajusta formato según necesites

---

## ⚠️ NOTAS IMPORTANTES:

1. **Modelo Recomendado**: GPT-4 (más preciso para documentación técnica)
2. **Si es largo**: El contenido puede ser muy largo, pide que divida en secciones
3. **Formato**: Pide siempre "formato HTML" o "Markdown" para mejor exportación a Word
4. **Iteraciones**: No dudes en pedir ajustes: "Hace la tabla más simple", "Explica más detalladamente", etc.
5. **Verificación**: Verifica que la información técnica sea correcta antes de usar en tu documento final

---

## EJEMPLO DE USO:

**TÚ**: [Copias PROMPT 1 completo]

**CHATGPT**: [Genera el documento con estructura completa]

**TÚ**: "Aumenta el tamaño de fuente en los títulos a 16 y haz la portada más profesional"

**CHATGPT**: [Ajusta según tu solicitud]

**TÚ**: [Copias el resultado final]

**EN WORD**: Pegas y ajustas márgenes/números de página

---

## 🎓 ALTERNATIVA: Si ChatGPT es muy complicado

Use **Microsoft Copilot** que es más simple:
1. Ve a https://copilot.microsoft.com
2. Usa los mismos prompts
3. Genera contenido
4. Copia a Word

O use **Google Gemini** (similar):
1. https://gemini.google.com
2. Pega prompt
3. Copia resultado

---

**¡Listo! Tienes TODO para crear tu documento Word profesional!**
