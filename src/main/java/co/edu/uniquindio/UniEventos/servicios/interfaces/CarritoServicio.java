package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.CrearCarritoDTO;

public interface CarritoServicio {

    String crearCarrito(CrearCarritoDTO carritoDTO) throws Exception;

    String agregarItemCarrito() throws Exception;

    String eliminarItemCarrito() throws Exception;

    String eliminarCarrito() throws Exception;

    void obtenerInformacionCarrito() throws Exception;
}
