package com.wallet.infrastructure.services;

import com.wallet.domain.services.IConversorDivisas;
import com.wallet.domain.valueobjects.Dinero;
import com.wallet.infrastructure.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementación del conversor de divisas usando API pública.
 * Usa exchangerate-api.com para obtener tasas de cambio en tiempo real.
 * 
 * API: https://open.er-api.com/v6/latest/{currency}
 * Gratis, sin API key requerida, 1500 requests/mes
 * 
 * Principios aplicados:
 * - SRP: Solo responsable de conversión de divisas
 * - DIP: Implementa interfaz del dominio
 */
public class ConversorDivisasAPI implements IConversorDivisas {
    
    private static final String API_URL = "https://open.er-api.com/v6/latest/";
    private static final int TIMEOUT_MS = 5000;
    private static final int CACHE_DURATION_MS = 3600000; // 1 hora
    
    private final Map<String, CachedRate> cache = new HashMap<>();
    
    /**
     * Clase interna para cachear tasas de cambio.
     */
    private static class CachedRate {
        final Map<String, Double> rates;
        final long timestamp;
        
        CachedRate(Map<String, Double> rates) {
            this.rates = rates;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_DURATION_MS;
        }
    }
    
    @Override
    public double obtenerTasaCambio(String monedaOrigen, String monedaDestino) {
        validarMoneda(monedaOrigen);
        validarMoneda(monedaDestino);
        
        if (monedaOrigen.equals(monedaDestino)) {
            return 1.0;
        }
        
        try {
            Map<String, Double> rates = obtenerTasas(monedaOrigen);
            
            if (!rates.containsKey(monedaDestino)) {
                throw new IllegalArgumentException(
                    "Moneda destino no soportada: " + monedaDestino
                );
            }
            
            return rates.get(monedaDestino);
            
        } catch (Exception e) {
            Logger.error("Error obteniendo tasa de cambio: " + e.getMessage());
            throw new RuntimeException(
                "No se pudo obtener la tasa de cambio. Verifique su conexión a internet.", 
                e
            );
        }
    }
    
    @Override
    public Dinero convertir(Dinero dinero, String monedaDestino) {
        if (dinero == null) {
            throw new IllegalArgumentException("El dinero no puede ser nulo");
        }
        
        validarMoneda(monedaDestino);
        
        if (dinero.getMoneda().equals(monedaDestino)) {
            return dinero;
        }
        
        double tasaCambio = obtenerTasaCambio(dinero.getMoneda(), monedaDestino);
        
        BigDecimal cantidadConvertida = dinero.getCantidad()
            .multiply(BigDecimal.valueOf(tasaCambio))
            .setScale(2, RoundingMode.HALF_UP);
        
        Logger.info(String.format(
            "Conversión: %s %s → %s %s (Tasa: %.4f)",
            dinero.getCantidad(), dinero.getMoneda(),
            cantidadConvertida, monedaDestino,
            tasaCambio
        ));
        
        return new Dinero(cantidadConvertida, monedaDestino);
    }
    
    @Override
    public boolean estaDisponible() {
        try {
            obtenerTasas("USD");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Obtiene las tasas de cambio desde la API o caché.
     */
    private Map<String, Double> obtenerTasas(String monedaBase) throws Exception {
        // Verificar caché
        CachedRate cached = cache.get(monedaBase);
        if (cached != null && !cached.isExpired()) {
            Logger.info("Usando tasas de cambio desde caché para " + monedaBase);
            return cached.rates;
        }
        
        // Obtener desde API
        Logger.info("Obteniendo tasas de cambio desde API para " + monedaBase);
        Map<String, Double> rates = fetchRatesFromAPI(monedaBase);
        
        // Guardar en caché
        cache.put(monedaBase, new CachedRate(rates));
        
        return rates;
    }
    
    /**
     * Realiza la petición HTTP a la API.
     */
    private Map<String, Double> fetchRatesFromAPI(String monedaBase) throws Exception {
        String urlString = API_URL + monedaBase;
        URL url = new URL(urlString);
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        conn.setRequestProperty("Accept", "application/json");
        
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException(
                "Error en la API: código de respuesta " + responseCode
            );
        }
        
        BufferedReader in = new BufferedReader(
            new InputStreamReader(conn.getInputStream())
        );
        
        StringBuilder response = new StringBuilder();
        String inputLine;
        
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        conn.disconnect();
        
        return parseJSON(response.toString());
    }
    
    /**
     * Parse simple del JSON (sin librerías externas).
     * Busca el objeto "rates" y extrae los pares clave-valor.
     */
    private Map<String, Double> parseJSON(String json) {
        Map<String, Double> rates = new HashMap<>();
        
        // Buscar el objeto "rates"
        Pattern ratesPattern = Pattern.compile("\"rates\"\\s*:\\s*\\{([^}]+)\\}");
        Matcher ratesMatcher = ratesPattern.matcher(json);
        
        if (!ratesMatcher.find()) {
            throw new RuntimeException("Formato de respuesta JSON inválido");
        }
        
        String ratesContent = ratesMatcher.group(1);
        
        // Extraer cada par "MONEDA": valor
        Pattern pairPattern = Pattern.compile("\"([A-Z]{3})\"\\s*:\\s*([0-9.]+)");
        Matcher pairMatcher = pairPattern.matcher(ratesContent);
        
        while (pairMatcher.find()) {
            String currency = pairMatcher.group(1);
            double rate = Double.parseDouble(pairMatcher.group(2));
            rates.put(currency, rate);
        }
        
        if (rates.isEmpty()) {
            throw new RuntimeException("No se pudieron extraer tasas de cambio");
        }
        
        return rates;
    }
    
    private void validarMoneda(String moneda) {
        if (moneda == null || moneda.trim().isEmpty()) {
            throw new IllegalArgumentException("La moneda no puede estar vacía");
        }
        
        if (moneda.length() != 3) {
            throw new IllegalArgumentException(
                "El código de moneda debe tener 3 caracteres (ISO 4217)"
            );
        }
        
        if (!moneda.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                "El código de moneda debe ser 3 letras mayúsculas (ej: USD, EUR, PEN)"
            );
        }
    }
}
