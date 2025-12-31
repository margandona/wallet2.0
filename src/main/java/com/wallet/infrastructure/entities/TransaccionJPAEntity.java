package com.wallet.infrastructure.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una Transacción en la base de datos.
 *
 * Esta clase mapea la tabla 'transacciones' con su relación
 * a la tabla de Cuentas.
 */
@Entity
@Table(name = "transacciones", indexes = {
    @Index(name = "idx_transacciones_cuenta_id", columnList = "cuenta_id"),
    @Index(name = "idx_transacciones_fecha", columnList = "fecha_transaccion")
})
public class TransaccionJPAEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    @Column(name = "monto", nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "saldo_anterior", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoAnterior;

    @Column(name = "saldo_nuevo", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoNuevo;

    @Column(name = "fecha_transaccion", nullable = false)
    private LocalDateTime fechaTransaccion;

    @Column(name = "cuenta_origen_id", length = 36)
    private String cuentaOrigenId;

    @Column(name = "cuenta_destino_id", length = 36)
    private String cuentaDestinoId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relación muchos-a-uno con Cuenta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private CuentaJPAEntity cuenta;

    // Constructores
    public TransaccionJPAEntity() {
        // Constructor vacío requerido por JPA
    }

    public TransaccionJPAEntity(String id, String tipo, BigDecimal monto, String descripcion,
                               BigDecimal saldoAnterior, BigDecimal saldoNuevo,
                               LocalDateTime fechaTransaccion, String cuentaOrigenId,
                               String cuentaDestinoId, LocalDateTime createdAt) {
        this.id = id;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.saldoAnterior = saldoAnterior;
        this.saldoNuevo = saldoNuevo;
        this.fechaTransaccion = fechaTransaccion;
        this.cuentaOrigenId = cuentaOrigenId;
        this.cuentaDestinoId = cuentaDestinoId;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getSaldoNuevo() {
        return saldoNuevo;
    }

    public void setSaldoNuevo(BigDecimal saldoNuevo) {
        this.saldoNuevo = saldoNuevo;
    }

    public LocalDateTime getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(LocalDateTime fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public String getCuentaOrigenId() {
        return cuentaOrigenId;
    }

    public void setCuentaOrigenId(String cuentaOrigenId) {
        this.cuentaOrigenId = cuentaOrigenId;
    }

    public String getCuentaDestinoId() {
        return cuentaDestinoId;
    }

    public void setCuentaDestinoId(String cuentaDestinoId) {
        this.cuentaDestinoId = cuentaDestinoId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public CuentaJPAEntity getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaJPAEntity cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    public String toString() {
        return "TransaccionJPAEntity{" +
                "id='" + id + '\'' +
                ", tipo='" + tipo + '\'' +
                ", monto=" + monto +
                ", descripcion='" + descripcion + '\'' +
                ", fechaTransaccion=" + fechaTransaccion +
                '}';
    }
}
