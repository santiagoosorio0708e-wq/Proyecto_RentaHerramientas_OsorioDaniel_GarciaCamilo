# 🔌 Guía de Integración REST — Módulos DEV 2

**Autor:** Daniel Osorio (DEV 2)  
**Context-Path:** `/api/v1`  
**Base URL:** `http://localhost:8080/api/v1`

---

## 1. Módulo de Reservas (`/reservas`)

### Crear Reserva
```bash
curl -X POST http://localhost:8080/api/v1/reservas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_CLIENTE" \
  -d '{
    "idCliente": 8,
    "fechaInicio": "2026-10-20",
    "fechaFin": "2026-10-25",
    "notasCliente": "Entregar en obra Torre 3",
    "items": [
      {
        "idHerramienta": 1,
        "nombreHerramienta": "Vibrocompactador Tipo Rana",
        "cantidad": 1,
        "precioUnitarioDia": 75000.00
      }
    ]
  }'
```

### Aprobar Reserva
```bash
curl -X PUT http://localhost:8080/api/v1/reservas/1/aprobar \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

### Rechazar Reserva
```bash
curl -X PUT http://localhost:8080/api/v1/reservas/1/rechazar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -d '{
    "motivo": "Sin disponibilidad física en patio central"
  }'
```

---

## 2. Módulo de Pagos y Facturación (`/pagos`, `/facturas`)

### Procesar Pago y Emitir Factura Automática
```bash
curl -X POST http://localhost:8080/api/v1/pagos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_CLIENTE" \
  -d '{
    "idReserva": 1,
    "idCliente": 8,
    "idMetodoPago": 1,
    "monto": 375000.00,
    "tipoPago": "TOTALIDAD",
    "descripcionServicio": "Liquidación total de alquiler de maquinaria"
  }'
```

### Consultar Factura por Número Fiscal
```bash
curl -X GET http://localhost:8080/api/v1/facturas/numero/FAC-20261012-0001 \
  -H "Authorization: Bearer $TOKEN_CLIENTE"
```

---

## 3. Módulo de Devoluciones y Check-in (`/devoluciones`)

### Registrar Check-in Físico con Daño y Evidencia 4FN
```bash
curl -X POST http://localhost:8080/api/v1/devoluciones \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_PROVEEDOR" \
  -d '{
    "idReserva": 1,
    "fechaDevolucionReal": "2026-10-26",
    "recibidoPor": 2,
    "estadoEquipoGeneral": "DANO_LEVE",
    "observaciones": "Retorno con un día de mora y peritaje mecánico",
    "reportesDano": [
      {
        "idHerramienta": 1,
        "tipoDano": "LEVE",
        "descripcionDano": "Fisura en zapata de impacto",
        "costoEstimadoReparacion": 120000.00,
        "responsableAsume": "CLIENTE",
        "evidencias": [
          {
            "urlFoto": "https://storage.googleapis.com/evidencias/zapata-rotura.jpg",
            "descripcionFoto": "Vista frontal de fisura en perno de anclaje"
          }
        ]
      }
    ]
  }'
```

---

## 4. Módulo de Reportes Gerenciales (`/reportes`)

### Dashboard Ejecutivo Consolidado
```bash
curl -X GET http://localhost:8080/api/v1/reportes/dashboard \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

### Top 5 Herramientas Más Alquiladas
```bash
curl -X GET "http://localhost:8080/api/v1/reportes/ranking-herramientas?limite=5" \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```
