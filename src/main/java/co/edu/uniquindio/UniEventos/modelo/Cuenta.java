package co.edu.uniquindio.UniEventos.modelo;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cuenta{

    private Rol rol;
    private String email;
    private CodigoValidacion codigoValidacionRegistro;
    private Usuario usuario;
    private String id;
    private LocalDateTime fechaRegistro;
    private String password;
    private EstadoCuenta estadoCuenta;
    private CodigoValidacion codigoValidacionPassword;
}
