package com.maho.crochet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

// ================================================================
//  ENTIDAD: Rol
// ================================================================
// (archivo separado en la práctica; aquí agrupados para claridad)

@Entity
@Table(name = "roles")
class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String nombre;

    @Column(length = 100)
    private String descripcion;

    @OneToMany(mappedBy = "rol", fetch = FetchType.LAZY)
    private List<Usuario> usuarios;

    // ── Constructores ──────────────────────────────────────────
    public Rol() {}
    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // ── Getters y Setters ──────────────────────────────────────
    public Integer getId()               { return id; }
    public void setId(Integer id)        { this.id = id; }

    public String getNombre()            { return nombre; }
    public void setNombre(String n)      { this.nombre = n; }

    public String getDescripcion()       { return descripcion; }
    public void setDescripcion(String d) { this.descripcion = d; }

    @Override
    public String toString() {
        return "Rol{id=" + id + ", nombre='" + nombre + "'}";
    }
}
