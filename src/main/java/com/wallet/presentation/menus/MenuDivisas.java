package com.wallet.presentation.menus;

import com.wallet.application.dtos.ConversionDivisaDTO;
import com.wallet.application.dtos.requests.ConvertirDivisaRequest;
import com.wallet.application.usecases.ConvertirDivisaUseCase;
import com.wallet.domain.services.IConversorDivisas;
import com.wallet.infrastructure.services.ConversorDivisasAPI;
import com.wallet.presentation.utils.ConsoleUtils;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Menú para conversión de divisas.
 */
public class MenuDivisas {
    
    private final Scanner scanner;
    private final ConvertirDivisaUseCase convertirDivisaUseCase;
    
    // Monedas - Principales del mundo + Latinoamérica
    private static final String[][] MONEDAS_COMUNES = {
        // Principales mundiales
        {"USD", "Dólar Estadounidense"},
        {"EUR", "Euro"},
        {"GBP", "Libra Esterlina"},
        {"JPY", "Yen Japonés"},
        {"CHF", "Franco Suizo"},
        {"CNY", "Yuan Chino"},
        {"SGD", "Dólar Singapur"},
        {"HKD", "Dólar Hong Kong"},
        {"AUD", "Dólar Australiano"},
        {"CAD", "Dólar Canadiense"},
        {"NZD", "Dólar Nueva Zelanda"},
        {"INR", "Rupia India"},
        {"KRW", "Won Coreano"},
        {"AED", "Dirham EAU"},
        {"ZAR", "Rand Sudáfrica"},
        
        // Latinoamérica - Principales
        {"MXN", "Peso Mexicano"},
        {"BRL", "Real Brasileño"},
        {"PEN", "Sol Peruano"},
        {"CLP", "Peso Chileno"},
        {"COP", "Peso Colombiano"},
        {"ARS", "Peso Argentino"},
        {"UYU", "Peso Uruguayo"},
        {"PYG", "Guaraní Paraguayo"},
        {"BOB", "Boliviano"},
        {"VES", "Bolívar Venezolano"},
        {"GTQ", "Quetzal Guatemalteco"},
        {"HNL", "Lempira Hondureño"},
        {"CRC", "Colón Costarricense"},
        {"PAN", "Balboa Panameño"}
    };
    
    public MenuDivisas(Scanner scanner) {
        this.scanner = scanner;
        IConversorDivisas conversor = new ConversorDivisasAPI();
        this.convertirDivisaUseCase = new ConvertirDivisaUseCase(conversor);
    }
    
    public void mostrar() {
        boolean continuar = true;
        
        while (continuar) {
            try {
                ConsoleUtils.clearScreen();
                ConsoleUtils.printHeader("CONVERSOR DE DIVISAS");
                
                System.out.println("1. Convertir divisa");
                System.out.println("2. Ver monedas disponibles");
                System.out.println("3. Verificar disponibilidad del servicio");
                System.out.println("0. Volver al menú principal");
                System.out.println();
                
                int opcion = ConsoleUtils.readInt("Seleccione una opción: ");
                
                switch (opcion) {
                    case 1:
                        convertirDivisa();
                        break;
                    case 2:
                        mostrarMonedasDisponibles();
                        break;
                    case 3:
                        verificarDisponibilidad();
                        break;
                    case 0:
                        continuar = false;
                        break;
                    default:
                        ConsoleUtils.printError("Opción inválida");
                        ConsoleUtils.pause();
                }
                
            } catch (Exception e) {
                ConsoleUtils.printError("Error inesperado: " + e.getMessage());
                ConsoleUtils.pause();
            }
        }
    }
    
    private void convertirDivisa() {
        try {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("CONVERTIR DIVISA");
            
            System.out.println("Ingrese los datos de la conversión:");
            System.out.println();
            
            BigDecimal cantidad = ConsoleUtils.readBigDecimal("Cantidad: ");
            
            System.out.println();
            System.out.println("Monedas comunes (o ingrese código ISO 4217):");
            for (int i = 0; i < MONEDAS_COMUNES.length; i++) {
                System.out.printf("%2d. %s - %s%n", 
                    i + 1, 
                    MONEDAS_COMUNES[i][0], 
                    MONEDAS_COMUNES[i][1]
                );
            }
            System.out.println();
            
            String monedaOrigen = leerMoneda("Moneda origen (número o código): ");
            String monedaDestino = leerMoneda("Moneda destino (número o código): ");
            
            System.out.println();
            System.out.println("⏳ Consultando tasa de cambio...");
            
            ConvertirDivisaRequest request = new ConvertirDivisaRequest(
                cantidad,
                monedaOrigen,
                monedaDestino
            );
            
            ConversionDivisaDTO resultado = convertirDivisaUseCase.ejecutar(request);
            
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("RESULTADO DE LA CONVERSIÓN");
            
            System.out.println();
            System.out.println("┌─────────────────────────────────────────────────────┐");
            System.out.printf("│  CANTIDAD ORIGINAL:  %10s %-3s              │%n",
                resultado.getCantidadOriginal(), resultado.getMonedaOrigen());
            System.out.println("├─────────────────────────────────────────────────────┤");
            System.out.printf("│  TASA DE CAMBIO:     1 %s = %.6f %s        │%n",
                resultado.getMonedaOrigen(), 
                resultado.getTasaCambio(),
                resultado.getMonedaDestino());
            System.out.println("├─────────────────────────────────────────────────────┤");
            System.out.printf("│  CANTIDAD CONVERTIDA: %10s %-3s             │%n",
                resultado.getCantidadConvertida(), resultado.getMonedaDestino());
            System.out.println("└─────────────────────────────────────────────────────┘");
            System.out.println();
            
            ConsoleUtils.printSuccess("Conversión realizada exitosamente");
            
        } catch (IllegalArgumentException e) {
            ConsoleUtils.printError("Datos inválidos: " + e.getMessage());
        } catch (RuntimeException e) {
            ConsoleUtils.printError("Error: " + e.getMessage());
        } catch (Exception e) {
            ConsoleUtils.printError("Error inesperado: " + e.getMessage());
        }
        
        ConsoleUtils.pause();
    }
    
    private void mostrarMonedasDisponibles() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("MONEDAS DISPONIBLES");
        
        System.out.println();
        System.out.println("Principales monedas soportadas:");
        System.out.println();
        
        for (String[] moneda : MONEDAS_COMUNES) {
            System.out.printf("  • %s - %s%n", moneda[0], moneda[1]);
        }
        
        System.out.println();
        System.out.println("💡 También puede usar cualquier código ISO 4217 válido");
        System.out.println("   (3 letras mayúsculas, ej: SEK, NOK, DKK, etc.)");
        System.out.println();
        
        ConsoleUtils.pause();
    }
    
    private void verificarDisponibilidad() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("VERIFICAR DISPONIBILIDAD");
        
        System.out.println();
        System.out.println("⏳ Verificando conexión con el servicio...");
        
        boolean disponible = convertirDivisaUseCase.estaDisponible();
        
        System.out.println();
        if (disponible) {
            ConsoleUtils.printSuccess("✓ Servicio disponible");
            System.out.println();
            System.out.println("  El conversor de divisas está funcionando correctamente.");
            System.out.println("  Tasas de cambio actualizadas en tiempo real.");
        } else {
            ConsoleUtils.printError("✗ Servicio no disponible");
            System.out.println();
            System.out.println("  No se puede conectar al servicio de conversión.");
            System.out.println("  Verifique su conexión a internet.");
        }
        
        System.out.println();
        ConsoleUtils.pause();
    }
    
    private String leerMoneda(String mensaje) {
        System.out.print(mensaje);
        String input = scanner.nextLine().trim().toUpperCase();
        
        // Si es un número, buscar en la lista
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < MONEDAS_COMUNES.length) {
                return MONEDAS_COMUNES[index][0];
            }
        } catch (NumberFormatException e) {
            // No es un número, asumir que es un código de moneda
        }
        
        // Validar que sea un código válido (3 letras)
        if (input.length() == 3 && input.matches("[A-Z]{3}")) {
            return input;
        }
        
        throw new IllegalArgumentException("Código de moneda inválido: " + input);
    }
}
