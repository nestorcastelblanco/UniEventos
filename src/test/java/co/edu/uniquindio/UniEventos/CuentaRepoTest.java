package co.edu.uniquindio.UniEventos;

import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.LoginDTO;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.TokenDTO;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCuenta;
import co.edu.uniquindio.UniEventos.servicios.implementaciones.CuentaServicioImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import co.edu.uniquindio.UniEventos.modelo.documentos.Cuenta;
import co.edu.uniquindio.UniEventos.repositorios.CuentaRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CuentaRepoTest {

    @Autowired
    private CuentaServicioImpl cuentaServicio;

    private Cuenta cuenta;

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
        if (inputStream == null) {
            throw new FileNotFoundException("Archivo cuentas.json no encontrado");
        }
        List<Cuenta> cuentas = objectMapper.readValue(inputStream, new TypeReference<List<Cuenta>>() {});

        // Codificar las contraseñas y guardar las cuentas en el repositorio
        for (Cuenta cuenta : cuentas) {
            cuenta.setPassword(passwordEncoder.encode(cuenta.getPassword())); // Codifica la contraseña
        }
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
    public void testEditarCuenta() throws Exception {
        // Obtener la cuenta existente
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCedula("12345678");
        assertTrue(cuentaOptional.isPresent(), "La cuenta debería estar presente");

        Cuenta cuenta = cuentaOptional.get();
        cuenta.getUsuario().setNombre("Usuario Modificado");
        cuentaRepo.save(cuenta);

        // Verificar que se haya modificado
        Optional<Cuenta> cuentaModificada = cuentaRepo.buscarCuentaPorCedula("12345678");
        assertTrue(cuentaModificada.isPresent(), "La cuenta debería estar presente después de la modificación");
        assertEquals("Usuario Modificado", cuentaModificada.get().getUsuario().getNombre(), "El nombre debería haber sido modificado");
    }

    @Test
    public void testEliminarCuenta() throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarCuentaPorCedula("12345678");
        assertTrue(cuentaOptional.isPresent(), "La cuenta debería estar presente");

        Cuenta cuenta = cuentaOptional.get();
        cuenta.setEstadoCuenta(EstadoCuenta.ELIMINADO);
        cuentaRepo.save(cuenta);

        // Verificar que el estado de cuenta haya cambiado
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
        // Datos de prueba del archivo cuentas.json
        String email = "usuario2@example.com";  // correo del usuario
        String codigoValidacion = "def456";      // código de validación

        // Simula la búsqueda de la cuenta por el email
        Optional<Cuenta> optionalCuenta = cuentaRepo.buscarCuentaPorCorreo(email);
        assertTrue(optionalCuenta.isPresent(), "La cuenta no fue encontrada por el email proporcionado.");

        Cuenta cuenta = optionalCuenta.get();

        // Verifica si el código de validación coincide
        String codigoGuardado = cuenta.getCodigoValidacionRegistro().getCodigo();
        assertEquals(codigoValidacion, codigoGuardado, "El código de validación no coincide.");

        // Simula la activación de la cuenta
        cuenta.setEstadoCuenta(EstadoCuenta.ACTIVO);  // Utiliza el enum directamente
        cuentaRepo.save(cuenta);

        // Verificamos que la cuenta fue activada correctamente
        Cuenta cuentaActualizada = cuentaRepo.findById(cuenta.getId()).get();
        assertEquals(EstadoCuenta.ACTIVO, cuentaActualizada.getEstadoCuenta(), "La cuenta no fue activada correctamente.");  // Compara el enum
    }



}


