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
public class DetalleCarrito {

    @EqualsAndHashCode.Include
    private ObjectId idDetalleCarrito;

    private int cantidad;
    private String nombreLocalidad;
    private String idEvento;
}
