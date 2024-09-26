package co.edu.uniquindio.UniEventos.modelo.vo;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class DetalleCarrito {

    @EqualsAndHashCode.Include
    private ObjectId id;

    private int cantidad;
    private String nombreLocalidad;
    private ObjectId idEvento;
}
