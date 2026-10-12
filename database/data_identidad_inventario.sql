-- ==========================================================================
-- PLATAFORMA DE RENTA DE HERRAMIENTAS Y EQUIPOS DE CONSTRUCCION
--
-- SCRIPT DML - MODULOS DE IDENTIDAD, CATALOGO E INVENTARIO
-- Responsable: Camilo Garcia
--
-- Cumple el requisito de MINIMO 20 REGISTROS POR CADA ENTIDAD a mi cargo.
--
-- Debe ejecutarse DESPUES de:
--   1) database/schema_4fn.sql
--   2) database/schema_identidad_inventario_4fn.sql
--   3) database/data_20_records.sql
--
-- IMPORTANTE SOBRE LAS CONTRASENAS:
--   El script data_20_records.sql carga hashes de ejemplo que NO fueron
--   generados con BCrypt real, por lo que el login siempre fallaria.
--   Mas abajo se actualizan TODOS los usuarios con un hash BCrypt valido.
--   Contrasena de todos los usuarios de prueba: Password123
-- ==========================================================================

USE renta_herramientas_db;

-- --------------------------------------------------------------------------
-- ENTIDAD: permisos (24 registros)
-- --------------------------------------------------------------------------
INSERT INTO permisos (id_permiso, codigo, descripcion) VALUES
(1, 'USUARIO_LEER', 'Consultar la informacion de los usuarios'),
(2, 'USUARIO_CREAR', 'Registrar nuevos usuarios en la plataforma'),
(3, 'USUARIO_EDITAR', 'Modificar los datos de un usuario'),
(4, 'USUARIO_BLOQUEAR', 'Bloquear o reactivar la cuenta de un usuario'),
(5, 'USUARIO_ELIMINAR', 'Eliminar definitivamente una cuenta'),
(6, 'PROVEEDOR_VERIFICAR', 'Validar la documentacion legal de un proveedor'),
(7, 'PROVEEDOR_LEER', 'Consultar los perfiles de proveedor'),
(8, 'CLIENTE_LEER', 'Consultar los perfiles de cliente'),
(9, 'HERRAMIENTA_LEER', 'Consultar el catalogo de herramientas'),
(10, 'HERRAMIENTA_CREAR', 'Registrar una herramienta en el inventario'),
(11, 'HERRAMIENTA_EDITAR', 'Modificar los datos de una herramienta'),
(12, 'HERRAMIENTA_ELIMINAR', 'Eliminar una herramienta del inventario'),
(13, 'HERRAMIENTA_CAMBIAR_ESTADO', 'Cambiar el estado operativo de una herramienta'),
(14, 'CATEGORIA_LEER', 'Consultar las categorias del catalogo'),
(15, 'CATEGORIA_CREAR', 'Crear una categoria nueva'),
(16, 'CATEGORIA_EDITAR', 'Modificar una categoria existente'),
(17, 'CATEGORIA_ELIMINAR', 'Eliminar una categoria sin herramientas asociadas'),
(18, 'TARIFA_GESTIONAR', 'Administrar las tarifas de alquiler'),
(19, 'IMAGEN_GESTIONAR', 'Administrar la galeria de fotos de las herramientas'),
(20, 'ESPECIFICACION_GESTIONAR', 'Administrar la ficha tecnica de las herramientas'),
(21, 'RESERVA_LEER', 'Consultar las reservas de alquiler'),
(22, 'RESERVA_APROBAR', 'Aprobar o rechazar solicitudes de alquiler'),
(23, 'PAGO_LEER', 'Consultar los pagos registrados'),
(24, 'REPORTE_VER', 'Acceder a los reportes y estadisticas');

-- --------------------------------------------------------------------------
-- ENTIDAD: roles_permisos (40 registros)
-- 4FN: descompone la relacion muchos a muchos entre roles y permisos.
-- --------------------------------------------------------------------------
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 7),
(1, 8),
(1, 9),
(1, 10),
(1, 11),
(1, 12),
(1, 13),
(1, 14),
(1, 15),
(1, 16),
(1, 17),
(1, 18),
(1, 19),
(1, 20),
(1, 21),
(1, 22),
(1, 23),
(1, 24),
(2, 9),
(2, 10),
(2, 11),
(2, 12),
(2, 13),
(2, 14),
(2, 18),
(2, 19),
(2, 20),
(2, 21),
(2, 22),
(2, 23),
(3, 9),
(3, 14),
(3, 21),
(3, 23);

-- --------------------------------------------------------------------------
-- ENTIDAD: usuarios (22 registros ADICIONALES)
-- Se agregan para que perfiles_proveedor y perfiles_cliente puedan tener
-- 20 registros cada una respetando su restriccion UNIQUE sobre id_usuario.
-- --------------------------------------------------------------------------
INSERT INTO usuarios (id_usuario, nombre_completo, email, password_hash, telefono, direccion, tipo_documento, numero_documento, activo) VALUES
(21, 'Equipos Pesados del Huila SAS', 'contacto@equipohuila.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3301110001', 'Calle 8 # 12-45, Neiva', 'NIT', '901700001-1', TRUE),
(22, 'Alquileres Tecnicos de Boyaca', 'ventas@alquiboyaca.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3301110002', 'Carrera 11 # 20-30, Tunja', 'NIT', '901700002-2', TRUE),
(23, 'Maquinaria Industrial del Cauca', 'info@maquicauca.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3301110003', 'Calle 5 # 18-22, Popayan', 'NIT', '901700003-3', TRUE),
(24, 'Renta Equipos Nariño Ltda', 'gerencia@rentanarino.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3301110004', 'Carrera 27 # 14-08, Pasto', 'NIT', '901700004-4', TRUE),
(25, 'Herramientas del Magdalena SAS', 'compras@herramagdalena.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3301110005', 'Calle 22 # 3-40, Santa Marta', 'NIT', '901700005-5', TRUE),
(26, 'Equipos Constructivos del Meta', 'contacto@equipometa.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3301110006', 'Avenida 40 # 25-60, Villavicencio', 'NIT', '901700006-6', TRUE),
(27, 'Alquiler Total Caldas SAS', 'info@alquilercaldas.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3301110007', 'Carrera 23 # 62-15, Manizales', 'NIT', '901700007-7', TRUE),
(28, 'Maquinados y Montajes Quindio', 'ventas@maquiquindio.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3301110008', 'Calle 19 # 14-30, Armenia', 'NIT', '901700008-8', TRUE),
(29, 'Proveedora Industrial del Tolima', 'gerencia@proindtolima.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3301110009', 'Carrera 5 # 37-12, Ibague', 'NIT', '901700009-9', TRUE),
(30, 'Equipos y Andamios del Cesar', 'contacto@equiposcesar.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3301110010', 'Calle 16 # 9-55, Valledupar', 'NIT', '901700010-1', TRUE),
(31, 'Rental Tools Risaralda SAS', 'info@rentalrisaralda.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3301110011', 'Avenida 30 de Agosto # 40-20, Pereira', 'NIT', '901700011-2', TRUE),
(32, 'Servicios Tecnicos de Cordoba', 'ventas@sertecordoba.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3301110012', 'Carrera 6 # 62-18, Monteria', 'NIT', '901700012-3', TRUE),
(33, 'Hector Fabio Mejia Maquinaria', 'hfmejia.maquinaria@gmail.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3301110013', 'Calle 44 # 70-25, Barranquilla', 'CC', '7208090100', TRUE),
(34, 'Sandra Milena Cardenas', 'sandra.cardenas@gmail.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3302220001', 'Calle 53 # 24-10, Bucaramanga', 'CC', '6301020301', TRUE),
(35, 'Oscar Ivan Pelaez', 'oscar.pelaez@outlook.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3302220002', 'Carrera 70 # 44-18, Medellin', 'CC', '7101020302', TRUE),
(36, 'Constructora Monteverde SAS', 'proyectos@monteverde.co', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3302220003', 'Calle 98 # 15-30, Bogota', 'NIT', '901800001-1', TRUE),
(37, 'Diana Carolina Rojas', 'diana.rojas@gmail.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3302220004', 'Calle 12 # 8-40, Tunja', 'CC', '5201020303', TRUE),
(38, 'Edificaciones del Oriente Ltda', 'compras@edificaoriente.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3302220005', 'Carrera 33 # 52-14, Bucaramanga', 'NIT', '901800002-2', TRUE),
(39, 'Wilson Andres Beltran', 'wilson.beltran@yahoo.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3302220006', 'Calle 80 # 102-11, Bogota', 'CC', '8001020304', TRUE),
(40, 'Remodelaciones Integrales SAS', 'contacto@remodelintegral.co', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3302220007', 'Avenida 6 # 23-50, Cali', 'NIT', '901800003-3', TRUE),
(41, 'Paola Andrea Suarez', 'paola.suarez@gmail.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3302220008', 'Carrera 50 # 10-22, Barranquilla', 'CC', '4501020305', TRUE),
(42, 'Obras Civiles Arismendi', 'gerencia@obrasarismendi.com', '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e', '3302220009', 'Calle 28 # 16-08, Cucuta', 'NIT', '901800004-4', TRUE);

-- --------------------------------------------------------------------------
-- ENTIDAD: usuarios_roles (27 registros ADICIONALES)
-- --------------------------------------------------------------------------
INSERT INTO usuarios_roles (id_usuario, id_rol) VALUES
(16, 2),
(17, 2),
(18, 3),
(19, 3),
(20, 3),
(21, 2),
(22, 2),
(23, 2),
(24, 2),
(25, 2),
(26, 2),
(27, 2),
(28, 2),
(29, 2),
(30, 2),
(31, 2),
(32, 2),
(33, 2),
(34, 3),
(35, 3),
(36, 3),
(37, 3),
(38, 3),
(39, 3),
(40, 3),
(41, 3),
(42, 3);

-- --------------------------------------------------------------------------
-- ENTIDAD: perfiles_proveedor (20 registros)
-- 4FN: atributos exclusivos del proveedor, separados de la tabla usuarios.
-- --------------------------------------------------------------------------
INSERT INTO perfiles_proveedor (id_usuario, razon_social, nombre_comercial, ciudad_operacion, sitio_web, fecha_vinculacion, verificado, calificacion_promedio) VALUES
(3, 'Constructora Equipos del Norte SAS', 'EquiNorte', 'Chia', 'https://www.equiposnorte.co', '2021-03-15', TRUE, 5),
(4, 'Maquinaria y Andamios Occidente SAS', 'MaquiOccidente', 'Medellin', 'https://www.maquiantioquia.com', '2020-07-02', TRUE, 5),
(5, 'Suministros Civiles de la Sabana SAS', 'SumiCiviles', 'Cota', 'https://www.sumiciviles.com', '2022-01-20', TRUE, 4),
(6, 'RentaTools Santander Ltda', 'RentaTools', 'Bucaramanga', 'https://www.rentasandander.com', '2019-11-08', TRUE, 5),
(7, 'Ferreteria y Maquinaria El Progreso SAS', 'El Progreso', 'Cali', 'https://www.ferreprogreso.co', '2021-09-30', TRUE, 4),
(16, 'Disenos y Cimentaciones Andinas SAS', 'Cimentaciones Andinas', 'Pereira', 'https://www.cimentacionesandinas.co', '2022-05-18', TRUE, 4),
(17, 'Gustavo Quintero Equipos', 'GQ Equipos', 'Cali', NULL, '2023-02-14', FALSE, 3),
(21, 'Equipos Pesados del Huila SAS', 'EquiHuila', 'Neiva', 'https://www.equipohuila.com', '2023-04-10', TRUE, 4),
(22, 'Alquileres Tecnicos de Boyaca SAS', 'AlquiBoyaca', 'Tunja', NULL, '2023-06-22', TRUE, 3),
(23, 'Maquinaria Industrial del Cauca SAS', 'MaquiCauca', 'Popayan', 'https://www.maquicauca.com', '2022-08-05', TRUE, 4),
(24, 'Renta Equipos Nariño Ltda', 'RentaNarino', 'Pasto', NULL, '2023-09-12', FALSE, 3),
(25, 'Herramientas del Magdalena SAS', 'HerraMagdalena', 'Santa Marta', 'https://www.herramagdalena.com', '2021-12-01', TRUE, 5),
(26, 'Equipos Constructivos del Meta SAS', 'EquiMeta', 'Villavicencio', NULL, '2024-01-25', TRUE, 3),
(27, 'Alquiler Total Caldas SAS', 'AlquilerTotal', 'Manizales', 'https://www.alquilercaldas.com', '2022-03-17', TRUE, 4),
(28, 'Maquinados y Montajes Quindio SAS', 'MaquiQuindio', 'Armenia', NULL, '2023-11-09', FALSE, 2),
(29, 'Proveedora Industrial del Tolima SAS', 'ProIndTolima', 'Ibague', 'https://www.proindtolima.com', '2020-10-14', TRUE, 5),
(30, 'Equipos y Andamios del Cesar SAS', 'EquiCesar', 'Valledupar', NULL, '2024-02-28', TRUE, 3),
(31, 'Rental Tools Risaralda SAS', 'RentalRisaralda', 'Pereira', 'https://www.rentalrisaralda.com', '2021-06-11', TRUE, 4),
(32, 'Servicios Tecnicos de Cordoba SAS', 'SerTeCordoba', 'Monteria', NULL, '2023-07-19', FALSE, 3),
(33, 'Hector Fabio Mejia Maquinaria', 'HFM Maquinaria', 'Barranquilla', NULL, '2024-03-05', TRUE, 4);

-- --------------------------------------------------------------------------
-- ENTIDAD: perfiles_cliente (20 registros)
-- 4FN: atributos exclusivos del cliente, separados de la tabla usuarios.
-- --------------------------------------------------------------------------
INSERT INTO perfiles_cliente (id_usuario, tipo_cliente, ciudad_residencia, cupo_credito, fecha_registro, alquileres_realizados) VALUES
(8, 'PARTICULAR', 'Bogota', 0.00, '2023-01-12', 4),
(9, 'PARTICULAR', 'Bucaramanga', 500000.00, '2023-02-20', 3),
(10, 'EMPRESARIAL', 'Bogota', 15000000.00, '2022-05-03', 12),
(11, 'EMPRESARIAL', 'Barranquilla', 12000000.00, '2022-08-17', 9),
(12, 'PARTICULAR', 'Medellin', 800000.00, '2023-04-28', 5),
(13, 'PARTICULAR', 'Bogota', 0.00, '2023-06-09', 2),
(14, 'EMPRESARIAL', 'Buenaventura', 20000000.00, '2021-11-22', 18),
(15, 'PARTICULAR', 'Bogota', 1200000.00, '2023-08-14', 6),
(18, 'EMPRESARIAL', 'Floridablanca', 9000000.00, '2022-10-05', 7),
(19, 'PARTICULAR', 'Bogota', 600000.00, '2023-09-19', 3),
(20, 'PARTICULAR', 'Envigado', 0.00, '2024-01-08', 1),
(34, 'PARTICULAR', 'Bucaramanga', 700000.00, '2024-02-11', 2),
(35, 'PARTICULAR', 'Medellin', 450000.00, '2024-02-25', 1),
(36, 'EMPRESARIAL', 'Bogota', 18000000.00, '2023-03-30', 14),
(37, 'PARTICULAR', 'Tunja', 0.00, '2024-03-16', 1),
(38, 'EMPRESARIAL', 'Bucaramanga', 11000000.00, '2022-12-07', 10),
(39, 'PARTICULAR', 'Bogota', 950000.00, '2024-04-02', 4),
(40, 'EMPRESARIAL', 'Cali', 7500000.00, '2023-05-21', 8),
(41, 'PARTICULAR', 'Barranquilla', 300000.00, '2024-04-18', 2),
(42, 'EMPRESARIAL', 'Cucuta', 6200000.00, '2023-10-26', 6);

-- --------------------------------------------------------------------------
-- ENTIDAD: herramientas_categorias (41 registros)
-- 4FN: una herramienta puede estar en varias categorias, y esa lista es
-- independiente de sus fotos y de sus tarifas.
-- --------------------------------------------------------------------------
INSERT INTO herramientas_categorias (id_herramienta, id_categoria) VALUES
(1, 1),
(1, 15),
(2, 1),
(2, 15),
(3, 2),
(3, 19),
(4, 2),
(4, 13),
(4, 19),
(5, 2),
(5, 3),
(6, 4),
(6, 11),
(7, 4),
(7, 15),
(8, 5),
(8, 17),
(9, 6),
(9, 14),
(10, 7),
(10, 20),
(11, 3),
(11, 8),
(12, 9),
(12, 17),
(13, 7),
(13, 10),
(14, 5),
(14, 11),
(15, 10),
(15, 12),
(16, 6),
(16, 14),
(17, 2),
(17, 15),
(18, 3),
(18, 19),
(19, 10),
(19, 18),
(20, 7),
(20, 20);

-- --------------------------------------------------------------------------
-- ENTIDAD: herramientas_imagenes (40 registros)
-- 4FN: galeria multivaluada en tabla propia, no como columnas foto1/foto2.
-- --------------------------------------------------------------------------
INSERT INTO herramientas_imagenes (id_herramienta, url_foto, descripcion_foto, es_principal, orden_visualizacion) VALUES
(1, 'https://cdn.rentatool.com/herramientas/vibrocompactador-rana-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(1, 'https://cdn.rentatool.com/herramientas/vibrocompactador-rana-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(2, 'https://cdn.rentatool.com/herramientas/apisonador-canguro-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(2, 'https://cdn.rentatool.com/herramientas/apisonador-canguro-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(3, 'https://cdn.rentatool.com/herramientas/martillo-demoledor-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(3, 'https://cdn.rentatool.com/herramientas/martillo-demoledor-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(4, 'https://cdn.rentatool.com/herramientas/rotomartillo-sdsmax-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(4, 'https://cdn.rentatool.com/herramientas/rotomartillo-sdsmax-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(5, 'https://cdn.rentatool.com/herramientas/cortadora-pavimento-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(5, 'https://cdn.rentatool.com/herramientas/cortadora-pavimento-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(6, 'https://cdn.rentatool.com/herramientas/mezcladora-concreto-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(6, 'https://cdn.rentatool.com/herramientas/mezcladora-concreto-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(7, 'https://cdn.rentatool.com/herramientas/vibrador-concreto-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(7, 'https://cdn.rentatool.com/herramientas/vibrador-concreto-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(8, 'https://cdn.rentatool.com/herramientas/andamio-multidireccional-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(8, 'https://cdn.rentatool.com/herramientas/andamio-multidireccional-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(9, 'https://cdn.rentatool.com/herramientas/generador-diesel-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(9, 'https://cdn.rentatool.com/herramientas/generador-diesel-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(10, 'https://cdn.rentatool.com/herramientas/motobomba-autocebante-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(10, 'https://cdn.rentatool.com/herramientas/motobomba-autocebante-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(11, 'https://cdn.rentatool.com/herramientas/soldador-inverter-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(11, 'https://cdn.rentatool.com/herramientas/soldador-inverter-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(12, 'https://cdn.rentatool.com/herramientas/estacion-total-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(12, 'https://cdn.rentatool.com/herramientas/estacion-total-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(13, 'https://cdn.rentatool.com/herramientas/hidrolavadora-industrial-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(13, 'https://cdn.rentatool.com/herramientas/hidrolavadora-industrial-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(14, 'https://cdn.rentatool.com/herramientas/malacate-obra-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(14, 'https://cdn.rentatool.com/herramientas/malacate-obra-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(15, 'https://cdn.rentatool.com/herramientas/equipo-airless-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(15, 'https://cdn.rentatool.com/herramientas/equipo-airless-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(16, 'https://cdn.rentatool.com/herramientas/torre-iluminacion-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(16, 'https://cdn.rentatool.com/herramientas/torre-iluminacion-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(17, 'https://cdn.rentatool.com/herramientas/compresor-aire-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(17, 'https://cdn.rentatool.com/herramientas/compresor-aire-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(18, 'https://cdn.rentatool.com/herramientas/pulidora-angular-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(18, 'https://cdn.rentatool.com/herramientas/pulidora-angular-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(19, 'https://cdn.rentatool.com/herramientas/extractor-axial-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(19, 'https://cdn.rentatool.com/herramientas/extractor-axial-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2),
(20, 'https://cdn.rentatool.com/herramientas/bomba-hidrostatica-frontal.jpg', 'Vista frontal del equipo', TRUE, 1),
(20, 'https://cdn.rentatool.com/herramientas/bomba-hidrostatica-detalle.jpg', 'Detalle de controles y placa de serie', FALSE, 2);

-- --------------------------------------------------------------------------
-- ENTIDAD: especificaciones_tecnicas (60 registros)
-- 4FN: ficha tecnica como pares atributo/valor. Evita una tabla con
-- decenas de columnas que estarian en NULL segun el tipo de maquina.
-- --------------------------------------------------------------------------
INSERT INTO especificaciones_tecnicas (id_herramienta, atributo, valor, unidad_medida) VALUES
(1, 'Potencia', '5.5', 'HP'),
(1, 'Peso', '88', 'kg'),
(1, 'Fuerza centrifuga', '15', 'kN'),
(2, 'Potencia', '4', 'HP'),
(2, 'Peso', '70', 'kg'),
(2, 'Golpes por minuto', '650', 'gpm'),
(3, 'Potencia', '2000', 'W'),
(3, 'Energia de impacto', '68', 'J'),
(3, 'Peso', '30', 'kg'),
(4, 'Potencia', '1500', 'W'),
(4, 'Encastre', 'SDS Max', NULL),
(4, 'Peso', '10', 'kg'),
(5, 'Potencia', '13', 'HP'),
(5, 'Diametro de disco', '14', 'pulgadas'),
(5, 'Profundidad de corte', '125', 'mm'),
(6, 'Capacidad', '1.5', 'bultos'),
(6, 'Potencia', '9', 'HP'),
(6, 'Peso', '180', 'kg'),
(7, 'Potencia', '5.5', 'HP'),
(7, 'Longitud de manguera', '4', 'm'),
(7, 'Diametro de aguja', '45', 'mm'),
(8, 'Dimensiones', '2x2x1.5', 'm'),
(8, 'Carga maxima', '450', 'kg'),
(8, 'Acabado', 'Galvanizado', NULL),
(9, 'Potencia', '7.5', 'kVA'),
(9, 'Combustible', 'Diesel', NULL),
(9, 'Autonomia', '10', 'h'),
(10, 'Diametro', '3', 'pulgadas'),
(10, 'Caudal', '1100', 'L/min'),
(10, 'Potencia', '7', 'HP'),
(11, 'Amperaje', '250', 'A'),
(11, 'Procesos', 'TIG/MIG/MMA', NULL),
(11, 'Voltaje', '220', 'V'),
(12, 'Precision angular', '2', 'segundos'),
(12, 'Alcance con prisma', '3000', 'm'),
(12, 'Peso', '5.2', 'kg'),
(13, 'Presion', '3600', 'PSI'),
(13, 'Caudal', '16', 'L/min'),
(13, 'Combustible', 'Gasolina', NULL),
(14, 'Capacidad de carga', '1000', 'kg'),
(14, 'Longitud de guaya', '60', 'm'),
(14, 'Potencia', '1.5', 'kW'),
(15, 'Presion maxima', '3300', 'PSI'),
(15, 'Caudal', '3.1', 'L/min'),
(15, 'Potencia', '1100', 'W'),
(16, 'Potencia luminica', '1400', 'W'),
(16, 'Altura de mastil', '7', 'm'),
(16, 'Autonomia', '60', 'h'),
(17, 'Caudal de aire', '185', 'CFM'),
(17, 'Presion', '7', 'bar'),
(17, 'Combustible', 'Diesel', NULL),
(18, 'Potencia', '2600', 'W'),
(18, 'Diametro de disco', '9', 'pulgadas'),
(18, 'Velocidad', '6600', 'rpm'),
(19, 'Diametro', '16', 'pulgadas'),
(19, 'Caudal de aire', '3200', 'm3/h'),
(19, 'Voltaje', '110', 'V'),
(20, 'Presion maxima', '50', 'bar'),
(20, 'Capacidad de deposito', '12', 'L'),
(20, 'Peso', '11', 'kg');

-- --------------------------------------------------------------------------
-- ENTIDAD: tarifas_herramienta (58 registros)
-- 4FN: las modalidades de cobro son multivaluadas e independientes de la
-- galeria y de las categorias. La tarifa SEMANAL cobra 7 dias al precio de
-- 6 y la MENSUAL 30 dias al precio de 24.
-- --------------------------------------------------------------------------
INSERT INTO tarifas_herramienta (id_herramienta, tipo_tarifa, valor, activa) VALUES
(1, 'DIARIA', 75000.00, TRUE),
(1, 'SEMANAL', 450000.00, TRUE),
(1, 'MENSUAL', 1800000.00, TRUE),
(2, 'DIARIA', 85000.00, TRUE),
(2, 'SEMANAL', 510000.00, TRUE),
(3, 'DIARIA', 110000.00, TRUE),
(3, 'SEMANAL', 660000.00, TRUE),
(4, 'DIARIA', 55000.00, TRUE),
(4, 'SEMANAL', 330000.00, TRUE),
(4, 'FIN_DE_SEMANA', 99000.00, TRUE),
(5, 'DIARIA', 130000.00, TRUE),
(5, 'SEMANAL', 780000.00, TRUE),
(5, 'MENSUAL', 3120000.00, TRUE),
(6, 'DIARIA', 65000.00, TRUE),
(6, 'SEMANAL', 390000.00, TRUE),
(6, 'MENSUAL', 1560000.00, TRUE),
(7, 'DIARIA', 45000.00, TRUE),
(7, 'SEMANAL', 270000.00, TRUE),
(7, 'FIN_DE_SEMANA', 81000.00, TRUE),
(8, 'DIARIA', 25000.00, TRUE),
(8, 'SEMANAL', 150000.00, TRUE),
(8, 'FIN_DE_SEMANA', 45000.00, TRUE),
(9, 'DIARIA', 150000.00, TRUE),
(9, 'SEMANAL', 900000.00, TRUE),
(9, 'MENSUAL', 3600000.00, TRUE),
(10, 'DIARIA', 60000.00, TRUE),
(10, 'SEMANAL', 360000.00, TRUE),
(10, 'FIN_DE_SEMANA', 108000.00, TRUE),
(11, 'DIARIA', 90000.00, TRUE),
(11, 'SEMANAL', 540000.00, TRUE),
(11, 'FIN_DE_SEMANA', 162000.00, TRUE),
(12, 'DIARIA', 220000.00, TRUE),
(12, 'SEMANAL', 1320000.00, TRUE),
(12, 'MENSUAL', 5280000.00, TRUE),
(13, 'DIARIA', 70000.00, TRUE),
(13, 'SEMANAL', 420000.00, TRUE),
(13, 'FIN_DE_SEMANA', 126000.00, TRUE),
(14, 'DIARIA', 120000.00, TRUE),
(14, 'SEMANAL', 720000.00, TRUE),
(14, 'MENSUAL', 2880000.00, TRUE),
(15, 'DIARIA', 95000.00, TRUE),
(15, 'SEMANAL', 570000.00, TRUE),
(15, 'FIN_DE_SEMANA', 171000.00, TRUE),
(16, 'DIARIA', 280000.00, TRUE),
(16, 'SEMANAL', 1680000.00, TRUE),
(16, 'MENSUAL', 6720000.00, TRUE),
(17, 'DIARIA', 320000.00, TRUE),
(17, 'SEMANAL', 1920000.00, TRUE),
(17, 'MENSUAL', 7680000.00, TRUE),
(18, 'DIARIA', 30000.00, TRUE),
(18, 'SEMANAL', 180000.00, TRUE),
(18, 'FIN_DE_SEMANA', 54000.00, TRUE),
(19, 'DIARIA', 40000.00, TRUE),
(19, 'SEMANAL', 240000.00, TRUE),
(19, 'FIN_DE_SEMANA', 72000.00, TRUE),
(20, 'DIARIA', 35000.00, TRUE),
(20, 'SEMANAL', 210000.00, TRUE),
(20, 'FIN_DE_SEMANA', 63000.00, TRUE);

-- --------------------------------------------------------------------------
-- Ubicacion en bodega de las 20 herramientas existentes
-- --------------------------------------------------------------------------
UPDATE herramientas SET ubicacion_bodega = 'Pasillo A - Estante 1' WHERE id_herramienta = 1;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo B - Estante 1' WHERE id_herramienta = 2;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo C - Estante 1' WHERE id_herramienta = 3;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo D - Estante 1' WHERE id_herramienta = 4;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo E - Estante 1' WHERE id_herramienta = 5;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo A - Estante 2' WHERE id_herramienta = 6;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo B - Estante 2' WHERE id_herramienta = 7;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo C - Estante 2' WHERE id_herramienta = 8;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo D - Estante 2' WHERE id_herramienta = 9;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo E - Estante 2' WHERE id_herramienta = 10;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo A - Estante 3' WHERE id_herramienta = 11;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo B - Estante 3' WHERE id_herramienta = 12;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo C - Estante 3' WHERE id_herramienta = 13;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo D - Estante 3' WHERE id_herramienta = 14;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo E - Estante 3' WHERE id_herramienta = 15;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo A - Estante 4' WHERE id_herramienta = 16;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo B - Estante 4' WHERE id_herramienta = 17;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo C - Estante 4' WHERE id_herramienta = 18;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo D - Estante 4' WHERE id_herramienta = 19;
UPDATE herramientas SET ubicacion_bodega = 'Pasillo E - Estante 4' WHERE id_herramienta = 20;

-- --------------------------------------------------------------------------
-- ACTUALIZACION DE CONTRASENAS A UN HASH BCrypt REAL
--
-- Los hashes cargados en data_20_records.sql son de ejemplo y no validan
-- contra ninguna contrasena, por lo que el endpoint /auth/login siempre
-- respondería 'Credenciales invalidas'.
--
-- Este UPDATE deja a TODOS los usuarios de prueba con la contrasena:
--
--     Password123
--
-- El hash corresponde a BCrypt con 10 rondas, compatible con el
-- BCryptPasswordEncoder configurado en SecurityConfig.
-- --------------------------------------------------------------------------
UPDATE usuarios SET password_hash = '$2b$10$atEV.h0BQLn6quCDnDKLQ..3WjyU6Oab0CNUfctbYQYXC9dD41L6e';

-- --------------------------------------------------------------------------
-- CONSULTAS DE VERIFICACION
-- Se ejecutan al final para comprobar que cada entidad quedo con al menos
-- 20 registros, tal como exige el enunciado.
-- --------------------------------------------------------------------------
SELECT 'permisos'                  AS entidad, COUNT(*) AS registros FROM permisos
UNION ALL SELECT 'roles_permisos', COUNT(*) FROM roles_permisos
UNION ALL SELECT 'perfiles_proveedor', COUNT(*) FROM perfiles_proveedor
UNION ALL SELECT 'perfiles_cliente', COUNT(*) FROM perfiles_cliente
UNION ALL SELECT 'herramientas_categorias', COUNT(*) FROM herramientas_categorias
UNION ALL SELECT 'herramientas_imagenes', COUNT(*) FROM herramientas_imagenes
UNION ALL SELECT 'especificaciones_tecnicas', COUNT(*) FROM especificaciones_tecnicas
UNION ALL SELECT 'tarifas_herramienta', COUNT(*) FROM tarifas_herramienta
UNION ALL SELECT 'usuarios', COUNT(*) FROM usuarios
;

-- ==========================================================================
-- FIN DEL SCRIPT DML
-- ==========================================================================
