package com.wallet.presentation.menus;

import com.wallet.presentation.controllers.*;
import com.wallet.presentation.utils.ConsoleUtils;
import com.wallet.infrastructure.services.*;

/**
 * Menú principal de la aplicación.
 * Punto de entrada de la interfaz de usuario.
 * 
 * @author Wallet Team
 * @version 1.0.0
 */
public class MenuPrincipal {
    
    private final UsuarioController usuarioController;
    private final CuentaController cuentaController;
    private final TransaccionController transaccionController;
    
    private final MenuUsuarios menuUsuarios;
    private final MenuCuentas menuCuentas;
    private final MenuTransacciones menuTransacciones;
    private final MenuDivisas menuDivisas;
    
    private boolean salir = false;
    
    public MenuPrincipal() {
        // Inicializar servicios
        UsuarioService usuarioService = new UsuarioService();
        CuentaService cuentaService = new CuentaService();
        TransaccionService transaccionService = new TransaccionService();
        
        // Inicializar controladores
        this.usuarioController = new UsuarioController(usuarioService);
        this.cuentaController = new CuentaController(cuentaService);
        this.transaccionController = new TransaccionController(transaccionService);
        
        // Inicializar menús
        this.menuUsuarios = new MenuUsuarios(usuarioController);
        this.menuCuentas = new MenuCuentas(cuentaController);
        this.menuTransacciones = new MenuTransacciones(transaccionController);
        this.menuDivisas = new MenuDivisas(ConsoleUtils.getScanner());
    }
    
    /**
     * Inicia el menú principal.
     */
    public void iniciar() {
        mostrarBienvenida();
        
        while (!salir) {
            mostrarMenu();
            procesarOpcion();
        }
        
        mostrarDespedida();
    }
    
    private void mostrarBienvenida() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printSeparator();
        System.out.println("  WALLET - Billetera Digital");
        System.out.println("  Sistema de Gestion v1.0.0");
        ConsoleUtils.printSeparator();
        ConsoleUtils.printLine();
        ConsoleUtils.printInfo("Bienvenido al sistema de gestion de billetera digital");
        ConsoleUtils.pause();
    }
    
    private void mostrarMenu() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("MENU PRINCIPAL");
        
        System.out.println("1. Gestion de Usuarios");
        System.out.println("2. Gestion de Cuentas");
        System.out.println("3. Transacciones");
        System.out.println("4. Consultas");
        System.out.println("5. Conversor de Divisas");
        System.out.println("0. Salir");
        ConsoleUtils.printLine();
    }
    
    private void procesarOpcion() {
        int opcion = ConsoleUtils.readIntInRange("Seleccione una opcion: ", 0, 5);
        ConsoleUtils.printLine();
        
        switch (opcion) {
            case 1:
                menuUsuarios.mostrar();
                break;
            case 2:
                menuCuentas.mostrar();
                break;
            case 3:
                menuTransacciones.mostrar();
                break;
            case 4:
                mostrarMenuConsultas();
                break;
            case 5:
                menuDivisas.mostrar();
                break;
            case 0:
                salir = true;
                break;
            default:
                ConsoleUtils.printError("Opcion no valida.");
        }
    }
    
    private void mostrarMenuConsultas() {
        boolean volver = false;
        
        while (!volver) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("MENU DE CONSULTAS");
            
            System.out.println("1. Consultar Saldo");
            System.out.println("2. Ver Historial de Transacciones");
            System.out.println("3. Ver Ultimas Transacciones");
            System.out.println("4. Buscar Usuario por Email");
            System.out.println("5. Buscar Cuenta por Numero");
            System.out.println("0. Volver");
            ConsoleUtils.printLine();
            
            int opcion = ConsoleUtils.readIntInRange("Seleccione una opcion: ", 0, 5);
            ConsoleUtils.printLine();
            
            switch (opcion) {
                case 1:
                    cuentaController.consultarSaldo();
                    ConsoleUtils.pause();
                    break;
                case 2:
                    transaccionController.consultarHistorial();
                    ConsoleUtils.pause();
                    break;
                case 3:
                    transaccionController.consultarUltimas();
                    ConsoleUtils.pause();
                    break;
                case 4:
                    usuarioController.buscarPorEmail();
                    ConsoleUtils.pause();
                    break;
                case 5:
                    cuentaController.buscarPorNumero();
                    ConsoleUtils.pause();
                    break;
                case 0:
                    volver = true;
                    break;
            }
        }
    }
    
    private void mostrarDespedida() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printSeparator();
        ConsoleUtils.printInfo("Gracias por usar Wallet!");
        ConsoleUtils.printInfo("Hasta pronto.");
        ConsoleUtils.printSeparator();
        ConsoleUtils.printLine();
    }
}
