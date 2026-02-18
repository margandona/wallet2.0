package com.wallet.presentation.web;

import com.wallet.infrastructure.config.JPAConfiguration;
import com.wallet.infrastructure.persistence.DatabaseInitializer;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Initializes JPA and database for the web application lifecycle.
 */
@WebListener
public class AppBootstrapListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        JPAConfiguration.initialize();
        EntityManager entityManager = JPAConfiguration.getEntityManager();
        try {
            DatabaseInitializer.initialize(entityManager);
            if (!DatabaseInitializer.verificarIntegridad(entityManager)) {
                DatabaseInitializer.crearTablasManualmente(entityManager);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JPAConfiguration.close();
    }
}
