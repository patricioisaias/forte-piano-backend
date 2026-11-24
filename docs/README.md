# Documentación Oficial — Forte & Piano Backend

Este directorio contiene toda la documentación técnica, arquitectónica y de usuario para el sistema **Forte & Piano**, desarrollada para la asignatura **Desarrollo Fullstack II (DSY1104)** de **Duoc UC**.

---

## 📁 Estructura del Directorio `docs/`

```text
docs/
├── pdf/                                        # Entregables oficiales compilados en PDF
│   ├── ERS-FortePiano-v2.0.pdf                 # Especificación de Requisitos IEEE 830
│   ├── documentacion-api-fortepiano.pdf        # Matriz de Endpoints (formato Pokémon API)
│   ├── documentacion-pruebas-integracion-api.pdf # Pruebas con Postman, MockMvc y evidencias
│   └── manual-de-usuario-fortepiano.pdf        # Manual paso a paso para el usuario final
│
├── scripts/                                    # Scripts en Python / ReportLab para generar los PDFs
│   ├── generate_all.py                         # Generador maestro (compila los 4 documentos)
│   ├── generate_ers_pdf.py                     # Compilador exclusivo de ERS v2.0
│   ├── generate_api_matrix_pdf.py              # Compilador exclusivo de Matriz API
│   └── generate_all_pdfs.py                    # Compilador de Pruebas y Manual de Usuario
│
├── ERS-FortePiano-v2.0.md                      # Versión Markdown del ERS v2.0
├── documentacion-api-fortepiano.md             # Versión Markdown de la Matriz de Endpoints
├── documentacion-pruebas-integracion-api.md    # Versión Markdown de Pruebas e Integración
├── manual-de-usuario-fortepiano.md             # Versión Markdown del Manual de Usuario
└── plan-migracion-backend-springboot.md        # Plan de migración y distribución paralela (3 integrantes)
```

---

## 📑 Índice de Documentos

| Documento | Descripción | Formato Markdown | Formato PDF |
| :--- | :--- | :---: | :---: |
| **Especificación de Requisitos de Software (ERS v2.0)** | Requisitos funcionales y no funcionales, reglas de negocio, diagrama ER relacional y arquitectura Fullstack según norma IEEE 830-1998. | [`ERS-FortePiano-v2.0.md`](ERS-FortePiano-v2.0.md) | [`pdf/ERS-FortePiano-v2.0.pdf`](pdf/ERS-FortePiano-v2.0.pdf) |
| **Matriz de Endpoints de la API** | Tabla completa de los 25 endpoints REST, parámetros, roles, tipos de acceso y respuestas (formato tipo hoja Pokémon API). | [`documentacion-api-fortepiano.md`](documentacion-api-fortepiano.md) | [`pdf/documentacion-api-fortepiano.pdf`](pdf/documentacion-api-fortepiano.pdf) |
| **Documentación de Pruebas e Integración de APIs** | Estrategia de integración Frontend-Backend, casos de prueba (CP-01 a CP-09) con evidencias en marcos de 1px, y reporte de 58 pruebas automatizadas con JUnit 5 + Mockito + JaCoCo. | [`documentacion-pruebas-integracion-api.md`](documentacion-pruebas-integracion-api.md) | [`pdf/documentacion-pruebas-integracion-api.pdf`](pdf/documentacion-pruebas-integracion-api.pdf) |
| **Manual de Usuario** | Guía de uso del sistema para Clientes y Administradores (Login, Catálogo, Checkout con pasarela simulada, Boletas y Dashboard). Incluye figuras con borde 1px y notas explicativas. | [`manual-de-usuario-fortepiano.md`](manual-de-usuario-fortepiano.md) | [`pdf/manual-de-usuario-fortepiano.pdf`](pdf/manual-de-usuario-fortepiano.pdf) |
| **Plan de Migración Paralela** | Guía metodológica para desarrollo paralelo en 3 ramas desacopladas (Módulos A, B y C) con prompts para asistentes AI. | [`plan-migracion-backend-springboot.md`](plan-migracion-backend-springboot.md) | — |

---

## 🛠️ Cómo Recompilar los PDFs

Si realizas modificaciones en los archivos Markdown y requieres actualizar los archivos PDF, ejecuta el script maestro desde la raíz del proyecto:

```powershell
python docs/scripts/generate_all.py
```
*(Requiere `reportlab` instalado: `pip install reportlab`)*.
