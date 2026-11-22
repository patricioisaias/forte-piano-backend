package cl.duoc.fortepiano.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @Size(max = 20, message = "El RUN no debe superar los 20 caracteres")
    private String clienteRun;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 150)
    private String clienteNombre;

    @Size(max = 150)
    private String clienteApellidos;

    @NotBlank(message = "El correo del cliente es obligatorio")
    @Email(message = "El correo del cliente no es valido")
    @Size(max = 150)
    private String clienteCorreo;

    @Size(max = 100)
    private String metodoPago;

    @NotNull(message = "Los datos de envio son obligatorios")
    private DatosEnvioDto datosEnvio;

    @NotEmpty(message = "El checkout debe incluir al menos un item")
    private List<ItemCheckoutDto> items;

    /** exitoso | rechazado | intentar_mas_tarde */
    private String resultadoTransaccion;

    /** Si viene, se actualiza esa boleta en lugar de crear una nueva. */
    private String boletaExistenteId;
}
