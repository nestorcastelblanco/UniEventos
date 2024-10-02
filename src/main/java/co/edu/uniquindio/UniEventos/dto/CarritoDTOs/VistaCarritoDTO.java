package co.edu.uniquindio.UniEventos.dto.CarritoDTOs;

import co.edu.uniquindio.UniEventos.modelo.vo.DetalleCarrito;

import java.time.LocalDateTime;
import java.util.List;

public record VistaCarritoDTO(

        String id_carrito,
        List<DetalleCarrito> detallesCarrito,
        LocalDateTime fecha
) {
    // Constructor that initializes all fields
    public VistaCarritoDTO(String id_carrito, List<DetalleCarrito> detallesCarrito, LocalDateTime fecha) {
        this.id_carrito = id_carrito;
        this.detallesCarrito = detallesCarrito;
        this.fecha = fecha;
    }
}
