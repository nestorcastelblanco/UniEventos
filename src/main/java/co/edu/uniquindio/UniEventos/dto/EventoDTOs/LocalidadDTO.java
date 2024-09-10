package co.edu.uniquindio.UniEventos.dto.EventoDTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LocalidadDTO(
        @NotBlank(message = "El nombre de la localidad es obligatorio")
        @Length(max = 50, message = "El nombre de la localidad no debe exceder los 50 caracteres")
        String nombre,

        @Min(value = 0, message = "El precio debe ser un valor positivo")
        float precio,

        @Min(value = 0, message = "La capacidad debe ser un valor positivo")
        int capacidad
) {
}

