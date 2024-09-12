package co.edu.uniquindio.UniEventos.dto.EventoDTOs;

import co.edu.uniquindio.UniEventos.modelo.enums.TipoEvento;
import co.edu.uniquindio.UniEventos.modelo.vo.Localidad;

import java.time.LocalDateTime;
import java.util.List;

public record InformacionEventoDTO(
        String id,
        String nombre,
        String descripcion,
        String direccion,
        String ciudad,
        LocalDateTime fecha,
        TipoEvento tipo,
        String imagenPoster,
        String imagenLocalidades,
        List<Localidad> localidades
) {
}
