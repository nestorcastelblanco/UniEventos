package co.edu.uniquindio.UniEventos.dto.CarritoDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public record EventoCarritoDTO(

        @NotNull(message = "El ID del evento es obligatorio")
        String id, // Cambiado de `idEvento` a `id`

        @NotNull(message = "El ID del cliente es obligatorio")
        String idUsuario,

        @NotBlank(message = "El nombre es obligatorio")
        String nombreEvento,

        @NotNull(message = "La fecha del evento es obligatoria")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") // Asegúrate de que el patrón coincide con el formato de entrada
        LocalDateTime fechaEvento,

        @NotNull(message = "El número de boletas es obligatorio")
        @Min(value = 1, message = "El número de boletas debe ser al menos 1")
        Integer numBoletas,

        @NotBlank(message = "El nombre de la localidad es obligatorio")
        String nombreLocalidad
) {}
