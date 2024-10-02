package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.config.JWTUtils;
import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.*;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.TokenDTO;

import java.util.List;

public interface CuentaServicio {

    void crearCuenta(CrearCuentaDTO cuenta) throws Exception;

    String editarCuenta(EditarCuentaDTO cuenta) throws Exception;

    String eliminarCuenta(String id) throws Exception;

    InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception;

    String enviarCodigoRecuperacionPassword(String correo) throws Exception;

    String cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws Exception;

    TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception;

    String validarCuenta(ValidarCuentaDTO validarCuentaDTO) throws Exception;

    String encriptarPassword(String password) throws Exception;

    List<ItemCuentaDTO> listarCuentas() throws Exception;
}
