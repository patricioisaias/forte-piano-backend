"""
Script maestro para compilar todos los documentos PDF de Forte & Piano:
1. ERS-FortePiano-v2.0.pdf
2. documentacion-api-fortepiano.pdf
3. documentacion-pruebas-integracion-api.pdf
4. manual-de-usuario-fortepiano.pdf
"""
import sys
import os

# Asegurar que el directorio de salida exista
os.makedirs("docs/pdf", exist_ok=True)

# Importar generadores
from generate_ers_pdf import build_pdf as generate_ers
from generate_api_matrix_pdf import generate_api_doc_pdf as generate_api_matrix
from generate_all_pdfs import generate_api_doc_pdf as generate_integration_tests, generate_user_manual_pdf as generate_manual

def main():
    print("=" * 60)
    print("Compilando todos los documentos oficiales PDF (Duoc UC)...")
    print("=" * 60)
    
    # 1. ERS v2.0
    print("\n[1/4] Generando ERS v2.0...")
    generate_ers("docs/pdf/ERS-FortePiano-v2.0.pdf")

    # 2. Matriz API (formato Pokémon)
    print("\n[2/4] Generando Matriz de Endpoints de API...")
    generate_api_matrix("docs/pdf/documentacion-api-fortepiano.pdf")

    # 3. Pruebas e Integración
    print("\n[3/4] Generando Documentación de Pruebas e Integración...")
    generate_integration_tests("docs/pdf/documentacion-pruebas-integracion-api.pdf")

    # 4. Manual de Usuario
    print("\n[4/4] Generando Manual de Usuario...")
    generate_manual("docs/pdf/manual-de-usuario-fortepiano.pdf")

    print("\n" + "=" * 60)
    print("¡Todos los PDFs fueron generados exitosamente en docs/pdf/!")
    print("=" * 60)

if __name__ == "__main__":
    main()
