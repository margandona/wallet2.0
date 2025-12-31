package com.wallet.infrastructure.integration;

import com.wallet.domain.entities.Cuenta;
import com.wallet.domain.entities.Transaccion;
import com.wallet.domain.entities.Usuario;
import com.wallet.domain.valueobjects.Dinero;
import com.wallet.domain.valueobjects.DocumentoIdentidad;
import com.wallet.domain.valueobjects.Email;
import com.wallet.domain.valueobjects.TipoTransaccion;
import com.wallet.infrastructure.config.JPAConfiguration;
import com.wallet.infrastructure.repositories.CuentaJPARepository;
import com.wallet.infrastructure.repositories.TransaccionJPARepository;
import com.wallet.infrastructure.repositories.UsuarioJPARepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Tests de Integración Completa - End-to-End.
 *
 * Prueba flujo completo:
 * 1. Crear usuario
 * 2. Crear cuenta para usuario
 * 3. Realizar transacciones (depósito, retiro, transferencia)
 * 4. Validar consistencia de datos
 * 5. Validar historial de transacciones
 */
@DisplayName("Flujo Completo - Tests de Integración E2E")
public class FlujoComipletoIntegrationTest {

    private UsuarioJPARepository usuarioRepository;
    private CuentaJPARepository cuentaRepository;
    private TransaccionJPARepository transaccionRepository;

    @BeforeAll
    static void setupAll() {
        if (!JPAConfiguration.isInitialized()) {
            JPAConfiguration.initialize();
        }
    }

    @BeforeEach
    void setUp() {
        usuarioRepository = new UsuarioJPARepository();
        cuentaRepository = new CuentaJPARepository();
        transaccionRepository = new TransaccionJPARepository();
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

    // ==================== TESTS E2E ====================

    @Test
    @DisplayName("✅ E2E: Crear usuario → Crear cuenta → Depósito → Validar estado")
    void testFlujoCmpletoCreaciónYDepósito() {
        // ============================================
        // PASO 1: Crear Usuario
        // ============================================
        Email email = new Email("usuario@example.com");
        DocumentoIdentidad documento = new DocumentoIdentidad("12345678", DocumentoIdentidad.TipoDocumento.DNI);
        Usuario usuario = new Usuario("Juan", "Pérez", email, documento);

        Usuario usuarioGuardado = usuarioRepository.guardar(usuario);
        Assertions.assertNotNull(usuarioGuardado.getId());
        System.out.println("✅ Usuario creado: " + usuarioGuardado.getId());

        // ============================================
        // PASO 2: Buscar Usuario Creado
        // ============================================
        Optional<Usuario> usuarioBuscado = usuarioRepository.buscarPorEmail(email);
        Assertions.assertTrue(usuarioBuscado.isPresent());
        System.out.println("✅ Usuario encontrado: " + usuarioBuscado.get().getNombre());

        // ============================================
        // PASO 3: Crear Cuenta para el Usuario
        // ============================================
        Cuenta cuenta = new Cuenta(usuarioGuardado.getId());
        Cuenta cuentaGuardada = cuentaRepository.guardar(cuenta);

        Assertions.assertNotNull(cuentaGuardada.getId());
        Assertions.assertNotNull(cuentaGuardada.getNumeroCuenta());
        Assertions.assertEquals(usuarioGuardado.getId(), cuentaGuardada.getUsuarioId());
        System.out.println("✅ Cuenta creada: " + cuentaGuardada.getNumeroCuenta());

        // ============================================
        // PASO 4: Realizar Depósito
        // ============================================
        Dinero montoDeposito = new Dinero(new BigDecimal("1000.00"));
        cuentaGuardada.depositar(montoDeposito);
        Cuenta cuentaActualizada = cuentaRepository.guardar(cuentaGuardada);

        Assertions.assertEquals(new BigDecimal("1000.00"), cuentaActualizada.getSaldo().getCantidad());
        System.out.println("✅ Depósito realizado: " + montoDeposito.getCantidad());

        // ============================================
        // PASO 5: Crear Transacción de Depósito
        // ============================================
        Transaccion transaccion = Transaccion.deposito(
            montoDeposito,
            cuentaGuardada.getId(),
            "Depósito inicial",
            Dinero.CERO,
            montoDeposito
        );

        Transaccion transaccionGuardada = transaccionRepository.guardar(transaccion);
        Assertions.assertNotNull(transaccionGuardada.getId());
        Assertions.assertEquals(TipoTransaccion.DEPOSITO, transaccionGuardada.getTipo());
        System.out.println("✅ Transacción de depósito registrada");

        // ============================================
        // PASO 6: Validar Historial
        // ============================================
        List<Transaccion> historial = transaccionRepository.buscarPorCuentaId(cuentaGuardada.getId());
        Assertions.assertEquals(1, historial.size());
        Assertions.assertEquals(TipoTransaccion.DEPOSITO, historial.get(0).getTipo());
        System.out.println("✅ Historial de transacciones validado");
    }

    @Test
    @DisplayName("✅ E2E: Múltiples cuentas por usuario")
    void testMultiplesCuentasPorUsuario() {
        // Crear usuario
        Email email = new Email("multicuentas@example.com");
        DocumentoIdentidad documento = new DocumentoIdentidad("87654321", DocumentoIdentidad.TipoDocumento.DNI);
        Usuario usuario = new Usuario("María", "García", email, documento);
        Usuario usuarioGuardado = usuarioRepository.guardar(usuario);

        // Crear múltiples cuentas
        Cuenta cuenta1 = new Cuenta(usuarioGuardado.getId());
        Cuenta cuenta2 = new Cuenta(usuarioGuardado.getId());
        Cuenta cuenta3 = new Cuenta(usuarioGuardado.getId());

        Cuenta c1 = cuentaRepository.guardar(cuenta1);
        Cuenta c2 = cuentaRepository.guardar(cuenta2);
        Cuenta c3 = cuentaRepository.guardar(cuenta3);

        // Verificar que se crearon todas
        List<Cuenta> cuentasUsuario = cuentaRepository.buscarPorUsuarioId(usuarioGuardado.getId());
        Assertions.assertEquals(3, cuentasUsuario.size());
        System.out.println("✅ " + cuentasUsuario.size() + " cuentas creadas para el usuario");

        // Realizar operaciones en diferentes cuentas
        Dinero monto = new Dinero(new BigDecimal("500.00"));
        c1.depositar(monto);
        c2.depositar(new Dinero(new BigDecimal("200.00")));

        cuentaRepository.guardar(c1);
        cuentaRepository.guardar(c2);

        // Obtener solo cuentas activas
        List<Cuenta> cuentasActivas = cuentaRepository.obtenerActivasPorUsuario(usuarioGuardado.getId());
        Assertions.assertEquals(3, cuentasActivas.size());
        System.out.println("✅ Todas las cuentas activas");
    }

    @Test
    @DisplayName("✅ E2E: Ciclo completo de transacciones (Depósito → Retiro → Validación)")
    void testCicloCompletoTransacciones() {
        // Crear usuario y cuenta
        Email email = new Email("transacciones@example.com");
        DocumentoIdentidad documento = new DocumentoIdentidad("11111111", DocumentoIdentidad.TipoDocumento.DNI);
        Usuario usuario = new Usuario("Carlos", "López", email, documento);
        Usuario usuarioGuardado = usuarioRepository.guardar(usuario);

        Cuenta cuenta = new Cuenta(usuarioGuardado.getId());
        Cuenta cuentaGuardada = cuentaRepository.guardar(cuenta);

        // ============================================
        // TRANSACCIÓN 1: Depósito Inicial
        // ============================================
        Dinero deposito1 = new Dinero(new BigDecimal("1000.00"));
        cuentaGuardada.depositar(deposito1);
        cuentaRepository.guardar(cuentaGuardada);

        Transaccion transDeposito1 = Transaccion.deposito(
            deposito1, cuentaGuardada.getId(), "Depósito 1",
            Dinero.CERO, deposito1
        );
        transaccionRepository.guardar(transDeposito1);

        // ============================================
        // TRANSACCIÓN 2: Depósito Adicional
        // ============================================
        Dinero deposito2 = new Dinero(new BigDecimal("500.00"));
        Dinero saldoAntes2 = cuentaGuardada.getSaldo();
        cuentaGuardada.depositar(deposito2);

        Dinero saldoDespues2 = cuentaGuardada.getSaldo();
        cuentaRepository.guardar(cuentaGuardada);

        Transaccion transDeposito2 = Transaccion.deposito(
            deposito2, cuentaGuardada.getId(), "Depósito 2",
            saldoAntes2, saldoDespues2
        );
        transaccionRepository.guardar(transDeposito2);

        // ============================================
        // TRANSACCIÓN 3: Retiro
        // ============================================
        Dinero retiro = new Dinero(new BigDecimal("300.00"));
        Dinero saldoAntesRetiro = cuentaGuardada.getSaldo();
        cuentaGuardada.retirar(retiro);

        Dinero saldoDespuesRetiro = cuentaGuardada.getSaldo();
        cuentaRepository.guardar(cuentaGuardada);

        Transaccion transRetiro = Transaccion.retiro(
            retiro, cuentaGuardada.getId(), "Retiro",
            saldoAntesRetiro, saldoDespuesRetiro
        );
        transaccionRepository.guardar(transRetiro);

        // ============================================
        // VALIDACIONES
        // ============================================
        Cuenta cuentaFinal = cuentaRepository.buscarPorId(cuentaGuardada.getId()).get();
        BigDecimal saldoEsperado = new BigDecimal("1200.00"); // 1000 + 500 - 300
        Assertions.assertEquals(saldoEsperado, cuentaFinal.getSaldo().getCantidad());
        System.out.println("✅ Saldo final correcto: " + cuentaFinal.getSaldo().getCantidad());

        // Verificar historial
        List<Transaccion> historial = transaccionRepository.buscarPorCuentaId(cuentaGuardada.getId());
        Assertions.assertEquals(3, historial.size());
        System.out.println("✅ " + historial.size() + " transacciones registradas");

        // Contar por tipo
        List<Transaccion> depositos = transaccionRepository.buscarPorCuentaIdYTipo(cuentaGuardada.getId(), TipoTransaccion.DEPOSITO);
        List<Transaccion> retiros = transaccionRepository.buscarPorCuentaIdYTipo(cuentaGuardada.getId(), TipoTransaccion.RETIRO);

        Assertions.assertEquals(2, depositos.size());
        Assertions.assertEquals(1, retiros.size());
        System.out.println("✅ Historial por tipo: " + depositos.size() + " depósitos, " + retiros.size() + " retiros");
    }

    @Test
    @DisplayName("✅ E2E: Validación de integridad referencial")
    void testIntegridadReferencial() {
        // Crear usuario
        Email email = new Email("integridad@example.com");
        DocumentoIdentidad documento = new DocumentoIdentidad("22222222", DocumentoIdentidad.TipoDocumento.DNI);
        Usuario usuario = new Usuario("Ana", "Rodríguez", email, documento);
        Usuario usuarioGuardado = usuarioRepository.guardar(usuario);

        // Crear 2 cuentas
        Cuenta cuenta1 = new Cuenta(usuarioGuardado.getId());
        Cuenta cuenta2 = new Cuenta(usuarioGuardado.getId());

        Cuenta c1 = cuentaRepository.guardar(cuenta1);
        Cuenta c2 = cuentaRepository.guardar(cuenta2);

        // Realizar transacciones en ambas cuentas
        Dinero monto = new Dinero(new BigDecimal("100.00"));
        c1.depositar(monto);
        c2.depositar(monto);

        cuentaRepository.guardar(c1);
        cuentaRepository.guardar(c2);

        Transaccion t1 = Transaccion.deposito(monto, c1.getId(), "T1", Dinero.CERO, monto);
        Transaccion t2 = Transaccion.deposito(monto, c2.getId(), "T2", Dinero.CERO, monto);

        transaccionRepository.guardar(t1);
        transaccionRepository.guardar(t2);

        // Verificar que las transacciones pertenecen a sus cuentas respectivas
        List<Transaccion> transC1 = transaccionRepository.buscarPorCuentaId(c1.getId());
        List<Transaccion> transC2 = transaccionRepository.buscarPorCuentaId(c2.getId());

        Assertions.assertEquals(1, transC1.size());
        Assertions.assertEquals(1, transC2.size());
        Assertions.assertEquals(c1.getId(), transC1.get(0).getCuentaOrigenId());
        Assertions.assertEquals(c2.getId(), transC2.get(0).getCuentaOrigenId());

        System.out.println("✅ Integridad referencial validada");
    }

    @Test
    @DisplayName("✅ E2E: Listar usuarios y sus resúmenes")
    void testResumenUsuariosYCuentas() {
        // Crear 3 usuarios
        for (int i = 1; i <= 3; i++) {
            Email email = new Email("usuario" + i + "@example.com");
            DocumentoIdentidad doc = new DocumentoIdentidad(String.format("%08d", 30000000 + i), DocumentoIdentidad.TipoDocumento.DNI);
            Usuario usuario = new Usuario("Usuario", "" + i, email, doc);
            Usuario usuarioGuardado = usuarioRepository.guardar(usuario);

            // Crear 2 cuentas por usuario
            for (int j = 0; j < 2; j++) {
                Cuenta cuenta = new Cuenta(usuarioGuardado.getId());
                Dinero monto = new Dinero(new BigDecimal(String.valueOf((i + 1) * (j + 1) * 100)));
                cuenta.depositar(monto);

                cuentaRepository.guardar(cuenta);
            }
        }

        // Verificar
        List<Usuario> usuarios = usuarioRepository.obtenerTodos();
        Assertions.assertEquals(3, usuarios.size());
        System.out.println("✅ " + usuarios.size() + " usuarios creados");

        for (Usuario usuario : usuarios) {
            List<Cuenta> cuentas = cuentaRepository.buscarPorUsuarioId(usuario.getId());
            System.out.println("   → " + usuario.getNombre() + " tiene " + cuentas.size() + " cuenta(s)");
            Assertions.assertEquals(2, cuentas.size());
        }
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
