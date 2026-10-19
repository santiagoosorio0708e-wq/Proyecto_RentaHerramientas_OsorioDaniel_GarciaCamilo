# Changelog

Todas las modificaciones notables a este proyecto están documentadas en este archivo.
El formato se basa en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y se adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [1.0.1] - 2026-10-19

### Añadido
- **Diagrama Relacional Vectorial (SVG):** Generación e integración en `docs/img/diagrama_relacional_4fn.svg` y `database/diagrama_relacional_4fn.svg` de las 16 tablas del modelo en 4FN.
- **Resolución Dinámica de URL_BASE:** Detección de contexto en `api.js` para compatibilidad nativa con Tomcat embebido y servidores estáticos externos.
- **Suite Ampliada de Tests (57 Tests):** Pruebas unitarias adicionales para rangos disjuntos en `RangoFechasTest`, exactitud decimal contable en `FacturaTest` y devoluciones anticipadas en `DevolucionApplicationServiceTest`.
- **Documentación OpenAPI Enriquecida:** Anotaciones `@ApiResponse` para códigos 200, 201, 404 y 422 en todos los controladores REST.

### Mejorado
- **README.md Profesional:** Integración de badges shields.io, tabla de contenidos desplegable, catálogo de endpoints con payloads JSON de solicitud/respuesta y guía de configuración de `application.properties`.
- **Vistas Analíticas SQL:** Creación de `vista_ingresos_proveedor` en `database/views_and_indexes.sql`.

## [1.0.0] - 2026-10-12

### Añadido
- **Módulo de Reservas (DEV 2):** Solicitud, aprobación, rechazo, cancelación y auditoría transaccional de alquileres en Arquitectura Hexagonal.
- **Módulo de Pagos y Facturación (DEV 2):** Pasarela simulada multimetodo y emisión de facturación electrónica con 19% de IVA según normativa fiscal colombiana.
- **Módulo de Devoluciones y Daños (DEV 2):** Check-in físico, cargos por mora por días de retraso y reporte forense de averías en Cuarta Forma Normal (4FN).
- **Módulo de Reportes y BI (DEV 2):** Dashboard ejecutivo, KPIs financieros, rankings de herramientas y clientes frecuentes.
- **Frontend Interactivo (DEV 2):** Portales para `reservas.html`, `gestion_reservas.html`, `dashboard.html` y modal en `index.html`.
- **Base de Datos 4FN:** Scripts `database/schema_4fn.sql`, `database/data_20_records.sql`, `database/views_and_indexes.sql` y diagramas ERD.
- **Colección Postman:** Entorno y peticiones preconfiguradas para pruebas end-to-end.
- **Módulos de Identidad e Inventario (DEV 1):** Autenticación JWT y catálogo de herramientas.

### Mejorado
- Suite completa de 45+ pruebas unitarias y de integración pasando al 100% en H2 en memoria.
- Documentación técnica profesional de compilación y despliegue multi-entorno.
