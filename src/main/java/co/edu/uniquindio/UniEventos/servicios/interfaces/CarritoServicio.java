package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.*;
import org.bson.types.ObjectId;

import java.util.List;

public interface CarritoServicio {

    String agregarItemCarrito(EventoCarritoDTO eventoCarritoDTO) throws Exception;

    String eliminarItemCarrito(EventoEliminarCarritoDTO eventoCarritoDTO) throws Exception;

    void eliminarCarrito(EliminarCarritoDTO eliminarCarritoDTO) throws Exception;

    VistaCarritoDTO obtenerInformacionCarrito(ObjectId id) throws Exception;

    String actualizarItemCarrito(ActualizarItemCarritoDTO actualizarItemCarritoDTO) throws Exception;

    double calcularTotalCarrito(ObjectId idCliente) throws Exception;

    String vaciarCarrito(ObjectId id) throws Exception;

    List<CarritoListDTO> listarCarritos() throws Exception;
}
