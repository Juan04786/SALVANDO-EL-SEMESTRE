package com.maho.crochet.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// ================================================================
//  ENTIDAD: Categoria
// ================================================================
@Entity
@Table(name = "categorias")
class Categoria {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 60)
    private String nombre;

    @Column(length = 200)
    private String descripcion;

    @Column(length = 10)
    private String emoji;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<Producto> productos;

    public Categoria() {}
    public Categoria(String nombre, String descripcion, String emoji) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.emoji = emoji;
    }

    public Integer getId()               { return id; }
    public void setId(Integer id)        { this.id = id; }
    public String getNombre()            { return nombre; }
    public void setNombre(String n)      { this.nombre = n; }
    public String getDescripcion()       { return descripcion; }
    public void setDescripcion(String d) { this.descripcion = d; }
    public String getEmoji()             { return emoji; }
    public void setEmoji(String e)       { this.emoji = e; }
    public List<Producto> getProductos() { return productos; }

    @Override
    public String toString() {
        return emoji + " " + nombre;
    }
}

// ================================================================
//  ENTIDAD: Producto
// ================================================================
@Entity
@Table(name = "productos")
class Producto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(length = 10)
    private String emoji;

    @Column(nullable = false)
    private Boolean disponible = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<DetallePedido> detalles;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<Resena> resenas;

    public Producto() {}
    public Producto(String nombre, String descripcion, BigDecimal precio,
                    Integer stock, String emoji, Categoria categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.emoji = emoji;
        this.categoria = categoria;
        this.disponible = true;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Integer getId()                       { return id; }
    public void setId(Integer id)                { this.id = id; }
    public String getNombre()                    { return nombre; }
    public void setNombre(String n)              { this.nombre = n; }
    public String getDescripcion()               { return descripcion; }
    public void setDescripcion(String d)         { this.descripcion = d; }
    public BigDecimal getPrecio()                { return precio; }
    public void setPrecio(BigDecimal p)          { this.precio = p; }
    public Integer getStock()                    { return stock; }
    public void setStock(Integer s)              { this.stock = s; }
    public String getEmoji()                     { return emoji; }
    public void setEmoji(String e)               { this.emoji = e; }
    public Boolean getDisponible()               { return disponible; }
    public void setDisponible(Boolean d)         { this.disponible = d; }
    public Categoria getCategoria()              { return categoria; }
    public void setCategoria(Categoria c)        { this.categoria = c; }
    public LocalDateTime getFechaCreacion()      { return fechaCreacion; }

    @Override
    public String toString() {
        return String.format("%s %s | Precio: $%,.0f | Stock: %d",
            emoji, nombre, precio, stock);
    }
}

// ================================================================
//  ENTIDAD: Pedido
// ================================================================
@Entity
@Table(name = "pedidos")
class Pedido {

    public enum EstadoPedido {
        pendiente, en_proceso, enviado, entregado, cancelado
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado = EstadoPedido.pendiente;

    @Column(length = 250)
    private String direccion;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "fecha_pedido", updatable = false)
    private LocalDateTime fechaPedido = LocalDateTime.now();

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetallePedido> detalles;

    public Pedido() {}
    public Pedido(Usuario usuario, BigDecimal total, String direccion) {
        this.usuario   = usuario;
        this.total     = total;
        this.direccion = direccion;
        this.estado    = EstadoPedido.pendiente;
        this.fechaPedido = LocalDateTime.now();
    }

    public Integer getId()                        { return id; }
    public void setId(Integer id)                 { this.id = id; }
    public Usuario getUsuario()                   { return usuario; }
    public void setUsuario(Usuario u)             { this.usuario = u; }
    public BigDecimal getTotal()                  { return total; }
    public void setTotal(BigDecimal t)            { this.total = t; }
    public EstadoPedido getEstado()               { return estado; }
    public void setEstado(EstadoPedido e)         { this.estado = e; }
    public String getDireccion()                  { return direccion; }
    public void setDireccion(String d)            { this.direccion = d; }
    public String getNotas()                      { return notas; }
    public void setNotas(String n)                { this.notas = n; }
    public LocalDateTime getFechaPedido()         { return fechaPedido; }
    public LocalDateTime getFechaEntrega()        { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime f)  { this.fechaEntrega = f; }
    public List<DetallePedido> getDetalles()      { return detalles; }

    @Override
    public String toString() {
        return String.format("Pedido{id=%d, cliente='%s', total=$%,.0f, estado='%s'}",
            id, usuario != null ? usuario.getNombre() : "—", total, estado);
    }
}

// ================================================================
//  ENTIDAD: DetallePedido
// ================================================================
@Entity
@Table(name = "detalle_pedidos")
class DetallePedido {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad = 1;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    public DetallePedido() {}
    public DetallePedido(Pedido pedido, Producto producto, Integer cantidad) {
        this.pedido         = pedido;
        this.producto       = producto;
        this.cantidad       = cantidad;
        this.precioUnitario = producto.getPrecio();
    }

    public Integer getId()                        { return id; }
    public Pedido getPedido()                     { return pedido; }
    public void setPedido(Pedido p)               { this.pedido = p; }
    public Producto getProducto()                 { return producto; }
    public void setProducto(Producto p)           { this.producto = p; }
    public Integer getCantidad()                  { return cantidad; }
    public void setCantidad(Integer c)            { this.cantidad = c; }
    public BigDecimal getPrecioUnitario()         { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal p)   { this.precioUnitario = p; }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    @Override
    public String toString() {
        return String.format("  - %s x%d = $%,.0f",
            producto.getNombre(), cantidad, getSubtotal());
    }
}

// ================================================================
//  ENTIDAD: Resena
// ================================================================
@Entity
@Table(name = "resenas")
class Resena {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Byte estrellas;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(updatable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    public Resena() {}
    public Resena(Usuario usuario, Producto producto, Byte estrellas, String comentario) {
        this.usuario    = usuario;
        this.producto   = producto;
        this.estrellas  = estrellas;
        this.comentario = comentario;
        this.fecha      = LocalDateTime.now();
    }

    public Integer getId()                 { return id; }
    public Usuario getUsuario()            { return usuario; }
    public void setUsuario(Usuario u)      { this.usuario = u; }
    public Producto getProducto()          { return producto; }
    public void setProducto(Producto p)    { this.producto = p; }
    public Byte getEstrellas()             { return estrellas; }
    public void setEstrellas(Byte e)       { this.estrellas = e; }
    public String getComentario()          { return comentario; }
    public void setComentario(String c)    { this.comentario = c; }
    public LocalDateTime getFecha()        { return fecha; }

    public String getEstrellaStr() {
        return "⭐".repeat(estrellas != null ? estrellas : 0);
    }

    @Override
    public String toString() {
        return String.format("Reseña{usuario='%s', producto='%s', %s}",
            usuario.getNombre(), producto.getNombre(), getEstrellaStr());
    }
}
