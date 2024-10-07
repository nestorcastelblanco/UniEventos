package co.edu.uniquindio.UniEventos.dto.OrdenDTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

public record CrearOrdenDTO(
        @NotNull(message = "El ID del cliente es obligatorio")
        String idCliente,

        @NotBlank(message = "El código de la pasarela de pago es obligatorio")
        @Length(max = 50, message = "El código de la pasarela no puede exceder 50 caracteres")
        String codigoPasarela,

        @NotNull(message = "Debe proporcionar al menos un ítem en la orden")
        List<ItemDTO> items,

        @Min(value = 0, message = "El total debe ser mayor o igual a cero")
        float total,

        String codigoCupon
) {
        public record ItemDTO(
                String idDetalleCarrito,
                String idEvento,
                float precio,
                String nombreLocalidad,
                int cantidad
        ) {
        }

        // Clase interna para Pago
        public record PagoDTO(
                String moneda,
                String tipoPago,
                String detalleEstado,
                String codigoAutorizacion,
                LocalDateTime fecha,
                String idPago,
                float valorTransaccion,
                String estado
        ) {
        }
}
