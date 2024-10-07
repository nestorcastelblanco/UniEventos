package co.edu.uniquindio.UniEventos;

import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.CambiarPasswordDTO;
import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.LoginDTO;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.TokenDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Cuenta;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCuenta;
import co.edu.uniquindio.UniEventos.repositorios.CuentaRepo;
import co.edu.uniquindio.UniEventos.servicios.implementaciones.CuentaServicioImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class CuentaTest {

    @Autowired
    private CuentaServicioImpl cuentaServicio;

    @Autowired
    private CuentaRepo cuentaRepo;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() throws Exception {
        passwordEncoder = new BCryptPasswordEncoder();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Cargar el archivo JSON desde el directorio de recursos
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("cuentas.json");
        assertNotNull(inputStream, "Archivo cuentas.json no encontrado");

        List<Cuenta> cuentas = objectMapper.readValue(inputStream, new TypeReference<List<Cuenta>>() {});

        // Codificar las contraseñas y guardar las cuentas en el repositorio
        for (Cuenta cuenta : cuentas) {
            cuenta.setPassword(passwordEncoder.encode(cuenta.getPassword())); // Codifica la contraseña
        }
        cuentaRepo.deleteAll(); // Limpiar el repositorio antes de agregar nuevos registros
        cuentaRepo.saveAll(cuentas);
    }

    @Test
    public void testBuscarCuentaPorCedula() {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCedula("12345678");
        assertTrue(cuentaOptional.isPresent(), "La cuenta debería estar presente");
        assertEquals("usuario1@example.com", cuentaOptional.get().getEmail(), "El correo debería coincidir");
    }

    @Test
    public void testBuscarCuentaPorCorreo() {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo("usuario2@example.com");
        assertTrue(cuentaOptional.isPresent(), "La cuenta debería estar presente");
        assertEquals("87654321", cuentaOptional.get().getUsuario().getCedula(), "La cédula debería coincidir");
    }

    @Test
    public void testEditarCuenta() {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCedula("12345678");
        assertTrue(cuentaOptional.isPresent(), "La cuenta debería estar presente");

        Cuenta cuenta = cuentaOptional.get();
        cuenta.getUsuario().setNombre("Usuario Modificado");
        cuentaRepo.save(cuenta);

        Optional<Cuenta> cuentaModificada = cuentaRepo.buscarCuentaPorCedula("12345678");
        assertTrue(cuentaModificada.isPresent(), "La cuenta debería estar presente después de la modificación");
        assertEquals("Usuario Modificado", cuentaModificada.get().getUsuario().getNombre(), "El nombre debería haber sido modificado");
    }

    @Test
    public void testEliminarCuenta() {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCedula("12345678");
        assertTrue(cuentaOptional.isPresent(), "La cuenta debería estar presente");

        Cuenta cuenta = cuentaOptional.get();
        cuenta.setEstadoCuenta(EstadoCuenta.ELIMINADO);
        cuentaRepo.save(cuenta);

        Optional<Cuenta> cuentaEliminada = cuentaRepo.buscarCuentaPorCedula("12345678");
        assertTrue(cuentaEliminada.isPresent(), "La cuenta debería estar presente después de eliminarla");
        assertEquals(EstadoCuenta.ELIMINADO, cuentaEliminada.get().getEstadoCuenta(), "El estado de la cuenta debería ser ELIMINADO");
    }

    @Test
    public void testListarCuentas() {
        List<Cuenta> cuentas = cuentaRepo.findAll();
        assertEquals(5, cuentas.size(), "El número de cuentas debería ser 5");
    }

    @Test
    public void testValidarCuentaPorCorreoYCodigo() {
        String email = "usuario2@example.com";  // correo del usuario
        String codigoValidacion = "def456";      // código de validación

        Optional<Cuenta> optionalCuenta = cuentaRepo.buscarCuentaPorCorreo(email);
        assertTrue(optionalCuenta.isPresent(), "La cuenta no fue encontrada por el email proporcionado.");

        Cuenta cuenta = optionalCuenta.get();
        String codigoGuardado = cuenta.getCodigoValidacionRegistro().getCodigoValidacion();
        assertEquals(codigoValidacion, codigoGuardado, "El código de validación no coincide.");

        cuenta.setEstadoCuenta(EstadoCuenta.ACTIVO);
        cuentaRepo.save(cuenta);

        Cuenta cuentaActualizada = cuentaRepo.findById(String.valueOf(cuenta.getId())).orElseThrow();
        assertEquals(EstadoCuenta.ACTIVO, cuentaActualizada.getEstadoCuenta(), "La cuenta no fue activada correctamente.");
    }

    @Test
    public void testBuscarCuentaPorId() {
        // ID que existe en el archivo cuentas.json
        String cuentaId = "60d21b4667d0d8992e610c85";

        // Ejecutar la búsqueda por ID
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(cuentaId);

        // Verificar que la cuenta fue encontrada
        assertTrue(cuentaOptional.isPresent(), "La cuenta debería estar presente");

        // Verificar que los campos importantes coinciden
        Cuenta cuenta = cuentaOptional.get();
        assertEquals("usuario1@example.com", cuenta.getEmail(), "El correo debería coincidir");
        assertEquals("12345678", cuenta.getUsuario().getCedula(), "La cédula debería coincidir");
        assertEquals(EstadoCuenta.ACTIVO, cuenta.getEstadoCuenta(), "El estado de la cuenta debería ser ACTIVO");
    }

    @Test
    public void testCambiarPassword() throws Exception {
        CambiarPasswordDTO cambiarPasswordDTO = new CambiarPasswordDTO(
                "pwd456",
                "nuevaPassword123",
                "usuario1@example.com"
        );

        String resultado = cuentaServicio.cambiarPassword(cambiarPasswordDTO);
        Assertions.assertEquals("La clave se ha cambiado correctamente", resultado);

        // Verifica que la contraseña haya sido cambiada correctamente
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCorreo("usuario1@example.com");
        Assertions.assertTrue(cuentaOptional.isPresent());
        Assertions.assertTrue(passwordEncoder.matches("nuevaPassword123", cuentaOptional.get().getPassword()));
    }

    @Test
    void testEnviarCodigoRecuperacionPassword() throws Exception {
        // Datos de prueba
        String email = "usuario1@example.com";

        // Llamada al metodo para enviar el código de recuperación
        String respuesta = cuentaServicio.enviarCodigoRecuperacionPassword(email);

        // Verifica que la respuesta sea la esperada
        assertEquals("Se ha enviado un código a su correo, con una duración de 15 minutos", respuesta);

        // Verifica que el código de validación se haya actualizado
        Cuenta cuentaActualizada = cuentaRepo.buscarCuentaPorCorreo(email).orElseThrow(() -> new Exception("El correo no está registrado"));

        // Verifica que el código de validación no sea nulo
        assertNotNull(cuentaActualizada.getCodigoValidacionPassword());

        // Verifica que el código de validación sea correcto (puedes verificar con un patrón o similar)
        assertNotNull(cuentaActualizada.getCodigoValidacionPassword().getCodigoValidacion());

        // Verifica que la fecha de creación del código sea reciente (dentro de los últimos minutos)
        assertTrue(cuentaActualizada.getCodigoValidacionPassword().getFechaCreacion().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    public void testIniciarSesion_Correcto() throws Exception {
        LoginDTO loginDTO = new LoginDTO("usuario1@example.com", "password123");
        TokenDTO tokenDTO = cuentaServicio.iniciarSesion(loginDTO);

        // Verificar que se genera un token no nulo
        assertEquals(tokenDTO.token() != null, true);
    }


}
