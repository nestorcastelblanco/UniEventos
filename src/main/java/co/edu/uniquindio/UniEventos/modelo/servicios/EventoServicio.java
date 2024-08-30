package co.edu.uniquindio.UniEventos.modelo.servicios;

import co.edu.uniquindio.UniEventos.modelo.dto.*;

import java.util.List;

public interface EventoServicio {

    String crearEvento(CrearEventoDTO cuenta) throws Exception;

    String editarCuenta(EditarEventoDTO cuenta) throws Exception;

    String eliminarCuenta(String id) throws Exception;

    InformacionEventoDTO obtenerInformacionCuenta(String id) throws Exception;

    List<ItemEventoDTO> listarEventos();

    List<ItemEventoDTO> filtrarEventos (FiltroEventoDTO filtroEventoDTO) throws Exception;


}
