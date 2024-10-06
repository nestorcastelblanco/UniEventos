package co.edu.uniquindio.UniEventos.dto.EventoDTOs;

import co.edu.uniquindio.UniEventos.modelo.enums.TipoEvento;
import co.edu.uniquindio.UniEventos.modelo.vo.Localidad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record InformacionEventoDTO(
        @NotBlank(message = "El id del evento es obligatorio")
        String id,

        @NotBlank(message = "El nombre del evento es obligatorio")
        @Size(max = 100, message = "El nombre del evento no debe exceder los 100 caracteres")
        String nombre,

        @NotBlank(message = "La descripción del evento es obligatoria")
        @Size(max = 500, message = "La descripción no debe exceder los 500 caracteres")
        String descripcion,

        @NotBlank(message = "La dirección es obligatoria")
        String direccion,

        @NotBlank(message = "La ciudad es obligatoria")
        String ciudad,

        @NotNull(message = "La fecha del evento es obligatoria")
        LocalDateTime fecha,

        @NotNull(message = "El tipo de evento es obligatorio")
        TipoEvento tipo,

        @NotBlank(message = "La imagen del póster es obligatoria")
        String imagenPoster,

        @NotBlank(message = "La imagen de las localidades es obligatoria")
        String imagenLocalidades,

        @NotNull(message = "La lista de localidades no puede estar vacía")
        List<Localidad> localidades
) {
}
