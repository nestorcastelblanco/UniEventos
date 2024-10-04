package co.edu.uniquindio.UniEventos.dto.CuentaDTOs;

public record InformacionCuentaDTO(
        org.bson.types.ObjectId cedula,
        String nombre,
        String telefono,
        String direccion,
        String correo
) {
}
