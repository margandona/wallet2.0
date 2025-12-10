package com.wallet.domain.services;

import com.wallet.domain.valueobjects.Dinero;

/**
 * Interfaz para el servicio de conversión de divisas.
 * Define el contrato para obtener tasas de cambio y convertir monedas.
 * 
 * Principios aplicados:
 * - DIP: La capa de dominio define el contrato, Infrastructure lo implementa
 * - ISP: Interfaz específica para conversión de divisas
 */
public interface IConversorDivisas {
    
    /**
     * Obtiene la tasa de cambio entre dos monedas.
     * 
     * @param monedaOrigen código ISO 4217 de la moneda origen (ej: "USD")
     * @param monedaDestino código ISO 4217 de la moneda destino (ej: "PEN")
     * @return la tasa de cambio
     * @throws IllegalArgumentException si las monedas son inválidas
     * @throws RuntimeException si no se puede obtener la tasa
     */
    double obtenerTasaCambio(String monedaOrigen, String monedaDestino);
    
    /**
     * Convierte una cantidad de dinero a otra moneda.
     * 
     * @param dinero el dinero a convertir
     * @param monedaDestino código ISO 4217 de la moneda destino
     * @return nuevo Dinero en la moneda destino
     * @throws IllegalArgumentException si la moneda es inválida
     * @throws RuntimeException si no se puede realizar la conversión
     */
    Dinero convertir(Dinero dinero, String monedaDestino);
    
    /**
     * Verifica si el servicio está disponible.
     * 
     * @return true si el servicio está disponible, false en caso contrario
     */
    boolean estaDisponible();
}
