package com.wallet.infrastructure.repositories;

import com.wallet.domain.entities.Cuenta;
import com.wallet.domain.entities.Transaccion;
import com.wallet.domain.entities.Usuario;
import com.wallet.domain.valueobjects.Dinero;
import com.wallet.domain.valueobjects.DocumentoIdentidad;
import com.wallet.domain.valueobjects.Email;
import com.wallet.domain.valueobjects.TipoTransaccion;
import com.wallet.infrastructure.config.JPAConfiguration;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Tests de integración para TransaccionJPARepository.
 *
 * Prueba:
 * - Operaciones CRUD para transacciones
 * - Búsquedas por cuenta, tipo y fechas
 * - Historial de transacciones
 * - Relación con cuentas
 */
@DisplayName("TransaccionJPARepository - Tests de Integración")
public class TransaccionJPARepositoryTest {

    private TransaccionJPARepository transaccionRepository;
    private CuentaJPARepository cuentaRepository;
    private UsuarioJPARepository usuarioRepository;
    private Usuario usuarioTest;
    private Cuenta cuentaTest;

    @BeforeAll
    static void setupAll() {
        if (!JPAConfiguration.isInitialized()) {
            JPAConfiguration.initialize();
        }
    }

    @BeforeEach
    void setUp() {
        transaccionRepository = new TransaccionJPARepository();
        cuentaRepository = new CuentaJPARepository();
        usuarioRepository = new UsuarioJPARepository();
        limpiarBD();

        // Crear usuario y cuenta de prueba
        Email email = new Email("transacciones@example.com");
        DocumentoIdentidad documento = new DocumentoIdentidad("88888888", DocumentoIdentidad.TipoDocumento.DNI);
        usuarioTest = new Usuario("Usuario", "Test", email, documento);
        usuarioTest = usuarioRepository.guardar(usuarioTest);

        cuentaTest = new Cuenta(usuarioTest.getId());
        cuentaTest = cuentaRepository.guardar(cuentaTest);
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
    @DisplayName("✅ Guardar transacción de depósito")
    void testGuardarDeposito() {
        // Arrange
        Dinero monto = new Dinero(new BigDecimal("100.00"));
        Transaccion transaccion = Transaccion.deposito(monto, cuentaTest.getId(), "Depósito inicial", Dinero.CERO, monto);

        // Act
        Transaccion transaccionGuardada = transaccionRepository.guardar(transaccion);

        // Assert
        Assertions.assertNotNull(transaccionGuardada.getId());
        Assertions.assertEquals(TipoTransaccion.DEPOSITO, transaccionGuardada.getTipo());
        Assertions.assertEquals(new BigDecimal("100.00"), transaccionGuardada.getMonto().getCantidad());
    }

    @Test
    @DisplayName("✅ Buscar transacción por ID")
    void testBuscarPorId() {
        // Arrange
        Dinero monto = new Dinero(new BigDecimal("50.00"));
        Transaccion transaccion = Transaccion.deposito(monto, cuentaTest.getId(), "Test", Dinero.CERO, monto);
        Transaccion transaccionGuardada = transaccionRepository.guardar(transaccion);

        // Act
        Optional<Transaccion> transaccionEncontrada = transaccionRepository.buscarPorId(transaccionGuardada.getId());

        // Assert
        Assertions.assertTrue(transaccionEncontrada.isPresent());
        Assertions.assertEquals(transaccionGuardada.getId(), transaccionEncontrada.get().getId());
    }

    @Test
    @DisplayName("✅ Obtener todas las transacciones de una cuenta")
    void testBuscarPorCuentaId() {
        // Arrange
        Dinero monto1 = new Dinero(new BigDecimal("100.00"));
        Dinero monto2 = new Dinero(new BigDecimal("50.00"));

        Transaccion trans1 = Transaccion.deposito(monto1, cuentaTest.getId(), "Dep 1", Dinero.CERO, monto1);
        Transaccion trans2 = Transaccion.deposito(monto2, cuentaTest.getId(), "Dep 2", monto1, new Dinero(new BigDecimal("150.00")));

        transaccionRepository.guardar(trans1);
        transaccionRepository.guardar(trans2);

        // Act
        List<Transaccion> transacciones = transaccionRepository.buscarPorCuentaId(cuentaTest.getId());

        // Assert
        Assertions.assertEquals(2, transacciones.size());
    }

    @Test
    @DisplayName("✅ Obtener transacciones filtradas por tipo")
    void testBuscarPorCuentaIdYTipo() {
        // Arrange
        Dinero monto = new Dinero(new BigDecimal("100.00"));

        Transaccion deposito = Transaccion.deposito(monto, cuentaTest.getId(), "Dep", Dinero.CERO, monto);
        Transaccion retiro = Transaccion.retiro(monto, cuentaTest.getId(), "Ret", monto, Dinero.CERO);

        transaccionRepository.guardar(deposito);
        transaccionRepository.guardar(retiro);

        // Act
        List<Transaccion> depositos = transaccionRepository.buscarPorCuentaIdYTipo(cuentaTest.getId(), TipoTransaccion.DEPOSITO);
        List<Transaccion> retiros = transaccionRepository.buscarPorCuentaIdYTipo(cuentaTest.getId(), TipoTransaccion.RETIRO);

        // Assert
        Assertions.assertEquals(1, depositos.size());
        Assertions.assertEquals(1, retiros.size());
        Assertions.assertEquals(TipoTransaccion.DEPOSITO, depositos.get(0).getTipo());
        Assertions.assertEquals(TipoTransaccion.RETIRO, retiros.get(0).getTipo());
    }

    @Test
    @DisplayName("✅ Obtener últimas N transacciones")
    void testObtenerUltimasPorCuenta() {
        // Arrange
        Dinero monto = new Dinero(new BigDecimal("10.00"));

        for (int i = 0; i < 5; i++) {
            Dinero nuevoSaldo = new Dinero(new BigDecimal(String.valueOf(i * 10 + 10)));
            Transaccion trans = Transaccion.deposito(monto, cuentaTest.getId(), "Dep " + i, 
                    new Dinero(new BigDecimal(String.valueOf(i * 10))), nuevoSaldo);
            transaccionRepository.guardar(trans);
        }

        // Act
        List<Transaccion> ultimas = transaccionRepository.obtenerUltimasPorCuenta(cuentaTest.getId(), 3);

        // Assert
        Assertions.assertEquals(3, ultimas.size());
    }

    @Test
    @DisplayName("✅ Obtener todas las transacciones")
    void testObtenerTodas() {
        // Arrange
        Dinero monto = new Dinero(new BigDecimal("25.00"));

        Transaccion trans1 = Transaccion.deposito(monto, cuentaTest.getId(), "T1", Dinero.CERO, monto);
        Transaccion trans2 = Transaccion.deposito(monto, cuentaTest.getId(), "T2", monto, new Dinero(new BigDecimal("50.00")));

        transaccionRepository.guardar(trans1);
        transaccionRepository.guardar(trans2);

        // Act
        List<Transaccion> transacciones = transaccionRepository.obtenerTodas();

        // Assert
        Assertions.assertTrue(transacciones.size() >= 2);
    }

    @Test
    @DisplayName("✅ No encontrar transacción inexistente")
    void testBuscarTransaccionInexistente() {
        // Act
        Optional<Transaccion> transaccion = transaccionRepository.buscarPorId("id-inexistente");

        // Assert
        Assertions.assertFalse(transaccion.isPresent());
    }

    // ==================== TESTS DE RANGO DE FECHAS ====================

    @Test
    @DisplayName("✅ Obtener transacciones por rango de fechas")
    void testObtenerPorFechas() {
        // Arrange
        Dinero monto = new Dinero(new BigDecimal("75.00"));
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime hace1Dia = ahora.minusDays(1);
        LocalDateTime dentro1Dia = ahora.plusDays(1);

        Transaccion trans = Transaccion.deposito(monto, cuentaTest.getId(), "Trans", Dinero.CERO, monto);
        transaccionRepository.guardar(trans);

        // Act
        List<Transaccion> transacciones = transaccionRepository.obtenerPorCuentaYFechas(
                cuentaTest.getId(), hace1Dia, dentro1Dia);

        // Assert
        Assertions.assertTrue(transacciones.size() >= 1);
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
