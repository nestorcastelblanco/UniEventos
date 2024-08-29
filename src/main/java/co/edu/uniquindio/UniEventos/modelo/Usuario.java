package co.edu.uniquindio.UniEventos.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
