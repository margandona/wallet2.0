package com.wallet.domain.exceptions;

/**
 * Excepción base para errores de repositorio.
 */
public class RepositoryException extends RuntimeException {
    
    public RepositoryException(String mensaje) {
        super(mensaje);
    }
    
    public RepositoryException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
    
    public static RepositoryException operacionFallida(String entidad, String operacion, String razon) {
        return new RepositoryException(
            String.format("Error al %s %s. Razón: %s", operacion, entidad, razon)
        );
    }
}
