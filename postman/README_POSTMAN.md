# 📮 Guía de Pruebas con Postman — Módulos DEV 2

**Autor:** Daniel Osorio (DEV 2)  
**Colección:** `postman/RentaHerramientas-DEV2-Reservas-Pagos-Devoluciones-Reportes.postman_collection.json`  
**Entorno:** `postman/RentaHerramientas.postman_environment.json`

---

## 1. Importación en Postman

1. Abra Postman (Desktop o Web).
2. Haga clic en **Import**.
3. Seleccione los archivos:
   - `postman/RentaHerramientas-DEV2-Reservas-Pagos-Devoluciones-Reportes.postman_collection.json`
   - `postman/RentaHerramientas.postman_environment.json`
4. Seleccione el entorno activo **RentaHerramientas - Local**.

---

## 2. Variables de Entorno Clave

| Variable | Valor por Defecto | Propósito |
|---|---|---|
| `baseUrl` | `http://localhost:8080/api/v1` | URL base de la API con context path |
| `token_cliente` | *Auto-capturado o preconfigurado* | Token JWT para endpoints de cliente |
| `token_admin` | *Auto-capturado o preconfigurado* | Token JWT con permisos de administrador |
| `token_proveedor` | *Auto-capturado o preconfigurado* | Token JWT con permisos de proveedor |

---

## 3. Secuencia de Ejecución Recomendada

```
1. [Reservas] Solicitar Nueva Reserva (Cliente)
         │
         ▼
2. [Reservas] Aprobar Reserva (Admin/Proveedor)
         │
         ▼
3. [Pagos] Procesar Pago PSE con Factura (Cliente)
         │
         ▼
4. [Facturas] Consultar Factura y Desglose IVA 19%
         │
         ▼
5. [Devoluciones] Check-in con Fotos en 4FN
         │
         ▼
6. [Reportes] Dashboard Gerencial y KPIs
```

---

## 4. Credenciales de Prueba Rápidas para Autenticación

| Rol | Usuario | Contraseña |
|---|---|---|
| **Administrador** | `admin.perez@rentatool.com` | `Password123` |
| **Proveedor** | `ventas@maquiantioquia.com` | `Password123` |
| **Cliente** | `camilo.garcia@gmail.com` | `Password123` |

Todas las solicitudes protegidas requieren el encabezado:
```http
Authorization: Bearer {{token}}
```

