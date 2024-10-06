package co.edu.uniquindio.UniEventos.modelo.vo;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("localidades")
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Localidad {

    private String nombreLocalidad;
    private float precio;
    private int capacidadMaxima, entradasVendidas;


}
