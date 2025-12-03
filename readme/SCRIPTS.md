# 🚀 Scripts de Utilidad

Este directorio contiene scripts para facilitar el desarrollo sin necesidad de Maven instalado.

## 📜 Scripts Disponibles

### `compile.ps1`
Compila todo el código fuente del proyecto.

**Uso:**
```powershell
.\compile.ps1
```

**Qué hace:**
- Limpia el directorio `target/`
- Crea la estructura de directorios necesaria
- Compila todos los archivos `.java` del proyecto
- Muestra mensajes de éxito o error

### `run.ps1`
Ejecuta la aplicación principal.

**Uso:**
```powershell
.\run.ps1
```

**Requisitos:**
- El proyecto debe estar compilado primero (ejecutar `compile.ps1`)

### `build-and-run.ps1`
Compila y ejecuta la aplicación en un solo comando.

**Uso:**
```powershell
.\build-and-run.ps1
```

## 💡 Notas

- Estos scripts están diseñados para **Windows PowerShell**
- Requieren **Java 17 o superior** instalado y en el PATH
- Son una alternativa cuando Maven no está disponible
- Para proyectos grandes, se recomienda usar Maven

## 🔧 Solución de Problemas

### Error: "No se puede ejecutar scripts"
Si recibes un error sobre políticas de ejecución, ejecuta:
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Error: "javac no reconocido"
Asegúrate de tener Java JDK instalado y en el PATH del sistema.

## 📦 Alternativa con Maven

Si tienes Maven instalado, puedes usar los comandos estándar:
```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Empaquetar
mvn package

# Ejecutar
mvn exec:java -Dexec.mainClass="com.wallet.Main"
```
