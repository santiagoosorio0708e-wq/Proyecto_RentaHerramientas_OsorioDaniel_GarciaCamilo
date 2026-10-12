# ADR 003: Auditoría Transaccional e Histórica de Estados de Reserva

- **Estado:** Aceptado
- **Fecha:** 2026-10-12
- **Autor:** Daniel Osorio (DEV 2)
- **Contexto:** Plataforma de Renta de Herramientas y Equipos de Construcción

---

## Contexto y Planteamiento del Problema

El ciclo de vida de un alquiler de maquinaria involucra múltiples transiciones críticas:
`SOLICITADA ➔ APROBADA ➔ EN_CURSO ➔ FINALIZADA` (o `RECHAZADA`/`CANCELADA`).

Para resolver disputas comerciales, retrasos de entrega o aclaraciones de cobro de depósito de garantía, el sistema no puede limitarse a almacenar el estado actual; requiere una bitácora inmutable de auditoría con actor, timestamp y justificación.

## Decisión de Diseño

Se implementó el patrón de **Auditoría Transaccional Desacoplada**:

1. **Puerto de Salida:** `AuditoriaReservaPort` expone el contrato de persistencia de eventos de estado.
2. **Entidad Dedicada:** `audit_log_reservas` (`id_log`, `id_reserva`, `estado_anterior`, `estado_nuevo`, `cambiado_por`, `fecha_cambio`, `motivo_observaciones`).
3. **Garantía ACID:** Cada transición de estado en `ReservaApplicationService` ejecuta la actualización del agregado `Reserva` y la inserción del log de auditoría dentro de la misma transacción `@Transactional`.

## Consecuencias

### Positivas
- Trazabilidad legal y forense al 100%.
- Facilidad para calcular el Lead Time entre solicitud y aprobación.
- Responsabilidad clara de cada usuario (operario, cliente o administrador) que intervino en el alquiler.
