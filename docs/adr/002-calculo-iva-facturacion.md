# ADR 002: Cálculo Automático de IVA del 19% y Base Gravable en Facturación Electrónica

- **Estado:** Aceptado
- **Fecha:** 2026-10-12
- **Autor:** Daniel Osorio (DEV 2)
- **Contexto:** Plataforma de Renta de Herramientas y Equipos de Construcción

---

## Contexto y Planteamiento del Problema

El marco tributario colombiano (Art. 468 del Estatuto Tributario) exige que los servicios de alquiler de bienes muebles e inmuebles liquiden el Impuesto sobre las Ventas (**IVA**) con tarifa general del **19%**.

Existían dos enfoques para la arquitectura del cobro:
1. Recibir del cliente el valor neto y que el frontend sume el impuesto.
2. Recibir del cliente el valor total pactado y que el dominio de negocio descomponga de forma determinista la base gravable y el impuesto utilizando aritmética de alta precisión.

## Decisión de Diseño

Se decidió encapsular el cálculo en el núcleo de dominio (`Factura.java` y `PagoApplicationService.java`):

1. **Aritmética Exacta:** Uso estricto de `java.math.BigDecimal` con modo de redondeo bancario `RoundingMode.HALF_UP` a 2 decimales para evitar diferencias por centavos.
2. **Fórmula Determinista:**
   $$\text{Subtotal (Base Gravable)} = \frac{\text{Total}}{1.19}$$
   $$\text{IVA (19\%)} = \text{Total} - \text{Subtotal}$$
3. **Inmutabilidad y Auditoría:** Una vez generada la factura (`FAC-YYYYMMDD-XXXX`), los valores quedan fijados de forma persistente en la tabla `facturas`, impidiendo modificaciones posteriores.

## Consecuencias

### Positivas
- Cumplimiento normativo y contable automático.
- Respaldo legal para comprobantes PDF simulados.
- Facilidad para reportes consolidados de recaudación fiscal en el módulo de reportes.
