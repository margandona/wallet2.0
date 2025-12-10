package com.wallet.presentation.utils;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Utilidades para manejo de consola.
 * Proporciona métodos para lectura, validación y formateo de entrada/salida.
 * 
 * @author Wallet Team
 * @version 1.0.0
 */
public class ConsoleUtils {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    /**
     * Imprime una línea en blanco.
     */
    public static void printLine() {
        System.out.println();
    }
    
    /**
     * Imprime un separador.
     */
    public static void printSeparator() {
        System.out.println("========================================");
    }
    
    /**
     * Imprime un encabezado.
     */
    public static void printHeader(String titulo) {
        printLine();
        printSeparator();
        System.out.println("  " + titulo);
        printSeparator();
        printLine();
    }
    
    /**
     * Imprime un mensaje de éxito.
     */
    public static void printSuccess(String mensaje) {
        System.out.println("[OK] " + mensaje);
    }
    
    /**
     * Imprime un mensaje de error.
     */
    public static void printError(String mensaje) {
        System.out.println("[ERROR] " + mensaje);
    }
    
    /**
     * Imprime un mensaje de advertencia.
     */
    public static void printWarning(String mensaje) {
        System.out.println("[AVISO] " + mensaje);
    }
    
    /**
     * Imprime un mensaje informativo.
     */
    public static void printInfo(String mensaje) {
        System.out.println("[INFO] " + mensaje);
    }
    
    /**
     * Lee una línea de texto del usuario.
     */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    /**
     * Lee un número entero del usuario.
     */
    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printError("Por favor ingrese un numero valido.");
            }
        }
    }
    
    /**
     * Lee un número entero en un rango específico.
     */
    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            printError("El numero debe estar entre " + min + " y " + max + ".");
        }
    }
    
    /**
     * Lee un BigDecimal del usuario.
     */
    public static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) <= 0) {
                    printError("El monto debe ser mayor a cero.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                printError("Por favor ingrese un monto valido (ejemplo: 100.50).");
            }
        }
    }
    
    /**
     * Lee una confirmación (S/N) del usuario.
     */
    public static boolean readConfirmation(String prompt) {
        while (true) {
            String input = readLine(prompt + " (S/N): ").toUpperCase();
            if (input.equals("S") || input.equals("SI")) {
                return true;
            } else if (input.equals("N") || input.equals("NO")) {
                return false;
            }
            printError("Por favor ingrese S o N.");
        }
    }
    
    /**
     * Pausa hasta que el usuario presione Enter.
     */
    public static void pause() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Limpia la pantalla (simulado con saltos de línea).
     */
    public static void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
    
    /**
     * Valida que un string no esté vacío.
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Valida formato de email básico.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.contains("@") && email.contains(".");
    }
    
    /**
     * Formatea un BigDecimal como moneda.
     */
    public static String formatMoney(BigDecimal amount) {
        return String.format("$%.2f", amount);
    }
    
    /**
     * Obtiene el scanner compartido.
     */
    public static Scanner getScanner() {
        return scanner;
    }
}
