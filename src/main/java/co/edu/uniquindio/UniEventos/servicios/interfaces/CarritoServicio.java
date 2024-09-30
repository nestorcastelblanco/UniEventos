package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.*;

public interface CarritoServicio {

    String crearCarrito(CrearCarritoDTO carritoDTO) throws Exception;

    String agregarItemCarrito(EventoCarritoDTO eventoCarritoDTO) throws Exception;

    String eliminarItemCarrito(EventoEliminarCarritoDTO eventoCarritoDTO) throws Exception;

    void eliminarCarrito(EliminarCarritoDTO eliminarCarritoDTO) throws Exception;

    void obtenerInformacionCarrito(VistaCarritoDTO carritoDTO) throws Exception;

    String actualizarItemCarrito(ActualizarItemCarritoDTO actualizarItemCarritoDTO) throws Exception;

    double calcularTotalCarrito(String idCliente) throws Exception;

    String vaciarCarrito(String id) throws Exception;
}
