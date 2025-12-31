package com.wallet.domain.exceptions;

/**
 * Excepción lanzada cuando un número de cuenta ya existe.
 */
public class DuplicateCuentaException extends RuntimeException {
    
    public DuplicateCuentaException(String numeroCuenta) {
        super(String.format("El número de cuenta '%s' ya existe en el sistema.", numeroCuenta));
    }
    
    public DuplicateCuentaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
