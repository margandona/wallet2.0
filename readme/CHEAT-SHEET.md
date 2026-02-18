# ⚡ CHEAT SHEET - COMANDOS RÁPIDOS

## 🔨 Compilación

```bash
# Método recomendado (Windows)
.\compilar.bat

# O con Maven directo
mvn clean package -DskipTests -q
```

## ▶️ Ejecución

```bash
# Método recomendado (Windows)
.\ejecutar.bat

# O con Java directo
java -jar target/wallet-app-1.0.0-jar-with-dependencies.jar
```

## 🔄 Compilar y Ejecutar en un paso

```bash
.\compilar.bat && .\ejecutar.bat
```

## 📊 Verificar base de datos

```bash
python verify_db.py
```

## 🧹 Limpiar y recompilar

```bash
# Opción 1: Script
.\compilar.bat

# Opción 2: Maven directo
mvn clean package -DskipTests -q
```

## 📁 Archivos importantes

| Archivo | Propósito |
|---------|-----------|
| `compilar.bat` | Compila la app |
| `ejecutar.bat` | Ejecuta la app |
| `pom.xml` | Configuración de Maven |
| `wallet.db` | Base de datos SQLite (se crea automáticamente) |
| `target/wallet-app-1.0.0-jar-with-dependencies.jar` | JAR ejecutable |

## 🐛 Troubleshooting

### Error: "mvn is not recognized"
```bash
# Maven no está en el PATH
# Solución: Instalar Maven y agregarlo al PATH
```

### Error: "java is not recognized"
```bash
# Java no está en el PATH
# Solución: Instalar Java 21 y agregarlo al PATH
```

### Error: "No se encontró el JAR"
```bash
# Necesitas compilar primero
.\compilar.bat
```

---

**Hecho con ❤️ para el proyecto Wallet**
