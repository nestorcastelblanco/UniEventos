package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.CrearOrdenDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.InformacionOrdenCompraDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.ItemOrdenDTO;

import java.util.List;

public interface OrdenServicio {

    String crearOrden(CrearOrdenDTO crearOrdenDTO) throws Exception;

    String cancelarOrden(String idOrden) throws Exception;

    List<ItemOrdenDTO> obtenerHistorialOrdenes(String idCuenta) throws Exception;

    InformacionOrdenCompraDTO obtenerInformacionOrden(String idOrden) throws Exception;

}
