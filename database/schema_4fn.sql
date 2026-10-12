-- ============================================================================
-- PLATAFORMA DE RENTA DE HERRAMIENTAS Y EQUIPOS DE CONSTRUCCIÓN
-- SCRIPT DDL: CREACIÓN DE BASE DE DATOS Y TABLAS (NORMALIZACIÓN ESTRICTA EN 4FN)
-- ============================================================================

DROP DATABASE IF EXISTS renta_herramientas_db;
CREATE DATABASE renta_herramientas_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE renta_herramientas_db;

-- ============================================================================
-- 1. TABLAS DEL MÓDULO DE IDENTIDAD Y USUARIOS (Base Compartida / DEV 1)
-- ============================================================================

CREATE TABLE roles (
    id_rol BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE usuarios (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(120) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    direccion VARCHAR(180) NOT NULL,
    tipo_documento ENUM('CC', 'NIT', 'CE', 'PASAPORTE') NOT NULL,
    numero_documento VARCHAR(30) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 4FN: Descomposición de la dependencia multivaluada id_usuario ->-> id_rol
CREATE TABLE usuarios_roles (
    id_usuario BIGINT NOT NULL,
    id_rol BIGINT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_usuariorol_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_usuariorol_rol FOREIGN KEY (id_rol) REFERENCES roles (id_rol) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================================
-- 2. TABLAS DEL MÓDULO DE HERRAMIENTAS E INVENTARIO (DEV 1)
-- ============================================================================

CREATE TABLE categorias (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE herramientas (
    id_herramienta BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_proveedor BIGINT NOT NULL,
    id_categoria BIGINT NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    descripcion TEXT NOT NULL,
    marca VARCHAR(60) NOT NULL,
    modelo VARCHAR(60) NOT NULL,
    numero_serie VARCHAR(80) NOT NULL UNIQUE,
    estado_operativo ENUM('DISPONIBLE', 'ALQUILADA', 'EN_MANTENIMIENTO', 'FUERA_DE_SERVICIO') NOT NULL DEFAULT 'DISPONIBLE',
    tarifa_diaria DECIMAL(12, 2) NOT NULL,
    valor_reposicion DECIMAL(12, 2) NOT NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_herramienta_proveedor FOREIGN KEY (id_proveedor) REFERENCES usuarios (id_usuario) ON DELETE RESTRICT,
    CONSTRAINT fk_herramienta_categoria FOREIGN KEY (id_categoria) REFERENCES categorias (id_categoria) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================================
-- 3. TABLAS DEL MÓDULO DE RESERVAS Y ALQUILERES (DEV 2)
-- ============================================================================

CREATE TABLE reservas (
    id_reserva BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_reserva VARCHAR(30) NOT NULL UNIQUE,
    id_cliente BIGINT NOT NULL,
    fecha_solicitud TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado ENUM('SOLICITADA', 'APROBADA', 'RECHAZADA', 'EN_CURSO', 'FINALIZADA', 'CANCELADA') NOT NULL DEFAULT 'SOLICITADA',
    monto_total_estimado DECIMAL(12, 2) NOT NULL,
    deposito_garantia DECIMAL(12, 2) NOT NULL,
    notas_cliente VARCHAR(255),
    motivo_rechazo VARCHAR(255),
    CONSTRAINT fk_reserva_cliente FOREIGN KEY (id_cliente) REFERENCES usuarios (id_usuario) ON DELETE RESTRICT,
    CONSTRAINT chk_rango_fechas CHECK (fecha_fin >= fecha_inicio)
) ENGINE=InnoDB;

CREATE INDEX idx_reserva_fechas ON reservas (fecha_inicio, fecha_fin, estado);

-- 4FN: Desacopla las herramientas reservadas de cualquier otra característica multivaluada
CREATE TABLE detalles_reserva (
    id_detalle_reserva BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_reserva BIGINT NOT NULL,
    id_herramienta BIGINT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario_dia DECIMAL(12, 2) NOT NULL,
    dias_alquiler INT NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_detallereserva_reserva FOREIGN KEY (id_reserva) REFERENCES reservas (id_reserva) ON DELETE CASCADE,
    CONSTRAINT fk_detallereserva_herramienta FOREIGN KEY (id_herramienta) REFERENCES herramientas (id_herramienta) ON DELETE RESTRICT,
    CONSTRAINT uq_reserva_herramienta UNIQUE (id_reserva, id_herramienta)
) ENGINE=InnoDB;

-- 4FN: Desacopla la auditoría temporal de transiciones de estado de la reserva
CREATE TABLE auditoria_estados_reserva (
    id_auditoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_reserva BIGINT NOT NULL,
    estado_anterior VARCHAR(30),
    estado_nuevo VARCHAR(30) NOT NULL,
    fecha_cambio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cambiado_por BIGINT NOT NULL,
    observacion VARCHAR(255),
    CONSTRAINT fk_auditoria_reserva FOREIGN KEY (id_reserva) REFERENCES reservas (id_reserva) ON DELETE CASCADE,
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (cambiado_por) REFERENCES usuarios (id_usuario) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================================
-- 4. TABLAS DEL MÓDULO DE PAGOS Y FACTURACIÓN (DEV 2)
-- ============================================================================

CREATE TABLE metodos_pago (
    id_metodo_pago BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE pagos (
    id_pago BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_transaccion VARCHAR(60) NOT NULL UNIQUE,
    id_reserva BIGINT NOT NULL,
    id_metodo_pago BIGINT NOT NULL,
    monto DECIMAL(12, 2) NOT NULL,
    tipo_pago ENUM('ANTICIPO', 'TOTALIDAD', 'DEPOSITO_GARANTIA', 'MULTA_DANO', 'RECARGO_MORA') NOT NULL,
    estado_pago ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO', 'REEMBOLSADO') NOT NULL DEFAULT 'PENDIENTE',
    fecha_pago TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    referencia_pasarela VARCHAR(120),
    CONSTRAINT fk_pago_reserva FOREIGN KEY (id_reserva) REFERENCES reservas (id_reserva) ON DELETE RESTRICT,
    CONSTRAINT fk_pago_metodo FOREIGN KEY (id_metodo_pago) REFERENCES metodos_pago (id_metodo_pago) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE facturas (
    id_factura BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_factura VARCHAR(40) NOT NULL UNIQUE,
    id_pago BIGINT NOT NULL UNIQUE,
    id_cliente BIGINT NOT NULL,
    fecha_emision TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(12, 2) NOT NULL,
    iva DECIMAL(12, 2) NOT NULL,
    total DECIMAL(12, 2) NOT NULL,
    estado_factura ENUM('EMITIDA', 'ANULADA', 'PAGADA') NOT NULL DEFAULT 'EMITIDA',
    url_comprobante_pdf VARCHAR(255),
    CONSTRAINT fk_factura_pago FOREIGN KEY (id_pago) REFERENCES pagos (id_pago) ON DELETE RESTRICT,
    CONSTRAINT fk_factura_cliente FOREIGN KEY (id_cliente) REFERENCES usuarios (id_usuario) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE detalles_factura (
    id_detalle_factura BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_factura BIGINT NOT NULL,
    descripcion_item VARCHAR(200) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(12, 2) NOT NULL,
    subtotal_linea DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_detallefactura_factura FOREIGN KEY (id_factura) REFERENCES facturas (id_factura) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================================
-- 5. TABLAS DEL MÓDULO DE DEVOLUCIONES, DAÑOS Y CARGOS (DEV 2)
-- ============================================================================

CREATE TABLE devoluciones (
    id_devolucion BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_reserva BIGINT NOT NULL UNIQUE,
    fecha_devolucion_pactada DATE NOT NULL,
    fecha_devolucion_real DATE NOT NULL,
    recibido_por BIGINT NOT NULL,
    estado_equipo_general ENUM('BUEN_ESTADO', 'DANO_LEVE', 'DANO_GRAVE', 'PERDIDA_TOTAL') NOT NULL DEFAULT 'BUEN_ESTADO',
    dias_retraso INT NOT NULL DEFAULT 0,
    observaciones TEXT,
    CONSTRAINT fk_devolucion_reserva FOREIGN KEY (id_reserva) REFERENCES reservas (id_reserva) ON DELETE RESTRICT,
    CONSTRAINT fk_devolucion_usuario FOREIGN KEY (recibido_por) REFERENCES usuarios (id_usuario) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE reportes_dano (
    id_reporte_dano BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_devolucion BIGINT NOT NULL,
    id_herramienta BIGINT NOT NULL,
    tipo_dano ENUM('ESTETICO', 'MECANICO', 'ELECTRICO', 'FALTANTE_PIEZA', 'IRREPARABLE') NOT NULL,
    descripcion_dano TEXT NOT NULL,
    costo_estimado_reparacion DECIMAL(12, 2) NOT NULL,
    responsable_asume ENUM('CLIENTE', 'PROVEEDOR', 'POLIZA_GARANTIA') NOT NULL,
    fecha_reporte TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reporte_devolucion FOREIGN KEY (id_devolucion) REFERENCES devoluciones (id_devolucion) ON DELETE CASCADE,
    CONSTRAINT fk_reporte_herramienta FOREIGN KEY (id_herramienta) REFERENCES herramientas (id_herramienta) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 4FN: Desacopla la lista multivaluada de fotos de evidencia de los atributos escalares del daño
CREATE TABLE evidencias_dano (
    id_evidencia BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_reporte_dano BIGINT NOT NULL,
    url_foto VARCHAR(255) NOT NULL,
    descripcion_foto VARCHAR(200),
    fecha_subida TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evidencia_reporte FOREIGN KEY (id_reporte_dano) REFERENCES reportes_dano (id_reporte_dano) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE cargos_adicionales (
    id_cargo BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_devolucion BIGINT NOT NULL,
    tipo_cargo ENUM('MORA_RETRASO', 'REPARACION_DANO', 'LIMPIEZA_PROFUNDA', 'REPOSICION_EQUIPO') NOT NULL,
    monto DECIMAL(12, 2) NOT NULL,
    pagado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_cargo TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion VARCHAR(255) NOT NULL,
    CONSTRAINT fk_cargo_devolucion FOREIGN KEY (id_devolucion) REFERENCES devoluciones (id_devolucion) ON DELETE CASCADE
) ENGINE=InnoDB;
