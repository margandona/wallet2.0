package com.wallet.infrastructure.repositories;

import com.wallet.domain.entities.Usuario;
import com.wallet.domain.exceptions.*;
import com.wallet.domain.repositories.IUsuarioRepository;
import com.wallet.domain.valueobjects.DocumentoIdentidad;
import com.wallet.domain.valueobjects.Email;
import com.wallet.infrastructure.config.JPAConfiguration;
import com.wallet.infrastructure.entities.UsuarioJPAEntity;
import com.wallet.infrastructure.logging.OperationLogger;
import com.wallet.infrastructure.validation.ValidatorUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA del repositorio de Usuarios.
 *
 * Responsabilidades:
 * - Convertir entre Domain Entities (Usuario) y JPA Entities (UsuarioJPAEntity)
 * - Ejecutar operaciones CRUD en la base de datos
 * - Manejar transacciones
 * - Traducir excepciones JPA a excepciones de dominio
 */
public class UsuarioJPARepository implements IUsuarioRepository {

    /**
     * Guarda un usuario nuevo o actualiza uno existente.
     *
     * @param usuario el usuario a guardar (de dominio)
     * @return el usuario guardado (de dominio)
     * @throws InvalidEmailFormatException si el email no es válido
     * @throws InvalidDocumentoFormatException si el documento no es válido
     * @throws DuplicateEmailException si el email ya existe
     * @throws DuplicateDocumentoException si el documento ya existe
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public Usuario guardar(Usuario usuario) {
        EntityManager em = null;
        try {
            // Validar entrada
            ValidatorUtil.validarEmail(usuario.getEmail().getValor());
            ValidatorUtil.validarDocumento(
                usuario.getDocumentoIdentidad().getNumero(),
                usuario.getDocumentoIdentidad().getTipo().name()
            );

            em = JPAConfiguration.getEntityManager();

            em.getTransaction().begin();

            // Convertir usuario de dominio a JPA entity
            UsuarioJPAEntity usuarioJPA = domainToJPA(usuario);

            // Persist del usuario
            em.persist(usuarioJPA);

            em.flush();
            
            em.getTransaction().commit();
            
            // Pequeña pausa para asegurar que SQLite sincronice
            Thread.sleep(100);
            
            // Forzar sincronización de SQLite
            try (java.sql.Connection sqlConn = java.sql.DriverManager.getConnection("jdbc:sqlite:wallet.db");
                 java.sql.Statement stmt = sqlConn.createStatement()) {
                stmt.execute("PRAGMA synchronous = FULL");
                stmt.execute("PRAGMA wal_checkpoint(RESTART)");
            } catch (Exception e) {
                // Ignorar errores de sincronización
            }

            // Convertir de vuelta a dominio y retornar
            Usuario usuarioResultado = jpaToDomain(usuarioJPA);
            
            OperationLogger.logCreate("Usuario", usuarioResultado.getId(),
                String.format("Usuario %s %s creado", usuarioResultado.getNombre(), usuarioResultado.getApellido()));
            
            return usuarioResultado;
            
        } catch (InvalidEmailFormatException | InvalidDocumentoFormatException |
                 DuplicateEmailException | DuplicateDocumentoException e) {
            // Excepciones de validación - propagar directamente
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (PersistenceException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OperationLogger.logError("Usuario", usuario.getId(), "Error de persistencia al guardar", e);
            throw RepositoryException.operacionFallida("Usuario", "guardar", e.getMessage());
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OperationLogger.logError("Usuario", usuario.getId(), "Error inesperado al guardar", e);
            throw RepositoryException.operacionFallida("Usuario", "guardar", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id el ID del usuario
     * @return Optional con el usuario si existe
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public Optional<Usuario> buscarPorId(String id) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();
            UsuarioJPAEntity usuarioJPA = em.find(UsuarioJPAEntity.class, id);

            if (usuarioJPA != null) {
                OperationLogger.logRead("Usuario", id, "Usuario encontrado");
                return Optional.of(jpaToDomain(usuarioJPA));
            }
            
            OperationLogger.logRead("Usuario", id, "Usuario no encontrado");
            return Optional.empty();
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Usuario", id, "Error de persistencia al buscar", e);
            throw RepositoryException.operacionFallida("Usuario", "buscar", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Usuario", id, "Error inesperado al buscar", e);
            throw RepositoryException.operacionFallida("Usuario", "buscar", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Busca un usuario por su email.
     *
     * @param email el email del usuario
     * @return Optional con el usuario si existe
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public Optional<Usuario> buscarPorEmail(Email email) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT u FROM UsuarioJPAEntity u WHERE u.email = :email", UsuarioJPAEntity.class);
            query.setParameter("email", email.getValor());

            try {
                UsuarioJPAEntity usuarioJPA = (UsuarioJPAEntity) query.getSingleResult();
                OperationLogger.logRead("Usuario", usuarioJPA.getId(), "Usuario encontrado por email: " + email.getValor());
                return Optional.of(jpaToDomain(usuarioJPA));
            } catch (NoResultException e) {
                OperationLogger.logRead("Usuario", "desconocido", "No encontrado usuario con email: " + email.getValor());
                return Optional.empty();
            }
        } catch (PersistenceException e) {
            OperationLogger.logError("Usuario", "desconocido", "Error de persistencia al buscar por email", e);
            throw RepositoryException.operacionFallida("Usuario", "buscar por email", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Usuario", "desconocido", "Error inesperado al buscar por email", e);
            throw RepositoryException.operacionFallida("Usuario", "buscar por email", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Busca un usuario por su número de documento.
     *
     * @param numeroDocumento el número del documento
     * @return Optional con el usuario si existe
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public Optional<Usuario> buscarPorDocumento(String numeroDocumento) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT u FROM UsuarioJPAEntity u WHERE u.documento = :documento", UsuarioJPAEntity.class);
            query.setParameter("documento", numeroDocumento);

            try {
                UsuarioJPAEntity usuarioJPA = (UsuarioJPAEntity) query.getSingleResult();
                OperationLogger.logRead("Usuario", usuarioJPA.getId(), "Usuario encontrado por documento: " + numeroDocumento);
                return Optional.of(jpaToDomain(usuarioJPA));
            } catch (NoResultException e) {
                OperationLogger.logRead("Usuario", "desconocido", "No encontrado usuario con documento: " + numeroDocumento);
                return Optional.empty();
            }
        } catch (PersistenceException e) {
            OperationLogger.logError("Usuario", "desconocido", "Error de persistencia al buscar por documento", e);
            throw RepositoryException.operacionFallida("Usuario", "buscar por documento", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Usuario", "desconocido", "Error inesperado al buscar por documento", e);
            throw RepositoryException.operacionFallida("Usuario", "buscar por documento", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Obtiene todos los usuarios.
     *
     * @return lista de todos los usuarios
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public List<Usuario> obtenerTodos() {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT u FROM UsuarioJPAEntity u ORDER BY u.nombre, u.apellido");
            List<UsuarioJPAEntity> usuariosJPA = query.getResultList();

            OperationLogger.logRead("Usuario", "todos", "Se obtuvieron " + usuariosJPA.size() + " usuarios");
            return convertirListaDominio(usuariosJPA);
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Usuario", "todos", "Error de persistencia al obtener todos", e);
            throw RepositoryException.operacionFallida("Usuario", "obtener todos", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Usuario", "todos", "Error inesperado al obtener todos", e);
            throw RepositoryException.operacionFallida("Usuario", "obtener todos", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Obtiene todos los usuarios activos.
     *
     * @return lista de usuarios activos
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public List<Usuario> obtenerActivos() {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT u FROM UsuarioJPAEntity u WHERE u.activo = true ORDER BY u.nombre, u.apellido");
            List<UsuarioJPAEntity> usuariosJPA = query.getResultList();

            OperationLogger.logRead("Usuario", "activos", "Se obtuvieron " + usuariosJPA.size() + " usuarios activos");
            return convertirListaDominio(usuariosJPA);
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Usuario", "activos", "Error de persistencia al obtener activos", e);
            throw RepositoryException.operacionFallida("Usuario", "obtener activos", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Usuario", "activos", "Error inesperado al obtener activos", e);
            throw RepositoryException.operacionFallida("Usuario", "obtener activos", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id el ID del usuario
     * @return true si se eliminó correctamente
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public boolean eliminar(String id) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();
            em.getTransaction().begin();

            UsuarioJPAEntity usuario = em.find(UsuarioJPAEntity.class, id);

            if (usuario == null) {
                em.getTransaction().rollback();
                OperationLogger.logWarn("Intento de eliminar usuario no existente: " + id);
                return false;
            }

            em.remove(usuario);
            em.getTransaction().commit();
            em.flush();
            
            OperationLogger.logDelete("Usuario", id, "Usuario eliminado exitosamente");
            return true;
            
        } catch (PersistenceException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OperationLogger.logError("Usuario", id, "Error de persistencia al eliminar", e);
            throw RepositoryException.operacionFallida("Usuario", "eliminar", e.getMessage());
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OperationLogger.logError("Usuario", id, "Error inesperado al eliminar", e);
            throw RepositoryException.operacionFallida("Usuario", "eliminar", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Verifica si existe un usuario con el email dado.
     *
     * @param email el email a verificar
     * @return true si existe
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public boolean existePorEmail(Email email) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT COUNT(u) FROM UsuarioJPAEntity u WHERE u.email = :email");
            query.setParameter("email", email.getValor());

            Long count = (Long) query.getSingleResult();

            boolean existe = count > 0;
            if (existe) {
                OperationLogger.logRead("Usuario", "verificación", "Email " + email.getValor() + " ya existe");
            }
            return existe;
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Usuario", "verificación", "Error de persistencia al verificar email", e);
            throw RepositoryException.operacionFallida("Usuario", "verificar email", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Usuario", "verificación", "Error inesperado al verificar email", e);
            throw RepositoryException.operacionFallida("Usuario", "verificar email", e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Verifica si existe un usuario con el documento dado.
     *
     * @param numeroDocumento el número de documento
     * @return true si existe
     * @throws RepositoryException si ocurre error en BD
     */
    @Override
    public boolean existePorDocumento(com.wallet.domain.valueobjects.DocumentoIdentidad documento) {
        EntityManager em = null;
        try {
            em = JPAConfiguration.getEntityManager();

            Query query = em.createQuery("SELECT COUNT(u) FROM UsuarioJPAEntity u WHERE u.documento = :documento");
            query.setParameter("documento", documento.getNumero());

            Long count = (Long) query.getSingleResult();

            boolean existe = count > 0;
            if (existe) {
                OperationLogger.logRead("Usuario", "verificación", "Documento " + documento.getNumero() + " ya existe");
            }
            return existe;
            
        } catch (PersistenceException e) {
            OperationLogger.logError("Usuario", "verificación", "Error de persistencia al verificar documento", e);
            throw RepositoryException.operacionFallida("Usuario", "verificar documento", e.getMessage());
        } catch (Exception e) {
            OperationLogger.logError("Usuario", "verificación", "Error inesperado al verificar documento", e);
            throw RepositoryException.operacionFallida("Usuario", "verificar documento", e.getMessage());
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
     * @param usuarioJPA entidad JPA
     * @return entidad de dominio
     */
    private Usuario jpaToDomain(UsuarioJPAEntity usuarioJPA) {
        Email email = new Email(usuarioJPA.getEmail());
        DocumentoIdentidad documento = new DocumentoIdentidad(
            usuarioJPA.getDocumento(),
            DocumentoIdentidad.TipoDocumento.valueOf(usuarioJPA.getTipoDocumento())
        );

        return new Usuario(
            usuarioJPA.getId(),
            usuarioJPA.getNombre(),
            usuarioJPA.getApellido(),
            email,
            documento,
            usuarioJPA.getCreatedAt(),
            usuarioJPA.getUpdatedAt(),
            usuarioJPA.isActivo()
        );
    }

    /**
     * Convierte una entidad de dominio a entidad JPA.
     *
     * @param usuario entidad de dominio
     * @return entidad JPA
     */
    private UsuarioJPAEntity domainToJPA(Usuario usuario) {
        UsuarioJPAEntity usuarioJPA = new UsuarioJPAEntity(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getEmail().getValor(),
            usuario.getDocumentoIdentidad().getNumero(),
            usuario.getDocumentoIdentidad().getTipo().name(),
            usuario.isActivo(),
            usuario.getFechaCreacion(),
            usuario.getFechaActualizacion()
        );

        return usuarioJPA;
    }

    /**
     * Convierte lista de entidades JPA a lista de entidades de dominio.
     *
     * @param usuariosJPA lista de JPA entities
     * @return lista de domain entities
     */
    private List<Usuario> convertirListaDominio(List<UsuarioJPAEntity> usuariosJPA) {
        List<Usuario> usuarios = new ArrayList<>();
        for (UsuarioJPAEntity usuarioJPA : usuariosJPA) {
            usuarios.add(jpaToDomain(usuarioJPA));
        }
        return usuarios;
    }
}
