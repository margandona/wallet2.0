package com.wallet.domain.exceptions;

/**
 * Excepción lanzada cuando el formato del documento es inválido.
 */
public class InvalidDocumentoFormatException extends RuntimeException {
    
    public InvalidDocumentoFormatException(String documento, String tipoDocumento) {
        super(String.format("El documento '%s' (%s) tiene un formato inválido.", documento, tipoDocumento));
    }
    
    public InvalidDocumentoFormatException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
