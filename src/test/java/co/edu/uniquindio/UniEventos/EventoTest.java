package co.edu.uniquindio.UniEventos;

import co.edu.uniquindio.UniEventos.dto.EventoDTOs.*;
import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoEvento;
import co.edu.uniquindio.UniEventos.repositorios.EventoRepo;
import co.edu.uniquindio.UniEventos.servicios.implementaciones.EventoServicioImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EventoTest {

    @Autowired
    private EventoServicioImpl eventoServicio;

    @Autowired
    private EventoRepo eventoRepo;

    @BeforeEach
    public void setUp() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Cargar el archivo JSON desde el directorio de recursos
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("eventos.json");
        assertNotNull(inputStream, "Archivo eventos.json no encontrado");

        List<Evento> eventos = objectMapper.readValue(inputStream, new TypeReference<List<Evento>>() {});

        // Limpiar el repositorio antes de agregar nuevos registros
        eventoRepo.deleteAll();
        eventoRepo.saveAll(eventos);
    }

    @Test
    public void testListarEventos() throws Exception {
        List<EventoDTO> eventos = eventoServicio.listarEventos();
        Assertions.assertFalse(eventos.isEmpty());
    }

    @Test
    public void testObtenerInformacionEvento() throws Exception {
        // ID que existe en el archivo eventos.json
        String eventoId = "651fb39e67d99177cd75e4a1"; // Cambia esto por un ID válido

        InformacionEventoDTO infoEvento = eventoServicio.obtenerInformacionEvento(eventoId);
        assertNotNull(infoEvento, "La información del evento no debe ser nula");
        assertEquals(eventoId, infoEvento.id(), "El ID del evento debe coincidir");
    }

    @Test
    public void testEditarEvento() throws Exception {
        // ID que existe en el archivo eventos.json
        String eventoId = "651fb39e67d99177cd75e4a1"; // Cambia esto por un ID válido

        // Obtener el evento para editar
        Evento evento = eventoRepo.findById(eventoId).get();

        // Preparar el DTO de edición
        EditarEventoDTO editarEventoDTO = new EditarEventoDTO(
                eventoId,
                "Concierto de Rock Modificado",
                evento.getDescripcion(),
                evento.getDireccion(),
                evento.getCiudad(),
                evento.getFecha(),
                evento.getTipo(),
                evento.getImagenPortada(),
                evento.getImagenLocalidades(),
                evento.getLocalidades()
        );

        String idEventoEditado = eventoServicio.editarEvento(editarEventoDTO);
        assertEquals(eventoId, idEventoEditado, "El evento editado debe tener el mismo ID");

        // Verificar que el evento fue editado correctamente
        InformacionEventoDTO infoEventoModificado = eventoServicio.obtenerInformacionEvento(eventoId);
        assertEquals("Concierto de Rock Modificado", infoEventoModificado.nombre(), "El nombre del evento debe haber sido modificado");
    }

    @Test
    public void testFiltrarEventos() throws Exception {
        FiltroEventoDTO filtroEventoDTO = new FiltroEventoDTO("Concierto de Rock", "Bogotá", "CONCIERTO", null, null);
        List<Evento> eventosFiltrados = eventoServicio.filtrarEventos(filtroEventoDTO);
        Assertions.assertFalse(eventosFiltrados.isEmpty());
    }

    @Test
    public void testEliminarEvento() throws Exception {
        // Busca el evento que se ha cargado
        Evento eventoGuardado = eventoRepo.findById("651fb39e67d99177cd75e4a1").orElseThrow();

        // Verifica que el evento se haya guardado correctamente
        assertEquals(EstadoEvento.ACTIVO, eventoGuardado.getEstado());

        // Ahora intenta eliminar el evento
        String resultado = eventoServicio.eliminarEvento(eventoGuardado.getId().toString());

        // Verifica que el resultado sea el esperado
        assertEquals("Eliminado", resultado);

        // Asegúrate de que el evento esté inactivo después de la eliminación
        Evento eventoInactivo = eventoRepo.findById(eventoGuardado.getId()).orElseThrow();
        assertEquals(EstadoEvento.INACTIVO, eventoInactivo.getEstado());
    }
}
