package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.CrearOrdenDTO;
import co.edu.uniquindio.UniEventos.dto.InformacionOrdenCompraDTO;
import co.edu.uniquindio.UniEventos.dto.ItemOrdenDTO;

import java.util.List;

public interface OrdenServicio {

    String crearOrden(CrearOrdenDTO crearOrdenDTO) throws Exception;

    String cancelarOrden(String idOrden) throws Exception;

    List<ItemOrdenDTO> obtenerHistorialOrdenes(String idCuenta) throws Exception;

    InformacionOrdenCompraDTO obtenerInformacionOrden(String idOrden) throws Exception;

}
