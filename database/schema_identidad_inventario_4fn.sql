-- ============================================================================
-- PLATAFORMA DE RENTA DE HERRAMIENTAS Y EQUIPOS DE CONSTRUCCION
--
-- SCRIPT DDL - MODULOS DE IDENTIDAD, CATALOGO E INVENTARIO
-- Responsable: Camilo Garcia
--
-- IMPORTANTE: este script es COMPLEMENTARIO y debe ejecutarse DESPUES de
-- database/schema_4fn.sql. No vuelve a crear la base de datos ni toca las
-- tablas que ya existen; unicamente agrega las tablas que faltaban para
-- completar la normalizacion en 4FN de los modulos a mi cargo.
--
-- Orden de ejecucion recomendado:
--   1) database/schema_4fn.sql
--   2) database/schema_identidad_inventario_4fn.sql   <-- este archivo
--   3) database/data_20_records.sql
--   4) database/data_identidad_inventario.sql
-- ============================================================================

USE renta_herramientas_db;

-- ============================================================================
-- 1. PERMISOS Y SU RELACION CON LOS ROLES
--
-- 4FN: un rol tiene muchos permisos y un permiso pertenece a muchos roles.
-- Guardar los permisos como una lista dentro de la fila del rol
-- (permisos = 'CREAR,EDITAR,BORRAR') romperia incluso la 1FN. La tabla puente
-- roles_permisos descompone esa dependencia multivaluada.
-- ============================================================================

CREATE TABLE IF NOT EXISTS permisos (
    id_permiso  BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo      VARCHAR(60)  NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS roles_permisos (
    id_rol     BIGINT NOT NULL,
    id_permiso BIGINT NOT NULL,
    PRIMARY KEY (id_rol, id_permiso),
    CONSTRAINT fk_rolpermiso_rol
        FOREIGN KEY (id_rol) REFERENCES roles (id_rol) ON DELETE CASCADE,
    CONSTRAINT fk_rolpermiso_permiso
        FOREIGN KEY (id_permiso) REFERENCES permisos (id_permiso) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================================
-- 2. PERFILES DIFERENCIADOS DE USUARIO
--
-- 4FN: los datos de proveedor (razon social, verificacion) y los de cliente
-- (cupo de credito, tipo de cliente) son conjuntos de atributos independientes
-- entre si. Si vivieran dentro de la tabla usuarios, cada cliente tendria en
-- NULL todas las columnas de proveedor y viceversa.
--
-- La relacion es 1 a 1 opcional, garantizada con UNIQUE sobre id_usuario.
-- ============================================================================

CREATE TABLE IF NOT EXISTS perfiles_proveedor (
    id_perfil_proveedor   BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario            BIGINT NOT NULL UNIQUE,
    razon_social          VARCHAR(150) NOT NULL,
    nombre_comercial      VARCHAR(120),
    ciudad_operacion      VARCHAR(80)  NOT NULL,
    sitio_web             VARCHAR(150),
    fecha_vinculacion     DATE         NOT NULL,
    verificado            BOOLEAN      NOT NULL DEFAULT FALSE,
    calificacion_promedio INT          DEFAULT 0,
    CONSTRAINT fk_perfilproveedor_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario) ON DELETE CASCADE,
    CONSTRAINT chk_calificacion_proveedor
        CHECK (calificacion_promedio BETWEEN 0 AND 5)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS perfiles_cliente (
    id_perfil_cliente     BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario            BIGINT NOT NULL UNIQUE,
    tipo_cliente          ENUM('PARTICULAR', 'EMPRESARIAL') NOT NULL DEFAULT 'PARTICULAR',
    ciudad_residencia     VARCHAR(80),
    cupo_credito          DECIMAL(12, 2) DEFAULT 0.00,
    fecha_registro        DATE NOT NULL,
    alquileres_realizados INT  DEFAULT 0,
    CONSTRAINT fk_perfilcliente_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario) ON DELETE CASCADE,
    CONSTRAINT chk_cupo_no_negativo CHECK (cupo_credito >= 0)
) ENGINE=InnoDB;

-- ============================================================================
-- 3. COLUMNA ADICIONAL DE HERRAMIENTAS
--
-- Se agrega la ubicacion fisica en bodega, que necesita el panel del proveedor.
-- Va como columna normal porque es un dato escalar y monovaluado: cada
-- herramienta esta en un solo lugar.
--
-- Nota: MySQL 8 no admite "ADD COLUMN IF NOT EXISTS". Para que el script se
-- pueda ejecutar varias veces sin error, se consulta primero el diccionario
-- de datos y se arma la sentencia dinamicamente.
--
-- Se usa este patron y no un procedimiento almacenado a proposito: evita la
-- directiva DELIMITER, que es propia del cliente de consola de MySQL y falla
-- en varios gestores graficos.
-- ============================================================================

SET @existe_columna := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'herramientas'
      AND COLUMN_NAME  = 'ubicacion_bodega'
);

SET @sentencia := IF(@existe_columna = 0,
    'ALTER TABLE herramientas ADD COLUMN ubicacion_bodega VARCHAR(120) NULL AFTER valor_reposicion',
    'SELECT ''La columna ubicacion_bodega ya existia, no se modifico nada'' AS aviso');

PREPARE stmt FROM @sentencia;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================================================
-- 4. CATEGORIAS MULTIPLES POR HERRAMIENTA
--
-- 4FN: una herramienta puede pertenecer a varias categorias (un martillo
-- demoledor es "Demolicion" y tambien "Electricas"). Esa lista es independiente
-- de su galeria de fotos y de sus tarifas.
--
-- Si se guardaran las tres cosas en una sola tabla, para una herramienta con
-- 3 categorias, 4 fotos y 3 tarifas harian falta 3 x 4 x 3 = 36 filas con
-- datos repetidos. Separandolas quedan 3 + 4 + 3 = 10 filas. Esa explosion
-- combinatoria es exactamente lo que elimina la 4FN.
-- ============================================================================

CREATE TABLE IF NOT EXISTS herramientas_categorias (
    id_herramienta BIGINT NOT NULL,
    id_categoria   BIGINT NOT NULL,
    PRIMARY KEY (id_herramienta, id_categoria),
    CONSTRAINT fk_herrcat_herramienta
        FOREIGN KEY (id_herramienta) REFERENCES herramientas (id_herramienta) ON DELETE CASCADE,
    CONSTRAINT fk_herrcat_categoria
        FOREIGN KEY (id_categoria) REFERENCES categorias (id_categoria) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================================
-- 5. GALERIA DE IMAGENES
--
-- 4FN: la lista de fotos es multivaluada e independiente de las categorias y
-- de las tarifas. Columnas tipo foto1, foto2, foto3 violarian la 1FN y ademas
-- limitarian artificialmente la cantidad de imagenes.
-- ============================================================================

CREATE TABLE IF NOT EXISTS herramientas_imagenes (
    id_imagen           BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_herramienta      BIGINT NOT NULL,
    url_foto            VARCHAR(255) NOT NULL,
    descripcion_foto    VARCHAR(200),
    es_principal        BOOLEAN NOT NULL DEFAULT FALSE,
    orden_visualizacion INT     NOT NULL DEFAULT 1,
    fecha_subida        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_imagen_herramienta
        FOREIGN KEY (id_herramienta) REFERENCES herramientas (id_herramienta) ON DELETE CASCADE,
    CONSTRAINT uq_imagen_url UNIQUE (id_herramienta, url_foto)
) ENGINE=InnoDB;

SET @existe := (SELECT COUNT(*) FROM information_schema.STATISTICS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'herramientas_imagenes'
                  AND INDEX_NAME = 'idx_imagen_herramienta');
SET @sentencia := IF(@existe = 0,
    'CREATE INDEX idx_imagen_herramienta ON herramientas_imagenes (id_herramienta, es_principal)',
    'SELECT ''El indice idx_imagen_herramienta ya existia'' AS aviso');
PREPARE stmt FROM @sentencia; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================================
-- 6. FICHA TECNICA
--
-- 4FN: cada tipo de maquina tiene atributos distintos (un andamio no tiene
-- voltaje, un taladro no tiene altura maxima). Modelarlos como pares
-- atributo/valor evita una tabla con decenas de columnas casi siempre en NULL.
-- ============================================================================

CREATE TABLE IF NOT EXISTS especificaciones_tecnicas (
    id_especificacion BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_herramienta    BIGINT NOT NULL,
    atributo          VARCHAR(80)  NOT NULL,
    valor             VARCHAR(120) NOT NULL,
    unidad_medida     VARCHAR(20),
    CONSTRAINT fk_especificacion_herramienta
        FOREIGN KEY (id_herramienta) REFERENCES herramientas (id_herramienta) ON DELETE CASCADE,
    CONSTRAINT uq_especificacion_atributo UNIQUE (id_herramienta, atributo)
) ENGINE=InnoDB;

-- ============================================================================
-- 7. TARIFAS POR MODALIDAD
--
-- 4FN: una herramienta puede tener tarifa diaria, de fin de semana, semanal y
-- mensual. Es otra dependencia multivaluada independiente de las fotos y de
-- las categorias, por lo que exige su propia tabla.
--
-- La restriccion UNIQUE impide registrar dos tarifas del mismo tipo para la
-- misma herramienta.
-- ============================================================================

CREATE TABLE IF NOT EXISTS tarifas_herramienta (
    id_tarifa      BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_herramienta BIGINT NOT NULL,
    tipo_tarifa    ENUM('DIARIA', 'FIN_DE_SEMANA', 'SEMANAL', 'MENSUAL') NOT NULL,
    valor          DECIMAL(12, 2) NOT NULL,
    activa         BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_tarifa_herramienta
        FOREIGN KEY (id_herramienta) REFERENCES herramientas (id_herramienta) ON DELETE CASCADE,
    CONSTRAINT uq_tarifa_tipo UNIQUE (id_herramienta, tipo_tarifa),
    CONSTRAINT chk_tarifa_positiva CHECK (valor > 0)
) ENGINE=InnoDB;

SET @existe := (SELECT COUNT(*) FROM information_schema.STATISTICS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tarifas_herramienta'
                  AND INDEX_NAME = 'idx_tarifa_herramienta');
SET @sentencia := IF(@existe = 0,
    'CREATE INDEX idx_tarifa_herramienta ON tarifas_herramienta (id_herramienta, activa)',
    'SELECT ''El indice idx_tarifa_herramienta ya existia'' AS aviso');
PREPARE stmt FROM @sentencia; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================================
-- 8. INDICES DE APOYO PARA LAS CONSULTAS DEL CATALOGO
--
-- El catalogo filtra constantemente por estado, por precio y por proveedor.
-- Estos indices evitan recorrer la tabla completa en cada busqueda.
-- ============================================================================

SET @existe := (SELECT COUNT(*) FROM information_schema.STATISTICS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'herramientas'
                  AND INDEX_NAME = 'idx_herramienta_estado');
SET @sentencia := IF(@existe = 0,
    'CREATE INDEX idx_herramienta_estado ON herramientas (estado_operativo)',
    'SELECT ''El indice idx_herramienta_estado ya existia'' AS aviso');
PREPARE stmt FROM @sentencia; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @existe := (SELECT COUNT(*) FROM information_schema.STATISTICS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'herramientas'
                  AND INDEX_NAME = 'idx_herramienta_tarifa');
SET @sentencia := IF(@existe = 0,
    'CREATE INDEX idx_herramienta_tarifa ON herramientas (tarifa_diaria)',
    'SELECT ''El indice idx_herramienta_tarifa ya existia'' AS aviso');
PREPARE stmt FROM @sentencia; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @existe := (SELECT COUNT(*) FROM information_schema.STATISTICS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'herramientas'
                  AND INDEX_NAME = 'idx_herramienta_proveedor');
SET @sentencia := IF(@existe = 0,
    'CREATE INDEX idx_herramienta_proveedor ON herramientas (id_proveedor)',
    'SELECT ''El indice idx_herramienta_proveedor ya existia'' AS aviso');
PREPARE stmt FROM @sentencia; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @existe := (SELECT COUNT(*) FROM information_schema.STATISTICS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'usuarios'
                  AND INDEX_NAME = 'idx_usuario_email');
SET @sentencia := IF(@existe = 0,
    'CREATE INDEX idx_usuario_email ON usuarios (email)',
    'SELECT ''El indice idx_usuario_email ya existia'' AS aviso');
PREPARE stmt FROM @sentencia; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================================
-- FIN DEL SCRIPT DDL COMPLEMENTARIO
-- ============================================================================
