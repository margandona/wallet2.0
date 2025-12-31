package com.wallet.domain.exceptions;

import java.math.BigDecimal;

/**
 * Excepción lanzada cuando el saldo es inválido (negativo).
 */
public class InvalidSaldoException extends RuntimeException {
    
    public InvalidSaldoException(BigDecimal saldo) {
        super(String.format("El saldo no puede ser negativo. Valor: %s", saldo));
    }
    
    public InvalidSaldoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
