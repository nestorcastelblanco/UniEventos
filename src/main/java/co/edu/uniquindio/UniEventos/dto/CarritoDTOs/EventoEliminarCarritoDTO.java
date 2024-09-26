package co.edu.uniquindio.UniEventos.dto.CarritoDTOs;

import org.bson.types.ObjectId;

public record EventoEliminarCarritoDTO(
        ObjectId idDetalle,
        ObjectId idCarrito
        ) {
}
