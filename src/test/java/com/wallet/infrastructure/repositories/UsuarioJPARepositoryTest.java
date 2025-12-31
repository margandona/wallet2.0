package com.wallet.infrastructure.repositories;

import com.wallet.domain.valueobjects.DocumentoIdentidad;
import com.wallet.domain.valueobjects.Email;
import com.wallet.domain.entities.Usuario;
import com.wallet.domain.entities.Cuenta;
import com.wallet.domain.valueobjects.Dinero;
import com.wallet.infrastructure.config.JPAConfiguration;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Tests de integración para UsuarioJPARepository.
 *
 * Prueba:
 * - Operaciones CRUD (Create, Read, Update, Delete)
 * - Búsquedas por email y documento
 * - Validaciones de unicidad
 * - Transacciones y rollback
 *
 * Nota: Utiliza la BD real (SQLite wallet.db)
 */
@DisplayName("UsuarioJPARepository - Tests de Integración")
public class UsuarioJPARepositoryTest {

    private UsuarioJPARepository repository;
    private static final String EMAIL_TEST = "test.usuario@example.com";
    private static final String DOCUMENTO_TEST = "87654321";

    @BeforeAll
    static void setupAll() {
        // Inicializar JPA una sola vez para todos los tests
        if (!JPAConfiguration.isInitialized()) {
            JPAConfiguration.initialize();
        }
    }

    @BeforeEach
    void setUp() {
        repository = new UsuarioJPARepository();
        // Limpiar BD antes de cada test
        limpiarBD();
    }

    @AfterEach
    void tearDown() {
        limpiarBD();
    }

    @AfterAll
    static void cleanupAll() {
        JPAConfiguration.close();
    }

    // ==================== TESTS CRUD ====================

    @Test
    @DisplayName("✅ Guardar nuevo usuario exitosamente")
    void testGuardarNuevoUsuario() {
        // Arrange
        Email email = new Email(EMAIL_TEST);
        DocumentoIdentidad documento = new DocumentoIdentidad(DOCUMENTO_TEST, DocumentoIdentidad.TipoDocumento.DNI);
        Usuario usuario = new Usuario("Juan", "Pérez", email, documento);

        // Act
        Usuario usuarioGuardado = repository.guardar(usuario);

        // Assert
        Assertions.assertNotNull(usuarioGuardado.getId());
        Assertions.assertEquals("Juan", usuarioGuardado.getNombre());
        Assertions.assertEquals("Pérez", usuarioGuardado.getApellido());
        Assertions.assertEquals(EMAIL_TEST, usuarioGuardado.getEmail().getValor());
        Assertions.assertTrue(usuarioGuardado.isActivo());
    }

    @Test
    @DisplayName("✅ Buscar usuario por ID")
    void testBuscarPorId() {
        // Arrange
        Email email = new Email(EMAIL_TEST);
        DocumentoIdentidad documento = new DocumentoIdentidad(DOCUMENTO_TEST, DocumentoIdentidad.TipoDocumento.DNI);
        Usuario usuario = new Usuario("María", "López", email, documento);
        Usuario usuarioGuardado = repository.guardar(usuario);

        // Act
        Optional<Usuario> usuarioEncontrado = repository.buscarPorId(usuarioGuardado.getId());

        // Assert
        Assertions.assertTrue(usuarioEncontrado.isPresent());
        Assertions.assertEquals("María", usuarioEncontrado.get().getNombre());
    }

    @Test
    @DisplayName("✅ Buscar usuario por email")
    void testBuscarPorEmail() {
        // Arrange
        Email email = new Email(EMAIL_TEST);
        DocumentoIdentidad documento = new DocumentoIdentidad(DOCUMENTO_TEST, DocumentoIdentidad.TipoDocumento.DNI);
        Usuario usuario = new Usuario("Carlos", "García", email, documento);
        repository.guardar(usuario);

        // Act
        Optional<Usuario> usuarioEncontrado = repository.buscarPorEmail(email);

        // Assert
        Assertions.assertTrue(usuarioEncontrado.isPresent());
        Assertions.assertEquals("Carlos", usuarioEncontrado.get().getNombre());
    }

    @Test
    @DisplayName("✅ Buscar usuario por documento")
    void testBuscarPorDocumento() {
        // Arrange
        Email email = new Email(EMAIL_TEST);
        DocumentoIdentidad documento = new DocumentoIdentidad(DOCUMENTO_TEST, DocumentoIdentidad.TipoDocumento.DNI);
        Usuario usuario = new Usuario("Ana", "Rodríguez", email, documento);
        repository.guardar(usuario);

        // Act
        Optional<Usuario> usuarioEncontrado = repository.buscarPorDocumento(DOCUMENTO_TEST);

        // Assert
        Assertions.assertTrue(usuarioEncontrado.isPresent());
        Assertions.assertEquals("Ana", usuarioEncontrado.get().getNombre());
    }

    @Test
    @DisplayName("✅ Obtener todos los usuarios")
    void testObtenerTodos() {
        // Arrange
        Email email1 = new Email("usuario1@example.com");
        Email email2 = new Email("usuario2@example.com");
        DocumentoIdentidad doc1 = new DocumentoIdentidad("11111111", DocumentoIdentidad.TipoDocumento.DNI);
        DocumentoIdentidad doc2 = new DocumentoIdentidad("22222222", DocumentoIdentidad.TipoDocumento.DNI);

        Usuario usuario1 = new Usuario("Usuario", "Uno", email1, doc1);
        Usuario usuario2 = new Usuario("Usuario", "Dos", email2, doc2);

        repository.guardar(usuario1);
        repository.guardar(usuario2);

        // Act
        List<Usuario> usuarios = repository.obtenerTodos();

        // Assert
        Assertions.assertTrue(usuarios.size() >= 2);
    }

    @Test
    @DisplayName("✅ Obtener solo usuarios activos")
    void testObtenerActivos() {
        // Arrange
        Email email1 = new Email("activo@example.com");
        Email email2 = new Email("inactivo@example.com");
        DocumentoIdentidad doc1 = new DocumentoIdentidad("33333333", DocumentoIdentidad.TipoDocumento.DNI);
        DocumentoIdentidad doc2 = new DocumentoIdentidad("44444444", DocumentoIdentidad.TipoDocumento.DNI);

        Usuario usuarioActivo = new Usuario("Activo", "User", email1, doc1);
        Usuario usuarioInactivo = new Usuario("Inactivo", "User", email2, doc2);
        usuarioInactivo.desactivar();

        repository.guardar(usuarioActivo);
        repository.guardar(usuarioInactivo);

        // Act
        List<Usuario> usuariosActivos = repository.obtenerActivos();

        // Assert
        Assertions.assertTrue(usuariosActivos.stream().allMatch(Usuario::isActivo));
        Assertions.assertTrue(usuariosActivos.stream().anyMatch(u -> u.getNombre().equals("Activo")));
    }

    @Test
    @DisplayName("✅ Eliminar usuario exitosamente")
    void testEliminar() {
        // Arrange
        Email email = new Email(EMAIL_TEST);
        DocumentoIdentidad documento = new DocumentoIdentidad(DOCUMENTO_TEST, DocumentoIdentidad.TipoDocumento.DNI);
        Usuario usuario = new Usuario("Pedro", "Sánchez", email, documento);
        Usuario usuarioGuardado = repository.guardar(usuario);

        // Act
        boolean eliminado = repository.eliminar(usuarioGuardado.getId());

        // Assert
        Assertions.assertTrue(eliminado);
        Optional<Usuario> usuarioBuscado = repository.buscarPorId(usuarioGuardado.getId());
        Assertions.assertFalse(usuarioBuscado.isPresent());
    }

    // ==================== TESTS DE VALIDACIÓN ====================

    @Test
    @DisplayName("✅ Email único - No permitir duplicados")
    void testEmailUnico() {
        // Arrange
        Email email = new Email(EMAIL_TEST);
        DocumentoIdentidad doc1 = new DocumentoIdentidad("55555555", DocumentoIdentidad.TipoDocumento.DNI);
        DocumentoIdentidad doc2 = new DocumentoIdentidad("66666666", DocumentoIdentidad.TipoDocumento.DNI);

        Usuario usuario1 = new Usuario("Usuario", "Uno", email, doc1);
        repository.guardar(usuario1);

        // Act & Assert
        Assertions.assertTrue(repository.existePorEmail(email));
        Assertions.assertThrows(Exception.class, () -> {
            Usuario usuario2 = new Usuario("Usuario", "Dos", email, doc2);
            repository.guardar(usuario2);
        });
    }

    @Test
    @DisplayName("✅ Documento único - No permitir duplicados")
    void testDocumentoUnico() {
        // Arrange
        Email email1 = new Email("usuario1@example.com");
        Email email2 = new Email("usuario2@example.com");
        DocumentoIdentidad documento = new DocumentoIdentidad(DOCUMENTO_TEST, DocumentoIdentidad.TipoDocumento.DNI);

        Usuario usuario1 = new Usuario("Usuario", "Uno", email1, documento);
        repository.guardar(usuario1);

        // Act & Assert
        Assertions.assertTrue(repository.existePorDocumento(documento));
        Assertions.assertThrows(Exception.class, () -> {
            Usuario usuario2 = new Usuario("Usuario", "Dos", email2, documento);
            repository.guardar(usuario2);
        });
    }

    @Test
    @DisplayName("✅ Actualizar usuario existente")
    void testActualizarUsuario() {
        // Arrange
        Email email = new Email(EMAIL_TEST);
        DocumentoIdentidad documento = new DocumentoIdentidad(DOCUMENTO_TEST, DocumentoIdentidad.TipoDocumento.DNI);
        Usuario usuario = new Usuario("Original", "Nombre", email, documento);
        Usuario usuarioGuardado = repository.guardar(usuario);

        // Act
        Email nuevoEmail = new Email("newemail@example.com");
        usuarioGuardado.actualizar("Actualizado", "Apellido", nuevoEmail);
        Usuario usuarioActualizado = repository.guardar(usuarioGuardado);

        // Assert
        Assertions.assertEquals("Actualizado", usuarioActualizado.getNombre());
        Assertions.assertEquals("newemail@example.com", usuarioActualizado.getEmail().getValor());
    }

    @Test
    @DisplayName("✅ No encontrar usuario por ID inexistente")
    void testBuscarPorIdNoExistente() {
        // Act
        Optional<Usuario> usuario = repository.buscarPorId("id-inexistente");

        // Assert
        Assertions.assertFalse(usuario.isPresent());
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private void limpiarBD() {
        try {
            EntityManager em = JPAConfiguration.getEntityManager();
            em.getTransaction().begin();

            // Eliminar en orden inverso de dependencias
            em.createNativeQuery("DELETE FROM transacciones").executeUpdate();
            em.createNativeQuery("DELETE FROM cuentas").executeUpdate();
            em.createNativeQuery("DELETE FROM usuarios").executeUpdate();

            em.getTransaction().commit();
            em.close();
        } catch (Exception e) {
            System.err.println("Error limpiando BD: " + e.getMessage());
        }
    }
}
