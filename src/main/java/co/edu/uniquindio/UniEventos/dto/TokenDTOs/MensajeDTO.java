package co.edu.uniquindio.UniEventos.dto.TokenDTOs;

public record MensajeDTO<T>(
        boolean error,
        T respuesta
) {
}
