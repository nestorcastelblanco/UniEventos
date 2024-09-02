package co.edu.uniquindio.UniEventos.modelo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
public class CodigoValidacion {

    private String codigo;
    private LocalDateTime fechaCreacion;
}
