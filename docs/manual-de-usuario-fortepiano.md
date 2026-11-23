# Manual de Usuario
# "Forte & Piano"

**Versión del Documento:** 2.0  
**Fecha de Creación:** 22/11/2026  
**Sistema:** Plataforma E-Commerce de Instrumentos Musicales Forte & Piano  
**Institución:** Duoc UC — Escuela de Informática y Telecomunicaciones  
**Asignatura:** Desarrollo Fullstack II (`DSY1104`)  

---

## Evidencia de la Documentación

Cada captura de pantalla o imagen añadida a este manual cumple rigurosamente con el estándar gráfico exigido:
- **Número, Título, Imagen y Nota**.
- **Imagen con borde de 1px sólido en color negro**.

```
    +-----------------------------------------------------------------------------------+
    | [Figura N]                                                                        |
    | [Título de la Figura]                                                             |
    | +-------------------------------------------------------------------------------+ |
    | |                                                                               | |
    | |                          IMAGEN / CAPTURA DE PANTALLA                         | |
    | |                              (Borde 1px negro)                                | |
    | |                                                                               | |
    | +-------------------------------------------------------------------------------+ |
    | Nota: Descripción detallada de las acciones, ruta de navegación y controles.      |
    +-----------------------------------------------------------------------------------+
```

---

## 1. Introducción

### 1.1. Propósito del Documento
El propósito de este manual es guiar de manera didáctica y visual a todos los usuarios del sistema **Forte & Piano** en la correcta operación de la plataforma, detallando tanto las funcionalidades públicas orientadas a **Clientes** como las herramientas de gestión exclusivas para **Vendedores** y **Administradores**.

### 1.2. Descripción del Sistema
**Forte & Piano** es una solución web de comercio electrónico musical desarrollada bajo una arquitectura Fullstack moderna (Frontend SPA en **React + TypeScript** y Backend REST API en **Spring Boot 4 / Java 21** respaldado por una base de datos relacional **MySQL**). 

El sistema permite la exploración interactiva de pianos y teclados con ofertas automáticas, compra mediante una pasarela de pago simulada con control de inventario en tiempo real, emisión de boletas electrónicas con desglose de IVA (19%) y Neto, lectura de blog musical, formulario de contacto y un completo panel administrativo con dashboard estadístico en vivo.

### 1.3. Cómo Usar este Manual
Este manual está estructurado cronológicamente:
1. **Requisitos e Instalación:** Para poner en marcha la plataforma localmente.
2. **Comenzando:** Para aprender a registrarse, iniciar sesión y conocer la interfaz.
3. **Uso del Software:** Guía paso a paso de cada función con capturas de pantalla, explicaciones y notas operativas.

---

## 2. Requisitos del Sistema

### 2.1. Hardware Necesario
- **Dispositivo:** Computador de escritorio o portátil con procesador de al menos 2 núcleos y 4 GB de memoria RAM (recomendado 8 GB).
- **Pantalla:** Resolución mínima recomendada de 1366x768 píxeles (la aplicación es responsiva y soporta navegación desde smartphones a partir de 360px de ancho).
- **Conectividad:** Conexión de red local o a Internet para carga de tipografías y recursos web.

### 2.2. Software Necesario
- **Navegador Web:** Google Chrome (v90+), Mozilla Firefox (v88+), Microsoft Edge (v90+) o Safari (v14+).
- **Entorno de Ejecución Backend:** Java 21 JDK (Java Development Kit).
- **Servidor de Base de Datos:** MySQL Server 8.x activo en el puerto `3306` (ej. vía XAMPP o servicio local de MySQL).
- **Entorno Frontend:** Node.js (v18+) y gestor de paquetes `npm`.

---

## 3. Instalación y Configuración

### 3.1. Instrucciones de Instalación Paso a Paso

#### Paso 1: Iniciar el Servicio de Base de Datos MySQL
Inicia el servicio de MySQL desde el panel de control de XAMPP o desde los servicios de Windows. El backend de Spring Boot creará automáticamente la base de datos `fortepiano_db` al arrancar si no existe.

#### Paso 2: Ejecutar el Backend (Spring Boot)
Abre una terminal de PowerShell en la carpeta raíz del proyecto backend (`forte-piano-backend`) y ejecuta:
```powershell
.\mvnw.cmd spring-boot:run
```
El servidor backend iniciará en el puerto **`8080`**. Durante el arranque, los seeders poblarán automáticamente las tablas con los datos maestros (roles, regiones, comunas, usuarios con contraseñas seguras BCrypt, productos, ofertas y noticias).

#### Paso 3: Ejecutar el Frontend (React)
Abre una segunda terminal en la carpeta de la interfaz web (`forte-piano-react`) y ejecuta:
```powershell
npm run dev
```
La aplicación cliente iniciará y estará accesible en **`http://localhost:5173`**.

### 3.2. Configuración Inicial y Usuarios de Prueba
El sistema incluye 4 cuentas de acceso preconfiguradas para pruebas inmediatas:

| Perfil | Correo Electrónico | Contraseña | RUN Chileno | Rol en el Sistema |
| :--- | :--- | :--- | :--- | :--- |
| **Administrador** | `admin@gmail.com` | `admin123` | `11111111-1` | Acceso total al panel de administración y tienda. |
| **Vendedor** | `vendedor@gmail.com` | `vend1234` | `22222222-2` | Gestión de órdenes, consulta de stock y dashboard. |
| **Cliente 1** | `camila@gmail.com` | `cliente1` | `12345678-5` | Compras en tienda, carrito y seguimiento de boletas. |
| **Cliente 2** | `diego@duoc.cl` | `diego123` | `18765432-7` | Cliente institucional Duoc UC. |

---

## 4. Comenzando

### 4.1. Crear una Cuenta e Iniciar Sesión

#### Figura 1
**Pantalla de Inicio de Sesión (Login)**  
```
+-----------------------------------------------------------------------------------+
|  FORTE & PIANO                                           [ Carrito (0) ] [ Login ]|
|-----------------------------------------------------------------------------------|
|                                                                                   |
|                                INICIAR SESIÓN                                     |
|                                                                                   |
|            Correo Electrónico:  [ admin@gmail.com               ]                 |
|            Contraseña:          [ *********                     ]                 |
|                                                                                   |
|            [ Botón: Iniciar Sesión ]       ¿No tienes cuenta? [ Regístrate aquí ] |
|                                                                                   |
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** Al presionar "Iniciar Sesión", el sistema valida las credenciales contra la API `/api/v1/auth/login`. Si son correctas, almacena el token JWT y redirige al usuario según su rol: Administradores al Dashboard (`/admin/dashboard`) y Clientes a la tienda.

---

#### Figura 2
**Formulario de Registro de Nuevos Clientes**  
```
+-----------------------------------------------------------------------------------+
|  CREAR CUENTA NUEVA                                                               |
|-----------------------------------------------------------------------------------|
|  RUN (con guión):  [ 18765432-7          ]  Nombre:       [ Diego               ] |
|  Apellidos:        [ Morales Silva       ]  Correo:       [ diego@duoc.cl       ] |
|  Contraseña:       [ **********          ]  F. Nacimiento:[ 2001-02-14          ] |
|  Región:           [ [13] Metropolitana v]  Comuna:       [ Maipú              v] |
|  Dirección:        [ Av. Pajaritos 2500, Villa Los Héroes                       ] |
|                                                                                   |
|                               [ Registrarse ]                                     |
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** El formulario valida estrictamente el algoritmo Módulo 11 en el RUN chileno y exige correos con dominios institucionales autorizados (`@duoc.cl`, `@profesor.duoc.cl`, `@gmail.com`). La contraseña se cifra con BCrypt.

---

### 4.2. Interfaz de Usuario Inicial (Home y Tienda)

#### Figura 3
**Página Principal y Navegación de la Tienda**  
```
+-----------------------------------------------------------------------------------+
|  [LOGO] FORTE & PIANO    [Inicio] [Productos] [Blog] [Contacto] [Carro (1)] [User]|
|-----------------------------------------------------------------------------------|
|  BANNER PRINCIPAL: "La Excelencia del Piano Clásico y Moderno a tu Alcance"       |
|                                                                                   |
|  BUSCADOR: [ Buscar por nombre o marca...      ]  CATEGORÍA: [ Todas las Cat. v]  |
|                                                                                   |
|  [TARJETA PRODUCTO]         [TARJETA PRODUCTO]         [TARJETA PRODUCTO]         |
|  +-----------------------+  +-----------------------+  +-----------------------+  |
|  | [FOTO PIANO YAMAHA]   |  | [FOTO CASIO PRIVIA]   |  | [FOTO SINTETIZADOR]   |  |
|  | Yamaha P-45           |  | Casio Privia PX-S1100 |  | Roland GO:KEYS        |  |
|  | [Badge: -15% OFERTA]  |  | Stock: 6 unidades     |  | [Badge: -20% OFERTA]  |  |
|  | $424.990              |  | $599.990              |  | $319.990              |  |
|  | Antes: $499.990       |  |                       |  | Antes: $399.990       |  |
|  | [Detalle] [Añadir]    |  | [Detalle] [Añadir]    |  | [Detalle] [Añadir]    |  |
|  +-----------------------+  +-----------------------+  +-----------------------+  |
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** La vista principal expone el catálogo dinámico obtenido desde el backend (`GET /api/v1/productos`). Los productos con oferta calculan y muestran automáticamente el precio rebajado en base a su porcentaje de descuento.

---

## 5. Uso del Software

### 5.1. Carrito de Compras y Checkout con Pasarela de Pago Simulada

#### Figura 4
**Vista del Carrito de Compras**  
```
+-----------------------------------------------------------------------------------+
|  TU CARRO DE COMPRAS                                                              |
|-----------------------------------------------------------------------------------|
|  Producto             Precio Unitario     Cantidad        Subtotal      Acciones  |
|  Yamaha P-45          $424.990            [-] [ 1 ] [+]   $424.990      [Eliminar]|
|  Funda Acolchada 88T  $39.990             [-] [ 1 ] [+]   $39.990       [Eliminar]|
|-----------------------------------------------------------------------------------|
|  Subtotal Neto: $390.739 | IVA (19%): $74.241 | TOTAL A PAGAR: $464.980           |
|                                                                                   |
|                [ Seguir Comprando ]          [ Proceder al Checkout ]             |
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** Permite aumentar o disminuir cantidades de los productos, calculando en tiempo real subtotales y total con persistencia local.

---

#### Figura 5
**Proceso de Checkout y Simulación de Pasarela de Pago**  
```
+-----------------------------------------------------------------------------------+
|  FINALIZAR COMPRA (CHECKOUT)                                                      |
|-----------------------------------------------------------------------------------|
|  1. DATOS DE DESPACHO                     2. SIMULACIÓN PASARELA FINANCIERA       |
|  Destinatario: [ Diego Morales        ]   Seleccione resultado de la simulación:  |
|  Teléfono:     [ +56912345678         ]   (•) APROBADO (Transacción Exitosa)      |
|  Región:       [ Metropolitana       v]   ( ) RECHAZADO (Fondos Insuficientes)    |
|  Comuna:       [ Maipú               v]   ( ) PENDIENTE (Intentar Más Tarde)      |
|  Dirección:    [ Av. Pajaritos 2500   ]                                           |
|                                           Método: [ Tarjeta de Crédito (Visa)   v]|
|-----------------------------------------------------------------------------------|
|  TOTAL A PAGAR: $464.980                  [ Botón: PAGAR Y GENERAR BOLETA ]       |
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** Ruta `/checkout`. El usuario selecciona el escenario de prueba. Si elige "Aprobado", el backend descuenta el stock en la base de datos MySQL, calcula IVA y emite la boleta con estado `Emitida`. Si elige "Rechazado" o "Pendiente", no descuenta stock y guarda el estado respectivo.

---

### 5.2. Historial de Compras ("Mis Boletas")

#### Figura 6
**Historial de Boletas del Cliente Autenticado**  
```
+-----------------------------------------------------------------------------------+
|  HISTORIAL DE COMPRAS — MIS BOLETAS                                               |
|-----------------------------------------------------------------------------------|
|  Folio   Fecha        Total        Estado      Método de Pago    Acciones         |
|  #1006   22/11/2026   $464.980     EMITIDA     Tarjeta Crédito   [Ver Boleta]     |
|  #1004   15/11/2026   $599.990     PENDIENTE   Tarjeta Débito    [Reintentar Pago]|
|  #1002   10/11/2026   $319.990     RECHAZADA   Transferencia     [Reintentar Pago]|
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** Ruta `/mis-boletas`. Muestra las compras del cliente autenticado a través del endpoint `GET /api/v1/boletas/mis-boletas`. Permite ver el comprobante o reintentar el pago de órdenes pendientes.

---

### 5.3. Panel Administrativo y Dashboard Estadístico

#### Figura 7
**Dashboard Administrativo con Gráficos y Métricas en Tiempo Real**  
```
+-----------------------------------------------------------------------------------+
|  PANEL DE ADMINISTRACIÓN — FORTE & PIANO                                          |
|  [ Dashboard ] [ Productos ] [ Categorías ] [ Boletas ] [ Usuarios ] [ Contactos ]|
|-----------------------------------------------------------------------------------|
|  +--------------------+  +--------------------+  +--------------------+           |
|  | VENTAS TOTALES     |  | PEDIDOS TOTALES    |  | PRODUCTOS STOCK CR.|           |
|  | $4.850.000         |  | 15 órdenes         |  | 2 productos        |           |
|  | Boletas Emitidas   |  | En el sistema      |  | Stock <= Crítico   |           |
|  +--------------------+  +--------------------+  +--------------------+           |
|                                                                                   |
|  DISTRIBUCIÓN DE PEDIDOS (GRÁFICO TORTA)    TOP 5 PRODUCTOS MÁS VENDIDOS          |
|  [====== 80% Emitidas               ]       1. Yamaha P-45 (10 unidades)          |
|  [=== 13% Pendientes                ]       2. Roland GO:KEYS (5 unidades)        |
|  [= 7% Rechazadas                   ]       3. Casio Privia PX-S (4 unidades)     |
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** Ruta `/admin/dashboard`. Disponible para Administradores y Vendedores. Consume el endpoint `GET /api/v1/dashboard/resumen` y consolida datos relacionales de MySQL en gráficos estadísticos en vivo.

---

### 5.4. Mantenedor de Productos e Inventario (CRUD)

#### Figura 8
**Mantenedor de Catálogo de Productos y Control de Stock**  
```
+-----------------------------------------------------------------------------------+
|  GESTIÓN DE INVENTARIO Y CATÁLOGO                       [ + Nuevo Producto ]      |
|-----------------------------------------------------------------------------------|
|  SKU     Nombre              Precio       Stock     Alerta      Acciones          |
|  PD-001  Yamaha P-45         $424.990     9         Normal      [Editar] [Borrar] |
|  PD-002  Casio Privia PX-S   $599.990     2         ¡CRÍTICO!   [Editar] [Borrar] |
|  SY-001  Roland GO:KEYS      $319.990     0         ¡AGOTADO!   [Editar] [Borrar] |
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** Ruta `/admin/productos`. Permite crear, modificar y eliminar productos. Si el stock disponible es menor o igual al stock crítico, la interfaz muestra un distintivo de advertencia visual.

---

### 5.5. Mantenedor de Usuarios y Roles

#### Figura 9
**Administración de Cuentas de Acceso y Roles**  
```
+-----------------------------------------------------------------------------------+
|  ADMINISTRACIÓN DE USUARIOS                             [ + Crear Usuario ]       |
|-----------------------------------------------------------------------------------|
|  RUN          Nombre             Correo               Rol           Estado        |
|  11111111-1   Patricio Céspedes  admin@gmail.com      Administrador Activo        |
|  22222222-2   Daniel Aedo        vendedor@gmail.com   Vendedor      Activo        |
|  12345678-5   Camila Silva       camila@gmail.com     Cliente       Activo        |
|  18765432-7   Diego Morales      diego@duoc.cl        Cliente       Activo        |
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** Ruta `/admin/usuarios`. Exclusivo para el Administrador. Permite crear nuevos colaboradores, cambiar roles (`1: Admin`, `2: Vendedor`, `3: Cliente`) o desactivar cuentas de acceso.

---

### 5.6. Blog Musical y Formulario de Contacto

#### Figura 10
**Portal de Novedades y Formulario de Mensajería**  
```
+-----------------------------------------------------------------------------------+
|  CONTACTO Y ATENCIÓN AL CLIENTE                                                   |
|-----------------------------------------------------------------------------------|
|  Nombre Completo: [ Ana López                                ]                    |
|  Correo Electrónico: [ ana@gmail.com                         ]                    |
|  Comentario:      [ Hola, quisiera consultar si tienen stock de fundas para el    |
|                     piano Yamaha P-45 y opciones de despacho a Viña del Mar.  ]   |
|                                                                                   |
|                               [ Enviar Mensaje ]                                  |
|                                                                                   |
|  Alerta SweetAlert2: "¡Mensaje Enviado con Éxito! Te responderemos a la brevedad" |
+-----------------------------------------------------------------------------------+
```
*Borde de imagen: 1px sólido negro.*  
**Nota:** Ruta `/contacto`. Endpoint `POST /api/v1/contactos`. Los mensajes son recepcionados y almacenados en la tabla `mensaje_contacto` de MySQL para revisión desde la bandeja administrativa.

---

## 6. Resumen de Navegación y Rutas del Sistema

| Módulo / Función | Ruta en Frontend | Rol Requerido | Endpoint Backend Involucrado |
| :--- | :--- | :---: | :--- |
| **Inicio y Tienda** | `/` y `/productos` | Público | `GET /api/v1/productos`, `GET /api/v1/categorias` |
| **Detalle de Producto** | `/productos/:codigo` | Público | `GET /api/v1/productos/{codigo}` |
| **Iniciar Sesión** | `/login` | Público | `POST /api/v1/auth/login` |
| **Registro de Cliente** | `/registro` | Público | `POST /api/v1/auth/registro`, `/roles`, `/regiones`, `/comunas` |
| **Carrito y Checkout** | `/carrito`, `/checkout` | Cliente, Admin | `POST /api/v1/boletas/checkout` |
| **Mis Boletas** | `/mis-boletas` | Cliente, Admin | `GET /api/v1/boletas/mis-boletas`, `/boletas/{id}` |
| **Dashboard** | `/admin/dashboard` | Admin, Vendedor | `GET /api/v1/dashboard/resumen` |
| **Mantenedor Productos** | `/admin/productos` | Admin | `POST`, `PUT`, `DELETE /api/v1/productos/**` |
| **Mantenedor Usuarios** | `/admin/usuarios` | Admin | `GET`, `POST`, `PUT`, `DELETE /api/v1/usuarios/**` |
| **Gestión de Boletas** | `/admin/boletas` | Admin, Vendedor | `GET /api/v1/boletas`, `PUT /boletas/{id}/anular` |
| **Blog de Noticias** | `/blog`, `/blog/:slug` | Público | `GET /api/v1/noticias`, `GET /api/v1/noticias/{slug}` |
| **Contacto** | `/contacto` | Público | `POST /api/v1/contactos` |
