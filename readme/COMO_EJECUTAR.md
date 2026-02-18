# GUÍA RÁPIDA: Cómo Ejecutar WALLET

## Opción 1: Ejecución Rápida (Recomendado)

### Desde PowerShell o CMD:

```powershell
cd "C:\Users\marga\Desktop\NeekWorld\boot android\wallet"
java -jar "target/wallet-app-1.0.0-jar-with-dependencies.jar"
```

### Explicación:
- `cd` - Cambia al directorio del proyecto
- `java -jar` - Ejecuta el archivo JAR empaquetado
- El JAR contiene la aplicación compilada + todas las dependencias

---

## Opción 2: Compilar y Ejecutar

Si quieres recompilar desde el código fuente:

```powershell
cd "C:\Users\marga\Desktop\NeekWorld\boot android\wallet"
mvn clean package -DskipTests -q
java -jar "target/wallet-app-1.0.0-jar-with-dependencies.jar"
```

---

## ¿Qué Sucede al Iniciar?

1. **Inicialización JPA/Hibernate**
   ```
   ? WALLET - SISTEMA DE BILLETERA DIGITAL
   ? Inicializando aplicación...
   ```

2. **Inicialización de Base de Datos**
   ```
   ? Inicializando base de datos...
   ? Inicialización completada.
   ```
   
   Se crea/valida `wallet.db` con las tablas:
   - `usuarios` - Datos de usuarios
   - `cuentas` - Cuentas bancarias
   - `transacciones` - Historial de operaciones

3. **Interfaz Principal**
   ```
   ════════════════════════════════════════
     WALLET - Billetera Digital
     Sistema de Gestion v1.0.0
   ════════════════════════════════════════

   [INFO] Bienvenido al sistema de gestion de billetera digital

   1. Gestion de Usuarios
   2. Gestion de Cuentas
   3. Transacciones
   4. Consultas
   5. Conversor de Divisas
   0. Salir

   Seleccione una opcion: _
   ```

---

## Menú Principal

### 1. Gestion de Usuarios
```
Registrar Nuevo Usuario
- Solicita: Nombre, Apellido, Email, Documento, Tipo Documento
- Valida: Email único, Documento único
- Crea cuenta de usuario en BD

Listar Usuarios Activos
- Muestra todos los usuarios registrados
- Información: Nombre, Email, Documento

Buscar Usuario por Email
- Búsqueda por email exacto
```

### 2. Gestion de Cuentas
```
Crear Nueva Cuenta
- Selecciona usuario propietario
- Ingresa moneda (USD, CLP, EUR, PEN, etc.)
- Establece saldo inicial
- Asigna número de cuenta único (10 dígitos)

Listar Cuentas de Usuario
- Muestra todas las cuentas del usuario
- Información: Número, Saldo, Moneda, Estado
```

### 3. Transacciones
```
Transferir Dinero
- Ingresa número de cuenta origen
- Ingresa número de cuenta destino
- Ingresa monto a transferir
- Sistema genera 2 transacciones automáticas:
  * TRANSFERENCIA_SALIDA (cuenta origen)
  * TRANSFERENCIA_ENTRADA (cuenta destino)

Ver Historial de Transacciones
- Lista todas las operaciones de una cuenta
- Muestra: Fecha, Tipo, Monto, Saldo Anterior, Saldo Nuevo

Ver Ultimas Transacciones
- Últimas 10 operaciones registradas
```

### 4. Consultas
```
Consultar Saldo de Cuenta
- Ingresa número de cuenta
- Muestra saldo actual y moneda

Ver Historial de Transacciones
- Búsqueda de historial

Ver Ultimas Transacciones
- Últimas operaciones

Buscar Usuario por Email
- Búsqueda de usuario

Buscar Cuenta por Número
- Búsqueda de cuenta
```

### 5. Conversor de Divisas
```
Convertir divisa
- Ingresa cantidad a convertir
- Selecciona moneda origen (30 opciones disponibles)
- Selecciona moneda destino
- Sistema obtiene tasa de cambio en tiempo real
- Muestra resultado de conversión

Monedas disponibles (30 total):
Principales Mundiales:
- USD (Dólar Estadounidense)
- EUR (Euro)
- GBP (Libra Esterlina)
- JPY (Yen Japonés)
- CHF (Franco Suizo)
- CNY (Yuan Chino)
- SGD (Dólar Singapur)
- HKD (Dólar Hong Kong)
- AUD (Dólar Australiano)
- CAD (Dólar Canadiense)
- NZD (Dólar Nueva Zelanda)
- INR (Rupia India)
- KRW (Won Coreano)
- AED (Dirham EAU)
- ZAR (Rand Sudáfrica)

Latinoamérica (Importante):
- MXN (Peso Mexicano)
- BRL (Real Brasileño)
- PEN (Sol Peruano)
- CLP (Peso Chileno)
- COP (Peso Colombiano)
- ARS (Peso Argentino)
- UYU (Peso Uruguayo)
- PYG (Guaraní Paraguayo)
- BOB (Boliviano)
- VES (Bolívar Venezolano)
- GTQ (Quetzal Guatemalteco)
- HNL (Lempira Hondureño)
- CRC (Colón Costarricense)
- PAN (Balboa Panameño)

Ver monedas disponibles
- Lista todas las monedas soportadas

Verificar disponibilidad del servicio
- Comprueba que la API de tasas está accesible
```

---

## Ejemplo de Uso Completo

### Crear Usuario
```
Seleccione una opcion: 1
Seleccione una opcion: 1

Ingrese nombre: Juan
Ingrese apellido: Pérez
Ingrese email: juan@example.com
Ingrese documento: 12345678
Seleccione tipo de documento:
 1. CEDULA
 2. PASAPORTE
 3. RUT
Opcion: 1

✅ Usuario registrado exitosamente
ID: 550e8400-e29b-41d4-a716-446655440000
```

### Crear Cuenta
```
Seleccione una opcion: 2
Seleccione una opcion: 1

Usuarios disponibles:
 1. Juan Pérez (juan@example.com)
Seleccione usuario: 1

Ingrese moneda (USD/EUR/CLP/etc): USD
Ingrese saldo inicial: 5000

✅ Cuenta creada exitosamente
Número de cuenta: 1234567890
Saldo: 5000.00 USD
```

### Realizar Transferencia
```
Seleccione una opcion: 3
Seleccione una opcion: 1

Numero de cuenta origen: 1234567890
Numero de cuenta destino: 0987654321
Monto a transferir: 1000
Descripcion: Pago de servicios

Confirma transferencia de 1000.00 USD? (s/n): s

✅ Transferencia ejecutada exitosamente
- Cuenta origen: 1234567890 | Saldo anterior: 5000.00 → Nuevo: 4000.00
- Cuenta destino: 0987654321 | Saldo anterior: 2000.00 → Nuevo: 3000.00
```

### Conversor de Divisas
```
Seleccione una opcion: 5
Seleccione una opcion: 1

Cantidad: 500

Monedas comunes (o ingrese código ISO 4217):
 1. USD - Dólar Estadounidense
 2. EUR - Euro
...
19. CLP - Peso Chileno
...

Moneda origen (número o código): USD
Moneda destino (número o código): CLP

? Consultando tasa de cambio...

RESULTADO DE LA CONVERSIÓN
┌─────────────────────────────────────────────────────┐
│  CANTIDAD ORIGINAL:      500.00 USD              │
├─────────────────────────────────────────────────────┤
│  TASA DE CAMBIO:     1 USD = 914,774092 CLP        │
├─────────────────────────────────────────────────────┤
│  CANTIDAD CONVERTIDA:  457387.05 CLP             │
└─────────────────────────────────────────────────────┘

[OK] Conversión realizada exitosamente
```

---

## Solución de Problemas

### ❌ Error: "No se encuentra el archivo wallet.db"
- **Causa**: Base de datos no inicializada
- **Solución**: Reinicia la aplicación, se creará automáticamente

### ❌ Error: "Invalid JDBC URL format"
- **Causa**: Problemas con la conexión a SQLite
- **Solución**: Verifica que `wallet.db` no esté corrupto, borralo y reinicia

### ❌ Error: "java: command not found"
- **Causa**: Java no está en el PATH
- **Solución**: Instala Java 21 JDK desde https://www.oracle.com/java/technologies/downloads/

### ❌ Error: "mvn: command not found"
- **Causa**: Maven no está en el PATH
- **Solución**: Instala Maven desde https://maven.apache.org/download.cgi

---

## Ubicaciones de Archivos Importantes

```
Proyecto:
├── wallet-app-1.0.0-jar-with-dependencies.jar  (Ejecutable)
├── wallet.db                                     (Base de datos SQLite)
├── pom.xml                                       (Configuración Maven)
├── src/
│   ├── main/
│   │   ├── java/com/wallet/
│   │   │   ├── domain/       (Lógica de negocio)
│   │   │   ├── application/  (Use cases)
│   │   │   ├── infrastructure/ (BD, persistence)
│   │   │   └── presentation/  (Menús, UI)
│   │   └── resources/
│   │       └── schema.sql    (Script de inicialización BD)
│   └── test/
│       └── java/com/wallet/  (Tests unitarios)
├── target/
│   └── wallet-app-1.0.0-jar-with-dependencies.jar
├── BASE_DE_DATOS.md          (Documentación BD - Este archivo)
└── README.md                 (Documentación general)
```

---

## Información de Contacto y Soporte

**Proyecto**: WALLET - Billetera Digital
**Versión**: 1.0.0
**Última Actualización**: Diciembre 2025
**Desarrollador**: Equipo WALLET
**Tecnologías**: Java 21, Hibernate ORM, SQLite, Clean Architecture

Para más información, consulta:
- `BASE_DE_DATOS.md` - Arquitectura de BD
- `ARCHITECTURE.md` - Arquitectura de la aplicación
- `README.md` - Documentación general
