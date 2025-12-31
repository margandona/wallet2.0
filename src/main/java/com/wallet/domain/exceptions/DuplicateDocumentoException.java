package com.wallet.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta crear un usuario con un documento que ya existe.
 */
public class DuplicateDocumentoException extends RuntimeException {
    
    public DuplicateDocumentoException(String documento, String tipoDocumento) {
        super(String.format("El documento '%s' (%s) ya está registrado en el sistema.", documento, tipoDocumento));
    }
    
    public DuplicateDocumentoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
