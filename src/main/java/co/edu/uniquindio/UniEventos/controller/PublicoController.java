package co.edu.uniquindio.UniEventos.controller;

import co.edu.uniquindio.UniEventos.dto.CuentaDTOs.*;
import co.edu.uniquindio.UniEventos.dto.CuponDTOs.InformacionCuponDTO;
import co.edu.uniquindio.UniEventos.dto.EventoDTOs.EventoDTO;
import co.edu.uniquindio.UniEventos.dto.EventoDTOs.FiltroEventoDTO;
import co.edu.uniquindio.UniEventos.dto.EventoDTOs.InformacionEventoDTO;
import co.edu.uniquindio.UniEventos.dto.EventoDTOs.ItemEventoDTO;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.MensajeDTO;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.TokenDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;
import co.edu.uniquindio.UniEventos.servicios.interfaces.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
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
    private final CuponServicio cuponServicio;

    @PostMapping("/iniciar-sesion")
    public ResponseEntity<MensajeDTO<TokenDTO>> iniciarSesion(@Valid @RequestBody LoginDTO loginDTO) throws Exception{
        TokenDTO token = cuentaServicio.iniciarSesion(loginDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<MensajeDTO<TokenDTO>> refresh(@RequestBody TokenDTO tokenDTO) throws Exception {
        TokenDTO nuevoToken = cuentaServicio.refreshToken(tokenDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, nuevoToken));
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

    @GetMapping("/evento/informacion/{id}")
    public ResponseEntity<InformacionEventoDTO> obtenerInformacionEvento(@PathVariable String id) throws Exception {
        InformacionEventoDTO eventos = eventoServicio.obtenerInformacionEvento(id);
        return new ResponseEntity<>(eventos, HttpStatus.OK);
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
    public ResponseEntity<MensajeDTO<String>> enviarCodigoRecuperacionPassword(@Valid @RequestBody EnviarCodigoDTO enviarCodigoDTO) throws Exception {
        cuentaServicio.enviarCodigoRecuperacionPassword(enviarCodigoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Se ha enviado un código de Verificación a su correo, con una duración de 15 minutos"));
    }


    @PostMapping("/cambiar-password")
    public ResponseEntity<MensajeDTO<String>> cambiarPassword(@Valid @RequestBody CambiarPasswordDTO cambiarPasswordDTO) throws Exception {
        cuentaServicio.cambiarPassword(cambiarPasswordDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "La contraseña se ha cambiado correctamente"));
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

    @GetMapping("/cupon/obtener-informacion/{id}")
    public ResponseEntity<MensajeDTO<InformacionCuponDTO>> obtenerInformacionCupon(@PathVariable String id) throws Exception {
        InformacionCuponDTO cuponInfo = cuponServicio.obtenerInformacionCupon(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, cuponInfo));

    }
}
