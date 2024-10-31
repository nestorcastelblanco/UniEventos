package co.edu.uniquindio.UniEventos.dto.CuentaDTOs;

import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCuenta;
import co.edu.uniquindio.UniEventos.modelo.enums.Rol;
import org.bson.types.ObjectId;

public record ItemCuentaDTO(
        ObjectId id,
        String nombre,
        String email,
        String telefono,
        EstadoCuenta estadoCuenta,
        Rol rol
) {
}
