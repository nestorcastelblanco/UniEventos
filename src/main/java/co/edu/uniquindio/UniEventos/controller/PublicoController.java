package co.edu.uniquindio.UniEventos.controller;

import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.CambiarPasswordDTO;
import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.CrearCuentaDTO;
import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.LoginDTO;
import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.ValidarCuentaDTO;
import co.edu.uniquindio.UniEventos.dto.EventoDTOs.FiltroEventoDTO;
import co.edu.uniquindio.UniEventos.dto.EventoDTOs.InformacionEventoDTO;
import co.edu.uniquindio.UniEventos.dto.EventoDTOs.ItemEventoDTO;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.MensajeDTO;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.TokenDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;
import co.edu.uniquindio.UniEventos.servicios.interfaces.CuentaServicio;
import co.edu.uniquindio.UniEventos.servicios.interfaces.EmailServicio;
import co.edu.uniquindio.UniEventos.servicios.interfaces.EventoServicio;
import co.edu.uniquindio.UniEventos.servicios.interfaces.OrdenServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/publico")
public class PublicoController {

    private final CuentaServicio cuentaServicio;
    private final EmailServicio emailServicio;
    private final EventoServicio eventoServicio;
    private final OrdenServicio ordenServicio;

    @PostMapping("/iniciar-sesion")
    public ResponseEntity<MensajeDTO<TokenDTO>> iniciarSesion(@Valid @RequestBody LoginDTO loginDTO) throws Exception{
        TokenDTO token = cuentaServicio.iniciarSesion(loginDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, token));
    }

    @PostMapping("/enviar_recuperacion")
    public ResponseEntity<MensajeDTO<String>> enviarCorreoRecuperacion(@RequestBody String emailRecuperacionDTO) {
        try {
            emailServicio.enviarCorreoRecuperacion(emailRecuperacionDTO);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Correo enviado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MensajeDTO<>(true, "Error al enviar el correo: " + e.getMessage()));
        }
    }

    @GetMapping("/evento/listar")
    public ResponseEntity<List<ItemEventoDTO>> listarEventos() {
        try {
            List<ItemEventoDTO> eventos = eventoServicio.listarEventos();
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/evento/informacion/{id}")
    public ResponseEntity<?> obtenerInformacionEvento(@PathVariable String id) {
        try {
            InformacionEventoDTO eventoDTO = eventoServicio.obtenerInformacionEvento(id);
            return ResponseEntity.ok(eventoDTO);
        } catch (Exception e) {

            if (e.getMessage().contains("no existe")) {
                return ResponseEntity.status(404).body(new MensajeDTO<>(true, "Evento no encontrado: " + e.getMessage()));
            } else {

                return ResponseEntity.status(500).body(new MensajeDTO<>(true, "Error al buscar el evento: " + e.getMessage()));
            }
        }
    }
    @GetMapping("/evento/filtrar")
    public ResponseEntity<List<Evento>> filtrarEventos(FiltroEventoDTO filtroEventoDTO) throws Exception {
        List<Evento> eventos = eventoServicio.filtrarEventos(filtroEventoDTO);
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    // Recibir notificaciones de MercadoPago
    @PostMapping("/orden/notificacion-pago")
    public void recibirNotificacionMercadoPago(@RequestBody Map<String, Object> requestBody) {
        ordenServicio.recibirNotificacionMercadoPago(requestBody);
    }

    @PostMapping("/enviar-codigo-recuperacion")
    public String enviarCodigoRecuperacionPassword(@RequestParam String correo) throws Exception {
        return cuentaServicio.enviarCodigoRecuperacionPassword(correo);
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestBody CambiarPasswordDTO cambiarPasswordDTO) throws Exception {
        return cuentaServicio.cambiarPassword(cambiarPasswordDTO);
    }

    @PostMapping("/crear-cuenta")
    public ResponseEntity<MensajeDTO<String>> crearCuenta(@Valid @RequestBody CrearCuentaDTO cuenta) throws Exception{
        cuentaServicio.crearCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta creada exitosamente, se envio un codigo de activación de la cuenta a su correo registrado"));
    }

    @PostMapping("/activar-cuenta")
    public ResponseEntity<MensajeDTO<String>> activarCuenta(@Valid @RequestBody ValidarCuentaDTO cuenta) throws Exception{
        cuentaServicio.activarCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta activada exitosamente"));
    }

}
