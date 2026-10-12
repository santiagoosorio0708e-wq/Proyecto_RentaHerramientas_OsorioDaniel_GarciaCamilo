# 🗺️ Diagrama Entidad-Relación (ERD) - Normalización en 4FN

## Plataforma de Renta de Herramientas y Equipos de Construcción

Este documento contiene el modelo relacional completo de la base de datos `renta_herramientas_db`, estructurado estrictamente en **Cuarta Forma Normal (4FN)**.

---

## 📊 Diagrama Entidad-Relación (Mermaid)

```mermaid
erDiagram
    ROLES ||--o{ USUARIOS_ROLES : "asignado_a"
    USUARIOS ||--o{ USUARIOS_ROLES : "tiene"
    USUARIOS ||--o{ HERRAMIENTAS : "publica_como_proveedor"
    USUARIOS ||--o{ RESERVAS : "solicita_como_cliente"
    USUARIOS ||--o{ AUDITORIA_ESTADOS_RESERVA : "cambiado_por"
    USUARIOS ||--o{ FACTURAS : "facturado_a"
    USUARIOS ||--o{ DEVOLUCIONES : "recibido_por"

    CATEGORIAS ||--o{ HERRAMIENTAS : "clasifica"

    HERRAMIENTAS ||--o{ DETALLES_RESERVA : "es_alquilada_en"
    HERRAMIENTAS ||--o{ REPORTES_DANO : "inspeccionada_en"

    RESERVAS ||--|{ DETALLES_RESERVA : "contiene"
    RESERVAS ||--o{ AUDITORIA_ESTADOS_RESERVA : "registra_historial"
    RESERVAS ||--o{ PAGOS : "es_pagada_con"
    RESERVAS ||--o| DEVOLUCIONES : "culmina_con"

    METODOS_PAGO ||--o{ PAGOS : "utilizado_en"

    PAGOS ||--o| FACTURAS : "genera"

    FACTURAS ||--|{ DETALLES_FACTURA : "desglosa_con_iva"

    DEVOLUCIONES ||--o{ REPORTES_DANO : "presenta"
    DEVOLUCIONES ||--o{ CARGOS_ADICIONALES : "liquida_recargos"

    REPORTES_DANO ||--o{ EVIDENCIAS_DANO : "respaldado_con_fotos_4FN"

    ROLES {
        bigint id_rol PK
        varchar nombre UK
        varchar descripcion
    }

    USUARIOS {
        bigint id_usuario PK
        varchar nombre_completo
        varchar email UK
        varchar password_hash
        varchar telefono
        varchar direccion
        enum tipo_documento
        varchar numero_documento UK
        boolean activo
        timestamp fecha_creacion
    }

    USUARIOS_ROLES {
        bigint id_usuario PK,FK
        bigint id_rol PK,FK
    }

    CATEGORIAS {
        bigint id_categoria PK
        varchar nombre UK
        varchar descripcion
    }

    HERRAMIENTAS {
        bigint id_herramienta PK
        bigint id_proveedor FK
        bigint id_categoria FK
        varchar nombre
        text descripcion
        varchar marca
        varchar modelo
        varchar numero_serie UK
        enum estado_operativo
        decimal tarifa_diaria
        decimal valor_reposicion
        timestamp fecha_registro
    }

    RESERVAS {
        bigint id_reserva PK
        varchar codigo_reserva UK
        bigint id_cliente FK
        timestamp fecha_solicitud
        date fecha_inicio
        date fecha_fin
        enum estado
        decimal monto_total_estimado
        decimal deposito_garantia
        varchar notas_cliente
        varchar motivo_rechazo
    }

    DETALLES_RESERVA {
        bigint id_detalle_reserva PK
        bigint id_reserva FK
        bigint id_herramienta FK
        int cantidad
        decimal precio_unitario_dia
        int dias_alquiler
        decimal subtotal
    }

    AUDITORIA_ESTADOS_RESERVA {
        bigint id_auditoria PK
        bigint id_reserva FK
        varchar estado_anterior
        varchar estado_nuevo
        timestamp fecha_cambio
        bigint cambiado_por FK
        varchar observacion
    }

    METODOS_PAGO {
        bigint id_metodo_pago PK
        varchar nombre UK
        varchar descripcion
        boolean activo
    }

    PAGOS {
        bigint id_pago PK
        varchar numero_transaccion UK
        bigint id_reserva FK
        bigint id_metodo_pago FK
        decimal monto
        enum tipo_pago
        enum estado_pago
        timestamp fecha_pago
        varchar referencia_pasarela
    }

    FACTURAS {
        bigint id_factura PK
        varchar numero_factura UK
        bigint id_pago UK,FK
        bigint id_cliente FK
        timestamp fecha_emision
        decimal subtotal
        decimal iva
        decimal total
        enum estado_factura
        varchar url_comprobante_pdf
    }

    DETALLES_FACTURA {
        bigint id_detalle_factura PK
        bigint id_factura FK
        varchar descripcion_item
        int cantidad
        decimal precio_unitario
        decimal subtotal_linea
    }

    DEVOLUCIONES {
        bigint id_devolucion PK
        bigint id_reserva UK,FK
        date fecha_devolucion_pactada
        date fecha_devolucion_real
        bigint recibido_por FK
        enum estado_equipo_general
        int dias_retraso
        text observaciones
    }

    REPORTES_DANO {
        bigint id_reporte_dano PK
        bigint id_devolucion FK
        bigint id_herramienta FK
        enum tipo_dano
        text descripcion_dano
        decimal costo_estimado_reparacion
        enum responsable_asume
        timestamp fecha_reporte
    }

    EVIDENCIAS_DANO {
        bigint id_evidencia PK
        bigint id_reporte_dano FK
        varchar url_foto
        varchar descripcion_foto
        timestamp fecha_subida
    }

    CARGOS_ADICIONALES {
        bigint id_cargo PK
        bigint id_devolucion FK
        enum tipo_cargo
        decimal monto
        boolean pagado
        timestamp fecha_cargo
        varchar descripcion
    }
```

---

## 📌 Puntos Críticos de la Normalización 4FN

1. **`evidencias_dano`:**
   - Desacopla la lista multivaluada de fotografías de los atributos escalares del daño (`costo_estimado_reparacion`, `responsable_asume`). Esto evita que cada foto adicional obligue a duplicar los metadatos de la avería.
2. **`auditoria_estados_reserva`:**
   - Desacopla la línea temporal de cambios de estado del conjunto multivaluado de herramientas alquiladas (`detalles_reserva`).
3. **`detalles_factura`:**
   - Garantiza que cada línea de cobro se justifique individualmente, aplicando la base imponible y el 19% de IVA de manera verificable ante auditorías tributarias.
