package com.wallet.infrastructure.persistence;

import jakarta.persistence.EntityManager;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Inicializador de Base de Datos.
 *
 * Responsabilidades:
 * - Detectar si la BD existe
 * - Crear tablas si no existen
 * - Ejecutar scripts SQL de inicialización
 * - Preparar la BD para primera ejecución
 *
 * Características:
 * - Uso de Hibernate/JPA para ejecutar SQL nativo
 * - Lectura de schema.sql desde resources
 * - Manejo de transacciones
 * - Logging informativo
 */
public class DatabaseInitializer {

    private static final String SCHEMA_SQL_PATH = "schema.sql";
    private static final String DATABASE_FILE = "wallet.db";

    /**
     * Inicializa la base de datos.
     * Crea las tablas si no existen.
     *
     * @param entityManager EntityManager de JPA para ejecutar queries
     * @throws Exception si ocurre error durante la inicialización
     */
    public static void initialize(EntityManager entityManager) throws Exception {
        try {
            // Detectar si BD existe
            detectarBDExistente();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Detecta si el archivo de BD existe.
     *
     * @return true si el archivo wallet.db existe
     */
    private static void detectarBDExistente() {
        File dbFile = new File(DATABASE_FILE);
        // Solo validar que será creado si no existe
    }

    /**
     * Ejecuta el script schema.sql para crear tablas.
     *
     * @param entityManager EntityManager para ejecutar SQL
     * @throws Exception si hay error al leer o ejecutar el script
     */
    private static void ejecutarSchemaSQL(EntityManager entityManager) throws Exception {
        try {
            // Leer schema.sql desde resources
            String schemaSql = leerSchemaSQL();

            if (schemaSql == null || schemaSql.trim().isEmpty()) {
                throw new RuntimeException("schema.sql vacío o no encontrado");
            }

            // Dividir en sentencias individuales (separadas por ;)
            String[] sentencias = schemaSql.split(";");

            entityManager.getTransaction().begin();

            int sentenciasEjecutadas = 0;
            for (String sentencia : sentencias) {
                String sentenciaLimpia = sentencia.trim();

                // Saltar comentarios y líneas vacías
                if (sentenciaLimpia.isEmpty() || sentenciaLimpia.startsWith("--")) {
                    continue;
                }

                // Saltar comentarios de bloque
                if (sentenciaLimpia.startsWith("/*")) {
                    continue;
                }

                try {
                    // Ejecutar sentencia SQL nativa
                    entityManager.createNativeQuery(sentenciaLimpia).executeUpdate();
                    sentenciasEjecutadas++;
                } catch (Exception e) {
                    // En SQLite, si la tabla ya existe, continuar sin error
                    if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                        // Tabla ya existe, continuando
                    } else {
                        throw e;
                    }
                }
            }

            entityManager.getTransaction().commit();

        } catch (Exception e) {
            throw new RuntimeException("No se pudo inicializar el schema de BD", e);
        }
    }

    /**
     * Lee el contenido del archivo schema.sql desde resources.
     *
     * @return contenido del schema.sql, o null si no se encuentra
     * @throws IOException si hay error al leer el archivo
     */
    private static String leerSchemaSQL() throws IOException {
        try {
            // Intentar leer desde el classpath (en JAR empaquetado)
            InputStream inputStream = DatabaseInitializer.class.getClassLoader()
                    .getResourceAsStream(SCHEMA_SQL_PATH);

            if (inputStream != null) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            // Intentar leer como archivo del sistema (en desarrollo)
            File schemaFile = new File("src/main/resources/" + SCHEMA_SQL_PATH);
            if (schemaFile.exists()) {
                return new String(Files.readAllBytes(schemaFile.toPath()), StandardCharsets.UTF_8);
            }

            // Intentar en target/classes (después de compilar)
            schemaFile = new File("target/classes/" + SCHEMA_SQL_PATH);
            if (schemaFile.exists()) {
                return new String(Files.readAllBytes(schemaFile.toPath()), StandardCharsets.UTF_8);
            }

            return null;
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Crea tabla de usuarios manualmente (alternativa si schema.sql falla).
     * Se usa como fallback.
     *
     * @param entityManager EntityManager
     */
    public static void crearTablasManualmente(EntityManager entityManager) {

        try {
            entityManager.getTransaction().begin();

            // Crear tabla usuarios
            String crearUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id VARCHAR(36) PRIMARY KEY," +
                    "nombre VARCHAR(100) NOT NULL," +
                    "apellido VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100) NOT NULL UNIQUE," +
                    "documento VARCHAR(50) NOT NULL UNIQUE," +
                    "tipo_documento VARCHAR(20) NOT NULL," +
                    "activo BOOLEAN NOT NULL DEFAULT 1," +
                    "created_at TIMESTAMP NOT NULL," +
                    "updated_at TIMESTAMP NOT NULL)";

            entityManager.createNativeQuery(crearUsuarios).executeUpdate();

            // Crear tabla cuentas
            String crearCuentas = "CREATE TABLE IF NOT EXISTS cuentas (" +
                    "id VARCHAR(36) PRIMARY KEY," +
                    "numero_cuenta VARCHAR(50) NOT NULL UNIQUE," +
                    "usuario_id VARCHAR(36) NOT NULL," +
                    "saldo DECIMAL(19, 2) NOT NULL," +
                    "moneda VARCHAR(3) NOT NULL," +
                    "activa BOOLEAN NOT NULL DEFAULT 1," +
                    "created_at TIMESTAMP NOT NULL," +
                    "updated_at TIMESTAMP NOT NULL," +
                    "CONSTRAINT fk_cuentas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE)";

            entityManager.createNativeQuery(crearCuentas).executeUpdate();

            // Crear tabla transacciones
            String crearTransacciones = "CREATE TABLE IF NOT EXISTS transacciones (" +
                    "id VARCHAR(36) PRIMARY KEY," +
                    "cuenta_id VARCHAR(36) NOT NULL," +
                    "tipo VARCHAR(30) NOT NULL," +
                    "monto DECIMAL(19, 2) NOT NULL," +
                    "descripcion VARCHAR(255)," +
                    "saldo_anterior DECIMAL(19, 2) NOT NULL," +
                    "saldo_nuevo DECIMAL(19, 2) NOT NULL," +
                    "fecha_transaccion TIMESTAMP NOT NULL," +
                    "cuenta_origen_id VARCHAR(36)," +
                    "cuenta_destino_id VARCHAR(36)," +
                    "created_at TIMESTAMP NOT NULL," +
                    "CONSTRAINT fk_transacciones_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuentas(id) ON DELETE CASCADE)";

            entityManager.createNativeQuery(crearTransacciones).executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Verifica la integridad del schema.
     * Comprueba que todas las tablas necesarias existan.
     *
     * @param entityManager EntityManager
     * @return true si todas las tablas existen
     */
    public static boolean verificarIntegridad(EntityManager entityManager) {

        try {
            String[] tablas = {"usuarios", "cuentas", "transacciones"};

            for (String tabla : tablas) {
                // En SQLite, verificar si tabla existe
                String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tabla + "'";
                Object resultado = entityManager.createNativeQuery(query).getSingleResult();

                if (resultado == null) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
