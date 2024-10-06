
package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.EventoDTOs.*;
import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;
import org.bson.types.ObjectId;

import java.util.List;

public interface EventoServicio {

    String crearEvento(CrearEventoDTO cuenta) throws Exception;

    String editarEvento(EditarEventoDTO editarEventoDTO) throws Exception;

    String eliminarEvento(String id) throws Exception;

    List<ItemEventoDTO> listarEventos() throws Exception;

    List<Evento> filtrarEventos (FiltroEventoDTO filtroEventoDTO) throws Exception;

    InformacionEventoDTO obtenerInformacionEvento(String id) throws Exception;

    Evento obtenerEvento(ObjectId id) throws Exception;

}
