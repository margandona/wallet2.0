package com.wallet.infrastructure.validation;

import com.wallet.domain.exceptions.*;
import com.wallet.domain.valueobjects.DocumentoIdentidad;
import com.wallet.domain.valueobjects.Email;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utilidad de validación para datos de entrada.
 *
 * Proporciona métodos estáticos para validar:
 * - Formato de email
 * - Formato de documento de identidad
 * - Saldo (no negativo)
 * - Monto de transacción (positivo)
 * - Información de usuario
 * - Información de cuenta
 *
 * Lanza excepciones específicas de dominio cuando la validación falla.
 *
 * Uso:
 * ValidatorUtil.validarEmail("usuario@mail.com");
 * ValidatorUtil.validarSaldo(new BigDecimal("1000.00"));
 * ValidatorUtil.validarMonto(new BigDecimal("50.00"));
 */
public class ValidatorUtil {

    private static final String EMAIL_REGEX = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    private static final String CEDULA_REGEX = "^[0-9]{7,10}$";
    private static final String PASAPORTE_REGEX = "^[A-Z0-9]{6,9}$";
    private static final String LICENCIA_REGEX = "^[0-9]{7,12}$";
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern CEDULA_PATTERN = Pattern.compile(CEDULA_REGEX);
    private static final Pattern PASAPORTE_PATTERN = Pattern.compile(PASAPORTE_REGEX);
    private static final Pattern LICENCIA_PATTERN = Pattern.compile(LICENCIA_REGEX);

    /**
     * Valida el formato de un email.
     *
     * @param email el email a validar
     * @throws InvalidEmailFormatException si el formato es inválido
     */
    public static void validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidEmailFormatException("El email no puede estar vacío", null);
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailFormatException(email);
        }
    }

    /**
     * Valida el formato de un documento de identidad según su tipo.
     *
     * @param documento el número de documento
     * @param tipoDocumento el tipo (CEDULA, PASAPORTE, LICENCIA)
     * @throws InvalidDocumentoFormatException si el formato es inválido
     */
    public static void validarDocumento(String documento, String tipoDocumento) {
        if (documento == null || documento.trim().isEmpty()) {
            throw new InvalidDocumentoFormatException("El documento no puede estar vacío", new IllegalArgumentException("Documento vacío"));
        }

        boolean valido = false;
        
        switch (tipoDocumento.toUpperCase()) {
            case "CEDULA" -> valido = CEDULA_PATTERN.matcher(documento).matches();
            case "PASAPORTE" -> valido = PASAPORTE_PATTERN.matcher(documento).matches();
            case "LICENCIA" -> valido = LICENCIA_PATTERN.matcher(documento).matches();
            default -> throw new InvalidDocumentoFormatException(
                String.format("Tipo de documento inválido: %s", tipoDocumento), 
                new IllegalArgumentException("Tipo documento inválido")
            );
        }

        if (!valido) {
            throw new InvalidDocumentoFormatException(documento, tipoDocumento);
        }
    }

    /**
     * Valida que el saldo sea válido (no negativo).
     *
     * @param saldo el saldo a validar
     * @throws InvalidSaldoException si el saldo es negativo
     */
    public static void validarSaldo(BigDecimal saldo) {
        if (saldo == null) {
            throw new InvalidSaldoException(BigDecimal.ZERO);
        }
        
        if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidSaldoException(saldo);
        }
    }

    /**
     * Valida que el monto sea válido (positivo y mayor que 0).
     *
     * @param monto el monto a validar
     * @throws InvalidMontoException si el monto es 0 o negativo
     */
    public static void validarMonto(BigDecimal monto) {
        if (monto == null) {
            throw new InvalidMontoException(BigDecimal.ZERO);
        }
        
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidMontoException(monto);
        }
    }

    /**
     * Valida que un nombre no esté vacío.
     *
     * @param nombre el nombre a validar
     * @throws IllegalArgumentException si está vacío
     */
    public static void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        
        if (nombre.trim().length() < 2) {
            throw new IllegalArgumentException("El nombre debe tener al menos 2 caracteres");
        }
    }

    /**
     * Valida que un número de cuenta tenga formato válido.
     *
     * @param numeroCuenta el número de cuenta
     * @throws IllegalArgumentException si el formato es inválido
     */
    public static void validarNumeroCuenta(String numeroCuenta) {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede estar vacío");
        }
        
        // Validar que solo contenga números y tenga 10-20 dígitos
        if (!numeroCuenta.matches("^[0-9]{10,20}$")) {
            throw new IllegalArgumentException(
                "El número de cuenta debe contener 10-20 dígitos"
            );
        }
    }

    /**
     * Valida que haya suficiente saldo para una operación.
     *
     * @param saldoActual saldo actual disponible
     * @param montoRequerido monto a retirar/transferir
     * @throws com.wallet.domain.exceptions.SaldoInsuficienteException si no hay saldo
     */
    public static void validarSaldoSuficiente(BigDecimal saldoActual, BigDecimal montoRequerido) {
        if (saldoActual.compareTo(montoRequerido) < 0) {
            throw new SaldoInsuficienteException(
                String.format(
                    "Saldo insuficiente. Disponible: %s, Requerido: %s",
                    saldoActual, montoRequerido
                )
            );
        }
    }

    /**
     * Valida una descripción de transacción.
     *
     * @param descripcion la descripción a validar
     * @throws IllegalArgumentException si no es válida
     */
    public static void validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }
        
        if (descripcion.length() > 500) {
            throw new IllegalArgumentException("La descripción no puede exceder 500 caracteres");
        }
    }
}
