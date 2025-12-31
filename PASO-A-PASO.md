# 📚 GUÍA PASO A PASO - COMPILAR Y EJECUTAR LA APP

## 🎯 Objetivo
Aprenderás a compilar y ejecutar la aplicación Wallet en Windows.

---

## ✅ Paso 1: Verificar Requisitos

Abre **PowerShell** y verifica que tienes las herramientas necesarias:

### Verificar Java
```powershell
java -version
```
**Esperado:** `java version "21.x.x"`

Si no tienes Java:
1. Descarga [Java 21 LTS](https://www.oracle.com/java/technologies/downloads/#java21)
2. Instala siguiendo el instalador
3. Reinicia PowerShell y prueba de nuevo

### Verificar Maven
```powershell
mvn -version
```
**Esperado:** `Apache Maven 3.9.x`

Si no tienes Maven:
1. Descarga [Maven](https://maven.apache.org/download.cgi)
2. Descomprime en una carpeta (ej: `C:\maven`)
3. Agrega al PATH:
   - Click derecho en "Este PC" → Propiedades
   - Variables de entorno → Nueva variable del sistema
   - Nombre: `MAVEN_HOME`, Valor: `C:\maven`
   - Agregar `%MAVEN_HOME%\bin` al PATH
4. Reinicia PowerShell y prueba de nuevo

---

## 📁 Paso 2: Navegar al Proyecto

Abre PowerShell y ve a la carpeta del proyecto:

```powershell
cd "C:\Users\marga\Desktop\NeekWorld\boot android\wallet"
```

**Verifica que ves estos archivos:**
```powershell
ls
```

Deberías ver:
- `pom.xml` ✓
- `compilar.bat` ✓
- `ejecutar.bat` ✓
- `src/` carpeta
- `target/` carpeta

---

## 🔨 Paso 3: Compilar la Aplicación

### Opción A: Usar el script (RECOMENDADO - Una línea)

```powershell
.\compilar.bat
```

**Verás algo como:**
```
============================================================================
 COMPILACION DE WALLET APPLICATION
============================================================================

[OK] Archivo pom.xml encontrado
[OK] Maven disponible: Apache Maven 3.9.6
[OK] Limpieza completada
[Compilando...]
[OK] Compilacion completada exitosamente!
[OK] JAR generado: wallet-app-1.0.0-jar-with-dependencies.jar
   Tamano:     30.6 MB
```

**Tiempo:** 30-60 segundos (la primera vez tarda más)

### Opción B: Usar Maven directamente

```powershell
mvn clean package -DskipTests -q
```

---

## ▶️ Paso 4: Ejecutar la Aplicación

### Opción A: Usar el script (RECOMENDADO - Una línea)

```powershell
.\ejecutar.bat
```

**Verás algo como:**
```
============================================================================
 EJECUTAR WALLET APPLICATION
============================================================================

[OK] Archivo JAR encontrado
   Ubicacion: ...\wallet-app-1.0.0-jar-with-dependencies.jar

Iniciando aplicacion...

============================================================================

[INFO] Bienvenido al sistema de gestion de billetera digital

Presione Enter para continuar...
```

### Opción B: Usar Java directamente

```powershell
java -jar target/wallet-app-1.0.0-jar-with-dependencies.jar
```

---

## 🚀 Paso 5: Usar la Aplicación

Cuando se inicie, verás el menú:

```
========================================
  WALLET - Billetera Digital
  Sistema de Gestion v1.0.0
========================================

MENU PRINCIPAL
========================================

1. Gestion de Usuarios
2. Gestion de Cuentas
3. Transacciones
4. Consultas
5. Conversor de Divisas
0. Salir

Seleccione una opcion:
```

### Ejemplo de prueba rápida:

1. **Opción 1**: Crear usuario
   - Nombre: `juan`
   - Apellido: `perez`
   - Email: `juan@perez.cl`
   - CEDULA: `123456789`

2. **Opción 2**: Gestión de Cuentas → Crear Cuenta
   - Email: `juan@perez.cl`
   - Se genera cuenta con número automático

3. **Opción 2**: Gestión de Cuentas → Depositar
   - Número de cuenta: `[el número mostrado]`
   - Monto: `100000`
   - ✓ Dinero depositado

---

## ⏹️ Paso 6: Cerrar la Aplicación

En el menú principal:
```
Seleccione una opcion: 0
```

**Verás:**
```
✓ Aplicacion cerrada exitosamente
```

---

## 🔄 Resumen: Comandos Principales

```powershell
# Navegar al proyecto
cd "C:\Users\marga\Desktop\NeekWorld\boot android\wallet"

# Compilar
.\compilar.bat

# Ejecutar
.\ejecutar.bat

# Compilar y ejecutar en un paso
.\compilar.bat; .\ejecutar.bat

# Verificar base de datos
python verify_db.py

# Limpiar y recompilar (si hay errores)
mvn clean package -DskipTests -q
```

---

## 📝 Notas Importantes

1. **Primera compilación**: Tarda más porque descarga todas las dependencias
2. **Compilaciones siguientes**: Más rápidas (10-30 segundos)
3. **Base de datos**: Se crea automáticamente como `wallet.db` en la primera ejecución
4. **JAR ejecutable**: Se genera en `target/wallet-app-1.0.0-jar-with-dependencies.jar`

---

## ✨ Cambios Recientes

✅ **Transacciones mejoradas**: Ahora usan número de cuenta en lugar de ID largo
- Antes: `ID cuenta: 550e8400-e29b-41d4-a716-446655440000`
- Ahora: `Número de cuenta: 1234567890`

✅ **Scripts creados**: `compilar.bat` y `ejecutar.bat` para facilitar el uso

✅ **Documentación completa**: Guías de compilación y ejecución

---

## 🆘 Problemas Comunes

### "mvn is not recognized"
**Solución:** Maven no está instalado o no está en el PATH
- Instalar Maven desde [maven.apache.org](https://maven.apache.org)
- Agregar al PATH

### "java is not recognized"
**Solución:** Java no está instalado o no está en el PATH
- Instalar Java 21 desde [oracle.com](https://www.oracle.com/java/technologies/downloads/#java21)
- Agregar al PATH

### "El archivo JAR no se encontró"
**Solución:** Compilar primero con `.\compilar.bat`

### "La compilación tarda mucho"
**Solución:** Normal en primera ejecución. Las siguientes serán más rápidas.

---

**¿Necesitas más ayuda?** Consulta `COMPILAR_Y_EJECUTAR.md` para una guía más completa.
