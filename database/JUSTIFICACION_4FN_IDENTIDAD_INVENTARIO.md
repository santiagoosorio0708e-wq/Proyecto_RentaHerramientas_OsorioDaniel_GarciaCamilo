# Justificación de la normalización en 4FN
## Módulos de Identidad, Catálogo de Herramientas e Inventario

**Responsable:** Camilo García
**Tablas cubiertas:** `permisos`, `roles_permisos`, `usuarios`, `usuarios_roles`, `perfiles_proveedor`, `perfiles_cliente`, `categorias`, `herramientas`, `herramientas_categorias`, `herramientas_imagenes`, `especificaciones_tecnicas`, `tarifas_herramienta`

> Este documento complementa a `JUSTIFICACION_4FN.md`, que cubre las tablas de reservas, pagos y devoluciones.

---

## 1. Qué problema resuelve la 4FN

Las tres primeras formas normales eliminan la redundancia causada por **dependencias funcionales** (una columna que depende de otra). La cuarta forma normal ataca un problema distinto: las **dependencias multivaluadas**.

Una dependencia multivaluada aparece cuando una entidad tiene dos o más conjuntos de valores que son **independientes entre sí**. El síntoma es inconfundible: al meterlos en la misma tabla, el número de filas se multiplica sin que haya información nueva.

Una tabla está en 4FN cuando está en 3FN (en realidad en BCNF) y **no tiene dependencias multivaluadas no triviales**, salvo las que dependen de una superclave.

---

## 2. El caso más claro del proyecto: la herramienta

Una herramienta del catálogo tiene tres conjuntos de datos que no se relacionan entre sí:

| Conjunto | Ejemplo para un martillo demoledor |
|---|---|
| Categorías | Demolición, Herramientas Inalámbricas |
| Fotos | frontal.jpg, detalle.jpg, maletin.jpg |
| Tarifas | Diaria, Semanal, Mensual |

La foto frontal **no tiene nada que ver** con la tarifa semanal, y la categoría "Demolición" no tiene nada que ver con ninguna de las dos.

### Diseño incorrecto (viola la 4FN)

```sql
CREATE TABLE herramientas_mal (
    id_herramienta BIGINT,
    categoria      VARCHAR(80),
    url_foto       VARCHAR(255),
    tipo_tarifa    VARCHAR(20),
    valor_tarifa   DECIMAL(12,2)
);
```

Para representar esa sola herramienta con 2 categorías, 3 fotos y 3 tarifas, la base de datos tendría que guardar **todas las combinaciones posibles**:

```
2 categorías × 3 fotos × 3 tarifas = 18 filas
```

Un fragmento se vería así:

| id | categoria | url_foto | tipo_tarifa |
|----|-----------|----------|-------------|
| 3  | Demolición | frontal.jpg | DIARIA |
| 3  | Demolición | frontal.jpg | SEMANAL |
| 3  | Demolición | frontal.jpg | MENSUAL |
| 3  | Demolición | detalle.jpg | DIARIA |
| 3  | Demolición | detalle.jpg | SEMANAL |
| ... | ... | ... | ... |

Las **anomalías** que esto produce:

- **Inserción:** para agregar una cuarta foto hay que crear 2 × 3 = 6 filas nuevas.
- **Actualización:** para corregir el valor de la tarifa semanal hay que modificar 6 filas; si una se queda sin actualizar, la base de datos queda inconsistente.
- **Eliminación:** al borrar la última categoría se pierden también las fotos y las tarifas.

### Diseño aplicado (en 4FN)

Cada dependencia multivaluada se descompone en su propia tabla:

```sql
herramientas_categorias   (id_herramienta, id_categoria)
herramientas_imagenes     (id_imagen, id_herramienta, url_foto, ...)
tarifas_herramienta       (id_tarifa, id_herramienta, tipo_tarifa, valor, ...)
```

Las mismas 18 filas se convierten en **2 + 3 + 3 = 8 filas**, sin información repetida. Agregar una foto es insertar una sola fila, y cambiar la tarifa semanal es un `UPDATE` sobre un único registro.

> **Comprobación teórica:** la descomposición es válida porque las tres tablas comparten `id_herramienta`, que es clave en la tabla original. Al reunirlas con un `JOIN` se recupera exactamente la información inicial, sin filas de más ni de menos (descomposición sin pérdida de información).

---

## 3. Tabla por tabla

### 3.1 `usuarios_roles` — roles múltiples

Un usuario puede tener varios roles (en los datos de prueba, el usuario 1 es `ROLE_ADMIN` y `ROLE_SUPER_ADMIN`), y un rol lo tienen muchos usuarios.

Guardarlo como `usuarios.roles = 'ADMIN,SUPER_ADMIN'` violaría incluso la **1FN**, porque la columna dejaría de ser atómica y habría que partir texto para consultarla.

```sql
CREATE TABLE usuarios_roles (
    id_usuario BIGINT NOT NULL,
    id_rol     BIGINT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol)
);
```

La clave primaria compuesta es toda la tabla, así que no queda ninguna dependencia multivaluada no trivial: **está en 4FN**.

### 3.2 `roles_permisos` — permisos agrupados por rol

El mismo razonamiento. Permite pasar de un control de acceso por rol a uno por permiso fino (`@PreAuthorize("hasAuthority('HERRAMIENTA_CREAR')")`) sin rediseñar nada.

### 3.3 `perfiles_proveedor` y `perfiles_cliente` — atributos excluyentes

Este caso no es una dependencia multivaluada sino de **atributos opcionales y mutuamente excluyentes**.

Si `razon_social`, `verificado`, `cupo_credito` y `tipo_cliente` vivieran en `usuarios`:

- todos los clientes tendrían `razon_social` y `verificado` en `NULL`;
- todos los proveedores tendrían `cupo_credito` y `tipo_cliente` en `NULL`;
- sería imposible poner `NOT NULL` en campos que sí son obligatorios para su tipo.

Separarlos en dos tablas 1-a-1 opcionales, con `UNIQUE` sobre `id_usuario`, permite declarar `razon_social NOT NULL` y `ciudad_operacion NOT NULL` de verdad, porque esa fila solo existe si el usuario es proveedor.

### 3.4 `herramientas_imagenes` — galería

La alternativa habitual sería `foto1`, `foto2`, `foto3` en la tabla `herramientas`. Eso tiene tres problemas:

1. Viola la 1FN (grupo repetitivo).
2. Limita artificialmente el número de fotos.
3. Desperdicia espacio en las herramientas que tienen una sola.

La tabla aparte, con `UNIQUE (id_herramienta, url_foto)`, admite cualquier cantidad de fotos y evita cargar la misma URL dos veces. La columna `es_principal` marca la portada.

### 3.5 `especificaciones_tecnicas` — ficha técnica variable

Cada tipo de máquina tiene atributos distintos: un andamio no tiene voltaje y un taladro no tiene altura máxima. Una tabla con columnas fijas necesitaría decenas de campos y casi todos quedarían en `NULL`.

El modelo atributo/valor resuelve esto:

```sql
(3, 'Potencia',           '2000', 'W')
(3, 'Energia de impacto', '68',   'J')
(8, 'Carga maxima',       '450',  'kg')
```

`UNIQUE (id_herramienta, atributo)` impide registrar dos veces la potencia de la misma herramienta.

### 3.6 `tarifas_herramienta` — modalidades de cobro

La tabla `herramientas` conserva `tarifa_diaria` como columna porque es el precio de referencia que se muestra en el catálogo y se consulta en cada búsqueda. Las demás modalidades (fin de semana, semanal, mensual) van en la tabla aparte.

`UNIQUE (id_herramienta, tipo_tarifa)` garantiza que no existan dos tarifas semanales contradictorias para el mismo equipo, y `CHECK (valor > 0)` evita precios en cero o negativos.

> **Nota de diseño honesta:** mantener `tarifa_diaria` en las dos partes es una redundancia controlada, hecha a propósito por rendimiento. El riesgo de que se desincronicen se neutraliza en el código: el método `Herramienta.cambiarTarifaDiaria()` actualiza siempre los dos lugares a la vez. Si se prefiriera un diseño 100 % purista, bastaría con eliminar la columna y leer siempre la tarifa `DIARIA` de `tarifas_herramienta`.

---

## 4. Resumen

| Tabla | Dependencia multivaluada que descompone | FN |
|---|---|---|
| `usuarios_roles` | usuario ↠ rol | 4FN |
| `roles_permisos` | rol ↠ permiso | 4FN |
| `perfiles_proveedor` | atributos exclusivos de proveedor | 4FN |
| `perfiles_cliente` | atributos exclusivos de cliente | 4FN |
| `herramientas_categorias` | herramienta ↠ categoría | 4FN |
| `herramientas_imagenes` | herramienta ↠ foto | 4FN |
| `especificaciones_tecnicas` | herramienta ↠ (atributo, valor) | 4FN |
| `tarifas_herramienta` | herramienta ↠ (modalidad, precio) | 4FN |

Las tres dependencias multivaluadas de `herramientas` (categorías, fotos y tarifas) son mutuamente independientes, y cada una quedó aislada en su propia relación. Ninguna tabla del módulo conserva dependencias multivaluadas no triviales, por lo que **todas cumplen la cuarta forma normal**.

---

## 5. Índices añadidos

La normalización reparte la información en más tablas, lo que significa más `JOIN` en las consultas. Para que el catálogo no se degrade se agregaron índices sobre las columnas que más se filtran:

| Índice | Tabla | Motivo |
|---|---|---|
| `idx_herramienta_estado` | `herramientas` | El catálogo filtra por `DISPONIBLE` en cada búsqueda |
| `idx_herramienta_tarifa` | `herramientas` | Filtro por rango de precio |
| `idx_herramienta_proveedor` | `herramientas` | Panel "mi inventario" del proveedor |
| `idx_usuario_email` | `usuarios` | El login busca por correo en cada autenticación |
| `idx_imagen_herramienta` | `herramientas_imagenes` | Obtener la foto de portada de cada tarjeta |
| `idx_tarifa_herramienta` | `tarifas_herramienta` | Leer las tarifas vigentes de un equipo |
