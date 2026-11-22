package cl.duoc.fortepiano.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosEnvioDto {

    @NotBlank(message = "La modalidad de entrega es obligatoria")
    @Size(max = 30)
    private String modalidad;

    @NotBlank(message = "El destinatario es obligatorio")
    @Size(max = 150)
    private String destinatario;

    @Size(max = 30)
    private String telefono;

    private Integer regionId;

    @Size(max = 100)
    private String regionNombre;

    private Integer comunaId;

    @Size(max = 100)
    private String comunaNombre;

    @Size(max = 255)
    private String direccion;

    @Size(max = 255)
    private String referencia;
}
