package com.wallet.infrastructure.repositories;

import com.wallet.domain.entities.Transaccion;
import com.wallet.domain.exceptions.*;
import com.wallet.domain.repositories.ITransaccionRepository;
import com.wallet.domain.valueobjects.Dinero;
import com.wallet.domain.valueobjects.TipoTransaccion;
import com.wallet.infrastructure.config.JPAConfiguration;
import com.wallet.infrastructure.entities.CuentaJPAEntity;
import com.wallet.infrastructure.entities.TransaccionJPAEntity;
import com.wallet.infrastructure.logging.OperationLogger;
import com.wallet.infrastructure.validation.ValidatorUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA del repositorio de Transacciones.
 *
 * Responsabilidades:
 * - Convertir entre Domain Entities (Transaccion) y JPA Entities (TransaccionJPAEntity)
 * - Ejecutar operaciones CRUD en la base de datos
 * - Manejar transacciones
 * - Traducir excepciones JPA a excepciones de dominio
 */
public class TransaccionJPARepository implements ITransaccionRepository {

    /**
     * Guarda una nueva transacción.
     *
     * @param transaccion la transacción a guardar
     * @return la transacción guardada
     * @throws InvalidMontoException si el monto es inválido (0 o negativo)
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public Transaccion guardar(Transaccion transaccion) {
        EntityManager em = null;
        try {
            // Validar entrada
            ValidatorUtil.validarMonto(transaccion.getMonto().getCantidad());

            em = JPAConfiguration.getEntityManager();
            em.getTransaction().begin();

            TransaccionJPAEntity transaccionJPA = domainToJPA(transaccion);
            
            // **CRÍTICO**: Reattach la cuenta a la transacción actual
            if (transaccionJPA.getCuenta() != null && transaccionJPA.getCuenta().getId() != null) {
                var cuentaManaged = em.find(CuentaJPAEntity.class, transaccionJPA.getCuenta().getId());
                if (cuentaManaged != null) {
                    transaccionJPA.setCuenta(cuentaManaged);
                } else {
                    throw new RuntimeException("Cuenta no encontrada en BD: " + transaccionJPA.getCuenta().getId());
                }
            }
            
            // Verificar si es nueva o existente
            TransaccionJPAEntity transaccionExistente = null;
            if (transaccionJPA.getId() != null && !transaccionJPA.getId().isEmpty()) {
                transaccionExistente = em.find(TransaccionJPAEntity.class, transaccionJPA.getId());
            }
            
            TransaccionJPAEntity transaccionGuardada;
            if (transaccionExistente == null) {
                em.persist(transaccionJPA);
                transaccionGuardada = transaccionJPA;
            } else {
                transaccionGuardada = em.merge(transaccionJPA);
            }

            em.flush();
            em.getTransaction().commit();
            
            // Pequeña pausa para asegurar que SQLite sincronice
            Thread.sleep(100);

            Transaccion transaccionResultado = jpaToDomain(transaccionGuardada);
            OperationLogger.logCreate("Transacción", transaccionResultado.getId(),
                String.format("Transacción %s de %s guardada", transaccionResultado.getTipo(), transaccionResultado.getMonto()));
            
            return transaccionResultado;
            
        } catch (InvalidMontoException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (PersistenceException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OperationLogger.logError("Transacción", transaccion.getId(), "Error de persistencia al guardar", e);
            throw RepositoryException.operacionFallida("Transacción", "guardar", e.getMessage());
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OperationLogger.logError("Transacción", transaccion.getId(), "Error inesperado al guardar", e);
            throw RepositoryException.operacionFallida("Transacción", "guardar", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Busca una transacción por su ID.
     *
     * @param id el ID de la transacción
     * @return Optional con la transacción si existe
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public Optional<Transaccion> buscarPorId(String id) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            TransaccionJPAEntity transaccionJPA = em.find(TransaccionJPAEntity.class, id);

            if (transaccionJPA != null) {
                OperationLogger.logRead("Transacción", id, "Transacción encontrada");
                return Optional.of(jpaToDomain(transaccionJPA));
            }
            
            OperationLogger.logRead("Transacción", id, "Transacción no encontrada");
            return Optional.empty();
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Transacción", id, "Error de persistencia al buscar", e);
            throw RepositoryException.operacionFallida("Transacción", "buscar", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Transacción", id, "Error inesperado al buscar", e);
            throw RepositoryException.operacionFallida("Transacción", "buscar", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Obtiene todas las transacciones de una cuenta.
     *
     * @param cuentaId el ID de la cuenta
     * @return lista de transacciones
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public List<Transaccion> buscarPorCuentaId(String cuentaId) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT t FROM TransaccionJPAEntity t WHERE t.cuenta.id = :cuentaId ORDER BY t.fechaTransaccion DESC");
            query.setParameter("cuentaId", cuentaId);

            List<TransaccionJPAEntity> transaccionesJPA = query.getResultList();

            OperationLogger.logRead("Transacción", cuentaId, "Se obtuvieron " + transaccionesJPA.size() + " transacciones de la cuenta");
            return convertirListaDominio(transaccionesJPA);
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Transacción", cuentaId, "Error de persistencia al buscar por cuenta", e);
            throw RepositoryException.operacionFallida("Transacción", "buscar por cuenta", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Transacción", cuentaId, "Error inesperado al buscar por cuenta", e);
            throw RepositoryException.operacionFallida("Transacción", "buscar por cuenta", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Obtiene transacciones de una cuenta por tipo.
     *
     * @param cuentaId el ID de la cuenta
     * @param tipo el tipo de transacción
     * @return lista de transacciones filtradas
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public List<Transaccion> buscarPorCuentaIdYTipo(String cuentaId, TipoTransaccion tipo) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT t FROM TransaccionJPAEntity t WHERE t.cuenta.id = :cuentaId AND t.tipo = :tipo ORDER BY t.fechaTransaccion DESC");
            query.setParameter("cuentaId", cuentaId);
            query.setParameter("tipo", tipo.name());

            List<TransaccionJPAEntity> transaccionesJPA = query.getResultList();

            OperationLogger.logRead("Transacción", cuentaId, 
                "Se obtuvieron " + transaccionesJPA.size() + " transacciones tipo " + tipo.name());
            return convertirListaDominio(transaccionesJPA);
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Transacción", cuentaId, "Error de persistencia al buscar por tipo", e);
            throw RepositoryException.operacionFallida("Transacción", "buscar por tipo", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Transacción", cuentaId, "Error inesperado al buscar por tipo", e);
            throw RepositoryException.operacionFallida("Transacción", "buscar por tipo", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Obtiene transacciones de una cuenta en un rango de fechas.
     *
     * @param cuentaId el ID de la cuenta
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return lista de transacciones en el rango
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public List<Transaccion> obtenerPorCuentaYFechas(String cuentaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT t FROM TransaccionJPAEntity t WHERE t.cuenta.id = :cuentaId AND t.fechaTransaccion BETWEEN :fechaInicio AND :fechaFin ORDER BY t.fechaTransaccion DESC");
            query.setParameter("cuentaId", cuentaId);
            query.setParameter("fechaInicio", fechaInicio);
            query.setParameter("fechaFin", fechaFin);

            List<TransaccionJPAEntity> transaccionesJPA = query.getResultList();

            OperationLogger.logRead("Transacción", cuentaId, 
                "Se obtuvieron " + transaccionesJPA.size() + " transacciones en el rango de fechas");
            return convertirListaDominio(transaccionesJPA);
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Transacción", cuentaId, "Error de persistencia al obtener por fechas", e);
            throw RepositoryException.operacionFallida("Transacción", "obtener por fechas", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Transacción", cuentaId, "Error inesperado al obtener por fechas", e);
            throw RepositoryException.operacionFallida("Transacción", "obtener por fechas", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Obtiene todas las transacciones.
     *
     * @return lista de todas las transacciones
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public List<Transaccion> obtenerTodas() {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT t FROM TransaccionJPAEntity t ORDER BY t.fechaTransaccion DESC");
            List<TransaccionJPAEntity> transaccionesJPA = query.getResultList();

            OperationLogger.logRead("Transacción", "todas", "Se obtuvieron " + transaccionesJPA.size() + " transacciones en total");
            return convertirListaDominio(transaccionesJPA);
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Transacción", "todas", "Error de persistencia al obtener todas", e);
            throw RepositoryException.operacionFallida("Transacción", "obtener todas", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Transacción", "todas", "Error inesperado al obtener todas", e);
            throw RepositoryException.operacionFallida("Transacción", "obtener todas", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Obtiene las últimas N transacciones de una cuenta.
     *
     * @param cuentaId el ID de la cuenta
     * @param limite número máximo de transacciones
     * @return lista de últimas transacciones
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public List<Transaccion> obtenerUltimasPorCuenta(String cuentaId, int limite) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT t FROM TransaccionJPAEntity t WHERE t.cuenta.id = :cuentaId ORDER BY t.fechaTransaccion DESC");
            query.setParameter("cuentaId", cuentaId);
            query.setMaxResults(limite);

            List<TransaccionJPAEntity> transaccionesJPA = query.getResultList();

            OperationLogger.logRead("Transacción", cuentaId, 
                "Se obtuvieron " + transaccionesJPA.size() + " últimas transacciones (límite: " + limite + ")");
            return convertirListaDominio(transaccionesJPA);
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Transacción", cuentaId, "Error de persistencia al obtener últimas", e);
            throw RepositoryException.operacionFallida("Transacción", "obtener últimas", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Transacción", cuentaId, "Error inesperado al obtener últimas", e);
            throw RepositoryException.operacionFallida("Transacción", "obtener últimas", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Convierte una entidad JPA a entidad de dominio.
     *
     * @param transaccionJPA entidad JPA
     * @return entidad de dominio
     */
    private Transaccion jpaToDomain(TransaccionJPAEntity transaccionJPA) {
        Dinero monto = new Dinero(transaccionJPA.getMonto());
        Dinero saldoAnterior = new Dinero(transaccionJPA.getSaldoAnterior());
        Dinero saldoNuevo = new Dinero(transaccionJPA.getSaldoNuevo());
        TipoTransaccion tipo = TipoTransaccion.valueOf(transaccionJPA.getTipo());

        return new Transaccion(
            transaccionJPA.getId(),
            tipo,
            monto,
            transaccionJPA.getCuentaOrigenId(),
            transaccionJPA.getCuentaDestinoId(),
            transaccionJPA.getDescripcion(),
            transaccionJPA.getFechaTransaccion(),
            saldoAnterior,
            saldoNuevo
        );
    }

    /**
     * Convierte una entidad de dominio a entidad JPA.
     *
     * @param transaccion entidad de dominio
     * @return entidad JPA
     */
    private TransaccionJPAEntity domainToJPA(Transaccion transaccion) {
        TransaccionJPAEntity transaccionJPA = new TransaccionJPAEntity(
            transaccion.getId(),
            transaccion.getTipo().name(),
            transaccion.getMonto().getCantidad(),
            transaccion.getDescripcion(),
            transaccion.getSaldoAnterior().getCantidad(),
            transaccion.getSaldoNuevo().getCantidad(),
            transaccion.getFecha(),
            transaccion.getCuentaOrigenId(),
            transaccion.getCuentaDestinoId(),
            LocalDateTime.now()
        );
        
        // IMPORTANTE: Crear una entidad cuenta mínima con solo el ID
        // (será reattached correctamente en el método guardar())
        // La transacción se vincula a través de cuentaOrigenId
        if (transaccion.getCuentaOrigenId() != null && !transaccion.getCuentaOrigenId().isEmpty()) {
            CuentaJPAEntity cuentaReference = new CuentaJPAEntity();
            cuentaReference.setId(transaccion.getCuentaOrigenId());
            transaccionJPA.setCuenta(cuentaReference);
        }

        return transaccionJPA;
    }

    /**
     * Convierte lista de entidades JPA a lista de entidades de dominio.
     *
     * @param transaccionesJPA lista de JPA entities
     * @return lista de domain entities
     */
    private List<Transaccion> convertirListaDominio(List<TransaccionJPAEntity> transaccionesJPA) {
        List<Transaccion> transacciones = new ArrayList<>();
        for (TransaccionJPAEntity transaccionJPA : transaccionesJPA) {
            transacciones.add(jpaToDomain(transaccionJPA));
        }
        return transacciones;
    }
}
