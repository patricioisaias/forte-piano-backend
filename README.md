# Forte & Piano — Backend Spring Boot (`forte-piano-backend`)

API REST para el sistema E-Commerce musical **Forte & Piano**, desarrollada en **Spring Boot 4 / Java 21** con **MySQL**, **Spring Data JPA**, **Spring Security + JWT** y documentación interactiva con **Swagger / OpenAPI 3**.

- **Repositorio Backend:** [https://github.com/patricioisaias/forte-piano-backend](https://github.com/patricioisaias/forte-piano-backend)
- **Repositorio Frontend:** [https://github.com/lich73/forte-piano-react](https://github.com/lich73/forte-piano-react)
- **Documentación y Guía de Desarrollo:** Consulte [docs/plan-migracion-backend-springboot.md](docs/plan-migracion-backend-springboot.md)
- **Matriz de Endpoints y Documentación de API:** Consulte [docs/documentacion-api-fortepiano.md](docs/documentacion-api-fortepiano.md)

---

## 👥 Equipo y Distribución de Módulos Paralelos

El desarrollo se realiza de forma **100% paralela (no secuencial)**, con dominios verticales desacoplados (cero colisiones de merge):

| Integrante | Módulo | Ramas de Trabajo | Entidades Principales |
| :--- | :--- | :--- | :--- |
| **Patricio Céspedes** | **Módulo A:** Identidad, Usuarios, Geografía y Seguridad JWT | `feat/patricio-usuarios-ubicacion`<br>`feat/patricio-security-jwt-auth` | `Rol`, `Region`, `Comuna`, `Usuario` |
| **Daniel Aedo** | **Módulo B:** Catálogo, Ofertas, Checkout, Boletas y Pasarela Simulada | `feat/daniel-catalogo-ofertas`<br>`feat/daniel-boletas-pasarela` | `Categoria`, `Producto`, `Boleta`, `DetalleBoleta` |
| **Angelo Ponce** | **Módulo C:** Blog, Contacto, Dashboard Estadístico y QA | `feat/angelo-blog-contacto`<br>`feat/angelo-dashboard-swagger` | `Noticia`, `MensajeContacto`, `DashboardResumenDto` |

> 💡 **Nota para el equipo:** En [docs/plan-migracion-backend-springboot.md](docs/plan-migracion-backend-springboot.md) encontrarán los **prompts listos para copiar y pegar en Antigravity** para cada uno de los módulos.

---

## 🚀 Inicio Rápido para Desarrolladores

### 1. Clonar y posicionarse en `main`
```bash
git clone https://github.com/patricioisaias/forte-piano-backend.git
cd forte-piano-backend
git checkout main
git pull origin main
```

### 2. Crear su rama de trabajo según su módulo

- **Patricio:**
  ```bash
  git checkout -b feat/patricio-usuarios-ubicacion
  ```
- **Daniel:**
  ```bash
  git checkout -b feat/daniel-catalogo-ofertas
  ```
- **Angelo:**
  ```bash
  git checkout -b feat/angelo-blog-contacto
  ```

---

## ⚙️ Configuración y Ejecución

### Requisitos Previos
- **Java 21 JDK**
- **MySQL 8.x** activo en `localhost:3306` (usuario `root`, sin contraseña por defecto).
  > La base de datos `fortepiano_db` se crea automáticamente al levantar el proyecto si no existe.

### Ejecutar el Backend
```bash
# En Windows:
.\mvnw.cmd spring-boot:run

# En Linux / macOS:
./mvnw spring-boot:run
```

### URLs de Interés
- **API Base:** `http://localhost:8080/api/v1`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI Docs:** `http://localhost:8080/v3/api-docs`
