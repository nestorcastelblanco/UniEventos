package co.edu.uniquindio.UniEventos.modelo.dto;

public record CambiarPasswordDTO(
        String codigoVerificacion,
        String passwordNueva
) {
}
