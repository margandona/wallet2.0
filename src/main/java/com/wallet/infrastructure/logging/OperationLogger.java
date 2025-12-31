package com.wallet.infrastructure.logging;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Logger de Operaciones de Base de Datos.
 *
 * Registra todas las operaciones CRUD:
 * - CREATE (Insertar)
 * - READ (Consultar)
 * - UPDATE (Actualizar)
 * - DELETE (Eliminar)
 *
 * Características:
 * - Thread-safe (sincronizado)
 * - Escribe en archivo de log
 * - Formateo consistente
 * - Niveles de log (INFO, ERROR, WARN)
 * - Timestamps
 *
 * Uso:
 * OperationLogger.logCreate("Usuario", usuarioId, "Usuario Juan Pérez creado");
 * OperationLogger.logUpdate("Cuenta", cuentaId, "Saldo actualizado a 1000.00");
 * OperationLogger.logDelete("Transacción", transaccionId, "Transacción eliminada");
 * OperationLogger.logError("Cuenta", cuentaId, "Error: Saldo insuficiente");
 */
public class OperationLogger {

    private static final String LOG_FILE = "wallet_operations.log";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // Niveles de log
    public enum LogLevel {
        CREATE("CREATE"),
        READ("READ"),
        UPDATE("UPDATE"),
        DELETE("DELETE"),
        INFO("INFO"),
        WARN("WARN"),
        ERROR("ERROR");

        private final String label;

        LogLevel(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    /**
     * Registra una operación CREATE.
     *
     * @param entityType tipo de entidad (Usuario, Cuenta, etc)
     * @param entityId ID de la entidad
     * @param description descripción de la operación
     */
    public static void logCreate(String entityType, String entityId, String description) {
        log(LogLevel.CREATE, entityType, entityId, description);
    }

    /**
     * Registra una operación READ.
     *
     * @param entityType tipo de entidad
     * @param entityId ID de la entidad (puede ser null para búsquedas generales)
     * @param description descripción
     */
    public static void logRead(String entityType, String entityId, String description) {
        log(LogLevel.READ, entityType, entityId, description);
    }

    /**
     * Registra una operación UPDATE.
     *
     * @param entityType tipo de entidad
     * @param entityId ID de la entidad
     * @param description descripción de cambios
     */
    public static void logUpdate(String entityType, String entityId, String description) {
        log(LogLevel.UPDATE, entityType, entityId, description);
    }

    /**
     * Registra una operación DELETE.
     *
     * @param entityType tipo de entidad
     * @param entityId ID de la entidad
     * @param description descripción
     */
    public static void logDelete(String entityType, String entityId, String description) {
        log(LogLevel.DELETE, entityType, entityId, description);
    }

    /**
     * Registra un evento informativo.
     *
     * @param description descripción del evento
     */
    public static void logInfo(String description) {
        log(LogLevel.INFO, "SYSTEM", "-", description);
    }

    /**
     * Registra una advertencia.
     *
     * @param description descripción de la advertencia
     */
    public static void logWarn(String description) {
        log(LogLevel.WARN, "SYSTEM", "-", description);
    }

    /**
     * Registra un error.
     *
     * @param entityType tipo de entidad donde ocurrió el error
     * @param entityId ID relacionado
     * @param description descripción del error
     */
    public static void logError(String entityType, String entityId, String description) {
        log(LogLevel.ERROR, entityType, entityId, description);
    }

    /**
     * Registra error con excepción.
     *
     * @param entityType tipo de entidad
     * @param entityId ID de la entidad
     * @param description descripción
     * @param exception excepción
     */
    public static void logError(String entityType, String entityId, String description, Exception exception) {
        String detalles = description + " | Causa: " + exception.getMessage();
        log(LogLevel.ERROR, entityType, entityId, detalles);
    }

    /**
     * Limpia el archivo de log.
     * Útil para resets o tests.
     */
    public static void clearLog() {
        lock.writeLock().lock();
        try {
            File logFile = new File(LOG_FILE);
            if (logFile.exists()) {
                logFile.delete();
                System.out.println("✅ Log limpiado");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Obtiene el contenido actual del log.
     *
     * @return contenido del archivo de log
     */
    public static String getLogContent() {
        lock.readLock().lock();
        try {
            File logFile = new File(LOG_FILE);
            if (!logFile.exists()) {
                return "[ Log vacío ]";
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            return content.toString();
        } catch (IOException e) {
            return "[ Error leyendo log: " + e.getMessage() + " ]";
        } finally {
            lock.readLock().unlock();
        }
    }

    // ==================== MÉTODOS PRIVADOS ====================

    /**
     * Método central de logging.
     * Escribe la entrada de log en archivo de forma thread-safe.
     *
     * @param level nivel de log
     * @param entityType tipo de entidad
     * @param entityId ID de la entidad
     * @param description descripción
     */
    private static void log(LogLevel level, String entityType, String entityId, String description) {
        lock.writeLock().lock();
        try {
            String timestamp = LocalDateTime.now().format(formatter);
            String logEntry = formatLogEntry(timestamp, level, entityType, entityId, description);

            // Escribir en archivo
            try (FileWriter fw = new FileWriter(LOG_FILE, true);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(logEntry);
                bw.newLine();
            } catch (IOException e) {
                System.err.println("❌ Error escribiendo en log: " + e.getMessage());
            }

            // También imprimir en consola si es error
            if (level == LogLevel.ERROR) {
                System.err.println("❌ [" + level.getLabel() + "] " + logEntry);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Formatea una entrada de log.
     *
     * @param timestamp timestamp
     * @param level nivel de log
     * @param entityType tipo de entidad
     * @param entityId ID de entidad
     * @param description descripción
     * @return entrada formateada
     */
    private static String formatLogEntry(String timestamp, LogLevel level, String entityType, 
                                        String entityId, String description) {
        return String.format(
            "[%s] %-6s | %-15s | %-36s | %s",
            timestamp,
            level.getLabel(),
            entityType,
            entityId,
            description
        );
    }

    /**
     * Escribe estadísticas del log.
     * Cuenta operaciones por tipo.
     */
    public static void printStatistics() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     ESTADÍSTICAS DE OPERACIONES BD     ║");
        System.out.println("╚════════════════════════════════════════╝");

        lock.readLock().lock();
        try {
            File logFile = new File(LOG_FILE);
            if (!logFile.exists()) {
                System.out.println("  [ Sin operaciones registradas ]");
                return;
            }

            // Contar por tipo
            int creates = 0, reads = 0, updates = 0, deletes = 0, errors = 0;
            int totalLines = 0;

            try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    totalLines++;
                    if (line.contains("CREATE")) creates++;
                    else if (line.contains("READ")) reads++;
                    else if (line.contains("UPDATE")) updates++;
                    else if (line.contains("DELETE")) deletes++;
                    else if (line.contains("ERROR")) errors++;
                }
            }

            System.out.println("  Total de líneas: " + totalLines);
            System.out.println("  ├─ CREATE:  " + creates);
            System.out.println("  ├─ READ:    " + reads);
            System.out.println("  ├─ UPDATE:  " + updates);
            System.out.println("  ├─ DELETE:  " + deletes);
            System.out.println("  └─ ERROR:   " + errors);
        } catch (IOException e) {
            System.err.println("❌ Error leyendo estadísticas: " + e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
    }
}
