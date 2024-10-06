package co.edu.uniquindio.UniEventos.modelo.vo;

import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class DetalleOrden {

    private ObjectId idDetalleOrden;
    private ObjectId idEvento;
    private float precio;
    private String nombreLocalidad;
    private int cantidad;

}
