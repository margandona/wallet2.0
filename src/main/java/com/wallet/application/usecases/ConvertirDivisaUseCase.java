package com.wallet.application.usecases;

import com.wallet.application.dtos.ConversionDivisaDTO;
import com.wallet.application.dtos.requests.ConvertirDivisaRequest;
import com.wallet.domain.services.IConversorDivisas;
import com.wallet.domain.valueobjects.Dinero;

/**
 * Caso de uso para convertir divisas.
 * 
 * Principios aplicados:
 * - SRP: Solo responsable de conversión de divisas
 * - DIP: Depende de abstracción (IConversorDivisas)
 */
public class ConvertirDivisaUseCase {
    
    private final IConversorDivisas conversorDivisas;
    
    /**
     * Constructor con inyección de dependencias.
     * 
     * @param conversorDivisas el servicio de conversión
     */
    public ConvertirDivisaUseCase(IConversorDivisas conversorDivisas) {
        if (conversorDivisas == null) {
            throw new IllegalArgumentException(
                "El conversor de divisas no puede ser nulo"
            );
        }
        this.conversorDivisas = conversorDivisas;
    }
    
    /**
     * Ejecuta la conversión de divisa.
     * 
     * @param request los datos de la conversión
     * @return DTO con el resultado de la conversión
     * @throws IllegalArgumentException si el request es inválido
     * @throws RuntimeException si no se puede realizar la conversión
     */
    public ConversionDivisaDTO ejecutar(ConvertirDivisaRequest request) {
        validarRequest(request);
        
        Dinero dineroOriginal = new Dinero(
            request.getCantidad(),
            request.getMonedaOrigen()
        );
        
        double tasaCambio = conversorDivisas.obtenerTasaCambio(
            request.getMonedaOrigen(),
            request.getMonedaDestino()
        );
        
        Dinero dineroConvertido = conversorDivisas.convertir(
            dineroOriginal,
            request.getMonedaDestino()
        );
        
        return new ConversionDivisaDTO(
            dineroOriginal.getCantidad(),
            request.getMonedaOrigen(),
            dineroConvertido.getCantidad(),
            request.getMonedaDestino(),
            tasaCambio
        );
    }
    
    /**
     * Verifica si el servicio de conversión está disponible.
     * 
     * @return true si está disponible, false en caso contrario
     */
    public boolean estaDisponible() {
        return conversorDivisas.estaDisponible();
    }
    
    private void validarRequest(ConvertirDivisaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El request no puede ser nulo");
        }
        
        if (request.getCantidad() == null) {
            throw new IllegalArgumentException("La cantidad no puede ser nula");
        }
        
        if (request.getMonedaOrigen() == null || request.getMonedaOrigen().trim().isEmpty()) {
            throw new IllegalArgumentException("La moneda origen no puede estar vacía");
        }
        
        if (request.getMonedaDestino() == null || request.getMonedaDestino().trim().isEmpty()) {
            throw new IllegalArgumentException("La moneda destino no puede estar vacía");
        }
    }
}
