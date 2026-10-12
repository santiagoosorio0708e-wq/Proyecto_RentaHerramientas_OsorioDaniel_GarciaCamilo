# Plataforma de Renta de Herramientas y Equipos de Construcción

API REST y frontend para que proveedores publiquen maquinaria de construcción y los clientes la alquilen: catálogo, reservas, pagos, facturación y devoluciones.

El proyecto está construido con **Spring Boot 3.4**, **arquitectura hexagonal + DDD** y una base de datos **MySQL normalizada en 4FN**.

**Equipo:**

| Integrante | Módulos |
|---|---|
| Daniel Osorio | Reservas, Pagos, Facturación, Devoluciones, Reportes |
| Camilo García | Identidad y Autenticación, Catálogo de Herramientas, Inventario |

---

## Tabla de contenido

1. [Tecnologías](#1-tecnologías)
2. [Arquitectura](#2-arquitectura)
3. [Puesta en marcha](#3-puesta-en-marcha)
4. [Configuración](#4-configuración)
5. [Modelo de datos](#5-modelo-de-datos)
6. [Autenticación y roles](#6-autenticación-y-roles)
7. [Endpoints](#7-endpoints)
8. [Frontend](#8-frontend)
9. [Pruebas](#9-pruebas)
10. [Documentación adicional](#10-documentación-adicional)

---

## 1. Tecnologías

### Backend

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje |
| Spring Boot | 3.4.1 | Framework base |
| Spring Web MVC | — | API REST |
| Spring Data JPA | — | Persistencia (Hibernate) |
| Spring Security | — | Autenticación y autorización |
| JJWT | 0.12.6 | Generación y validación de JWT |
| Spring Validation | — | Validación de DTOs |
| springdoc-openapi | 2.8.3 | Documentación Swagger |
| MySQL Connector/J | — | Driver de base de datos |
| Lombok | — | Reducción de código repetitivo |
| JUnit 5 + Mockito | — | Pruebas unitarias |

### Frontend

HTML5, CSS3 y JavaScript puro (ES6+). Sin frameworks ni build: se abre directamente desde cualquier servidor estático.

### Base de datos

MySQL 8.0, normalizada hasta cuarta forma normal.

---

## 2. Arquitectura

### Hexagonal + DDD por módulos

El código se organiza por **bounded context**, y dentro de cada uno por capas:

```
src/main/java/com/rentaherramientas/
│
├── common/                         Código compartido
│   ├── config/                     SecurityConfig, OpenApiConfig
│   ├── dto/                        ApiResponseDto
│   └── exception/                  Excepciones y manejador global
│
└── modules/
    ├── identidad/                  BC: auth-usuarios
    ├── inventario/                 BC: herramientas-inventario
    └── reservas/                   BC: reservas y alquileres
```

Cada módulo repite la misma estructura interna:

```
<modulo>/
├── domain/                 Clases puras de negocio, SIN Spring ni JPA
│   ├── model/              Entidades y Value Objects
│   └── ports/
│       ├── in/             Qué puede hacer el módulo (casos de uso)
│       └── out/            Qué necesita del exterior (repositorios)
│
├── application/            Orquestación de los casos de uso
│   ├── dto/                Contratos de entrada y salida
│   └── service/            Implementación de los puertos de entrada
│
└── infrastructure/         Detalles técnicos
    └── adapters/
        ├── in/web/         Controladores REST
        └── out/persistence/ Entidades JPA, repositorios, mappers
```

### La idea en una frase

El dominio no sabe que existe MySQL, ni Spring, ni HTTP. Define **interfaces** (puertos) con lo que necesita, y la infraestructura las **implementa** (adaptadores).

```
HTTP  →  Controller  →  Puerto IN  →  Servicio  →  Puerto OUT  →  Adaptador  →  MySQL
         (adaptador)   (interfaz)    (aplicación)  (interfaz)     (JPA)
```

El beneficio concreto: cambiar MySQL por PostgreSQL significa escribir otro adaptador. El dominio y los casos de uso no se tocan. Y las pruebas unitarias corren sin levantar Spring ni base de datos, porque los puertos se reemplazan por mocks.

### Comunicación entre módulos

El módulo de Inventario necesita saber quién es el proveedor que pide una operación, pero **no importa las clases de Identidad**. Lo resuelve con un puerto anticorrupción:

```
inventario/domain/ports/out/ProveedorPort.java          ← la interfaz que Inventario necesita
inventario/infrastructure/adapters/out/identidad/
    ProveedorAdapter.java                                ← el único punto que conoce Identidad
```

Si mañana Identidad se convierte en un microservicio aparte, solo hay que reescribir ese adaptador para que haga una llamada HTTP.

---

## 3. Puesta en marcha

### Requisitos

- JDK 21 (mínimo 17)
- MySQL 8.0+
- Maven 3.9+ *(opcional: el proyecto trae `mvnw`)*

### Clonar

```bash
git clone https://github.com/santiagoosorio0708e-wq/Proyecto_RentaHerramientas_OsorioDaniel_GarciaCamilo.git
cd Proyecto_RentaHerramientas_OsorioDaniel_GarciaCamilo
```

### Cargar la base de datos

**El orden importa.** Cada script depende del anterior:

```bash
mysql -u root -p < database/schema_4fn.sql
mysql -u root -p < database/schema_identidad_inventario_4fn.sql
mysql -u root -p < database/data_20_records.sql
mysql -u root -p < database/data_identidad_inventario.sql
```

> El cuarto script es obligatorio: además de cargar perfiles, permisos, imágenes y tarifas, reemplaza los hashes de contraseña de ejemplo por hashes BCrypt reales. Sin él, el login siempre responde *Credenciales inválidas*.

Después de ejecutarlo, **todos los usuarios de prueba tienen la contraseña `Password123`**.

### Ejecutar el backend

```bash
mvn spring-boot:run
```

En Windows sin Maven instalado:

```cmd
mvnw.cmd spring-boot:run
```

Queda disponible en `http://localhost:8080/api/v1`.

### Ejecutar el frontend

No se puede abrir con doble clic (el navegador bloquea las peticiones por CORS). Hay que servirlo por HTTP:

```bash
cd frontend
python -m http.server 5500
```

O con la extensión **Live Server** de VS Code sobre `frontend/index.html`.

### Con Docker (alternativa)

Levanta MySQL y el backend juntos:

```bash
docker compose up --build
```

### Verificar que todo funciona

| URL | Debe mostrar |
|---|---|
| http://localhost:8080/api/v1/catalogo | JSON con las herramientas |
| http://localhost:8080/api/v1/swagger-ui.html | Documentación interactiva |
| http://localhost:5500 | El catálogo en el navegador |

---

## 4. Configuración

### `application.properties`

Ninguna credencial está escrita en el archivo. Todo usa `${VARIABLE:valor_por_defecto}`, de modo que el proyecto arranca en local sin configurar nada, pero en producción se sobrescribe con variables de entorno.

```properties
spring.application.name=renta-herramientas

# Servidor
server.port=${SERVER_PORT:8080}
server.servlet.context-path=/api/v1

# Base de datos
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/renta_herramientas_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=${JPA_DDL_AUTO:update}
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Seguridad y JWT
jwt.secret=${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
jwt.expiration-ms=${JWT_EXPIRATION_MS:86400000}
jwt.refresh-expiration-ms=${JWT_REFRESH_EXPIRATION_MS:604800000}

# CORS
app.cors.allowed-origins=${APP_CORS_ALLOWED_ORIGINS:http://localhost:5500,http://127.0.0.1:5500,http://localhost:5173,http://localhost:3000,http://localhost:4200}

# Swagger
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operations-sorter=alpha
springdoc.swagger-ui.tags-sorter=alpha
```

### Variables de entorno

```bash
export DB_USERNAME=root
export DB_PASSWORD=mi_clave
export JWT_SECRET=clave_de_al_menos_32_bytes_generada_aleatoriamente
```

En Windows (CMD):

```cmd
set DB_PASSWORD=mi_clave
```

> `JWT_SECRET` debe tener **al menos 32 bytes**, porque HS256 lo exige. Si es más corta, la aplicación no arranca y lo dice explícitamente.

---

## 5. Modelo de datos

### Diagrama relacional

```mermaid
erDiagram
    usuarios ||--o{ usuarios_roles : tiene
    roles    ||--o{ usuarios_roles : asignado
    roles    ||--o{ roles_permisos : agrupa
    permisos ||--o{ roles_permisos : pertenece

    usuarios ||--o| perfiles_proveedor : "si es proveedor"
    usuarios ||--o| perfiles_cliente   : "si es cliente"

    usuarios   ||--o{ herramientas : publica
    categorias ||--o{ herramientas : clasifica

    herramientas ||--o{ herramientas_categorias   : "4FN categorias"
    categorias   ||--o{ herramientas_categorias   : "4FN categorias"
    herramientas ||--o{ herramientas_imagenes     : "4FN galeria"
    herramientas ||--o{ especificaciones_tecnicas : "4FN ficha tecnica"
    herramientas ||--o{ tarifas_herramienta       : "4FN tarifas"

    usuarios ||--o{ reservas         : solicita
    reservas ||--o{ detalles_reserva : contiene
    herramientas ||--o{ detalles_reserva : "se alquila en"
    reservas ||--o{ auditoria_estados_reserva : registra

    reservas ||--o{ pagos    : genera
    metodos_pago ||--o{ pagos : "se paga con"
    pagos    ||--|| facturas : respalda
    facturas ||--o{ detalles_factura : detalla

    reservas ||--o| devoluciones  : finaliza
    devoluciones ||--o{ reportes_dano : reporta
    reportes_dano ||--o{ evidencias_dano : documenta
    devoluciones ||--o{ cargos_adicionales : genera
```

### Las cinco tablas que explican la 4FN

Una herramienta tiene tres conjuntos de datos **independientes entre sí**: sus categorías, sus fotos y sus tarifas. Guardarlos en una sola tabla obligaría a repetir todas las combinaciones.

Para una herramienta con 2 categorías, 3 fotos y 3 tarifas:

| Diseño | Filas necesarias |
|---|---|
| Todo en una tabla | 2 × 3 × 3 = **18** |
| Normalizado en 4FN | 2 + 3 + 3 = **8** |

Por eso existen `herramientas_categorias`, `herramientas_imagenes`, `especificaciones_tecnicas` y `tarifas_herramienta` como tablas separadas.

El análisis completo está en:
- [`database/JUSTIFICACION_4FN.md`](database/JUSTIFICACION_4FN.md) — reservas, pagos y devoluciones
- [`database/JUSTIFICACION_4FN_IDENTIDAD_INVENTARIO.md`](database/JUSTIFICACION_4FN_IDENTIDAD_INVENTARIO.md) — identidad e inventario

---

## 6. Autenticación y roles

### Cómo funciona

La API es **stateless**: el servidor no guarda sesión. Cada petición debe traer su propia credencial en el header `Authorization`.

```
1. POST /auth/login con correo y contraseña
          ↓
2. AuthApplicationService busca el usuario y compara el hash con BCrypt
          ↓
3. Si coincide, JwtTokenProvider firma un access token y un refresh token
          ↓
4. El frontend los guarda en localStorage
          ↓
5. Cada petición siguiente envía: Authorization: Bearer <access token>
          ↓
6. JwtAuthenticationFilter valida la firma y la expiración,
   carga el usuario y lo deja en el SecurityContext
          ↓
7. Spring Security evalúa si el rol alcanza para ese endpoint
```

### Los dos tokens

| Token | Vigencia | Para qué |
|---|---|---|
| Access token | 24 horas | Se envía en cada petición |
| Refresh token | 7 días | Solo sirve para pedir un nuevo access token |

El filtro los distingue por un claim `tipo`: si alguien intenta usar un refresh token como credencial de acceso, se rechaza.

### Decisiones de seguridad aplicadas

- Las contraseñas se guardan con **BCrypt**, nunca en texto plano. BCrypt agrega una sal aleatoria, por eso dos usuarios con la misma contraseña tienen hashes distintos.
- La clave de firma **no está en el código**: viene de `JWT_SECRET`.
- El payload del JWT solo lleva correo, nombre y roles. **Nunca la contraseña**, porque el payload de un JWT se puede decodificar sin la clave: la firma protege contra modificaciones, no oculta el contenido.
- El login responde el mismo mensaje genérico si el correo no existe o si la contraseña está mal, para no revelar qué correos están registrados.
- CSRF está desactivado porque la autenticación va por header, no por cookie de sesión.

### Roles

| Rol | Puede |
|---|---|
| `ROLE_ADMIN` | Todo: gestionar usuarios, verificar proveedores, administrar categorías e intervenir cualquier herramienta |
| `ROLE_PROVEEDOR` | Administrar **su propio** inventario: alta, edición, fotos, ficha técnica, tarifas y estados |
| `ROLE_CLIENTE` | Consultar el catálogo, su perfil y sus reservas |
| *(sin token)* | Explorar el catálogo público y el listado de categorías |

### Dos niveles de control

**En `SecurityConfig`**, por ruta y método HTTP:

```java
.requestMatchers(HttpMethod.GET, "/catalogo/**").permitAll()
.requestMatchers(HttpMethod.POST, "/herramientas/**").hasAnyRole("PROVEEDOR", "ADMIN")
.requestMatchers(HttpMethod.POST, "/categorias/**").hasRole("ADMIN")
.anyRequest().authenticated()
```

**En cada método**, con `@PreAuthorize`:

```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<...> listarUsuarios(...) { ... }
```

Y además una tercera validación en el servicio de aplicación: un proveedor solo puede tocar herramientas cuyo `id_proveedor` coincida con el suyo. El rol lo autoriza a usar el endpoint; la verificación de propiedad lo limita a sus propios datos.

### 401 vs 403

| Código | Significa |
|---|---|
| `401 Unauthorized` | No sé quién eres: falta el token, está mal formado o venció |
| `403 Forbidden` | Sé quién eres, pero tu rol no alcanza para esto |

### Usuarios de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | `admin.perez@rentatool.com` | `Password123` |
| Proveedor | `ventas@maquiantioquia.com` | `Password123` |
| Cliente | `camilo.garcia@gmail.com` | `Password123` |

---

## 7. Endpoints

> Base: `http://localhost:8080/api/v1`
> Documentación interactiva: `http://localhost:8080/api/v1/swagger-ui.html`

Todas las respuestas comparten la misma envoltura:

```json
{
  "success": true,
  "message": "Operación exitosa",
  "data": { },
  "timestamp": "2026-10-12T14:32:10.123"
}
```

### Autenticación — `/auth`

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/auth/registro/cliente` | Público | Registrar un cliente |
| POST | `/auth/registro/proveedor` | Público | Registrar un proveedor |
| POST | `/auth/login` | Público | Iniciar sesión |
| POST | `/auth/refresh` | Público | Renovar el access token |
| POST | `/auth/logout` | Público | Cerrar sesión (el cliente borra los tokens) |

**Ejemplo — Login**

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "admin.perez@rentatool.com",
  "password": "Password123"
}
```

```json
{
  "success": true,
  "message": "Autenticación exitosa",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tipoToken": "Bearer",
    "expiraEnSegundos": 86400,
    "idUsuario": 1,
    "nombreCompleto": "Carlos Andres Perez",
    "email": "admin.perez@rentatool.com",
    "roles": ["ROLE_ADMIN", "ROLE_SUPER_ADMIN"]
  },
  "timestamp": "2026-10-12T14:32:10.123"
}
```

**Ejemplo — Registro de cliente**

```http
POST /api/v1/auth/registro/cliente
Content-Type: application/json

{
  "nombreCompleto": "Ana Maria Torres",
  "email": "ana.torres@correo.com",
  "password": "Password123",
  "telefono": "3185550120",
  "direccion": "Calle 45 # 12-30, Bucaramanga",
  "tipoDocumento": "CC",
  "numeroDocumento": "1098765440",
  "tipoCliente": "PARTICULAR",
  "ciudadResidencia": "Bucaramanga",
  "cupoCredito": 0
}
```

Responde `201 Created`.

### Usuarios — `/usuarios`

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/usuarios/me` | Autenticado | Mi perfil |
| PUT | `/usuarios/me` | Autenticado | Actualizar mis datos |
| PATCH | `/usuarios/me/password` | Autenticado | Cambiar mi contraseña |
| GET | `/usuarios` | ADMIN | Listar usuarios (filtro `?rol=CLIENTE`) |
| GET | `/usuarios/{id}` | ADMIN | Consultar un usuario |
| PATCH | `/usuarios/{id}/bloquear` | ADMIN | Bloquear cuenta |
| PATCH | `/usuarios/{id}/activar` | ADMIN | Reactivar cuenta |
| PATCH | `/usuarios/{id}/verificar-proveedor` | ADMIN | Verificar documentación |

**Ejemplo — Mi perfil**

```http
GET /api/v1/usuarios/me
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

```json
{
  "success": true,
  "data": {
    "idUsuario": 9,
    "nombreCompleto": "Juan Camilo Garcia",
    "email": "camilo.garcia@gmail.com",
    "telefono": "3185550109",
    "direccion": "Carrera 33 # 48-19, Bucaramanga",
    "tipoDocumento": "CC",
    "numeroDocumento": "1098765433",
    "activo": true,
    "roles": ["ROLE_CLIENTE"],
    "perfilCliente": {
      "idPerfilCliente": 2,
      "tipoCliente": "PARTICULAR",
      "ciudadResidencia": "Bucaramanga",
      "cupoCredito": 500000.00,
      "alquileresRealizados": 3
    }
  }
}
```

Observe que **nunca** aparece el hash de la contraseña: para eso se usan DTOs en lugar de devolver la entidad directamente.

### Catálogo público — `/catalogo`

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/catalogo` | Público | Buscar con filtros |
| GET | `/catalogo/{id}` | Público | Ficha completa |
| GET | `/catalogo/categoria/{idCategoria}` | Público | Por categoría |

Filtros disponibles, todos opcionales y combinables:

```
GET /api/v1/catalogo?texto=martillo&idCategoria=2&precioMinimo=40000&precioMaximo=200000&ubicacion=Medellin&soloDisponibles=true
```

```json
{
  "success": true,
  "message": "Se encontraron 2 herramienta(s)",
  "data": [
    {
      "idHerramienta": 3,
      "nombre": "Martillo Demoledor Hexagonal 30 kg",
      "marca": "Bosch",
      "modelo": "GSH 27 VC",
      "categoriaPrincipal": "Demolicion y Perforacion",
      "estadoOperativo": "DISPONIBLE",
      "disponible": true,
      "tarifaDiaria": 110000.00,
      "imagenPrincipal": "https://cdn.rentatool.com/herramientas/martillo-demoledor-frontal.jpg",
      "idProveedor": 4
    }
  ]
}
```

### Inventario — `/herramientas`

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/herramientas` | PROVEEDOR / ADMIN | Registrar herramienta |
| GET | `/herramientas/mi-inventario` | PROVEEDOR / ADMIN | Mi inventario |
| GET | `/herramientas/{id}` | PROVEEDOR / ADMIN | Detalle |
| PUT | `/herramientas/{id}` | PROVEEDOR / ADMIN | Actualizar |
| DELETE | `/herramientas/{id}` | PROVEEDOR / ADMIN | Eliminar |
| PATCH | `/herramientas/{id}/estado` | PROVEEDOR / ADMIN | Cambiar estado |
| GET | `/herramientas/estado/{estado}` | PROVEEDOR / ADMIN | Filtrar por estado |
| POST | `/herramientas/{id}/imagenes` | PROVEEDOR / ADMIN | Agregar foto |
| POST | `/herramientas/{id}/especificaciones` | PROVEEDOR / ADMIN | Agregar especificación |
| POST | `/herramientas/{id}/tarifas` | PROVEEDOR / ADMIN | Agregar tarifa |
| POST | `/herramientas/{id}/categorias/{idCategoria}` | PROVEEDOR / ADMIN | Asociar categoría |

**Ejemplo — Registrar herramienta**

```http
POST /api/v1/herramientas
Authorization: Bearer <token de proveedor>
Content-Type: application/json

{
  "idCategoriaPrincipal": 2,
  "nombre": "Rotomartillo SDS Plus 800W",
  "descripcion": "Taladro percutor para anclajes en concreto",
  "marca": "Bosch",
  "modelo": "GBH 2-26",
  "numeroSerie": "SN-BSH-7788",
  "tarifaDiaria": 48000,
  "valorReposicion": 2400000,
  "ubicacionBodega": "Pasillo A - Estante 2",
  "categoriasAdicionales": [19],
  "imagenes": [
    {
      "urlFoto": "https://cdn.rentatool.com/herramientas/rotomartillo.jpg",
      "descripcion": "Vista frontal",
      "principal": true,
      "orden": 1
    }
  ],
  "especificaciones": [
    { "atributo": "Potencia", "valor": "830", "unidadMedida": "W" },
    { "atributo": "Peso",     "valor": "2.9", "unidadMedida": "kg" }
  ],
  "tarifasAdicionales": [
    { "tipoTarifa": "SEMANAL", "valor": 288000 }
  ]
}
```

Responde `201 Created` con la ficha completa. La tarifa `DIARIA` se crea sola a partir de `tarifaDiaria`.

**Ejemplo — Cambiar estado**

```http
PATCH /api/v1/herramientas/5/estado
Authorization: Bearer <token de proveedor>
Content-Type: application/json

{
  "nuevoEstado": "EN_MANTENIMIENTO",
  "motivo": "Revisión preventiva de 500 horas"
}
```

Los estados posibles son `DISPONIBLE`, `ALQUILADA`, `EN_MANTENIMIENTO` y `FUERA_DE_SERVICIO`, y **no todas las transiciones están permitidas**. Por ejemplo, de `FUERA_DE_SERVICIO` solo se puede pasar a `EN_MANTENIMIENTO`: un equipo dado de baja tiene que pasar primero por revisión técnica. Una transición inválida responde `422`.

### Categorías — `/categorias`

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/categorias` | Público | Listar |
| GET | `/categorias/{id}` | Público | Consultar |
| POST | `/categorias` | ADMIN | Crear |
| PUT | `/categorias/{id}` | ADMIN | Actualizar |
| DELETE | `/categorias/{id}` | ADMIN | Eliminar (solo si no tiene herramientas) |

### Reservas, pagos y devoluciones

Módulos a cargo de Daniel Osorio: `/reservas`, `/pagos`, `/facturas`, `/devoluciones`, `/reportes`. Están documentados en Swagger.

### Códigos de respuesta

| Código | Cuándo |
|---|---|
| `200 OK` | Consulta o actualización exitosa |
| `201 Created` | Recurso creado |
| `204 No Content` | Eliminación exitosa |
| `400 Bad Request` | Validación fallida o JSON mal formado |
| `401 Unauthorized` | Token ausente, inválido o expirado |
| `403 Forbidden` | Autenticado pero sin permisos |
| `404 Not Found` | El recurso no existe |
| `409 Conflict` | Violación de restricción en base de datos |
| `422 Unprocessable Entity` | Regla de negocio incumplida |
| `500 Internal Server Error` | Error inesperado |

**Ejemplo de error de validación**

```json
{
  "success": false,
  "message": "Error de validación en los campos enviados",
  "data": {
    "email": "El formato del correo electrónico no es válido",
    "password": "La contraseña debe tener entre 8 y 72 caracteres"
  },
  "timestamp": "2026-10-12T14:40:22.871"
}
```

---

## 8. Frontend

```
frontend/
├── index.html          Catálogo público con filtros
├── login.html          Inicio de sesión
├── registro.html       Registro diferenciado cliente / proveedor
├── proveedor.html      Panel de inventario
├── admin.html          Control de usuarios
├── css/styles.css
└── js/
    ├── api.js          Cliente HTTP con interceptor de JWT
    ├── comun.js        Utilidades compartidas
    ├── catalogo.js
    ├── login.js
    ├── registro.js
    ├── proveedor.js
    └── admin.js
```

### El interceptor

Todo pasa por `api.js`. Antes de cada petición adjunta el token automáticamente:

```javascript
const token = obtenerToken();
if (!publica && token) {
    cabeceras['Authorization'] = 'Bearer ' + token;
}
```

Y si el backend responde `401`, intenta refrescar el token **una sola vez** y reintenta la petición original. Si el refresco también falla, borra la sesión y manda al login. Ninguna pantalla tiene que preocuparse por esto.

### Pantallas por rol

| Pantalla | Quién entra |
|---|---|
| `index.html` | Cualquiera, incluso sin cuenta |
| `login.html` / `registro.html` | Cualquiera |
| `proveedor.html` | Proveedor y administrador |
| `admin.html` | Solo administrador |

El login redirige a cada rol a su panel correspondiente.

---

## 9. Pruebas

### Unitarias

```bash
mvn clean test
```

Cubren las reglas de negocio con JUnit 5 y Mockito. No levantan Spring ni base de datos: los puertos se reemplazan por mocks, que es justamente lo que permite la arquitectura hexagonal.

| Clase | Qué verifica |
|---|---|
| `AuthApplicationServiceTest` | Registro, duplicados, política de contraseñas, login, cuenta bloqueada, refresh token |
| `HerramientaApplicationServiceTest` | Alta, proveedor sin verificar, propiedad de la herramienta, transiciones de estado, cotización, galería |
| `ReservaApplicationServiceTest` | Ciclo de vida de las reservas |

Ejecutar una sola clase:

```bash
mvn test -Dtest=AuthApplicationServiceTest
```

### Con Postman

En `postman/` están la colección y el environment:

1. Importar `RentaHerramientas-Identidad-Inventario.postman_collection.json`
2. Importar `RentaHerramientas.postman_environment.json` y seleccionarlo
3. Ejecutar **1. Auth → Login Administrador**: el token se guarda solo en `{{tokenAdmin}}`
4. Hacer lo mismo con *Login Proveedor* y *Login Cliente*
5. El resto de peticiones ya usan el token correcto

La colección incluye los casos que **deben fallar** (403 con rol equivocado, 401 sin token, 404, duplicados, transiciones inválidas), porque probar solo el camino feliz no demuestra que la seguridad funcione.

También se puede correr completa desde la terminal:

```bash
newman run postman/RentaHerramientas-Identidad-Inventario.postman_collection.json \
       -e postman/RentaHerramientas.postman_environment.json
```

### Con Swagger

`http://localhost:8080/api/v1/swagger-ui.html`

Para probar endpoints protegidos: botón **Authorize**, pegar el token (solo el token, sin la palabra *Bearer*) y ya quedan autenticadas todas las peticiones.

---

## 10. Documentación adicional

| Documento | Contenido |
|---|---|
| [`docs/DOCUMENTACION_COMPILACION.md`](docs/DOCUMENTACION_COMPILACION.md) | Requisitos, variables de entorno, compilación y errores frecuentes |
| [`docs/DOCUMENTACION_DESPLIEGUE.md`](docs/DOCUMENTACION_DESPLIEGUE.md) | Despliegue con JAR, Docker y Tomcat externo |
| [`database/JUSTIFICACION_4FN.md`](database/JUSTIFICACION_4FN.md) | Normalización de reservas, pagos y devoluciones |
| [`database/JUSTIFICACION_4FN_IDENTIDAD_INVENTARIO.md`](database/JUSTIFICACION_4FN_IDENTIDAD_INVENTARIO.md) | Normalización de identidad e inventario |

---

## Estructura del repositorio

```
.
├── database/            Scripts SQL y justificaciones de 4FN
├── docs/                Documentación de compilación y despliegue
├── frontend/            Interfaz HTML + CSS + JS
├── postman/             Colección y environment de pruebas
├── src/
│   ├── main/java/       Código fuente
│   ├── main/resources/  application.properties
│   └── test/java/       Pruebas unitarias
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```
