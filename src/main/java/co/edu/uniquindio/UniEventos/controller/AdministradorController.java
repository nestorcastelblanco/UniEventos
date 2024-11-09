package co.edu.uniquindio.UniEventos.controller;

import co.edu.uniquindio.UniEventos.dto.CuponDTOs.CrearCuponDTO;
import co.edu.uniquindio.UniEventos.dto.CuponDTOs.EditarCuponDTO;
import co.edu.uniquindio.UniEventos.dto.CuponDTOs.InformacionCuponDTO;
import co.edu.uniquindio.UniEventos.dto.CuponDTOs.ItemCuponDTO;
import co.edu.uniquindio.UniEventos.dto.EventoDTOs.CrearEventoDTO;
import co.edu.uniquindio.UniEventos.dto.EventoDTOs.EditarEventoDTO;
import co.edu.uniquindio.UniEventos.dto.EventoDTOs.EventoDTO;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.MensajeDTO;
import co.edu.uniquindio.UniEventos.servicios.interfaces.CuponServicio;
import co.edu.uniquindio.UniEventos.servicios.interfaces.EventoServicio;
import co.edu.uniquindio.UniEventos.servicios.interfaces.ImagenesServicio;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
public class AdministradorController {

    private final CuponServicio cuponServicio;
    private final EventoServicio eventoServicio;
    private final ImagenesServicio imagenesServicio;

    @PostMapping("/imagen/subir")
    public ResponseEntity<MensajeDTO<String>> subir(@RequestParam("imagen") MultipartFile imagen) throws Exception {
        String respuesta = imagenesServicio.subirImagen(imagen);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, respuesta));
    }


    @DeleteMapping("/imagen/eliminar")
    public ResponseEntity<MensajeDTO<String>> eliminar(@RequestParam("idImagen") String idImagen)  throws Exception{
        imagenesServicio.eliminarImagen( idImagen );
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "La imagen fue eliminada correctamente"));
    }

    @PostMapping("/evento/crear")
    public ResponseEntity<MensajeDTO<String>> crearEvento(@RequestBody CrearEventoDTO crearEventoDTO) {
        try {
            String idEvento = eventoServicio.crearEvento(crearEventoDTO);
            return ResponseEntity.ok(new MensajeDTO<>(false, idEvento));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MensajeDTO<>(true, "Error al crear el evento: " + e.getMessage()));
        }
    }

    @PutMapping("/evento/editar")
    public ResponseEntity<MensajeDTO<String>> editarEvento(@RequestBody EditarEventoDTO editarEventoDTO) {
        try {
            String idEvento = eventoServicio.editarEvento(editarEventoDTO);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Evento editado con éxito, ID: " + idEvento));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MensajeDTO<>(true, "Error al editar el evento: " + e.getMessage()));
        }
    }

    @DeleteMapping("/evento/eliminar/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarEvento(@PathVariable String id) {
        try {
            String resultado = eventoServicio.eliminarEvento(id);
            return ResponseEntity.ok(new MensajeDTO<>(false, resultado));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MensajeDTO<>(true, "Error al eliminar el evento: " + e.getMessage()));
        }
    }

    @GetMapping("/evento/listar")
    public ResponseEntity<MensajeDTO<List<EventoDTO>>> listarEventos() {
        try {
            List<EventoDTO> eventos = eventoServicio.listarEventos();
            System.out.println(eventos);
            System.out.println("LLego eventos");
            return ResponseEntity.ok(new MensajeDTO<>(false, eventos));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/evento/tipos")
    public ResponseEntity<MensajeDTO<List<String>>> obtenerTiposEvento() throws Exception {
        List<String> tiposEvento = eventoServicio.listarTiposEvento();
        return ResponseEntity.ok(new MensajeDTO<>(false, tiposEvento));
    }

    @PostMapping("/cupon/crear")
    public ResponseEntity<MensajeDTO<String>> crearCupon(@Valid @RequestBody CrearCuponDTO cuponDTO) throws Exception {
        String idCupon = cuponServicio.crearCupon(cuponDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupón creado con ID: " + idCupon));
    }

    @PutMapping("/cupon/editar")
    public ResponseEntity<MensajeDTO<String>> editarCupon(@Valid @RequestBody EditarCuponDTO cuponDTO) throws Exception {
        cuponServicio.editarCupon(cuponDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupón editado exitosamente"));
    }

    @DeleteMapping("/cupon/eliminar/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCupon(@PathVariable String id) throws Exception {
        cuponServicio.eliminarCupon(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupón eliminado"));
    }

    @GetMapping("/cupon/obtener-informacion/{id}")
    public ResponseEntity<MensajeDTO<InformacionCuponDTO>> obtenerInformacionCupon(@PathVariable String id) throws Exception {
        InformacionCuponDTO cuponInfo = cuponServicio.obtenerInformacionCupon(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, cuponInfo));
    }

    @GetMapping("/cupon/listar")
    public ResponseEntity<MensajeDTO<List<ItemCuponDTO>>> obtenerCupones() throws Exception {
        List<ItemCuponDTO> cupones = cuponServicio.obtenerCupones();
        return ResponseEntity.ok(new MensajeDTO<>(false, cupones));
    }
    @GetMapping("/cupon/tipos")
    public ResponseEntity<MensajeDTO<List<String>>> obtenerTiposCupon() throws Exception {
        List<String> tiposCupon = cuponServicio.listarTiposCupon();
        return ResponseEntity.ok(new MensajeDTO<>(false, tiposCupon));
    }

    @GetMapping("/cupon/estados")
    public ResponseEntity<MensajeDTO<List<String>>> obtenerEstadosCupon() throws Exception {
        List<String> estadosCupon = cuponServicio.listarEstadosCupon();
        return ResponseEntity.ok(new MensajeDTO<>(false, estadosCupon));
    }

}
