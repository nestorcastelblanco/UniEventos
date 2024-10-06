package co.edu.uniquindio.UniEventos.dto.CarritoDTOs;

import org.bson.types.ObjectId;

public record ActualizarItemCarritoDTO(
        ObjectId idCliente,
        ObjectId idEvento,
        int nuevaCantidad
) {
}
