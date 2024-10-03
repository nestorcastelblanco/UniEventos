package co.edu.uniquindio.UniEventos.controller;

import co.edu.uniquindio.UniEventos.dto.EmailDTOs.EmailDTO;
import co.edu.uniquindio.UniEventos.dto.TokenDTOs.MensajeDTO;
import co.edu.uniquindio.UniEventos.servicios.interfaces.EmailServicio;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/email")
@SecurityRequirement(name = "bearerAuth")
public class EmailController {

    private final EmailServicio emailServicio;

    @PostMapping("/enviar")
    public ResponseEntity<MensajeDTO<String>> enviarCorreo(@RequestBody EmailDTO emailDTO) {
        try {
            emailServicio.enviarCorreo(emailDTO);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Correo enviado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MensajeDTO<>(true, "Error al enviar el correo: " + e.getMessage()));
        }
    }
}
