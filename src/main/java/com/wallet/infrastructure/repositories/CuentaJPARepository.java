package com.wallet.infrastructure.repositories;

import com.wallet.domain.entities.Cuenta;
import com.wallet.domain.exceptions.*;
import com.wallet.domain.repositories.ICuentaRepository;
import com.wallet.domain.valueobjects.Dinero;
import com.wallet.infrastructure.config.JPAConfiguration;
import com.wallet.infrastructure.entities.CuentaJPAEntity;
import com.wallet.infrastructure.entities.UsuarioJPAEntity;
import com.wallet.infrastructure.logging.OperationLogger;
import com.wallet.infrastructure.validation.ValidatorUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA del repositorio de Cuentas.
 *
 * Responsabilidades:
 * - Convertir entre Domain Entities (Cuenta) y JPA Entities (CuentaJPAEntity)
 * - Ejecutar operaciones CRUD en la base de datos
 * - Manejar transacciones
 * - Traducir excepciones JPA a excepciones de dominio
 */
public class CuentaJPARepository implements ICuentaRepository {

    /**
     * Guarda una cuenta nueva o actualiza una existente.
     *
     * @param cuenta la cuenta a guardar
     * @return la cuenta guardada
     * @throws InvalidSaldoException si el saldo es negativo
     * @throws DuplicateCuentaException si el número de cuenta ya existe
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public Cuenta guardar(Cuenta cuenta) {
        EntityManager em = null;
        try {
            // Validar entrada
            ValidatorUtil.validarSaldo(cuenta.getSaldo().getCantidad());
            ValidatorUtil.validarNumeroCuenta(cuenta.getNumeroCuenta());

            em = JPAConfiguration.getEntityManager();
            
            // Verificar duplicado si es nueva cuenta
            if (cuenta.getId() == null || cuenta.getId().isEmpty()) {
                if (existeNumeroCuenta(cuenta.getNumeroCuenta())) {
                    OperationLogger.logWarn("Intento de crear cuenta con número duplicado: " + cuenta.getNumeroCuenta());
                    throw new DuplicateCuentaException(cuenta.getNumeroCuenta());
                }
            }

            em.getTransaction().begin();

            CuentaJPAEntity cuentaJPA = domainToJPA(cuenta);
            
            // **CRÍTICO**: Reattach el usuario a la transacción actual
            // Necesitamos que la referencia al usuario sea managed por este EntityManager
            if (cuentaJPA.getUsuario() != null && cuentaJPA.getUsuario().getId() != null) {
                // Buscar el usuario en la BD y asignarlo a la cuenta
                var usuarioManaged = em.find(UsuarioJPAEntity.class, cuentaJPA.getUsuario().getId());
                if (usuarioManaged != null) {
                    cuentaJPA.setUsuario(usuarioManaged);
                } else {
                    throw new RuntimeException("Usuario no encontrado en BD: " + cuentaJPA.getUsuario().getId());
                }
            }
            
            // Verificar si es nueva o existente
            CuentaJPAEntity cuentaExistente = null;
            if (cuentaJPA.getId() != null && !cuentaJPA.getId().isEmpty()) {
                cuentaExistente = em.find(CuentaJPAEntity.class, cuentaJPA.getId());
            }
            
            CuentaJPAEntity cuentaGuardada;
            if (cuentaExistente == null) {
                em.persist(cuentaJPA);
                cuentaGuardada = cuentaJPA;
            } else {
                cuentaGuardada = em.merge(cuentaJPA);
            }

            em.flush();
            em.getTransaction().commit();
            
            // Pequeña pausa para asegurar que SQLite sincronice
            Thread.sleep(100);

            Cuenta cuentaResultado = jpaToDomain(cuentaGuardada);
            OperationLogger.logCreate("Cuenta", cuentaResultado.getId(),
                String.format("Cuenta %s creada con saldo %s", cuentaResultado.getNumeroCuenta(), cuentaResultado.getSaldo()));
            
            return cuentaResultado;
            
        } catch (InvalidSaldoException | DuplicateCuentaException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (PersistenceException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OperationLogger.logError("Cuenta", cuenta.getId(), "Error de persistencia al guardar", e);
            throw RepositoryException.operacionFallida("Cuenta", "guardar", e.getMessage());
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OperationLogger.logError("Cuenta", cuenta.getId(), "Error inesperado al guardar", e);
            throw RepositoryException.operacionFallida("Cuenta", "guardar", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Busca una cuenta por su ID.
     *
     * @param id el ID de la cuenta
     * @return Optional con la cuenta si existe
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public Optional<Cuenta> buscarPorId(String id) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            CuentaJPAEntity cuentaJPA = em.find(CuentaJPAEntity.class, id);

            if (cuentaJPA != null) {
                OperationLogger.logRead("Cuenta", id, "Cuenta encontrada");
                return Optional.of(jpaToDomain(cuentaJPA));
            }
            
            OperationLogger.logRead("Cuenta", id, "Cuenta no encontrada");
            return Optional.empty();
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Cuenta", id, "Error de persistencia al buscar", e);
            throw RepositoryException.operacionFallida("Cuenta", "buscar", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Cuenta", id, "Error inesperado al buscar", e);
            throw RepositoryException.operacionFallida("Cuenta", "buscar", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Busca una cuenta por su número.
     *
     * @param numeroCuenta el número de cuenta
     * @return Optional con la cuenta si existe
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT c FROM CuentaJPAEntity c WHERE c.numeroCuenta = :numero", CuentaJPAEntity.class);
            query.setParameter("numero", numeroCuenta);

            try {
                CuentaJPAEntity cuentaJPA = (CuentaJPAEntity) query.getSingleResult();
                OperationLogger.logRead("Cuenta", cuentaJPA.getId(), "Cuenta encontrada por número: " + numeroCuenta);
                return Optional.of(jpaToDomain(cuentaJPA));
            } catch (NoResultException e) {
                OperationLogger.logRead("Cuenta", "desconocida", "No encontrada cuenta con número: " + numeroCuenta);
                return Optional.empty();
            }
        } catch (PersistenceException e) {
            OperationLogger.logError("Cuenta", "desconocida", "Error de persistencia al buscar por número", e);
            throw RepositoryException.operacionFallida("Cuenta", "buscar por número", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Cuenta", "desconocida", "Error inesperado al buscar por número", e);
            throw RepositoryException.operacionFallida("Cuenta", "buscar por número", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Obtiene todas las cuentas de un usuario.
     *
     * @param usuarioId el ID del usuario
     * @return lista de cuentas del usuario
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public List<Cuenta> buscarPorUsuarioId(String usuarioId) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT c FROM CuentaJPAEntity c WHERE c.usuario.id = :usuarioId ORDER BY c.createdAt DESC");
            query.setParameter("usuarioId", usuarioId);

            List<CuentaJPAEntity> cuentasJPA = query.getResultList();

            OperationLogger.logRead("Cuenta", usuarioId, "Se obtuvieron " + cuentasJPA.size() + " cuentas del usuario");
            return convertirListaDominio(cuentasJPA);
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Cuenta", usuarioId, "Error de persistencia al buscar por usuario", e);
            throw RepositoryException.operacionFallida("Cuenta", "buscar por usuario", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Cuenta", usuarioId, "Error inesperado al buscar por usuario", e);
            throw RepositoryException.operacionFallida("Cuenta", "buscar por usuario", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Obtiene todas las cuentas activas de un usuario.
     *
     * @param usuarioId el ID del usuario
     * @return lista de cuentas activas
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public List<Cuenta> obtenerActivasPorUsuario(String usuarioId) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT c FROM CuentaJPAEntity c WHERE c.usuario.id = :usuarioId AND c.activa = true ORDER BY c.createdAt DESC");
            query.setParameter("usuarioId", usuarioId);

            List<CuentaJPAEntity> cuentasJPA = query.getResultList();

            OperationLogger.logRead("Cuenta", usuarioId, "Se obtuvieron " + cuentasJPA.size() + " cuentas activas del usuario");
            return convertirListaDominio(cuentasJPA);
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Cuenta", usuarioId, "Error de persistencia al obtener activas", e);
            throw RepositoryException.operacionFallida("Cuenta", "obtener activas", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Cuenta", usuarioId, "Error inesperado al obtener activas", e);
            throw RepositoryException.operacionFallida("Cuenta", "obtener activas", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Obtiene todas las cuentas.
     *
     * @return lista de todas las cuentas
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public List<Cuenta> obtenerTodas() {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT c FROM CuentaJPAEntity c ORDER BY c.createdAt DESC");
            List<CuentaJPAEntity> cuentasJPA = query.getResultList();

            OperationLogger.logRead("Cuenta", "todas", "Se obtuvieron " + cuentasJPA.size() + " cuentas en total");
            return convertirListaDominio(cuentasJPA);
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Cuenta", "todas", "Error de persistencia al obtener todas", e);
            throw RepositoryException.operacionFallida("Cuenta", "obtener todas", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Cuenta", "todas", "Error inesperado al obtener todas", e);
            throw RepositoryException.operacionFallida("Cuenta", "obtener todas", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Elimina una cuenta por su ID.
     *
     * @param id el ID de la cuenta
     * @return true si se eliminó correctamente
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public boolean eliminar(String id) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();
            em.getTransaction().begin();

            CuentaJPAEntity cuenta = em.find(CuentaJPAEntity.class, id);

            if (cuenta == null) {
                em.getTransaction().rollback();
                OperationLogger.logWarn("Intento de eliminar cuenta no existente: " + id);
                return false;
            }

            em.remove(cuenta);
            em.flush();
            em.getTransaction().commit();
            
            OperationLogger.logDelete("Cuenta", id, "Cuenta eliminada exitosamente");
            return true;
            
        } catch (PersistenceException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OperationLogger.logError("Cuenta", id, "Error de persistencia al eliminar", e);
            throw RepositoryException.operacionFallida("Cuenta", "eliminar", e.getMessage());
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OperationLogger.logError("Cuenta", id, "Error inesperado al eliminar", e);
            throw RepositoryException.operacionFallida("Cuenta", "eliminar", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Verifica si existe una cuenta con el número dado.
     *
     * @param numeroCuenta el número de cuenta
     * @return true si existe
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public boolean existeNumeroCuenta(String numeroCuenta) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT COUNT(c) FROM CuentaJPAEntity c WHERE c.numeroCuenta = :numero");
            query.setParameter("numero", numeroCuenta);

            Long count = (Long) query.getSingleResult();

            boolean existe = count > 0;
            if (existe) {
                OperationLogger.logRead("Cuenta", "verificación", "Número de cuenta " + numeroCuenta + " ya existe");
            }
            return existe;
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Cuenta", "verificación", "Error de persistencia al verificar número", e);
            throw RepositoryException.operacionFallida("Cuenta", "verificar número", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Cuenta", "verificación", "Error inesperado al verificar número", e);
            throw RepositoryException.operacionFallida("Cuenta", "verificar número", e.getMessage());
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
     * @param cuentaJPA entidad JPA
     * @return entidad de dominio
     */
    private Cuenta jpaToDomain(CuentaJPAEntity cuentaJPA) {
        Dinero saldo = new Dinero(cuentaJPA.getSaldo(), cuentaJPA.getMoneda());

        return new Cuenta(
            cuentaJPA.getId(),
            cuentaJPA.getNumeroCuenta(),
            cuentaJPA.getUsuario().getId(),
            saldo,
            cuentaJPA.getCreatedAt(),
            cuentaJPA.getUpdatedAt(),
            cuentaJPA.isActiva()
        );
    }

    /**
     * Convierte una entidad de dominio a entidad JPA.
     *
     * @param cuenta entidad de dominio
     * @return entidad JPA
     */
    private CuentaJPAEntity domainToJPA(Cuenta cuenta) {
        CuentaJPAEntity cuentaJPA = new CuentaJPAEntity(
            cuenta.getId(),
            cuenta.getNumeroCuenta(),
            cuenta.getSaldo().getMoneda(),
            cuenta.getSaldo().getCantidad(),
            cuenta.isActiva(),
            cuenta.getFechaCreacion(),
            cuenta.getFechaActualizacion()
        );
        
        // IMPORTANTE: Crear una entidad usuario mínima con solo el ID
        // (será reattached correctamente en el método guardar())
        if (cuenta.getUsuarioId() != null && !cuenta.getUsuarioId().isEmpty()) {
            UsuarioJPAEntity usuarioReference = new UsuarioJPAEntity();
            usuarioReference.setId(cuenta.getUsuarioId());
            cuentaJPA.setUsuario(usuarioReference);
        }

        return cuentaJPA;
    }

    /**
     * Convierte lista de entidades JPA a lista de entidades de dominio.
     *
     * @param cuentasJPA lista de JPA entities
     * @return lista de domain entities
     */
    private List<Cuenta> convertirListaDominio(List<CuentaJPAEntity> cuentasJPA) {
        List<Cuenta> cuentas = new ArrayList<>();
        for (CuentaJPAEntity cuentaJPA : cuentasJPA) {
            cuentas.add(jpaToDomain(cuentaJPA));
        }
        return cuentas;
    }
}
