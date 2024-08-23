package co.edu.uniquindio.UniEventos.modelo;

import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("ventas")
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Venta {

    @Id
    @EqualsAndHashCode.Include
    private String codigo;

    private LocalDate fecha;
    private float valor;
    private Evento evento;


}
