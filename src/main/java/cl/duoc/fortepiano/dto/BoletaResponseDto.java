package cl.duoc.fortepiano.dto;

import cl.duoc.fortepiano.entity.Boleta;
import cl.duoc.fortepiano.entity.DetalleBoleta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoletaResponseDto {

    private String id;
    private Integer folio;
    private String fecha;
    private String clienteRun;
    private String clienteNombre;
    private String clienteCorreo;
    private List<DetalleBoleta> items;
    private Integer subtotalNeto;
    private Integer iva;
    private Integer total;
    private String metodoPago;
    private String estado;
    private DatosEnvioDto datosEnvio;
    private String mensajeTransaccion;

    public static BoletaResponseDto from(Boleta boleta) {
        DatosEnvioDto envio = null;
        if (boleta.getModalidad() != null || boleta.getDestinatario() != null || boleta.getDireccion() != null) {
            envio = new DatosEnvioDto(
                    boleta.getModalidad(),
                    boleta.getDestinatario(),
                    boleta.getTelefono(),
                    boleta.getRegionId(),
                    boleta.getRegionNombre(),
                    boleta.getComunaId(),
                    boleta.getComunaNombre(),
                    boleta.getDireccion(),
                    boleta.getReferencia());
        }

        // Copiamos la coleccion dentro de la transaccion para que Jackson no
        // tenga que inicializar el proxy lazy al serializar la respuesta.
        List<DetalleBoleta> items = boleta.getItems() == null
                ? new ArrayList<>()
                : new ArrayList<>(boleta.getItems());

        return BoletaResponseDto.builder()
                .id(boleta.getId())
                .folio(boleta.getFolio())
                .fecha(boleta.getFecha())
                .clienteRun(boleta.getClienteRun())
                .clienteNombre(boleta.getClienteNombre())
                .clienteCorreo(boleta.getClienteCorreo())
                .items(items)
                .subtotalNeto(boleta.getSubtotalNeto())
                .iva(boleta.getIva())
                .total(boleta.getTotal())
                .metodoPago(boleta.getMetodoPago())
                .estado(boleta.getEstado())
                .datosEnvio(envio)
                .mensajeTransaccion(boleta.getMensajeTransaccion())
                .build();
    }
}
