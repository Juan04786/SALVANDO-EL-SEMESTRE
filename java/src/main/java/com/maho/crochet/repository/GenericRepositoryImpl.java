package com.maho.crochet.repository;

import com.maho.crochet.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

/**
 * GenericRepositoryImpl<T, ID>
 * Implementación genérica reutilizable de las operaciones CRUD.
 * Todas las entidades del proyecto la extienden sin reescribir CRUD.
 *
 * Uso de genéricos: permite tipado fuerte en compile-time.
 * Transaccionalidad: begin / commit / rollback en cada operación.
 */
public abstract class GenericRepositoryImpl<T, ID> implements Repository<T, ID> {

    // Clase de la entidad concreta (p.ej. Producto.class)
    protected final Class<T> entityClass;

    protected GenericRepositoryImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    // ── CREATE ────────────────────────────────────────────────
    @Override
    public T save(T entity) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al guardar: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ── READ BY ID ────────────────────────────────────────────
    @Override
    public Optional<T> findById(ID id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            T entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }

    // ── READ ALL ──────────────────────────────────────────────
    @Override
    public List<T> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(jpql, entityClass).getResultList();
        } finally {
            em.close();
        }
    }

    // ── UPDATE ────────────────────────────────────────────────
    @Override
    public T update(T entity) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T merged = em.merge(entity);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al actualizar: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ── DELETE ────────────────────────────────────────────────
    @Override
    public void deleteById(ID id) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entity = em.find(entityClass, id);
            if (entity == null) {
                throw new RuntimeException("Entidad no encontrada con id: " + id);
            }
            em.remove(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al eliminar: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ── EXISTS ────────────────────────────────────────────────
    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }
}
