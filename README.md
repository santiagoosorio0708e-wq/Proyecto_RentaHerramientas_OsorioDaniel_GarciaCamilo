# Plataforma de Renta de Herramientas y Equipos de Construcción

API REST y frontend para que los proveedores publiquen maquinaria de construcción y los clientes la alquilen. Cubre catálogo, reservas, pagos, facturación y devoluciones.

Stack: Spring Boot 3.4, Java 21, MySQL 8 (normalizado hasta 4FN), arquitectura hexagonal con DDD.

| Integrante | Módulos |
|---|---|
| Daniel Osorio | Reservas, pagos, facturación, devoluciones, reportes |
| Camilo García | Identidad y autenticación, catálogo, inventario |

## Tecnologías

- **Backend:** Spring Web, Spring Data JPA, Spring Security, JJWT 0.12, springdoc-openapi 2.8, Lombok
- **Pruebas:** JUnit 5 y Mockito
- **Base de datos:** MySQL 8
- **Frontend:** HTML, CSS y JavaScript sin frameworks

## Arquitectura

El código se organiza por bounded context y, dentro de cada uno, en tres capas:

```
src/main/java/com/rentaherramientas/
├── common/                  Configuración, excepciones y DTO compartidos
└── modules/
    ├── identidad/           Usuarios, roles y autenticación
    ├── inventario/          Herramientas, categorías y tarifas
    └── reservas/            Reservas y alquileres

<modulo>/
├── domain/                  Modelo de negocio puro y puertos (sin Spring ni JPA)
├── application/             DTO y servicios que implementan los casos de uso
└── infrastructure/          Controladores REST y adaptadores de persistencia
```

El dominio define interfaces (puertos) y la infraestructura las implementa (adaptadores). Por eso las pruebas unitarias corren sin base de datos y cambiar de motor solo requiere un adaptador nuevo.

Inventario necesita conocer al proveedor que hace una operación, pero no importa clases de Identidad: lo resuelve con un puerto anticorrupción (`ProveedorPort`) cuya única implementación es `ProveedorAdapter`.

## Puesta en marcha (Windows)

### Requisitos

- **JDK 21.** Se comprueba con `javac -version`. Si ese comando no existe, solo hay un JRE instalado y Maven fallará con *No compiler is provided*. Instalación: `winget install EclipseAdoptium.Temurin.21.JDK`.
- **MySQL 8** o superior.
- **Git.** Maven no hace falta, el proyecto incluye `mvnw.cmd`.

### Pasos

**1. Clonar**

```cmd
git clone https://github.com/santiagoosorio0708e-wq/Proyecto_RentaHerramientas_OsorioDaniel_GarciaCamilo.git
cd Proyecto_RentaHerramientas_OsorioDaniel_GarciaCamilo
```

**2. Cargar la base de datos.** El orden es obligatorio:

```cmd
mysql -u root -p < database\schema_4fn.sql
mysql -u root -p < database\schema_identidad_inventario_4fn.sql
mysql -u root -p < database\data_20_records.sql
mysql -u root -p < database\data_identidad_inventario.sql
```

El primer script crea la base `renta_herramientas_db`; si ya existe, hay que eliminarla antes. El último reemplaza los hashes de ejemplo por BCrypt válidos, sin él el login falla. Todos los usuarios de prueba usan la contraseña `Password123`.

**3. Definir la contraseña de MySQL** (en la ventana donde se arranca la app):

```cmd
set DB_PASSWORD=su_clave
```

**4. Arrancar el backend**

```cmd
mvnw.cmd spring-boot:run
```

Queda en `http://localhost:8080/api/v1`.

**5. Arrancar el frontend** (en otra ventana). Debe servirse por HTTP, no con doble clic:

```cmd
cd frontend
python -m http.server 5500
```

Para comprobar que todo funciona: `http://localhost:8080/api/v1/swagger-ui.html` y `http://localhost:5500`.

### Con Docker

```cmd
docker compose up --build
```

### Problemas frecuentes

| Síntoma | Solución |
|---|---|
| `No compiler is provided in this environment` | Instalar el JDK 21 (ver requisitos). |
| `Communications link failure` | MySQL apagado o `DB_PASSWORD` incorrecta. |
| Login responde *Credenciales inválidas* | Falta el último script SQL. |
| El frontend no muestra datos | Se abrió con doble clic; servirlo con `python -m http.server 5500`. |

## Configuración

Los valores se leen de variables de entorno. Los valores por defecto son solo para desarrollo local.

| Variable | Descripción | Por defecto |
|---|---|---|
| `DB_URL` | URL JDBC | MySQL local, base `renta_herramientas_db` |
| `DB_USERNAME` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contraseña de MySQL | vacía |
| `JWT_SECRET` | Clave de firma, mínimo 32 bytes | clave de ejemplo |
| `JWT_EXPIRATION_MS` | Vigencia del access token | 24 horas |
| `JWT_REFRESH_EXPIRATION_MS` | Vigencia del refresh token | 7 días |
| `APP_CORS_ALLOWED_ORIGINS` | Orígenes permitidos, separados por coma | puertos locales habituales |
| `SERVER_PORT` | Puerto del servidor | `8080` |

La clave `JWT_SECRET` por defecto es pública porque está en el repositorio. En cualquier entorno real debe reemplazarse por una propia.

## Autenticación y roles

La API es stateless. El cliente inicia sesión en `POST /auth/login`, recibe un access token (24 h) y un refresh token (7 días), y envía el primero en cada petición como `Authorization: Bearer <token>`. Las contraseñas se almacenan con BCrypt.

| Rol | Permisos |
|---|---|
| `ADMIN` | Gestiona usuarios y categorías; puede intervenir cualquier herramienta. |
| `PROVEEDOR` | Administra únicamente su propio inventario. |
| `CLIENTE` | Consulta el catálogo y su perfil. |
| Sin token | Consulta el catálogo y las categorías. |

El acceso se controla en tres niveles: reglas por ruta en `SecurityConfig`, `@PreAuthorize` en los controladores y una verificación de propiedad en el servicio (un proveedor no puede modificar herramientas ajenas). Un `401` indica que falta o venció el token; un `403`, que el rol no alcanza.

| Rol | Correo de prueba |
|---|---|
| Administrador | `admin.perez@rentatool.com` |
| Proveedor | `ventas@maquiantioquia.com` |
| Cliente | `camilo.garcia@gmail.com` |

## Endpoints principales

Base: `http://localhost:8080/api/v1`. El detalle completo, con esquemas y ejemplos, está en Swagger: `/swagger-ui.html`.

| Recurso | Rutas | Acceso |
|---|---|---|
| Autenticación | `POST /auth/registro/cliente`, `/auth/registro/proveedor`, `/auth/login`, `/auth/refresh` | Público |
| Usuarios | `GET`/`PUT /usuarios/me`, `PATCH /usuarios/{id}/bloquear`, `/activar`, `/verificar-proveedor` | Autenticado / ADMIN |
| Catálogo | `GET /catalogo` (filtros: texto, categoría, precio, ubicación, disponibles), `GET /catalogo/{id}` | Público |
| Inventario | `POST`/`PUT`/`DELETE /herramientas`, `GET /herramientas/mi-inventario`, `PATCH /herramientas/{id}/estado` | PROVEEDOR / ADMIN |
| Categorías | `GET /categorias` (público); `POST`/`PUT`/`DELETE` | ADMIN |

Todas las respuestas usan la misma estructura: `success`, `message`, `data` y `timestamp`. Los errores de validación devuelven `400`, las reglas de negocio incumplidas `422` y los recursos inexistentes `404`.

## Base de datos

El esquema está en 4FN. Las categorías, imágenes y tarifas de una herramienta son datos multivaluados independientes entre sí; guardarlos en una sola tabla obligaría a repetir todas sus combinaciones (2×3×3 = 18 filas contra 2+3+3 = 8). Por eso viven en `herramientas_categorias`, `herramientas_imagenes` y `tarifas_herramienta`. El análisis completo está en `database/`.

## Pruebas

```cmd
mvnw.cmd clean test
```

Son 30 pruebas unitarias (11 de autenticación, 15 de inventario y 4 de reservas) que usan mocks de los puertos y no necesitan base de datos. La excepción es `RentaHerramientasApplicationTests`, que arranca la aplicación completa y requiere MySQL en marcha.

En `postman/` hay una colección y un environment. Al ejecutar los *Login*, los tokens se guardan solos en variables (`tokenAdmin`, `tokenProveedor`, `tokenCliente`) y el resto de peticiones los usan. Incluye también los casos que deben fallar (401, 403, 404, transiciones de estado inválidas).

## Documentación adicional

- [`docs/DOCUMENTACION_COMPILACION.md`](docs/DOCUMENTACION_COMPILACION.md): compilación y variables de entorno
- [`docs/DOCUMENTACION_DESPLIEGUE.md`](docs/DOCUMENTACION_DESPLIEGUE.md): despliegue con JAR, Docker y Tomcat
- [`database/JUSTIFICACION_4FN.md`](database/JUSTIFICACION_4FN.md): normalización de reservas, pagos y devoluciones
- [`database/JUSTIFICACION_4FN_IDENTIDAD_INVENTARIO.md`](database/JUSTIFICACION_4FN_IDENTIDAD_INVENTARIO.md): normalización de identidad e inventario
