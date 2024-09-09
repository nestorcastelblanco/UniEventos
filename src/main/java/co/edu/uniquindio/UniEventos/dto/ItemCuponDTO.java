package co.edu.uniquindio.UniEventos.dto;

import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCupon;

import java.time.LocalDateTime;

public record ItemCuponDTO(
        String id,
        String nombre,
        String codigo,
        float descuento,
        LocalDateTime fechaVencimiento,
        EstadoCupon estado
) {
}
