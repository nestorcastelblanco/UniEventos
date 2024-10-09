package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.ReseñaDTO;
import org.springframework.scheduling.annotation.Async;
import java.util.List;

public interface ReseñaServicio {

    String crearReseña(ReseñaDTO reseñaDTO) throws Exception;


    List<ReseñaDTO> obtenerReseñasPorEvento(String idEvento) throws Exception;


    List<ReseñaDTO> obtenerReseñasPorUsuario(String idUsuario) throws Exception;
}
