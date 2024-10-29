package co.edu.uniquindio.UniEventos.dto.CuentaDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record EnviarCodigoDTO (
        @NotBlank @Length(max = 30, message = "Ingrese un correo valido") @Email String correo
) {
}