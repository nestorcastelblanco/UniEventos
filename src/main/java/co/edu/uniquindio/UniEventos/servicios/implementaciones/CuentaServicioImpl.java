package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.config.JWTUtils;
import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.*;
import co.edu.uniquindio.UniEventos.dto.EmailDTOs.EmailDTO;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.TokenDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Carrito;
import co.edu.uniquindio.UniEventos.modelo.documentos.Cuenta;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCuenta;
import co.edu.uniquindio.UniEventos.modelo.enums.Rol;
import co.edu.uniquindio.UniEventos.modelo.vo.CodigoValidacion;
import co.edu.uniquindio.UniEventos.modelo.vo.Usuario;
import co.edu.uniquindio.UniEventos.repositorios.CarritoRepo;
import co.edu.uniquindio.UniEventos.repositorios.CuentaRepo;
import co.edu.uniquindio.UniEventos.servicios.interfaces.CuentaServicio;
import co.edu.uniquindio.UniEventos.servicios.interfaces.EmailServicio;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CuentaServicioImpl implements CuentaServicio {

    private final EmailServicio emailServicio;
    private JWTUtils jwtUtils;
    private final CuentaRepo cuentaRepo;
    private final CarritoRepo carritoRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public CuentaServicioImpl(CuentaRepo cuentaRepo, EmailServicio emailServicio, BCryptPasswordEncoder passwordEncoder, JWTUtils jwtUtils, CarritoRepo carritoRepo) {
        this.cuentaRepo = cuentaRepo;
        this.emailServicio = emailServicio;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.carritoRepo = carritoRepo;
    }

    @Override
    public void crearCuenta(CrearCuentaDTO cuenta) throws Exception {

        if (existeCedula(cuenta.cedula())) {
            throw new Exception("Ya existe una cuenta con esta cedula");
        }

        if (existeCorreo(cuenta.correo())) {
            throw new Exception("Ya existe una cuenta con este correo");
        }

        Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setEmail(cuenta.correo());

        nuevaCuenta.setPassword(passwordEncoder.encode(cuenta.password()));

        nuevaCuenta.setRol(Rol.CLIENTE);
        nuevaCuenta.setFechaRegistro(LocalDateTime.now());
        nuevaCuenta.setEstadoCuenta(EstadoCuenta.INACTIVO);
        nuevaCuenta.setUsuario(new Usuario(
                cuenta.cedula(),
                cuenta.nombre(),
                cuenta.telefono(),
                cuenta.direccion()
        ));

        String codigoActivacion = generarCodigoValidacion();
        nuevaCuenta.setCodigoValidacionRegistro(
                new CodigoValidacion(
                        codigoActivacion ,
                        LocalDateTime.now()
                )
        );

        emailServicio.enviarCorreo(new EmailDTO("Codigo de activación de cuenta de Unieventos", "El codigo de activacion asignado para recuperar la cuenta es el siguiente " + codigoActivacion, nuevaCuenta.getEmail()));
        cuentaRepo.save(nuevaCuenta);
    }

    private boolean existeCedula(String cedula) {
        return cuentaRepo.buscarCuentaPorCedula(cedula).isPresent();
    }

    private boolean existeCorreo(String correo) {
        return cuentaRepo.buscarCuentaPorCorreo(correo).isPresent();
    }

    private String generarCodigoValidacion() {
        String cadena = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int indice = (int) (Math.random() * cadena.length());
            resultado.append(cadena.charAt(indice));
        }

        return resultado.toString();
    }

    @Override
    public ObjectId editarCuenta(EditarCuentaDTO cuenta) throws Exception {

        Cuenta cuentaUsuario = obtenerCuenta(cuenta.id());

        if (cuentaUsuario.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
            throw new Exception("La cuenta no está activa");
        }

        cuentaUsuario.getUsuario().setNombre(cuenta.nombre());
        cuentaUsuario.getUsuario().setDireccion(cuenta.direccion());
        cuentaUsuario.getUsuario().setTelefono(cuenta.telefono());

        // Cifrar la nueva contraseña antes de almacenarla
        cuentaUsuario.setPassword(passwordEncoder.encode(cuenta.password()));

        cuentaRepo.save(cuentaUsuario);

        return cuentaUsuario.getId();
    }

    @Override
    public String eliminarCuenta(String id) throws Exception {

        Cuenta cuentaUsuario = obtenerCuenta(id);

        if (cuentaUsuario.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
            throw new Exception("La cuenta no está activa");
        }

        cuentaUsuario.setEstadoCuenta(EstadoCuenta.ELIMINADO);
        cuentaRepo.save(cuentaUsuario);

        return "Eliminado";
    }

    @Override
    public InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception {
        Cuenta cuentaUsuario = obtenerCuenta(id);
        if (cuentaUsuario == null || cuentaUsuario.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
            return null;
        }
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

        if (cuentaOptional.isEmpty()) {
            throw new Exception("El correo no está registrado");
        }

        Cuenta cuentaUsuario = cuentaOptional.get();

        if (cuentaUsuario.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
            throw new Exception("La cuenta no está activa");
        }

        String codigoValidacion = generarCodigoValidacion();

        cuentaUsuario.setCodigoValidacionPassword(
                new CodigoValidacion(
                        codigoValidacion,
                        LocalDateTime.now()
                )
        );

        emailServicio.enviarCorreo(new EmailDTO("Codigo de recuperacion de contraseña de Unieventos", "El codigo de verificacion asignado para recuperar la cuenta es el siguiente " + codigoValidacion, cuentaUsuario.getEmail()));

        cuentaRepo.save(cuentaUsuario);

        return "Se ha enviado un código a su correo, con una duración de 15 minutos";
    }

    @Override
    public String cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(cambiarPasswordDTO.email());

        if (cuentaOptional.isEmpty()) {
            throw new Exception("El correo no está registrado");
        }

        Cuenta cuentaUsuario = cuentaOptional.get();

        if (cuentaUsuario.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
            throw new Exception("La cuenta no está activa");
        }

        CodigoValidacion codigoValidacion = cuentaUsuario.getCodigoValidacionPassword();

        if (codigoValidacion.getCodigoValidacion().equals(cambiarPasswordDTO.codigoVerificacion())) {
            if (codigoValidacion.getFechaCreacion().plusMinutes(15).isAfter(LocalDateTime.now())) {
                cuentaUsuario.setPassword(passwordEncoder.encode(cambiarPasswordDTO.passwordNueva()));
                cuentaRepo.save(cuentaUsuario);
            } else {
                throw new Exception("Su código de verificación ya expiró");
            }
        } else {
            throw new Exception("El código no es correcto");
        }

        return "La clave se ha cambiado correctamente";
    }

    @Override
    public TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception {

        Cuenta cuenta = obtenerPorEmail(loginDTO.correo());

        if (cuenta.getEstadoCuenta() != EstadoCuenta.ACTIVO) {
            throw new Exception("La cuenta no está activa");
        }

        if (!passwordEncoder.matches(loginDTO.password().trim(), cuenta.getPassword().trim())) {
            throw new Exception("La contraseña es incorrecta");
        }

        Map<String, Object> map = construirClaims(cuenta);
        return new TokenDTO(jwtUtils.generarToken(cuenta.getEmail(), map));
    }

    private Cuenta obtenerPorEmail(String correo) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo(correo);

        if (cuentaOptional.isEmpty()) {
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
    public List<ItemCuentaDTO> listarCuentas() throws Exception {
        List<Cuenta> cuentas = cuentaRepo.findAll();

        if (cuentas.isEmpty()) {
            throw new Exception("No se encontraron cuentas registradas");
        }

        // Convertimos las cuentas a una lista de ItemCuentaDTO para retornar la información
        return cuentas.stream().map(cuenta -> new ItemCuentaDTO(
                cuenta.getId(),
                cuenta.getUsuario().getNombre(),
                cuenta.getEmail(),
                cuenta.getUsuario().getTelefono(),
                cuenta.getEstadoCuenta(),
                cuenta.getRol()
        )).toList();
    }

    @Override
    public String activarCuenta(ValidarCuentaDTO validarCuentaDTO) throws Exception {
        Optional<Cuenta> cuenta_activacion = cuentaRepo.buscarCuentaPorCorreo(validarCuentaDTO.correo());
        if (cuenta_activacion.isEmpty()) {
            throw new Exception("No se encuentra una cuenta con el correo ingresado");
        }

        Cuenta cuenta_usuario = cuenta_activacion.get();


        if (cuenta_usuario.getEstadoCuenta() == EstadoCuenta.ELIMINADO) {
            throw new Exception("La cuenta no esta disponible en Unievento");
        }

        if (cuenta_usuario.getEstadoCuenta() == EstadoCuenta.ACTIVO) {
            throw new Exception("La cuenta ya esta activa");
        }

        if (cuenta_usuario.getCodigoValidacionRegistro().getCodigoValidacion().equals(validarCuentaDTO.codigo())){
            if (cuenta_usuario.getCodigoValidacionRegistro().getFechaCreacion().plusMinutes(15).isAfter(LocalDateTime.now())) {
                cuenta_usuario.setEstadoCuenta(EstadoCuenta.ACTIVO);
                cuentaRepo.save(cuenta_usuario);
            } else {
                throw new Exception("Su código de verificación ya expiró");
            }
        } else {
            throw new Exception("El código no es correcto");
        }

        Optional<Cuenta> cuentaCreadaOptional = cuentaRepo.buscarCuentaPorCorreo(cuenta_usuario.getEmail());

        if (cuentaCreadaOptional.isPresent()) {
            Cuenta cuentaCliente = cuentaCreadaOptional.get();
            ObjectId idCliente = cuentaCliente.getId();
            Carrito carrito = Carrito.builder()
                    .fecha(LocalDateTime.now())
                    .items(new ArrayList<>())
                    .idUsuario(idCliente)
                    .build();
            carritoRepo.save(carrito);

        } else {
            throw new Exception("Error: No se encontró la cuenta");
        }
        return "Se activo la cuenta correctamente y se creo el carrito";
    }

    private Cuenta obtenerCuenta(String id) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(id);

        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el id: " + id + " no existe");
        }

        return cuentaOptional.get();
    }
}
