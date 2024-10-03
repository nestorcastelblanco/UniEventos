package co.edu.uniquindio.UniEventos.controller;

import co.edu.uniquindio.UniEventos.dto.EventoDTOs.*;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.MensajeDTO;
import co.edu.uniquindio.UniEventos.servicios.interfaces.EventoServicio;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/evento")
@SecurityRequirement(name = "bearerAuth")
public class EventoController {

    private final EventoServicio eventoServicio;

    @PostMapping("/crear")
    public ResponseEntity<MensajeDTO<String>> crearEvento(@RequestBody CrearEventoDTO crearEventoDTO) {
        try {
            String idEvento = eventoServicio.crearEvento(crearEventoDTO);
            return ResponseEntity.ok(new MensajeDTO<>(false, idEvento));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MensajeDTO<>(true, "Error al crear el evento: " + e.getMessage()));
        }
    }

    @PutMapping("/editar")
    public ResponseEntity<MensajeDTO<String>> editarEvento(@RequestBody EditarEventoDTO editarEventoDTO) {
        try {
            String idEvento = eventoServicio.editarEvento(editarEventoDTO);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Evento editado con éxito, ID: " + idEvento));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MensajeDTO<>(true, "Error al editar el evento: " + e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarEvento(@PathVariable String id) {
        try {
            String resultado = eventoServicio.eliminarEvento(id);
            return ResponseEntity.ok(new MensajeDTO<>(false, resultado));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MensajeDTO<>(true, "Error al eliminar el evento: " + e.getMessage()));
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ItemEventoDTO>> listarEventos() {
        try {
            List<ItemEventoDTO> eventos = eventoServicio.listarEventos();
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/informacion/{id}")
    public ResponseEntity<InformacionEventoDTO> obtenerInformacionEvento(@PathVariable String id) {
        try {
            InformacionEventoDTO eventoDTO = eventoServicio.obtenerInformacionEvento(id);
            return ResponseEntity.ok(eventoDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/filtrar")
    public ResponseEntity<List<ItemEventoDTO>> filtrarEventos(@RequestBody FiltroEventoDTO filtroEventoDTO) {
        try {
            List<ItemEventoDTO> eventosFiltrados = eventoServicio.filtrarEventos(filtroEventoDTO);
            return ResponseEntity.ok(eventosFiltrados);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
