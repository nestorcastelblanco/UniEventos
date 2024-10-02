package co.edu.uniquindio.UniEventos.dto.CuentaDTOs;

public record ItemCuentaDTO(String id, String nombre, String email, String telefono,
                            co.edu.uniquindio.UniEventos.modelo.enums.EstadoCuenta estadoCuenta,
                            co.edu.uniquindio.UniEventos.modelo.enums.Rol rol) {
}
