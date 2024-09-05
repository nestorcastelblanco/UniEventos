package co.edu.uniquindio.UniEventos.dto;

import lombok.Getter;

@Getter
public record CambiarPasswordDTO(
        String codigoVerificacion,
        String passwordNueva,
        String email
) {
}
