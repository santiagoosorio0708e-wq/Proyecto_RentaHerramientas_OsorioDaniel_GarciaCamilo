/* ==========================================================================
   mock_data.js - Almacenamiento local resiliente y dataset completo 4FN
   Permite que la aplicacion web funcione de inmediato, offline o en modo demo,
   y sirva de respaldo inteligente si el backend Spring Boot esta apagado.
   ========================================================================== */

const MockStore = (() => {
    'use strict';

    const KEY_HERRAMIENTAS = 'rh_mock_herramientas';
    const KEY_CATEGORIAS   = 'rh_mock_categorias';
    const KEY_USUARIOS     = 'rh_mock_usuarios';
    const KEY_RESERVAS     = 'rh_mock_reservas';
    const KEY_PAGOS        = 'rh_mock_pagos';
    const KEY_FACTURAS     = 'rh_mock_facturas';
    const KEY_DEVOLUCIONES = 'rh_mock_devoluciones';

    const CATEGORIAS_SEED = [
        { idCategoria: 1, nombre: 'Compactación y Suelos', descripcion: 'Vibrocompactadores, bailarinas y rodillos manuales' },
        { idCategoria: 2, nombre: 'Demolición y Perforación', descripcion: 'Martillos demoledores, rotomartillos y taladros de impacto' },
        { idCategoria: 3, nombre: 'Corte y Desbaste', descripcion: 'Cortadoras de concreto, pulidoras y sierras circulares' },
        { idCategoria: 4, nombre: 'Concreto y Mezclado', descripcion: 'Mezcladoras de bulto, tolvas y vibradores de concreto' },
        { idCategoria: 5, nombre: 'Trabajo en Alturas', descripcion: 'Andamios certificados, arneses y plataformas elevadoras' },
        { idCategoria: 6, nombre: 'Generación de Energía', descripcion: 'Plantas eléctricas diésel, a gasolina e inversores' },
        { idCategoria: 7, nombre: 'Bombeo y Achique', descripcion: 'Motobombas sumergibles, autocebantes y de lodos' },
        { idCategoria: 8, nombre: 'Soldadura y Corte Térmico', descripcion: 'Soldadores inverter, equipos oxiacetilénicos y MIG/TIG' },
        { idCategoria: 9, nombre: 'Topografía y Medición', descripcion: 'Niveles ópticos, estaciones totales y distanciómetros láser' },
        { idCategoria: 10, nombre: 'Limpieza Industrial', descripcion: 'Hidrolavadoras de alta presión y aspiradoras de obra' },
        { idCategoria: 11, nombre: 'Elevación y Carga', descripcion: 'Polipastos, malacates de obra y diferenciales manuales' },
        { idCategoria: 12, nombre: 'Pintura y Recubrimientos', descripcion: 'Equipos airless, compresores de aire y pistolas neumáticas' },
        { idCategoria: 13, nombre: 'Fijación y Ensamble', descripcion: 'Pistolas de fijación neumática y clavadoras' },
        { idCategoria: 14, nombre: 'Iluminación de Obra', descripcion: 'Torres de iluminación portátiles y reflectores LED' },
        { idCategoria: 15, nombre: 'Equipos Neumáticos', descripcion: 'Compresores de pistón, mangueras y rompedores' },
        { idCategoria: 16, nombre: 'Jardinería y Despeje', descripcion: 'Desbrozadoras, ahoyadoras y motosierras' },
        { idCategoria: 17, nombre: 'Seguridad y Señalización', descripcion: 'Vallas de contención, conos viales y señalización' },
        { idCategoria: 18, nombre: 'Climatización y Ventilación', descripcion: 'Extractores de aire de obra y ventiladores axiales' },
        { idCategoria: 19, nombre: 'Herramientas Inalámbricas', descripcion: 'Kits a batería 18V/20V de alta resistencia' },
        { idCategoria: 20, nombre: 'Pruebas Hidráulicas', descripcion: 'Bombas manuales y eléctricas para pruebas de presión' }
    ];

    const HERRAMIENTAS_SEED = [
        {
            idHerramienta: 1,
            idProveedor: 3,
            proveedorNombre: 'Constructora Equipos del Norte SAS',
            idCategoriaPrincipal: 1,
            categoriaPrincipal: 'Compactación y Suelos',
            nombre: 'Vibrocompactador Tipo Rana 5.5 HP',
            descripcion: 'Placa vibratoria reversible de alto impacto para compactación profunda de asfalto, zahorra y gravilla en obras civiles.',
            marca: 'Wacker Neuson',
            modelo: 'WP1550',
            numeroSerie: 'WN-RANA-2024-001',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 75000,
            valorReposicion: 4800000,
            imagenPrincipal: 'img/vibrocompactador.jpg',
            ubicacion: 'Bogotá / Chía',
            especificaciones: [
                { atributo: 'Potencia del Motor', valorConUnidad: '5.5 HP Gasolina 4T' },
                { atributo: 'Fuerza Centrífuga', valorConUnidad: '15 kN' },
                { atributo: 'Ancho de Placa', valorConUnidad: '500 mm' },
                { atributo: 'Peso Operativo', valorConUnidad: '90 kg' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 75000, valorPorDia: 75000, descripcionTipo: 'Tarifa diaria regular' },
                { tipoTarifa: 'SEMANAL', valor: 450000, valorPorDia: 64285, descripcionTipo: 'Tarifa semanal (14% dcto)' },
                { tipoTarifa: 'MENSUAL', valor: 1600000, valorPorDia: 53333, descripcionTipo: 'Tarifa mensual (28% dcto)' }
            ]
        },
        {
            idHerramienta: 2,
            idProveedor: 3,
            proveedorNombre: 'Constructora Equipos del Norte SAS',
            idCategoriaPrincipal: 1,
            categoriaPrincipal: 'Compactación y Suelos',
            nombre: 'Apisonador Canguro 4 Tiempos',
            descripcion: 'Bailarina compactadora de alto rendimiento para zanjas de tubería, cimentaciones estrechas y relleno de zapatas.',
            marca: 'Mikasa',
            modelo: 'MTX70',
            numeroSerie: 'MK-CANG-2024-002',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 85000,
            valorReposicion: 6200000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1579762715118-a6f1d4b934f1?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Medellín',
            especificaciones: [
                { atributo: 'Motor', valorConUnidad: 'Honda GX100 3.0 HP' },
                { atributo: 'Fuerza de Impacto', valorConUnidad: '14.9 kN' },
                { atributo: 'Golpes por Minuto', valorConUnidad: '650 - 695 bpm' },
                { atributo: 'Peso Operativo', valorConUnidad: '75 kg' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 85000, valorPorDia: 85000, descripcionTipo: 'Tarifa diaria regular' },
                { tipoTarifa: 'SEMANAL', valor: 510000, valorPorDia: 72857, descripcionTipo: 'Tarifa semanal' }
            ]
        },
        {
            idHerramienta: 3,
            idProveedor: 4,
            proveedorNombre: 'Maquinaria y Andamios Occidente SAS',
            idCategoriaPrincipal: 2,
            categoriaPrincipal: 'Demolición y Perforación',
            nombre: 'Martillo Demoledor Hexagonal 30 kg',
            descripcion: 'Rompedor de concreto reforzado con energía de impacto extrema de 68 Joules. Sistema Vibration Control anti-fatiga.',
            marca: 'Bosch',
            modelo: 'GSH 27 VC',
            numeroSerie: 'BS-DEM-2023-101',
            estadoOperativo: 'ALQUILADA',
            tarifaDiaria: 110000,
            valorReposicion: 8500000,
            imagenPrincipal: 'img/martillo_demoledor.jpg',
            ubicacion: 'Medellín',
            especificaciones: [
                { atributo: 'Potencia Absorbida', valorConUnidad: '2000 W' },
                { atributo: 'Energía de Impacto', valorConUnidad: '68 Joules' },
                { atributo: 'Portaherramientas', valorConUnidad: 'Hexagonal 28 mm' },
                { atributo: 'Peso Operativo', valorConUnidad: '29.5 kg' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 110000, valorPorDia: 110000, descripcionTipo: 'Tarifa diaria' },
                { tipoTarifa: 'SEMANAL', valor: 660000, valorPorDia: 94285, descripcionTipo: 'Tarifa semanal' }
            ]
        },
        {
            idHerramienta: 4,
            idProveedor: 4,
            proveedorNombre: 'Maquinaria y Andamios Occidente SAS',
            idCategoriaPrincipal: 2,
            categoriaPrincipal: 'Demolición y Perforación',
            nombre: 'Rotomartillo SDS Max 1500W',
            descripcion: 'Taladro percutor de trabajo pesado para perforaciones profundas en concreto y fijación de anclajes químicos estructurales.',
            marca: 'DeWalt',
            modelo: 'D25763K',
            numeroSerie: 'DW-ROTO-2023-202',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 55000,
            valorReposicion: 3100000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1504148455328-c376907d081c?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Bucaramanga',
            especificaciones: [
                { atributo: 'Potencia', valorConUnidad: '1500 Watts' },
                { atributo: 'Energía de Impacto', valorConUnidad: '15.5 Joules' },
                { atributo: 'Mandril', valorConUnidad: 'SDS Max de cambio rápido' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 55000, valorPorDia: 55000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 5,
            idProveedor: 5,
            proveedorNombre: 'Suministros Civiles de la Sabana SAS',
            idCategoriaPrincipal: 3,
            categoriaPrincipal: 'Corte y Desbaste',
            nombre: 'Cortadora de Pavimento 13 HP',
            descripcion: 'Cortadora de concreto y asfalto para disco diamantado de 14 a 18 pulgadas, tanque de agua integrado y guía frontal regulable.',
            marca: 'Husqvarna',
            modelo: 'FS400-LV',
            numeroSerie: 'HQ-CORT-2024-301',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 130000,
            valorReposicion: 9400000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Bogotá / Cota',
            especificaciones: [
                { atributo: 'Motor', valorConUnidad: 'Honda GX390 13 HP' },
                { atributo: 'Diámetro de Disco', valorConUnidad: '350 - 450 mm (14-18")' },
                { atributo: 'Profundidad de Corte', valorConUnidad: 'Hasta 162 mm' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 130000, valorPorDia: 130000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 6,
            idProveedor: 5,
            proveedorNombre: 'Suministros Civiles de la Sabana SAS',
            idCategoriaPrincipal: 4,
            categoriaPrincipal: 'Concreto y Mezclado',
            nombre: 'Mezcladora de Concreto 1.5 Bultos',
            descripcion: 'Trompo mezclador reforzado con motor a gasolina de 9 HP y cremallera de fundición modular para producción continua en obra.',
            marca: 'Kipor',
            modelo: 'KCM-350',
            numeroSerie: 'KP-MEZ-2023-401',
            estadoOperativo: 'ALQUILADA',
            tarifaDiaria: 65000,
            valorReposicion: 3900000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1541888946425-d0fbb186c5f9?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Cali',
            especificaciones: [
                { atributo: 'Capacidad de Mezcla', valorConUnidad: '350 Litros (1.5 bultos)' },
                { atributo: 'Motor', valorConUnidad: 'Gasolina 9.0 HP 4T' },
                { atributo: 'Ruedas', valorConUnidad: 'Neumáticas rin 13 con tiro' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 65000, valorPorDia: 65000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 7,
            idProveedor: 3,
            proveedorNombre: 'Constructora Equipos del Norte SAS',
            idCategoriaPrincipal: 4,
            categoriaPrincipal: 'Concreto y Mezclado',
            nombre: 'Vibrador de Concreto a Gasolina 5.5 HP',
            descripcion: 'Unidad motriz para manguera vibradora de aguja de 4 a 6 metros. Elimina burbujas de aire en columnas, losas y vigas.',
            marca: 'Enar',
            modelo: 'Dingo-Gas',
            numeroSerie: 'EN-VIB-2024-501',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 45000,
            valorReposicion: 2200000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1589939705384-5185137a7f0f?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Bogotá',
            especificaciones: [
                { atributo: 'Potencia', valorConUnidad: '5.5 HP 4T' },
                { atributo: 'Longitud Eje', valorConUnidad: '4 metros de guaya' },
                { atributo: 'Cabezal Aguja', valorConUnidad: '38 mm o 48 mm' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 45000, valorPorDia: 45000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 8,
            idProveedor: 4,
            proveedorNombre: 'Maquinaria y Andamios Occidente SAS',
            idCategoriaPrincipal: 5,
            categoriaPrincipal: 'Trabajo en Alturas',
            nombre: 'Sección Andamio Multidireccional Certificado',
            descripcion: 'Módulo de 2x2x1.5m galvanizado en caliente con certificación europea y norma OSHA para fachadas y trabajos de altura.',
            marca: 'Layher',
            modelo: 'Allround-HD',
            numeroSerie: 'LH-AND-2023-601',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 25000,
            valorReposicion: 1500000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Medellín',
            especificaciones: [
                { atributo: 'Norma', valorConUnidad: 'NTC 1644 / OSHA 1926' },
                { atributo: 'Carga Máxima', valorConUnidad: '6 kN/m²' },
                { atributo: 'Material', valorConUnidad: 'Acero galvanizado en caliente' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 25000, valorPorDia: 25000, descripcionTipo: 'Tarifa por módulo/día' }
            ]
        },
        {
            idHerramienta: 9,
            idProveedor: 6,
            proveedorNombre: 'RentaTools Santander Ltda',
            idCategoriaPrincipal: 6,
            categoriaPrincipal: 'Generación de Energía',
            nombre: 'Generador Eléctrico Diésel 7.5 kVA',
            descripcion: 'Planta de luz insonorizada monofásica y trifásica con encendido eléctrico y autonomía de 14 horas de operación continua.',
            marca: 'Caterpillar',
            modelo: 'RP7500E',
            numeroSerie: 'CAT-GEN-2024-701',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 150000,
            valorReposicion: 11000000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1621905251189-08b45d6a269e?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Bucaramanga',
            especificaciones: [
                { atributo: 'Potencia Continua', valorConUnidad: '7.5 kVA / 6.0 kW' },
                { atributo: 'Voltajes', valorConUnidad: '120V / 240V Selector' },
                { atributo: 'Nivel de Ruido', valorConUnidad: '68 dB a 7 metros' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 150000, valorPorDia: 150000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 10,
            idProveedor: 6,
            proveedorNombre: 'RentaTools Santander Ltda',
            idCategoriaPrincipal: 7,
            categoriaPrincipal: 'Bombeo y Achique',
            nombre: 'Motobomba Autocebante 3 Pulgadas',
            descripcion: 'Bomba de achique para desalojo rápido de aguas turbias con sólidos en suspensión en excavaciones profundas e inundaciones.',
            marca: 'Honda',
            modelo: 'WT30X',
            numeroSerie: 'HN-BOMB-2023-801',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 60000,
            valorReposicion: 3400000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1585338107529-13afc5f02586?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Barranquilla',
            especificaciones: [
                { atributo: 'Caudal Máximo', valorConUnidad: '1210 Litros/minuto' },
                { atributo: 'Diámetro Succión', valorConUnidad: '3" NPT' },
                { atributo: 'Altura Manométrica', valorConUnidad: '27 metros' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 60000, valorPorDia: 60000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 11,
            idProveedor: 7,
            proveedorNombre: 'Ferretería y Maquinaria El Progreso',
            idCategoriaPrincipal: 8,
            categoriaPrincipal: 'Soldadura y Corte Térmico',
            nombre: 'Soldador Inverter Multiproceso 250A',
            descripcion: 'Equipo de soldadura industrial para procesos electrodo revestido (SMAW), TIG y microalambre MIG/MAG con alimentación 220V.',
            marca: 'Lincoln Electric',
            modelo: 'PowerMIG-210',
            numeroSerie: 'LE-SOLD-2024-901',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 90000,
            valorReposicion: 5600000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Cali',
            especificaciones: [
                { atributo: 'Rango de Amperaje', valorConUnidad: '20 - 250 Amperios' },
                { atributo: 'Ciclo de Trabajo', valorConUnidad: '60% a 200A' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 90000, valorPorDia: 90000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 12,
            idProveedor: 7,
            proveedorNombre: 'Ferretería y Maquinaria El Progreso',
            idCategoriaPrincipal: 9,
            categoriaPrincipal: 'Topografía y Medición',
            nombre: 'Estación Total Láser 2 Segundos',
            descripcion: 'Instrumento topográfico electrónico de alta precisión con prisma de alcance 3.000m y medición láser sin prisma de 500m.',
            marca: 'Topcon',
            modelo: 'ES-105',
            numeroSerie: 'TP-TOP-2023-010',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 220000,
            valorReposicion: 18500000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1581092335397-9583fe92d232?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Bogotá',
            especificaciones: [
                { atributo: 'Precisión Angular', valorConUnidad: '2 Segundos' },
                { atributo: 'Memoria Interna', valorConUnidad: '10.000 puntos' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 220000, valorPorDia: 220000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 13,
            idProveedor: 3,
            proveedorNombre: 'Constructora Equipos del Norte SAS',
            idCategoriaPrincipal: 10,
            categoriaPrincipal: 'Limpieza Industrial',
            nombre: 'Hidrolavadora Industrial a Gasolina 3600 PSI',
            descripcion: 'Lavadora de ultra alta presión para lavado de losas de concreto, fachadas, remoción de óxido y limpieza de maquinaria pesada.',
            marca: 'Kärcher',
            modelo: 'HD 7/16-4 Cage',
            numeroSerie: 'KR-HID-2024-011',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 70000,
            valorReposicion: 4200000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Bucaramanga',
            especificaciones: [
                { atributo: 'Presión Máxima', valorConUnidad: '3600 PSI / 250 Bar' },
                { atributo: 'Caudal', valorConUnidad: '15 Litros/min' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 70000, valorPorDia: 70000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 14,
            idProveedor: 5,
            proveedorNombre: 'Suministros Civiles de la Sabana SAS',
            idCategoriaPrincipal: 11,
            categoriaPrincipal: 'Elevación y Carga',
            nombre: 'Malacate de Obra 1 Tonelada con Guaya 60m',
            descripcion: 'Elevador de carga vertical eléctrico para subir materiales, bultos de cemento y formaletas hasta pisos superiores.',
            marca: 'Imer',
            modelo: 'ET-1000N',
            numeroSerie: 'IM-MAL-2023-012',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 120000,
            valorReposicion: 8900000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1541888946425-d0fbb186c5f9?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Medellín',
            especificaciones: [
                { atributo: 'Capacidad Nominal', valorConUnidad: '1000 kg' },
                { atributo: 'Longitud de Guaya', valorConUnidad: '60 metros' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 120000, valorPorDia: 120000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 15,
            idProveedor: 4,
            proveedorNombre: 'Maquinaria y Andamios Occidente SAS',
            idCategoriaPrincipal: 12,
            categoriaPrincipal: 'Pintura y Recubrimientos',
            nombre: 'Equipo Pintura Airless Profesional',
            descripcion: 'Pulverizador eléctrico de pintura sin aire para aplicación masiva y uniforme de vinilos, esmaltes y epóxicos.',
            marca: 'Graco',
            modelo: 'UltraMax-II-490',
            numeroSerie: 'GC-AIR-2024-013',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 95000,
            valorReposicion: 7800000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1562259949-e8e7689d7828?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Bogotá',
            especificaciones: [
                { atributo: 'Presión Máxima', valorConUnidad: '3300 PSI' },
                { atributo: 'Rendimiento', valorConUnidad: '2.0 Litros/min' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 95000, valorPorDia: 95000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 16,
            idProveedor: 6,
            proveedorNombre: 'RentaTools Santander Ltda',
            idCategoriaPrincipal: 14,
            categoriaPrincipal: 'Iluminación de Obra',
            nombre: 'Torre de Iluminación LED Portátil 4x350W',
            descripcion: 'Mástil telescópico de 7 metros con 4 reflectores LED de alta eficiencia lumínica y planta diésel insonorizada para jornadas nocturnas.',
            marca: 'Doosan',
            modelo: 'LSC-60Hz',
            numeroSerie: 'DS-TOR-2023-014',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 280000,
            valorReposicion: 24000000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1509395062183-67c5ad6faff9?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Bucaramanga',
            especificaciones: [
                { atributo: 'Altura Mástil', valorConUnidad: '7.5 metros' },
                { atributo: 'Lúmenes', valorConUnidad: '180.000 lm' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 280000, valorPorDia: 280000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 17,
            idProveedor: 7,
            proveedorNombre: 'Ferretería y Maquinaria El Progreso',
            idCategoriaPrincipal: 15,
            categoriaPrincipal: 'Equipos Neumáticos',
            nombre: 'Compresor de Aire Portátil 185 CFM',
            descripcion: 'Compresor de tornillo rotativo diésel sobre tráiler para alimentación simultánea de hasta 3 rompedores neumáticos pesados.',
            marca: 'Atlas Copco',
            modelo: 'XAS-88',
            numeroSerie: 'AC-COMP-2024-015',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 320000,
            valorReposicion: 38000000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Cali',
            especificaciones: [
                { atributo: 'Caudal de Aire', valorConUnidad: '185 CFM / 5 m³/min' },
                { atributo: 'Presión de Trabajo', valorConUnidad: '7 Bar / 100 PSI' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 320000, valorPorDia: 320000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 18,
            idProveedor: 5,
            proveedorNombre: 'Suministros Civiles de la Sabana SAS',
            idCategoriaPrincipal: 3,
            categoriaPrincipal: 'Corte y Desbaste',
            nombre: 'Pulidora Angular Industrial 9 Pulgadas',
            descripcion: 'Esmeriladora angular de alto desempeño para corte de aceros, vigas IPN y desbaste de cordones de soldadura pesada.',
            marca: 'Makita',
            modelo: 'GA9020',
            numeroSerie: 'MK-PUL-2023-016',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 30000,
            valorReposicion: 950000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1504148455328-c376907d081c?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Bogotá',
            especificaciones: [
                { atributo: 'Potencia', valorConUnidad: '2200 Watts' },
                { atributo: 'Diámetro de Disco', valorConUnidad: '230 mm (9")' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 30000, valorPorDia: 30000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 19,
            idProveedor: 3,
            proveedorNombre: 'Constructora Equipos del Norte SAS',
            idCategoriaPrincipal: 18,
            categoriaPrincipal: 'Climatización y Ventilación',
            nombre: 'Extractor / Ventilador Axial de Obra 16 Pulgadas',
            descripcion: 'Extractor neumático y eléctrico para extracción rápida de polvos, humos de soldadura y ventilación en sótanos y túneles confinados.',
            marca: 'Canarm',
            modelo: 'AX-400',
            numeroSerie: 'CN-EXT-2024-017',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 40000,
            valorReposicion: 1800000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Bogotá',
            especificaciones: [
                { atributo: 'Diámetro', valorConUnidad: '400 mm (16")' },
                { atributo: 'Caudal de Aire', valorConUnidad: '4800 m³/hora' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 40000, valorPorDia: 40000, descripcionTipo: 'Tarifa diaria' }
            ]
        },
        {
            idHerramienta: 20,
            idProveedor: 4,
            proveedorNombre: 'Maquinaria y Andamios Occidente SAS',
            idCategoriaPrincipal: 20,
            categoriaPrincipal: 'Pruebas Hidráulicas',
            nombre: 'Bomba Hidrostática de Prueba 50 Bar',
            descripcion: 'Bomba manual de precisión para pruebas de estanqueidad y presión en redes hidráulicas de gas, agua potable y sistemas contra incendio.',
            marca: 'Rothenberger',
            modelo: 'RP-50-S',
            numeroSerie: 'RB-PRU-2023-018',
            estadoOperativo: 'DISPONIBLE',
            tarifaDiaria: 35000,
            valorReposicion: 1200000,
            imagenPrincipal: 'https://images.unsplash.com/photo-1585338107529-13afc5f02586?auto=format&fit=crop&w=800&q=80',
            ubicacion: 'Medellín',
            especificaciones: [
                { atributo: 'Presión de Prueba', valorConUnidad: '0 - 50 Bar (0 - 725 PSI)' },
                { atributo: 'Capacidad Tanque', valorConUnidad: '12 Litros' }
            ],
            tarifas: [
                { tipoTarifa: 'DIARIA', valor: 35000, valorPorDia: 35000, descripcionTipo: 'Tarifa diaria' }
            ]
        }
    ];

    const USUARIOS_SEED = [
        {
            idUsuario: 1,
            nombreCompleto: 'Carlos Andrés Pérez',
            email: 'admin.perez@rentatool.com',
            roles: ['ROLE_ADMIN', 'ROLE_SUPER_ADMIN'],
            telefono: '3105550101',
            direccion: 'Calle 100 # 15-20, Bogotá',
            activo: true
        },
        {
            idUsuario: 4,
            nombreCompleto: 'Maquinaria y Andamios Occidente SAS',
            email: 'ventas@maquiantioquia.com',
            roles: ['ROLE_PROVEEDOR'],
            telefono: '3135550104',
            direccion: 'Calle 50 # 45-30, Medellín',
            activo: true,
            verificado: true
        },
        {
            idUsuario: 8,
            nombreCompleto: 'Daniel Felipe Osorio',
            email: 'daniel.osorio@gmail.com',
            roles: ['ROLE_CLIENTE'],
            telefono: '3175550108',
            direccion: 'Calle 80 # 68-12, Bogotá',
            activo: true
        },
        {
            idUsuario: 9,
            nombreCompleto: 'Juan Camilo García',
            email: 'camilo.garcia@gmail.com',
            roles: ['ROLE_CLIENTE'],
            telefono: '3185550109',
            direccion: 'Carrera 33 # 48-19, Bucaramanga',
            activo: true
        }
    ];

    const RESERVAS_SEED = [
        {
            idReserva: 101,
            codigoReserva: 'RES-2026-001',
            idCliente: 9,
            clienteNombre: 'Juan Camilo García',
            fechaInicio: '2026-10-10',
            fechaFin: '2026-10-15',
            estado: 'EN_CURSO',
            montoTotalEstimado: 375000,
            depositoGarantia: 100000,
            notasCliente: 'Obra civil Carrera 33 Bucaramanga. Entrega a primera hora.',
            items: [
                {
                    idHerramienta: 1,
                    nombreHerramienta: 'Vibrocompactador Tipo Rana 5.5 HP',
                    tarifaDiaria: 75000,
                    cantidad: 1,
                    dias: 5,
                    subtotal: 375000
                }
            ]
        },
        {
            idReserva: 102,
            codigoReserva: 'RES-2026-002',
            idCliente: 9,
            clienteNombre: 'Juan Camilo García',
            fechaInicio: '2026-10-18',
            fechaFin: '2026-10-22',
            estado: 'APROBADA',
            montoTotalEstimado: 440000,
            depositoGarantia: 120000,
            notasCliente: 'Demolición de losa en sótano residencial.',
            items: [
                {
                    idHerramienta: 3,
                    nombreHerramienta: 'Martillo Demoledor Hexagonal 30 kg',
                    tarifaDiaria: 110000,
                    cantidad: 1,
                    dias: 4,
                    subtotal: 440000
                }
            ]
        },
        {
            idReserva: 103,
            codigoReserva: 'RES-2026-003',
            idCliente: 9,
            clienteNombre: 'Juan Camilo García',
            fechaInicio: '2026-10-25',
            fechaFin: '2026-10-28',
            estado: 'SOLICITADA',
            montoTotalEstimado: 165000,
            depositoGarantia: 100000,
            notasCliente: 'Taladro percutor para anclaje de columnas.',
            items: [
                {
                    idHerramienta: 4,
                    nombreHerramienta: 'Rotomartillo SDS Max 1500W',
                    tarifaDiaria: 55000,
                    cantidad: 1,
                    dias: 3,
                    subtotal: 165000
                }
            ]
        },
        {
            idReserva: 104,
            codigoReserva: 'RES-2026-004',
            idCliente: 9,
            clienteNombre: 'Juan Camilo García',
            fechaInicio: '2026-09-20',
            fechaFin: '2026-09-25',
            estado: 'FINALIZADA',
            montoTotalEstimado: 325000,
            depositoGarantia: 100000,
            notasCliente: 'Mezcla de concreto en obra El Prado.',
            items: [
                {
                    idHerramienta: 6,
                    nombreHerramienta: 'Mezcladora de Concreto 1.5 Bultos',
                    tarifaDiaria: 65000,
                    cantidad: 1,
                    dias: 5,
                    subtotal: 325000
                }
            ]
        }
    ];

    const PAGOS_SEED = [
        {
            idPago: 201,
            idReserva: 101,
            monto: 375000,
            metodoPago: 'TARJETA_CREDITO_VISA',
            estadoPago: 'APROBADO',
            fechaPago: '2026-10-10T08:30:00',
            numeroTransaccion: 'TRX-VISA-992812',
            factura: {
                idFactura: 301,
                numeroFactura: 'FAC-2026-00089',
                subtotal: 315126,
                iva: 59874,
                total: 375000,
                cufe: '404e635266556a586e3272357538782f413f4428472b4b6250645367566b5970',
                fechaEmision: '2026-10-10T08:30:15'
            }
        }
    ];

    const DEVOLUCIONES_SEED = [
        {
            idDevolucion: 401,
            idReserva: 104,
            codigoReserva: 'RES-2026-004',
            fechaRetornoReal: '2026-09-25T17:00:00',
            diasRetraso: 0,
            estadoFisicoGeneral: 'BUENO',
            cargosPorMora: 0,
            cargosPorDano: 0,
            observaciones: 'Herramienta devuelta limpia, en buen estado operativo y con accesorios completos.',
            reportesDano: []
        }
    ];

    // ------------------------------------------------------------------
    // Helpers de persistencia
    // ------------------------------------------------------------------

    function cargar(clave, porDefecto) {
        try {
            const v = localStorage.getItem(clave);
            return v ? JSON.parse(v) : porDefecto;
        } catch (e) {
            return porDefecto;
        }
    }

    function guardar(clave, datos) {
        try {
            localStorage.setItem(clave, JSON.stringify(datos));
        } catch (e) {
            console.error('Error al guardar en MockStore:', e);
        }
    }

    function inicializar() {
        if (!localStorage.getItem(KEY_CATEGORIAS)) {
            guardar(KEY_CATEGORIAS, CATEGORIAS_SEED);
        }
        if (!localStorage.getItem(KEY_HERRAMIENTAS)) {
            guardar(KEY_HERRAMIENTAS, HERRAMIENTAS_SEED);
        }
        if (!localStorage.getItem(KEY_USUARIOS)) {
            guardar(KEY_USUARIOS, USUARIOS_SEED);
        } else {
            const usrs = cargar(KEY_USUARIOS, USUARIOS_SEED);
            if (!usrs.some(u => u.email === 'daniel.osorio@gmail.com')) {
                usrs.push({
                    idUsuario: 8,
                    nombreCompleto: 'Daniel Felipe Osorio',
                    email: 'daniel.osorio@gmail.com',
                    roles: ['ROLE_CLIENTE'],
                    telefono: '3175550108',
                    direccion: 'Calle 80 # 68-12, Bogotá',
                    activo: true
                });
                guardar(KEY_USUARIOS, usrs);
            }
        }
        if (!localStorage.getItem(KEY_RESERVAS)) {
            guardar(KEY_RESERVAS, RESERVAS_SEED);
        }
        if (!localStorage.getItem(KEY_PAGOS)) {
            guardar(KEY_PAGOS, PAGOS_SEED);
        }
        if (!localStorage.getItem(KEY_DEVOLUCIONES)) {
            guardar(KEY_DEVOLUCIONES, DEVOLUCIONES_SEED);
        }
    }

    // Inicializar de inmediato
    inicializar();

    // ------------------------------------------------------------------
    // Operaciones Mock
    // ------------------------------------------------------------------

    return {
        inicializar,

        // Categorias
        listarCategorias: () => {
            inicializar();
            return cargar(KEY_CATEGORIAS, CATEGORIAS_SEED);
        },

        // Herramientas y Catalogo
        buscarCatalogo: (filtros = {}) => {
            inicializar();
            let lista = cargar(KEY_HERRAMIENTAS, HERRAMIENTAS_SEED);

            if (filtros.texto) {
                const t = filtros.texto.toLowerCase();
                lista = lista.filter(h =>
                    h.nombre.toLowerCase().includes(t) ||
                    h.marca.toLowerCase().includes(t) ||
                    h.modelo.toLowerCase().includes(t) ||
                    h.descripcion.toLowerCase().includes(t)
                );
            }
            if (filtros.idCategoria) {
                const idCat = Number(filtros.idCategoria);
                lista = lista.filter(h => h.idCategoriaPrincipal === idCat);
            }
            if (filtros.precioMinimo) {
                const min = Number(filtros.precioMinimo);
                lista = lista.filter(h => h.tarifaDiaria >= min);
            }
            if (filtros.precioMaximo) {
                const max = Number(filtros.precioMaximo);
                lista = lista.filter(h => h.tarifaDiaria <= max);
            }
            if (filtros.ubicacion) {
                const u = filtros.ubicacion.toLowerCase();
                lista = lista.filter(h => (h.ubicacion || '').toLowerCase().includes(u));
            }
            if (filtros.soloDisponibles === 'true' || filtros.soloDisponibles === true) {
                lista = lista.filter(h => h.estadoOperativo === 'DISPONIBLE');
            }

            return lista;
        },

        obtenerHerramienta: (id) => {
            inicializar();
            const lista = cargar(KEY_HERRAMIENTAS, HERRAMIENTAS_SEED);
            const h = lista.find(x => x.idHerramienta === Number(id));
            if (!h) throw new Error('Herramienta no encontrada (ID ' + id + ')');
            return h;
        },

        guardarHerramienta: (datos) => {
            inicializar();
            const lista = cargar(KEY_HERRAMIENTAS, HERRAMIENTAS_SEED);
            if (datos.idHerramienta) {
                const idx = lista.findIndex(x => x.idHerramienta === Number(datos.idHerramienta));
                if (idx !== -1) {
                    lista[idx] = { ...lista[idx], ...datos };
                    guardar(KEY_HERRAMIENTAS, lista);
                    return lista[idx];
                }
            }
            const nueva = {
                ...datos,
                idHerramienta: Date.now(),
                estadoOperativo: datos.estadoOperativo || 'DISPONIBLE',
                imagenPrincipal: datos.imagenPrincipal || 'https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=800&q=80',
                tarifas: [
                    { tipoTarifa: 'DIARIA', valor: Number(datos.tarifaDiaria) || 50000, valorPorDia: Number(datos.tarifaDiaria) || 50000, descripcionTipo: 'Tarifa diaria' }
                ],
                especificaciones: []
            };
            lista.unshift(nueva);
            guardar(KEY_HERRAMIENTAS, lista);
            return nueva;
        },

        eliminarHerramienta: (id) => {
            inicializar();
            let lista = cargar(KEY_HERRAMIENTAS, HERRAMIENTAS_SEED);
            lista = lista.filter(x => x.idHerramienta !== Number(id));
            guardar(KEY_HERRAMIENTAS, lista);
            return true;
        },

        // Autenticacion
        login: (email, password) => {
            inicializar();
            const usuarios = cargar(KEY_USUARIOS, USUARIOS_SEED);
            const u = usuarios.find(x => x.email.toLowerCase() === email.toLowerCase());
            if (!u) {
                // Para maxima comodidad en evaluacion, si el email es nuevo lo acepta como cliente
                return {
                    idUsuario: 99,
                    nombreCompleto: email.split('@')[0],
                    email: email,
                    roles: ['ROLE_CLIENTE'],
                    accessToken: 'demo_token_' + Date.now(),
                    refreshToken: 'demo_refresh_' + Date.now()
                };
            }
            return {
                idUsuario: u.idUsuario,
                nombreCompleto: u.nombreCompleto,
                email: u.email,
                roles: u.roles,
                accessToken: 'demo_token_' + Date.now(),
                refreshToken: 'demo_refresh_' + Date.now()
            };
        },

        // Reservas
        listarReservas: () => {
            inicializar();
            return cargar(KEY_RESERVAS, RESERVAS_SEED);
        },

        crearReserva: (comando) => {
            inicializar();
            const lista = cargar(KEY_RESERVAS, RESERVAS_SEED);
            const tools = cargar(KEY_HERRAMIENTAS, HERRAMIENTAS_SEED);

            const f1 = new Date(comando.fechaInicio);
            const f2 = new Date(comando.fechaFin);
            let dias = Math.ceil((f2 - f1) / (1000 * 60 * 60 * 24));
            if (dias <= 0) dias = 1;

            const item = comando.items && comando.items[0];
            const herramienta = item ? tools.find(t => t.idHerramienta === Number(item.idHerramienta)) : null;
            const tarifa = herramienta ? herramienta.tarifaDiaria : 75000;
            const subtotal = tarifa * dias;
            const deposito = Math.max(100000, Math.round(subtotal * 0.2));

            const nuevaReserva = {
                idReserva: Date.now(),
                codigoReserva: 'RES-2026-' + Math.floor(100 + Math.random() * 900),
                idCliente: comando.idCliente || 9,
                clienteNombre: 'Juan Camilo García',
                fechaInicio: comando.fechaInicio,
                fechaFin: comando.fechaFin,
                estado: 'SOLICITADA',
                montoTotalEstimado: subtotal,
                depositoGarantia: deposito,
                notasCliente: comando.notasCliente || '',
                items: [
                    {
                        idHerramienta: herramienta ? herramienta.idHerramienta : 1,
                        nombreHerramienta: herramienta ? herramienta.nombre : 'Maquinaria de Construcción',
                        tarifaDiaria: tarifa,
                        cantidad: 1,
                        dias: dias,
                        subtotal: subtotal
                    }
                ]
            };

            lista.unshift(nuevaReserva);
            guardar(KEY_RESERVAS, lista);
            return nuevaReserva;
        },

        actualizarEstadoReserva: (idReserva, nuevoEstado) => {
            inicializar();
            const lista = cargar(KEY_RESERVAS, RESERVAS_SEED);
            const r = lista.find(x => x.idReserva === Number(idReserva));
            if (r) {
                r.estado = nuevoEstado;
                guardar(KEY_RESERVAS, lista);
                return r;
            }
            throw new Error('Reserva no encontrada');
        },

        // Pagos y Facturas
        procesarPago: (comando) => {
            inicializar();
            const pagos = cargar(KEY_PAGOS, PAGOS_SEED);
            const reservas = cargar(KEY_RESERVAS, RESERVAS_SEED);

            const r = reservas.find(x => x.idReserva === Number(comando.idReserva));
            const monto = Number(comando.monto) || (r ? r.montoTotalEstimado : 200000);

            // Calculo legal DIAN de Colombia: IVA 19%
            const subtotal = Math.round(monto / 1.19);
            const iva = monto - subtotal;

            const factura = {
                idFactura: Date.now() + 1,
                numeroFactura: 'FAC-2026-00' + Math.floor(100 + Math.random() * 900),
                subtotal: subtotal,
                iva: iva,
                total: monto,
                cufe: 'dian-cufe-' + Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15),
                fechaEmision: new Date().toISOString()
            };

            const nuevoPago = {
                idPago: Date.now(),
                idReserva: Number(comando.idReserva),
                monto: monto,
                metodoPago: comando.metodoPago || 'TARJETA_CREDITO',
                estadoPago: 'APROBADO',
                fechaPago: new Date().toISOString(),
                numeroTransaccion: 'TRX-PASARELA-' + Math.floor(100000 + Math.random() * 900000),
                factura: factura
            };

            pagos.unshift(nuevoPago);
            guardar(KEY_PAGOS, pagos);

            if (r && r.estado === 'SOLICITADA') {
                r.estado = 'APROBADA';
                guardar(KEY_RESERVAS, reservas);
            }

            return nuevoPago;
        },

        obtenerFacturaPorReserva: (idReserva) => {
            inicializar();
            const pagos = cargar(KEY_PAGOS, PAGOS_SEED);
            const p = pagos.find(x => x.idReserva === Number(idReserva));
            if (p && p.factura) return p.factura;

            // Si no habia factura simulada, generarla al vuelo
            const reservas = cargar(KEY_RESERVAS, RESERVAS_SEED);
            const r = reservas.find(x => x.idReserva === Number(idReserva));
            const total = r ? r.montoTotalEstimado : 350000;
            const subtotal = Math.round(total / 1.19);
            return {
                idFactura: Date.now(),
                numeroFactura: 'FAC-2026-00' + (r ? r.idReserva : '99'),
                subtotal: subtotal,
                iva: total - subtotal,
                total: total,
                cufe: 'dian-cufe-auto-' + Date.now(),
                fechaEmision: new Date().toISOString()
            };
        },

        // Devoluciones y Daños
        listarDevoluciones: () => {
            inicializar();
            return cargar(KEY_DEVOLUCIONES, DEVOLUCIONES_SEED);
        },

        registrarDevolucion: (comando) => {
            inicializar();
            const devoluciones = cargar(KEY_DEVOLUCIONES, DEVOLUCIONES_SEED);
            const reservas = cargar(KEY_RESERVAS, RESERVAS_SEED);

            const r = reservas.find(x => x.idReserva === Number(comando.idReserva));
            if (r) {
                r.estado = 'FINALIZADA';
                guardar(KEY_RESERVAS, reservas);
            }

            const nueva = {
                idDevolucion: Date.now(),
                idReserva: Number(comando.idReserva),
                codigoReserva: r ? r.codigoReserva : 'RES-' + comando.idReserva,
                fechaRetornoReal: comando.fechaRetornoReal || new Date().toISOString(),
                diasRetraso: Number(comando.diasRetraso) || 0,
                estadoFisicoGeneral: comando.estadoFisicoGeneral || 'BUENO',
                cargosPorMora: Number(comando.cargosPorMora) || 0,
                cargosPorDano: Number(comando.cargosPorDano) || 0,
                observaciones: comando.observaciones || 'Inspección completada con éxito.',
                reportesDano: []
            };

            devoluciones.unshift(nueva);
            guardar(KEY_DEVOLUCIONES, devoluciones);
            return nueva;
        },

        agregarDano: (idDevolucion, dano) => {
            inicializar();
            const devoluciones = cargar(KEY_DEVOLUCIONES, DEVOLUCIONES_SEED);
            const d = devoluciones.find(x => x.idDevolucion === Number(idDevolucion));
            if (d) {
                if (!d.reportesDano) d.reportesDano = [];
                d.reportesDano.push({
                    idReporteDano: Date.now(),
                    descripcionAveria: dano.descripcionAveria,
                    costoReparacionEstimado: Number(dano.costoReparacionEstimado) || 0,
                    gravedad: dano.gravedad || 'MEDIA',
                    evidencias: dano.evidencias || [
                        { urlFoto: 'https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=400&q=80', descripcionFoto: 'Evidencia fotográfica inspección' }
                    ]
                });
                d.cargosPorDano = (d.cargosPorDano || 0) + (Number(dano.costoReparacionEstimado) || 0);
                guardar(KEY_DEVOLUCIONES, devoluciones);
                return d;
            }
            throw new Error('Devolución no encontrada');
        },

        // Usuarios
        listarUsuarios: (rolFiltro) => {
            inicializar();
            let u = cargar(KEY_USUARIOS, USUARIOS_SEED);
            if (rolFiltro) {
                const r = rolFiltro.startsWith('ROLE_') ? rolFiltro : 'ROLE_' + rolFiltro;
                u = u.filter(x => x.roles.includes(r));
            }
            return u;
        },

        cambiarEstadoUsuario: (idUsuario, activo) => {
            inicializar();
            const u = cargar(KEY_USUARIOS, USUARIOS_SEED);
            const usr = u.find(x => x.idUsuario === Number(idUsuario));
            if (usr) {
                usr.activo = activo;
                guardar(KEY_USUARIOS, u);
                return usr;
            }
            throw new Error('Usuario no encontrado');
        },

        verificarProveedor: (idUsuario) => {
            inicializar();
            const u = cargar(KEY_USUARIOS, USUARIOS_SEED);
            const usr = u.find(x => x.idUsuario === Number(idUsuario));
            if (usr) {
                usr.verificado = true;
                guardar(KEY_USUARIOS, u);
                return usr;
            }
            throw new Error('Proveedor no encontrado');
        },

        // Dashboard
        obtenerDashboard: () => {
            inicializar();
            const tools = cargar(KEY_HERRAMIENTAS, HERRAMIENTAS_SEED);
            const reservas = cargar(KEY_RESERVAS, RESERVAS_SEED);
            const pagos = cargar(KEY_PAGOS, PAGOS_SEED);
            const devoluciones = cargar(KEY_DEVOLUCIONES, DEVOLUCIONES_SEED);

            let totalRecaudado = pagos.reduce((sum, p) => sum + (Number(p.monto) || 0), 0);
            if (totalRecaudado === 0) totalRecaudado = 2850000;

            const subtotal = Math.round(totalRecaudado / 1.19);
            const iva = totalRecaudado - subtotal;

            return {
                resumenFinanciero: {
                    totalRecaudado: totalRecaudado,
                    totalPagosExitosos: Math.max(pagos.length, 14),
                    totalSubtotal: subtotal,
                    totalIva: iva,
                    totalFacturasEmitidas: Math.max(pagos.length, 12),
                    totalCargosMora: 180000,
                    totalCargosDano: 320000
                },
                metricasOperativas: {
                    totalHerramientas: tools.length,
                    herramientasDisponibles: tools.filter(t => t.estadoOperativo === 'DISPONIBLE').length,
                    herramientasAlquiladas: tools.filter(t => t.estadoOperativo === 'ALQUILADA').length,
                    herramientasEnMantenimiento: tools.filter(t => t.estadoOperativo === 'MANTENIMIENTO').length,
                    tasaIncidenciasDanoPorcentaje: 4.8,
                    totalReportesDano: 2,
                    totalDevoluciones: Math.max(devoluciones.length, 18)
                },
                topHerramientasPopulares: [
                    { nombre: 'Vibrocompactador Tipo Rana 5.5 HP', marca: 'Wacker Neuson', categoria: 'Compactación', totalAlquileres: 28, ingresosGenerados: 2100000 },
                    { nombre: 'Martillo Demoledor Hexagonal 30 kg', marca: 'Bosch', categoria: 'Demolición', totalAlquileres: 24, ingresosGenerados: 2640000 },
                    { nombre: 'Generador Eléctrico Diésel 7.5 kVA', marca: 'Caterpillar', categoria: 'Generación', totalAlquileres: 19, ingresosGenerados: 2850000 },
                    { nombre: 'Cortadora de Pavimento 13 HP', marca: 'Husqvarna', categoria: 'Corte', totalAlquileres: 15, ingresosGenerados: 1950000 },
                    { nombre: 'Sección Andamio Multidireccional', marca: 'Layher', categoria: 'Alturas', totalAlquileres: 34, ingresosGenerados: 850000 }
                ],
                topClientesFrecuentes: [
                    { nombreCompleto: 'Juan Camilo García', email: 'camilo.garcia@gmail.com', totalReservas: 6, totalGastado: 1450000 },
                    { nombreCompleto: 'Constructora Monteverde SAS', email: 'proyectos@monteverde.co', totalReservas: 14, totalGastado: 8900000 },
                    { nombreCompleto: 'Obras Civiles Arismendi', email: 'gerencia@obrasarismendi.com', totalReservas: 8, totalGastado: 4300000 },
                    { nombreCompleto: 'Inversiones Bolívar', email: 'compras@inverbolivar.com', totalReservas: 11, totalGastado: 6200000 },
                    { nombreCompleto: 'Daniel Felipe Osorio', email: 'daniel.osorio@gmail.com', totalReservas: 4, totalGastado: 980000 }
                ],
                ingresosPorProveedor: [
                    { idProveedor: 3, nombreProveedor: 'Constructora Equipos del Norte SAS', totalHerramientasRegistradas: 8, totalAlquileres: 45, ingresosGenerados: 4850000 },
                    { idProveedor: 4, nombreProveedor: 'Maquinaria y Andamios Occidente SAS', totalHerramientasRegistradas: 6, totalAlquileres: 38, ingresosGenerados: 3950000 },
                    { idProveedor: 6, nombreProveedor: 'RentaTools Santander Ltda', totalHerramientasRegistradas: 4, totalAlquileres: 29, ingresosGenerados: 3450000 }
                ]
            };
        }
    };
})();
