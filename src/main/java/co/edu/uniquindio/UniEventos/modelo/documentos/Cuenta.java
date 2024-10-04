package co.edu.uniquindio.UniEventos.modelo.documentos;

import co.edu.uniquindio.UniEventos.modelo.vo.CodigoValidacion;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCuenta;
import co.edu.uniquindio.UniEventos.modelo.enums.Rol;
import co.edu.uniquindio.UniEventos.modelo.vo.Usuario;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("cuentas")
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Cuenta{

    private Rol rol;
    private String email;
    private CodigoValidacion codigoValidacionRegistro;
    private Usuario usuario;
    private ObjectId id;
    private LocalDateTime fechaRegistro;
    private String password;
    private EstadoCuenta estadoCuenta;
    private CodigoValidacion codigoValidacionPassword;
}
