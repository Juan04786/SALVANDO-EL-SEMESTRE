package com.maho.crochet.repository;

import com.maho.crochet.entity.Producto;
import com.maho.crochet.entity.Usuario;
import com.maho.crochet.entity.Pedido;
import com.maho.crochet.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// ================================================================
//  REPOSITORIO ESPECÍFICO: Producto
// ================================================================
public class ProductoRepository extends GenericRepositoryImpl<Producto, Integer> {

    public ProductoRepository() {
        super(Producto.class);
    }

    /** Busca productos disponibles por categoría */
    public List<Producto> findByCategoria(Integer idCategoria) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM Producto p WHERE p.categoria.id = :id AND p.disponible = true",
                Producto.class
            ).setParameter("id", idCategoria).getResultList();
        } finally {
            em.close();
        }
    }

    /** Busca productos con stock bajo el umbral dado */
    public List<Producto> findByStockMenorA(int umbral) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM Producto p WHERE p.stock < :umbral AND p.disponible = true",
                Producto.class
            ).setParameter("umbral", umbral).getResultList();
        } finally {
            em.close();
        }
    }

    /** Busca productos por nombre (búsqueda parcial) */
    public List<Producto> buscarPorNombre(String texto) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(:texto)",
                Producto.class
            ).setParameter("texto", "%" + texto + "%").getResultList();
        } finally {
            em.close();
        }
    }

    /** Actualiza el stock de un producto */
    public void actualizarStock(Integer idProducto, int nuevoStock) {
        EntityManager em = JpaUtil.getEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery(
                "UPDATE Producto p SET p.stock = :stock WHERE p.id = :id"
            ).setParameter("stock", nuevoStock).setParameter("id", idProducto).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al actualizar stock: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}

// ================================================================
//  REPOSITORIO ESPECÍFICO: Usuario
// ================================================================
class UsuarioRepository extends GenericRepositoryImpl<Usuario, Integer> {

    public UsuarioRepository() {
        super(Usuario.class);
    }

    /** Busca un usuario por email (para login) */
    public Optional<Usuario> findByEmail(String email) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            var list = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.email = :email AND u.activo = true",
                Usuario.class
            ).setParameter("email", email).getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } finally {
            em.close();
        }
    }

    /** Retorna todos los usuarios activos */
    public List<Usuario> findAllActivos() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT u FROM Usuario u WHERE u.activo = true ORDER BY u.nombre",
                Usuario.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    /** Retorna todos los usuarios con rol específico */
    public List<Usuario> findByRol(String nombreRol) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT u FROM Usuario u WHERE u.rol.nombre = :rol",
                Usuario.class
            ).setParameter("rol", nombreRol).getResultList();
        } finally {
            em.close();
        }
    }
}

// ================================================================
//  REPOSITORIO ESPECÍFICO: Pedido
// ================================================================
class PedidoRepository extends GenericRepositoryImpl<Pedido, Integer> {

    public PedidoRepository() {
        super(Pedido.class);
    }

    /** Retorna pedidos de un usuario específico */
    public List<Pedido> findByUsuario(Integer idUsuario) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM Pedido p WHERE p.usuario.id = :id ORDER BY p.fechaPedido DESC",
                Pedido.class
            ).setParameter("id", idUsuario).getResultList();
        } finally {
            em.close();
        }
    }

    /** Retorna pedidos por estado */
    public List<Pedido> findByEstado(Pedido.EstadoPedido estado) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM Pedido p WHERE p.estado = :estado ORDER BY p.fechaPedido",
                Pedido.class
            ).setParameter("estado", estado).getResultList();
        } finally {
            em.close();
        }
    }

    /** Calcula el total de ventas (pedidos no cancelados) */
    public BigDecimal calcularTotalVentas() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Object result = em.createQuery(
                "SELECT SUM(p.total) FROM Pedido p WHERE p.estado != 'cancelado'"
            ).getSingleResult();
            return result != null ? (BigDecimal) result : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }
}
