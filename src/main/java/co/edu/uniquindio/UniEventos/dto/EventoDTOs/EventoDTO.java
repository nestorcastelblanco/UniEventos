package co.edu.uniquindio.UniEventos.dto.EventoDTOs;

import co.edu.uniquindio.UniEventos.modelo.enums.EstadoEvento;
import co.edu.uniquindio.UniEventos.modelo.enums.TipoEvento;
import co.edu.uniquindio.UniEventos.modelo.vo.Localidad;

import java.time.LocalDateTime;
import java.util.List;

public record EventoDTO(String id,

                        String nombre,
                        String direccion,
                        String ciudad,
                        String descripcion ,
                        TipoEvento tipo ,
                        EstadoEvento estado ,
                        String imagenPortada ,
                        LocalDateTime fecha ,
                        List<Localidad> localidades ,
                        String imagenLocalidades) {


}
