package cl.duoc.fortepiano.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private boolean exito;
    private String token;
    private String mensaje;
    private SesionDto sesion;
}
