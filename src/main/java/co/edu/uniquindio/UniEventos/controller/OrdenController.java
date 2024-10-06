package co.edu.uniquindio.UniEventos.controller;

import co.edu.uniquindio.UniEventos.dto.EmailDTOs.EmailDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.CrearOrdenDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.InformacionOrdenCompraDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.ItemOrdenDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.OrdenesUsuarioDTO;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.MensajeDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Orden;
import co.edu.uniquindio.UniEventos.servicios.interfaces.OrdenServicio;
import com.mercadopago.resources.preference.Preference;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/orden")
@SecurityRequirement(name = "bearerAuth")
public class OrdenController {

    private final OrdenServicio ordenServicio;

    // Crear una nueva orden
    @PostMapping("/crear")
    public ResponseEntity<MensajeDTO<String>> crearOrden(@RequestBody CrearOrdenDTO crearOrdenDTO) throws Exception {
        String idOrden = ordenServicio.crearOrden(crearOrdenDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, idOrden));
    }

    // Cancelar una orden existente
    @PostMapping("/cancelar")
    public ResponseEntity<MensajeDTO<String>> cancelarOrden(@RequestParam("idOrden") String idOrden) throws Exception {
        String mensaje = ordenServicio.cancelarOrden(new ObjectId(idOrden));
        return ResponseEntity.ok().body(new MensajeDTO<>(false, mensaje));
    }

    // Obtener todas las órdenes de un usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<MensajeDTO<List<Orden>>> obtenerOrdenesUsuario(@PathVariable String idUsuario) throws Exception {
        List<Orden> ordenes = ordenServicio.ordenesUsuario(new OrdenesUsuarioDTO(new ObjectId(idUsuario)).idCliente());
        return ResponseEntity.ok().body(new MensajeDTO<>(false, ordenes));
    }

    // Obtener historial de órdenes de una cuenta
    @GetMapping("/historial")
    public ResponseEntity<MensajeDTO<List<ItemOrdenDTO>>> obtenerHistorialOrdenes() throws Exception {
        List<ItemOrdenDTO> historial = ordenServicio.obtenerHistorialOrdenes();
        return ResponseEntity.ok().body(new MensajeDTO<>(false, historial));
    }

    // Obtener detalles de una orden específica
    @GetMapping("/detalles/{idOrden}")
    public ResponseEntity<MensajeDTO<InformacionOrdenCompraDTO>> obtenerDetallesOrden(@PathVariable("idOrden") String idOrden) throws Exception {
        InformacionOrdenCompraDTO detallesOrden = ordenServicio.obtenerInformacionOrden(new ObjectId(idOrden));
        return ResponseEntity.ok().body(new MensajeDTO<>(false, detallesOrden));
    }

    // Realizar el pago de una orden
    @PostMapping("/realizar-pago")
    public ResponseEntity<MensajeDTO<Preference>> realizarPago(@RequestParam("idOrden") String idOrden) throws Exception {
        Preference preference = ordenServicio.realizarPago(idOrden);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, preference));
    }

    // Recibir notificaciones de MercadoPago
    @PostMapping("/notificacion-pago")
    public void recibirNotificacionMercadoPago(@RequestBody Map<String, Object> requestBody) {
        ordenServicio.recibirNotificacionMercadoPago(requestBody);
    }

    // Enviar código QR por correo
    @PostMapping("/enviar-qr")
    public ResponseEntity<MensajeDTO<String>> enviarCorreoQR(@RequestParam("idOrden") String idOrden, @RequestParam("email") String email) throws Exception {
        ordenServicio.enviarCorreoOrden(new ObjectId(idOrden), email);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Correo enviado con éxito."));
    }
}
