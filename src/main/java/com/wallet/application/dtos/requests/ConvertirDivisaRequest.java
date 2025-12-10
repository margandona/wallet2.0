package com.wallet.application.dtos.requests;

import java.math.BigDecimal;

/**
 * Request para convertir divisas.
 */
public class ConvertirDivisaRequest {
    
    private final BigDecimal cantidad;
    private final String monedaOrigen;
    private final String monedaDestino;
    
    /**
     * Constructor.
     * 
     * @param cantidad cantidad a convertir
     * @param monedaOrigen código ISO 4217 de la moneda origen
     * @param monedaDestino código ISO 4217 de la moneda destino
     */
    public ConvertirDivisaRequest(
            BigDecimal cantidad,
            String monedaOrigen,
            String monedaDestino) {
        this.cantidad = cantidad;
        this.monedaOrigen = monedaOrigen;
        this.monedaDestino = monedaDestino;
    }
    
    public BigDecimal getCantidad() {
        return cantidad;
    }
    
    public String getMonedaOrigen() {
        return monedaOrigen;
    }
    
    public String getMonedaDestino() {
        return monedaDestino;
    }
}
