package co.edu.uniquindio.UniEventos.dto.CuentaDTOs;

public record CambiarPasswordDTO(
        String codigoVerificacion,
        String passwordNueva,
        String email
) {
}
