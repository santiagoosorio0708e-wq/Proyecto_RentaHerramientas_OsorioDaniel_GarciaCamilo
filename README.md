<div align="center">

# 🏗️ Plataforma de Renta de Herramientas y Equipos de Construcción 🚜

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/Security-JJWT%200.12.6-red.svg?logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/API%20Docs-OpenAPI%203%20%2F%20Swagger-green.svg?logo=swagger&logoColor=white)](http://localhost:8080/api/v1/swagger-ui.html)
[![Tests](https://img.shields.io/badge/Tests-41%20Passed-success.svg?logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg?logo=docker&logoColor=white)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

<p align="center">
  <b>Solución empresarial completa para el alquiler, despacho, control de inventario, pasarela de pagos, facturación legal con 19% de IVA y check-in de retorno con inspección física en 4FN.</b>
</p>

---

</div>

<details>
<summary><b>📑 Tabla de Contenidos (Click para desplegar)</b></summary>

- [📖 Acerca del Proyecto](#-acerca-del-proyecto)
- [🛠️ Construido con](#️-construido-con)
- [🏛️ Arquitectura del Sistema](#️-arquitectura-del-sistema)
  - [📐 Estructura Hexagonal DDD por Bounded Context](#-estructura-hexagonal-ddd-por-bounded-context)
  - [🗄️ Modelo de Datos y Cuarta Forma Normal (4FN)](#️-modelo-de-datos-y-cuarta-forma-normal-4fn)
- [📋 Prerrequisitos](#-prerrequisitos)
- [🚀 Instalación y Puesta en Marcha](#-instalación-y-puesta-en-marcha)
  - [Opción 1: Ejecución Local en Windows](#opción-1-ejecución-local-en-windows)
  - [Opción 2: Despliegue con Docker y Docker Compose](#opción-2-despliegue-con-docker-y-docker-compose)
- [💻 Uso y Características](#-uso-y-características)
  - [🔑 1. Identidad y Control de Acceso (RBAC)](#1-identidad-y-control-de-acceso-rbac)
  - [📦 2. Catálogo e Inventario](#2-catálogo-e-inventario)
  - [📅 3. Reservas y Alquileres](#3-reservas-y-alquileres)
  - [💳 4. Pagos y Facturación Electrónica (IVA 19%)](#4-pagos-y-facturación-electrónica-iva-19)
  - [🔍 5. Devoluciones, Daños y Evidencias 4FN](#5-devoluciones-daños-y-evidencias-4fn)
  - [📊 6. Dashboard Analítico y Reportes Ejecutivos](#6-dashboard-analítico-y-reportes-ejecutivos)
- [🧪 Pruebas Automatizadas](#-pruebas-automatizadas)
- [📮 Pruebas con Postman](#-pruebas-con-postman)
- [🗺️ Hoja de Ruta (Roadmap)](#️-hoja-de-ruta-roadmap)
- [👥 Equipo y Contacto](#-equipo-y-contacto)

</details>

---

## 📖 Acerca del Proyecto

La **Plataforma de Renta de Herramientas y Equipos de Construcción** es un sistema de grado productivo diseñado para resolver de forma integral la cadena de valor entre contratistas, particulares y empresas proveedoras de maquinaria pesada y herramientas de obra.

Permite a los **proveedores** publicar maquinaria especializada con tarifas diarias y fichas técnicas, y a los **clientes** reservar equipos con confirmación inmediata, realizar pagos en línea mediante pasarela simulada, descargar facturas legales con desglose de IVA del 19%, y gestionar el ciclo de devolución con inspección técnica de daños respaldada por evidencia fotográfica.

### 🌟 Funcionalidades Principales
- **Registro y Autenticación Segura:** JWT con rotación de refresh token y control estricto de roles (`CLIENTE`, `PROVEEDOR`, `ADMIN`).
- **Catálogo de Maquinaria en Tiempo Real:** Filtros por texto, categoría, rango de precios, disponibilidad y ubicación.
- **Flujo Integral de Reservas:** Estados de máquina (`SOLICITADA` ➔ `APROBADA` ➔ `EN_CURSO` ➔ `FINALIZADA`), depósitos de garantía retornables y cálculo automático de mora.
- **Facturación Electrónica DIAN:** Desglose automático de Base Gravable (Subtotal) e Impuesto al Valor Agregado (**IVA 19%**).
- **Inspección Física y Check-In:** Gestión de retrasos con multas automáticas y reporte de averías con fotos normalizadas en **4FN**.
- **Dashboard Gerencial:** KPIs de recaudo, top herramientas más solicitadas, clientes frecuentes y tasa de siniestralidad.

[Volver al inicio ↑](#-plataforma-de-renta-de-herramientas-y-equipos-de-construcción-)

---

## 🛠️ Construido con

El proyecto aprovecha un stack moderno, desacoplado y de alto rendimiento:

| Componente | Tecnología | Versión | Propósito |
|---|---|---|---|
| **Lenguaje** | **Java** | 21 LTS | Tipado estático moderno, Records, Pattern Matching |
| **Framework Base** | **Spring Boot** | 3.4.1 | Inyección de dependencias y orquestación |
| **Acceso a Datos** | **Spring Data JPA / Hibernate** | 6.6.4 | Mapeo objeto-relacional y consultas optimizadas |
| **Seguridad** | **Spring Security + JJWT** | 0.12.6 | Autenticación stateless y control RBAC |
| **Base de Datos** | **MySQL Server** | 8.0+ | Almacenamiento relacional normalizado en 4FN |
| **BD Pruebas** | **H2 Database** | 2.3+ | Base de datos en memoria para suites de tests |
| **Documentación** | **Springdoc OpenAPI / Swagger UI**| 2.8.3 | Documentación interactiva OAS 3.0 |
| **Frontend** | **HTML5, CSS3 Moderno, JavaScript ES6+**| Vanilla | UI interactiva, responsiva, sin dependencias externas |
| **Contenedores** | **Docker & Docker Compose** | 24+ | Despliegue reproducible multi-servicio |

[Volver al inicio ↑](#-plataforma-de-renta-de-herramientas-y-equipos-de-construcción-)

---

## 🏛️ Arquitectura del Sistema

El backend sigue rigurosamente los principios de **Arquitectura Hexagonal (Puertos y Adaptadores)** combinados con **Diseño Guiado por el Dominio (DDD)**. El núcleo de negocio permanece completamente agnóstico de frameworks, bases de datos o pasarelas externas.

### 📐 Estructura Hexagonal DDD por Bounded Context

```
src/main/java/com/rentaherramientas/
│
├── common/                                      # Infraestructura y Utilidades Compartidas
│   ├── config/                                  # SecurityConfig, OpenApiConfig, CorsConfig
│   ├── dto/                                     # ApiResponseDto<T> estandarizado
│   └── exception/                               # GlobalExceptionHandler, ApiException
│
└── modules/                                     # Módulos de Negocio Desacoplados (Bounded Contexts)
    │
    ├── identidad/                               # BC 1: Autenticación, Usuarios y Roles (DEV 1)
    │   ├── domain/                              # Modelos puros: Usuario, Rol, Value Objects
    │   ├── application/                         # Casos de uso: AuthApplicationService, DTOs
    │   └── infrastructure/                      # AuthController, JWT Providers, Repositorios JPA
    │
    ├── inventario/                              # BC 2: Herramientas, Categorías y Tarifas (DEV 1)
    │   ├── domain/                              # Modelos puros: Herramienta, Tarifa, Categoria
    │   ├── application/                         # Casos de uso: HerramientaApplicationService
    │   └── infrastructure/                      # CatalogoController, HerramientaPersistenceAdapter
    │
    ├── reservas/                                # BC 3: Ciclo de Reservas y Alquileres (DEV 2)
    │   ├── domain/                              # Modelos puros: Reserva, DetalleReserva, RangoFechas
    │   │   ├── ports/in/                        # SolicitarReservaUseCase, GestionarEstadoReservaUseCase
    │   │   └── ports/out/                       # ReservaRepositoryPort, AuditoriaReservaPort
    │   ├── application/                         # ReservaApplicationService, Comandos y DTOs
    │   └── infrastructure/                      # ReservaController, ReservaPersistenceAdapter
    │
    ├── pagos/                                   # BC 4: Pasarela de Pago y Facturación con IVA (DEV 2)
    │   ├── domain/                              # Modelos: Pago, Factura, DetalleFactura, MetodoPago
    │   │   ├── ports/in/                        # ProcesarPagoUseCase, ConsultarFacturaUseCase
    │   │   └── ports/out/                       # PagoRepositoryPort, FacturaRepositoryPort
    │   ├── application/                         # PagoApplicationService (Cálculo 19% IVA)
    │   └── infrastructure/                      # PagoController, FacturaController, Adaptadores
    │
    ├── devoluciones/                            # BC 5: Retorno, Reporte de Daños en 4FN y Mora (DEV 2)
    │   ├── domain/                              # Modelos: Devolucion, ReporteDano, EvidenciaDano
    │   │   ├── ports/in/                        # RegistrarDevolucionUseCase, GestionarReporteDanoUseCase
    │   │   └── ports/out/                       # DevolucionRepositoryPort, HerramientaPort, ReservaPort
    │   ├── application/                         # DevolucionApplicationService (Liquidación y Mora)
    │   └── infrastructure/                      # DevolucionController, Adaptadores de Persistencia
    │
    └── reportes/                                # BC 6: Métricas Financieras y Flota (DEV 2)
        ├── domain/                              # Modelos: ResumenFinanciero, MetricasOperativas
        │   ├── ports/in/                        # ConsultarReportesUseCase
        │   └── ports/out/                       # ReporteRepositoryPort
        ├── application/                         # ReporteApplicationService (Dashboard y Rankings)
        └── infrastructure/                      # ReporteController, ReportePersistenceAdapter
```

### 🗄️ Modelo de Datos y Cuarta Forma Normal (4FN)

El diseño de la base de datos elimina toda anomalía de inserción, borrado y actualización cumpliendo rigurosamente la **Cuarta Forma Normal (4FN)**:
- **Desacoplamiento Multivaluado:** Toda dependencia multivaluada $X \twoheadrightarrow Y \mid Z$ ha sido descompuesta en relaciones independientes.
- **Herramientas:** Las categorías (`herramientas_categorias`), imágenes (`herramientas_imagenes`) y modalidades tarifarias (`tarifas_herramienta`) residen en tablas separadas.
- **Devoluciones y Daños:** Los reportes de averías desacoplan la lista multivaluada de fotografías de prueba mediante `evidencias_dano` (`url_foto`, `descripcion_foto`, `fecha_subida`), permitiendo auditorías forenses precisas.
- **Dataset de Calidad:** Más de **20 registros completos por entidad** en `database/data_20_records.sql`.

[Volver al inicio ↑](#-plataforma-de-renta-de-herramientas-y-equipos-de-construcción-)

---

## 📋 Prerrequisitos

Asegúrese de contar con las siguientes herramientas instaladas:
- **Java Development Kit (JDK 21 LTS):** Comprobar con `javac -version`.
- **MySQL Server 8.0+** (o motor Docker).
- **Git** para clonar el repositorio.
- *(Opcional)* **Docker & Docker Compose** para despliegue automatizado.

[Volver al inicio ↑](#-plataforma-de-renta-de-herramientas-y-equipos-de-construcción-)

---

## 🚀 Instalación y Puesta en Marcha

### Opción 1: Ejecución Local en Windows

#### 1. Clonar el Repositorio
```cmd
git clone https://github.com/santiagoosorio0708e-wq/Proyecto_RentaHerramientas_OsorioDaniel_GarciaCamilo.git
cd Proyecto_RentaHerramientas_OsorioDaniel_GarciaCamilo
```

#### 2. Cargar la Base de Datos en MySQL
Ejecute los scripts de esquema y datos con normalización 4FN en orden estricto:
```cmd
mysql -u root -p < database\schema_4fn.sql
mysql -u root -p < database\schema_identidad_inventario_4fn.sql
mysql -u root -p < database\data_20_records.sql
mysql -u root -p < database\data_identidad_inventario.sql
```

> **Nota de Seguridad:** Las contraseñas de los usuarios de prueba en el dataset vienen preconfiguradas con hash BCrypt para `Password123`.

#### 3. Configurar Variables de Entorno (Opcional si usa valores locales por defecto)
```cmd
set DB_URL=jdbc:mysql://localhost:3306/renta_herramientas_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
set DB_USERNAME=root
set DB_PASSWORD=su_clave_mysql
set JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```

#### 4. Compilar y Ejecutar el Backend
```cmd
.\mvnw.cmd spring-boot:run
```
El servidor arrancará en: **`http://localhost:8080/api/v1`**  
Documentación Swagger UI: **`http://localhost:8080/api/v1/swagger-ui.html`**

#### 5. Servir el Frontend Web
En una nueva terminal, levante el servidor HTTP estático (por ejemplo con Python o extensión Live Server):
```cmd
cd frontend
python -m http.server 5500
```
Abra en su navegador: **`http://localhost:5500`**

---

### Opción 2: Despliegue con Docker y Docker Compose

Levanta automáticamente la base de datos MySQL 8 inicializada y la aplicación Spring Boot compilada en multi-stage build:

```cmd
docker compose up --build
```
Para detener los servicios:
```cmd
docker compose down
```

[Volver al inicio ↑](#-plataforma-de-renta-de-herramientas-y-equipos-de-construcción-)

---

## 💻 Uso y Características

### 1. Identidad y Control de Acceso (RBAC)
- **`POST /api/v1/auth/login`**: Autenticación con credenciales y entrega de Access Token + Refresh Token.
- **`POST /api/v1/auth/registro/cliente`**: Alta de clientes particulares o contratistas.
- **`POST /api/v1/auth/registro/proveedor`**: Registro de empresas proveedoras de maquinaria.

**Credenciales de Prueba del Sistema:**
| Rol | Correo Electrónico | Contraseña |
|---|---|---|
| **Administrador** | `admin.perez@rentatool.com` | `Password123` |
| **Proveedor** | `ventas@maquiantioquia.com` | `Password123` |
| **Cliente** | `camilo.garcia@gmail.com` | `Password123` |

---

### 2. Catálogo e Inventario
- **`GET /api/v1/catalogo`**: Catálogo abierto al público con filtros por marca, modelo, categoría, rango de tarifas diarias y ciudad.
- **`POST /api/v1/herramientas`**: Registro de nueva maquinaria (exclusivo para proveedores autenticados).

---

### 3. Reservas y Alquileres
- **`POST /api/v1/reservas`**: El cliente solicita el alquiler con fechas y depósito de garantía.
- **`GET /api/v1/reservas/mis-reservas`**: Consulta personalizada de reservas del cliente autenticado.
- **`PATCH /api/v1/reservas/{id}/aprobar`**: El proveedor aprueba la solicitud técnica.
- **`PATCH /api/v1/reservas/{id}/iniciar`**: Despacho físico de la maquinaria hacia la obra (`EN_CURSO`).

---

### 4. Pagos y Facturación Electrónica (IVA 19%)
- **`POST /api/v1/pagos/procesar`**: Simulación de pasarela segura (Tarjetas, PSE, Transferencia).
- **Cálculo Automático de Impuestos:**
  $$\text{Subtotal Base} = \frac{\text{Monto Total}}{1.19}, \quad \text{IVA (19\%)} = \text{Monto Total} - \text{Subtotal Base}$$
- **`GET /api/v1/facturas/pago/{idPago}`**: Emisión y consulta de factura electrónica legal con numeración consecutiva.

---

### 5. Devoluciones, Daños y Evidencias 4FN
- **`POST /api/v1/devoluciones`**: Registro de check-in de retorno:
  - Comparación de fechas: si la entrega es posterior a la pactada, liquida automáticamente **Cargo por Mora**.
  - Inspección de averías: categorización (`MECANICO`, `ELECTRICO`, `ESTETICO`, `IRREPARABLE`).
  - **Soporte de Evidencias en 4FN:** Registro de múltiples enlaces fotográficos con descripción forense.
  - Actualización de maquinaria: cambia automáticamente el estado del equipo a `DISPONIBLE`, `EN_MANTENIMIENTO` o `FUERA_DE_SERVICIO`.
- **`POST /api/v1/devoluciones/cargos/{idCargo}/pagar`**: Liquidación de cargos adicionales por reparaciones o mora.

---

### 6. Dashboard Analítico y Reportes Ejecutivos
- **`GET /api/v1/reportes/dashboard`**: Vista gerencial integral en formato JSON consumida por el frontend (`dashboard.html`).
- **`GET /api/v1/reportes/financiero`**: Recaudo total, base gravable, IVA legal y penalizaciones.
- **`GET /api/v1/reportes/herramientas-populares?limite=10`**: Ranking de maquinaria más demandada.
- **`GET /api/v1/reportes/clientes-frecuentes?limite=10`**: Clientes con mayor facturación acumulada.
- **`GET /api/v1/reportes/metricas-operativas`**: Disponibilidad de flota y tasa porcentual de averías.

[Volver al inicio ↑](#-plataforma-de-renta-de-herramientas-y-equipos-de-construcción-)

---

## 🧪 Pruebas Automatizadas

El proyecto cuenta con **41 pruebas unitarias y de integración** que validan exhaustivamente la lógica de dominio y los casos de uso:

```cmd
.\mvnw.cmd test
```

### Resumen de Suites de Pruebas:
- **`AuthApplicationServiceTest` (11 tests):** Registro, login, refresh token, validación de credenciales y roles.
- **`HerramientaApplicationServiceTest` (15 tests):** CRUD de inventario, cambio de estados operativos y control de pertenencia.
- **`ReservaApplicationServiceTest` (4 tests):** Transiciones de estado, validación de fechas y cálculo de depósitos.
- **`PagoApplicationServiceTest` (3 tests):** Aprobación de pasarela y desglose de IVA del 19%.
- **`DevolucionApplicationServiceTest` (4 tests):** Cálculo de mora por días de retraso, penalizaciones por daños y cambios de estado de maquinaria.
- **`ReporteApplicationServiceTest` (3 tests):** Métricas de facturación, consolidado de KPIs y rankings de maquinaria.
- **`RentaHerramientasApplicationTests` (1 test):** Carga completa del contexto Spring Boot y adaptadores JPA en memoria (H2).

```text
[INFO] Results:
[INFO] Tests run: 41, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

[Volver al inicio ↑](#-plataforma-de-renta-de-herramientas-y-equipos-de-construcción-)

---

## 📮 Pruebas con Postman

En la carpeta [`postman/`](postman/) se incluyen las colecciones oficiales listas para importar:
1. `RentaHerramientas-Identidad-Inventario.postman_collection.json` (Módulos DEV 1).
2. `RentaHerramientas-DEV2-Reservas-Pagos-Devoluciones-Reportes.postman_collection.json` (Módulos DEV 2).
3. `RentaHerramientas.postman_environment.json`: Entorno con variables configuradas (`base_url`, `access_token`, `refresh_token`).

[Volver al inicio ↑](#-plataforma-de-renta-de-herramientas-y-equipos-de-construcción-)

---

## 🗺️ Hoja de Ruta (Roadmap)

- [x] **Fase 1: Fundaciones y Arquitectura**
  - [x] Configuración de Spring Boot 3.4 y Java 21 LTS con arquitectura hexagonal.
  - [x] Diseño relacional estricto en Cuarta Forma Normal (4FN).
  - [x] Scripts DDL y DML con más de 20 registros por tabla (`schema_4fn.sql`, `data_20_records.sql`).
- [x] **Fase 2: Identidad y Catálogo (DEV 1)**
  - [x] Seguridad JWT con roles y refresh tokens.
  - [x] Catálogo público con filtros y administración de inventario.
- [x] **Fase 3: Reservas y Alquileres (DEV 2)**
  - [x] Bounded context de reservas con auditoría de estados.
  - [x] Reglas de rango de fechas y depósito en garantía.
- [x] **Fase 4: Pagos y Facturación (DEV 2)**
  - [x] Integración de pasarela simulada con métodos de pago configurables.
  - [x] Facturación electrónica con desglose del 19% de IVA legal.
- [x] **Fase 5: Devoluciones, Daños y Cargos (DEV 2)**
  - [x] Check-in físico con cálculo dinámico de mora por retraso.
  - [x] Reportes de daños con evidencias fotográficas en 4FN y cargos de reparación.
  - [x] Retorno automático de herramientas a estado `DISPONIBLE` o `EN_MANTENIMIENTO`.
- [x] **Fase 6: Analítica y Reportes (DEV 2)**
  - [x] Dashboard ejecutivo con KPIs financieros y operativos.
  - [x] Rankings de herramientas y clientes frecuentes.
- [x] **Fase 7: Frontend Web e Interactividad**
  - [x] Portal de cliente para consulta y reserva en línea (`index.html`, `reservas.html`).
  - [x] Portal de proveedor y control de despachos (`proveedor.html`, `gestion_reservas.html`).
  - [x] Dashboard de métricas gerenciales (`dashboard.html`).
- [x] **Fase 8: Contenedores y Documentación**
  - [x] Dockerfile y Docker Compose funcional para orquestación.
  - [x] Colección Postman completa y actualizada.

[Volver al inicio ↑](#-plataforma-de-renta-de-herramientas-y-equipos-de-construcción-)

---

## 👥 Equipo y Contacto

Proyecto académico desarrollado con altos estándares de ingeniería de software:

- **Daniel Osorio** - *DEV 2 (Reservas, Pagos, Facturación, Devoluciones, Reportes & Frontend)*
- **Camilo García** - *DEV 1 (Identidad, Seguridad JWT, Catálogo, Inventario & Documentación)*

---

<div align="center">
  <sub>Construido con dedicación para la gestión moderna y eficiente de maquinaria de construcción. © 2026</sub>
</div>
