package co.edu.uniquindio.UniEventos.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/evento")
@SecurityRequirement(name = "bearerAuth")

public class EventoController {


    
}
