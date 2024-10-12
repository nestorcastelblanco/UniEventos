package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.ResenaDTO;

import java.util.List;

public interface ResenaServicio {

    String crearReseña(ResenaDTO resenaDTO) throws Exception;


    List<ResenaDTO> obtenerReseñasPorEvento(String idEvento) throws Exception;


    List<ResenaDTO> obtenerReseñasPorUsuario(String idUsuario) throws Exception;

}
