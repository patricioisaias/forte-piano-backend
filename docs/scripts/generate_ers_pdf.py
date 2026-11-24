import os
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable
)
from reportlab.pdfgen import canvas

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super(NumberedCanvas, self).__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_number(num_pages)
            super(NumberedCanvas, self).showPage()
        super(NumberedCanvas, self).save()

    def draw_page_number(self, page_count):
        self.saveState()
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#666666"))
        
        # Header (pages after cover page)
        if self._pageNumber > 1:
            self.drawString(54, 750, "Forte & Piano — Especificación de Requisitos de Software (ERS v2.0)")
            self.drawRightString(558, 750, "IEEE 830-1998 | Duoc UC")
            self.setStrokeColor(colors.HexColor("#CCCCCC"))
            self.setLineWidth(0.5)
            self.line(54, 742, 558, 742)

        # Footer
        page_str = f"Página {self._pageNumber} de {page_count}"
        self.drawRightString(558, 35, page_str)
        self.drawString(54, 35, "DSY1104 — Desarrollo Fullstack II (Evaluación Parcial N° 3)")
        self.setStrokeColor(colors.HexColor("#CCCCCC"))
        self.setLineWidth(0.5)
        self.line(54, 47, 558, 47)
        self.restoreState()

def build_pdf(filename="docs/pdf/ERS-FortePiano-v2.0.pdf"):
    doc = SimpleDocTemplate(
        filename,
        pagesize=letter,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54
    )

    styles = getSampleStyleSheet()
    
    # Custom styles
    primary_color = colors.HexColor("#0D3B66") # Navy blue
    secondary_color = colors.HexColor("#0077B6") # Medium blue
    accent_color = colors.HexColor("#0096C7") # Cyan blue
    text_dark = colors.HexColor("#1A1A1A")
    table_header_bg = colors.HexColor("#EBF4FA")
    table_border = colors.HexColor("#B0C4DE")

    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=20,
        leading=24,
        textColor=primary_color,
        spaceAfter=6
    )

    subtitle_style = ParagraphStyle(
        'DocSubtitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=15,
        leading=19,
        textColor=secondary_color,
        spaceAfter=15
    )

    meta_style = ParagraphStyle(
        'DocMeta',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=10,
        leading=14,
        textColor=text_dark,
        spaceAfter=3
    )

    meta_bold = ParagraphStyle(
        'DocMetaBold',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10,
        leading=14,
        textColor=text_dark,
        spaceAfter=3
    )

    h1_style = ParagraphStyle(
        'SectionH1',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=14,
        leading=18,
        textColor=primary_color,
        spaceBefore=14,
        spaceAfter=8,
        keepWithNext=True
    )

    h2_style = ParagraphStyle(
        'SectionH2',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=11,
        leading=15,
        textColor=secondary_color,
        spaceBefore=10,
        spaceAfter=4,
        keepWithNext=True
    )

    h3_style = ParagraphStyle(
        'SectionH3',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10,
        leading=13,
        textColor=text_dark,
        spaceBefore=6,
        spaceAfter=3,
        keepWithNext=True
    )

    body_style = ParagraphStyle(
        'BodyDark',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9.5,
        leading=13.5,
        textColor=text_dark,
        spaceAfter=6
    )

    bullet_style = ParagraphStyle(
        'BulletDark',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9.5,
        leading=13.5,
        textColor=text_dark,
        leftIndent=15,
        spaceAfter=3
    )

    table_cell = ParagraphStyle(
        'TableCell',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.5,
        leading=11,
        textColor=text_dark
    )

    table_cell_bold = ParagraphStyle(
        'TableCellBold',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11,
        textColor=text_dark
    )

    table_header = ParagraphStyle(
        'TableHeader',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11,
        textColor=primary_color
    )

    story = []

    # -------------------------------------------------------------
    # PORTADA / ENCABEZADO DUOC UC
    # -------------------------------------------------------------
    story.append(Paragraph("Duoc UC — Escuela de Informática y Telecomunicaciones", title_style))
    story.append(Paragraph("Especificación de Requisitos de Software (ERS)", subtitle_style))
    story.append(HRFlowable(width="100%", thickness=2, color=secondary_color, spaceAfter=15))

    story.append(Paragraph("<b>Proyecto:</b> Forte & Piano — Tienda Online de Instrumentos Musicales", meta_style))
    story.append(Paragraph("<b>Estándar:</b> Conforme a IEEE 830-1998", meta_style))
    story.append(Paragraph("<b>Revisión:</b> 2.0 (Actualización de Arquitectura Fullstack Spring Boot + MySQL)", meta_style))
    story.append(Paragraph("<b>Asignatura:</b> Desarrollo Fullstack II (DSY1104) — Evaluación Parcial N° 3", meta_style))
    story.append(Paragraph("<b>Fecha:</b> Noviembre 2026", meta_style))
    story.append(Spacer(1, 15))

    # Ficha del Documento
    story.append(Paragraph("Ficha del Documento", h2_style))
    
    ficha_data = [
        [Paragraph("Fecha", table_header), Paragraph("Revisión", table_header), Paragraph("Autor", table_header), Paragraph("Modificación", table_header)],
        [
            Paragraph("07/09/2026", table_cell),
            Paragraph("1.0", table_cell_bold),
            Paragraph("Equipo Frontend Duoc UC", table_cell),
            Paragraph("Creación inicial del documento ERS para la aplicación Forte & Piano según estándar IEEE 830 (Frontend HTML5/JS/localStorage).", table_cell)
        ],
        [
            Paragraph("22/11/2026", table_cell),
            Paragraph("2.0", table_cell_bold),
            Paragraph("Patricio Céspedes<br/>Daniel Aedo<br/>Angelo Ponce", table_cell),
            Paragraph("<b>Actualización Mayor a Arquitectura Fullstack</b>:<br/>"
                      "• Reemplazo de localStorage por <b>Backend REST API en Spring Boot 4 / Java 21</b>.<br/>"
                      "• Persistencia en base de datos relacional <b>MySQL 8.x (fortepiano_db)</b>.<br/>"
                      "• Seguridad <b>Spring Security con JWT y BCrypt</b>.<br/>"
                      "• Control de acceso basado en roles (<b>RBAC</b>: Admin, Vendedor, Cliente).<br/>"
                      "• Pasarela de pago simulada transaccional y control de stock en BD.<br/>"
                      "• Dashboard estadístico en tiempo real y documentación <b>OpenAPI 3 / Swagger</b>.", table_cell)
        ]
    ]

    t_ficha = Table(ficha_data, colWidths=[65, 50, 115, 274])
    t_ficha.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), table_header_bg),
        ('GRID', (0,0), (-1,-1), 0.5, table_border),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('TOPPADDING', (0,0), (-1,-1), 5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 5),
    ]))
    story.append(t_ficha)
    story.append(Spacer(1, 12))

    # Firmas de Validación
    story.append(Paragraph("Documento validado por las partes:", h3_style))
    firmas_data = [
        [Paragraph("Por el Cliente / Docente Evaluador", table_header), Paragraph("Por el Equipo Suministrador / Desarrolladores", table_header)],
        [
            Paragraph("<br/><br/>Firma: ___________________________<br/><b>Docente DSY1104</b><br/>Escuela de Informática y Telecomunicaciones<br/>Duoc UC", table_cell),
            Paragraph("<br/><br/>Firma: ___________________________<br/><b>Patricio Céspedes</b> (Módulo A: Seguridad e Identidad)<br/><b>Daniel Aedo</b> (Módulo B: Catálogo, Checkout y Boletas)<br/><b>Angelo Ponce</b> (Módulo C: Blog, Contacto y Dashboard)", table_cell)
        ]
    ]
    t_firmas = Table(firmas_data, colWidths=[252, 252])
    t_firmas.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), table_header_bg),
        ('GRID', (0,0), (-1,-1), 0.5, table_border),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('TOPPADDING', (0,0), (-1,-1), 6),
        ('BOTTOMPADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(t_firmas)
    
    story.append(PageBreak())

    # -------------------------------------------------------------
    # SECCIÓN 1: INTRODUCCIÓN
    # -------------------------------------------------------------
    story.append(Paragraph("1. Introducción", h1_style))
    story.append(Paragraph(
        "El presente documento formaliza la <b>Especificación de Requisitos de Software (ERS) Versión 2.0</b> "
        "para la plataforma de comercio electrónico <b>Forte & Piano</b>, elaborada en conformidad estricta "
        "con el estándar <b>IEEE 830-1998</b>. Provee el marco de diseño funcional, no funcional, arquitectónico "
        "y de seguridad necesario tanto para los evaluadores académicos como para los desarrolladores.",
        body_style
    ))

    story.append(Paragraph("1.1. Propósito", h2_style))
    story.append(Paragraph(
        "El propósito de esta ERS es detallar de manera formal y no ambigua los requerimientos funcionales, "
        "requerimientos no funcionales, reglas de negocio e interfaces que constituyen la solución integral de "
        "Forte & Piano bajo una arquitectura desacoplada Fullstack (Frontend React + Backend Spring Boot + BD MySQL). "
        "Este documento está dirigido a:",
        body_style
    ))
    story.append(Paragraph("• <b>Equipo de desarrollo Fullstack de Duoc UC:</b> Responsable de la codificación, pruebas automatizadas y despliegue del frontend y backend.", bullet_style))
    story.append(Paragraph("• <b>Docentes y evaluadores de la asignatura DSY1104:</b> Encargados de la verificación y validación del cumplimiento de requisitos.", bullet_style))
    story.append(Paragraph("• <b>Usuarios y administradores futuros:</b> Interesados en la operatividad, seguridad y rendimiento de la plataforma.", bullet_style))

    story.append(Paragraph("1.2. Ámbito del Sistema", h2_style))
    story.append(Paragraph("<b>Nombre del Sistema:</b> Forte & Piano — E-Commerce de Pianos, Teclados y Accesorios Musicales.", body_style))
    story.append(Paragraph("<b>Lo que el sistema HACE:</b>", h3_style))
    story.append(Paragraph("• Provee una <b>API RESTful stateless</b> desarrollada en <b>Spring Boot 4 / Java 21</b> con endpoints versionados bajo el prefijo <code>/api/v1/...</code>.", bullet_style))
    story.append(Paragraph("• Gestiona la persistencia de datos en una base de datos relacional <b>MySQL 8.x (fortepiano_db)</b> con integridad referencial completa y transacciones ACID.", bullet_style))
    story.append(Paragraph("• Ofrece un subsistema de autenticación y autorización robusto mediante <b>Spring Security y JSON Web Tokens (JWT)</b> con algoritmo HMAC-SHA y contraseñas cifradas en <b>BCrypt</b>.", bullet_style))
    story.append(Paragraph("• Implementa control de acceso basado en roles (<b>RBAC</b>) con 3 perfiles: <b>Administrador (rolId=1)</b>, <b>Vendedor (rolId=2)</b> y <b>Cliente (rolId=3)</b>.", bullet_style))
    story.append(Paragraph("• Expone un catálogo dinámico con control de inventario en tiempo real, alertas de stock crítico y cálculo automatizado de precios de oferta con porcentaje de descuento.", bullet_style))
    story.append(Paragraph("• Procesa compras mediante un <b>Checkout transaccional con pasarela de pago simulada</b> (estados: <code>Emitida</code>, <code>Pendiente</code>, <code>Rechazada</code>, <code>Anulada</code>), con desglose de IVA (19%) y Neto, y descuento/reposición automática de stock en BD.", bullet_style))
    story.append(Paragraph("• Dispone de un panel de administración con <b>Dashboard estadístico en tiempo real</b> que consolida ventas totales, cantidad de pedidos, distribución por estados y ranking de productos.", bullet_style))
    story.append(Paragraph("• Implementa validaciones estrictas en el servidor: validación de <b>RUN chileno mediante algoritmo Módulo 11</b> (rechazando RUNs sin dígito verificador) y lista blanca de dominios de correo permitidos (<code>@duoc.cl</code>, <code>@profesor.duoc.cl</code>, <code>@gmail.com</code>).", bullet_style))
    story.append(Paragraph("• Expone documentación viva e interactiva a través de <b>Swagger UI / OpenAPI 3</b>.", bullet_style))

    story.append(Spacer(1, 4))
    story.append(Paragraph("<b>Lo que el sistema NO HACE:</b>", h3_style))
    story.append(Paragraph("• No procesa transacciones bancarias con dinero real (utiliza una pasarela bancaria simulada transaccional conforme a los requisitos de la evaluación académica).", bullet_style))
    story.append(Paragraph("• No realiza envíos logísticos físicos (simula el cálculo y almacenamiento de datos de despacho y destinatario).", bullet_style))

    story.append(Paragraph("1.3. Definiciones, Acrónimos y Abreviaturas", h2_style))
    story.append(Paragraph("• <b>ERS:</b> Especificación de Requisitos de Software (IEEE 830-1998).", bullet_style))
    story.append(Paragraph("• <b>REST:</b> Representational State Transfer (Estilo arquitectónico para servicios web ligeros y escalables).", bullet_style))
    story.append(Paragraph("• <b>JWT:</b> JSON Web Token (Estándar RFC 7519 para transmisión segura de identidad compacta y autofirmada).", bullet_style))
    story.append(Paragraph("• <b>BCrypt:</b> Función de hashing criptográfico unidireccional con salting para protección de contraseñas.", bullet_style))
    story.append(Paragraph("• <b>RBAC:</b> Role-Based Access Control (Control de acceso basado en roles).", bullet_style))
    story.append(Paragraph("• <b>JPA / ORM:</b> Java Persistence API / Object-Relational Mapping con Hibernate.", bullet_style))
    story.append(Paragraph("• <b>CORS:</b> Cross-Origin Resource Sharing (Seguridad para peticiones entre diferentes puertos u orígenes).", bullet_style))
    story.append(Paragraph("• <b>RUN:</b> Rol Único Nacional (Identificador fiscal chileno de personas naturales).", bullet_style))
    story.append(Paragraph("• <b>Stock Crítico:</b> Umbral numérico mínimo de inventario tras el cual se genera alerta operativa.", bullet_style))

    story.append(Paragraph("1.4. Referencias", h2_style))
    story.append(Paragraph("• Estándar IEEE 830-1998: Práctica recomendada para especificaciones de requisitos de software.", bullet_style))
    story.append(Paragraph("• Pauta de Evaluación DSY1104: Escuela de Informática y Telecomunicaciones, Duoc UC.", bullet_style))
    story.append(Paragraph("• Documentación Oficial de Spring Boot 4 y Spring Security 7.", bullet_style))
    story.append(Paragraph("• Especificación OpenAPI 3.0 / SpringDoc OpenAPI UI.", bullet_style))

    story.append(PageBreak())

    # -------------------------------------------------------------
    # SECCIÓN 2: DESCRIPCIÓN GENERAL
    # -------------------------------------------------------------
    story.append(Paragraph("2. Descripción General", h1_style))
    story.append(Paragraph("2.1. Perspectiva del Producto", h2_style))
    story.append(Paragraph(
        "Forte & Piano opera bajo una <b>arquitectura en tres capas desacoplada (Cliente-Servidor)</b>:",
        body_style
    ))
    story.append(Paragraph("1. <b>Capa de Presentación (Frontend SPA):</b> Aplicación cliente desarrollada en <b>React + TypeScript + Vite</b> (puerto 5173). Consume la API REST de forma asíncrona y almacena el token JWT.", bullet_style))
    story.append(Paragraph("2. <b>Capa de Negocio (Backend REST):</b> API REST desarrollada en <b>Spring Boot 4 / Java 21</b> (puerto 8080), organizada en controladores, servicios transaccionales, DTOs y filtros de seguridad stateless.", bullet_style))
    story.append(Paragraph("3. <b>Capa de Persistencia y Datos:</b> Gestionada por <b>MySQL 8.x (fortepiano_db)</b> mediante Spring Data JPA e Hibernate, asegurando integridad referencial, índices y atomicidad transaccional.", bullet_style))

    story.append(Paragraph("2.2. Funciones del Producto (Nivel Macro)", h2_style))
    story.append(Paragraph("• <b>Subsistema Tienda Pública:</b> Catálogo dinámico con búsqueda y filtros por categoría, vista detallada por SKU, carrito de compras, blog temático y formulario de mensajería.", bullet_style))
    story.append(Paragraph("• <b>Subsistema de Autenticación e Identidad:</b> Registro de clientes con validación Módulo 11, inicio de sesión seguro, emisión de JWT, endpoint de sesión activa (<code>/auth/me</code>) y actualización de perfil.", bullet_style))
    story.append(Paragraph("• <b>Subsistema de Checkout, Boletas y Pasarela:</b> Checkout transaccional, pasarela bancaria simulada (3 escenarios), desglose de IVA (19%) y Neto, consulta de órdenes propias, reintento de pago y anulación con devolución de stock.", bullet_style))
    story.append(Paragraph("• <b>Subsistema Administrativo y Métricas:</b> Panel de control con métricas en tiempo real (ventas totales, distribución por estados, top ventas, stock crítico), mantenedores CRUD de catálogo y usuarios.", bullet_style))

    story.append(Paragraph("2.3. Características de los Usuarios y Matriz de Roles (RBAC)", h2_style))
    
    roles_data = [
        [Paragraph("Rol / Perfil", table_header), Paragraph("Nivel Técnico", table_header), Paragraph("Permisos y Responsabilidades en el Sistema", table_header)],
        [
            Paragraph("<b>Administrador</b><br/>(rolId = 1)", table_cell),
            Paragraph("Técnico o profesional; gestión de datos y ventas.", table_cell),
            Paragraph("<b>Acceso total e irrestricto</b>: Gestión CRUD de usuarios, creación y edición de productos y categorías, anulación de boletas con reposición de inventario en BD, supervisión general de ventas y acceso al Dashboard estadístico.", table_cell)
        ],
        [
            Paragraph("<b>Vendedor</b><br/>(rolId = 2)", table_cell),
            Paragraph("Medio / técnico; atención comercial e inventario.", table_cell),
            Paragraph("Visualización de productos y existencias, supervisión y lectura de órdenes/boletas, y acceso a métricas del Dashboard. <b>Restringido</b> de creación/edición de catálogo, gestión de usuarios y anulación de boletas.", table_cell)
        ],
        [
            Paragraph("<b>Cliente</b><br/>(rolId = 3)", table_cell),
            Paragraph("Usuario general.", table_cell),
            Paragraph("Navegación de catálogo, carrito de compras, ejecución de Checkout con pasarela de pago, consulta de su historial de compras (<code>/mis-boletas</code>) y edición de su propio perfil de usuario.", table_cell)
        ],
        [
            Paragraph("<b>Público / Invitado</b>", table_cell),
            Paragraph("Público general sin autenticar.", table_cell),
            Paragraph("Exploración pública de catálogo, lectura de blog, envío de mensajes por formulario de contacto, inicio de sesión y registro de cuenta nueva.", table_cell)
        ]
    ]

    t_roles = Table(roles_data, colWidths=[95, 110, 299])
    t_roles.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), table_header_bg),
        ('GRID', (0,0), (-1,-1), 0.5, table_border),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('TOPPADDING', (0,0), (-1,-1), 5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 5),
    ]))
    story.append(t_roles)
    story.append(Spacer(1, 8))

    story.append(Paragraph("2.4. Restricciones del Sistema", h2_style))
    story.append(Paragraph("• <b>Tecnología Base:</b> Java 21 LTS y Spring Boot 4.", bullet_style))
    story.append(Paragraph("• <b>Prohibición de Persistencia en localStorage:</b> La persistencia de entidades de negocio reside exclusivamente en MySQL (<code>fortepiano_db</code>); <code>localStorage</code> solo almacena el token JWT de sesión en el cliente.", bullet_style))
    story.append(Paragraph("• <b>Seguridad Stateless:</b> Ningún estado de sesión se almacena en memoria de servidor (<code>SessionCreationPolicy.STATELESS</code>). Toda petición a endpoints protegidos debe adjuntar el token JWT.", bullet_style))
    story.append(Paragraph("• <b>CORS Restringido:</b> Orígenes autorizados configurados para <code>http://localhost:5173</code> y <code>http://127.0.0.1:5173</code>.", bullet_style))
    story.append(Paragraph("• <b>Integridad Transaccional:</b> Toda mutación compuesta (Checkout, Anulación de boleta) debe ejecutarse bajo anotación <code>@Transactional</code>.", bullet_style))

    story.append(Paragraph("2.5. Requisitos Futuros", h2_style))
    story.append(Paragraph("• Integración con pasarela de pago real bancaria (Transbank Webpay Plus / Mercado Pago).", bullet_style))
    story.append(Paragraph("• Microservicio de mensajería y notificaciones transaccionales vía correo electrónico (Spring Mail / SendGrid).", bullet_style))
    story.append(Paragraph("• Contenerización con Docker y despliegue en entornos Cloud (AWS / GCP / Azure).", bullet_style))

    story.append(PageBreak())

    # -------------------------------------------------------------
    # SECCIÓN 3: REQUISITOS ESPECÍFICOS
    # -------------------------------------------------------------
    story.append(Paragraph("3. Requisitos Específicos", h1_style))
    story.append(Paragraph("3.1. Requisitos de Interfaces del Sistema", h2_style))
    story.append(Paragraph("• <b>Servidor de Aplicación:</b> Spring Boot 4 sobre motor Apache Tomcat embebido (puerto 8080).", bullet_style))
    story.append(Paragraph("• <b>Base de Datos Relacional:</b> MySQL 8.x con codificación UTF-8 e InnoDB.", bullet_style))
    story.append(Paragraph("• <b>Seguridad:</b> Spring Security con filtro personalizado <code>JwtAuthenticationFilter</code> y JJWT 0.12.6.", bullet_style))
    story.append(Paragraph("• <b>Documentación Interactiva:</b> SpringDoc OpenAPI 3 UI disponible en <code>/swagger-ui.html</code>.", bullet_style))
    story.append(Paragraph("• <b>Protocolo de Comunicación:</b> HTTP/1.1 RESTful con payloads JSON.", bullet_style))

    story.append(Paragraph("3.2. Catálogo de Requisitos Funcionales", h2_style))

    rfs = [
        ("RF01", "Inicialización y Sembrado Automatizado de Datos (Seeders)",
         "Sistema / Servidor Spring Boot.",
         "Al iniciar el backend, el sistema verifica la existencia de registros en la base de datos MySQL fortepiano_db. Si no existen, ejecuta automáticamente los componentes @Order(1..3): UsuariosSeeder (roles, regiones, comunas, usuarios con BCrypt), CatalogoBoletasSeeder (categorías, productos con ofertas, boletas históricas) y ContenidoSeeder (noticias del blog)."),
        
        ("RF02", "Registro de Nuevos Clientes con Validación Módulo 11",
         "Cliente / Público General.",
         "Permite crear una cuenta de cliente (rolId=3) mediante POST /api/v1/auth/registro. Valida obligatoriamente que el RUN cumpla el algoritmo Módulo 11 (rechazando RUNs sin DV), que el correo pertenezca a dominios autorizados (@duoc.cl, @profesor.duoc.cl, @gmail.com) y no esté duplicado, encriptando la contraseña con BCrypt."),

        ("RF03", "Autenticación de Usuarios y Control de Acceso (Login)",
         "Cliente, Vendedor, Administrador.",
         "Mediante POST /api/v1/auth/login, valida correo y password con BCrypt. Si las credenciales son válidas y la cuenta está activa, emite un token Bearer JWT firmado digitalmente con expiración de 24 horas y retorna el objeto de sesión con rolId y permisos."),

        ("RF04", "Persistencia y Consulta de Sesión Activa (/auth/me)",
         "Usuario Autenticado.",
         "Endpoint GET /api/v1/auth/me que recibe el token JWT en el encabezado Authorization y retorna la sesión del usuario. Permite al frontend React persistir el estado de sesión ante recargas de página (F5) sin pedir login nuevamente."),

        ("RF05", "Actualización de Perfil de Usuario",
         "Usuario Autenticado (Cliente, Vendedor, Admin).",
         "Permite actualizar datos personales, dirección de despacho, región y comuna mediante PUT /api/v1/auth/perfil. Utilizado también durante el flujo de Checkout para consolidar los datos de envío del comprador."),

        ("RF06", "Gestión Integral de Usuarios (CRUD Administrativo)",
         "Administrador (ROLE_ADMIN).",
         "Endpoints bajo /api/v1/usuarios: listar usuarios, obtener por RUN, crear usuarios administrativos o vendedores con BCrypt y Módulo 11, actualizar roles/estados (Activo/Inactivo) y eliminación física de registros."),

        ("RF07", "Catálogo Dinámico de Productos y Ofertas",
         "Público General, Cliente, Vendedor, Administrador.",
         "Endpoints GET /api/v1/productos y GET /api/v1/productos/{codigo}. Permite filtrar por búsqueda (?texto=), categoría (?categoriaId=) y promociones (?enOferta=true). Si enOferta=true y porcentajeDescuento > 0, calcula automáticamente precioOferta = Math.round(precio * (1 - porcentajeDescuento/100))."),

        ("RF08", "Mantenimiento de Productos y Categorías (CRUD Admin)",
         "Administrador (ROLE_ADMIN).",
         "Permite crear, actualizar y eliminar categorías (POST, DELETE /api/v1/categorias) y productos (POST, PUT, DELETE /api/v1/productos). Controla que no se eliminen categorías con productos asociados y emite alerta si stock <= stockCritico."),

        ("RF09", "Checkout y Pasarela de Pago Simulada",
         "Cliente Autenticado, Administrador.",
         "Procesa compras vía POST /api/v1/boletas/checkout en una transacción @Transactional. Simula 3 escenarios: 'exitoso' (verifica y descuenta stock en MySQL, calcula Neto e IVA 19%, estado 'Emitida'), 'rechazado' (no descuenta stock, estado 'Rechazada'), e 'intentar_mas_tarde' (no descuenta stock, estado 'Pendiente')."),

        ("RF10", "Consulta e Historial de Boletas de Compra",
         "Cliente, Vendedor, Administrador.",
         "GET /api/v1/boletas/mis-boletas permite al cliente consultar su historial personal a partir de su JWT. GET /api/v1/boletas permite a Administradores y Vendedores consultar y filtrar todas las órdenes del sistema por cliente o estado. GET /api/v1/boletas/{id} entrega el detalle completo de la orden."),

        ("RF11", "Recuperación de Compras y Reintento de Pago",
         "Cliente Autenticado, Administrador.",
         "Mediante PUT /api/v1/boletas/{id}/reintentar-pago, permite reactivar compras en estado Pendiente o Rechazada. Si el resultado es 'exitoso', verifica y descuenta stock y cambia el estado a 'Emitida'."),

        ("RF12", "Anulación de Boletas con Devolución de Stock",
         "Exclusivo Administrador (ROLE_ADMIN).",
         "Mediante PUT /api/v1/boletas/{id}/anular, cambia el estado de una boleta Emitida a 'Anulada' y restituye automáticamente el stock de cada uno de sus productos en la base de datos MySQL."),

        ("RF13", "Publicación y Difusión de Noticias (Blog Musical)",
         "Público General.",
         "GET /api/v1/noticias (listado general) y GET /api/v1/noticias/{slug} (lectura estructurada con párrafos ordenados de novedades musicales y consejos técnicos)."),

        ("RF14", "Formulario de Contacto y Feedback",
         "Público General, Administrador, Vendedor.",
         "POST /api/v1/contactos permite enviar consultas capturando nombre, correo validado y mensaje (hasta 500 caracteres). GET /api/v1/contactos ofrece una bandeja de entrada protegida para revisión administrativa."),

        ("RF15", "Dashboard Estadístico y Reportes en Tiempo Real",
         "Administrador, Vendedor.",
         "GET /api/v1/dashboard/resumen consolida métricas clave: ventas totales (suma boletas Emitidas), cantidad de pedidos, unidades vendidas, distribución de pedidos por estado, ranking top 5 de productos más vendidos y alertas de stock crítico."),

        ("RF16", "Documentación Interactiva de API (OpenAPI 3 / Swagger)",
         "Desarrolladores, Docentes Evaluadores.",
         "Interfaz Swagger UI disponible en /swagger-ui.html con catálogo de endpoints documentados con etiquetas, esquemas de entrada/salida y botón Authorize para Bearer Token JWT.")
    ]

    for codigo, titulo, actores, descripcion in rfs:
        story.append(Paragraph(f"<b>{codigo}: {titulo}</b>", h3_style))
        story.append(Paragraph(f"<b>Actores:</b> {actores}", bullet_style))
        story.append(Paragraph(f"<b>Descripción:</b> {descripcion}", bullet_style))
        story.append(Spacer(1, 3))

    story.append(PageBreak())

    # -------------------------------------------------------------
    # REQUISITOS NO FUNCIONALES Y MATRIZ
    # -------------------------------------------------------------
    story.append(Paragraph("3.3. Requisitos No Funcionales (Atributos de Calidad)", h2_style))
    
    story.append(Paragraph("3.3.1. Requisitos de Rendimiento", h3_style))
    story.append(Paragraph("• Las operaciones de lectura (<code>GET</code>) deben responder en menos de <b>100 milisegundos</b> en red local.", bullet_style))
    story.append(Paragraph("• Las operaciones transaccionales compuestas (Checkout y Anulación con actualización de stock) deben completarse en menos de <b>250 milisegundos</b>.", bullet_style))
    story.append(Paragraph("• Pool de conexiones optimizado mediante <b>HikariCP</b> para alta concurrencia de accesos JDBC a MySQL.", bullet_style))

    story.append(Paragraph("3.3.2. Requisitos de Seguridad", h3_style))
    story.append(Paragraph("• Contraseñas protegidas mediante algoritmo <b>BCrypt con factor de costo 10</b>; ninguna clave se almacena en texto plano.", bullet_style))
    story.append(Paragraph("• Tokens <b>JWT (JSON Web Tokens)</b> firmados con clave simétrica secreta de 256 bits y expiración configurable (24 horas).", bullet_style))
    story.append(Paragraph("• Control de acceso basado en roles (<b>RBAC</b>) en Spring Security para endpoints administrativos y comerciales.", bullet_style))
    story.append(Paragraph("• Prevención de <b>SQL Injection</b> mediante consultas parametrizadas de Hibernate / JPA.", bullet_style))
    story.append(Paragraph("• Política de <b>CORS</b> restringida a orígenes del frontend (<code>http://localhost:5173</code>).", bullet_style))

    story.append(Paragraph("3.3.3. Fiabilidad e Integridad Transaccional", h3_style))
    story.append(Paragraph("• Atomicidad y consistencia ACID garantizadas mediante anotaciones <code>@Transactional</code> en operaciones críticas.", bullet_style))
    story.append(Paragraph("• Cobertura de pruebas unitarias automatizadas con <b>JUnit 5 y Mockito</b> (<b>58 pruebas pasando exitosamente con 0 fallos</b>).", bullet_style))

    story.append(Paragraph("3.3.4. Disponibilidad y Escalabilidad", h3_style))
    story.append(Paragraph("• Arquitectura Stateless desacoplada que permite balanceo de carga y escalabilidad horizontal transparente.", bullet_style))
    story.append(Paragraph("• Respuestas de error estructuradas globalmente mediante <code>ApiErrorDto</code>.", bullet_style))

    story.append(Paragraph("3.3.5. Mantenibilidad y Portabilidad", h3_style))
    story.append(Paragraph("• Arquitectura modular por capas limpias (Controller -> Service -> Repository -> Entity / DTO / Security).", bullet_style))
    story.append(Paragraph("• Portabilidad multiplataforma garantizada por la máquina virtual de Java (<b>JVM 21+</b>) ejecutable en Windows, Linux y macOS.", bullet_style))

    story.append(Spacer(1, 10))
    story.append(Paragraph("3.4. Matriz de Trazabilidad: Requisitos vs. Endpoints vs. Roles", h2_style))

    matriz_data = [
        [Paragraph("Req.", table_header), Paragraph("Endpoint de la API", table_header), Paragraph("Método", table_header), Paragraph("Roles Permitidos", table_header), Paragraph("Tipo de Operación", table_header)],
        [Paragraph("RF01", table_cell_bold), Paragraph("<i>Interno (Seeders al iniciar)</i>", table_cell), Paragraph("—", table_cell), Paragraph("Sistema", table_cell), Paragraph("Sembrado de datos maestros", table_cell)],
        [Paragraph("RF02", table_cell_bold), Paragraph("/api/v1/auth/registro", table_cell), Paragraph("POST", table_cell), Paragraph("Público", table_cell), Paragraph("Registro de clientes (Módulo 11)", table_cell)],
        [Paragraph("RF03", table_cell_bold), Paragraph("/api/v1/auth/login", table_cell), Paragraph("POST", table_cell), Paragraph("Público", table_cell), Paragraph("Autenticación y entrega de JWT", table_cell)],
        [Paragraph("RF04", table_cell_bold), Paragraph("/api/v1/auth/me", table_cell), Paragraph("GET", table_cell), Paragraph("Admin, Vend, Cliente", table_cell), Paragraph("Recuperación de sesión (F5)", table_cell)],
        [Paragraph("RF05", table_cell_bold), Paragraph("/api/v1/auth/perfil", table_cell), Paragraph("PUT", table_cell), Paragraph("Admin, Vend, Cliente", table_cell), Paragraph("Actualización de datos propios", table_cell)],
        [Paragraph("RF06", table_cell_bold), Paragraph("/api/v1/usuarios/**", table_cell), Paragraph("CRUD", table_cell), Paragraph("Administrador", table_cell), Paragraph("Gestión de cuentas y personal", table_cell)],
        [Paragraph("RF07", table_cell_bold), Paragraph("/api/v1/productos/**", table_cell), Paragraph("GET", table_cell), Paragraph("Público", table_cell), Paragraph("Consulta de catálogo y ofertas", table_cell)],
        [Paragraph("RF08", table_cell_bold), Paragraph("/api/v1/productos/**", table_cell), Paragraph("POST/PUT/DEL", table_cell), Paragraph("Administrador", table_cell), Paragraph("Mantenimiento de inventario", table_cell)],
        [Paragraph("RF08", table_cell_bold), Paragraph("/api/v1/categorias/**", table_cell), Paragraph("POST/DEL", table_cell), Paragraph("Administrador", table_cell), Paragraph("Mantenimiento de categorías", table_cell)],
        [Paragraph("RF09", table_cell_bold), Paragraph("/api/v1/boletas/checkout", table_cell), Paragraph("POST", table_cell), Paragraph("Cliente, Admin", table_cell), Paragraph("Pasarela simulada y descuento stock", table_cell)],
        [Paragraph("RF10", table_cell_bold), Paragraph("/api/v1/boletas/mis-boletas", table_cell), Paragraph("GET", table_cell), Paragraph("Cliente, Admin", table_cell), Paragraph("Historial de compras personales", table_cell)],
        [Paragraph("RF10", table_cell_bold), Paragraph("/api/v1/boletas/**", table_cell), Paragraph("GET", table_cell), Paragraph("Admin, Vendedor", table_cell), Paragraph("Supervisión general de órdenes", table_cell)],
        [Paragraph("RF11", table_cell_bold), Paragraph("/api/v1/boletas/{id}/reintentar-pago", table_cell), Paragraph("PUT", table_cell), Paragraph("Cliente, Admin", table_cell), Paragraph("Reintento de pago y descuento stock", table_cell)],
        [Paragraph("RF12", table_cell_bold), Paragraph("/api/v1/boletas/{id}/anular", table_cell), Paragraph("PUT", table_cell), Paragraph("Administrador", table_cell), Paragraph("Anulación y reposición de stock", table_cell)],
        [Paragraph("RF13", table_cell_bold), Paragraph("/api/v1/noticias/**", table_cell), Paragraph("GET", table_cell), Paragraph("Público", table_cell), Paragraph("Difusión de noticias del blog", table_cell)],
        [Paragraph("RF14", table_cell_bold), Paragraph("/api/v1/contactos", table_cell), Paragraph("POST / GET", table_cell), Paragraph("Público / Admin, Vend", table_cell), Paragraph("Formulario y bandeja de contacto", table_cell)],
        [Paragraph("RF15", table_cell_bold), Paragraph("/api/v1/dashboard/resumen", table_cell), Paragraph("GET", table_cell), Paragraph("Admin, Vendedor", table_cell), Paragraph("Métricas estadísticas en tiempo real", table_cell)],
        [Paragraph("RF16", table_cell_bold), Paragraph("/swagger-ui.html", table_cell), Paragraph("GET", table_cell), Paragraph("Público / Desarrolladores", table_cell), Paragraph("Documentación interactiva OpenAPI", table_cell)]
    ]

    t_matriz = Table(matriz_data, colWidths=[35, 140, 60, 115, 154])
    t_matriz.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), table_header_bg),
        ('GRID', (0,0), (-1,-1), 0.5, table_border),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
    ]))
    story.append(t_matriz)

    # Construir PDF
    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"PDF generado exitosamente en: {filename}")

if __name__ == "__main__":
    build_pdf()
