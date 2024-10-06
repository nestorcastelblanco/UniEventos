package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.*;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.TokenDTO;
import org.bson.types.ObjectId;

import java.util.List;

public interface CuentaServicio {

    void crearCuenta(CrearCuentaDTO cuenta) throws Exception;

    ObjectId editarCuenta(EditarCuentaDTO cuenta) throws Exception;

    String eliminarCuenta(String id) throws Exception;

    InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception;

    String enviarCodigoRecuperacionPassword(String correo) throws Exception;

    String cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws Exception;

    TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception;

    String encriptarPassword(String password) throws Exception;

    List<ItemCuentaDTO> listarCuentas() throws Exception;

    String activarCuenta(ValidarCuentaDTO validarCuentaDTO) throws Exception;
}
