package co.edu.uniquindio.UniEventos.dto.CarritoDTOs;

import co.edu.uniquindio.UniEventos.modelo.vo.DetalleCarrito;
import jakarta.validation.constraints.NotBlank;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

public record CrearCarritoDTO(
        @NotBlank ObjectId idUsuario
) {
}
