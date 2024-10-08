package co.edu.uniquindio.UniEventos;

import co.edu.uniquindio.UniEventos.dto.EventoDTOs.InformacionEventoDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.InformacionOrdenCompraDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.ItemOrdenDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Cuenta;
import co.edu.uniquindio.UniEventos.modelo.documentos.Orden;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCuenta;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoOrden;
import co.edu.uniquindio.UniEventos.modelo.vo.Pago;
import co.edu.uniquindio.UniEventos.repositorios.OrdenRepo;
import co.edu.uniquindio.UniEventos.servicios.implementaciones.OrdenServicioImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.preference.PreferenceItem;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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
    void testRealizarPago() throws Exception {
        // Datos de la orden preexistente
        String idOrden = "60d21b4667d0d8992e610c90";

        // Llamar al método de realizar pago
        Preference preference = ordenServicio.realizarPago(idOrden);

        // Verificar que el código de pasarela se haya generado correctamente
        Orden ordenGuardada = ordenRepo.buscarOrdenPorId(idOrden).orElseThrow(() -> new Exception("Orden no encontrada"));
        assertNotNull(ordenGuardada.getCodigoPasarela(), "El código de pasarela no debe ser nulo");

        // Verificar que el código de pasarela tenga un valor generado por MercadoPago
        assertFalse(ordenGuardada.getCodigoPasarela().isEmpty(), "El código de la pasarela no debe estar vacío");

        // Verificar que los ítems de la pasarela tengan la información correcta
        assertEquals(1, preference.getItems().size(), "Debe haber un ítem en la preferencia");
        PreferenceItem item = preference.getItems().get(0);
        assertEquals("651fb39e67d99177cd75e4a1", item.getId(), "El ID del evento debe coincidir");
        assertEquals("COP", item.getCurrencyId(), "La moneda debe ser COP");
        assertEquals(BigDecimal.valueOf(400000), item.getUnitPrice(), "El precio total debe ser 400000");

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
        // ID que existe en el archivo eventos.json
        String ordenId = "60d21b4667d0d8992e610c90"; // Cambia esto por un ID válido

        InformacionOrdenCompraDTO infoOrden = ordenServicio.obtenerInformacionOrden("60d21b4667d0d8992e610c90");
        assertNotNull(infoOrden, "La información del orden no debe ser nula");
        assertEquals(ordenId, infoOrden.ordenId(), "El ID de la orden debe coincidir");
    }

    @Test
    public void testEliminarOrden() {
        Optional<Orden> ordenOptional = ordenRepo.buscarOrdenPorId("60d21b4667d0d8992e610c90");
        assertTrue(ordenOptional.isPresent(), "La orden debería estar presente");

        Orden orden = ordenOptional.get();
        orden.setEstado(EstadoOrden.CANCELADA);
        ordenRepo.save(orden);

        Optional<Orden> ordenEliminada = ordenRepo.buscarOrdenPorId("60d21b4667d0d8992e610c90");
        assertTrue(ordenEliminada.isPresent(), "La orden debería estar presente después de eliminarla");
        assertEquals(EstadoOrden.CANCELADA, ordenEliminada.get().getEstado(), "El estado de la orden debería ser CANCELADO");
    }

    @Test
    public void testRecibirNotificacionMercadoPago() {
        Map<String, Object> request = new HashMap<>();
        request.put("type", "payment");

        Map<String, Object> data = new HashMap<>();
        data.put("id", "PAY123456"); // Asegúrate de que esto sea un String
        data.put("metadata", new HashMap<String, Object>() {{
            put("id_orden", "60d21b4667d0d8992e610c90"); // ID de la orden
        }});

        request.put("data", data);

        // Invocar el método que estás probando
        ordenServicio.recibirNotificacionMercadoPago(request);

        // Comprobar que el ID de pago se haya establecido correctamente
        Orden orden = ordenRepo.buscarOrdenPorId("60d21b4667d0d8992e610c90").orElseThrow();
        assertEquals("PAY123456", orden.getPago().getIdPago()); // Verifica el valor correcto
    }




}

