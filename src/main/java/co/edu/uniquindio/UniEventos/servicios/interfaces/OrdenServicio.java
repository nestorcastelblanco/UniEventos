package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.CrearOrdenDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.InformacionOrdenCompraDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.ItemOrdenDTO;
import java.util.Map;
import com.mercadopago.resources.preference.Preference;

import java.util.List;

public interface OrdenServicio {

    String crearOrden(CrearOrdenDTO crearOrdenDTO) throws Exception;

    String cancelarOrden(String idOrden) throws Exception;

    List<ItemOrdenDTO> obtenerHistorialOrdenes(String idCuenta) throws Exception;

    InformacionOrdenCompraDTO obtenerInformacionOrden(String idOrden) throws Exception;

    Preference realizarPago(String idOrden) throws Exception;
    void recibirNotificacionMercadoPago(Map<String, Object> request);


}
