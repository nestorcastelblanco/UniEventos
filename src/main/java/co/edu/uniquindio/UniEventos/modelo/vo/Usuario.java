package co.edu.uniquindio.UniEventos.modelo.vo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("usuarios")
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Usuario {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String telefono;
    private String cedula;
    private String nombre;
    private String direccion;

}
