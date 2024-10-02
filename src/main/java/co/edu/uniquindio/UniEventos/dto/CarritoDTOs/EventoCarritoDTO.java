package co.edu.uniquindio.UniEventos.dto.CarritoDTOs;

import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;
import co.edu.uniquindio.UniEventos.modelo.vo.DetalleCarrito;
import jakarta.validation.constraints.NotBlank;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

public record EventoCarritoDTO(

        @NotBlank ObjectId idEvento,
        @NotBlank String idCliente,
        @NotBlank String nombre,
        @NotBlank LocalDateTime fechaEvento,
        @NotBlank int numBoletas,
        @NotBlank String nombreLocalidad
) {
}
