# 🔨 GUÍA DE COMPILACIÓN Y EJECUCIÓN - WALLET APP

## 📋 Índice
1. [Requisitos](#requisitos)
2. [Scripts disponibles](#scripts-disponibles)
3. [Cómo compilar](#cómo-compilar)
4. [Cómo ejecutar](#cómo-ejecutar)
5. [Solución de problemas](#solución-de-problemas)

---

## 📦 Requisitos

Antes de compilar, asegúrate de tener instalado:

### Java 21 LTS
```bash
java -version
```
Debe mostrar: `java version "21.x.x"`

### Maven 3.9.6+
```bash
mvn -version
```
Debe mostrar: `Apache Maven 3.9.x`

Si no tienes estas herramientas instaladas:
- [Descargar Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Descargar Maven](https://maven.apache.org/download.cgi)

---

## 🛠️ Scripts disponibles

### 1️⃣ `compilar.ps1` - Script de Compilación
Compila la aplicación y genera el JAR ejecutable.

**Características:**
- ✅ Valida la instalación de Maven
- ✅ Limpia compilaciones anteriores
- ✅ Compila sin ejecutar tests (`-DskipTests`)
- ✅ Genera JAR con todas las dependencias incluidas
- ✅ Muestra tiempo de compilación y tamaño del JAR

### 2️⃣ `ejecutar.ps1` - Script de Ejecución
Ejecuta la aplicación compilada.

**Características:**
- ✅ Verifica que el JAR exista
- ✅ Inicia la aplicación
- ✅ Si falta el JAR, sugiere compilar primero

---

## 🔨 Cómo compilar

### Opción 1: Usar el script (RECOMENDADO)

**Paso 1:** Abre PowerShell en la carpeta del proyecto
```bash
# En Windows, navega a la carpeta y escribe PowerShell en la barra de dirección
# O abre PowerShell y ejecuta:
cd "C:\Users\marga\Desktop\NeekWorld\boot android\wallet"
```

**Paso 2:** Ejecuta el script de compilación
```bash
.\compilar.ps1
```

**Resultado esperado:**
```
╔════════════════════════════════════════════════════════════════════════════╗
║ 🔨 COMPILACIÓN DE WALLET APPLICATION                                     ║
╚════════════════════════════════════════════════════════════════════════════╝

  [1/4] Verificando Maven...
  ✓ Maven disponible: Apache Maven 3.9.6

  [2/4] Preparando compilación...

  [3/4] Limpiando compilaciones anteriores...
  ✓ Limpieza completada

  [4/4] Compilando aplicación...

  ✓ ¡Compilación completada exitosamente!
  ✓ JAR generado: wallet-app-1.0.0-jar-with-dependencies.jar
     Tamaño:     42.53 MB
     Ubicación:  C:\...\target\wallet-app-1.0.0-jar-with-dependencies.jar
```

### Opción 2: Usar Maven directamente

Si prefieres compilar manualmente sin usar el script:

```bash
# Navegadera a la carpeta del proyecto
cd "C:\Users\marga\Desktop\NeekWorld\boot android\wallet"

# Compilar sin tests
mvn clean package -DskipTests -q

# O compilar con tests
mvn clean package
```

---

## ▶️ Cómo ejecutar

### Opción 1: Usar el script (RECOMENDADO)

```bash
.\ejecutar.ps1
```

**Resultado:**
```
╔════════════════════════════════════════════════════════════════════════════╗
║ ▶️  EJECUTAR WALLET APPLICATION                                           ║
╚════════════════════════════════════════════════════════════════════════════╝

  ℹ Buscando archivo ejecutable...
  ✓ Archivo JAR encontrado
  Tamaño: 42.53 MB

  ℹ Iniciando aplicación...

  ════════════════════════════════════════════════════════════════════════════

  [Aquí aparece el menú de la aplicación]
```

### Opción 2: Ejecutar directamente con Java

```bash
java -jar target/wallet-app-1.0.0-jar-with-dependencies.jar
```

---

## 🔄 Flujo Completo: Compilar y Ejecutar

### Método 1: Pasos separados
```bash
# Primero, compilar
.\compilar.ps1

# Después, ejecutar
.\ejecutar.ps1
```

### Método 2: Todo en un comando
```bash
.\compilar.ps1; .\ejecutar.ps1
```

---

## ⚡ Ejemplo Paso a Paso

```powershell
# 1. Abrir PowerShell y navegar
PS C:\Users\marga\Desktop\NeekWorld\boot android\wallet>

# 2. Compilar
PS C:\...> .\compilar.ps1

# 3. Esperar a que compile (1-2 minutos)

# 4. Ver el mensaje ✓ ¡Compilación completada exitosamente!

# 5. Ejecutar
PS C:\...> .\ejecutar.ps1

# 6. Ver el menú principal de la aplicación
```

---

## 🛠️ Solución de Problemas

### Problema: "No se puede cargar el archivo .ps1 porque la ejecución de scripts está deshabilitada"

**Solución:**
```powershell
# Ejecutar PowerShell como administrador y ejecutar:
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Luego confirmar con: Y
```

### Problema: "Maven no está instalado o no está en el PATH"

**Solución:**
1. Descargar Maven desde [maven.apache.org](https://maven.apache.org/download.cgi)
2. Descomprimir en una carpeta (ej: `C:\maven`)
3. Agregar a la variable de entorno PATH:
   - Click derecho en "Este PC" → Propiedades
   - Variables de entorno → Nueva variable
   - Nombre: `MAVEN_HOME`, Valor: `C:\maven`
   - Agregar `%MAVEN_HOME%\bin` al PATH

### Problema: "Java no se reconoce"

**Solución:**
Similar a Maven, verificar que Java está en el PATH.

### Problema: "El archivo JAR es demasiado grande / La compilación tarda mucho"

**Solución:**
Esto es normal. El JAR incluye todas las dependencias. La primera compilación tarda más.

---

## 📊 Información de Compilación

| Aspecto | Valor |
|---------|-------|
| Framework | Maven 3.9.6 |
| Java | 21 LTS |
| JAR Size | ~42-45 MB |
| Tiempo compilación | 30-60 segundos |
| Dependencias | Incluidas en JAR |
| Tests | Saltados con `-DskipTests` |

---

## 🎯 Comando Rápido de Referencia

```bash
# Compilar
.\compilar.ps1

# Ejecutar
.\ejecutar.ps1

# Compilar y ejecutar en una línea
.\compilar.ps1; .\ejecutar.ps1

# Compilar con Maven directo
mvn clean package -DskipTests -q

# Ejecutar JAR directo
java -jar target/wallet-app-1.0.0-jar-with-dependencies.jar
```

---

## 📝 Notas Importantes

1. **Limpieza automática:** El script `compilar.ps1` limpia compilaciones anteriores automáticamente
2. **Tests deshabilitados:** Se usa `-DskipTests` para compilar más rápido. Si necesitas ejecutar tests, elimina esa opción
3. **Ubicación del JAR:** Siempre se genera en `target/wallet-app-1.0.0-jar-with-dependencies.jar`
4. **Base de datos:** Se crea automáticamente `wallet.db` cuando ejecutas la app por primera vez

---

**¿Preguntas?** Revisa los logs de compilación o ejecuta manualmente para ver mensajes de error más detallados.
