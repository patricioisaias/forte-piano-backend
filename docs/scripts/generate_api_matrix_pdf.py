import os
from reportlab.lib.pagesizes import letter, landscape
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable
)
from reportlab.pdfgen import canvas

class LandscapeNumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super(LandscapeNumberedCanvas, self).__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super(LandscapeNumberedCanvas, self).showPage()
        super(LandscapeNumberedCanvas, self).save()

    def draw_page_decorations(self, page_count):
        self.saveState()
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#555555"))
        
        # Header (except page 1 cover/banner)
        if self._pageNumber > 1:
            self.drawString(36, 582, "Documentación de API — FORTE & PIANO REST API (Spring Boot + MySQL + JWT)")
            self.drawRightString(756, 582, "Duoc UC | DSY1104")
            self.setStrokeColor(colors.HexColor("#CCCCCC"))
            self.setLineWidth(0.5)
            self.line(36, 574, 756, 574)

        # Footer on all pages
        page_str = f"Página {self._pageNumber} de {page_count}"
        self.drawRightString(756, 22, page_str)
        self.drawString(36, 22, "Evaluación Parcial N° 3 — Desarrollo Fullstack II (Matriz de Endpoints oficial)")
        self.setStrokeColor(colors.HexColor("#CCCCCC"))
        self.setLineWidth(0.5)
        self.line(36, 32, 756, 32)
        self.restoreState()


def generate_api_doc_pdf(filename="docs/pdf/documentacion-api-fortepiano.pdf"):
    # 792 x 612 pt (landscape letter)
    # Margins: 36 pt left/right, 42 pt top/bottom -> Printable width: 720 pt
    doc = SimpleDocTemplate(
        filename,
        pagesize=landscape(letter),
        leftMargin=36, rightMargin=36,
        topMargin=42, bottomMargin=40
    )

    styles = getSampleStyleSheet()

    # Custom styles
    title_style = ParagraphStyle(
        'MainTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=18,
        leading=22,
        textColor=colors.HexColor("#0D3B66"),
        alignment=1, # Center
        spaceAfter=12
    )

    section_style = ParagraphStyle(
        'SectionTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=11,
        leading=14,
        textColor=colors.HexColor("#0077B6"),
        spaceBefore=10,
        spaceAfter=5
    )

    meta_label = ParagraphStyle(
        'MetaLabel',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11,
        textColor=colors.HexColor("#1A1A1A")
    )

    meta_val = ParagraphStyle(
        'MetaVal',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.5,
        leading=11,
        textColor=colors.HexColor("#222222")
    )

    th_style = ParagraphStyle(
        'TH',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=7.5,
        leading=9.5,
        textColor=colors.HexColor("#2C2213"),
        alignment=1 # Center
    )

    td_style = ParagraphStyle(
        'TD',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=6.8,
        leading=8.5,
        textColor=colors.HexColor("#222222")
    )

    td_center = ParagraphStyle(
        'TDCenter',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=6.8,
        leading=8.5,
        textColor=colors.HexColor("#222222"),
        alignment=1
    )

    td_method = ParagraphStyle(
        'TDMethod',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=7.2,
        leading=9,
        alignment=1
    )

    td_code = ParagraphStyle(
        'TDCode',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=6.2,
        leading=7.8,
        textColor=colors.HexColor("#111111")
    )

    story = []

    # Title Banner (matching Pokémon API format)
    story.append(Paragraph("Documentación de API — FORTE & PIANO REST API", title_style))
    story.append(HRFlowable(width="100%", thickness=1.5, color=colors.HexColor("#0D3B66"), spaceAfter=10))

    # Metadata table
    meta_data = [
        [Paragraph("<b>Versión:</b> 1.0.0", meta_label), Paragraph("<b>URL Base:</b> <code>http://localhost:8080/api/v1</code>", meta_val), Paragraph("<b>Autenticación:</b> Bearer Token (JWT)", meta_label)],
        [Paragraph("<b>Fecha:</b> 22/11/2026", meta_label), Paragraph("<b>Swagger UI:</b> <code>http://localhost:8080/swagger-ui.html</code>", meta_val), Paragraph("<b>Header:</b> <code>Authorization: Bearer &lt;token&gt;</code>", meta_val)],
        [Paragraph("<b>Institución:</b> Duoc UC", meta_label), Paragraph("<b>OpenAPI JSON:</b> <code>http://localhost:8080/v3/api-docs</code>", meta_val), Paragraph("<b>Framework:</b> Spring Boot 4 + MySQL + JJWT", meta_label)]
    ]
    meta_table = Table(meta_data, colWidths=[140, 310, 270])
    meta_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#F8F9FA")),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor("#CED4DA")),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor("#E9ECEF")),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
        ('LEFTPADDING', (0,0), (-1,-1), 6),
        ('RIGHTPADDING', (0,0), (-1,-1), 6),
    ]))
    story.append(meta_table)
    story.append(Spacer(1, 10))

    # Helper function to generate an endpoint table matching Pokémon API
    # Width: 720 pt total
    col_widths = [45, 95, 105, 115, 105, 52, 50, 53, 100]

    header_row = [
        Paragraph("Método HTTP", th_style),
        Paragraph("Ruta del Endpoint", th_style),
        Paragraph("Descripción", th_style),
        Paragraph("Datos de Entrada", th_style),
        Paragraph("Respuestas", th_style),
        Paragraph("API Pública / Privada", th_style),
        Paragraph("Requiere Autenticación", th_style),
        Paragraph("Roles Permitido", th_style),
        Paragraph("Observaciones", th_style),
    ]

    def build_module_table(rows_data):
        table_rows = [header_row]
        for r in rows_data:
            method, route, desc, inp, resp, pub_priv, auth, roles, obs = r

            # Color badge for HTTP method
            m_color = "#0077B6"
            if method == "POST":
                m_color = "#2A9D8F"
            elif method == "PUT":
                m_color = "#E76F51"
            elif method == "DELETE":
                m_color = "#E63946"

            m_style = ParagraphStyle(
                'MStyle',
                parent=td_method,
                textColor=colors.HexColor(m_color)
            )

            row = [
                Paragraph(method, m_style),
                Paragraph(f"<b><code>{route}</code></b>", td_code),
                Paragraph(desc, td_style),
                Paragraph(inp, td_code),
                Paragraph(resp, td_style),
                Paragraph(pub_priv, td_center),
                Paragraph(auth, td_center),
                Paragraph(roles, td_center),
                Paragraph(obs, td_style)
            ]
            table_rows.append(row)

        t = Table(table_rows, colWidths=col_widths, repeatRows=1)
        t.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#F6EAC2")), # Pokemon API golden-beige header
            ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#B0A080")),
            ('BOX', (0, 0), (-1, -1), 1, colors.HexColor("#6B5B3B")),
            ('VALIGN', (0, 0), (-1, -1), 'TOP'),
            ('TOPPADDING', (0, 0), (-1, -1), 3),
            ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
            ('LEFTPADDING', (0, 0), (-1, -1), 3),
            ('RIGHTPADDING', (0, 0), (-1, -1), 3),
            ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, colors.HexColor("#FAFAFA")]),
        ]))
        return t

    # 1. Autenticación y Perfil
    story.append(Paragraph("1. Módulo de Autenticación y Perfil (<code>/api/v1/auth</code>)", section_style))
    auth_data = [
        ("POST", "/auth/login", "Inicia sesión y genera token JWT de acceso",
         '{"correo": "admin@gmail.com", "password": "..."}',
         "200: Éxito (Token JWT + Sesión)<br/>401: Credenciales inválidas<br/>500: Error servidor",
         "Pública", "No", "N/A (Todos)", "Retorna token Bearer JWT válido por 24 horas y objeto sesion con datos del usuario."),
        ("POST", "/auth/registro", "Registra un nuevo usuario cliente en el sistema",
         '{"run": "18765432-7", "nombre": "Diego", "correo": "diego@duoc.cl", "password": "...", "regionId": 13, "comunaId": 4, "direccion": "..."}',
         "201: Creado (Token JWT + Sesión)<br/>400: RUN o Correo inválido/duplicado<br/>500: Error servidor",
         "Pública", "No", "N/A (Todos)", "Valida RUN chileno estricto con Módulo 11 y dominios permitidos (@duoc.cl, @profesor.duoc.cl, @gmail.com). Asigna rolId = 3 (Cliente)."),
        ("GET", "/auth/me", "Obtiene la sesión actual del usuario autenticado",
         "N/A (Header Authorization)",
         "200: Éxito (Sesión actual)<br/>401: No autenticado / Token inválido<br/>404: Usuario no encontrado",
         "Privada", "Sí", "Admin, Vendedor, Cliente", "Permite persistir la sesión y estado del usuario en el frontend ante recargas de página (F5)."),
        ("PUT", "/auth/perfil", "Actualiza la información de perfil del usuario autenticado",
         '{"nombre": "Diego", "apellidos": "Morales", "correo": "diego@duoc.cl", "regionId": 13, "comunaId": 4, "direccion": "..."}',
         "200: Éxito (Perfil actualizado)<br/>400: Datos inválidos / Correo en uso<br/>401: No autenticado",
         "Privada", "Sí", "Admin, Vendedor, Cliente", "Usado en el flujo de Checkout para actualizar dirección de despacho y contacto.")
    ]
    story.append(build_module_table(auth_data))
    story.append(Spacer(1, 10))

    # 2. Gestión de Usuarios
    story.append(Paragraph("2. Módulo de Gestión de Usuarios (<code>/api/v1/usuarios</code>)", section_style))
    usr_data = [
        ("GET", "/usuarios", "Lista todos los usuarios registrados en el sistema",
         "N/A", "200: Éxito (Lista de usuarios)<br/>401: No autenticado<br/>403: Prohibido",
         "Privada", "Sí", "Admin", "Exclusivo para administradores en el panel de gestión de personal y clientes."),
        ("GET", "/usuarios/{run}", "Obtiene los detalles de un usuario específico por su RUN",
         "Ruta: {run} (ej: 11111111-1)",
         "200: Éxito<br/>401: No autenticado<br/>403: Prohibido<br/>404: No encontrado",
         "Privada", "Sí", "Admin", "Búsqueda insensible a puntos y formato de guion."),
        ("POST", "/usuarios", "Crea un usuario administrativo o vendedor manualmente",
         '{"run": "22222222-2", "nombre": "Daniel", "correo": "vendedor@gmail.com", "password": "...", "rolId": 2, "estado": "Activo"}',
         "201: Creado<br/>400: RUN o Correo inválido/duplicado<br/>401: No autenticado<br/>403: Prohibido",
         "Privada", "Sí", "Admin", "Permite asignar directamente roles de Administrador (1) o Vendedor (2). Clave encriptada con BCrypt."),
        ("PUT", "/usuarios/{run}", "Actualiza datos, rol o estado de un usuario",
         '{"nombre": "...", "rolId": 2, "estado": "Inactivo"}',
         "200: Éxito<br/>400: Datos inválidos<br/>401: No autenticado<br/>403: Prohibido<br/>404: No encontrado",
         "Privada", "Sí", "Admin", "Permite desactivar/bloquear cuentas o modificar privilegios."),
        ("DELETE", "/usuarios/{run}", "Elimina físicamente a un usuario del sistema",
         "Ruta: {run}", "204: Sin contenido (Eliminado)<br/>401: No autenticado<br/>403: Prohibido<br/>404: No encontrado",
         "Privada", "Sí", "Admin", "Eliminación permanente de registro en base de datos MySQL.")
    ]
    story.append(build_module_table(usr_data))

    story.append(PageBreak())

    # 3. Geografía y Roles
    story.append(Paragraph("3. Módulo de Geografía y Roles (<code>/api/v1</code>)", section_style))
    geo_data = [
        ("GET", "/roles", "Lista los roles del sistema (Admin, Vendedor, Cliente)",
         "N/A", "200: Éxito (Lista de roles)", "Pública", "No", "N/A", "Pobla selectores de roles en formularios del frontend."),
        ("GET", "/regiones", "Lista todas las regiones de Chile configuradas",
         "N/A", "200: Éxito (Lista de regiones)", "Pública", "No", "N/A", "Usado en selectores dependientes de dirección (Región -> Comuna)."),
        ("GET", "/comunas", "Lista comunas, con filtro opcional por región",
         "Query: ?regionId={id}", "200: Éxito (Lista de comunas)", "Pública", "No", "N/A", "Si se envía ?regionId=13, filtra solo comunas de esa región; de lo contrario lista todas.")
    ]
    story.append(build_module_table(geo_data))
    story.append(Spacer(1, 10))

    # 4. Catálogo y Ofertas
    story.append(Paragraph("4. Módulo de Catálogo y Ofertas (<code>/api/v1/productos</code>, <code>/api/v1/categorias</code>)", section_style))
    cat_data = [
        ("GET", "/categorias", "Lista las categorías de instrumentos y accesorios",
         "N/A", "200: Éxito (Lista de categorías)", "Pública", "No", "N/A", "Menú de navegación y filtros por tipo de instrumento."),
        ("POST", "/categorias", "Crea una nueva categoría de productos",
         '{"nombre": "Guitarras Eléctricas"}',
         "201: Creada<br/>400: Datos inválidos<br/>401: No autenticado<br/>403: Prohibido",
         "Privada", "Sí", "Admin", "Exclusivo para administradores."),
        ("DELETE", "/categorias/{id}", "Elimina una categoría si no posee productos vinculados",
         "Ruta: {id}",
         "204: Sin contenido<br/>400: Categoría con productos asociados<br/>403: Prohibido<br/>404: No encontrada",
         "Privada", "Sí", "Admin", "Validación de integridad referencial."),
        ("GET", "/productos", "Lista el catálogo general con precios y ofertas",
         "Query: ?categoriaId={id}&enOferta={bool}",
         "200: Éxito (Lista de productos)", "Pública", "No", "N/A", "Incluye cálculo de precioOferta cuando enOferta = true."),
        ("GET", "/productos/{codigo}", "Detalle completo de un producto por su código SKU",
         "Ruta: {codigo} (ej: YAM-P45-001)", "200: Éxito (Detalle producto)<br/>404: No encontrado",
         "Pública", "No", "N/A", "Vista de detalle del producto en la tienda virtual."),
        ("POST", "/productos", "Registra un nuevo producto en el catálogo",
         '{"codigo": "PD-003", "nombre": "Piano de Cola", "precio": 4500000, "stock": 5, "stockCritico": 2, "categoriaId": 1, "enOferta": false}',
         "201: Creado<br/>400: Código existente o datos inválidos<br/>401: No autenticado<br/>403: Prohibido",
         "Privada", "Sí", "Admin", "Calcula automáticamente precioOferta si se especifica porcentajeDescuento."),
        ("PUT", "/productos/{codigo}", "Modifica datos, precios, stock u ofertas de un producto",
         '{"nombre": "...", "precio": 4200000, "stock": 4, "enOferta": true, "porcentajeDescuento": 10}',
         "200: Éxito<br/>400: Datos inválidos<br/>401: No autenticado<br/>403: Prohibido<br/>404: No encontrado",
         "Privada", "Sí", "Admin", "Gestión de inventario y promociones comerciales."),
        ("DELETE", "/productos/{codigo}", "Elimina o desactiva un producto del catálogo",
         "Ruta: {codigo}", "204: Sin contenido<br/>401: No autenticado<br/>403: Prohibido<br/>404: No encontrado",
         "Privada", "Sí", "Admin", "Retiro de producto del catálogo comercial.")
    ]
    story.append(build_module_table(cat_data))

    story.append(PageBreak())

    # 5. Órdenes, Boletas y Pasarela Simulada
    story.append(Paragraph("5. Módulo de Órdenes, Boletas y Pasarela Simulada (<code>/api/v1/boletas</code>)", section_style))
    bol_data = [
        ("POST", "/boletas/checkout", "Procesa compra del carrito, simula pasarela de pago y emite boleta",
         '{"items": [{"codigo": "PD-001", "cantidad": 1}], "datosEnvio": {...}, "resultadoTransaccion": "exitoso", "metodoPago": "..."}',
         "201: Emitida / Creada<br/>400: Stock insuficiente o datos inválidos<br/>401: No autenticado",
         "Privada", "Sí", "Cliente, Admin",
         "Si resultadoTransaccion = 'exitoso', descuenta stock en MySQL, calcula Neto e IVA (19%) y fija estado Emitida. Si es 'rechazado', no descuenta stock."),
        ("GET", "/boletas/mis-boletas", "Lista el historial de compras del cliente autenticado",
         "N/A (Identifica al cliente desde el token JWT)",
         "200: Éxito (Lista de boletas del cliente)<br/>401: No autenticado",
         "Privada", "Sí", "Cliente, Admin", "Vista 'Mis Compras' en el frontend React."),
        ("GET", "/boletas", "Lista todas las órdenes de compra con filtros opcionales",
         "Query: ?clienteRun={run}&estado={estado}",
         "200: Éxito (Lista de boletas)<br/>401: No autenticado<br/>403: Prohibido",
         "Privada", "Sí", "Admin, Vendedor", "Panel de monitoreo de ventas para Administradores y Vendedores."),
        ("GET", "/boletas/{id}", "Obtiene el detalle de una boleta, ítems y despacho",
         "Ruta: {id} (ej: BOL-SEED-1001)",
         "200: Éxito<br/>401: No autenticado<br/>403: Prohibido<br/>404: No encontrada",
         "Privada", "Sí", "Admin, Vendedor, Cliente", "Vista de detalle y comprobante imprimible de la boleta."),
        ("PUT", "/boletas/{id}/reintentar-pago", "Reintenta el pago de una boleta Pendiente o Rechazada",
         '{"resultadoTransaccion": "exitoso", "metodoPago": "Tarjeta de Débito"}',
         "200: Éxito (Pasa a Emitida y descuenta stock)<br/>400: Stock insuficiente<br/>401: No autenticado",
         "Privada", "Sí", "Cliente, Admin", "Permite recuperar pedidos pendientes desde el historial del cliente."),
        ("PUT", "/boletas/{id}/anular", "Anula una boleta emitida y repone el stock de productos",
         "Ruta: {id}",
         "200: Éxito (Estado pasa a Anulada)<br/>400: Boleta ya anulada<br/>401: No autenticado<br/>403: Prohibido",
         "Privada", "Sí", "Admin", "Devuelve automáticamente las unidades al stock en MySQL.")
    ]
    story.append(build_module_table(bol_data))

    story.append(PageBreak())

    # 6. Blog y Contacto
    story.append(Paragraph("6. Módulo de Blog y Contacto (<code>/api/v1/noticias</code>, <code>/api/v1/contactos</code>)", section_style))
    blog_data = [
        ("GET", "/noticias", "Lista las noticias y artículos del blog musical",
         "N/A", "200: Éxito (Lista de noticias)", "Pública", "No", "N/A", "Vista de Blog en React."),
        ("GET", "/noticias/{slug}", "Obtiene el contenido completo de una noticia por su slug",
         "Ruta: {slug} (ej: detalle-1)", "200: Éxito (Noticia con párrafos)<br/>404: Noticia no encontrada",
         "Pública", "No", "N/A", "Vista de lectura de artículo individual."),
        ("GET", "/contactos", "Lista todos los mensajes recibidos desde el formulario",
         "N/A", "200: Éxito (Lista de mensajes)<br/>401: No autenticado<br/>403: Prohibido",
         "Privada", "Sí", "Admin, Vendedor", "Bandeja de entrada de mensajes de atención al cliente."),
        ("POST", "/contactos", "Envía un mensaje desde el formulario de contacto",
         '{"nombre": "Ana López", "correo": "ana@gmail.com", "comentario": "Consulta sobre garantía..."}',
         "201: Creado con éxito<br/>400: Datos inválidos o dominio no autorizado",
         "Pública", "No", "N/A", "Valida dominios autorizados y comentario hasta 500 caracteres.")
    ]
    story.append(build_module_table(blog_data))
    story.append(Spacer(1, 10))

    # 7. Dashboard Estadístico
    story.append(Paragraph("7. Módulo de Dashboard Estadístico (<code>/api/v1/dashboard</code>)", section_style))
    dash_data = [
        ("GET", "/dashboard/resumen", "Retorna métricas consolidadas para los gráficos del dashboard",
         "N/A", "200: Éxito (Métricas y estadísticas)<br/>401: No autenticado<br/>403: Prohibido",
         "Privada", "Sí", "Admin, Vendedor",
         "Provee ventasTotales, pedidosTotales, unidadesVendidas, distribución por estado (Emitida, Pendiente, Rechazada, Anulada), top ventas y stock crítico.")
    ]
    story.append(build_module_table(dash_data))
    story.append(Spacer(1, 10))

    # Códigos HTTP Utilizados
    story.append(Paragraph("Códigos de Estado HTTP Utilizados en la API", section_style))
    http_headers = [
        Paragraph("Código HTTP", th_style),
        Paragraph("Significado", th_style),
        Paragraph("Cuándo se emite en Forte & Piano", th_style)
    ]
    http_rows = [
        http_headers,
        [Paragraph("<b>200 OK</b>", td_center), Paragraph("Petición exitosa", td_style), Paragraph("Consultas GET exitosas o modificaciones PUT exitosas.", td_style)],
        [Paragraph("<b>201 Created</b>", td_center), Paragraph("Recurso creado exitosamente", td_style), Paragraph("Creación exitosa en POST (/auth/registro, /usuarios, /productos, /boletas/checkout, etc.).", td_style)],
        [Paragraph("<b>204 No Content</b>", td_center), Paragraph("Eliminación exitosa sin cuerpo", td_style), Paragraph("Borrado exitoso en peticiones DELETE (/usuarios/{run}, /productos/{codigo}).", td_style)],
        [Paragraph("<b>400 Bad Request</b>", td_center), Paragraph("Error en datos enviados", td_style), Paragraph("Formato de RUN inválido (Módulo 11), correo no autorizado, campos faltantes o stock insuficiente.", td_style)],
        [Paragraph("<b>401 Unauthorized</b>", td_center), Paragraph("Falta autenticación", td_style), Paragraph("Token JWT ausente, expirado o credenciales de login incorrectas.", td_style)],
        [Paragraph("<b>403 Forbidden</b>", td_center), Paragraph("Acceso prohibido (RBAC)", td_style), Paragraph("Usuario autenticado intentando acceder a endpoint restringido para su rol (ej: Cliente intentando listar /usuarios).", td_style)],
        [Paragraph("<b>404 Not Found</b>", td_center), Paragraph("Recurso no encontrado", td_style), Paragraph("RUN, SKU de producto, ID de boleta o noticia inexistente.", td_style)],
        [Paragraph("<b>500 Internal Error</b>", td_center), Paragraph("Error interno del servidor", td_style), Paragraph("Falla inesperada en tiempo de ejecución o desconexión de base de datos.", td_style)],
    ]
    http_table = Table(http_rows, colWidths=[90, 150, 480])
    http_table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#F6EAC2")),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#B0A080")),
        ('BOX', (0, 0), (-1, -1), 1, colors.HexColor("#6B5B3B")),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
        ('LEFTPADDING', (0, 0), (-1, -1), 4),
        ('RIGHTPADDING', (0, 0), (-1, -1), 4),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, colors.HexColor("#FAFAFA")]),
    ]))
    story.append(http_table)

    canvas_maker = LandscapeNumberedCanvas
    doc.build(story, canvasmaker=canvas_maker)
    print(f"PDF generado exitosamente en: {filename}")

if __name__ == "__main__":
    generate_api_doc_pdf()
