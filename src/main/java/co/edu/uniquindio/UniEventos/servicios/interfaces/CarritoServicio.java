package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.CrearCarritoDTO;
import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.EventoCarritoDTO;
import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.EventoEliminarCarritoDTO;
import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.VistaCarritoDTO;

public interface CarritoServicio {

    String crearCarrito(CrearCarritoDTO carritoDTO) throws Exception;

    String agregarItemCarrito(EventoCarritoDTO eventoCarritoDTO) throws Exception;

    String eliminarItemCarrito(EventoEliminarCarritoDTO eventoCarritoDTO) throws Exception;

    String eliminarCarrito() throws Exception;

    void obtenerInformacionCarrito(VistaCarritoDTO carritoDTO) throws Exception;
}
