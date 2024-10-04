package co.edu.uniquindio.UniEventos.dto.EventoDTOs;

import co.edu.uniquindio.UniEventos.modelo.enums.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ItemEventoDTO(
        @NotBlank(message = "El id del evento es obligatorio")
        String id,

        @NotBlank(message = "El nombre del evento es obligatorio")
        String nombre,

        @NotNull(message = "La fecha del evento es obligatoria")
        LocalDateTime fecha,

        @NotNull(message = "El tipo de evento es obligatorio")
        TipoEvento tipo
) {
}
