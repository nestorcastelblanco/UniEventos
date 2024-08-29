package co.edu.uniquindio.UniEventos.modelo;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Carrito {

    private List<DetalleCarrito> items;
    private LocalDateTime fecha;
    private String id;
    private ObjectId idUsuario;

}
