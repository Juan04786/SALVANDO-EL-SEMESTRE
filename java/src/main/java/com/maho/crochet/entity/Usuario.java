package com.maho.crochet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad Usuario — representa clientes y administradores.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 20)
    private String telefono;

    @Column(length = 80)
    private String ciudad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pedido> pedidos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Resena> resenas;

    // ── Constructores ──────────────────────────────────────────
    public Usuario() {}

    public Usuario(String nombre, String email, String passwordHash,
                   String telefono, String ciudad, Rol rol) {
        this.nombre       = nombre;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.telefono     = telefono;
        this.ciudad       = ciudad;
        this.rol          = rol;
        this.activo       = true;
        this.fechaRegistro = LocalDateTime.now();
    }

    // ── Getters y Setters ──────────────────────────────────────
    public Integer getId()                   { return id; }
    public void setId(Integer id)            { this.id = id; }

    public String getNombre()                { return nombre; }
    public void setNombre(String nombre)     { this.nombre = nombre; }

    public String getEmail()                 { return email; }
    public void setEmail(String email)       { this.email = email; }

    public String getPasswordHash()          { return passwordHash; }
    public void setPasswordHash(String p)    { this.passwordHash = p; }

    public String getTelefono()              { return telefono; }
    public void setTelefono(String t)        { this.telefono = t; }

    public String getCiudad()                { return ciudad; }
    public void setCiudad(String c)          { this.ciudad = c; }

    public Rol getRol()                      { return rol; }
    public void setRol(Rol rol)              { this.rol = rol; }

    public Boolean getActivo()               { return activo; }
    public void setActivo(Boolean activo)    { this.activo = activo; }

    public LocalDateTime getFechaRegistro()  { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime f) { this.fechaRegistro = f; }

    public List<Pedido> getPedidos()         { return pedidos; }
    public List<Resena> getResenas()         { return resenas; }

    @Override
    public String toString() {
        return String.format("Usuario{id=%d, nombre='%s', email='%s', rol='%s', activo=%s}",
            id, nombre, email, rol != null ? rol.getNombre() : "—", activo);
    }
}
