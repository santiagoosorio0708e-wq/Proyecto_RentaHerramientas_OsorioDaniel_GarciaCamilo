package com.rentaherramientas.common.config;

import com.rentaherramientas.modules.identidad.domain.model.valueobjects.TipoDocumento;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity.RolJpaEntity;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity.UsuarioJpaEntity;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.repository.SpringDataRolRepository;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.repository.SpringDataUsuarioRepository;
import com.rentaherramientas.modules.inventario.domain.model.EstadoHerramienta;
import com.rentaherramientas.modules.inventario.domain.model.TipoTarifa;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.*;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.repository.SpringDataCategoriaRepository;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.repository.SpringDataHerramientaRepository;
import com.rentaherramientas.modules.pagos.domain.model.EstadoFactura;
import com.rentaherramientas.modules.pagos.domain.model.EstadoPago;
import com.rentaherramientas.modules.pagos.domain.model.TipoPago;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.entity.*;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.repository.SpringDataFacturaRepository;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.repository.SpringDataMetodoPagoRepository;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.repository.SpringDataPagoRepository;
import com.rentaherramientas.modules.reservas.domain.model.EstadoReserva;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.entity.DetalleReservaJpaEntity;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.entity.ReservaJpaEntity;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.repository.SpringDataReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Inicializador y Sembrador de Datos para la Base de Datos SQLite.
 * Si la base de datos se encuentra vacía al arrancar, siembra automáticamente
 * roles, usuarios con contraseñas BCrypt, categorías, herramientas con especificaciones
 * e imágenes, métodos de pago y reservas iniciales con facturación.
 *
 * Si ya existen datos, no realiza cambios, garantizando 100% la permanencia
 * de todas las modificaciones realizadas desde la interfaz web o API REST.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SpringDataRolRepository rolRepository;
    private final SpringDataUsuarioRepository usuarioRepository;
    private final SpringDataCategoriaRepository categoriaRepository;
    private final SpringDataHerramientaRepository herramientaRepository;
    private final SpringDataMetodoPagoRepository metodoPagoRepository;
    private final SpringDataReservaRepository reservaRepository;
    private final SpringDataPagoRepository pagoRepository;
    private final SpringDataFacturaRepository facturaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (rolRepository.count() > 0) {
            log.info("✓ Base de datos SQLite ya inicializada con permanencia activa. Registros preservados.");
            return;
        }

        log.info("🚀 Inicializando base de datos SQLite con datos maestros del sistema...");

        // 1. Roles
        RolJpaEntity rolAdmin = rolRepository.save(RolJpaEntity.builder()
                .nombre("ROLE_ADMIN")
                .descripcion("Administrador general del sistema con acceso total")
                .build());

        RolJpaEntity rolProveedor = rolRepository.save(RolJpaEntity.builder()
                .nombre("ROLE_PROVEEDOR")
                .descripcion("Proveedor de maquinaria y herramientas")
                .build());

        RolJpaEntity rolCliente = rolRepository.save(RolJpaEntity.builder()
                .nombre("ROLE_CLIENTE")
                .descripcion("Cliente particular o contratista que alquila equipos")
                .build());

        rolRepository.save(RolJpaEntity.builder().nombre("ROLE_SUPER_ADMIN").descripcion("Super Administrador").build());
        rolRepository.save(RolJpaEntity.builder().nombre("ROLE_PROVEEDOR_PREMIUM").descripcion("Proveedor Certificado").build());
        rolRepository.save(RolJpaEntity.builder().nombre("ROLE_CLIENTE_EMPRESARIAL").descripcion("Empresa Constructora").build());

        // 2. Usuarios del Sistema (Contraseña unificada: Password123)
        String passHash = passwordEncoder.encode("Password123");

        UsuarioJpaEntity admin = usuarioRepository.save(UsuarioJpaEntity.builder()
                .nombreCompleto("Carlos Andres Perez")
                .email("admin@rentatools.com")
                .passwordHash(passHash)
                .telefono("3105550101")
                .direccion("Calle 100 # 15-20, Bogota")
                .tipoDocumento(TipoDocumento.CC)
                .numeroDocumento("1010202301")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .roles(Set.of(rolAdmin))
                .build());

        UsuarioJpaEntity proveedor = usuarioRepository.save(UsuarioJpaEntity.builder()
                .nombreCompleto("Constructora Equipos del Norte SAS")
                .email("proveedor@rentatools.com")
                .passwordHash(passHash)
                .telefono("3125550103")
                .direccion("Autopista Norte Km 18, Chia")
                .tipoDocumento(TipoDocumento.NIT)
                .numeroDocumento("900123456-1")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .roles(Set.of(rolProveedor))
                .build());

        UsuarioJpaEntity cliente = usuarioRepository.save(UsuarioJpaEntity.builder()
                .nombreCompleto("Daniel Felipe Osorio")
                .email("cliente@rentatools.com")
                .passwordHash(passHash)
                .telefono("3175550108")
                .direccion("Calle 80 # 68-12, Bogota")
                .tipoDocumento(TipoDocumento.CC)
                .numeroDocumento("1098765432")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .roles(Set.of(rolCliente))
                .build());

        UsuarioJpaEntity camilo = usuarioRepository.save(UsuarioJpaEntity.builder()
                .nombreCompleto("Juan Camilo Garcia")
                .email("camilo@rentatools.com")
                .passwordHash(passHash)
                .telefono("3185550109")
                .direccion("Carrera 33 # 48-19, Bucaramanga")
                .tipoDocumento(TipoDocumento.CC)
                .numeroDocumento("1098765433")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .roles(Set.of(rolCliente))
                .build());

        // 3. Métodos de Pago Oficiales
        MetodoPagoJpaEntity mpPse = metodoPagoRepository.save(MetodoPagoJpaEntity.builder()
                .nombre("PSE (Pagos Seguros en Línea)")
                .descripcion("Débito bancario en línea ACH con certificación inmediata")
                .activo(true)
                .build());

        MetodoPagoJpaEntity mpTarjeta = metodoPagoRepository.save(MetodoPagoJpaEntity.builder()
                .nombre("Tarjeta de Crédito")
                .descripcion("Tarjetas Visa, Mastercard y American Express con verificación 3D Secure")
                .activo(true)
                .build());

        MetodoPagoJpaEntity mpTransferencia = metodoPagoRepository.save(MetodoPagoJpaEntity.builder()
                .nombre("Transferencia Bancaria")
                .descripcion("Transferencia electrónica Bancolombia / Davivienda / Nequi")
                .activo(true)
                .build());

        metodoPagoRepository.save(MetodoPagoJpaEntity.builder()
                .nombre("Efectivo / En Bodega")
                .descripcion("Pago en efectivo o datáfono contra entrega en punto logístico")
                .activo(true)
                .build());

        // 4. Categorías de Herramientas
        Map<Integer, CategoriaJpaEntity> cats = new HashMap<>();
        String[][] catData = {
                {"Compactación y Suelos", "Vibrocompactadores, bailarinas y rodillos manuales"},
                {"Demolición y Perforación", "Martillos demoledores, rotomartillos y taladros de impacto"},
                {"Corte y Desbaste", "Cortadoras de concreto, pulidoras y sierras circulares"},
                {"Concreto y Mezclado", "Mezcladoras de bulto, tolvas y vibradores de concreto"},
                {"Trabajo en Alturas", "Andamios certificados, arneses y plataformas elevadoras"},
                {"Generación de Energía", "Plantas eléctricas diésel, a gasolina e inversores"},
                {"Bombeo y Achique", "Motobombas sumergibles, autocebantes y de lodos"},
                {"Soldadura y Corte Térmico", "Soldadores inverter, equipos oxiacetilénicos y MIG/TIG"},
                {"Topografía y Medición", "Niveles ópticos, estaciones totales y distanciómetros láser"},
                {"Limpieza Industrial", "Hidrolavadoras de alta presión y aspiradoras de obra"},
                {"Elevación y Carga", "Polipastos, malacates de obra y diferenciales manuales"},
                {"Pintura y Recubrimientos", "Equipos airless, compresores de aire y pistolas neumáticas"},
                {"Fijación y Ensamble", "Pistolas de fijación neumática y clavadoras"},
                {"Iluminación de Obra", "Torres de iluminación portátiles y reflectores LED"},
                {"Equipos Neumáticos", "Compresores de pistón, mangueras y rompedores"},
                {"Jardinería y Despeje", "Desbrozadoras, ahoyadoras y motosierras"},
                {"Seguridad y Señalización", "Vallas de contención, conos viales y señalización"},
                {"Climatización y Ventilación", "Extractores de aire de obra y ventiladores axiales"},
                {"Herramientas Inalámbricas", "Kits a batería 18V/20V de alta resistencia"},
                {"Pruebas Hidráulicas", "Bombas manuales y eléctricas para pruebas de presión"}
        };

        for (int i = 0; i < catData.length; i++) {
            CategoriaJpaEntity c = categoriaRepository.save(CategoriaJpaEntity.builder()
                    .nombre(catData[i][0])
                    .descripcion(catData[i][1])
                    .build());
            cats.put(i + 1, c);
        }

        // 5. Herramientas del Catálogo Oficial
        List<HerramientaJpaEntity> herramientasGuardadas = new ArrayList<>();

        Object[][] toolsData = {
                {1, 1, "Vibrocompactador Tipo Rana 5.5 HP", "Placa vibratoria reversible de alto impacto para compactación profunda de asfalto, zahorra y gravilla en obras civiles.", "Wacker Neuson", "WP1550", "WN-RANA-2024-001", 75000, 4800000, "https://images.unsplash.com/photo-1581094288338-2314dddb7ece?auto=format&fit=crop&w=800&q=80", "Bogotá / Chía"},
                {2, 1, "Apisonador Canguro 4 Tiempos", "Bailarina compactadora de alto rendimiento para zanjas de tubería, cimentaciones estrechas y relleno de zapatas.", "Mikasa", "MTX70", "MK-CANG-2024-002", 85000, 6200000, "https://images.unsplash.com/photo-1579762715118-a6f1d4b934f1?auto=format&fit=crop&w=800&q=80", "Medellín"},
                {3, 2, "Martillo Demoledor Hexagonal 30 kg", "Rompedor de concreto reforzado con energía de impacto de 68 Joules para demolición de losas y cimientos.", "Bosch", "GSH 27 VC", "BS-DEM-2023-101", 110000, 8500000, "https://images.unsplash.com/photo-1504307651254-35680f356dfd?auto=format&fit=crop&w=800&q=80", "Cali"},
                {4, 2, "Rotomartillo SDS Max 1500W", "Taladro percutor de trabajo pesado para anclajes químicos, perforación en mampostería y concreto estructural.", "DeWalt", "D25763K", "DW-ROTO-2023-202", 55000, 3100000, "https://images.unsplash.com/photo-1572981779307-38b8cabb2407?auto=format&fit=crop&w=800&q=80", "Bogotá / Calle 80"},
                {5, 3, "Cortadora de Concreto Autopropulsada 14 HP", "Sierra de piso para juntas de dilatación en pavimento rígido y corte de zanjas en asfalto hasta 15 cm de profundidad.", "Husqvarna", "FS400LV", "HQ-CORT-2024-050", 125000, 9500000, "https://images.unsplash.com/photo-1581092335397-9583fe92d232?auto=format&fit=crop&w=800&q=80", "Barranquilla"},
                {6, 3, "Pulidora Angular Industrial 9 Pulgadas", "Amoladora angular de 2600W para desbaste de cordones de soldadura, biselado de tubería y corte de acero estructural.", "Makita", "GA9020", "MK-PUL-2024-089", 35000, 1100000, "https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=800&q=80", "Bucaramanga"},
                {7, 4, "Mezcladora de Concreto 1.5 Bultos", "Trompo mezclador con motor a gasolina de 6.5 HP para preparación homogénea de morteros y hormigón en sitio.", "Imer", "Syntesi 350", "IM-MEZC-2023-404", 65000, 5200000, "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=800&q=80", "Pereira"},
                {8, 4, "Vibrador de Concreto a Gasolina con Manguera", "Agujero vibrador excéntrico de 38 mm con chicote de 6 metros para eliminación de vacíos en vigas y columnas.", "Enar", "Dingo Gas", "EN-VIB-2024-112", 48000, 2400000, "https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=800&q=80", "Bogotá / Norte"},
                {9, 5, "Cuerpo de Andamio Multidireccional Certificado", "Módulo de andamio tubular galvanizado bajo norma OSHA/NTC 1644 con plataformas antideslizantes y rodapiés.", "Layher", "Allround 2.0", "LY-ANDA-2024-900", 25000, 1800000, "https://images.unsplash.com/photo-1508873696983-2df5293cb32f?auto=format&fit=crop&w=800&q=80", "Cartagena"},
                {10, 6, "Planta Eléctrica Diésel Insonorizada 10 kVA", "Generador eléctrico trifásico encapsulado para suministro ininterrumpido en campamentos y obras sin red fija.", "Caterpillar", "DE9.5E3", "CAT-GEN-2024-777", 180000, 24000000, "https://images.unsplash.com/photo-1581092795360-fd1ca04f0952?auto=format&fit=crop&w=800&q=80", "Bogotá / Chía"},
                {11, 7, "Motobomba de Lodos y Aguas Negras 3 Pulgadas", "Bomba autocebante con impulsor vortex para evacuar agua con sólidos en suspensión hasta 25 mm en excavaciones.", "Honda", "WT30X", "HD-BOMB-2023-333", 70000, 4600000, "https://images.unsplash.com/photo-1581094373403-1620c32729b8?auto=format&fit=crop&w=800&q=80", "Montería"},
                {12, 8, "Soldador Inverter Multiproceso 250A MIG/MMA", "Fuente de poder electrónica para electrodo revestido y microalambre en carpintería metálica de alta exigencia.", "Lincoln Electric", "Powercraft 250", "LE-SOLD-2024-555", 90000, 6800000, "https://images.unsplash.com/photo-1504917599217-d4dc5ebe6122?auto=format&fit=crop&w=800&q=80", "Ibagué"},
                {13, 9, "Nivel Óptico Automático de Precisión 32X", "Instrumento topográfico para nivelación de rasantes, terraplenes y control de plomos en edificación.", "Leica Geosystems", "NA730 Plus", "LC-NIV-2023-018", 45000, 3900000, "https://images.unsplash.com/photo-1581093458791-9f3c3900df4b?auto=format&fit=crop&w=800&q=80", "Bogotá / Calle 100"},
                {14, 10, "Hidrolavadora de Alta Presión 3000 PSI Diésel", "Equipo de lavado con agua fría y caliente para desengrase de maquinaria pesada, encofrados y fachadas.", "Kärcher", "HD 9/20-4", "KC-HIDRO-2024-601", 95000, 7800000, "https://images.unsplash.com/photo-1581578731548-c64695cc6952?auto=format&fit=crop&w=800&q=80", "Villavicencio"},
                {15, 11, "Malacate de Obra 500 kg con Guaya 30m", "Cabrestante elevador con freno electromagnético de seguridad para izaje vertical de materiales en edificios.", "Umacon", "U-500K", "UM-MALA-2023-221", 80000, 5900000, "https://images.unsplash.com/photo-1581092334651-ddf26d9a09d0?auto=format&fit=crop&w=800&q=80", "Manizales"},
                {16, 12, "Equipo Airless de Pintura de Alta Presión", "Bomba dosificadora de pistón para aplicación ultra uniforme de esmaltes, vinilos y selladores epóxicos.", "Graco", "Ultra Max II 695", "GR-AIR-2024-811", 130000, 11500000, "https://images.unsplash.com/photo-1562259949-e8e7689d7828?auto=format&fit=crop&w=800&q=80", "Santa Marta"},
                {17, 13, "Pistola de Fijación a Pólvora Calibre 27", "Herramienta de tiro para anclaje directo de pernos y clavos en concreto de alta resistencia y perfiles de acero.", "Hilti", "DX 460-F8", "HT-FIJ-2024-419", 40000, 3200000, "https://images.unsplash.com/photo-1581244277943-fe4a9c777189?auto=format&fit=crop&w=800&q=80", "Pasto"},
                {18, 14, "Torre de Iluminación Portátil 4x1000W", "Mástil telescópico de 9 metros con generador propio para trabajos nocturnos de pavimentación y emergencias.", "Doosan", "LSC 50Hz", "DS-TORR-2024-303", 160000, 22000000, "https://images.unsplash.com/photo-1517420704952-d9f39e95b43e?auto=format&fit=crop&w=800&q=80", "Neiva"},
                {19, 15, "Compresor de Aire de Tornillo Portátil 185 CFM", "Compresor diésel sobre remolque homologado para alimentar 2 martillos rompedores de 30 kg en paralelo.", "Sullair", "185 Tier 4F", "SL-COMP-2024-185", 210000, 38000000, "https://images.unsplash.com/photo-1581092162384-8987c1d64718?auto=format&fit=crop&w=800&q=80", "Bogotá / Fontibón"},
                {20, 16, "Motosierra Profesional de Espada 24 Pulgadas", "Motosierra de gasolina de 70.7 cc para talas controladas, corte de vigas de madera y despeje de terrenos.", "Stihl", "MS 462 C-M", "ST-MOTO-2024-462", 60000, 4100000, "https://images.unsplash.com/photo-1590496793929-36417d3117de?auto=format&fit=crop&w=800&q=80", "Popayán"}
        };

        for (Object[] row : toolsData) {
            int catId = (int) row[1];
            CategoriaJpaEntity cat = cats.get(catId);
            BigDecimal diaria = BigDecimal.valueOf((int) row[7]);
            BigDecimal repo = BigDecimal.valueOf((int) row[8]);

            HerramientaJpaEntity h = HerramientaJpaEntity.builder()
                    .idProveedor(proveedor.getIdUsuario())
                    .categoriaPrincipal(cat)
                    .nombre((String) row[2])
                    .descripcion((String) row[3])
                    .marca((String) row[4])
                    .modelo((String) row[5])
                    .numeroSerie((String) row[6])
                    .estadoOperativo(EstadoHerramienta.DISPONIBLE)
                    .tarifaDiaria(diaria)
                    .valorReposicion(repo)
                    .ubicacionBodega((String) row[10])
                    .fechaRegistro(LocalDateTime.now().minusDays(15))
                    .categorias(Set.of(cat))
                    .build();

            // Agregar foto principal
            h.agregarImagen(ImagenHerramientaJpaEntity.builder()
                    .urlFoto((String) row[9])
                    .descripcion((String) row[2])
                    .principal(true)
                    .orden(1)
                    .build());

            // Agregar tarifas estándar
            h.agregarTarifa(TarifaHerramientaJpaEntity.builder()
                    .tipoTarifa(TipoTarifa.DIARIA)
                    .valor(diaria)
                    .activa(true)
                    .build());

            h.agregarTarifa(TarifaHerramientaJpaEntity.builder()
                    .tipoTarifa(TipoTarifa.SEMANAL)
                    .valor(diaria.multiply(BigDecimal.valueOf(6)))
                    .activa(true)
                    .build());

            // Agregar especificación técnica
            h.agregarEspecificacion(EspecificacionTecnicaJpaEntity.builder()
                    .atributo("Marca y Modelo")
                    .valor(row[4] + " " + row[5])
                    .unidadMedida("Comercial")
                    .build());

            h.agregarEspecificacion(EspecificacionTecnicaJpaEntity.builder()
                    .atributo("Estado Operativo")
                    .valor("Certificado y Calibrado")
                    .unidadMedida("Inspección")
                    .build());

            HerramientaJpaEntity guardada = herramientaRepository.save(h);
            herramientasGuardadas.add(guardada);
        }

        // 6. Reservas Iniciales de Ejemplo
        HerramientaJpaEntity h1 = herramientasGuardadas.get(0); // Rana
        HerramientaJpaEntity h2 = herramientasGuardadas.get(1); // Canguro
        HerramientaJpaEntity h4 = herramientasGuardadas.get(3); // Rotomartillo

        // Reserva 1: APROBADA con Pago y Factura DIAN emitida
        ReservaJpaEntity res1 = ReservaJpaEntity.builder()
                .codigoReserva("RES-2026-000101")
                .idCliente(cliente.getIdUsuario())
                .fechaSolicitud(LocalDateTime.now().minusDays(4))
                .fechaInicio(LocalDate.now().minusDays(2))
                .fechaFin(LocalDate.now().plusDays(3))
                .estado(EstadoReserva.APROBADA)
                .montoTotalEstimado(BigDecimal.valueOf(375000))
                .depositoGarantia(BigDecimal.valueOf(150000))
                .notasCliente("Entrega a primera hora en obra de Chía")
                .build();

        DetalleReservaJpaEntity det1 = DetalleReservaJpaEntity.builder()
                .reserva(res1)
                .idHerramienta(h1.getIdHerramienta())
                .cantidad(1)
                .precioUnitarioDia(BigDecimal.valueOf(75000))
                .diasAlquiler(5)
                .subtotal(BigDecimal.valueOf(375000))
                .build();
        res1.getDetalles().add(det1);
        ReservaJpaEntity res1Guardada = reservaRepository.save(res1);

        // Pago y Factura para Reserva 1
        PagoJpaEntity pago1 = pagoRepository.save(PagoJpaEntity.builder()
                .numeroTransaccion("TRX-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase())
                .idReserva(res1Guardada.getIdReserva())
                .idMetodoPago(mpPse.getIdMetodoPago())
                .monto(BigDecimal.valueOf(375000))
                .tipoPago(TipoPago.TOTALIDAD)
                .estadoPago(EstadoPago.APROBADO)
                .fechaPago(LocalDateTime.now().minusDays(3))
                .referenciaPasarela("PSE-ACH-" + (100000 + new Random().nextInt(900000)))
                .build());

        // Factura con cálculo del 19% de IVA
        BigDecimal total = BigDecimal.valueOf(375000);
        BigDecimal subtotal = total.divide(BigDecimal.valueOf(1.19), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal iva = total.subtract(subtotal);

        FacturaJpaEntity fact1 = FacturaJpaEntity.builder()
                .numeroFactura("FACT-2026-000101")
                .idPago(pago1.getIdPago())
                .idCliente(cliente.getIdUsuario())
                .fechaEmision(LocalDateTime.now().minusDays(3))
                .subtotal(subtotal)
                .iva(iva)
                .total(total)
                .estado(EstadoFactura.EMITIDA)
                .urlComprobantePdf("pdf/factura_101.pdf")
                .build();

        fact1.getDetalles().add(DetalleFacturaJpaEntity.builder()
                .factura(fact1)
                .descripcionItem("Alquiler por 5 días - " + h1.getNombre())
                .cantidad(1)
                .precioUnitario(subtotal)
                .subtotalLinea(subtotal)
                .build());

        facturaRepository.save(fact1);

        // Reserva 2: SOLICITADA
        ReservaJpaEntity res2 = ReservaJpaEntity.builder()
                .codigoReserva("RES-2026-000102")
                .idCliente(cliente.getIdUsuario())
                .fechaSolicitud(LocalDateTime.now().minusHours(12))
                .fechaInicio(LocalDate.now().plusDays(1))
                .fechaFin(LocalDate.now().plusDays(4))
                .estado(EstadoReserva.SOLICITADA)
                .montoTotalEstimado(BigDecimal.valueOf(165000))
                .depositoGarantia(BigDecimal.valueOf(80000))
                .notasCliente("Requiero broca SDS Max adicional de 1 pulgada")
                .build();

        res2.getDetalles().add(DetalleReservaJpaEntity.builder()
                .reserva(res2)
                .idHerramienta(h4.getIdHerramienta())
                .cantidad(1)
                .precioUnitarioDia(BigDecimal.valueOf(55000))
                .diasAlquiler(3)
                .subtotal(BigDecimal.valueOf(165000))
                .build());
        reservaRepository.save(res2);

        // Reserva 3: FINALIZADA
        ReservaJpaEntity res3 = ReservaJpaEntity.builder()
                .codigoReserva("RES-2026-000103")
                .idCliente(camilo.getIdUsuario())
                .fechaSolicitud(LocalDateTime.now().minusDays(10))
                .fechaInicio(LocalDate.now().minusDays(8))
                .fechaFin(LocalDate.now().minusDays(4))
                .estado(EstadoReserva.FINALIZADA)
                .montoTotalEstimado(BigDecimal.valueOf(340000))
                .depositoGarantia(BigDecimal.valueOf(150000))
                .notasCliente("Equipo devuelto con tanque lleno y limpio")
                .build();

        res3.getDetalles().add(DetalleReservaJpaEntity.builder()
                .reserva(res3)
                .idHerramienta(h2.getIdHerramienta())
                .cantidad(1)
                .precioUnitarioDia(BigDecimal.valueOf(85000))
                .diasAlquiler(4)
                .subtotal(BigDecimal.valueOf(340000))
                .build());
        reservaRepository.save(res3);

        log.info("🎉 Base de datos SQLite sembrada exitosamente con 20 herramientas, roles, usuarios, categorías y facturación activa.");
    }
}
