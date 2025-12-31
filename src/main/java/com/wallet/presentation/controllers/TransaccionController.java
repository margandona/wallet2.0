package com.wallet.presentation.controllers;

import com.wallet.application.dtos.TransaccionDTO;
import com.wallet.infrastructure.services.TransaccionService;
import com.wallet.presentation.utils.ConsoleUtils;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador para operaciones de transacciones.
 * 
 * @author Wallet Team
 * @version 1.0.0
 */
public class TransaccionController {
    
    private final TransaccionService transaccionService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }
    
    /**
     * Realiza una transferencia entre cuentas.
     */
    public List<TransaccionDTO> transferir() {
        ConsoleUtils.printHeader("TRANSFERIR DINERO");
        
        String numeroCuentaOrigen = ConsoleUtils.readLine("Numero de cuenta origen: ");
        String numeroCuentaDestino = ConsoleUtils.readLine("Numero de cuenta destino: ");
        BigDecimal monto = ConsoleUtils.readBigDecimal("Monto a transferir: $");
        String descripcion = ConsoleUtils.readLine("Descripcion (opcional): ");
        
        if (descripcion.isEmpty()) {
            descripcion = "Transferencia";
        }
        
        ConsoleUtils.printLine();
        System.out.println("Resumen de la transferencia:");
        System.out.println("  Desde: " + numeroCuentaOrigen);
        System.out.println("  Hacia: " + numeroCuentaDestino);
        System.out.println("  Monto: " + ConsoleUtils.formatMoney(monto));
        ConsoleUtils.printLine();
        
        if (!ConsoleUtils.readConfirmation("Confirmar transferencia")) {
            ConsoleUtils.printWarning("Operacion cancelada.");
            return null;
        }
        
        try {
            List<TransaccionDTO> transacciones = transaccionService.transferirPorNumero(
                numeroCuentaOrigen, numeroCuentaDestino, monto, descripcion
            );
            ConsoleUtils.printSuccess("Transferencia realizada exitosamente!");
            ConsoleUtils.printInfo("Se generaron " + transacciones.size() + " transacciones.");
            return transacciones;
            
        } catch (Exception e) {
            ConsoleUtils.printError("Error al transferir: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Consulta el historial de transacciones de una cuenta.
     */
    public void consultarHistorial() {
        ConsoleUtils.printHeader("HISTORIAL DE TRANSACCIONES");
        
        String numeroCuenta = ConsoleUtils.readLine("Numero de cuenta: ");
        
        try {
            List<TransaccionDTO> transacciones = transaccionService.consultarHistorial(numeroCuenta);
            
            if (transacciones.isEmpty()) {
                ConsoleUtils.printInfo("No hay transacciones registradas.");
                return;
            }
            
            ConsoleUtils.printLine();
            System.out.println("Total de transacciones: " + transacciones.size());
            ConsoleUtils.printSeparator();
            
            for (int i = 0; i < transacciones.size(); i++) {
                TransaccionDTO t = transacciones.get(i);
                System.out.println((i + 1) + ". " + t.getTipo());
                System.out.println("   Fecha: " + t.getFecha().format(DATE_FORMATTER));
                System.out.println("   Monto: " + ConsoleUtils.formatMoney(t.getMonto()) + " " + t.getMoneda());
                System.out.println("   Descripcion: " + t.getDescripcion());
                
                if (t.getCuentaDestinoId() != null) {
                    System.out.println("   Cuenta destino: " + t.getCuentaDestinoId());
                }
                
                ConsoleUtils.printLine();
            }
            
        } catch (Exception e) {
            ConsoleUtils.printError("Error al consultar historial: " + e.getMessage());
        }
    }
    
    /**
     * Consulta las últimas N transacciones.
     */
    public void consultarUltimas() {
        ConsoleUtils.printHeader("ULTIMAS TRANSACCIONES");
        
        String cuentaId = ConsoleUtils.readLine("ID de la cuenta: ");
        int limite = ConsoleUtils.readIntInRange("Cantidad de transacciones (1-50): ", 1, 50);
        
        try {
            List<TransaccionDTO> transacciones = transaccionService.consultarUltimas(cuentaId, limite);
            
            if (transacciones.isEmpty()) {
                ConsoleUtils.printInfo("No hay transacciones registradas.");
                return;
            }
            
            ConsoleUtils.printLine();
            System.out.println("Mostrando " + transacciones.size() + " transacciones:");
            ConsoleUtils.printSeparator();
            
            for (TransaccionDTO t : transacciones) {
                System.out.println(t.getTipo() + " - " + 
                                 ConsoleUtils.formatMoney(t.getMonto()) + 
                                 " (" + t.getFecha().format(DATE_FORMATTER) + ")");
            }
            ConsoleUtils.printLine();
            
        } catch (Exception e) {
            ConsoleUtils.printError("Error al consultar transacciones: " + e.getMessage());
        }
    }
}
