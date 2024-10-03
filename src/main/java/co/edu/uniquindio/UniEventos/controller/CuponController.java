package co.edu.uniquindio.UniEventos.controller;

import co.edu.uniquindio.UniEventos.dto.CuponDTOs.*;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.MensajeDTO;
import co.edu.uniquindio.UniEventos.servicios.interfaces.CuponServicio;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/cupon")
@SecurityRequirement(name = "bearerAuth")
public class CuponController {

    private final CuponServicio cuponServicio;

    @PostMapping("/crear")
    public ResponseEntity<MensajeDTO<String>> crearCupon(@Valid @RequestBody CrearCuponDTO cuponDTO) throws Exception {
        String idCupon = cuponServicio.crearCupon(cuponDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupón creado con ID: " + idCupon));
    }

    @PutMapping("/editar")
    public ResponseEntity<MensajeDTO<String>> editarCupon(@Valid @RequestBody EditarCuponDTO cuponDTO) throws Exception {
        cuponServicio.editarCupon(cuponDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupón editado exitosamente"));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCupon(@PathVariable String id) throws Exception {
        cuponServicio.eliminarCupon(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupón eliminado"));
    }

    @GetMapping("/obtener-informacion/{id}")
    public ResponseEntity<MensajeDTO<InformacionCuponDTO>> obtenerInformacionCupon(@PathVariable String id) throws Exception {
        InformacionCuponDTO cuponInfo = cuponServicio.obtenerInformacionCupon(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, cuponInfo));
    }

    @GetMapping("/listar")
    public ResponseEntity<MensajeDTO<List<ItemCuponDTO>>> obtenerCupones() throws Exception {
        List<ItemCuponDTO> cupones = cuponServicio.obtenerCupones();
        return ResponseEntity.ok(new MensajeDTO<>(false, cupones));
    }

    @PostMapping("/redimir")
    public ResponseEntity<MensajeDTO<String>> redimirCupon(@RequestParam String codigo) throws Exception {
        String resultado = cuponServicio.redimirCupon(codigo);
        return ResponseEntity.ok(new MensajeDTO<>(false, resultado));
    }
}
