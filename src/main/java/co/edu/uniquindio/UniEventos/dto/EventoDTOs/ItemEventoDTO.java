package co.edu.uniquindio.UniEventos.dto.EventoDTOs;

import co.edu.uniquindio.UniEventos.modelo.enums.TipoEvento;

import java.time.LocalDateTime;

public record ItemEventoDTO(
        String id,
        String nombre,
        LocalDateTime fecha,
        TipoEvento tipo
) {
}
