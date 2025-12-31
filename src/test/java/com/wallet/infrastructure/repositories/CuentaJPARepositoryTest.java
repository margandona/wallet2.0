package com.wallet.infrastructure.repositories;

import com.wallet.domain.entities.Cuenta;
import com.wallet.domain.entities.Usuario;
import com.wallet.domain.valueobjects.Dinero;
import com.wallet.domain.valueobjects.DocumentoIdentidad;
import com.wallet.domain.valueobjects.Email;
import com.wallet.infrastructure.config.JPAConfiguration;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Tests de integración para CuentaJPARepository.
 *
 * Prueba:
 * - Operaciones CRUD para cuentas
 * - Búsquedas por usuario
 * - Validaciones de número de cuenta único
 * - Relación con usuarios
 */
@DisplayName("CuentaJPARepository - Tests de Integración")
public class CuentaJPARepositoryTest {

    private CuentaJPARepository cuentaRepository;
    private UsuarioJPARepository usuarioRepository;
    private Usuario usuarioTest;

    @BeforeAll
    static void setupAll() {
        if (!JPAConfiguration.isInitialized()) {
            JPAConfiguration.initialize();
        }
    }

    @BeforeEach
    void setUp() {
        cuentaRepository = new CuentaJPARepository();
        usuarioRepository = new UsuarioJPARepository();
        limpiarBD();

        // Crear usuario de prueba
        Email email = new Email("cuentas@example.com");
        DocumentoIdentidad documento = new DocumentoIdentidad("99999999", DocumentoIdentidad.TipoDocumento.DNI);
        usuarioTest = new Usuario("Usuario", "Test", email, documento);
        usuarioTest = usuarioRepository.guardar(usuarioTest);
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
    @DisplayName("✅ Crear nueva cuenta")
    void testCrearNuevaCuenta() {
        // Arrange
        Cuenta cuenta = new Cuenta(usuarioTest.getId());

        // Act
        Cuenta cuentaGuardada = cuentaRepository.guardar(cuenta);

        // Assert
        Assertions.assertNotNull(cuentaGuardada.getId());
        Assertions.assertNotNull(cuentaGuardada.getNumeroCuenta());
        Assertions.assertEquals(usuarioTest.getId(), cuentaGuardada.getUsuarioId());
        Assertions.assertTrue(cuentaGuardada.isActiva());
        Assertions.assertTrue(cuentaGuardada.getSaldo().getCantidad().compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    @DisplayName("✅ Buscar cuenta por ID")
    void testBuscarPorId() {
        // Arrange
        Cuenta cuenta = new Cuenta(usuarioTest.getId());
        Cuenta cuentaGuardada = cuentaRepository.guardar(cuenta);

        // Act
        Optional<Cuenta> cuentaEncontrada = cuentaRepository.buscarPorId(cuentaGuardada.getId());

        // Assert
        Assertions.assertTrue(cuentaEncontrada.isPresent());
        Assertions.assertEquals(cuentaGuardada.getId(), cuentaEncontrada.get().getId());
    }

    @Test
    @DisplayName("✅ Buscar cuenta por número de cuenta")
    void testBuscarPorNumeroCuenta() {
        // Arrange
        Cuenta cuenta = new Cuenta(usuarioTest.getId());
        Cuenta cuentaGuardada = cuentaRepository.guardar(cuenta);

        // Act
        Optional<Cuenta> cuentaEncontrada = cuentaRepository.buscarPorNumeroCuenta(cuentaGuardada.getNumeroCuenta());

        // Assert
        Assertions.assertTrue(cuentaEncontrada.isPresent());
        Assertions.assertEquals(cuentaGuardada.getNumeroCuenta(), cuentaEncontrada.get().getNumeroCuenta());
    }

    @Test
    @DisplayName("✅ Obtener todas las cuentas de un usuario")
    void testBuscarPorUsuarioId() {
        // Arrange
        Cuenta cuenta1 = new Cuenta(usuarioTest.getId());
        Cuenta cuenta2 = new Cuenta(usuarioTest.getId());
        cuentaRepository.guardar(cuenta1);
        cuentaRepository.guardar(cuenta2);

        // Act
        List<Cuenta> cuentas = cuentaRepository.buscarPorUsuarioId(usuarioTest.getId());

        // Assert
        Assertions.assertEquals(2, cuentas.size());
    }

    @Test
    @DisplayName("✅ Obtener solo cuentas activas de usuario")
    void testObtenerActivasPorUsuario() {
        // Arrange
        Cuenta cuentaActiva = new Cuenta(usuarioTest.getId());
        Cuenta cuentaInactiva = new Cuenta(usuarioTest.getId());
        cuentaInactiva.desactivar();

        cuentaRepository.guardar(cuentaActiva);
        cuentaRepository.guardar(cuentaInactiva);

        // Act
        List<Cuenta> cuentasActivas = cuentaRepository.obtenerActivasPorUsuario(usuarioTest.getId());

        // Assert
        Assertions.assertEquals(1, cuentasActivas.size());
        Assertions.assertTrue(cuentasActivas.get(0).isActiva());
    }

    @Test
    @DisplayName("✅ Obtener todas las cuentas")
    void testObtenerTodas() {
        // Arrange
        Cuenta cuenta1 = new Cuenta(usuarioTest.getId());
        Cuenta cuenta2 = new Cuenta(usuarioTest.getId());
        cuentaRepository.guardar(cuenta1);
        cuentaRepository.guardar(cuenta2);

        // Act
        List<Cuenta> cuentas = cuentaRepository.obtenerTodas();

        // Assert
        Assertions.assertTrue(cuentas.size() >= 2);
    }

    @Test
    @DisplayName("✅ Eliminar cuenta")
    void testEliminar() {
        // Arrange
        Cuenta cuenta = new Cuenta(usuarioTest.getId());
        Cuenta cuentaGuardada = cuentaRepository.guardar(cuenta);

        // Act
        boolean eliminada = cuentaRepository.eliminar(cuentaGuardada.getId());

        // Assert
        Assertions.assertTrue(eliminada);
        Optional<Cuenta> cuentaBuscada = cuentaRepository.buscarPorId(cuentaGuardada.getId());
        Assertions.assertFalse(cuentaBuscada.isPresent());
    }

    // ==================== TESTS DE VALIDACIÓN ====================

    @Test
    @DisplayName("✅ Número de cuenta único")
    void testNumeroCuentaUnico() {
        // Arrange
        Cuenta cuenta1 = new Cuenta(usuarioTest.getId());
        cuentaRepository.guardar(cuenta1);

        // Act & Assert
        Assertions.assertTrue(cuentaRepository.existeNumeroCuenta(cuenta1.getNumeroCuenta()));
    }

    @Test
    @DisplayName("✅ Actualizar saldo de cuenta")
    void testActualizarSaldo() {
        // Arrange
        Cuenta cuenta = new Cuenta(usuarioTest.getId());
        Cuenta cuentaGuardada = cuentaRepository.guardar(cuenta);

        // Act
        Dinero monto = new Dinero(new BigDecimal("100.00"));
        cuentaGuardada.depositar(monto);
        Cuenta cuentaActualizada = cuentaRepository.guardar(cuentaGuardada);

        // Assert
        Assertions.assertEquals(new BigDecimal("100.00"), cuentaActualizada.getSaldo().getCantidad());
    }

    @Test
    @DisplayName("✅ No encontrar cuenta inexistente")
    void testBuscarCuentaInexistente() {
        // Act
        Optional<Cuenta> cuenta = cuentaRepository.buscarPorId("id-inexistente");

        // Assert
        Assertions.assertFalse(cuenta.isPresent());
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private void limpiarBD() {
        try {
            EntityManager em = JPAConfiguration.getEntityManager();
            em.getTransaction().begin();

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
