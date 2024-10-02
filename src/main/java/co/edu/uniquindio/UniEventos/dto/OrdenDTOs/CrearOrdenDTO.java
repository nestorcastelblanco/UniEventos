package co.edu.uniquindio.UniEventos.dto.OrdenDTOs;

import co.edu.uniquindio.UniEventos.modelo.vo.DetalleOrden;
import co.edu.uniquindio.UniEventos.modelo.vo.Pago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record CrearOrdenDTO(
        @NotNull(message = "El ID del cliente es obligatorio")
        ObjectId id,

        @NotBlank(message = "El código de la pasarela de pago es obligatorio")
        @Length(max = 50, message = "El código de la pasarela no puede exceder 50 caracteres")
        String codigoPasarela,

        @NotNull(message = "Debe proporcionar al menos un ítem en la orden")
        List<DetalleOrden> items,

        @NotNull(message = "El pago es obligatorio")
        @Valid Pago pago,

        @Min(value = 0, message = "El total debe ser mayor o igual a cero")
        float total,

        ObjectId idCupon
) {
}
