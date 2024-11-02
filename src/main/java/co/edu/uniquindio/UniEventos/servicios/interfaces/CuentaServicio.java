package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.*;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.TokenDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Cuenta;

import java.util.List;

public interface CuentaServicio {

    void crearCuenta(CrearCuentaDTO cuenta) throws Exception;

    String editarCuenta(EditarCuentaDTO cuenta) throws Exception;

    String eliminarCuenta(String id) throws Exception;

    InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception;

    String enviarCodigoRecuperacionPassword(EnviarCodigoDTO enviarCodigoDTO) throws Exception;

    String cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws Exception;

    TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception;

    List<ItemCuentaDTO> listarCuentas() throws Exception;

    String activarCuenta(ValidarCuentaDTO validarCuentaDTO) throws Exception;

    Cuenta obtenerPorEmailPrueba(String correo) throws Exception;

    List<ItemCuentaDTO> listarCuentasPrueba() throws Exception;

    String activarCuentaPrueba(ValidarCuentaDTO validarCuentaDTO) throws Exception;

    Cuenta obtenerCuentaPrueba(String id) throws Exception;
}
