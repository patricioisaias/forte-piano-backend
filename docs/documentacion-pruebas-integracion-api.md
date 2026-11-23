# Documentación de APIs e Integración
# "Forte & Piano"

**Título:** Documentación de API e Integración Fullstack para E-Commerce Musical Forte & Piano  
**Versión del Documento:** 2.0  
**Fecha de Creación:** 22/11/2026  
**Asignatura:** Desarrollo Fullstack II (`DSY1104`) — Evaluación Parcial N° 3  
**Equipo de Desarrollo:**  
- **Patricio Céspedes** (*Módulo A: Identidad, Usuarios, Geografía y Seguridad JWT*)  
- **Daniel Aedo** (*Módulo B: Catálogo, Ofertas, Checkout, Boletas y Pasarela Simulada*)  
- **Angelo Ponce** (*Módulo C: Blog, Contacto, Dashboard Estadístico y QA*)  

---

## 1. Descripción General

Este documento proporciona una descripción exhaustiva y detallada de los endpoints de la API REST diseñados para gestionar la plataforma de comercio electrónico musical **Forte & Piano**, así como el protocolo y evidencia de integración con el frontend desarrollado en **React + TypeScript + Vite**.

Está dirigido a desarrolladores, administradores de sistemas y docentes evaluadores de la asignatura **DSY1104** que interactúan, verifican o auditan los servicios backend desarrollados en **Spring Boot 4 / Java 21** y la base de datos relacional **MySQL (`fortepiano_db`)**.

---

## 2. Información General

- **URL Base de la API:** `http://localhost:8080/api/v1/`
- **URL Swagger UI (Documentación Interactiva):** `http://localhost:8080/swagger-ui.html`
- **URL OpenAPI 3 Docs (JSON):** `http://localhost:8080/v3/api-docs`
- **URL Frontend React:** `http://localhost:5173/`
- **Mecanismo de Autenticación:** Bearer Token (JSON Web Token - JWT) transmitido en el encabezado `Authorization: Bearer <token_jwt>`.

---

## 3. Documentación Detallada de Endpoints

### 3.1. Endpoints de Autenticación e Identidad (`/api/v1/auth`)

#### • POST `/api/v1/auth/login`
- **Descripción:** Autentica a un usuario registrado mediante sus credenciales y emite un token JWT firmado digitalmente junto a los datos de la sesión.
- **Método:** `POST`
- **Datos de Entrada:**
  ```json
  {
    "correo": "admin@gmail.com",
    "password": "admin123"
  }
  ```
- **Respuesta:**
  - `200 OK`: Éxito en la autenticación. Retorna token JWT y datos de sesión.
    ```json
    {
      "exito": true,
      "token": "eyJhbGciOiJIUzI1NiJ9...",
      "mensaje": "Inicio de sesión exitoso.",
      "sesion": {
        "run": "11111111-1",
        "nombre": "Patricio",
        "apellidos": "Céspedes",
        "correo": "admin@gmail.com",
        "rolId": 1,
        "rolNombre": "Administrador"
      }
    }
    ```
  - `401 Unauthorized`: Credenciales inválidas o cuenta inactiva.
- **Autenticación:** No (Endpoint público).
- **Observaciones:** Token con expiración de 24 horas (`86400000 ms`). Contraseña validada con `BCryptPasswordEncoder`.

#### • POST `/api/v1/auth/registro`
- **Descripción:** Registra un nuevo cliente (`rolId = 3`) en el sistema con validación de RUN chileno.
- **Método:** `POST`
- **Datos de Entrada:**
  ```json
  {
    "run": "18765432-7",
    "nombre": "Diego",
    "apellidos": "Morales",
    "correo": "diego@duoc.cl",
    "password": "diego123",
    "fechaNacimiento": "2001-02-14",
    "regionId": 13,
    "comunaId": 4,
    "direccion": "Av. Pajaritos 2500"
  }
  ```
- **Respuesta:**
  - `201 Created`: Usuario registrado con éxito, retornando token y sesión.
  - `400 Bad Request`: RUN inválido (Módulo 11), correo no institucional o correo ya registrado.
- **Autenticación:** No (Público).
- **Observaciones:** Valida algoritmo Módulo 11 (rechazando RUNs sin DV) y lista blanca de dominios (`@duoc.cl`, `@profesor.duoc.cl`, `@gmail.com`).

#### • GET `/api/v1/auth/me`
- **Descripción:** Obtiene los datos del usuario autenticado a partir del token JWT para persistencia de sesión.
- **Método:** `GET`
- **Datos de Entrada:** `N/A` (Header `Authorization: Bearer <token>`).
- **Respuesta:**
  - `200 OK`: Datos de la sesión activa (`run`, `nombre`, `correo`, `rolId`).
  - `401 Unauthorized`: Token ausente, expirado o adulterado.
- **Autenticación:** Sí (Cualquier rol autenticado: Admin, Vendedor, Cliente).
- **Observaciones:** Utilizado por React ante recargas de página (`F5`).

#### • PUT `/api/v1/auth/perfil`
- **Descripción:** Actualiza los datos de contacto y dirección del usuario autenticado.
- **Método:** `PUT`
- **Datos de Entrada:**
  ```json
  {
    "nombre": "Diego",
    "apellidos": "Morales Silva",
    "correo": "diego@duoc.cl",
    "regionId": 13,
    "comunaId": 4,
    "direccion": "Nueva Providencia 1234, Depto 402"
  }
  ```
- **Respuesta:** `200 OK` (Perfil actualizado), `400 Bad Request` (Datos erróneos), `401 Unauthorized`.
- **Autenticación:** Sí (Usuario autenticado).

---

### 3.2. Endpoints de Catálogo y Ofertas (`/api/v1/productos`, `/api/v1/categorias`)

#### • GET `/api/v1/productos`
- **Descripción:** Lista el catálogo de instrumentos musicales y accesorios con soporte de filtros y cálculo de ofertas.
- **Método:** `GET`
- **Datos de Entrada:** Query parameters opcionales: `?texto=piano&categoriaId=1&enOferta=true`
- **Respuesta:**
  - `200 OK`: Lista de productos con precio normal, porcentaje de descuento y `precioOferta` calculado.
- **Autenticación:** No (Público).
- **Observaciones:** Si `enOferta = true` y `porcentajeDescuento > 0`, calcula automáticamente `precioOferta = Math.round(precio * (1 - descuento/100))`.

#### • GET `/api/v1/productos/{codigo}`
- **Descripción:** Obtiene la ficha técnica completa de un producto por su código SKU.
- **Método:** `GET`
- **Datos de Entrada:** Parámetro de ruta `{codigo}` (ej. `PD-001`).
- **Respuesta:** `200 OK`, `404 Not Found`.
- **Autenticación:** No (Público).

#### • POST `/api/v1/productos`
- **Descripción:** Registra un nuevo producto en el catálogo.
- **Método:** `POST`
- **Datos de Entrada:**
  ```json
  {
    "codigo": "PD-003",
    "nombre": "Yamaha Clavinova CLP-745",
    "descripcion": "Piano digital de 88 teclas contrapesadas de madera GrandTouch-S.",
    "precio": 2490000,
    "stock": 4,
    "stockCritico": 2,
    "categoriaId": 1,
    "imagen": "/img/productos/clp745.jpg",
    "estado": "Activo",
    "enOferta": false
  }
  ```
- **Respuesta:** `201 Created`, `400 Bad Request`, `403 Forbidden`.
- **Autenticación:** Sí (Solo rol `ROLE_ADMIN`).

#### • GET `/api/v1/categorias`
- **Descripción:** Lista todas las categorías activas de instrumentos.
- **Método:** `GET`
- **Datos de Entrada:** `N/A`
- **Respuesta:** `200 OK` (Array de categorías).
- **Autenticación:** No (Público).

---

### 3.3. Endpoints de Checkout, Boletas y Pasarela Simulada (`/api/v1/boletas`)

#### • POST `/api/v1/boletas/checkout`
- **Descripción:** Procesa la orden de compra, descuenta el stock en MySQL si la transacción es aprobada y emite la boleta con cálculo de impuestos.
- **Método:** `POST`
- **Datos de Entrada:**
  ```json
  {
    "items": [
      {
        "codigo": "PD-001",
        "cantidad": 1
      }
    ],
    "datosEnvio": {
      "modalidad": "despacho",
      "destinatario": "Diego Morales",
      "telefono": "+56912345678",
      "regionId": 13,
      "regionNombre": "Región Metropolitana de Santiago",
      "comunaId": 4,
      "comunaNombre": "Maipú",
      "direccion": "Av. Pajaritos 2500",
      "referencia": "Casa esquina"
    },
    "resultadoTransaccion": "exitoso",
    "metodoPago": "Tarjeta de Crédito"
  }
  ```
- **Respuesta:**
  - `201 Created`: Boleta emitida con éxito.
    ```json
    {
      "id": "BOL-20261122-1006",
      "folio": 1006,
      "fecha": "2026-11-22T22:30:00",
      "clienteRun": "18765432-7",
      "clienteNombre": "Diego Morales",
      "subtotalNeto": 462184,
      "iva": 87815,
      "total": 549999,
      "estado": "Emitida",
      "mensajeTransaccion": "Transacción aprobada con éxito."
    }
    ```
  - `400 Bad Request`: Stock insuficiente para uno o más ítems.
- **Autenticación:** Sí (`ROLE_CLIENTE` o `ROLE_ADMIN`).
- **Observaciones:**
  - Si `resultadoTransaccion = "exitoso"`: Descuenta stock en BD, calcula Neto e IVA (19%) y fija estado `Emitida`.
  - Si `resultadoTransaccion = "rechazado"`: No descuenta stock, fija estado `Rechazada`.
  - Si `resultadoTransaccion = "intentar_mas_tarde"`: No descuenta stock, fija estado `Pendiente`.

#### • GET `/api/v1/boletas/mis-boletas`
- **Descripción:** Lista el historial de compras exclusivo del cliente autenticado.
- **Método:** `GET`
- **Autenticación:** Sí (`ROLE_CLIENTE`, `ROLE_ADMIN`).

#### • PUT `/api/v1/boletas/{id}/anular`
- **Descripción:** Anula una boleta emitida y repone automáticamente el inventario de sus ítems en MySQL.
- **Método:** `PUT`
- **Autenticación:** Sí (Exclusivo `ROLE_ADMIN`).

---

### 3.4. Endpoints de Dashboard, Blog y Contacto

#### • GET `/api/v1/dashboard/resumen`
- **Descripción:** Provee métricas consolidadas en tiempo real para los gráficos estadísticos del panel administrativo.
- **Método:** `GET`
- **Respuesta:**
  - `200 OK`:
    ```json
    {
      "ventasTotales": 4850000,
      "cantidadPedidos": 15,
      "unidadesVendidas": 22,
      "totalProductos": 8,
      "productosEnOferta": 4,
      "productosStockCritico": 2,
      "distribucionEstados": {
        "Emitida": 12,
        "Pendiente": 2,
        "Rechazada": 1,
        "Anulada": 0
      },
      "productosMasVendidos": [...],
      "productosMayorStock": [...]
    }
    ```
- **Autenticación:** Sí (`ROLE_ADMIN` o `ROLE_VENDEDOR`).

#### • GET `/api/v1/noticias` y GET `/api/v1/noticias/{slug}`
- **Descripción:** Lista noticias del blog y lectura de artículo completo por slug.
- **Método:** `GET`
- **Autenticación:** No (Público).

#### • POST `/api/v1/contactos`
- **Descripción:** Recepciona mensajes del formulario de contacto validando longitud (máx 500 car.) y dominio de correo.
- **Método:** `POST`
- **Autenticación:** No (Público).

---

## 4. Integración con el Frontend y Pruebas

### 4.1. Verificación de Entrega de Integración Frontend ↔ Backend

El frontend desarrollado en **React + TypeScript + Vite** interactúa con el backend Spring Boot a través de un servicio HTTP centralizado (`src/services/api.ts`) que gestiona:
1. Interceptores de petición para inyectar automáticamente el encabezado `Authorization: Bearer <token_jwt>`.
2. Interceptores de respuesta para capturar errores `401 Unauthorized` y renovar la sesión o redirigir al login.
3. Proxy de desarrollo en `vite.config.ts` redirigiendo `/api` hacia `http://localhost:8080`.

---

### 4.2. Evidencia de Pruebas de Integración (Capturas de Pantalla)

> **Regla de Formato:** Cada evidencia fotográfica incluye Número de Figura, Título, Imagen con borde de 1px negro y Nota explicativa.

#### Figura 1
**Inicio de Sesión y Autenticación JWT en el Frontend**  
```
+-----------------------------------------------------------------------------------+
|  [VENTANA DEL NAVEGADOR: http://localhost:5173/login]                             |
|                                                                                   |
|    FORTE & PIANO - Iniciar Sesión                                                 |
|    +-------------------------------------------------------------------------+    |
|    | Correo:    [ admin@gmail.com                                          ] |    |
|    | Contraseña: [ *********                                                ] |    |
|    |                                                                         |    |
|    |            [ Iniciar Sesión ] -> Request: POST /api/v1/auth/login       |    |
|    +-------------------------------------------------------------------------+    |
|                                                                                   |
|    Network: Status 200 OK | Response: { exito: true, token: "eyJhbGciOi...",      |
|    sesion: { nombre: "Patricio", rolId: 1, rolNombre: "Administrador" } }         |
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** Al autenticarse correctamente, el backend valida el hash BCrypt y emite el token JWT. El frontend almacena el token en `localStorage` y actualiza el Navbar mostrando el perfil de Administrador.

---

#### Figura 2
**Catálogo Dinámico de Productos con Ofertas Calculadas desde el Backend**  
```
+-----------------------------------------------------------------------------------+
|  [TIENDA: http://localhost:5173/productos]                                        |
|                                                                                   |
|  +--------------------+  +--------------------+  +--------------------+           |
|  | [IMAGEN PIANO]     |  | [IMAGEN TECLADO]   |  | [IMAGEN SINTETIZ]  |           |
|  | Yamaha P-45        |  | Casio Privia PX-S  |  | Roland GO:KEYS     |           |
|  | -15% OFERTA        |  | Stock: 6 unidades  |  | -20% OFERTA        |           |
|  | $424.990           |  | $599.990           |  | $319.990           |           |
|  | Antes: $499.990    |  |                    |  | Antes: $399.990    |           |
|  | [Añadir al Carro]  |  | [Añadir al Carro]  |  | [Añadir al Carro]  |           |
|  +--------------------+  +--------------------+  +--------------------+           |
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** Petición `GET /api/v1/productos`. La API entrega los valores calculados de `precioOferta` en base al campo `porcentajeDescuento`.

---

#### Figura 3
**Procesamiento de Checkout y Pasarela de Pago Simulada Aprobada**  
```
+-----------------------------------------------------------------------------------+
|  [CHECKOUT: http://localhost:5173/checkout]                                       |
|                                                                                   |
|  Datos de Envío: Diego Morales | Av. Pajaritos 2500, Maipú                        |
|  Resumen del Pedido: 1x Yamaha P-45 ($424.990)                                    |
|  Simulación Pasarela: (•) Aprobada  ( ) Rechazada  ( ) Intentar más tarde         |
|                                                                                   |
|  [ Confirmar y Pagar ] -> POST /api/v1/boletas/checkout                           |
|                                                                                   |
|  Alerta SweetAlert2: "¡Compra Realizada con Éxito! Boleta N° 1006 Emitida"        |
|  Detalle en BD: Stock de Yamaha P-45 descontado de 10 a 9 unidades en MySQL.      |
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** Transacción ejecutada bajo `@Transactional`. El backend descuenta el stock en la tabla `producto` y emite la boleta con estado `Emitida`.

---

#### Figura 4
**Dashboard Administrativo con Métricas y Gráficos en Tiempo Real**  
```
+-----------------------------------------------------------------------------------+
|  [PANEL ADMINISTRADOR: http://localhost:5173/admin/dashboard]                     |
|                                                                                   |
|  +-------------------+  +-------------------+  +-------------------+              |
|  | Ventas Totales    |  | Total Pedidos     |  | Stock Crítico     |              |
|  | $4.850.000        |  | 15 órdenes        |  | 2 productos       |              |
|  +-------------------+  +-------------------+  +-------------------+              |
|                                                                                   |
|  [Gráfico Torta]: Estados de Boletas (80% Emitidas, 13% Pendientes, 7% Rechazadas)|
|  [Gráfico Barras]: Top 5 Productos Más Vendidos                                   |
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** Petición `GET /api/v1/dashboard/resumen`. Consolida datos relacionales de las tablas `boleta`, `detalle_boleta` y `producto`.

---

#### Figura 5
**Documentación Interactiva y Pruebas en Vivo mediante Swagger UI**  
```
+-----------------------------------------------------------------------------------+
|  [SWAGGER UI: http://localhost:8080/swagger-ui.html]                              |
|                                                                                   |
|  Forte & Piano - API REST E-Commerce Musical v1.0.0              [ Authorize ]    |
|                                                                                   |
|  > Autenticación          /api/v1/auth/...                                        |
|  > Catálogo y Productos   /api/v1/productos/...                                   |
|  > Boletas y Checkout     /api/v1/boletas/...                                     |
|  > Dashboard              /api/v1/dashboard/...                                   |
|  > Usuarios               /api/v1/usuarios/...                                    |
|  > Ubicación y Roles      /api/v1/roles, /regiones, /comunas                      |
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** Swagger OpenAPI 3 con botón Authorize para probar endpoints protegidos inyectando el Bearer Token JWT.

---

## 5. Batería de Pruebas Automatizadas (JUnit 5 + Mockito)

El backend cuenta con **67 pruebas unitarias e integradas automatizadas**, ejecutadas exitosamente contra base de datos en memoria H2 (`BUILD SUCCESS`):

| Suite de Prueba | Cantidad | Resultado | Alcance de la Prueba |
| :--- | :---: | :---: | :--- |
| **`RutValidatorServiceTest`** | 19 | 19 / 19 Aprobados | Algoritmo Módulo 11 (cuerpo + DV), rechazo de RUNs sin DV y dominios `@duoc.cl`, `@profesor.duoc.cl`, `@gmail.com`. |
| **`AuthServiceTest`** | 6 | 6 / 6 Aprobados | Login exitoso, login con contraseña errónea, login correo inexistente, registro exitoso con BCrypt y consulta de sesión. |
| **`BoletaServiceTest`** | 9 | 9 / 9 Aprobados | Checkout exitoso con descuento de stock, checkout rechazado, checkout pendiente, reintento de pago y anulación con reposición de inventario. |
| **`ProductoServiceTest`** | 5 | 5 / 5 Aprobados | Cálculo de `precioOferta`, control de stock crítico y validación de SKU único. |
| **`DashboardServiceTest`** | 6 | 6 / 6 Aprobados | Agregación de ventas totales, cálculo de porcentajes por estado y ranking de productos. |
| **`NoticiaServiceTest`** | 5 | 5 / 5 Aprobados | Consulta por slug, listado y persistencia de párrafos. |
| **`ContactoServiceTest`** | 4 | 4 / 4 Aprobados | Validación de longitud y dominio en mensajes de contacto. |
| **`NoticiaContactoServiceTest`** | 12 | 12 / 12 Aprobados | Casos de borde en contenido y mensajería. |
| **`FortePianoBackendApplicationTests`** | 1 | 1 / 1 Aprobado | Carga y configuración de contexto de Spring Boot (`contextLoads`). |
| **TOTAL GENERAL** | **67** | **67 / 67 Aprobados (0 fallos)** | **100% de éxito en la suite de pruebas automatizadas.** |
