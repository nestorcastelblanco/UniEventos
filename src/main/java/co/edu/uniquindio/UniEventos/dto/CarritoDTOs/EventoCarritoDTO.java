package co.edu.uniquindio.UniEventos.dto.CarritoDTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public record EventoCarritoDTO(

        @NotNull(message = "El ID del evento es obligatorio")
        ObjectId idEvento,

        @NotNull(message = "El ID del cliente es obligatorio")
        ObjectId idUsuario,

        @NotBlank(message = "El nombre es obligatorio")
        String nombreEvento,

        @NotNull(message = "La fecha del evento es obligatoria")
        LocalDateTime fechaEvento,

        @NotNull(message = "El número de boletas es obligatorio")
        @Min(value = 1, message = "El número de boletas debe ser al menos 1")
        Integer numBoletas, // Cambiado a Integer para poder usar NotNull

        @NotBlank(message = "El nombre de la localidad es obligatorio")
        String nombreLocalidad
) {
}
