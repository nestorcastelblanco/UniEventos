package co.edu.uniquindio.UniEventos.dto.OrdenDTOs;

import co.edu.uniquindio.UniEventos.modelo.enums.EstadoOrden;
import co.edu.uniquindio.UniEventos.modelo.vo.DetalleOrden;
import co.edu.uniquindio.UniEventos.modelo.vo.Pago;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

public record InformacionOrdenCompraDTO (
        ObjectId idCliente,
        LocalDateTime fecha,
        String codigoPasarela,
        List<DetalleOrden>items,
        Pago pago,
        String ordenId,
        float total,
        String codigoCupon,
        EstadoOrden estado
){
}
