package com.wallet.infrastructure.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa una Cuenta en la base de datos.
 *
 * Esta clase mapea la tabla 'cuentas' con sus relaciones
 * a Usuarios y Transacciones.
 */
@Entity
@Table(name = "cuentas", indexes = {
    @Index(name = "idx_cuentas_numero", columnList = "numero_cuenta", unique = true),
    @Index(name = "idx_cuentas_usuario_id", columnList = "usuario_id")
})
public class CuentaJPAEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "numero_cuenta", nullable = false, length = 50, unique = true)
    private String numeroCuenta;

    @Column(name = "saldo", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;

    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda;

    @Column(name = "activa", nullable = false)
    private boolean activa;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relación muchos-a-uno con Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioJPAEntity usuario;

    // Relación uno-a-muchos con Transacciones
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TransaccionJPAEntity> transacciones = new ArrayList<>();

    // Constructores
    public CuentaJPAEntity() {
        // Constructor vacío requerido por JPA
    }

    public CuentaJPAEntity(String id, String numeroCuenta, String moneda, 
                          BigDecimal saldo, boolean activa,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.moneda = moneda;
        this.saldo = saldo;
        this.activa = activa;
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

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
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

    public UsuarioJPAEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioJPAEntity usuario) {
        this.usuario = usuario;
    }

    public List<TransaccionJPAEntity> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(List<TransaccionJPAEntity> transacciones) {
        this.transacciones = transacciones;
    }

    public void agregarTransaccion(TransaccionJPAEntity transaccion) {
        transacciones.add(transaccion);
        transaccion.setCuenta(this);
    }

    public void removerTransaccion(TransaccionJPAEntity transaccion) {
        transacciones.remove(transaccion);
        transaccion.setCuenta(null);
    }

    @Override
    public String toString() {
        return "CuentaJPAEntity{" +
                "id='" + id + '\'' +
                ", numeroCuenta='" + numeroCuenta + '\'' +
                ", saldo=" + saldo +
                ", moneda='" + moneda + '\'' +
                ", activa=" + activa +
                '}';
    }
}
