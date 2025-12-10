package com.wallet.application.dtos;

import java.math.BigDecimal;

/**
 * DTO para el resultado de una conversión de divisa.
 */
public class ConversionDivisaDTO {
    
    private final BigDecimal cantidadOriginal;
    private final String monedaOrigen;
    private final BigDecimal cantidadConvertida;
    private final String monedaDestino;
    private final double tasaCambio;
    
    /**
     * Constructor.
     * 
     * @param cantidadOriginal cantidad original
     * @param monedaOrigen moneda origen
     * @param cantidadConvertida cantidad después de la conversión
     * @param monedaDestino moneda destino
     * @param tasaCambio tasa de cambio aplicada
     */
    public ConversionDivisaDTO(
            BigDecimal cantidadOriginal,
            String monedaOrigen,
            BigDecimal cantidadConvertida,
            String monedaDestino,
            double tasaCambio) {
        this.cantidadOriginal = cantidadOriginal;
        this.monedaOrigen = monedaOrigen;
        this.cantidadConvertida = cantidadConvertida;
        this.monedaDestino = monedaDestino;
        this.tasaCambio = tasaCambio;
    }
    
    public BigDecimal getCantidadOriginal() {
        return cantidadOriginal;
    }
    
    public String getMonedaOrigen() {
        return monedaOrigen;
    }
    
    public BigDecimal getCantidadConvertida() {
        return cantidadConvertida;
    }
    
    public String getMonedaDestino() {
        return monedaDestino;
    }
    
    public double getTasaCambio() {
        return tasaCambio;
    }
    
    @Override
    public String toString() {
        return String.format(
            "%s %s = %s %s (Tasa: %.4f)",
            cantidadOriginal, monedaOrigen,
            cantidadConvertida, monedaDestino,
            tasaCambio
        );
    }
}
