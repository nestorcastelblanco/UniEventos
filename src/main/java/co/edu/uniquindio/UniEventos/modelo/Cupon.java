package co.edu.uniquindio.UniEventos.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private String id;

    private String nombre;
    private LocalDateTime fechaVencimiento;
    private float descuento;
    private TipoCupon tipo;
    private String codigo;
    private EstadoCupon estado;



}
