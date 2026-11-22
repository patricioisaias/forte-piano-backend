package cl.duoc.fortepiano.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @Column(name = "run", length = 15, nullable = false)
    private String run;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "apellidos", length = 100, nullable = false)
    private String apellidos;

    @Column(name = "correo", length = 150, nullable = false, unique = true)
    private String correo;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "fecha_nacimiento", length = 20)
    private String fechaNacimiento;

    @Column(name = "rol_id", nullable = false)
    private Integer rolId; // 1: Administrador, 2: Vendedor, 3: Cliente

    @Column(name = "region_id")
    private Integer regionId;

    @Column(name = "comuna_id")
    private Integer comunaId;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "estado", length = 20, nullable = false)
    @Builder.Default
    private String estado = "Activo"; // "Activo" | "Inactivo"
}
