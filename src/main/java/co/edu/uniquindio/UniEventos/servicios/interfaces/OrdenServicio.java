package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.MercadoPagoDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.CrearOrdenDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.InformacionOrdenCompraDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.ItemOrdenDTO;

import co.edu.uniquindio.UniEventos.modelo.documentos.Orden;
import co.edu.uniquindio.UniEventos.modelo.vo.DetalleCarrito;
import co.edu.uniquindio.UniEventos.modelo.vo.DetalleOrden;
import com.mercadopago.resources.preference.Preference;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

public interface OrdenServicio {

    String crearOrden(CrearOrdenDTO crearOrdenDTO) throws Exception;

    String cancelarOrden(String idOrden) throws Exception;

    List<ItemOrdenDTO> obtenerHistorialOrdenes() throws Exception;

    InformacionOrdenCompraDTO obtenerInformacionOrden(String idOrden) throws Exception;

    List<Orden> ordenesUsuario(ObjectId idUsuario) throws Exception;

    MercadoPagoDTO realizarPago(String idOrden) throws Exception;

    void recibirNotificacionMercadoPago(Map<String, Object> request);

    void enviarCorreoOrden(ObjectId objectId, String email) throws Exception;

    //METODOS DE PRUEBA JUNIT
    String crearOrdenPrueba(CrearOrdenDTO orden) throws Exception;

    String cancelarOrdenPrueba(String idOrden) throws Exception;

    List<Orden> ordenesUsuarioPrueba(ObjectId idUsuario) throws Exception;

    List<ItemOrdenDTO> obtenerHistorialOrdenesPrueba() throws Exception;

    InformacionOrdenCompraDTO obtenerInformacionOrdenPrueba(String idOrden) throws Exception;

    void enviarCorreoOrdenPrueba(ObjectId idOrden, String emailCliente) throws Exception;

    Preference realizarPagoPrueba(String idOrden) throws Exception;

    void recibirNotificacionMercadoPagoPrueba(Map<String, Object> request);

    List<DetalleOrden> convertirDetalleCarritoAOrdenPrueba(List<DetalleCarrito> items);

    List<DetalleCarrito> convertirDetalleOrdenACarritoPrueba(@NotNull(message = "Debe proporcionar al menos un ítem en la orden") List<CrearOrdenDTO.ItemDTO> items) throws Exception;
}
