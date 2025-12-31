package com.wallet.infrastructure.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Configuración centralizada de JPA/Hibernate.
 *
 * Esta clase gestiona:
 * - Creación y cierre de EntityManagerFactory
 * - Obtención de EntityManager para operaciones CRUD
 * - Gestión de transacciones
 *
 * Patrón: Singleton
 * Thread-safety: Sí (EntityManagerFactory es thread-safe)
 */
public class JPAConfiguration {

    private static final String PERSISTENCE_UNIT_NAME = "WalletPU";
    private static EntityManagerFactory entityManagerFactory;

    /**
     * Inicializa la fábrica de EntityManager.
     * Debe ser llamado una sola vez al iniciar la aplicación.
     */
    public static void initialize() {
        if (entityManagerFactory == null) {
            try {
                // Configurar SQLite con PRAGMA
                configureSQLitePragmas();
                
                entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("No se pudo inicializar JPA", e);
            }
        }
    }

    /**
     * Configura las PRAGMA de SQLite para durabilidad.
     */
    private static void configureSQLitePragmas() {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:wallet.db");
                 Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA synchronous = FULL");
                stmt.execute("PRAGMA journal_mode = WAL");
            }
        } catch (Exception e) {
            // No es fatal, continuar de todas formas
        }
    }

    /**
     * Obtiene un nuevo EntityManager.
     * Cada llamada retorna una nueva instancia.
     * El cliente es responsable de cerrar el EntityManager.
     *
     * @return EntityManager para operaciones con BD
     * @throws IllegalStateException si JPAConfiguration no está inicializado
     */
    public static EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("JPAConfiguration no inicializado. Llama a initialize() primero.");
        }
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Obtiene la EntityManagerFactory actual.
     * Útil para acceso directo si es necesario.
     *
     * @return EntityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("JPAConfiguration no inicializado. Llama a initialize() primero.");
        }
        return entityManagerFactory;
    }

    /**
     * Cierra la fábrica de EntityManager.
     * Debe ser llamado al finalizar la aplicación.
     * Libera conexiones y recursos.
     */
    public static void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    /**
     * Verifica si JPAConfiguration está inicializado.
     *
     * @return true si está inicializado, false en caso contrario
     */
    public static boolean isInitialized() {
        return entityManagerFactory != null && entityManagerFactory.isOpen();
    }

    /**
     * Ejecuta una operación dentro de una transacción.
     * Patrón: Template Method para simplificar transacciones.
     *
     * Uso:
     * JPAConfiguration.executeInTransaction(em -> {
     *     Usuario usuario = new Usuario(...);
     *     em.persist(usuario);
     * });
     *
     * @param operation Función que recibe EntityManager
     * @throws Exception Si ocurre error en la operación
     */
    public static void executeInTransaction(Consumer<EntityManager> operation) throws Exception {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            operation.accept(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Ejecuta una query dentro de una transacción y retorna resultado.
     * Patrón: Template Method para simplificar queries.
     *
     * Uso:
     * Usuario usuario = JPAConfiguration.executeInTransaction(em -> {
     *     Query q = em.createQuery("SELECT u FROM Usuario u WHERE u.id = :id");
     *     q.setParameter("id", usuarioId);
     *     return (Usuario) q.getSingleResult();
     * });
     *
     * @param query Función que recibe EntityManager y retorna resultado
     * @return Resultado de la operación
     * @throws Exception Si ocurre error en la operación
     */
    public static <T> T executeInTransaction(Function<EntityManager, T> query) throws Exception {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T result = query.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Ejecuta una query de lectura (sin transacción).
     * Para operaciones que no modifican datos.
     *
     * @param query Función que recibe EntityManager y retorna resultado
     * @return Resultado de la operación
     * @throws Exception Si ocurre error en la operación
     */
    public static <T> T executeQuery(Function<EntityManager, T> query) throws Exception {
        EntityManager em = getEntityManager();
        try {
            return query.apply(em);
        } finally {
            em.close();
        }
    }
}

// Interfaces funcionales para los métodos de transacción
@FunctionalInterface
interface Consumer<T> {
    void accept(T t) throws Exception;
}

@FunctionalInterface
interface Function<T, R> {
    R apply(T t) throws Exception;
}
