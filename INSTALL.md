# 🍎 Guía de Instalación y Ejecución en macOS

Bienvenido a la guía rápida para levantar y utilizar la **Plataforma de Renta de Herramientas y Equipos de Construcción** en sistemas **macOS** (compatible con procesadores **Apple Silicon M1/M2/M3/M4** y **Intel x86_64**).

Gracias a la integración de **SQLite 3 nativo**, **no necesitas instalar MySQL ni configurar contenedores de Docker**. Todo el sistema y la permanencia de datos funcionan de forma autónoma con un solo comando.

---

## ⚡ Inicio Rápido (En 2 Pasos)

Si ya tienes Java 21 instalado en tu Mac, abre una terminal en la carpeta del proyecto y ejecuta:

```bash
chmod +x start-mac.sh
./start-mac.sh
```

> **¿Qué hace este script automáticamente?**
> 1. Valida que tengas Java 21 activo.
> 2. Otorga permisos de ejecución al Maven Wrapper (`mvnw`).
> 3. Configura el motor de base de datos **SQLite 3** (`renta_herramientas.db`).
> 4. Inicia el servidor Spring Boot en el puerto `8080`.
> 5. Abre automáticamente tu navegador predeterminado (Safari o Google Chrome) en `http://localhost:8080/api/v1/index.html`.

---

## 📋 Prerrequisitos en macOS

### 1. Instalar Java 21 (LTS)

Si no tienes Java instalado en tu Mac, la forma más rápida y recomendada es a través de **Homebrew**:

```bash
# Instalar Homebrew (si aún no lo tienes)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Instalar OpenJDK 21
brew install openjdk@21

# Enlazar al sistema macOS para que sea detectado globalmente
sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk
```

Verifica la versión instalada:
```bash
java -version
# Debe indicar: openjdk version "21.x.x"
```

---

## 🚀 Opciones de Ejecución

### Opción A: Script Automatizado (Recomendada)
Ideal para iniciar en un clic y que el navegador se abra solo:
```bash
chmod +x start-mac.sh
./start-mac.sh
```

---

### Opción B: Ejecución Manual con Maven Wrapper
Si prefieres ver la salida estándar del servidor en primer plano:

```bash
# 1. Dar permisos de ejecución a Maven Wrapper
chmod +x ./mvnw

# 2. Iniciar el servidor Spring Boot
./mvnw spring-boot:run
```

Una vez que observes el banner de inicio en la terminal:
```text
  ____            _         _____           _     
 |  _ \ ___ _ __ | |_ __ _ |_   _|__   ___ | |___ 
 | |_) / _ \ '_ \| __/ _` |  | |/ _ \ / _ \| / __|
 |  _ <  __/ | | | || (_| |  | | (_) | (_) | \__ \
 |_| \_\___|_| |_|\__\__,_|  |_|\___/ \___/|_|___/
                                                   
 Plataforma de Renta de Herramientas y Construccion
 Spring Boot 3.4.1 | Java 21 LTS | SQLite 3 (Portable / Permanencia 100%)
```

Abre tu navegador en cualquiera de las siguientes URLs:
- **Portal Web Principal:** [http://localhost:8080/api/v1/index.html](http://localhost:8080/api/v1/index.html)
- **Catálogo de Maquinaria:** [http://localhost:8080/api/v1/catalogo.html](http://localhost:8080/api/v1/catalogo.html)
- **Mis Reservas y Facturas:** [http://localhost:8080/api/v1/reservas.html](http://localhost:8080/api/v1/reservas.html)
- **Documentación Swagger / OpenAPI:** [http://localhost:8080/api/v1/swagger-ui.html](http://localhost:8080/api/v1/swagger-ui.html)
- **Diagnóstico y Salud:** [http://localhost:8080/api/v1/health](http://localhost:8080/api/v1/health)

---

## 🔐 Credenciales de Prueba Preconfiguradas

Todos los usuarios maestros vienen sembrados con la contraseña unificada: **`Password123`**

| Rol | Correo Electrónico | Contraseña | Capacidades en la Plataforma |
|---|---|---|---|
| **Administrador** | `admin@rentatools.com` | `Password123` | Gestión global, aprobación de reservas, métricas y reportes BI. |
| **Proveedor** | `proveedor@rentatools.com` | `Password123` | Publicación de herramientas, tarifas y control de inventario. |
| **Cliente Principal** | `cliente@rentatools.com` | `Password123` | Alquiler, pago simulado con PSE/Tarjeta y descarga de factura en PDF. |
| **Cliente Secundario** | `camilo@rentatools.com` | `Password123` | Historial de alquileres finalizados y devoluciones. |

---

## 💾 Permanencia de Datos con SQLite 3

- **Archivo de la Base de Datos:** `renta_herramientas.db` (ubicado en la raíz del proyecto).
- **Sembrado Inteligente:** Si el archivo se elimina o se crea uno nuevo, el componente `DataInitializer` restaura automáticamente las 20 herramientas, 20 categorías, usuarios y reservas iniciales.
- **Persistencia en Vivo:** Todo lo que crees o modifiques en la interfaz web (nuevas reservas, herramientas agregadas, pagos realizados o cambios de estado) **se guarda inmediatamente en el archivo `.db`** y persistirá aunque apagues o reinicies el servidor.

---

## 🛠️ Solución de Problemas Frecuentes en Mac

### 1. "Permission denied" al ejecutar `mvnw` o `start-mac.sh`
Solución:
```bash
chmod +x ./mvnw start-mac.sh
```

### 2. "Port 8080 already in use" (Puerto 8080 ocupado)
Si tienes otro proceso usando el puerto 8080 en macOS:
```bash
# Identificar y liberar el puerto 8080
lsof -ti:8080 | xargs kill -9
```

O puedes cambiar el puerto temporalmente al arrancar:
```bash
SERVER_PORT=8090 ./start-mac.sh
```

### 3. Ejecutar la suite de pruebas automatizadas
Para comprobar la salud del sistema y verificar que los 63 tests pasen al 100%:
```bash
./mvnw test
```
