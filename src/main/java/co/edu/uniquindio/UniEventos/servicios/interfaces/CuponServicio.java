package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.CuponDTOs.CrearCuponDTO;
import co.edu.uniquindio.UniEventos.dto.CuponDTOs.EditarCuponDTO;
import co.edu.uniquindio.UniEventos.dto.CuponDTOs.InformacionCuponDTO;
import co.edu.uniquindio.UniEventos.dto.CuponDTOs.ItemCuponDTO;

import java.util.List;

public interface CuponServicio {

    String crearCupon(CrearCuponDTO crearCuponDTO) throws Exception;

    String editarCupon(EditarCuponDTO editarCuponDTO) throws Exception;

    String eliminarCupon(String id) throws Exception;

    List<ItemCuponDTO> obtenerCupones() throws Exception;

    InformacionCuponDTO obtenerInformacionCupon(String id) throws Exception;

    String redimirCupon(String codigo) throws Exception;
}
