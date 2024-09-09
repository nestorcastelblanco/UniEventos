package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.EventoDTOs.*;

import java.util.List;

public interface EventoServicio {

    String crearEvento(CrearEventoDTO cuenta) throws Exception;

    String editarEvento(EditarEventoDTO editarEventoDTO) throws Exception;

    String eliminarEvento(String id) throws Exception;

    InformacionEventoDTO obtenerInformacionCuenta(String id) throws Exception;

    List<ItemEventoDTO> listarEventos();

    List<ItemEventoDTO> filtrarEventos (FiltroEventoDTO filtroEventoDTO) throws Exception;


}
