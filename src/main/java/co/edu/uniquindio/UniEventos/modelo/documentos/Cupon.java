package co.edu.uniquindio.UniEventos.modelo.documentos;

import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCupon;
import co.edu.uniquindio.UniEventos.modelo.enums.TipoCupon;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

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

    private String codigo;
    private float descuento;
    private String nombre;
    private TipoCupon tipo;
    private EstadoCupon estado;
    private LocalDateTime fechaVencimiento;

    public Cupon(String id, String codigo, float descuento, String nombre, TipoCupon tipo, EstadoCupon estado, LocalDateTime fechaVencimiento) {
        this.id = id;
        this.codigo = codigo;
        this.descuento = descuento;
        this.nombre = nombre;
        this.tipo = tipo;
        this.estado = estado;
        this.fechaVencimiento = fechaVencimiento;
    }
}
