package co.edu.uniquindio.UniEventos.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("localidades")
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Localidad {

    private String nombre;
    private float precio;
    private int capacidad, boletasVendidas;


}
