package co.edu.uniquindio.UniEventos.dto.CarritoDTOs;

import co.edu.uniquindio.UniEventos.modelo.vo.DetalleCarrito;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;
import java.util.List;

public record VistaCarritoDTO(

        ObjectId id_carrito,
        List<DetalleCarrito> detallesCarrito,
        LocalDateTime fecha
) {
}
