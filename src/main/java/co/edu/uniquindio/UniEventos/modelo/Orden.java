package co.edu.uniquindio.UniEventos.modelo;

import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Orden {

    private ObjectId idCliente;
    private LocalDateTime fecha;
    private String codigoPasarela;
    private List<DetalleOrden> items;
    private Pago pago;
    private String id;
    private float total;
    private ObjectId idCupon;
}
