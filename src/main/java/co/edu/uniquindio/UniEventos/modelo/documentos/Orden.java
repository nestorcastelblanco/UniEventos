package co.edu.uniquindio.UniEventos.modelo.documentos;

import co.edu.uniquindio.UniEventos.modelo.vo.DetalleCarrito;
import co.edu.uniquindio.UniEventos.modelo.vo.DetalleOrden;
import co.edu.uniquindio.UniEventos.modelo.vo.Pago;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("ordenes")
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Orden extends Carrito {

    private ObjectId idCliente;
    private LocalDateTime fecha;
    private String codigoPasarela;
    private List<DetalleOrden> items;
    private Pago pago;
    private String id;
    private float total;
    private ObjectId idCupon;

}
