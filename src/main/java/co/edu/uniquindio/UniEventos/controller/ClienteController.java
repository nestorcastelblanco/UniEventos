package co.edu.uniquindio.UniEventos.controller;

import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.*;
import co.edu.uniquindio.UniEventos.dto.CuponDTOs.InformacionCuponDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.CrearOrdenDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.InformacionOrdenCompraDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.ItemOrdenDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.OrdenesUsuarioDTO;
import co.edu.uniquindio.UniEventos.dto.ResenaDTO;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.MensajeDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Orden;
import co.edu.uniquindio.UniEventos.servicios.interfaces.CarritoServicio;
import co.edu.uniquindio.UniEventos.servicios.interfaces.CuponServicio;
import co.edu.uniquindio.UniEventos.servicios.interfaces.OrdenServicio;
import co.edu.uniquindio.UniEventos.servicios.interfaces.ResenaServicio;
import com.google.longrunning.OperationInfoOrBuilder;
import com.mercadopago.resources.preference.Preference;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cliente")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final CuponServicio cuponServicio;
    private final CarritoServicio carritoServicio;
    private final OrdenServicio ordenServicio;
    private final ResenaServicio resenaServicio;

    // Crear una nueva orden
    @PostMapping("/orden/crear")
    public ResponseEntity<MensajeDTO<String>> crearOrden(@RequestBody CrearOrdenDTO crearOrdenDTO) throws Exception {
        String idOrden = ordenServicio.crearOrden(crearOrdenDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, idOrden));
    }

//    // Crear una reseña para un evento
//    @PostMapping("/reseña/crear")
//    public ResponseEntity<MensajeDTO<String>> crearReseña(@RequestBody ResenaDTO resenaDTO) throws Exception {
//        String idResena = resenaServicio.crearReseña(resenaDTO);
//        return ResponseEntity.ok().body(new MensajeDTO<>(false, idResena));
//    }

    // Cancelar una orden existente
    @PostMapping("/orden/cancelar")
    public ResponseEntity<MensajeDTO<String>> cancelarOrden(@RequestParam("idOrden") String idOrden) throws Exception {
        String mensaje = ordenServicio.cancelarOrden(idOrden);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, mensaje));
    }

    // Obtener todas las órdenes de un usuario
    @GetMapping("/orden/usuario/{idUsuario}")
    public ResponseEntity<MensajeDTO<List<Orden>>> obtenerOrdenesUsuario(@PathVariable String idUsuario) throws Exception {
        List<Orden> ordenes = ordenServicio.ordenesUsuario(new OrdenesUsuarioDTO(new ObjectId(idUsuario)).idCliente());
        return ResponseEntity.ok().body(new MensajeDTO<>(false, ordenes));
    }

    // Obtener historial de órdenes de una cuenta
    @GetMapping("/orden/historial")
    public ResponseEntity<MensajeDTO<List<ItemOrdenDTO>>> obtenerHistorialOrdenes() throws Exception {
        List<ItemOrdenDTO> historial = ordenServicio.obtenerHistorialOrdenes();
        return ResponseEntity.ok().body(new MensajeDTO<>(false, historial));
    }

    // Obtener detalles de una orden específica
    @GetMapping("/orden/detalles/{idOrden}")
    public ResponseEntity<MensajeDTO<InformacionOrdenCompraDTO>> obtenerDetallesOrden(@PathVariable("idOrden") String idOrden) throws Exception {
        InformacionOrdenCompraDTO detallesOrden = ordenServicio.obtenerInformacionOrden(idOrden);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, detallesOrden));
    }

    // Realizar el pago de una orden
    @PostMapping("/orden/realizar-pago")
    public ResponseEntity<MensajeDTO<Preference>> realizarPago(@RequestParam("idOrden") String idOrden) throws Exception {
        Preference preference = ordenServicio.realizarPago(idOrden);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, preference));
    }

    @PostMapping("/carrito/agregar-item")
    public ResponseEntity<MensajeDTO<String>> agregarItemCarrito(@Valid @RequestBody EventoCarritoDTO eventoCarritoDTO) throws Exception {
        String respuesta = carritoServicio.agregarItemCarrito(eventoCarritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, respuesta));
    }

    @DeleteMapping("/carrito/eliminar-item/{idDetalle}/{idCarrito}")
    public ResponseEntity<MensajeDTO<String>> eliminarItemCarrito(
            @PathVariable String idDetalle,
            @PathVariable String idCarrito) throws Exception {
        String respuesta = carritoServicio.eliminarItemCarrito(idDetalle, idCarrito);
        return ResponseEntity.ok(new MensajeDTO<>(false, respuesta));
    }

    @DeleteMapping("/carrito/eliminar-carrito/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCarrito(@PathVariable String id) throws Exception {
        carritoServicio.eliminarCarrito(new EliminarCarritoDTO(new ObjectId(id)));
        return ResponseEntity.ok(new MensajeDTO<>(false, "Carrito eliminado exitosamente"));
    }

    @GetMapping("/carrito/obtener-informacion/{id}")
    public ResponseEntity<MensajeDTO<VistaCarritoDTO>> obtenerInformacionCarrito(@PathVariable String id) throws Exception {
        VistaCarritoDTO carritoDTO = carritoServicio.obtenerInformacionCarrito(new InformacionCarritoDTO(new ObjectId(id)).idCarrito());
        return ResponseEntity.ok(new MensajeDTO<>(false, carritoDTO));
    }

    @GetMapping("/carrito/listar")
    public ResponseEntity<MensajeDTO<List<CarritoListDTO>>> listarCarritos() throws Exception {
        List<CarritoListDTO> carritos = carritoServicio.listarCarritos();
        return ResponseEntity.ok(new MensajeDTO<>(false, carritos));
    }

    @PutMapping("/carrito/actualizar-item")
    public ResponseEntity<MensajeDTO<String>> actualizarItemCarrito(@Valid @RequestBody ActualizarItemCarritoDTO actualizarItemCarritoDTO) throws Exception {
        String respuesta = carritoServicio.actualizarItemCarrito(actualizarItemCarritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, respuesta));
    }

    @GetMapping("/carrito/calcular-total/{idUsuario}")
    public ResponseEntity<MensajeDTO<Double>> calcularTotalCarrito(@PathVariable String idUsuario) throws Exception {
        double total = carritoServicio.calcularTotalCarrito(new TotalCarritoDTO(new ObjectId(idUsuario)).idUsuario());
        return ResponseEntity.ok(new MensajeDTO<>(false, total));
    }

    @DeleteMapping("/carrito/vaciar-carrito/{id}")
    public ResponseEntity<MensajeDTO<String>> vaciarCarrito(@PathVariable String id) throws Exception {
        String respuesta = carritoServicio.vaciarCarrito(new VaciarCarritoDTO(new ObjectId(id)).idCarrito());
        return ResponseEntity.ok(new MensajeDTO<>(false, respuesta));
    }

    @GetMapping("/cupon/obtener/{codigo}")
    public ResponseEntity<MensajeDTO<InformacionCuponDTO>> obtenerCupon(@PathVariable String codigo) throws Exception {
        InformacionCuponDTO cupon = cuponServicio.obtenerInformacionCupon(codigo);
        return ResponseEntity.ok(new MensajeDTO<>(false, cupon));
    }

}
