package co.edu.uniquindio.UniEventos.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public record ReseñaDTO(
        @NotNull(message = "El ID no puede estar vacío")
        String id,

        @NotNull(message = "El ID del evento no puede estar vacío")
        String idEvento,

        @NotNull(message = "El ID del usuario no puede estar vacío")
        String idUsuario,

        @Min(value = 1, message = "La calificación mínima es 1")
        @Max(value = 5, message = "La calificación máxima es 5")
        int calificacion,

        @NotBlank(message = "El comentario no puede estar vacío")
        @Size(max = 500, message = "El comentario no puede exceder los 500 caracteres")
        String comentario,

        @NotNull(message = "La fecha de creación no puede estar vacía")
        LocalDateTime fechaCreacion
) {}

