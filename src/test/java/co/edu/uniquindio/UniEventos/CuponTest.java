package co.edu.uniquindio.UniEventos;

import co.edu.uniquindio.UniEventos.dto.CuponDTOs.CrearCuponDTO;
import co.edu.uniquindio.UniEventos.dto.CuponDTOs.EditarCuponDTO;
import co.edu.uniquindio.UniEventos.dto.CuponDTOs.InformacionCuponDTO;
import co.edu.uniquindio.UniEventos.dto.CuponDTOs.ItemCuponDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Cupon;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCupon;
import co.edu.uniquindio.UniEventos.modelo.enums.TipoCupon;
import co.edu.uniquindio.UniEventos.repositorios.CuponRepo;
import co.edu.uniquindio.UniEventos.servicios.implementaciones.CuponServicioImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CuponTest {

    @Autowired
    private CuponServicioImpl cuponServicio;

    @Autowired
    private CuponRepo cuponRepo;

    @BeforeEach
    public void setUp() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Cargar el archivo JSON desde el directorio de recursos
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("cupones.json");
        Assertions.assertNotNull(inputStream, "Archivo cupones.json no encontrado");

        List<Cupon> cupones = objectMapper.readValue(inputStream, new TypeReference<List<Cupon>>() {});

        // Limpiar el repositorio antes de agregar nuevos registros
        cuponRepo.deleteAll();
        cuponRepo.saveAll(cupones);
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Limpiar después de cada prueba si es necesario
        cuponServicio.eliminarCupon("1"); // Método hipotético para eliminar cupones
    }

    @Test
    public void testEditarCupon() {
        // Primero, asegúrate de que el cupón existe
        Optional<Cupon> cuponOptional = cuponRepo.findById("1");
        assertTrue(cuponOptional.isPresent(), "El cupón debería estar presente");

        // Obtener el cupón existente y modificarlo
        Cupon cupon = cuponOptional.get();
        cupon.setNombre("Cupon Editado");
        cupon.setDescuento(20);

        // Guardar el cupón modificado
        cuponRepo.save(cupon);

        // Recuperar el cupón modificado para verificar los cambios
        Optional<Cupon> cuponModificado = cuponRepo.findById("1");
        assertTrue(cuponModificado.isPresent(), "El cupón debería estar presente después de la modificación");
        assertEquals("Cupon Editado", cuponModificado.get().getNombre(), "El nombre debería haber sido modificado");
        assertEquals(20, cuponModificado.get().getDescuento(), "El descuento debería haber sido modificado");
    }



    @Test
    public void redimirCuponTest() throws Exception {
        // Supone que hay un cupón con id "1"
        String resultado = cuponServicio.redimirCupon("0001");
        assertEquals("Cupón redimido exitosamente", resultado);
    }

    @Test
    public void obtenerInformacionCuponTest() throws Exception {
        InformacionCuponDTO cupon = cuponServicio.obtenerInformacionCupon("1"); // Supone que hay un cupón con id "1"

        Assertions.assertNotNull(cupon);
        assertEquals("Cupon de Bienvenida", cupon.nombre());
    }

    @Test
    public void listarCuponesTest() throws Exception {
        List<ItemCuponDTO> cupones = cuponServicio.obtenerCupones();

        Assertions.assertFalse(cupones.isEmpty());
    }

    @Test
    public void eliminarCuponTest() throws Exception {
        String resultado = cuponServicio.eliminarCupon("1"); // Supone que hay un cupón con id "1"

        assertEquals("Eliminado", resultado);
    }
}
