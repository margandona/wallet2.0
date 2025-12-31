package com.wallet.infrastructure.factories;

import com.wallet.domain.repositories.IUsuarioRepository;
import com.wallet.domain.repositories.ICuentaRepository;
import com.wallet.domain.repositories.ITransaccionRepository;
import com.wallet.infrastructure.repositories.UsuarioJPARepository;
import com.wallet.infrastructure.repositories.CuentaJPARepository;
import com.wallet.infrastructure.repositories.TransaccionJPARepository;

/**
 * Fábrica de Repositorios (Factory Pattern).
 * 
 * Centraliza la creación de instancias de repositorios.
 * Utiliza Singleton para garantizar una única instancia de cada repositorio.
 * 
 * Ventajas:
 * - Desacoplamiento: Los clientes no conocen las implementaciones concretas
 * - Reutilización: Una única instancia compartida
 * - Flexibilidad: Fácil cambio de implementaciones
 */
public class RepositoryFactory {
    
    // Instancias únicas (Singleton) - USANDO JPA PARA PERSISTENCIA
    private static final IUsuarioRepository usuarioRepository = new UsuarioJPARepository();
    private static final ICuentaRepository cuentaRepository = new CuentaJPARepository();
    private static final ITransaccionRepository transaccionRepository = new TransaccionJPARepository();
    
    // Constructor privado para evitar instanciación
    private RepositoryFactory() {
        throw new AssertionError("No se debe instanciar RepositoryFactory");
    }
    
    /**
     * Obtiene la instancia del repositorio de usuarios.
     */
    public static IUsuarioRepository getUsuarioRepository() {
        return usuarioRepository;
    }
    
    /**
     * Obtiene la instancia del repositorio de cuentas.
     */
    public static ICuentaRepository getCuentaRepository() {
        return cuentaRepository;
    }
    
    /**
     * Obtiene la instancia del repositorio de transacciones.
     */
    public static ITransaccionRepository getTransaccionRepository() {
        return transaccionRepository;
    }
    
    /**
     * Limpia todos los repositorios (útil para testing).
     * NOT APPLICABLE para JPA repositories.
     */
    public static void limpiarTodos() {
        // NO-OP: JPA repositories manejan datos en BD, no en memoria
    }
}
