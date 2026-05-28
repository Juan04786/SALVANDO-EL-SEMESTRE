package com.maho.crochet.repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository<T, ID> — Interfaz genérica con operaciones CRUD.
 * Separa la definición del contrato de su implementación técnica.
 *
 * T  = tipo de entidad (Producto, Usuario, Pedido…)
 * ID = tipo de la clave primaria (Integer, Long…)
 */
public interface Repository<T, ID> {

    /** Guarda una entidad nueva en la base de datos. */
    T save(T entity);

    /** Busca una entidad por su ID. Retorna Optional para evitar NPE. */
    Optional<T> findById(ID id);

    /** Retorna todas las entidades de este tipo. */
    List<T> findAll();

    /** Actualiza una entidad existente. */
    T update(T entity);

    /** Elimina una entidad por ID. */
    void deleteById(ID id);

    /** Verifica si existe una entidad con el ID dado. */
    boolean existsById(ID id);
}
