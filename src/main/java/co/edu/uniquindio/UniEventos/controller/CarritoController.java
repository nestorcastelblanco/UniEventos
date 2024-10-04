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

    @PostMapping("/crear-carrito")
    public ResponseEntity<MensajeDTO<String>> crearCarrito(@Valid @RequestBody CrearCarritoDTO carritoDTO) throws Exception {
        String respuesta = carritoServicio.crearCarrito(carritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, respuesta));
    }

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

    @DeleteMapping("/eliminar-carrito")
    public ResponseEntity<MensajeDTO<String>> eliminarCarrito(@Valid @RequestBody EliminarCarritoDTO eliminarCarritoDTO) throws Exception {
        carritoServicio.eliminarCarrito(eliminarCarritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Carrito eliminado exitosamente"));
    }

    @GetMapping("/obtener-informacion/{id}")
    public ResponseEntity<MensajeDTO<VistaCarritoDTO>> obtenerInformacionCarrito(@PathVariable ObjectId id) throws Exception {
        VistaCarritoDTO carritoDTO = carritoServicio.obtenerInformacionCarrito(new VistaCarritoDTO(id, null, null)); // Se deben obtener los detallesCarrito y fecha desde el servicio
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

    @GetMapping("/calcular-total/{idCliente}")
    public ResponseEntity<MensajeDTO<Double>> calcularTotalCarrito(@PathVariable String idCliente) throws Exception {
        double total = carritoServicio.calcularTotalCarrito(idCliente);
        return ResponseEntity.ok(new MensajeDTO<>(false, total));
    }

    @DeleteMapping("/vaciar-carrito/{id}")
    public ResponseEntity<MensajeDTO<String>> vaciarCarrito(@PathVariable String id) throws Exception {
        String respuesta = carritoServicio.vaciarCarrito(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, respuesta));
    }
}
