package co.edu.uniquindio.UniEventos.controller;

import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.*;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.MensajeDTO;
import co.edu.uniquindio.UniEventos.servicios.interfaces.CarritoServicio;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/carrito")
@SecurityRequirement(name = "bearerAuth")
public class CarritoController {

    private final CarritoServicio carritoServicio;

    @PostMapping("/agregar-item")
    public ResponseEntity<MensajeDTO<String>> agregarItemCarrito(@Valid @RequestBody EventoCarritoDTO eventoCarritoDTO) throws Exception {
        String respuesta = carritoServicio.agregarItemCarrito(eventoCarritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, respuesta));
    }

    @DeleteMapping("/eliminar-item")
    public ResponseEntity<MensajeDTO<String>> eliminarItemCarrito(@Valid @RequestBody EventoEliminarCarritoDTO eventoCarritoDTO) throws Exception {
        String respuesta = carritoServicio.eliminarItemCarrito(eventoCarritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, respuesta));
    }

    @DeleteMapping("/eliminar-carrito/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCarrito(@PathVariable String id) throws Exception {
        carritoServicio.eliminarCarrito(new EliminarCarritoDTO(new ObjectId(id)));
        return ResponseEntity.ok(new MensajeDTO<>(false, "Carrito eliminado exitosamente"));
    }

    @GetMapping("/obtener-informacion/{id}")
    public ResponseEntity<MensajeDTO<VistaCarritoDTO>> obtenerInformacionCarrito(@PathVariable String id) throws Exception {
        VistaCarritoDTO carritoDTO = carritoServicio.obtenerInformacionCarrito(new InformacionCarritoDTO(new ObjectId(id)).idCarrito());
        return ResponseEntity.ok(new MensajeDTO<>(false, carritoDTO));
    }

    @GetMapping("/listar")
    public ResponseEntity<MensajeDTO<List<CarritoListDTO>>> listarCarritos() throws Exception {
        List<CarritoListDTO> carritos = carritoServicio.listarCarritos();
        return ResponseEntity.ok(new MensajeDTO<>(false, carritos));
    }

    @PutMapping("/actualizar-item")
    public ResponseEntity<MensajeDTO<String>> actualizarItemCarrito(@Valid @RequestBody ActualizarItemCarritoDTO actualizarItemCarritoDTO) throws Exception {
        String respuesta = carritoServicio.actualizarItemCarrito(actualizarItemCarritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, respuesta));
    }

    @GetMapping("/calcular-total/{idUsuario}")
    public ResponseEntity<MensajeDTO<Double>> calcularTotalCarrito(@PathVariable String idUsuario) throws Exception {
        double total = carritoServicio.calcularTotalCarrito(new TotalCarritoDTO(new ObjectId(idUsuario)).idUsuario());
        return ResponseEntity.ok(new MensajeDTO<>(false, total));
    }

    @DeleteMapping("/vaciar-carrito/{id}")
    public ResponseEntity<MensajeDTO<String>> vaciarCarrito(@PathVariable String id) throws Exception {
        String respuesta = carritoServicio.vaciarCarrito(new VaciarCarritoDTO(new ObjectId(id)).idCarrito());
        return ResponseEntity.ok(new MensajeDTO<>(false, respuesta));
    }
}
