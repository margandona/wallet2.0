-- ============================================
-- SCHEMA.SQL - Wallet Database Structure
-- ============================================
-- Sistema de Billetera Digital
-- Base de datos: SQLite
-- Última actualización: 2024

-- ============================================
-- TABLA: USUARIOS
-- ============================================
CREATE TABLE IF NOT EXISTS usuarios (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    documento VARCHAR(50) NOT NULL UNIQUE,
    tipo_documento VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Índices para búsquedas rápidas
CREATE UNIQUE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE UNIQUE INDEX IF NOT EXISTS idx_usuarios_documento ON usuarios(documento);
CREATE INDEX IF NOT EXISTS idx_usuarios_activo ON usuarios(activo);
CREATE INDEX IF NOT EXISTS idx_usuarios_created_at ON usuarios(created_at);

-- ============================================
-- TABLA: CUENTAS
-- ============================================
CREATE TABLE IF NOT EXISTS cuentas (
    id VARCHAR(36) PRIMARY KEY,
    numero_cuenta VARCHAR(50) NOT NULL UNIQUE,
    usuario_id VARCHAR(36) NOT NULL,
    saldo DECIMAL(19, 2) NOT NULL,
    moneda VARCHAR(3) NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_cuentas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CHECK (saldo >= 0),
    CHECK (LENGTH(numero_cuenta) > 0),
    CHECK (LENGTH(moneda) = 3)
);

-- Índices para búsquedas rápidas
CREATE UNIQUE INDEX IF NOT EXISTS idx_cuentas_numero ON cuentas(numero_cuenta);
CREATE INDEX IF NOT EXISTS idx_cuentas_usuario_id ON cuentas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_cuentas_activa ON cuentas(activa);
CREATE INDEX IF NOT EXISTS idx_cuentas_moneda ON cuentas(moneda);
CREATE INDEX IF NOT EXISTS idx_cuentas_created_at ON cuentas(created_at);

-- ============================================
-- TABLA: TRANSACCIONES
-- ============================================
CREATE TABLE IF NOT EXISTS transacciones (
    id VARCHAR(36) PRIMARY KEY,
    cuenta_id VARCHAR(36) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    monto DECIMAL(19, 2) NOT NULL,
    descripcion VARCHAR(255),
    saldo_anterior DECIMAL(19, 2) NOT NULL,
    saldo_nuevo DECIMAL(19, 2) NOT NULL,
    fecha_transaccion TIMESTAMP NOT NULL,
    cuenta_origen_id VARCHAR(36),
    cuenta_destino_id VARCHAR(36),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_transacciones_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuentas(id) ON DELETE CASCADE,
    CHECK (monto > 0),
    CHECK (saldo_anterior >= 0),
    CHECK (saldo_nuevo >= 0),
    CHECK (tipo IN ('DEPOSITO', 'RETIRO', 'TRANSFERENCIA_ENVIADA', 'TRANSFERENCIA_RECIBIDA'))
);

-- Índices para búsquedas y reportes
CREATE INDEX IF NOT EXISTS idx_transacciones_cuenta_id ON transacciones(cuenta_id);
CREATE INDEX IF NOT EXISTS idx_transacciones_fecha ON transacciones(fecha_transaccion);
CREATE INDEX IF NOT EXISTS idx_transacciones_tipo ON transacciones(tipo);
CREATE INDEX IF NOT EXISTS idx_transacciones_created_at ON transacciones(created_at);
CREATE INDEX IF NOT EXISTS idx_transacciones_cuenta_origen ON transacciones(cuenta_origen_id);
CREATE INDEX IF NOT EXISTS idx_transacciones_cuenta_destino ON transacciones(cuenta_destino_id);

-- ============================================
-- VISTAS ÚTILES (Opcional)
-- ============================================

-- Vista: Resumen de saldos por usuario
CREATE VIEW IF NOT EXISTS v_saldos_por_usuario AS
SELECT 
    u.id,
    u.nombre,
    u.apellido,
    u.email,
    COUNT(c.id) AS total_cuentas,
    SUM(c.saldo) AS saldo_total,
    c.moneda
FROM usuarios u
LEFT JOIN cuentas c ON u.id = c.usuario_id AND c.activa = 1
WHERE u.activo = 1
GROUP BY u.id, u.nombre, u.apellido, u.email, c.moneda;

-- Vista: Últimas transacciones
CREATE VIEW IF NOT EXISTS v_ultimas_transacciones AS
SELECT 
    t.id,
    t.cuenta_id,
    c.numero_cuenta,
    u.nombre AS usuario_nombre,
    u.apellido AS usuario_apellido,
    t.tipo,
    t.monto,
    t.descripcion,
    t.fecha_transaccion,
    t.saldo_nuevo
FROM transacciones t
JOIN cuentas c ON t.cuenta_id = c.id
JOIN usuarios u ON c.usuario_id = u.id
ORDER BY t.fecha_transaccion DESC
LIMIT 100;

-- ============================================
-- DATOS INICIALES (OPCIONAL - DESCOMENTAR SI ES NECESARIO)
-- ============================================
/*
-- Usuario de prueba
INSERT INTO usuarios (id, nombre, apellido, email, documento, tipo_documento, activo, created_at, updated_at)
VALUES (
    '550e8400-e29b-41d4-a716-446655440000',
    'Juan',
    'Pérez',
    'juan.perez@example.com',
    '12345678',
    'DNI',
    1,
    datetime('now'),
    datetime('now')
);

-- Cuenta de prueba
INSERT INTO cuentas (id, numero_cuenta, usuario_id, saldo, moneda, activa, created_at, updated_at)
VALUES (
    '550e8400-e29b-41d4-a716-446655440001',
    '0000000001',
    '550e8400-e29b-41d4-a716-446655440000',
    1000.00,
    'PEN',
    1,
    datetime('now'),
    datetime('now')
);
*/

-- ============================================
-- FIN DEL SCHEMA
-- ============================================
