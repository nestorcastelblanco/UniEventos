package co.edu.uniquindio.UniEventos.dto.CarritoDTOs;

import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;
import co.edu.uniquindio.UniEventos.modelo.vo.DetalleCarrito;
import jakarta.validation.constraints.NotBlank;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

public record EventoCarritoDTO(
        ObjectId idCarrito,
        ObjectId idEvento,
        int numBoletas,
        String nombreLocalidad,
        ObjectId idCliente

    ) {
}
