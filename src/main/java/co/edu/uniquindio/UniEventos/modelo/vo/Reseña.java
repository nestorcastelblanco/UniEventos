package co.edu.uniquindio.UniEventos.modelo.vo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;


@Document("reseñas")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reseña {

    @Id
    private ObjectId id;
    private String idEvento;
    private String idUsuario;
    private int calificacion;  // Calificación de 1 a 5
    private String comentario;
    private LocalDateTime fechaCreacion;
}

