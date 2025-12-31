package com.wallet.infrastructure.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa un Usuario en la base de datos.
 *
 * Esta clase es un mapeo directo a la tabla 'usuarios'.
 * Es completamente independiente de la entidad de dominio Usuario,
 * permitiendo cambios en la BD sin afectar la lógica de negocio.
 */
@Entity
@Table(name = "usuarios", indexes = {
    @Index(name = "idx_usuarios_email", columnList = "email", unique = true),
    @Index(name = "idx_usuarios_documento", columnList = "documento", unique = true)
})
public class UsuarioJPAEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "documento", nullable = false, length = 50, unique = true)
    private String documento;

    @Column(name = "tipo_documento", nullable = false, length = 20)
    private String tipoDocumento;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relación uno-a-muchos con Cuentas
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CuentaJPAEntity> cuentas = new ArrayList<>();

    // Constructores
    public UsuarioJPAEntity() {
        // Constructor vacío requerido por JPA
    }

    public UsuarioJPAEntity(String id, String nombre, String apellido, String email, 
                           String documento, String tipoDocumento, boolean activo,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.documento = documento;
        this.tipoDocumento = tipoDocumento;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CuentaJPAEntity> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<CuentaJPAEntity> cuentas) {
        this.cuentas = cuentas;
    }

    public void agregarCuenta(CuentaJPAEntity cuenta) {
        cuentas.add(cuenta);
        cuenta.setUsuario(this);
    }

    public void removerCuenta(CuentaJPAEntity cuenta) {
        cuentas.remove(cuenta);
        cuenta.setUsuario(null);
    }

    @Override
    public String toString() {
        return "UsuarioJPAEntity{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", activo=" + activo +
                '}';
    }
}
