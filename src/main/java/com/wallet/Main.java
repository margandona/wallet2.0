package com.wallet;

import com.wallet.infrastructure.config.JPAConfiguration;
import com.wallet.infrastructure.persistence.DatabaseInitializer;
import com.wallet.presentation.menus.MenuPrincipal;
import jakarta.persistence.EntityManager;

/**
 * Clase principal de la aplicación Wallet.
 * Punto de entrada del sistema de billetera digital.
 * 
 * Responsabilidades:
 * - Inicializar configuración JPA
 * - Inicializar la base de datos
 * - Iniciar la interfaz de usuario
 * - Limpiar recursos al finalizar
 * 
 * @author Wallet Team
 * @version 1.0.0
 */
public class Main {
    
    /**
     * Método principal que inicia la aplicación.
     * 
     * Flujo de inicialización:
     * 1. Configurar JPA/Hibernate
     * 2. Inicializar base de datos (crear tablas si no existen)
     * 3. Iniciar menú principal
     * 4. Limpiar recursos al finalizar
     * 
     * @param args argumentos de línea de comandos (no utilizados actualmente)
     */
    public static void main(String[] args) {
        try {
            // ============================================
            // 1. INICIALIZAR JPA
            // ============================================
            System.out.println("════════════════════════════════════════");
            System.out.println("💳 WALLET - SISTEMA DE BILLETERA DIGITAL");
            System.out.println("════════════════════════════════════════");
            System.out.println();
            
            System.out.println("⚙️ Inicializando aplicación...");
            JPAConfiguration.initialize();
            System.out.println();

            // ============================================
            // 2. INICIALIZAR BASE DE DATOS
            // ============================================
            System.out.println("📦 Inicializando base de datos...");
            EntityManager entityManager = JPAConfiguration.getEntityManager();
            
            try {
                DatabaseInitializer.initialize(entityManager);
                
                // Verificar integridad del schema
                if (!DatabaseInitializer.verificarIntegridad(entityManager)) {
                    System.err.println("⚠️ El schema no pasó la verificación de integridad");
                    System.err.println("   Intentando crear tablas manualmente...");
                    DatabaseInitializer.crearTablasManualmente(entityManager);
                }
            } finally {
                if (entityManager.isOpen()) {
                    entityManager.close();
                }
            }
            
            System.out.println();

            // ============================================
            // 3. INICIAR APLICACIÓN
            // ============================================
            System.out.println("✅ Inicialización completada. Iniciando interfaz...");
            System.out.println();
            
            MenuPrincipal menu = new MenuPrincipal();
            menu.iniciar();

        } catch (Exception e) {
            System.err.println("════════════════════════════════════════");
            System.err.println("❌ ERROR FATAL");
            System.err.println("════════════════════════════════════════");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println();
            System.err.println("Stack trace:");
            e.printStackTrace();
            System.exit(1);
        } finally {
            // ============================================
            // 4. LIMPIAR RECURSOS
            // ============================================
            try {
                System.out.println();
                System.out.println("🔒 Cerrando recursos...");
                JPAConfiguration.close();
                System.out.println("✅ Adiós!");
            } catch (Exception e) {
                System.err.println("❌ Error al cerrar recursos: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

