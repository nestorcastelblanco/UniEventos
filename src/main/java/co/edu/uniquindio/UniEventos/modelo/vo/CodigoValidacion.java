package co.edu.uniquindio.UniEventos.modelo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class CodigoValidacion {

    private String codigo;
    private LocalDateTime fechaCreacion;
}
