package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.*;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.TokenDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Cuenta;
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

    List<ItemCuentaDTO> listarCuentas() throws Exception;

    String activarCuenta(ValidarCuentaDTO validarCuentaDTO) throws Exception;

    // METODOS PARA PRUEBAS UNITARIO JUNIT

    void crearCuentaPrueba(CrearCuentaDTO cuenta) throws Exception;

    ObjectId editarCuentaPrueba(EditarCuentaDTO cuenta) throws Exception;

    String eliminarCuentaPrueba(String id) throws Exception;

    InformacionCuentaDTO obtenerInformacionCuentaPrueba(String id) throws Exception;

    String enviarCodigoRecuperacionPasswordPrueba(String correo) throws Exception;

    String cambiarPasswordPrueba(CambiarPasswordDTO cambiarPasswordDTO) throws Exception;

    TokenDTO iniciarSesionPrueba(LoginDTO loginDTO) throws Exception;

    Cuenta obtenerPorEmailPrueba(String correo) throws Exception;

    List<ItemCuentaDTO> listarCuentasPrueba() throws Exception;

    String activarCuentaPrueba(ValidarCuentaDTO validarCuentaDTO) throws Exception;

    Cuenta obtenerCuentaPrueba(String id) throws Exception;
}
