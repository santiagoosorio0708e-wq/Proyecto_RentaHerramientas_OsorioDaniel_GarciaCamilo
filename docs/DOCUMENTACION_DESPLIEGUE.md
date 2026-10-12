# Documentación de despliegue

Plataforma de Renta de Herramientas y Equipos de Construcción

---

Hay tres formas de poner la aplicación a correr. Este documento las cubre todas y al final explica cuál conviene en cada caso.

| Opción | Empaquetado | Cuándo usarla |
|---|---|---|
| A — Tomcat embebido | JAR | Desarrollo y demostraciones. Es la configuración actual del proyecto. |
| B — Docker | JAR dentro de un contenedor | Levantar backend y base de datos con un solo comando |
| C — Tomcat externo | WAR | Cuando la organización ya tiene un Tomcat administrado |

---

## Opción A — Tomcat embebido (JAR)

Es la más directa. Spring Boot trae Tomcat dentro del propio `.jar`, así que no hay que instalar ningún servidor.

### Construir

```bash
mvn clean package
```

Genera `target/renta-herramientas-0.0.1-SNAPSHOT.jar`.

### Ejecutar

```bash
java -jar target/renta-herramientas-0.0.1-SNAPSHOT.jar
```

La aplicación queda en `http://localhost:8080/api/v1`.

### Ejecutar con configuración de producción

```bash
export DB_URL="jdbc:mysql://servidor-db:3306/renta_herramientas_db?useSSL=true&serverTimezone=UTC"
export DB_USERNAME=app_renta
export DB_PASSWORD='clave_segura'
export JWT_SECRET='clave_de_al_menos_32_bytes_generada_aleatoriamente'
export JPA_DDL_AUTO=validate
export APP_CORS_ALLOWED_ORIGINS=https://app.rentatools.com

java -jar renta-herramientas-0.0.1-SNAPSHOT.jar
```

> **`JPA_DDL_AUTO=validate` en producción.** Con `update`, Hibernate puede alterar tablas solo por un cambio en una entidad. Con `validate` únicamente comprueba que el esquema coincida y falla el arranque si no, que es exactamente lo que uno quiere: enterarse del problema antes de que la aplicación empiece a atender peticiones.

### Dejarlo corriendo como servicio en Linux

Crear `/etc/systemd/system/renta-herramientas.service`:

```ini
[Unit]
Description=Plataforma de Renta de Herramientas
After=network.target mysql.service

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/renta-herramientas
ExecStart=/usr/bin/java -jar /opt/renta-herramientas/renta-herramientas.jar
SuccessExitStatus=143
Restart=on-failure
RestartSec=10

Environment="DB_USERNAME=app_renta"
Environment="DB_PASSWORD=clave_segura"
Environment="JWT_SECRET=clave_de_al_menos_32_bytes"
Environment="JPA_DDL_AUTO=validate"

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable renta-herramientas
sudo systemctl start renta-herramientas
sudo systemctl status renta-herramientas

# Ver los logs en vivo
sudo journalctl -u renta-herramientas -f
```

---

## Opción B — Docker

Levanta MySQL y el backend juntos, con los scripts SQL ya cargados.

### Requisitos

- Docker Desktop (Windows/macOS) o Docker Engine + plugin Compose (Linux)

```bash
docker --version
docker compose version
```

### Levantar todo

Desde la raíz del proyecto:

```bash
docker compose up --build
```

La primera vez demora varios minutos: descarga la imagen de Maven, baja las dependencias y compila. Las siguientes son mucho más rápidas porque Docker reutiliza las capas en caché.

Para dejarlo corriendo en segundo plano:

```bash
docker compose up --build -d
```

### Verificar

```bash
docker compose ps          # estado de los contenedores
docker compose logs -f backend
```

Cuando en los logs aparezca `Started RentaHerramientasApplication`, pruebe:

```bash
curl http://localhost:8080/api/v1/catalogo
```

### Comandos útiles

```bash
docker compose stop            # detener sin borrar nada
docker compose start           # volver a arrancar
docker compose down            # detener y eliminar contenedores
docker compose down -v         # además borra el volumen con los datos
docker compose up --build      # reconstruir después de cambiar código
docker compose exec mysql mysql -u root -p renta_herramientas_db
```

### Cómo están armados los archivos

El **Dockerfile** usa build en dos etapas:

1. La etapa `builder` tiene Maven y el JDK completo, compila el proyecto y produce el `.jar`.
2. La imagen final arranca desde un JRE liviano y solo copia el `.jar`.

Así, ni Maven ni el código fuente ni el repositorio `~/.m2` quedan dentro de la imagen final. Además la aplicación corre con un usuario sin privilegios (`appuser`), no como root.

El **docker-compose.yml** define dos servicios en la misma red:

- `mysql` expone el puerto **3307** hacia afuera para no chocar con un MySQL que ya esté instalado en la máquina.
- `backend` espera con `depends_on: condition: service_healthy` a que MySQL realmente responda, no solo a que el contenedor exista. Sin eso, el backend arrancaría primero y fallaría la conexión.
- Dentro de la red de Docker el host de la base de datos es `mysql` (el nombre del servicio), no `localhost`.

### Variables en Docker

Se pueden sobrescribir con un archivo `.env` en la raíz:

```env
DB_ROOT_PASSWORD=mi_clave_segura
JWT_SECRET=clave_de_al_menos_32_bytes_generada_aleatoriamente
APP_CORS_ALLOWED_ORIGINS=http://localhost:5500
```

> El archivo `.env` **no debe subirse al repositorio**. Agréguelo al `.gitignore`.

### Problemas frecuentes con Docker

| Síntoma | Causa y solución |
|---|---|
| `port is already allocated` | Otro proceso usa el 8080 o el 3307. Cambie el mapeo en `docker-compose.yml` |
| Los scripts SQL no se ejecutaron | Solo corren la **primera** vez que se crea el volumen. Use `docker compose down -v` y vuelva a levantar |
| El backend no conecta a MySQL | Verifique que `DB_URL` apunte a `mysql:3306` y no a `localhost` |
| El build falla descargando dependencias | Problema de red o proxy. Reintente con `docker compose build --no-cache` |

---

## Opción C — Tomcat externo (WAR)

Esta opción es para entornos donde ya existe un Tomcat administrado por la organización.

### Paso 1 — Cambiar el empaquetado

En el `pom.xml`, justo debajo de `<version>`, agregar:

```xml
<packaging>war</packaging>
```

### Paso 2 — Marcar Tomcat como `provided`

Agregar esta dependencia:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

`provided` significa que la dependencia se usa para compilar, pero no se empaqueta: la va a aportar el servidor donde se despliegue. Sin esto, el WAR llevaría su propio Tomcat dentro del Tomcat que lo ejecuta, y eso genera conflictos de clases.

### Paso 3 — La clase inicializadora

**Ya está incluida** en el proyecto: `src/main/java/com/rentaherramientas/ServletInitializer.java`.

```java
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RentaHerramientasApplication.class);
    }
}
```

Hace falta porque Tomcat, al desplegar un WAR, no ejecuta ningún `main()`. Busca una clase que extienda `SpringBootServletInitializer` para saber cómo arrancar el contexto de Spring. Mientras el proyecto siga empaquetado como JAR, esta clase simplemente no se usa.

### Paso 4 — Generar el WAR

```bash
mvn clean package
```

Produce `target/renta-herramientas-0.0.1-SNAPSHOT.war`.

### Paso 5 — Desplegar

```bash
cp target/renta-herramientas-0.0.1-SNAPSHOT.war /opt/tomcat/webapps/renta.war
```

Tomcat detecta el archivo y lo despliega solo, expandiéndolo en `webapps/renta/`.

### Paso 6 — Atención con el context path

Aquí hay un detalle que confunde a mucha gente. Con el WAR se **acumulan dos prefijos**:

| Componente | Prefijo |
|---|---|
| Nombre del WAR (`renta.war`) | `/renta` |
| `server.servlet.context-path` del `application.properties` | `/api/v1` |

Entonces la URL real queda:

```
http://localhost:8080/renta/api/v1/catalogo
```

Y Swagger en:

```
http://localhost:8080/renta/api/v1/swagger-ui.html
```

Dos formas de evitar la duplicación:

- Renombrar el WAR a `ROOT.war`, con lo cual el context path del WAR desaparece y queda `http://localhost:8080/api/v1/catalogo`. Pero hay que mover antes la aplicación ROOT que Tomcat trae por defecto.
- Quitar `server.servlet.context-path` del `application.properties` y dejar que el nombre del WAR sea el único prefijo.

**Si cambia el context path, hay que actualizar también `URL_BASE` en `frontend/js/api.js`.**

### Paso 7 — Variables de entorno en Tomcat

Crear `/opt/tomcat/bin/setenv.sh`:

```bash
#!/bin/sh
export DB_URL="jdbc:mysql://localhost:3306/renta_herramientas_db?useSSL=false&serverTimezone=UTC"
export DB_USERNAME="app_renta"
export DB_PASSWORD="clave_segura"
export JWT_SECRET="clave_de_al_menos_32_bytes"
export JPA_DDL_AUTO="validate"
export APP_CORS_ALLOWED_ORIGINS="https://app.rentatools.com"
```

```bash
chmod +x /opt/tomcat/bin/setenv.sh
```

Tomcat ejecuta este archivo automáticamente al arrancar.

### Paso 8 — Arrancar y revisar

```bash
/opt/tomcat/bin/startup.sh
tail -f /opt/tomcat/logs/catalina.out
```

Para detenerlo:

```bash
/opt/tomcat/bin/shutdown.sh
```

### Nota sobre la versión de Tomcat

Spring Boot 3 usa `jakarta.servlet`, no `javax.servlet`. Eso exige **Tomcat 10.1 o superior**. Un Tomcat 9 va a fallar con `ClassNotFoundException` sobre clases de Jakarta.

### Problemas frecuentes con Tomcat

| Síntoma | Causa |
|---|---|
| `404` en todos los endpoints | Olvidó el context path del WAR en la URL |
| `ClassNotFoundException: jakarta.servlet...` | Tomcat 9 o anterior. Actualice a 10.1+ |
| La aplicación no arranca, `BeanCreationException` | Casi siempre es la base de datos o una variable de entorno faltante. Revise `catalina.out` |
| El WAR anterior sigue activo | Borre la carpeta expandida `webapps/renta/` antes de copiar el nuevo WAR |

---

## Despliegue del frontend

El frontend es estático: no necesita compilación ni servidor de aplicaciones. Basta copiarlo a cualquier servidor web.

### Con Nginx

```nginx
server {
    listen 80;
    server_name app.rentatools.com;

    root /var/www/renta-frontend;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # Proxy hacia el backend para evitar CORS por completo
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Si usa este esquema de proxy, en `frontend/js/api.js` cambie:

```javascript
const URL_BASE = '/api/v1';
```

Al quedar frontend y backend bajo el mismo origen, CORS deja de ser un problema porque ya no hay petición entre orígenes distintos.

### Con Apache HTTP Server

```apache
<VirtualHost *:80>
    ServerName app.rentatools.com
    DocumentRoot /var/www/renta-frontend

    ProxyPreserveHost On
    ProxyPass        /api/ http://localhost:8080/api/
    ProxyPassReverse /api/ http://localhost:8080/api/
</VirtualHost>
```

---

## Lista de verificación antes de desplegar

```
[ ] Las pruebas pasan:            mvn clean test
[ ] El artefacto se genera:       mvn clean package
[ ] Java del servidor es 17+:     java -version
[ ] La base de datos existe y tiene los 4 scripts ejecutados
[ ] JPA_DDL_AUTO está en validate (no en update ni create)
[ ] JWT_SECRET es una clave nueva, de 32+ bytes, NO la de desarrollo
[ ] DB_PASSWORD viene de variable de entorno, no del properties
[ ] APP_CORS_ALLOWED_ORIGINS tiene el dominio real, sin comodín "*"
[ ] El puerto está libre
[ ] HTTPS configurado (los JWT viajan en claro sobre HTTP)
[ ] Se revisaron los logs después del arranque
[ ] Se probó el login y un endpoint protegido
```

---

## Qué revisar si algo falla

El orden que más rápido lleva al problema:

1. **¿Arrancó la aplicación?** Busque `Started RentaHerramientasApplication` en los logs. Si no está, el fallo es de arranque: casi siempre base de datos o una variable faltante.
2. **¿Responde el endpoint público?** `curl http://localhost:8080/api/v1/catalogo`. Si esto funciona, el backend está vivo y el problema es de autenticación o de CORS.
3. **¿Funciona el login?** Pruébelo desde Postman, que no aplica CORS. Si en Postman funciona y en el navegador no, el problema es de CORS, no del backend.
4. **¿Qué código HTTP devuelve?**
   - `401` → el token falta, venció o está mal formado
   - `403` → el token es válido pero el rol no alcanza
   - `404` → revise el context path
   - `500` → mire los logs del servidor, el detalle técnico queda ahí
