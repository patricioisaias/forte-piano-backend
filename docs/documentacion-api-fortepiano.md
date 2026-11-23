# Documentación de API — FORTE & PIANO REST API

| Campo | Valor |
| :--- | :--- |
| **Versión** | 1.0.0 |
| **Fecha** | 22/11/2026 |
| **URL Base** | `http://localhost:8080/api/v1` |
| **URL Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **URL OpenAPI JSON** | `http://localhost:8080/v3/api-docs` |
| **Autenticación** | Bearer Token (JWT - JSON Web Token en Header `Authorization: Bearer <token>`) |

---

## Matriz General de Endpoints

### 1. Módulo de Autenticación y Perfil (`/api/v1/auth`)

| Método HTTP | Ruta del Endpoint | Descripción | Datos de entrada | Respuestas | API PÚBLICA/PRIVADA | Requiere Autenticación | Roles permitido | Observaciones |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: | :---: | :--- |
| **POST** | `/auth/login` | Inicia sesión y genera token JWT de acceso | `{"correo": "admin@gmail.com", "password": "..."}` | `200: Éxito (Token JWT + Sesión)`<br>`401: Credenciales inválidas`<br>`500: Error servidor` | Pública | No | N/A (Todos) | Retorna token Bearer JWT válido por 24 horas y objeto `sesion` con datos del usuario. |
| **POST** | `/auth/registro` | Registra un nuevo usuario cliente en el sistema | `{"run": "18765432-7", "nombre": "Diego", "apellidos": "Morales", "correo": "diego@duoc.cl", "password": "...", "fechaNacimiento": "2001-02-14", "regionId": 13, "comunaId": 4, "direccion": "Av. Pajaritos 2500"}` | `201: Creado (Token JWT + Sesión)`<br>`400: RUN o Correo inválido/duplicado`<br>`500: Error servidor` | Pública | No | N/A (Todos) | Valida RUN chileno estricto con Módulo 11 y dominios permitidos (`@duoc.cl`, `@profesor.duoc.cl`, `@gmail.com`). Asigna `rolId = 3` (Cliente). |
| **GET** | `/auth/me` | Obtiene la sesión actual del usuario autenticado | `N/A` *(Header `Authorization: Bearer <token>`)* | `200: Éxito (Sesión actual)`<br>`401: No autenticado / Token inválido`<br>`404: Usuario no encontrado` | Privada | Sí | Admin, Vendedor, Cliente | Permite persistir la sesión y estado del usuario en el frontend ante recargas de página (`F5`). |
| **PUT** | `/auth/perfil` | Actualiza la información de perfil del usuario autenticado | `{"nombre": "Diego", "apellidos": "Morales", "correo": "diego@duoc.cl", "regionId": 13, "comunaId": 4, "direccion": "Nueva Dirección 123"}` | `200: Éxito (Perfil actualizado)`<br>`400: Datos inválidos / Correo en uso`<br>`401: No autenticado` | Privada | Sí | Admin, Vendedor, Cliente | Usado en el flujo de Checkout para actualizar dirección de despacho y contacto. |

---

### 2. Módulo de Gestión de Usuarios (`/api/v1/usuarios`)

| Método HTTP | Ruta del Endpoint | Descripción | Datos de entrada | Respuestas | API PÚBLICA/PRIVADA | Requiere Autenticación | Roles permitido | Observaciones |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: | :---: | :--- |
| **GET** | `/usuarios` | Lista todos los usuarios registrados en el sistema | `N/A` | `200: Éxito (Lista de usuarios)`<br>`401: No autenticado`<br>`403: Prohibido` | Privada | Sí | Admin | Exclusivo para administradores en el panel de gestión de personal y clientes. |
| **GET** | `/usuarios/{run}` | Obtiene los detalles de un usuario específico por su RUN | `Parámetro de ruta: {run}` *(ej: 11111111-1)* | `200: Éxito`<br>`401: No autenticado`<br>`403: Prohibido`<br>`404: No encontrado` | Privada | Sí | Admin | Búsqueda insensible a puntos y formato de guion. |
| **POST** | `/usuarios` | Crea un usuario administrativo o vendedor manualmente | `{"run": "22222222-2", "nombre": "Daniel", "apellidos": "Aedo", "correo": "vendedor@gmail.com", "password": "...", "rolId": 2, "regionId": 13, "comunaId": 2, "direccion": "...", "estado": "Activo"}` | `201: Creado`<br>`400: RUN o Correo inválido/duplicado`<br>`401: No autenticado`<br>`403: Prohibido` | Privada | Sí | Admin | Permite asignar directamente roles de Administrador (`1`) o Vendedor (`2`). Clave encriptada con BCrypt. |
| **PUT** | `/usuarios/{run}` | Actualiza datos, rol o estado de un usuario | `{"nombre": "...", "rolId": 2, "estado": "Inactivo", ...}` | `200: Éxito`<br>`400: Datos inválidos`<br>`401: No autenticado`<br>`403: Prohibido`<br>`404: No encontrado` | Privada | Sí | Admin | Permite desactivar/bloquear cuentas o modificar privilegios. |
| **DELETE** | `/usuarios/{run}` | Elimina físicamente a un usuario del sistema | `Parámetro de ruta: {run}` | `204: Sin contenido (Eliminado)`<br>`401: No autenticado`<br>`403: Prohibido`<br>`404: No encontrado` | Privada | Sí | Admin | Eliminación permanente de registro en base de datos MySQL. |

---

### 3. Módulo de Geografía y Roles (`/api/v1`)

| Método HTTP | Ruta del Endpoint | Descripción | Datos de entrada | Respuestas | API PÚBLICA/PRIVADA | Requiere Autenticación | Roles permitido | Observaciones |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: | :---: | :--- |
| **GET** | `/roles` | Lista los roles del sistema (Admin, Vendedor, Cliente) | `N/A` | `200: Éxito (Lista de roles)` | Pública | No | N/A | Pobla selectores de roles en formularios del frontend. |
| **GET** | `/regiones` | Lista todas las regiones de Chile configuradas | `N/A` | `200: Éxito (Lista de regiones)` | Pública | No | N/A | Usado en selectores dependientes de dirección (Región -> Comuna). |
| **GET** | `/comunas` | Lista comunas, con filtro opcional por región | `Query param opcional: ?regionId={id}` | `200: Éxito (Lista de comunas)` | Pública | No | N/A | Si se envía `?regionId=13`, filtra solo comunas de esa región; de lo contrario lista todas. |

---

### 4. Módulo de Catálogo y Ofertas (`/api/v1/productos`, `/api/v1/categorias`)

| Método HTTP | Ruta del Endpoint | Descripción | Datos de entrada | Respuestas | API PÚBLICA/PRIVADA | Requiere Autenticación | Roles permitido | Observaciones |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: | :---: | :--- |
| **GET** | `/categorias` | Lista las categorías de instrumentos y accesorios | `N/A` | `200: Éxito (Lista de categorías)` | Pública | No | N/A | Menú de navegación y filtros por tipo de instrumento. |
| **POST** | `/categorias` | Crea una nueva categoría de productos | `{"nombre": "Guitarras Eléctricas"}` | `201: Creada`<br>`400: Datos inválidos`<br>`401: No autenticado`<br>`403: Prohibido` | Privada | Sí | Admin | Exclusivo para administradores. |
| **DELETE** | `/categorias/{id}` | Elimina una categoría si no posee productos vinculados | `Parámetro de ruta: {id}` | `204: Sin contenido`<br>`400: Categoría con productos asociados`<br>`403: Prohibido`<br>`404: No encontrada` | Privada | Sí | Admin | Validación de integridad referencial. |
| **GET** | `/productos` | Lista el catálogo general con precios y ofertas | `Query params opcionales: ?categoriaId={id}&enOferta={true/false}` | `200: Éxito (Lista de productos)` | Pública | No | N/A | Incluye cálculo de `precioOferta` cuando `enOferta = true`. |
| **GET** | `/productos/{codigo}` | Detalle completo de un producto por su código SKU | `Parámetro de ruta: {codigo}` *(ej: PD-001)* | `200: Éxito (Detalle producto)`<br>`404: No encontrado` | Pública | No | N/A | Vista de detalle del producto en la tienda virtual. |
| **POST** | `/productos` | Registra un nuevo producto en el catálogo | `{"codigo": "PD-003", "nombre": "Piano de Cola", "precio": 4500000, "stock": 5, "stockCritico": 2, "categoriaId": 1, "imagen": "...", "estado": "Activo", "enOferta": false}` | `201: Creado`<br>`400: Código existente o datos inválidos`<br>`401: No autenticado`<br>`403: Prohibido` | Privada | Sí | Admin | Calcula automáticamente `precioOferta` si se especifica `porcentajeDescuento`. |
| **PUT** | `/productos/{codigo}` | Modifica datos, precios, stock u ofertas de un producto | `{"nombre": "...", "precio": 4200000, "stock": 4, "enOferta": true, "porcentajeDescuento": 10}` | `200: Éxito`<br>`400: Datos inválidos`<br>`401: No autenticado`<br>`403: Prohibido`<br>`404: No encontrado` | Privada | Sí | Admin | Gestión de inventario y promociones comerciales. |
| **DELETE** | `/productos/{codigo}` | Elimina o desactiva un producto del catálogo | `Parámetro de ruta: {codigo}` | `204: Sin contenido`<br>`401: No autenticado`<br>`403: Prohibido`<br>`404: No encontrado` | Privada | Sí | Admin | Retiro de producto del catálogo comercial. |

---

### 5. Módulo de Órdenes, Boletas y Pasarela Simulada (`/api/v1/boletas`)

| Método HTTP | Ruta del Endpoint | Descripción | Datos de entrada | Respuestas | API PÚBLICA/PRIVADA | Requiere Autenticación | Roles permitido | Observaciones |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: | :---: | :--- |
| **POST** | `/boletas/checkout` | Procesa compra del carrito, simula pasarela de pago y emite boleta | `{"items": [{"codigo": "PD-001", "cantidad": 1}], "datosEnvio": {"modalidad": "despacho", "destinatario": "...", "telefono": "...", "regionId": 13, "comunaId": 1, "direccion": "..."}, "resultadoTransaccion": "exitoso", "metodoPago": "Tarjeta de Crédito"}` | `201: Emitida / Creada`<br>`400: Stock insuficiente o datos inválidos`<br>`401: No autenticado` | Privada | Sí | Cliente, Admin | Si `resultadoTransaccion = 'exitoso'`, descuenta stock en MySQL, calcula Neto e IVA (19%) y fija estado `Emitida`. Si es `'rechazado'` o `'intentar_mas_tarde'`, no descuenta stock y fija estado `Rechazada` o `Pendiente`. |
| **GET** | `/boletas/mis-boletas` | Lista el historial de compras del cliente autenticado | `N/A` *(Identifica al cliente desde el token JWT)* | `200: Éxito (Lista de boletas del cliente)`<br>`401: No autenticado` | Privada | Sí | Cliente, Admin | Vista "Mis Compras" en el frontend React. |
| **GET** | `/boletas` | Lista todas las órdenes de compra con filtros opcionales | `Query params opcionales: ?clienteRun={run}&estado={estado}` | `200: Éxito (Lista de boletas)`<br>`401: No autenticado`<br>`403: Prohibido` | Privada | Sí | Admin, Vendedor | Panel de monitoreo de ventas para Administradores y Vendedores. |
| **GET** | `/boletas/{id}` | Obtiene el detalle de una boleta, ítems y despacho | `Parámetro de ruta: {id}` *(ej: BOL-SEED-1001)* | `200: Éxito`<br>`401: No autenticado`<br>`403: Prohibido`<br>`404: No encontrada` | Privada | Sí | Admin, Vendedor, Cliente (propietario) | Vista de detalle y comprobante imprimible de la boleta. |
| **PUT** | `/boletas/{id}/reintentar-pago` | Reintenta el pago de una boleta Pendiente o Rechazada | `{"resultadoTransaccion": "exitoso", "metodoPago": "Tarjeta de Débito"}` | `200: Éxito (Pasa a Emitida y descuenta stock)`<br>`400: Stock insuficiente o boleta ya pagada`<br>`401: No autenticado` | Privada | Sí | Cliente, Admin | Permite recuperar pedidos pendientes desde el historial del cliente. |
| **PUT** | `/boletas/{id}/anular` | Anula una boleta emitida y repone el stock de productos | `Parámetro de ruta: {id}` | `200: Éxito (Estado pasa a Anulada)`<br>`400: Boleta ya anulada`<br>`401: No autenticado`<br>`403: Prohibido` | Privada | Sí | Admin | Devuelve automáticamente las unidades al stock en MySQL. |

---

### 6. Módulo de Blog y Contacto (`/api/v1/noticias`, `/api/v1/contactos`)

| Método HTTP | Ruta del Endpoint | Descripción | Datos de entrada | Respuestas | API PÚBLICA/PRIVADA | Requiere Autenticación | Roles permitido | Observaciones |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: | :---: | :--- |
| **GET** | `/noticias` | Lista las noticias y artículos del blog musical | `N/A` | `200: Éxito (Lista de noticias)` | Pública | No | N/A | Vista de Blog en React. |
| **GET** | `/noticias/{slug}` | Obtiene el contenido completo de una noticia por su slug | `Parámetro de ruta: {slug}` *(ej: detalle-1)* | `200: Éxito (Noticia con párrafos)`<br>`404: Noticia no encontrada` | Pública | No | N/A | Vista de lectura de artículo individual. |
| **GET** | `/contactos` | Lista todos los mensajes recibidos desde el formulario | `N/A` | `200: Éxito (Lista de mensajes)`<br>`401: No autenticado`<br>`403: Prohibido` | Privada | Sí | Admin, Vendedor | Bandeja de entrada de mensajes de atención al cliente. |
| **POST** | `/contactos` | Envía un mensaje desde el formulario de contacto | `{"nombre": "Ana López", "correo": "ana@gmail.com", "comentario": "Consulta sobre garantía de pianos..."}` | `201: Creado con éxito`<br>`400: Datos inválidos o dominio de correo no autorizado` | Pública | No | N/A | Valida que el correo pertenezca a dominios válidos y comentario hasta 500 caracteres. |

---

### 7. Módulo de Dashboard Estadístico (`/api/v1/dashboard`)

| Método HTTP | Ruta del Endpoint | Descripción | Datos de entrada | Respuestas | API PÚBLICA/PRIVADA | Requiere Autenticación | Roles permitido | Observaciones |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: | :---: | :--- |
| **GET** | `/dashboard/resumen` | Retorna métricas consolidadas para los gráficos del dashboard | `N/A` | `200: Éxito (Métricas y estadísticas)`<br>`401: No autenticado`<br>`403: Prohibido` | Privada | Sí | Admin, Vendedor | Provee ventasTotales, pedidosTotales, unidadesVendidas, distribución de boletas por estado (`Emitida`, `Pendiente`, `Rechazada`, `Anulada`), productos más vendidos y alertas de stock crítico. |

---

## Códigos de Estado HTTP Utilizados

| Código HTTP | Significado | Cuándo se emite |
| :---: | :--- | :--- |
| **`200 OK`** | Petición exitosa | Consultas `GET` exitosas o modificaciones `PUT` exitosas. |
| **`201 Created`** | Recurso creado exitosamente | Creación exitosa en `POST` (`/auth/registro`, `/usuarios`, `/productos`, `/boletas/checkout`, etc.). |
| **`204 No Content`** | Eliminación exitosa sin cuerpo de retorno | Borrado exitoso en peticiones `DELETE` (`/usuarios/{run}`, `/productos/{codigo}`). |
| **`400 Bad Request`** | Error en los datos enviados | Formato de RUN inválido (Módulo 11), correo no institucional, campos faltantes o stock insuficiente. |
| **`401 Unauthorized`** | Falta autenticación o credenciales erróneas | Token JWT ausente, expirado o credenciales de login incorrectas. |
| **`403 Forbidden`** | Acceso prohibido por falta de permisos (RBAC) | Usuario autenticado intentando acceder a un endpoint restringido para su rol (ej: Cliente intentando listar `/usuarios`). |
| **`404 Not Found`** | Recurso no encontrado | RUN, SKU de producto, ID de boleta o noticia inexistente. |
| **`500 Internal Server Error`** | Error no controlado en el servidor | Falla inesperada en tiempo de ejecución o desconexión de base de datos. |
