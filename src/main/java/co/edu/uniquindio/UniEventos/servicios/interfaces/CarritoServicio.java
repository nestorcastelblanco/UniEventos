package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.*;
import co.edu.uniquindio.UniEventos.modelo.documentos.Carrito;
import org.bson.types.ObjectId;

import java.util.List;

public interface CarritoServicio {

    String agregarItemCarrito(EventoCarritoDTO eventoCarritoDTO) throws Exception;

    String eliminarItemCarrito(String idDetalle, String idCarrito) throws Exception;

    void eliminarCarrito(EliminarCarritoDTO eliminarCarritoDTO) throws Exception;

    VistaCarritoDTO obtenerInformacionCarrito(ObjectId id) throws Exception;

    String actualizarItemCarrito(ActualizarItemCarritoDTO actualizarItemCarritoDTO) throws Exception;

    double calcularTotalCarrito(ObjectId idCliente) throws Exception;

    String vaciarCarrito(ObjectId id) throws Exception;

    //METODOS JUNIT
    String agregarItemCarritoPrueba(EventoCarritoDTO eventoCarritoDTO) throws Exception;

    String eliminarItemCarritoPrueba(EventoEliminarCarritoDTO eventoEliminarCarritoDTO) throws Exception;

    void eliminarCarritoPrueba(EliminarCarritoDTO eliminarCarritoDTO) throws Exception;

    VistaCarritoDTO obtenerInformacionCarritoPrueba(ObjectId id_carrito) throws Exception;

    List<CarritoListDTO> listarCarritosPrueba();

    String actualizarItemCarritoPrueba(ActualizarItemCarritoDTO actualizarItemCarritoDTO) throws Exception;

    double calcularTotalCarritoPrueba(ObjectId idCliente) throws Exception;

    double obtenerPrecioEventoPrueba(ObjectId idEvento, String nombreLocalidad) throws Exception;

    Carrito obtenerCarritoClientePrueba(ObjectId idCliente) throws Exception;

    String vaciarCarritoPrueba(ObjectId idCliente) throws Exception;

    List<CarritoListDTO> listarCarritos() throws Exception;




}
