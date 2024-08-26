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
    private String id;

    private String nombre;
    private LocalDate fechaVencimiento;
    private float descuento;
    private TipoCupon tipoCupon;
    private String codigo;
    private EstadoCupon estado;



}
