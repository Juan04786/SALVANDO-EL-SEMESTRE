package com.maho.crochet.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * JpaUtil — Singleton para gestionar EntityManagerFactory.
 * Garantiza una sola instancia de la fábrica durante toda la aplicación.
 *
 * Patrón: Singleton + Factory
 * Proyecto: Crocheterias Maho | CESDE Bello 2025
 */
public class JpaUtil {

    private static final String PERSISTENCE_UNIT = "CrocheteriasUnit";
    private static EntityManagerFactory emf;

    // Constructor privado — evita instanciación externa
    private JpaUtil() {}

    /**
     * Retorna la única instancia de EntityManagerFactory.
     * La crea en el primer acceso (lazy initialization).
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
        return emf;
    }

    /**
     * Crea y retorna un nuevo EntityManager listo para usar.
     */
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /**
     * Cierra la fábrica al terminar la aplicación.
     * Debe llamarse en el shutdown hook o al salir del menú principal.
     */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("[JpaUtil] Conexión cerrada correctamente.");
        }
    }
}
