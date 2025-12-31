package com.wallet.domain.exceptions;

/**
 * Excepción lanzada cuando el formato del email es inválido.
 */
public class InvalidEmailFormatException extends RuntimeException {
    
    public InvalidEmailFormatException(String email) {
        super(String.format("El email '%s' tiene un formato inválido.", email));
    }
    
    public InvalidEmailFormatException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
