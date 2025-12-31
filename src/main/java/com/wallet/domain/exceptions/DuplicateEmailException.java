package com.wallet.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta crear un usuario con un email que ya existe.
 */
public class DuplicateEmailException extends RuntimeException {
    
    public DuplicateEmailException(String email) {
        super(String.format("El email '%s' ya está registrado en el sistema.", email));
    }
    
    public DuplicateEmailException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
