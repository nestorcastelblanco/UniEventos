package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.config.JWTUtils;
import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.*;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.TokenDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Cuenta;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCuenta;
import co.edu.uniquindio.UniEventos.modelo.enums.Rol;
import co.edu.uniquindio.UniEventos.modelo.vo.CodigoValidacion;
import co.edu.uniquindio.UniEventos.modelo.vo.Usuario;
import co.edu.uniquindio.UniEventos.repositorios.CuentaRepo;
import co.edu.uniquindio.UniEventos.servicios.interfaces.CuentaServicio;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CuentaServicioImpl implements CuentaServicio {

    private JWTUtils jwtUtils;

    private final CuentaRepo cuentaRepo;

    public CuentaServicioImpl(CuentaRepo cuentaRepo) {
        this.cuentaRepo = cuentaRepo;
    }

    @Override
    public void crearCuenta(CrearCuentaDTO cuenta) throws Exception {

        if (existeCedula(cuenta.cedula())){
            throw new Exception("Ya existe una cuenta con esta cedula");
        }

        if (existeCorreo(cuenta.correo())){
            throw new Exception("Ya existe una cuenta con este correo");
        }

        Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setEmail(cuenta.correo());
        nuevaCuenta.setPassword(cuenta.password());
        nuevaCuenta.setRol(Rol.CLIENTE);
        nuevaCuenta.setFechaRegistro(LocalDateTime.now());
        nuevaCuenta.setEstadoCuenta(EstadoCuenta.INACTIVO);
        nuevaCuenta.setUsuario(new Usuario(
                cuenta.cedula(),
                cuenta.nombre(),
                cuenta.telefono(),
                cuenta.direccion()
        ));
        nuevaCuenta.setCodigoValidacionRegistro(
                new CodigoValidacion(
                        generarCodigoValidacion(),
                        LocalDateTime.now()
                )
        );

        Cuenta cuentaCreada = cuentaRepo.save(nuevaCuenta);
    }

    private boolean existeCedula(String cedula) {
        return cuentaRepo.buscarCuentaPorCedula(cedula).isPresent();
    }

    private boolean existeCorreo(String correo) {
        return cuentaRepo.buscarCuentaPorCorreo(correo).isPresent();
    }

    private String generarCodigoValidacion() {
        String cadena = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String resultado = "";

        for(int i = 0; i < 6; i ++) {
            int indice = (int) (Math.random() * cadena.length());
            char caracter = cadena.charAt(indice);
            resultado += caracter;
        }

        return resultado;
    }
    @Override
    public String editarCuenta(EditarCuentaDTO cuenta) throws Exception {

        Cuenta cuentaUsuario = obtenerCuenta(cuenta.id());
        cuentaUsuario.getUsuario().setNombre(cuenta.nombre());
        cuentaUsuario.getUsuario().setDireccion(cuenta.direccion());
        cuentaUsuario.getUsuario().setTelefono(cuenta.telefono());
        cuentaUsuario.setPassword(cuenta.password());

        cuentaRepo.save(cuentaUsuario);

        return cuentaUsuario.getId();
    }

    @Override
    public String eliminarCuenta(String id) throws Exception {

        Cuenta cuentaUsuario = obtenerCuenta(id);
        cuentaUsuario.setEstadoCuenta(EstadoCuenta.ELIMINADO);

        return "Eliminado";
    }

    @Override
    public InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception {
        Cuenta cuentaUsuario = obtenerCuenta(id);
        return new InformacionCuentaDTO(
                cuentaUsuario.getId(),
                cuentaUsuario.getUsuario().getNombre(),
                cuentaUsuario.getUsuario().getTelefono(),
                cuentaUsuario.getUsuario().getDireccion(),
                cuentaUsuario.getEmail()
        );
    }

    @Override
    public String enviarCodigoRecuperacionPassword(String correo) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(correo);

        if(cuentaOptional.isEmpty()){
            throw new Exception("El correo no esta registrado");
        }

        Cuenta cuentaUsuario = cuentaOptional.get();
        String codigoValidacion = generarCodigoValidacion();

        cuentaUsuario.setCodigoValidacionPassword(
                new CodigoValidacion(
                        codigoValidacion,
                        LocalDateTime.now()
                )
        );

        cuentaRepo.save(cuentaUsuario);

        return "Se ha enviado un código a su correo, con una duracion de 15 minutos";
    }

    @Override
    public String cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(cambiarPasswordDTO.email());

        if(cuentaOptional.isEmpty()){
            throw new Exception("El correo no esta registrado");
        }

        Cuenta cuentaUsuario = cuentaOptional.get();
        CodigoValidacion codigoValidacion = cuentaUsuario.getCodigoValidacionPassword();

        if (codigoValidacion.getCodigo().equals(cambiarPasswordDTO.codigoVerificacion())){
            if(codigoValidacion.getFechaCreacion().plusMinutes(15).isBefore(LocalDateTime.now())){
                cuentaUsuario.setPassword(cambiarPasswordDTO.passwordNueva());
                cuentaRepo.save(cuentaUsuario);
            }
            else{
                throw new Exception("Su código de verificación ya expiró");
            }
        }
        else{
            throw new Exception("El código no es correcto");
        }

        cuentaRepo.save(cuentaUsuario);

        return "La clave se ha cambiado correctamente";
    }

    @Override
    public TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception {

        Cuenta cuenta = obtenerPorEmail(loginDTO.correo());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


        if( !passwordEncoder.matches(loginDTO.password(), cuenta.getPassword()) ) {
            throw new Exception("La contraseña es incorrecta");
        }


        Map<String, Object> map = construirClaims(cuenta);
        return new TokenDTO( jwtUtils.generarToken(cuenta.getEmail(), map) );
    }

    private Cuenta obtenerPorEmail(String correo) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(correo);

        if(cuentaOptional.isEmpty()){
            throw new Exception("La cuenta con el correo: " + correo + " no existe");
        }

        return cuentaOptional.get();
    }

    private Map<String, Object> construirClaims(Cuenta cuenta) {
        return Map.of(
                "rol", cuenta.getRol(),
                "nombre", cuenta.getUsuario().getNombre(),
                "id", cuenta.getId()
        );
    }

    @Override
    public String validarCuenta(ValidarCuentaDTO validarCuentaDTO) throws Exception {
        return "";
    }

    @Override
    public String encriptarPassword(String password) throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode ( password);
    }

    private Cuenta obtenerCuenta (String id) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(id);

        if(cuentaOptional.isEmpty()){
            throw new Exception("La cuenta con el id: " + id + " no existe");
        }

        return cuentaOptional.get();
    }
}
