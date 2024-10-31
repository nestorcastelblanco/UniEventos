package co.edu.uniquindio.UniEventos.dto.CuentaDTOs;

import org.bson.types.ObjectId;

public record InformacionCuentaDTO(
        ObjectId cedula,
        String nombre,
        String telefono,
        String direccion,
        String correo
) {
}
