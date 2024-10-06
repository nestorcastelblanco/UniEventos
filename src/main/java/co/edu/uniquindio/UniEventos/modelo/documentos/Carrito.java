package co.edu.uniquindio.UniEventos.modelo.documentos;

import co.edu.uniquindio.UniEventos.modelo.vo.DetalleCarrito;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "carritos")  // Asegúrate de especificar la colección
public class Carrito {

    @Id  // Añadir la anotación @Id para indicar que este es el campo ID del documento
    private String id;  // Cambiar a ObjectId
    private List<DetalleCarrito> items;
    private LocalDateTime fecha;
    private ObjectId idUsuario;  // Asegúrate de que sea ObjectId para el idUsuario
}
