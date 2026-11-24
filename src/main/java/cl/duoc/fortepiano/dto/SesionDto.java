package cl.duoc.fortepiano.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionDto {
    private String run;
    private String nombre;
    private String apellidos;
    private String correo;
    private Integer rolId;
    private String rolNombre;
    private Integer regionId;
    private Integer comunaId;
    private String direccion;
}
