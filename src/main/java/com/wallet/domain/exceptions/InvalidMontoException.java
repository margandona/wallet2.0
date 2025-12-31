package com.wallet.domain.exceptions;

import java.math.BigDecimal;

/**
 * Excepción lanzada cuando el monto de una transacción es inválido (0 o negativo).
 */
public class InvalidMontoException extends RuntimeException {
    
    public InvalidMontoException(BigDecimal monto) {
        super(String.format("El monto debe ser mayor a 0. Valor: %s", monto));
    }
    
    public InvalidMontoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
