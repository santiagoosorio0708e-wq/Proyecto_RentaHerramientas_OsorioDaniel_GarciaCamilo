# Changelog

Todas las modificaciones notables a este proyecto están documentadas en este archivo.
El formato se basa en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y se adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [1.4.0] - 2026-10-20

### Añadido
- **Módulo de Perfil Integral del Usuario (`perfil.html` / `perfil.js`):**
  - Actualización reactiva de datos personales (nombre completo, correo electrónico, teléfono y dirección) con validación de no duplicidad de correo en backend y persistencia local inmediata.
  - Carrito de compras y cotizador de alquileres con cálculo dinámico de días, subtotales, depósito de garantía, IVA 19% y confirmación en lote de reservas.
  - Historial de alquileres pasados con visualización de estados, tarifas diarias, montos totales y re-alquiler rápido con un clic.
  - Visor interactivo y exportación en PDF de Facturación Electrónica DIAN.

## [1.3.0] - 2026-10-20

### Añadido
- **Base de Datos SQLite 3 con Permanencia Total de Datos:**
  - Configuración de SQLite como base de datos por defecto (`renta_herramientas.db`) con soporte universal para Windows, Linux y macOS (Apple Silicon M1/M2/M3/M4 e Intel) sin requerir instalaciones de MySQL ni contenedores Docker.
  - Conexión optimizada HikariCP con Write-Ahead Logging (`PRAGMA journal_mode=WAL;`), llaves foráneas (`PRAGMA foreign_keys=ON;`) y timeout de bloqueo concurrente de 30 segundos (`busy_timeout=30000;`).
  - Componente sembrador automático (`DataInitializer`): detecta bases de datos nuevas y carga automáticamente roles maestros, usuarios con contraseñas BCrypt reales (`Password123`), 20 categorías, 20 herramientas con fotos HD, tarifas y especificaciones técnicas, 4 métodos de pago y reservas iniciales con facturación e IVA del 19%.
  - Garantía de permanencia: cualquier cambio o registro nuevo efectuado desde la web se almacena directamente en el archivo SQLite persistiendo entre reinicios y cambios de dispositivo.
  - Controlador de diagnóstico y salud (`HealthController`): rutas públicas `/health` y `/actuator/health` para supervisión en tiempo real del estado del backend y del motor de datos.
  - Prueba de integración automatizada (`SQLitePersistenceTest`): 100% de cobertura validando creación física de la BD, idempotencia de datos maestros y transaccionalidad en tiempo de ejecución (63/63 tests pasando).

### Mejorado
- **Lanzador para macOS (`start-mac.sh`):** Configurado con SQLite por defecto, eliminando dependencias de servicios externos para arranque en un solo clic en Mac.
- **Detección Dinámica de Backend en `api.js`:** Consulta optimizada al endpoint de catálogo y salud para enlace reactivo inmediato entre frontend y backend Spring Boot.

## [1.2.0] - 2026-10-20

### Corregido
- **Resolución de Error en Facturación Electrónica (`idPago` undefined):** Corrección del flujo asíncrono y de los métodos de búsqueda en `api.js` y `mock_data.js` que impedían consultar la factura y provocaban una excepción de lectura sobre propiedades indefinidas.
- **Sincronización de Controladores y Puertos:** Inclusión del método `obtenerFacturaPorReserva` en `ConsultarFacturaUseCase`, `PagoApplicationService` y el endpoint REST `GET /facturas/reserva/{idReserva}` en `FacturaController`.
- **Importación de Anotaciones OpenAPI:** Corrección del import faltante de `@ApiResponse` en `ReporteController.java`.

### Añadido
- **Visor Oficial de Factura Electrónica en PDF (Estándar DIAN):**
  - Hoja digital A4 (`.hoja-factura-pdf`) con diseño tributario legal: membrete oficial de RENTA TOOLS COLOMBIA S.A.S. (NIT 901.458.789-4), radicado DIAN No. 1876400012, desglose de ítems, cálculo del 19% de IVA (Art. 468 E.T.) e importe en letras en pesos colombianos.
  - Bloque de certificación digital con código QR fiscal vectorial y CUFE con botón interactivo de copiado rápido al portapapeles.
  - Exportación y descarga directa a archivo PDF (`html2pdf.js`).
  - Soporte de impresión y guardado PDF nativo impecable mediante reglas dedicadas `@media print`.

## [1.1.1] - 2026-10-20

### Corregido
- **La autenticación ya no se simula en el cliente:** El inicio de sesión y el registro van siempre contra el backend con JWT firmado.
- **Detección del estado del servidor:** Sondeo optimizado para verificar conectividad real con el servidor.
- **Guardia de las pantallas privadas (`UI.exigirSesion`):** Redirección obligatoria a `login.html` sin auto-iniciar sesiones no deseadas.
- **Tiempo de espera de las peticiones:** Soporte ampliado para arranque en frío de la JVM.

### Añadido
- **Archivo `LICENSE` (MIT):** Licencia de código abierto MIT en la raíz del repositorio.

## [1.1.0] - 2026-10-19

### Añadido
- **Optimización y Lanzador para macOS (`start-mac.sh`):** Script de ejecución automatizado con permisos de ejecución nativos (`100755` en Git), verificación de JDK 21 / Homebrew, validación de MySQL y apertura automática del navegador Safari o Chrome con `open`.
- **Compatibilidad con Apple Silicon en Docker (`docker-compose.yml`):** Directiva `platform: linux/amd64` en el contenedor MySQL 8 para garantizar estabilidad y rendimiento nativo en arquitecturas ARM (M1/M2/M3/M4).
- **Soporte de Áreas Seguras de Apple (Safe Areas):** Adaptación visual a Dynamic Island, Notch y esquinas redondeadas en dispositivos Apple mediante `viewport-fit=cover` y variables `env(safe-area-inset-*)`.
- **Sincronización Automática con `prefers-color-scheme`:** Detección reactiva en tiempo real en `comun.js` para alternar fluidamente entre tema claro y oscuro según las preferencias del sistema macOS.

### Mejorado
- **Diseño Web 100% Responsive y Fluidez 60 FPS:**
  - Animaciones aceleradas por GPU (`macOpeningFluid`, `heroSlideIn`) con curvas cúbicas ultra fluidas (`cubic-bezier(0.16, 1, 0.3, 1)`).
  - Pila tipográfica con soporte nativo para San Francisco (`'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display'`) y antialiasing para pantallas Retina (`-webkit-font-smoothing: antialiased`).
  - Barras de scroll minimalistas translúcidas idénticas a macOS (`overlay scrollbars`).
  - Rejillas fluidas y tarjetas con optimización de pintado (`content-visibility: auto`).
  - Modales convertidos en hojas inferiores (*bottom-sheets*) interactivas en pantallas móviles.
  - Media queries adaptativas completas para pantallas móviles (320px, 390px, 480px), tablets (768px) y portátiles Mac (1024px+).

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
