# ADR 001: Adopción de la Cuarta Forma Normal (4FN) en el Modelo Relacional

- **Estado:** Aceptado
- **Fecha:** 2026-10-12
- **Autor:** Daniel Osorio (DEV 2)
- **Contexto:** Plataforma de Renta de Herramientas y Equipos de Construcción

---

## Contexto y Planteamiento del Problema

En sistemas de renta de maquinaria, múltiples entidades poseen atributos multivaluados independientes entre sí. Por ejemplo:
1. Una herramienta física puede pertenecer a múltiples categorías y simultáneamente poseer una galería fotográfica de inspección y diversas modalidades tarifarias.
2. Un reporte de avería físico (`reporte_dano`) debe respaldarse con múltiples fotografías forenses (`url_foto`, `descripcion_foto`, `fecha_subida`) sin acoplarse con la tasación económica ni con las piezas mecánicas afectadas.

Si estas dependencias se agrupan en una única relación que ya se encuentra en Forma Normal de Boyce-Codd (BCNF), se produce una **Dependencia Multivaluada no trivial ($X \twoheadrightarrow Y \mid Z$)**, originando redundancia exponencial y anomalías en actualización.

## Decisión de Diseño

Se adopta formalmente la **Cuarta Forma Normal (4FN)** mediante el principio de descomposición sin pérdida de Fagin:

1. **Descomposición de Reportes y Evidencias:**
   - `reportes_dano`: Almacena exclusivamente el dictamen pericial, tipo de daño y costo estimado.
   - `evidencias_dano`: Almacena la colección 1-a-N de fotografías forenses.
2. **Descomposición de Maquinaria y Multimedia:**
   - `herramientas`: Atributos escalares puros.
   - `herramientas_imagenes`: Enlaces fotográficos independientes.
   - `tarifas_herramienta`: Esquemas de cobro independientes.
3. **Descomposición de Reservas e Ítems:**
   - `reservas`: Contrato global y fechas.
   - `detalles_reserva`: Ítems alquilados con snapshot de precio.

## Consecuencias

### Positivas
- **Cero Redundancia:** No existen productos cartesianos de filas al asociar múltiples evidencias a un daño.
- **Integridad Referencial Estricta:** Las restricciones de clave foránea `ON DELETE RESTRICT / CASCADE` operan a nivel atómico.
- **Claridad Forense:** Permite auditorías técnicas independientes para compañías aseguradoras.

### Negativas / Mitigaciones
- Mayor cantidad de `JOIN` en consultas analíticas.
- **Mitigación:** Creación de vistas optimizadas (`database/views_and_indexes.sql`) e índices B-Tree específicos.
