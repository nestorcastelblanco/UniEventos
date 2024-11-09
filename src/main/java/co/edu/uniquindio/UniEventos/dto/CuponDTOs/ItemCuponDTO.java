package co.edu.uniquindio.UniEventos.dto.CuponDTOs;

import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCupon;
import co.edu.uniquindio.UniEventos.modelo.enums.TipoCupon;

import java.time.LocalDateTime;

public record ItemCuponDTO(
        String id,
        String nombre,
        String codigo,
        float descuento,
        LocalDateTime fechaVencimiento,
        TipoCupon tipo,
        EstadoCupon estado
) {
}
