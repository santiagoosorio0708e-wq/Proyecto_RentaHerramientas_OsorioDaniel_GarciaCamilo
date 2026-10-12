# Documentación de compilación

Plataforma de Renta de Herramientas y Equipos de Construcción
Módulos de Identidad, Catálogo e Inventario — Camilo García

---

## 1. Qué se necesita instalado

| Herramienta | Versión | Para qué | Cómo verificar |
|---|---|---|---|
| JDK | 21 (mínimo 17) | Compilar y ejecutar | `java -version` |
| Maven | 3.9+ | Gestionar dependencias y construir | `mvn -version` |
| MySQL | 8.0+ | Base de datos | `mysql --version` |
| Git | cualquiera | Clonar el repositorio | `git --version` |

El proyecto trae el **Maven Wrapper**, así que Maven no es obligatorio. Si no lo tiene instalado, use `mvnw.cmd` (Windows) o `./mvnw` (Linux/macOS) en lugar de `mvn` en todos los comandos de este documento.

### Verificar la versión de Java

```bash
java -version
```

Debe responder algo como:

```
openjdk version "21.0.2" 2024-01-16
```

Si aparece una versión menor a 17, la compilación va a fallar con un error de *release version not supported*. En ese caso descargue el JDK 21 desde [adoptium.net](https://adoptium.net/) y configure `JAVA_HOME`.

---

## 2. Clonar el proyecto

```bash
git clone https://github.com/santiagoosorio0708e-wq/Proyecto_RentaHerramientas_OsorioDaniel_GarciaCamilo.git
cd Proyecto_RentaHerramientas_OsorioDaniel_GarciaCamilo
```

---

## 3. Preparar la base de datos

Los scripts deben ejecutarse **en este orden**. Cada uno depende del anterior:

```bash
mysql -u root -p < database/schema_4fn.sql
mysql -u root -p < database/schema_identidad_inventario_4fn.sql
mysql -u root -p < database/data_20_records.sql
mysql -u root -p < database/data_identidad_inventario.sql
```

| # | Archivo | Qué hace |
|---|---|---|
| 1 | `schema_4fn.sql` | Crea la base de datos y las tablas principales |
| 2 | `schema_identidad_inventario_4fn.sql` | Agrega las tablas 4FN de identidad e inventario |
| 3 | `data_20_records.sql` | Carga los datos base (usuarios, categorías, herramientas, reservas) |
| 4 | `data_identidad_inventario.sql` | Carga perfiles, permisos, imágenes, tarifas y **arregla las contraseñas** |

> **El paso 4 no es opcional.** El script `data_20_records.sql` carga hashes de contraseña de ejemplo que no fueron generados con BCrypt real, así que el login respondería siempre *Credenciales inválidas*. El script 4 los reemplaza por un hash válido y deja a todos los usuarios de prueba con la contraseña `Password123`.

Para confirmar que todo quedó bien, el script 4 termina mostrando una tabla con el conteo de registros. Todas las entidades deben tener 20 o más.

---

## 4. Variables de entorno

El `application.properties` no tiene credenciales escritas directamente: usa la sintaxis `${VARIABLE:valor_por_defecto}`. Eso significa que el proyecto **arranca sin configurar nada** si su MySQL usa usuario `root` sin contraseña, pero en cualquier otro caso hay que definir las variables.

### Variables disponibles

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/renta_herramientas_db?...` | Cadena de conexión |
| `DB_USERNAME` | `root` | Usuario de MySQL |
| `DB_PASSWORD` | *(vacío)* | Contraseña de MySQL |
| `SERVER_PORT` | `8080` | Puerto del backend |
| `JPA_DDL_AUTO` | `update` | Estrategia de Hibernate |
| `JWT_SECRET` | *(clave de desarrollo)* | Clave de firma de los tokens |
| `JWT_EXPIRATION_MS` | `86400000` (24 h) | Vigencia del access token |
| `JWT_REFRESH_EXPIRATION_MS` | `604800000` (7 días) | Vigencia del refresh token |
| `APP_CORS_ALLOWED_ORIGINS` | varios localhost | Orígenes autorizados del frontend |

### Definirlas en Windows (CMD)

```cmd
set DB_USERNAME=root
set DB_PASSWORD=mi_clave_de_mysql
set JWT_SECRET=mi_clave_secreta_de_al_menos_32_bytes_1234567890
```

Ojo: estas variables solo duran mientras la ventana de CMD esté abierta. Si cierra la consola hay que volver a definirlas.

### Definirlas en Windows (PowerShell)

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "mi_clave_de_mysql"
$env:JWT_SECRET  = "mi_clave_secreta_de_al_menos_32_bytes_1234567890"
```

### Definirlas en Linux / macOS

```bash
export DB_USERNAME=root
export DB_PASSWORD=mi_clave_de_mysql
export JWT_SECRET=mi_clave_secreta_de_al_menos_32_bytes_1234567890
```

### Sobre `JWT_SECRET`

La firma usa HS256, que exige una clave de **al menos 256 bits (32 bytes)**. Si pone una clave más corta la aplicación no arranca y muestra:

```
La propiedad jwt.secret debe tener al menos 256 bits (32 bytes) para firmar con HS256
```

Para generar una clave segura:

```bash
# Linux / macOS
openssl rand -base64 48
```

```powershell
# Windows PowerShell
[Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Max 256 }))
```

**La clave real nunca debe subirse al repositorio.** La que está como valor por defecto es solo para desarrollo local.

---

## 5. Compilar

### Compilación rápida (solo verificar que no hay errores)

```bash
mvn clean compile
```

### Compilar y ejecutar las pruebas

```bash
mvn clean test
```

### Construir el paquete completo

```bash
mvn clean package
```

Esto genera `target/renta-herramientas-0.0.1-SNAPSHOT.jar`.

### Saltar las pruebas

```bash
mvn clean package -DskipTests
```

Útil cuando se quiere empaquetar rápido, pero no debería volverse costumbre: las pruebas son justamente las que avisan si algo se rompió.

### Qué hace cada fase

```
clean    → borra la carpeta target/
compile  → compila el código fuente a .class
test     → ejecuta las pruebas unitarias
package  → empaqueta todo en un .jar ejecutable
```

---

## 6. Ejecutar

### Opción A — desde Maven (desarrollo)

```bash
mvn spring-boot:run
```

En Windows sin Maven instalado:

```cmd
mvnw.cmd spring-boot:run
```

### Opción B — desde el JAR

```bash
java -jar target/renta-herramientas-0.0.1-SNAPSHOT.jar
```

### Opción C — pasando configuración en la línea de comandos

```bash
java -jar target/renta-herramientas-0.0.1-SNAPSHOT.jar \
  --spring.datasource.username=root \
  --spring.datasource.password=mi_clave
```

### Verificar que arrancó

Cuando termina de iniciar, la consola muestra:

```
Started RentaHerramientasApplication in 5.432 seconds
```

Y estas URLs deben responder:

| URL | Qué es |
|---|---|
| http://localhost:8080/api/v1/catalogo | Catálogo público (no pide token) |
| http://localhost:8080/api/v1/swagger-ui.html | Documentación interactiva |
| http://localhost:8080/api/v1/v3/api-docs | Especificación OpenAPI en JSON |

> Recuerde el `context-path`: **todas** las rutas llevan el prefijo `/api/v1`. Un `http://localhost:8080/catalogo` sin ese prefijo devuelve 404.

---

## 7. Ejecutar el frontend

El frontend es HTML, CSS y JavaScript puro, sin compilación ni `npm install`. Pero **no se puede abrir con doble clic**: si se abre como `file://`, el navegador bloquea las peticiones al backend por CORS.

Hay que servirlo por HTTP. Tres formas, de la más simple a la más manual:

### Con Visual Studio Code (lo más cómodo)

1. Instale la extensión **Live Server**.
2. Clic derecho sobre `frontend/index.html`.
3. *Open with Live Server*.

Queda en `http://127.0.0.1:5500/frontend/index.html`, que ya está en la lista de orígenes permitidos.

### Con Python

```bash
cd frontend
python -m http.server 5500
```

Y abra `http://localhost:5500`.

### Con Node

```bash
cd frontend
npx serve -l 5500
```

### Si usa otro puerto

Agregue su origen a la variable de CORS antes de arrancar el backend:

```cmd
set APP_CORS_ALLOWED_ORIGINS=http://localhost:5500,http://localhost:8000
```

---

## 8. Errores frecuentes y cómo resolverlos

### `Communications link failure` / `Unknown database`

MySQL no está corriendo o la base de datos no existe.

```bash
# Verificar que el servicio está arriba
mysql -u root -p -e "SHOW DATABASES;"
```

Si no aparece `renta_herramientas_db`, ejecute los scripts del punto 3.

### `Access denied for user 'root'@'localhost'`

La contraseña de MySQL no coincide. Defina `DB_PASSWORD` con la correcta.

### `Port 8080 was already in use`

Otro programa está ocupando el puerto.

```cmd
REM Windows: ver quién lo usa
netstat -ano | findstr :8080
REM y matar el proceso con ese PID
taskkill /PID <numero_pid> /F
```

O simplemente use otro puerto:

```cmd
set SERVER_PORT=8081
```

### `Table 'renta_herramientas_db.permisos' doesn't exist`

Faltó ejecutar `schema_identidad_inventario_4fn.sql` (paso 2 del punto 3).

### El login siempre responde "Credenciales inválidas"

Faltó ejecutar `data_identidad_inventario.sql` (paso 4 del punto 3), que es el que corrige los hashes de contraseña.

### `blocked by CORS policy` en la consola del navegador

El origen desde donde se abrió el frontend no está autorizado. Revise en qué puerto está corriendo y agréguelo a `APP_CORS_ALLOWED_ORIGINS`.

También pasa si abrió el HTML con doble clic (`file://`). Use un servidor local como se explicó en el punto 7.

### `401 Unauthorized` en todos los endpoints

El token no se está enviando o ya venció. Verifique:

1. Que el header sea exactamente `Authorization: Bearer <token>` (con el espacio después de *Bearer*).
2. Que no esté enviando el *refresh token* en lugar del *access token* — el filtro los distingue y rechaza el refresh.
3. Que el token no tenga más de 24 horas.

### Lombok: `cannot find symbol: method getNombre()`

El IDE no tiene habilitado el procesamiento de anotaciones.

- **IntelliJ IDEA:** instale el plugin Lombok y active *Settings → Build → Compiler → Annotation Processors → Enable annotation processing*.
- **Eclipse:** ejecute el instalador de `lombok.jar`.
- **VS Code:** instale *Extension Pack for Java* y *Lombok Annotations Support*.

Desde la línea de comandos con Maven no se necesita nada: Lombok funciona solo.

### La compilación falla descargando dependencias

Revise la conexión a internet. Si está detrás de un proxy corporativo hay que configurarlo en `~/.m2/settings.xml`. Para forzar que vuelva a descargar todo:

```bash
mvn clean package -U
```

---

## 9. Estructura de carpetas

```
Proyecto_RentaHerramientas_OsorioDaniel_GarciaCamilo/
├── database/                 Scripts SQL y justificación 4FN
├── docs/                     Documentación de compilación y despliegue
├── frontend/                 Interfaz HTML + CSS + JS
├── postman/                  Colección y environment de pruebas
├── src/main/java/            Código fuente
├── src/main/resources/       application.properties
├── src/test/java/            Pruebas unitarias
├── pom.xml                   Dependencias y build
├── mvnw / mvnw.cmd           Maven Wrapper
└── README.md
```

---

## 10. Comandos de referencia rápida

```bash
# Verificar entorno
java -version
mvn -version

# Construir
mvn clean compile          # solo compilar
mvn clean test             # compilar y probar
mvn clean package          # construir el jar
mvn clean package -DskipTests

# Ejecutar
mvn spring-boot:run
java -jar target/renta-herramientas-0.0.1-SNAPSHOT.jar

# Diagnóstico
mvn dependency:tree        # ver el árbol de dependencias
mvn clean package -U       # forzar descarga de dependencias
mvn -X clean package       # salida detallada para depurar el build
```
