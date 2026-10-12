# 📚 Diccionario de Datos — Módulos DEV 2 (Cuarta Forma Normal)

**Proyecto:** Plataforma de Renta de Herramientas y Equipos de Construcción  
**Autor:** Daniel Osorio (DEV 2)  
**Normalización:** Cuarta Forma Normal (4FN)

---

## 1. Tabla: `reservas`

Almacena la cabecera de los contratos de alquiler temporal entre clientes y el sistema.

| Columna | Tipo de Dato | Nulo | Llave | Descripción |
|---|---|---|---|---|
| `id_reserva` | `BIGINT` | NO | PK (Auto) | Identificador primario de la reserva |
| `codigo_reserva` | `VARCHAR(30)` | NO | UK | Código alfanumérico único para radicación (ej. `RES-20261012-0001`) |
| `id_cliente` | `BIGINT` | NO | FK | Referencia al usuario con rol CLIENTE |
| `fecha_solicitud` | `DATETIME` | NO | | Marca temporal del momento en que se radicó la reserva |
| `fecha_inicio` | `DATE` | NO | | Fecha pactada para la entrega de la herramienta |
| `fecha_fin` | `DATE` | NO | | Fecha pactada para la devolución |
| `estado` | `VARCHAR(20)` | NO | | Estado del ciclo: `SOLICITADA`, `APROBADA`, `RECHAZADA`, `EN_CURSO`, `FINALIZADA`, `CANCELADA` |
| `monto_total_estimado` | `DECIMAL(12,2)` | NO | | Total calculado (días × tarifa de ítems) |
| `deposito_garantia` | `DECIMAL(12,2)` | NO | | Depósito de seguridad retornable (30% del valor del alquiler) |
| `notas_cliente` | `TEXT` | SÍ | | Indicaciones especiales de obra o transporte provistas por el cliente |
| `motivo_rechazo` | `TEXT` | SÍ | | Justificación administrativa en caso de denegación |

---

## 2. Tabla: `detalles_reserva`

Descompone los equipos individuales que componen una reserva para evitar atributos repetitivos y permitir multi-herramienta (1FN, 2FN, 4FN).

| Columna | Tipo de Dato | Nulo | Llave | Descripción |
|---|---|---|---|---|
| `id_detalle` | `BIGINT` | NO | PK (Auto) | Identificador primario del ítem de detalle |
| `id_reserva` | `BIGINT` | NO | FK | Referencia a la reserva cabecera |
| `id_herramienta` | `BIGINT` | NO | FK | Referencia a la maquinaria alquilada |
| `nombre_herramienta` | `VARCHAR(150)` | NO | | Snapshot histórico del nombre de la herramienta |
| `cantidad` | `INT` | NO | | Unidades solicitadas (default 1 para maquinaria) |
| `precio_unitario_dia` | `DECIMAL(12,2)` | NO | | Snapshot de la tarifa diaria contratada |
| `dias_alquiler` | `INT` | NO | | Días calendario calculados para el ítem |
| `subtotal` | `DECIMAL(12,2)` | NO | | Importe neto calculado (`cantidad * precio_unitario_dia * dias_alquiler`) |

---

## 3. Tabla: `pagos`

Registra las transacciones monetarias procesadas a través de la pasarela de pagos.

| Columna | Tipo de Dato | Nulo | Llave | Descripción |
|---|---|---|---|---|
| `id_pago` | `BIGINT` | NO | PK (Auto) | Identificador primario del pago |
| `numero_transaccion` | `VARCHAR(50)` | NO | UK | Identificador único de transacción (ej. `TRX-20261012-XXXX`) |
| `id_reserva` | `BIGINT` | NO | FK | Referencia a la reserva asociada |
| `id_metodo_pago` | `BIGINT` | NO | FK | Referencia al medio de pago utilizado (`PSE`, `TARJETA`, etc.) |
| `monto` | `DECIMAL(12,2)` | NO | | Valor cobrado en moneda local (COP) |
| `tipo_pago` | `VARCHAR(30)` | NO | | Tipo de abono: `ANTICIPO`, `DEPOSITO_GARANTIA`, `TOTALIDAD`, `CARGO_MORA`, `CARGO_DANO` |
| `estado_pago` | `VARCHAR(20)` | NO | | Estado de la transacción: `APROBADO`, `PENDIENTE`, `RECHAZADO` |
| `fecha_pago` | `DATETIME` | NO | | Marca de tiempo de la respuesta de pasarela |
| `referencia_pasarela` | `VARCHAR(100)` | SÍ | | Código de confirmación emitido por la entidad financiera |

---

## 4. Tabla: `facturas`

Comprobante fiscal electrónico legal con desglose obligatorio del **IVA del 19%**.

| Columna | Tipo de Dato | Nulo | Llave | Descripción |
|---|---|---|---|---|
| `id_factura` | `BIGINT` | NO | PK (Auto) | Identificador primario de la factura |
| `numero_factura` | `VARCHAR(50)` | NO | UK | Consecutivo fiscal único (ej. `FAC-20261012-0001`) |
| `id_pago` | `BIGINT` | NO | FK | Referencia al pago bancario comprobado |
| `id_cliente` | `BIGINT` | NO | FK | Referencia al cliente adquirente |
| `fecha_emision` | `DATETIME` | NO | | Fecha y hora legal de emisión |
| `subtotal` | `DECIMAL(12,2)` | NO | | Base gravable (Total / 1.19) |
| `iva` | `DECIMAL(12,2)` | NO | | Impuesto al Valor Agregado del 19% |
| `total` | `DECIMAL(12,2)` | NO | | Total facturado |
| `estado` | `VARCHAR(20)` | NO | | Estado tributario: `EMITIDA`, `PAGADA`, `ANULADA` |
| `url_comprobante_pdf` | `VARCHAR(255)` | SÍ | | Enlace al comprobante digital |

---

## 5. Tabla: `devoluciones`

Check-in físico y acta de recepción de maquinaria al terminar el alquiler.

| Columna | Tipo de Dato | Nulo | Llave | Descripción |
|---|---|---|---|---|
| `id_devolucion` | `BIGINT` | NO | PK (Auto) | Identificador de la devolución |
| `id_reserva` | `BIGINT` | NO | FK (UK) | Reserva que se devuelve (1 a 1) |
| `fecha_devolucion_pactada`| `DATE` | NO | | Fecha límite original de retorno |
| `fecha_devolucion_real` | `DATE` | NO | | Fecha efectiva en que se entregó físicamente |
| `recibido_por` | `BIGINT` | NO | FK | Usuario supervisor que recibe el equipo |
| `estado_equipo_general` | `VARCHAR(30)` | NO | | Calificación técnica: `BUEN_ESTADO`, `DANO_LEVE`, `DANO_GRAVE`, `IRREPARABLE` |
| `dias_retraso` | `INT` | NO | | Días de mora (cero si fue a tiempo) |
| `observaciones` | `TEXT` | SÍ | | Anotaciones técnicas del peritaje |

---

## 6. Tabla: `reportes_dano` y `evidencias_dano` (4FN)

Descomposición estricta en Cuarta Forma Normal: una avería puede tener múltiples fotografías sin introducir dependencias multivaluadas en la tabla principal.

### `reportes_dano`
- `id_reporte`: PK
- `id_devolucion`: FK
- `id_herramienta`: FK
- `tipo_dano`: `LEVE`, `GRAVE`, `IRREPARABLE`
- `descripcion_dano`: Detalle de la rotura
- `costo_estimado_reparacion`: Valor tasado
- `responsable_asume`: `CLIENTE` o `PROVEEDOR`

### `evidencias_dano` (4FN)
- `id_evidencia`: PK
- `id_reporte`: FK
- `url_foto`: Enlace seguro de la imagen forense
- `descripcion_foto`: Ángulo o elemento fotografiado
- `fecha_subida`: Timestamp de subida
