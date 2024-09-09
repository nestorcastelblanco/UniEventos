package co.edu.uniquindio.UniEventos.dto;

import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCupon;
import co.edu.uniquindio.UniEventos.modelo.enums.TipoCupon;

import java.time.LocalDateTime;

public record InformacionCuponDTO(
        String id,
        String nombre,
        String codigo,
        float descuento,
        LocalDateTime fechaVencimiento,
        TipoCupon tipo,
        EstadoCupon estado
) {
}
