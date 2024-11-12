package co.edu.uniquindio.UniEventos.dto.EventoDTOs;

import co.edu.uniquindio.UniEventos.modelo.enums.EstadoEvento;
import co.edu.uniquindio.UniEventos.modelo.enums.TipoEvento;
import co.edu.uniquindio.UniEventos.modelo.vo.Localidad;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.List;

public record  CrearEventoDTO(
        @NotBlank(message = "El nombre del evento es obligatorio") @Size(max = 100, message = "El nombre del evento no puede exceder los 100 caracteres") String nombre,
        @NotBlank(message = "La descripción es obligatoria") @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres") String descripcion,
        @NotBlank(message = "La dirección es obligatoria") @Size(max = 200, message = "La dirección no puede exceder los 200 caracteres") String direccion,
        @NotBlank(message = "La ciudad es obligatoria") @Size(max = 50, message = "La ciudad no puede exceder los 50 caracteres") String ciudad,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @NotNull(message = "La fecha es obligatoria") LocalDateTime fecha,
        @NotBlank(message = "El tipo de evento es obligatorio") TipoEvento tipo,
        @NotBlank(message = "El tipo de evento es obligatorio") EstadoEvento estado,
        @URL(message = "La URL de la imagen del poster debe ser válida") String imagenPoster,
        @URL(message = "La URL de la imagen de localidades debe ser válida") String imagenLocalidades,
        @NotNull(message = "La lista de localidades es obligatoria") List<Localidad> localidades
) {
}

