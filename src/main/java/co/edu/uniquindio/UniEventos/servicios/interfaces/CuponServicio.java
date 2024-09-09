package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.CrearCuponDTO;
import co.edu.uniquindio.UniEventos.dto.EditarCuponDTO;
import co.edu.uniquindio.UniEventos.dto.InformacionCuponDTO;
import co.edu.uniquindio.UniEventos.dto.ItemCuponDTO;

import java.util.List;

public interface CuponServicio {

    String crearCupon(CrearCuponDTO crearCuponDTO) throws Exception;

    String editarCupon(EditarCuponDTO editarCuponDTO) throws Exception;

    String eliminarCupon(String id) throws Exception;

    List<ItemCuponDTO> obtenerCupones() throws Exception;

    InformacionCuponDTO obtenerInformacionCupon(String id) throws Exception;

    String redimirCupon(String codigo) throws Exception;
}
