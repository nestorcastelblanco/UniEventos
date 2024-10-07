package co.edu.uniquindio.UniEventos.modelo.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("usuarios")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    private String telefono;
    private String cedula;
    private String nombre;
    private String direccion;

}
