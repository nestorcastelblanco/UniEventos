package co.edu.uniquindio.UniEventos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import co.edu.uniquindio.UniEventos.dto.ReseñaDTO;
import co.edu.uniquindio.UniEventos.modelo.vo.Reseña;
import co.edu.uniquindio.UniEventos.repositorios.ReseñaRepo;
import co.edu.uniquindio.UniEventos.servicios.interfaces.ReseñaServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReseñaTest {

    @Autowired
    private ReseñaServicio reseñaServicio; // Inyección de la interfaz

    @Autowired
    private ReseñaRepo reseñaRepo;

    @BeforeEach
    public void setUp() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Cargar el archivo JSON desde el directorio de recursos
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("reseñas.json");
        assertNotNull(inputStream, "Archivo reseñas.json no encontrado");

        List<Reseña> reseñas = objectMapper.readValue(inputStream, new TypeReference<List<Reseña>>() {});

        // Limpiar el repositorio antes de agregar nuevos registros
        reseñaRepo.deleteAll();
        reseñaRepo.saveAll(reseñas);
    }

    @Test
    public void testObtenerReseñasPorEvento() throws Exception {
        String idEvento = "651fb39e67d99177cd75e4a1"; // Asegúrate de que haya reseñas para este evento
        List<ReseñaDTO> reseñas = reseñaServicio.obtenerReseñasPorEvento(idEvento);
        assertNotNull(reseñas);
        assertFalse(reseñas.isEmpty(), "No se encontraron reseñas para el evento");
    }

    @Test
    public void testObtenerReseñasPorUsuario() throws Exception {
        String idUsuario = "60d21b4667d0d8992e610c85"; // Asegúrate de que haya reseñas para este usuario
        List<ReseñaDTO> reseñas = reseñaServicio.obtenerReseñasPorUsuario(idUsuario);
        assertNotNull(reseñas);
        assertFalse(reseñas.isEmpty(), "No se encontraron reseñas para el usuario");
    }
}
