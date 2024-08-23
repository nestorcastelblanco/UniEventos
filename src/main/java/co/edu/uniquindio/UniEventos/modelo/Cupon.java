package co.edu.uniquindio.UniEventos.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document("cupones")
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Cupon {

    @Id
    @EqualsAndHashCode.Include
    private String codigo;

    private String nombre;
    private LocalDate fechaInicio, fechaFin;
    private float descuento;
    private TipoCupon tipoCupon;


}
