-- ============================================================================
-- PLATAFORMA DE RENTA DE HERRAMIENTAS Y EQUIPOS DE CONSTRUCCIÓN
-- SCRIPT DML: POBLACIÓN DE DATOS (MÍNIMO 20 REGISTROS POR CADA ENTIDAD)
-- ============================================================================

USE renta_herramientas_db;

-- ----------------------------------------------------------------------------
-- 1. ENTIDAD: roles (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO roles (id_rol, nombre, descripcion) VALUES
(1, 'ROLE_ADMIN', 'Administrador general del sistema con acceso total'),
(2, 'ROLE_PROVEEDOR', 'Proveedor de maquinaria y herramientas'),
(3, 'ROLE_CLIENTE', 'Cliente particular o contratista que alquila equipos'),
(4, 'ROLE_SUPER_ADMIN', 'Super Administrador con facultades de auditoria y configuracion'),
(5, 'ROLE_PROVEEDOR_PREMIUM', 'Proveedor certificado con prioridad de listado y beneficios'),
(6, 'ROLE_CLIENTE_EMPRESARIAL', 'Empresa constructora con linea de credito y tarifas corporativas'),
(7, 'ROLE_AUDITOR', 'Auditor de transacciones financieras y estados de equipos'),
(8, 'ROLE_SOPORTE_TECNICO', 'Personal de mesa de ayuda y soporte en plataforma'),
(9, 'ROLE_SUPERVISOR_ENTREGAS', 'Encargado de inspeccion fisica y despacho de maquinaria'),
(10, 'ROLE_INSPECTOR_DEVOLUCION', 'Especialista tecnico que evalua estado y desgaste al devolver'),
(11, 'ROLE_CONTADOR', 'Encargado de conciliacion contable y facturacion electronica'),
(12, 'ROLE_COORDINADOR_LOGISTICA', 'Gestiona despachos, fletes y transporte de maquinaria pesada'),
(13, 'ROLE_GESTOR_COBRANZAS', 'Encargado de seguimiento a carteras vencidas y moras'),
(14, 'ROLE_PERITO_DANOS', 'Valuador oficial de siniestros y reclamos de garantias'),
(15, 'ROLE_ASISTENTE_VENTAS', 'Ejecutivo comercial para captacion de contratistas'),
(16, 'ROLE_SUPERVISOR_PROVEEDORES', 'Verifica legalidad y documentacion de proveedores'),
(17, 'ROLE_OPERADOR_BODEGA', 'Control de patio de herramientas y almacenamiento'),
(18, 'ROLE_ANALISTA_RIESGO', 'Evaluacion crediticia de clientes antes de alquileres grandes'),
(19, 'ROLE_CLIENTE_FRECUENTE', 'Cliente particular con historial positivo y descuentos especiales'),
(20, 'ROLE_INVITADO', 'Usuario en proceso de verificacion documental');

-- ----------------------------------------------------------------------------
-- 2. ENTIDAD: usuarios (20 registros)
-- (Contraseñas hash bcrypt simuladas compatibles con Spring Security)
-- ----------------------------------------------------------------------------
INSERT INTO usuarios (id_usuario, nombre_completo, email, password_hash, telefono, direccion, tipo_documento, numero_documento, activo) VALUES
(1, 'Carlos Andres Perez', 'admin.perez@rentatool.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3105550101', 'Calle 100 # 15-20, Bogota', 'CC', '1010202301', TRUE),
(2, 'Maria Fernanda Gomez', 'admin.gomez@rentatool.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3115550102', 'Carrera 7 # 72-10, Bogota', 'CC', '1010202302', TRUE),
(3, 'Constructora Equipos del Norte SAS', 'contacto@equiposnorte.co', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3125550103', 'Autopista Norte Km 18, Chia', 'NIT', '900123456-1', TRUE),
(4, 'Maquinaria y Andamios Occidente', 'ventas@maquiantioquia.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3135550104', 'Calle 50 # 45-30, Medellin', 'NIT', '900987654-2', TRUE),
(5, 'Suministros Civiles de la Sabana', 'gerencia@sumiciviles.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3145550105', 'Zona Industrial Manzana B, Cota', 'NIT', '901234567-3', TRUE),
(6, 'RentaTools Santander Ltda', 'info@rentasandander.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3155550106', 'Bulevar Santander # 28-14, Bucaramanga', 'NIT', '901345678-4', TRUE),
(7, 'Ferreteria y Maquinaria El Progreso', 'contacto@ferreprogreso.co', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3165550107', 'Avenida Colombia # 10-55, Cali', 'NIT', '901456789-5', TRUE),
(8, 'Daniel Felipe Osorio', 'daniel.osorio@gmail.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3175550108', 'Calle 80 # 68-12, Bogota', 'CC', '1098765432', TRUE),
(9, 'Juan Camilo Garcia', 'camilo.garcia@gmail.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3185550109', 'Carrera 33 # 48-19, Bucaramanga', 'CC', '1098765433', TRUE),
(10, 'Inversiones y Construcciones Bolivar', 'compras@inverbolivar.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3195550110', 'Avenida El Dorado # 69-70, Bogota', 'NIT', '900555666-8', TRUE),
(11, 'Obras y Estructuras Caribe SAS', 'licitaciones@obrascaribe.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3205550111', 'Calle 72 # 56-40, Barranquilla', 'NIT', '900777888-9', TRUE),
(12, 'Andres Mauricio Restrepo', 'andres.restrepo@hotmail.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3215550112', 'Transversal 39 # 74-20, Medellin', 'CC', '1020304050', TRUE),
(13, 'Alejandra Sofia Benitez', 'aleja.benitez@yahoo.es', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3225550113', 'Calle 134 # 19-45, Bogota', 'CC', '1030405060', TRUE),
(14, 'Consorcio Vial Del Pacifico', 'operaciones@vialpacifico.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3235550114', 'Carrera 1 # 14-80, Buenaventura', 'NIT', '901112233-4', TRUE),
(15, 'Camilo Ernesto Vargas', 'camilovargas.arq@gmail.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3245550115', 'Carrera 15 # 85-30, Bogota', 'CC', '1040506070', TRUE),
(16, 'Disenos y Cimentaciones Andinas', 'proyectos@cimentacionesandinas.co', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3255550116', 'Calle 67 # 12-09, Pereira', 'NIT', '901998877-6', TRUE),
(17, 'Gustavo Adolfo Quintero', 'tavogquintero@gmail.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3265550117', 'Calle 9 # 42-18, Cali', 'CC', '1050607080', TRUE),
(18, 'Soluciones Electricas e Hidraulicas', 'info@solucioneseh.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3275550118', 'Carrera 27 # 36-15, Floridablanca', 'NIT', '901554433-2', TRUE),
(19, 'Laura Marcela Ospina', 'laura.ospina@gmail.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3285550119', 'Calle 140 # 11-25, Bogota', 'CC', '1060708090', TRUE),
(20, 'Julian David Morales', 'jmorales.civil@outlook.com', '$2a$10$w8TKnN6wQ14b8Z5Zg1j0m.bQYjVd8zN6C8o1t.3J9F1kR0m5yq1yW', '3295550120', 'Avenida Las Vegas # 16-40, Envigado', 'CC', '1070809010', TRUE);

-- ----------------------------------------------------------------------------
-- 3. ENTIDAD: usuarios_roles (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO usuarios_roles (id_usuario, id_rol) VALUES
(1, 1),  (1, 4),  (2, 1),  (3, 2),  (3, 5),
(4, 2),  (5, 2),  (6, 2),  (7, 2),  (8, 3),
(8, 19), (9, 3),  (10, 3), (10, 6), (11, 3),
(11, 6), (12, 3), (13, 3), (14, 3), (15, 3);

-- ----------------------------------------------------------------------------
-- 4. ENTIDAD: categorias (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO categorias (id_categoria, nombre, descripcion) VALUES
(1, 'Compactacion y Suelos', 'Vibrocompactadores, bailarinas y rodillos manuales'),
(2, 'Demolicion y Perforacion', 'Martillos demoledores, rotomartillos y taladros de impacto'),
(3, 'Corte y Desbaste', 'Cortadoras de concreto, pulidoras y sierras circulares'),
(4, 'Concreto y Mezclado', 'Mezcladoras de bulto, tolvas y vibradores de concreto'),
(5, 'Trabajo en Alturas', 'Andamios certificados, arneses y plataformas elevadoras'),
(6, 'Generacion de Energia', 'Plantas electricas diesel y a gasolina e inversores'),
(7, 'Bombeo y Achique', 'Motobombas sumergibles, autocebantes y de lodos'),
(8, 'Soldadura y Corte Termico', 'Soldadores inverter, equipos oxiacetilenicos y de microalambre'),
(9, 'Topografia y Medicion', 'Niveles opticos, estaciones totales y distanciómetros laser'),
(10, 'Limpieza Industrial', 'Hidrolavadoras de alta presion y aspiradoras industriales'),
(11, 'Elevacion y Carga', 'Polipastos, malacates de obra y diferenciales manuales'),
(12, 'Pintura y Recubrimientos', 'Equipos airless, compresores de aire y pistolas neumaticas'),
(13, 'Fijacion y Ensamble', 'Pistolas de fijacion neumatica, clavadoras y remachadoras'),
(14, 'Iluminacion de Obra', 'Torres de iluminacion portatiles y reflectores LED de alto poder'),
(15, 'Equipos Neumaticos', 'Compresores de piston, mangueras de alta presion y rompedores'),
(16, 'Jardineria y Movimiento Liviano', 'Desbrozadoras, ahoyadoras y motosierras de despeje'),
(17, 'Seguridad y Senalizacion', 'Vallas de contencion, conos viales y senales reflectivas'),
(18, 'Climatizacion y Secado', 'Calefactores de obra, extractores de aire y deshumidificadores'),
(19, 'Herramientas Inalambricas', 'Kits a bateria de 18V/20V para ensamble ligero'),
(20, 'Pruebas Hidraulicas', 'Bombas manuales y electricas de prueba de presion hidrostatica');

-- ----------------------------------------------------------------------------
-- 5. ENTIDAD: herramientas (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO herramientas (id_herramienta, id_proveedor, id_categoria, nombre, descripcion, marca, modelo, numero_serie, estado_operativo, tarifa_diaria, valor_reposicion) VALUES
(1, 3, 1, 'Vibrocompactador Tipo Rana 5.5 HP', 'Placa vibratoria para compactacion de asfalto y gravilla', 'Wacker Neuson', 'WP1550', 'WN-RANA-2024-001', 'DISPONIBLE', 75000.00, 4800000.00),
(2, 3, 1, 'Apisonador Canguro 4 Tiempos', 'Bailarina compactadora de zanjas y cimentaciones estrechas', 'Mikasa', 'MTX70', 'MK-CANG-2024-002', 'DISPONIBLE', 85000.00, 6200000.00),
(3, 4, 2, 'Martillo Demoledor Hexagonal 30 kg', 'Rompedor de concreto reforzado con energia de impacto 68J', 'Bosch', 'GSH 27 VC', 'BS-DEM-2023-101', 'ALQUILADA', 110000.00, 8500000.00),
(4, 4, 2, 'Rotomartillo SDS Max 1500W', 'Taladro percutor de trabajo pesado para anclajes quimicos', 'DeWalt', 'D25763K', 'DW-ROTO-2023-202', 'DISPONIBLE', 55000.00, 3100000.00),
(5, 5, 3, 'Cortadora de Pavimento 13 HP', 'Disco diamantado de 14 pulgadas para cortes de juntas', 'Husqvarna', 'FS400-LV', 'HQ-CORT-2024-301', 'DISPONIBLE', 130000.00, 9400000.00),
(6, 5, 4, 'Mezcladora de Concreto 1.5 Bultos', 'Trompo mezclador con motor a gasolina 9 HP', 'Kipor', 'KCM-350', 'KP-MEZ-2023-401', 'ALQUILADA', 65000.00, 3900000.00),
(7, 3, 4, 'Vibrador de Concreto a Gasolina 5.5 HP', 'Manguera de aguja de 4 metros con acople rapido dynapac', 'Enar', 'Dingo-Gas', 'EN-VIB-2024-501', 'DISPONIBLE', 45000.00, 2200000.00),
(8, 4, 5, 'Seccion Andamio Multidireccional Certificado', 'Modulo de 2x2x1.5m galvanizado norma OSHA', 'Layher', 'Allround-HD', 'LH-AND-2023-601', 'DISPONIBLE', 25000.00, 1500000.00),
(9, 6, 6, 'Generador Electrico Diesel 7.5 kVA', 'Planta insonorizada monofasica y trifasica con arranque electrico', 'Caterpillar', 'RP7500E', 'CAT-GEN-2024-701', 'DISPONIBLE', 150000.00, 11000000.00),
(10, 6, 7, 'Motobomba Autocebante 3 Pulgadas', 'Bomba para desalojo rapido de aguas turbias con solidos', 'Honda', 'WT30X', 'HN-BOMB-2023-801', 'DISPONIBLE', 60000.00, 3400000.00),
(11, 7, 8, 'Soldador Inverter Multiproceso 250A', 'Soldadora TIG/MIG/MMA digital para estructuras pesadas', 'Lincoln Electric', 'PowerMIG-210', 'LE-SOLD-2024-901', 'DISPONIBLE', 90000.00, 5600000.00),
(12, 7, 9, 'Estacion Total Laser 2 Segundos', 'Instrumento topografico electronico con prisma de alcance 3km', 'Topcon', 'ES-105', 'TP-TOP-2023-010', 'DISPONIBLE', 220000.00, 18500000.00),
(13, 3, 10, 'Hidrolavadora Industrial a Gasolina 3600 PSI', 'Lavadora de alta presion para lavado de fachadas y losas', 'Karcher', 'HD 7/16-4 Cage', 'KR-HID-2024-011', 'DISPONIBLE', 70000.00, 4200000.00),
(14, 5, 11, 'Malacate de Obra 1 Tonelada con Guaya 60m', 'Elevador de carga para materiales en pisos superiores', 'Imer', 'ET-1000N', 'IM-MAL-2023-012', 'DISPONIBLE', 120000.00, 8900000.00),
(15, 4, 12, 'Equipo Pintura Airless Profesional', 'Pulverizador electrico sin aire para aplicacion de vinilos y esmaltes', 'Graco', 'UltraMax-II-490', 'GC-AIR-2024-013', 'DISPONIBLE', 95000.00, 7800000.00),
(16, 6, 14, 'Torre de Iluminacion LED Portatil 4x350W', 'Mastil telescopico de 7 metros con generador diesel integrado', 'Doosan', 'LSC-60Hz', 'DS-TOR-2023-014', 'DISPONIBLE', 280000.00, 24000000.00),
(17, 7, 15, 'Compresor de Aire Portatil 185 CFM', 'Tornillo rotativo diesel para alimentacion de rompedores triples', 'Atlas Copco', 'XAS-88', 'AC-COMP-2024-015', 'DISPONIBLE', 320000.00, 38000000.00),
(18, 5, 3, 'Pulidora Angular Industrial 9 Pulgadas', 'Esmeriladora de alto desempeno 2600W con guarda antipolvo', 'Makita', 'GA9020', 'MK-PUL-2023-016', 'DISPONIBLE', 30000.00, 950000.00),
(19, 3, 18, 'Extractor / Ventilador Axial de Obra 16 Pulgadas', 'Extractor para ventilacion en tuneles y sotanos confinados', 'Canarm', 'AX-400', 'CN-EXT-2024-017', 'DISPONIBLE', 40000.00, 1800000.00),
(20, 4, 20, 'Bomba Hidrostatica de Prueba 50 Bar', 'Bomba para verificacion de fugas en tuberias de gas y acueducto', 'Rothenberger', 'RP-50-S', 'RB-PRU-2023-018', 'DISPONIBLE', 35000.00, 1200000.00);

-- ----------------------------------------------------------------------------
-- 6. ENTIDAD: metodos_pago (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO metodos_pago (id_metodo_pago, nombre, descripcion, activo) VALUES
(1, 'PSE_BANCOLOMBIA', 'Transferencia directa cuenta Bancolombia via PSE', TRUE),
(2, 'PSE_DAVIVIENDA', 'Transferencia directa cuenta Davivienda via PSE', TRUE),
(3, 'PSE_BANCO_BOGOTA', 'Transferencia directa Banco de Bogota via PSE', TRUE),
(4, 'PSE_BBVA', 'Transferencia directa BBVA Colombia via PSE', TRUE),
(5, 'PSE_BANCO_OCCIDENTE', 'Transferencia directa Banco de Occidente via PSE', TRUE),
(6, 'TARJETA_CREDITO_VISA', 'Pago seguro pasarela de pagos Visa', TRUE),
(7, 'TARJETA_CREDITO_MASTERCARD', 'Pago seguro pasarela de pagos Mastercard', TRUE),
(8, 'TARJETA_CREDITO_AMEX', 'Tarjeta American Express para corporativos', TRUE),
(9, 'NEQUI', 'Billetera digital Nequi mediante codigo QR o push', TRUE),
(10, 'DAVIPLATA', 'Billetera digital Daviplata enlace directo', TRUE),
(11, 'TRANSFERENCIA_BANCARIA_DIRECTA', 'Consignacion en cuenta corriente empresarial', TRUE),
(12, 'EFECTIVO_CONTRA_ENTREGA', 'Pago en efectivo al momento del retiro en patio', TRUE),
(13, 'CHEQUE_GERENCIA_CONSIGNADO', 'Cheque de gerencia verificado por area contable', TRUE),
(14, 'LINEA_CREDITO_CORPORATIVO_30D', 'Credito a 30 dias para constructoras autorizadas', TRUE),
(15, 'LINEA_CREDITO_CORPORATIVO_60D', 'Credito a 60 dias con orden de compra radicada', TRUE),
(16, 'TARJETA_DEBITO_PRESENCIAL', 'Datafono presencial en patio de despacho', TRUE),
(17, 'PAGOS_BOLD_LINK', 'Enlace de cobro rapido Bold', TRUE),
(18, 'PAGOS_WOMPI_QR', 'Pasarela digital Wompi de Bancolombia', TRUE),
(19, 'MERCADO_PAGO_GATEWAY', 'Pasarela integrada MercadoPago', TRUE),
(20, 'DEPOSITO_ANTICIPADO_CUENTA', 'Abono previo transferido antes de reserva', TRUE);

-- ----------------------------------------------------------------------------
-- 7. ENTIDAD: reservas (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO reservas (id_reserva, codigo_reserva, id_cliente, fecha_solicitud, fecha_inicio, fecha_fin, estado, monto_total_estimado, deposito_garantia, notas_cliente, motivo_rechazo) VALUES
(1, 'RES-2026-0001', 8, '2026-10-01 08:30:00', '2026-10-02', '2026-10-05', 'FINALIZADA', 225000.00, 300000.00, 'Obra remodelacion casa residencial', NULL),
(2, 'RES-2026-0002', 9, '2026-10-01 09:15:00', '2026-10-03', '2026-10-07', 'FINALIZADA', 440000.00, 500000.00, 'Demolicion muro de contencion', NULL),
(3, 'RES-2026-0003', 10, '2026-10-02 10:00:00', '2026-10-04', '2026-10-10', 'EN_CURSO', 780000.00, 1000000.00, 'Corte de juntas en parqueadero', NULL),
(4, 'RES-2026-0004', 11, '2026-10-02 11:20:00', '2026-10-05', '2026-10-12', 'EN_CURSO', 455000.00, 600000.00, 'Vaciado de placas torre 2', NULL),
(5, 'RES-2026-0005', 12, '2026-10-03 14:00:00', '2026-10-06', '2026-10-08', 'FINALIZADA', 170000.00, 250000.00, 'Compactacion de patio', NULL),
(6, 'RES-2026-0006', 13, '2026-10-03 15:30:00', '2026-10-07', '2026-10-09', 'FINALIZADA', 120000.00, 200000.00, 'Desalojo de zanja por inundacion', NULL),
(7, 'RES-2026-0007', 14, '2026-10-04 09:00:00', '2026-10-08', '2026-10-15', 'EN_CURSO', 1050000.00, 1500000.00, 'Energia provisional campamento vial', NULL),
(8, 'RES-2026-0008', 15, '2026-10-04 11:45:00', '2026-10-09', '2026-10-11', 'APROBADA', 180000.00, 300000.00, 'Estructura metalica mezanine', NULL),
(9, 'RES-2026-0009', 16, '2026-10-05 08:20:00', '2026-10-10', '2026-10-14', 'APROBADA', 880000.00, 1200000.00, 'Levantamiento topografico linderos', NULL),
(10, 'RES-2026-0010', 17, '2026-10-05 13:10:00', '2026-10-11', '2026-10-13', 'SOLICITADA', 140000.00, 200000.00, 'Limpieza losa post construccion', NULL),
(11, 'RES-2026-0011', 18, '2026-10-06 10:00:00', '2026-10-12', '2026-10-16', 'SOLICITADA', 480000.00, 600000.00, 'Izaje de materiales cubierta', NULL),
(12, 'RES-2026-0012', 19, '2026-10-06 16:40:00', '2026-10-13', '2026-10-15', 'SOLICITADA', 190000.00, 300000.00, 'Pintura fachada bodega 4', NULL),
(13, 'RES-2026-0013', 20, '2026-10-07 09:10:00', '2026-10-14', '2026-10-18', 'APROBADA', 1120000.00, 1500000.00, 'Jornada nocturna iluminacion pavimentacion', NULL),
(14, 'RES-2026-0014', 8, '2026-10-07 11:30:00', '2026-10-15', '2026-10-17', 'SOLICITADA', 640000.00, 800000.00, 'Alimentacion neumatica martillos', NULL),
(15, 'RES-2026-0015', 9, '2026-10-08 14:15:00', '2026-10-16', '2026-10-17', 'SOLICITADA', 60000.00, 100000.00, 'Desbaste de pisos bodega', NULL),
(16, 'RES-2026-0016', 10, '2026-10-08 15:50:00', '2026-10-17', '2026-10-20', 'APROBADA', 120000.00, 200000.00, 'Ventilacion en sotano 2', NULL),
(17, 'RES-2026-0017', 11, '2026-10-09 08:45:00', '2026-10-18', '2026-10-19', 'SOLICITADA', 70000.00, 150000.00, 'Prueba de presion red contra incendio', NULL),
(18, 'RES-2026-0018', 12, '2026-10-09 10:20:00', '2026-10-19', '2026-10-22', 'SOLICITADA', 300000.00, 400000.00, 'Compactacion de subbase anden', NULL),
(19, 'RES-2026-0019', 13, '2026-10-10 11:00:00', '2026-10-20', '2026-10-25', 'RECHAZADA', 275000.00, 300000.00, 'Perforaciones multiples sin operador certificado', 'Falta de documentacion de prevencion de riesgos'),
(20, 'RES-2026-0020', 14, '2026-10-10 16:30:00', '2026-10-21', '2026-10-23', 'CANCELADA', 130000.00, 200000.00, 'Cancelado por lluvias torrenciales en obra', NULL);

-- ----------------------------------------------------------------------------
-- 8. ENTIDAD: detalles_reserva (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO detalles_reserva (id_detalle_reserva, id_reserva, id_herramienta, cantidad, precio_unitario_dia, dias_alquiler, subtotal) VALUES
(1, 1, 1, 1, 75000.00, 3, 225000.00),
(2, 2, 3, 1, 110000.00, 4, 440000.00),
(3, 3, 5, 1, 130000.00, 6, 780000.00),
(4, 4, 6, 1, 65000.00, 7, 455000.00),
(5, 5, 2, 1, 85000.00, 2, 170000.00),
(6, 6, 10, 1, 60000.00, 2, 120000.00),
(7, 7, 9, 1, 150000.00, 7, 1050000.00),
(8, 8, 11, 1, 90000.00, 2, 180000.00),
(9, 9, 12, 1, 220000.00, 4, 880000.00),
(10, 10, 13, 1, 70000.00, 2, 140000.00),
(11, 11, 14, 1, 120000.00, 4, 480000.00),
(12, 12, 15, 1, 95000.00, 2, 190000.00),
(13, 13, 16, 1, 280000.00, 4, 1120000.00),
(14, 14, 17, 1, 320000.00, 2, 640000.00),
(15, 15, 18, 1, 30000.00, 2, 60000.00),
(16, 16, 19, 1, 40000.00, 3, 120000.00),
(17, 17, 20, 1, 35000.00, 2, 70000.00),
(18, 18, 1, 1, 75000.00, 4, 300000.00),
(19, 19, 4, 1, 55000.00, 5, 275000.00),
(20, 20, 6, 1, 65000.00, 2, 130000.00);

-- ----------------------------------------------------------------------------
-- 9. ENTIDAD: auditoria_estados_reserva (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO auditoria_estados_reserva (id_auditoria, id_reserva, estado_anterior, estado_nuevo, fecha_cambio, cambiado_por, observacion) VALUES
(1, 1, NULL, 'SOLICITADA', '2026-10-01 08:30:00', 8, 'Creacion de reserva inicial por el cliente'),
(2, 1, 'SOLICITADA', 'APROBADA', '2026-10-01 09:30:00', 1, 'Aprobacion por parte del proveedor'),
(3, 1, 'APROBADA', 'EN_CURSO', '2026-10-02 08:00:00', 1, 'Entrega en patio y despacho de equipo'),
(4, 1, 'EN_CURSO', 'FINALIZADA', '2026-10-05 17:30:00', 1, 'Devolucion recibida en buen estado'),
(5, 2, NULL, 'SOLICITADA', '2026-10-01 09:15:00', 9, 'Creacion de solicitud de alquiler'),
(6, 2, 'SOLICITADA', 'APROBADA', '2026-10-01 10:00:00', 2, 'Aprobado tras validacion de disponibilidad'),
(7, 2, 'APROBADA', 'EN_CURSO', '2026-10-03 08:15:00', 2, 'Despachado a obra'),
(8, 2, 'EN_CURSO', 'FINALIZADA', '2026-10-07 16:45:00', 2, 'Equipo devuelto con dano leve reportado'),
(9, 3, NULL, 'SOLICITADA', '2026-10-02 10:00:00', 10, 'Solicitud ingresada'),
(10, 3, 'SOLICITADA', 'APROBADA', '2026-10-02 11:30:00', 1, 'Aprobada orden de compra corporativa'),
(11, 3, 'APROBADA', 'EN_CURSO', '2026-10-04 07:45:00', 1, 'Equipo entregado en punto'),
(12, 4, NULL, 'SOLICITADA', '2026-10-02 11:20:00', 11, 'Solicitud registrada'),
(13, 4, 'SOLICITADA', 'APROBADA', '2026-10-02 14:00:00', 2, 'Aprobado por el proveedor'),
(14, 4, 'APROBADA', 'EN_CURSO', '2026-10-05 08:30:00', 2, 'Equipo en operacion en obra'),
(15, 5, 'SOLICITADA', 'APROBADA', '2026-10-03 14:30:00', 1, 'Confirmacion rapida'),
(16, 5, 'APROBADA', 'FINALIZADA', '2026-10-08 17:00:00', 1, 'Devuelto a tiempo'),
(17, 6, 'SOLICITADA', 'FINALIZADA', '2026-10-09 18:00:00', 2, 'Cierre de alquiler completo'),
(18, 7, 'APROBADA', 'EN_CURSO', '2026-10-08 09:00:00', 1, 'Generador entregado'),
(19, 19, 'SOLICITADA', 'RECHAZADA', '2026-10-10 11:45:00', 1, 'Rechazo por falta de documentos'),
(20, 20, 'SOLICITADA', 'CANCELADA', '2026-10-10 17:00:00', 14, 'Cliente cancela por lluvia');

-- ----------------------------------------------------------------------------
-- 10. ENTIDAD: pagos (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO pagos (id_pago, numero_transaccion, id_reserva, id_metodo_pago, monto, tipo_pago, estado_pago, fecha_pago, referencia_pasarela) VALUES
(1, 'TRX-2026-1001', 1, 1, 525000.00, 'TOTALIDAD', 'APROBADO', '2026-10-01 10:00:00', 'PSE-BCOL-998811'),
(2, 'TRX-2026-1002', 2, 6, 940000.00, 'TOTALIDAD', 'APROBADO', '2026-10-01 11:30:00', 'VIS-AUTH-554433'),
(3, 'TRX-2026-1003', 3, 14, 1780000.00, 'TOTALIDAD', 'APROBADO', '2026-10-02 12:00:00', 'CRED-CORP-001'),
(4, 'TRX-2026-1004', 4, 15, 1055000.00, 'TOTALIDAD', 'APROBADO', '2026-10-02 15:30:00', 'CRED-CORP-002'),
(5, 'TRX-2026-1005', 5, 9, 420000.00, 'TOTALIDAD', 'APROBADO', '2026-10-03 15:00:00', 'NEQ-TRF-776655'),
(6, 'TRX-2026-1006', 6, 10, 320000.00, 'TOTALIDAD', 'APROBADO', '2026-10-03 16:30:00', 'DAV-MOV-332211'),
(7, 'TRX-2026-1007', 7, 7, 2550000.00, 'TOTALIDAD', 'APROBADO', '2026-10-04 10:15:00', 'MC-GATE-881122'),
(8, 'TRX-2026-1008', 8, 2, 480000.00, 'TOTALIDAD', 'APROBADO', '2026-10-04 13:00:00', 'PSE-DAVI-445566'),
(9, 'TRX-2026-1009', 9, 11, 2080000.00, 'TOTALIDAD', 'APROBADO', '2026-10-05 09:30:00', 'BANC-DIR-12345'),
(10, 'TRX-2026-1010', 10, 1, 340000.00, 'ANTICIPO', 'PENDIENTE', '2026-10-05 14:00:00', 'PSE-BCOL-990011'),
(11, 'TRX-2026-1011', 11, 6, 1080000.00, 'ANTICIPO', 'PENDIENTE', '2026-10-06 11:00:00', 'VIS-AUTH-778899'),
(12, 'TRX-2026-1012', 12, 12, 490000.00, 'ANTICIPO', 'PENDIENTE', '2026-10-06 17:00:00', 'EFE-PAT-0091'),
(13, 'TRX-2026-1013', 13, 14, 2620000.00, 'TOTALIDAD', 'APROBADO', '2026-10-07 10:00:00', 'CRED-CORP-003'),
(14, 'TRX-2026-1014', 14, 8, 1440000.00, 'ANTICIPO', 'PENDIENTE', '2026-10-07 12:30:00', 'AMX-GATE-112233'),
(15, 'TRX-2026-1015', 15, 9, 160000.00, 'ANTICIPO', 'PENDIENTE', '2026-10-08 15:00:00', 'NEQ-TRF-994433'),
(16, 'TRX-2026-1016', 16, 4, 320000.00, 'TOTALIDAD', 'APROBADO', '2026-10-08 16:30:00', 'PSE-BBVA-667788'),
(17, 'TRX-2026-1017', 17, 3, 220000.00, 'ANTICIPO', 'PENDIENTE', '2026-10-09 09:30:00', 'PSE-BOG-223344'),
(18, 'TRX-2026-1018', 18, 5, 700000.00, 'ANTICIPO', 'PENDIENTE', '2026-10-09 11:00:00', 'PSE-OCC-556677'),
(19, 'TRX-2026-1019', 2, 1, 80000.00, 'MULTA_DANO', 'APROBADO', '2026-10-07 17:15:00', 'PSE-BCOL-443322'),
(20, 'TRX-2026-1020', 1, 1, 30000.00, 'RECARGO_MORA', 'APROBADO', '2026-10-05 18:00:00', 'PSE-BCOL-112233');

-- ----------------------------------------------------------------------------
-- 11. ENTIDAD: facturas (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO facturas (id_factura, numero_factura, id_pago, id_cliente, fecha_emision, subtotal, iva, total, estado_factura, url_comprobante_pdf) VALUES
(1, 'FAC-2026-0001', 1, 8, '2026-10-01 10:05:00', 441176.47, 83823.53, 525000.00, 'PAGADA', '/facturas/pdf/FAC-2026-0001.pdf'),
(2, 'FAC-2026-0002', 2, 9, '2026-10-01 11:35:00', 789915.97, 150084.03, 940000.00, 'PAGADA', '/facturas/pdf/FAC-2026-0002.pdf'),
(3, 'FAC-2026-0003', 3, 10, '2026-10-02 12:05:00', 1495798.32, 284201.68, 1780000.00, 'PAGADA', '/facturas/pdf/FAC-2026-0003.pdf'),
(4, 'FAC-2026-0004', 4, 11, '2026-10-02 15:35:00', 886554.62, 168445.38, 1055000.00, 'PAGADA', '/facturas/pdf/FAC-2026-0004.pdf'),
(5, 'FAC-2026-0005', 5, 12, '2026-10-03 15:05:00', 352941.18, 67058.82, 420000.00, 'PAGADA', '/facturas/pdf/FAC-2026-0005.pdf'),
(6, 'FAC-2026-0006', 6, 13, '2026-10-03 16:35:00', 268907.56, 51092.44, 320000.00, 'PAGADA', '/facturas/pdf/FAC-2026-0006.pdf'),
(7, 'FAC-2026-0007', 7, 14, '2026-10-04 10:20:00', 2142857.14, 407142.86, 2550000.00, 'PAGADA', '/facturas/pdf/FAC-2026-0007.pdf'),
(8, 'FAC-2026-0008', 8, 15, '2026-10-04 13:05:00', 403361.34, 76638.66, 480000.00, 'PAGADA', '/facturas/pdf/FAC-2026-0008.pdf'),
(9, 'FAC-2026-0009', 9, 16, '2026-10-05 09:35:00', 1747899.16, 332100.84, 2080000.00, 'PAGADA', '/facturas/pdf/FAC-2026-0009.pdf'),
(10, 'FAC-2026-0010', 10, 17, '2026-10-05 14:05:00', 285714.29, 54285.71, 340000.00, 'EMITIDA', '/facturas/pdf/FAC-2026-0010.pdf'),
(11, 'FAC-2026-0011', 11, 18, '2026-10-06 11:05:00', 907563.03, 172436.97, 1080000.00, 'EMITIDA', '/facturas/pdf/FAC-2026-0011.pdf'),
(12, 'FAC-2026-0012', 12, 19, '2026-10-06 17:05:00', 411764.71, 78235.29, 490000.00, 'EMITIDA', '/facturas/pdf/FAC-2026-0012.pdf'),
(13, 'FAC-2026-0013', 13, 20, '2026-10-07 10:05:00', 2201680.67, 418319.33, 2620000.00, 'PAGADA', '/facturas/pdf/FAC-2026-0013.pdf'),
(14, 'FAC-2026-0014', 14, 8, '2026-10-07 12:35:00', 1210084.03, 229915.97, 1440000.00, 'EMITIDA', '/facturas/pdf/FAC-2026-0014.pdf'),
(15, 'FAC-2026-0015', 15, 9, '2026-10-08 15:05:00', 134453.78, 25546.22, 160000.00, 'EMITIDA', '/facturas/pdf/FAC-2026-0015.pdf'),
(16, 'FAC-2026-0016', 16, 10, '2026-10-08 16:35:00', 268907.56, 51092.44, 320000.00, 'PAGADA', '/facturas/pdf/FAC-2026-0016.pdf'),
(17, 'FAC-2026-0017', 17, 11, '2026-10-09 09:35:00', 184873.95, 35126.05, 220000.00, 'EMITIDA', '/facturas/pdf/FAC-2026-0017.pdf'),
(18, 'FAC-2026-0018', 18, 12, '2026-10-09 11:05:00', 588235.29, 111764.71, 700000.00, 'EMITIDA', '/facturas/pdf/FAC-2026-0018.pdf'),
(19, 'FAC-2026-0019', 19, 9, '2026-10-07 17:20:00', 67226.89, 12773.11, 80000.00, 'PAGADA', '/facturas/pdf/FAC-2026-0019.pdf'),
(20, 'FAC-2026-0020', 20, 8, '2026-10-05 18:05:00', 25210.08, 4789.92, 30000.00, 'PAGADA', '/facturas/pdf/FAC-2026-0020.pdf');

-- ----------------------------------------------------------------------------
-- 12. ENTIDAD: detalles_factura (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO detalles_factura (id_detalle_factura, id_factura, descripcion_item, cantidad, precio_unitario, subtotal_linea) VALUES
(1, 1, 'Alquiler Vibrocompactador Tipo Rana (3 dias) + Garantia', 1, 441176.47, 441176.47),
(2, 2, 'Alquiler Martillo Demoledor Hexagonal (4 dias) + Garantia', 1, 789915.97, 789915.97),
(3, 3, 'Alquiler Cortadora Pavimento (6 dias) + Garantia', 1, 1495798.32, 1495798.32),
(4, 4, 'Alquiler Mezcladora Concreto (7 dias) + Garantia', 1, 886554.62, 886554.62),
(5, 5, 'Alquiler Apisonador Canguro (2 dias) + Garantia', 1, 352941.18, 352941.18),
(6, 6, 'Alquiler Motobomba 3 Pulgadas (2 dias) + Garantia', 1, 268907.56, 268907.56),
(7, 7, 'Alquiler Generador Electrico Diesel 7.5kVA (7 dias)', 1, 2142857.14, 2142857.14),
(8, 8, 'Alquiler Soldador Inverter 250A (2 dias) + Garantia', 1, 403361.34, 403361.34),
(9, 9, 'Alquiler Estacion Total Topografica (4 dias)', 1, 1747899.16, 1747899.16),
(10, 10, 'Anticipo Alquiler Hidrolavadora 3600 PSI', 1, 285714.29, 285714.29),
(11, 11, 'Anticipo Alquiler Malacate de Obra 1 Tonelada', 1, 907563.03, 907563.03),
(12, 12, 'Anticipo Alquiler Equipo Pintura Airless', 1, 411764.71, 411764.71),
(13, 13, 'Alquiler Torre de Iluminacion LED (4 dias)', 1, 2201680.67, 2201680.67),
(14, 14, 'Anticipo Alquiler Compresor Aire 185 CFM', 1, 1210084.03, 1210084.03),
(15, 15, 'Anticipo Alquiler Pulidora 9 Pulgadas', 1, 134453.78, 134453.78),
(16, 16, 'Alquiler Extractor Axial Obra (3 dias)', 1, 268907.56, 268907.56),
(17, 17, 'Anticipo Bomba Hidrostatica de Prueba', 1, 184873.95, 184873.95),
(18, 18, 'Anticipo Alquiler Placa Compactadora', 1, 588235.29, 588235.29),
(19, 19, 'Cobro Reparacion Puntero Roto Martillo Bosch', 1, 67226.89, 67226.89),
(20, 20, 'Recargo por 1 dia de retraso no pactado', 1, 25210.08, 25210.08);

-- ----------------------------------------------------------------------------
-- 13. ENTIDAD: devoluciones (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO devoluciones (id_devolucion, id_reserva, fecha_devolucion_pactada, fecha_devolucion_real, recibido_por, estado_equipo_general, dias_retraso, observaciones) VALUES
(1, 1, '2026-10-05', '2026-10-05', 1, 'BUEN_ESTADO', 0, 'Equipo entregado limpio y con niveles de aceite correctos'),
(2, 2, '2026-10-07', '2026-10-07', 2, 'DANO_LEVE', 0, 'Cincel hexagonal desgastado por uso rudo y cable con corte superficial'),
(3, 5, '2026-10-08', '2026-10-08', 1, 'BUEN_ESTADO', 0, 'Funcionamiento motor optimo'),
(4, 6, '2026-10-09', '2026-10-09', 2, 'BUEN_ESTADO', 0, 'Mangueras y acoples completos sin fugas'),
(5, 3, '2026-10-10', '2026-10-10', 1, 'BUEN_ESTADO', 0, 'Disco diamantado dentro del desgaste estimado'),
(6, 4, '2026-10-12', '2026-10-12', 2, 'BUEN_ESTADO', 0, 'Tolva limpia sin costras de concreto endurecido'),
(7, 7, '2026-10-15', '2026-10-16', 1, 'BUEN_ESTADO', 1, 'Retraso de 1 dia por logistica de transporte'),
(8, 8, '2026-10-11', '2026-10-11', 2, 'BUEN_ESTADO', 0, 'Pinzas de masa y portaelectrodos en buen estado'),
(9, 9, '2026-10-14', '2026-10-14', 1, 'BUEN_ESTADO', 0, 'Equipo de topografia calibrado y prisma intacto'),
(10, 10, '2026-10-13', '2026-10-13', 2, 'DANO_LEVE', 0, 'Manguera de presion raspada'),
(11, 11, '2026-10-16', '2026-10-17', 1, 'DANO_LEVE', 1, 'Guaya de acero con doblez forzado en punta'),
(12, 12, '2026-10-15', '2026-10-15', 2, 'BUEN_ESTADO', 0, 'Filtro y boquilla lavados correctamente'),
(13, 13, '2026-10-18', '2026-10-18', 1, 'BUEN_ESTADO', 0, 'Focos LED y mastil telescopico sin novedades'),
(14, 14, '2026-10-17', '2026-10-17', 2, 'BUEN_ESTADO', 0, 'Manometros y valvulas de alivio en rango'),
(15, 15, '2026-10-17', '2026-10-17', 1, 'DANO_GRAVE', 0, 'Carcasa de engranajes rajada por golpe frontal'),
(16, 16, '2026-10-20', '2026-10-20', 2, 'BUEN_ESTADO', 0, 'Aspas limpias sin obstruccion'),
(17, 17, '2026-10-19', '2026-10-19', 1, 'BUEN_ESTADO', 0, 'Retencion de presion adecuada'),
(18, 18, '2026-10-22', '2026-10-22', 2, 'BUEN_ESTADO', 0, 'Embrague centrifugo operativo'),
(19, 19, '2026-10-25', '2026-10-25', 1, 'BUEN_ESTADO', 0, 'Cierre formal de solicitud rechazada'),
(20, 20, '2026-10-23', '2026-10-23', 2, 'BUEN_ESTADO', 0, 'No hubo despacho efectivo por cancelacion');

-- ----------------------------------------------------------------------------
-- 14. ENTIDAD: reportes_dano (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO reportes_dano (id_reporte_dano, id_devolucion, id_herramienta, tipo_dano, descripcion_dano, costo_estimado_reparacion, responsable_asume, fecha_reporte) VALUES
(1, 2, 3, 'MECANICO', 'Puntero cincel fracturado y aislamiento de cable danado', 80000.00, 'CLIENTE', '2026-10-07 17:00:00'),
(2, 10, 13, 'ESTETICO', 'Cubierta protectora manguera de alta presion desgarrada', 45000.00, 'CLIENTE', '2026-10-13 16:30:00'),
(3, 11, 14, 'MECANICO', 'Tramo terminal de guaya deshilachado por mal enrollado', 120000.00, 'CLIENTE', '2026-10-17 11:00:00'),
(4, 15, 18, 'MECANICO', 'Ruptura de cabezal metalico de pulidora por caida de altura', 250000.00, 'CLIENTE', '2026-10-17 17:30:00'),
(5, 7, 9, 'ELECTRICO', 'Bateria de encendido descargada por dejar switch activado', 50000.00, 'CLIENTE', '2026-10-16 10:00:00'),
(6, 1, 1, 'ESTETICO', 'Rayaduras menores de pintura en chasis de placa', 0.00, 'PROVEEDOR', '2026-10-05 17:35:00'),
(7, 3, 2, 'MECANICO', 'Filtro de aire saturado de polvo por ambiente pesado', 30000.00, 'PROVEEDOR', '2026-10-08 17:15:00'),
(8, 4, 10, 'ESTETICO', 'Manija de transporte abollada', 20000.00, 'PROVEEDOR', '2026-10-09 18:10:00'),
(9, 5, 5, 'ESTETICO', 'Desgaste normal de polea tensora', 35000.00, 'PROVEEDOR', '2026-10-10 17:10:00'),
(10, 6, 6, 'MECANICO', 'Acumulacion de mortero en corona dentada', 60000.00, 'CLIENTE', '2026-10-12 17:20:00'),
(11, 8, 11, 'ELECTRICO', 'Terminal de pinza de masa recalentado', 40000.00, 'CLIENTE', '2026-10-11 17:10:00'),
(12, 9, 12, 'ESTETICO', 'Maletin de transporte de la estacion total rayado', 0.00, 'PROVEEDOR', '2026-10-14 16:30:00'),
(13, 12, 15, 'FALTANTE_PIEZA', 'Falta una de las dos boquillas de recambio suministradas', 75000.00, 'CLIENTE', '2026-10-15 17:15:00'),
(14, 13, 16, 'MECANICO', 'Freno manual de mastil con holgura', 50000.00, 'PROVEEDOR', '2026-10-18 16:45:00'),
(15, 14, 17, 'ESTETICO', 'Acople rapido tipo garra con rosca golpeada', 35000.00, 'CLIENTE', '2026-10-17 17:45:00'),
(16, 16, 19, 'ELECTRICO', 'Rejilla de ventilacion trasera con pequena hendidura', 25000.00, 'PROVEEDOR', '2026-10-20 17:15:00'),
(17, 17, 20, 'MECANICO', 'Empaque o-ring de piston con fuga leve a 40 bar', 20000.00, 'POLIZA_GARANTIA', '2026-10-19 17:00:00'),
(18, 18, 1, 'MECANICO', 'Correa de transmision agrietada por fatiga de material', 45000.00, 'POLIZA_GARANTIA', '2026-10-22 17:30:00'),
(19, 2, 4, 'ESTETICO', 'Empunadura lateral plastica con raspon lateral', 15000.00, 'PROVEEDOR', '2026-10-07 17:10:00'),
(20, 15, 18, 'IRREPARABLE', 'Inducido quemado por sobrecarga de voltaje prolongada', 320000.00, 'CLIENTE', '2026-10-17 17:40:00');

-- ----------------------------------------------------------------------------
-- 15. ENTIDAD: evidencias_dano (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO evidencias_dano (id_evidencia, id_reporte_dano, url_foto, descripcion_foto) VALUES
(1, 1, '/evidencias/danos/dano_01_puntero_fracturado.jpg', 'Fotografia del puntero cincel partido en la punta'),
(2, 1, '/evidencias/danos/dano_01_cable_cortado.jpg', 'Detalle de cable con recubrimiento de goma cortado'),
(3, 2, '/evidencias/danos/dano_02_manguera_rasgon.jpg', 'Fisura longitudinal en cubierta de manguera karcher'),
(4, 3, '/evidencias/danos/dano_03_guaya_deshilachada.jpg', 'Hilos de acero reventados en punta de malacate'),
(5, 4, '/evidencias/danos/dano_04_carcasa_rota.jpg', 'Fractura visible en carcasa de aluminio pulidora Makita'),
(6, 5, '/evidencias/danos/dano_05_bateria_sulfatada.jpg', 'Bornes de bateria con recalentamiento evidente'),
(7, 7, '/evidencias/danos/dano_07_filtro_polvo.jpg', 'Filtro de aire mikasa colmatado de lodo seco'),
(8, 10, '/evidencias/danos/dano_10_corona_concreto.jpg', 'Costras de concreto endurecidas trabando engranaje'),
(9, 11, '/evidencias/danos/dano_11_pinza_quemada.jpg', 'Punto de contacto fundido por mal agarre a tierra'),
(10, 13, '/evidencias/danos/dano_13_estuche_incompleto.jpg', 'Estuche de boquillas airless con posicion vacia'),
(11, 15, '/evidencias/danos/dano_15_acople_abollado.jpg', 'Acople neumatico con deformacion por impacto'),
(12, 17, '/evidencias/danos/dano_17_goteo_valvula.jpg', 'Goteo de fluido en prueba hidrostatica a 40 bar'),
(13, 18, '/evidencias/danos/dano_18_correa_rota.jpg', 'Correa trapezoidal con dientes desprendidos'),
(14, 20, '/evidencias/danos/dano_20_colector_carbonizado.jpg', 'Bobinado de rotor negro y con olor a quemado'),
(15, 1, '/evidencias/danos/dano_01_serial_verificado.jpg', 'Placa de serie de herramienta danada'),
(16, 4, '/evidencias/danos/dano_04_angulo_caida.jpg', 'Marcas de golpe directo contra suelo de concreto'),
(17, 10, '/evidencias/danos/dano_10_tambor_mezclador.jpg', 'Interior del trompo mezclador con residuos secos'),
(18, 3, '/evidencias/danos/dano_03_tambor_guaya.jpg', 'Guaya trabada en el eje de enrollamiento'),
(19, 5, '/evidencias/danos/dano_05_panel_generador.jpg', 'Panel de control con horometro marcando 320 horas'),
(20, 20, '/evidencias/danos/dano_20_prueba_multimetro.jpg', 'Medicion en multimetro marcando circuito abierto');

-- ----------------------------------------------------------------------------
-- 16. ENTIDAD: cargos_adicionales (20 registros)
-- ----------------------------------------------------------------------------
INSERT INTO cargos_adicionales (id_cargo, id_devolucion, tipo_cargo, monto, pagado, fecha_cargo, descripcion) VALUES
(1, 2, 'REPARACION_DANO', 80000.00, TRUE, '2026-10-07 17:10:00', 'Reposicion de puntero cincel Bosch'),
(2, 1, 'MORA_RETRASO', 30000.00, TRUE, '2026-10-05 17:50:00', 'Cargo por entrega 2 horas despues del horario de cierre'),
(3, 7, 'MORA_RETRASO', 150000.00, TRUE, '2026-10-16 10:15:00', '1 dia de retraso tarifa diaria generador'),
(4, 10, 'REPARACION_DANO', 45000.00, FALSE, '2026-10-13 16:45:00', 'Manga de proteccion manguera hidrolavadora'),
(5, 11, 'REPARACION_DANO', 120000.00, FALSE, '2026-10-17 11:15:00', 'Cambio tramo de guaya certificada 60m'),
(6, 11, 'MORA_RETRASO', 120000.00, FALSE, '2026-10-17 11:15:00', '1 dia de retraso en entrega de malacate'),
(7, 15, 'REPARACION_DANO', 250000.00, FALSE, '2026-10-17 17:45:00', 'Carcasa metalica original Makita'),
(8, 15, 'REPOSICION_EQUIPO', 320000.00, FALSE, '2026-10-17 17:50:00', 'Inducido nuevo para pulidora quemada'),
(9, 6, 'LIMPIEZA_PROFUNDA', 40000.00, TRUE, '2026-10-12 17:30:00', 'Retiro con quimico desincrustante de concreto pegado'),
(10, 12, 'REPOSICION_EQUIPO', 75000.00, TRUE, '2026-10-15 17:25:00', 'Boquilla RAC X 517 Graco no devuelta'),
(11, 14, 'REPARACION_DANO', 35000.00, FALSE, '2026-10-17 17:50:00', 'Repuesto acople garra de alta presion'),
(12, 2, 'LIMPIEZA_PROFUNDA', 25000.00, TRUE, '2026-10-07 17:12:00', 'Limpieza y sopleteado de polvo de concreto de motor'),
(13, 4, 'LIMPIEZA_PROFUNDA', 30000.00, TRUE, '2026-10-09 18:20:00', 'Lavado de canasta de motobomba por sedimentos'),
(14, 5, 'LIMPIEZA_PROFUNDA', 35000.00, TRUE, '2026-10-10 17:15:00', 'Descontaminacion de lodo en guardafangos de cortadora'),
(15, 3, 'LIMPIEZA_PROFUNDA', 20000.00, TRUE, '2026-10-08 17:20:00', 'Limpieza de zapata y acordeon de apisonador'),
(16, 8, 'REPARACION_DANO', 40000.00, TRUE, '2026-10-11 17:15:00', 'Reparacion de bornera de masa de soldador'),
(17, 7, 'MORA_RETRASO', 50000.00, FALSE, '2026-10-16 10:20:00', 'Carga de bateria por descuido en patio'),
(18, 13, 'LIMPIEZA_PROFUNDA', 30000.00, TRUE, '2026-10-18 16:50:00', 'Limpieza cristales focos LED de torre'),
(19, 16, 'LIMPIEZA_PROFUNDA', 25000.00, TRUE, '2026-10-20 17:20:00', 'Lavado ducto de lona y aspas de ventilador'),
(20, 17, 'LIMPIEZA_PROFUNDA', 20000.00, TRUE, '2026-10-19 17:10:00', 'Drenaje de sedimentos de tanque de prueba');
