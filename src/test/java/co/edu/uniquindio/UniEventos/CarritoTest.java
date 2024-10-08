package co.edu.uniquindio.UniEventos;

import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.*;
import co.edu.uniquindio.UniEventos.modelo.documentos.Carrito;
import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;
import co.edu.uniquindio.UniEventos.modelo.vo.DetalleCarrito;
import co.edu.uniquindio.UniEventos.repositorios.CarritoRepo;
import co.edu.uniquindio.UniEventos.repositorios.EventoRepo;
import co.edu.uniquindio.UniEventos.servicios.implementaciones.CarritoServicioImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CarritoTest {

    @Autowired
    private CarritoServicioImpl carritoServicio;

    @Autowired
    private EventoRepo eventoRepo;

    @Autowired
    private CarritoRepo carritoRepo;

    @BeforeEach
    public void setUp() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Cargar el archivo JSON desde el directorio de recursos
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("carritos.json");
        Assertions.assertNotNull(inputStream, "Archivo carritos.json no encontrado");

        List<Carrito> carritos = objectMapper.readValue(inputStream, new TypeReference<List<Carrito>>() {});

        // Limpiar el repositorio antes de agregar nuevos registros
        carritoRepo.deleteAll();
        carritoRepo.saveAll(carritos);
    }

    @Test
    void testAgregarItemCarrito() throws Exception {
        // Crear un DTO con datos válidos
        EventoCarritoDTO eventoCarritoDTO = new EventoCarritoDTO(
                new ObjectId("651f13fc3e89d7601f4b9b91"), // ID del evento
                new ObjectId("60d21b4667d0d8992e610c85"), // ID del usuario
                "Concierto de Rock", // Nombre del evento
                LocalDateTime.now().plusDays(5), // Fecha futura del evento
                2, // Número de boletas
                "VIP" // Nombre de la localidad
        );

        // Ejecutar el método de servicio para agregar el item
        String result = carritoServicio.agregarItemCarrito(eventoCarritoDTO);

        // Validar que la operación fue exitosa
        assertEquals("Item agregado al carrito correctamente nombre localidad : VIP", result);
    }


    @Test
    void testEliminarItemCarrito() throws Exception {
        // Crear un DTO para eliminar un ítem del carrito
        EventoEliminarCarritoDTO eventoEliminarCarritoDTO = new EventoEliminarCarritoDTO(
                new ObjectId("60d21b4667d0d8992e610c92"), // ID del carrito
                new ObjectId("60d21b4667d0d8992e610c91")  // ID del detalle del carrito
        );

        // Ejecutar el método de servicio para eliminar el item
        String result = carritoServicio.eliminarItemCarrito(eventoEliminarCarritoDTO);

        // Validar que la operación fue exitosa
        assertEquals("Elemento eliminado del carrito", result);
    }


    @Test
    public void testObtenerInformacionCarrito() throws Exception {
        VistaCarritoDTO carrito = carritoServicio.obtenerInformacionCarrito(new ObjectId("60d21b4667d0d8992e610c91"));
        assertNotNull(carrito);
        assertEquals(new ObjectId("60d21b4667d0d8992e610c91") , carrito.id_carrito());
    }

    @Test
    void testCalcularTotalCarrito() throws Exception {
        // Simular el ID del cliente
        ObjectId idCliente = new ObjectId("60d21b4667d0d8992e610c85");

        // Asegurarse de que el cliente tenga un carrito
        Carrito carrito = carritoRepo.buscarCarritoPorIdCliente(idCliente).orElseThrow(() -> new Exception("El carrito no existe"));

        // Verificar que las localidades del evento existan antes de calcular el total
        for (DetalleCarrito item : carrito.getItems()) {
            Evento evento = eventoRepo.buscarPorIdEvento(item.getIdEvento()).orElseThrow(() -> new Exception("El evento no existe"));
            assertTrue(evento.getLocalidades().stream().anyMatch(localidad -> localidad.getNombreLocalidad().equals(item.getNombreLocalidad())));
        }

        // Calcular el total
        double total = carritoServicio.calcularTotalCarrito(idCliente);

        // Validar que el total calculado sea correcto
        assertTrue(total > 0);
    }


    @Test
    public void testVaciarCarrito() throws Exception {
        String resultado = carritoServicio.vaciarCarrito(new ObjectId("60d21b4667d0d8992e610c91"));
        assertEquals("Carrito vaciado exitosamente", resultado);
    }

    @Test
    public void testEliminarCarrito() throws Exception {
        EliminarCarritoDTO dto = new EliminarCarritoDTO(new ObjectId("60d21b4667d0d8992e610c93"));
        carritoServicio.eliminarCarrito(dto);
        assertFalse(carritoRepo.existsById(new ObjectId("60d21b4667d0d8992e610c93").toString()));
    }

    @Test
    public void testActualizarItemCarrito() throws Exception {
        ActualizarItemCarritoDTO dto = new ActualizarItemCarritoDTO(new ObjectId("60d21b4667d0d8992e610c86"), new ObjectId("651fb39e67d99177cd75e4a2"), 5);
        String resultado = carritoServicio.actualizarItemCarrito(dto);
        assertEquals("Item actualizado exitosamente", resultado);
    }
}
