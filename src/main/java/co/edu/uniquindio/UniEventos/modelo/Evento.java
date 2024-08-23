package co.edu.uniquindio.UniEventos.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document("eventos")
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Evento {

    @Id
    @EqualsAndHashCode.Include
    private String codigo;

    private String nombre, dirección, ciudad, descripción;
    private Tipo tipo;
    private List<String> imágenes;
    private LocalDate fecha;
    private List<Localidad> localidades;


}
