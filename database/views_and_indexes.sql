-- ============================================================================
-- PLATAFORMA DE RENTA DE HERRAMIENTAS Y EQUIPOS DE CONSTRUCCIÓN
-- OPTIMIZACIÓN DE ÍNDICES B-TREE Y VISTAS ANALÍTICAS (DEV 2)
-- Autor: Daniel Osorio
-- ============================================================================

USE renta_herramientas_db;

-- ----------------------------------------------------------------------------
-- 1. ÍNDICES ADICIONALES PARA ACELERAR CONSULTAS DE DISPONIBILIDAD Y REPORTES
-- ----------------------------------------------------------------------------

-- Acelera la búsqueda de reservas activas por rango de fechas
CREATE INDEX IF NOT EXISTS idx_reservas_fechas_estado 
ON reservas (fecha_inicio, fecha_fin, estado);

-- Acelera la búsqueda de detalles por herramienta en reservas concurrentes
CREATE INDEX IF NOT EXISTS idx_detalles_reserva_herramienta 
ON detalles_reserva (id_herramienta, id_reserva);

-- Acelera la búsqueda de facturas emitidas por fecha y cliente
CREATE INDEX IF NOT EXISTS idx_facturas_cliente_fecha 
ON facturas (id_cliente, fecha_emision);

-- Acelera el filtro de devoluciones por retraso y fecha pactada
CREATE INDEX IF NOT EXISTS idx_devoluciones_retraso 
ON devoluciones (dias_retraso, fecha_devolucion_pactada);

-- Acelera la agregación de cargos adicionales por tipo y estado de pago
CREATE INDEX IF NOT EXISTS idx_cargos_tipo_pagado 
ON cargos_adicionales (tipo_cargo, pagado);

-- ----------------------------------------------------------------------------
-- 2. VISTAS ANALÍTICAS PARA REPORTES GERENCIALES
-- ----------------------------------------------------------------------------

-- Vista: Resumen Consolidado de Reservas con Duración e Importes
CREATE OR REPLACE VIEW vista_resumen_reservas AS
SELECT 
    r.id_reserva,
    r.codigo_reserva,
    r.id_cliente,
    r.estado,
    r.fecha_inicio,
    r.fecha_fin,
    DATEDIFF(r.fecha_fin, r.fecha_inicio) AS dias_alquiler,
    r.monto_total_estimado,
    r.deposito_garantia,
    COUNT(dr.id_detalle) AS cantidad_items
FROM reservas r
LEFT JOIN detalles_reserva dr ON r.id_reserva = dr.id_reserva
GROUP BY r.id_reserva, r.codigo_reserva, r.id_cliente, r.estado, r.fecha_inicio, r.fecha_fin, r.monto_total_estimado, r.deposito_garantia;

-- Vista: Control de Facturación con IVA Desglosado
CREATE OR REPLACE VIEW vista_control_facturacion_iva AS
SELECT 
    f.id_factura,
    f.numero_factura,
    f.id_pago,
    f.id_cliente,
    f.fecha_emision,
    f.subtotal AS base_gravable,
    f.iva AS iva_19_porc,
    f.total AS total_facturado,
    f.estado,
    p.numero_transaccion,
    p.estado_pago
FROM facturas f
JOIN pagos p ON f.id_pago = p.id_pago;

-- Vista: Siniestralidad y Reportes de Avería en 4FN
CREATE OR REPLACE VIEW vista_siniestralidad_herramientas AS
SELECT 
    rd.id_reporte,
    rd.id_devolucion,
    rd.id_herramienta,
    rd.tipo_dano,
    rd.responsable_asume,
    rd.costo_estimado_reparacion,
    COUNT(ed.id_evidencia) AS total_fotos_evidencia
FROM reportes_dano rd
LEFT JOIN evidencias_dano ed ON rd.id_reporte = ed.id_reporte
GROUP BY rd.id_reporte, rd.id_devolucion, rd.id_herramienta, rd.tipo_dano, rd.responsable_asume, rd.costo_estimado_reparacion;
