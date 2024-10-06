package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.CrearOrdenDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.InformacionOrdenCompraDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.ItemOrdenDTO;

import co.edu.uniquindio.UniEventos.modelo.documentos.Orden;
import com.mercadopago.resources.preference.Preference;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

public interface OrdenServicio {

    String crearOrden(CrearOrdenDTO crearOrdenDTO) throws Exception;

    String cancelarOrden(ObjectId idOrden) throws Exception;

    List<ItemOrdenDTO> obtenerHistorialOrdenes(ObjectId idCuenta) throws Exception;

    InformacionOrdenCompraDTO obtenerInformacionOrden(ObjectId idOrden) throws Exception;

    List<Orden> ordenesUsuario(ObjectId idUsuario) throws Exception;

    Preference realizarPago(ObjectId idOrden) throws Exception;

    void recibirNotificacionMercadoPago(Map<String, Object> request);

    void enviarCorreoOrden(ObjectId objectId, String email) throws Exception;
}
