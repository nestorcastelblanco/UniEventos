package co.edu.uniquindio.UniEventos;

import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.InformacionOrdenCompraDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.ItemOrdenDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Orden;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoOrden;
import co.edu.uniquindio.UniEventos.repositorios.OrdenRepo;
import co.edu.uniquindio.UniEventos.servicios.implementaciones.OrdenServicioImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mercadopago.resources.preference.Preference;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class OrdenTest {

    @Autowired
    private OrdenServicioImpl ordenServicio;

    @Autowired
    private OrdenRepo ordenRepo;

    @BeforeEach
    public void setUp() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Cargar el archivo JSON desde el directorio de recursos
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ordenes.json");
        Assertions.assertNotNull(inputStream, "Archivo ordenes.json no encontrado");

        List<Orden> ordenes = objectMapper.readValue(inputStream, new TypeReference<List<Orden>>() {});

        // Limpiar el repositorio antes de agregar nuevos registros
        ordenRepo.deleteAll();
        ordenRepo.saveAll(ordenes);
    }

    @Test
    public void testRealizarPago() throws Exception {
        // ID de la orden existente en la base de datos de prueba
        String idOrden = "60d21b4667d0d8992e610c90"; // Asegúrate de que este ID existe en tu base de datos de prueba.

        // Llama al método a probar
        Preference resultado = ordenServicio.realizarPago(idOrden);

        // Verifica que el resultado no sea nulo
        assertNotNull(resultado, "La preferencia no debe ser nula");

        // Verifica que el ID de la preferencia devuelto no sea nulo o vacío
        assertNotNull(resultado.getId(), "El ID de la preferencia no debe ser nulo");

        // Verifica que el ID de la preferencia sea igual al esperado (puedes agregar lógica adicional aquí)
        assertEquals("expectedPreferenceId", resultado.getId()); // Reemplaza con el ID esperado si es necesario
    }

    @Test
    public void testObtenerOrdenPorIdUsuario() throws Exception {
        // Supone que hay órdenes para un usuario con un ObjectId específico
        ObjectId usuarioId = new ObjectId("60d21b4667d0d8992e610c85"); // Reemplaza con un ObjectId válido
        List<Orden> ordenes = ordenServicio.ordenesUsuario(usuarioId);
        assertNotNull(ordenes);
        Assertions.assertFalse(ordenes.isEmpty(), "No se encontraron órdenes para el usuario");
    }

    @Test
    public void testObtenerHistorialOrdenes() throws Exception {
        List<ItemOrdenDTO> historial = ordenServicio.obtenerHistorialOrdenes();
        assertNotNull(historial);
        Assertions.assertFalse(historial.isEmpty(), "El historial de órdenes no debe estar vacío");
    }

    @Test
    public void testObtenerInformacionOrden() throws Exception {
        // ID de la orden existente en la base de datos de prueba
        String idOrden = "60d21b4667d0d8992e610c90"; // Asegúrate de que este ID existe en tu base de datos de prueba.

        // Llama al método a probar
        InformacionOrdenCompraDTO orden = ordenServicio.obtenerInformacionOrden(idOrden);

        // Verifica que la orden no sea nula
        assertNotNull(orden, "La orden no debe ser nula");

        // Verifica que los campos específicos de la orden sean correctos
        assertEquals("expectedClientId", orden.idCliente(), "El ID del cliente no coincide"); // Reemplaza con el ID esperado
        assertEquals("expectedPaymentCode", orden.codigoPasarela(), "El código de pasarela no coincide"); // Reemplaza con el valor esperado
        assertEquals("expectedTotal", orden.total(), "El total no coincide"); // Reemplaza con el total esperado

    }

    @Test
    public void testCancelarOrden() throws Exception {
        // Supone que hay una orden con un ObjectId específico en la base de datos de prueba
        String ordenId = "60d21b4667d0d8992e610c90"; // Reemplaza con un ObjectId válido

        // Llama al método a probar
        String resultado = ordenServicio.cancelarOrden(ordenId);

        // Verifica que el mensaje de retorno sea el esperado
        assertEquals("Orden cancelada", resultado);

        // Verifica que la orden se haya actualizado correctamente
        Optional<Orden> ordenCancelada = ordenRepo.buscarOrdenPorId(ordenId);
        assertNotNull(ordenCancelada, "La orden cancelada no debe ser nula");
        assertEquals(EstadoOrden.CANCELADA, ordenCancelada.get().getEstado(), "El estado de la orden no es el esperado");
    }

}

