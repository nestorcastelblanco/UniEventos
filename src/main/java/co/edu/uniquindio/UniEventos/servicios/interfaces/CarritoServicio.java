package co.edu.uniquindio.UniEventos.servicios.interfaces;

public interface CarritoServicio {

    String crearCarrito() throws Exception;

    String agregarItemCarrito() throws Exception;

    String eliminarItemCarrito() throws Exception;

    String eliminarCarrito() throws Exception;

    void obtenerInformacionCarrito() throws Exception;
}
