package co.edu.uniquindio.UniEventos.modelo.documentos;

import co.edu.uniquindio.UniEventos.modelo.enums.EstadoEvento;
import co.edu.uniquindio.UniEventos.modelo.vo.Localidad;
import co.edu.uniquindio.UniEventos.modelo.enums.TipoEvento;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("eventos")
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Evento {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String nombre, dirección, ciudad, descripción;
    private TipoEvento tipo;
    private EstadoEvento estado;
    private String imágenPortada;
    private LocalDateTime fecha;
    private List<Localidad> localidades;
    private String imagenLocalidades;


}
