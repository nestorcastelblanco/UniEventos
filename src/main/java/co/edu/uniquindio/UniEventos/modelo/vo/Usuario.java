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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @EqualsAndHashCode.Include
    private String idUsuario;

    private String telefono;
    private String cedula;
    private String nombre;
    private String direccion;

    public Usuario(@NotBlank(message = "La cédula es obligatoria") @Length(max = 10, message = "Ingrese una cédula valida") String cedula, @NotBlank(message = "El tamano del nombre no es adecuado") @Length(max = 20) String nombre, @Length(max = 10, message = "Ingrese un telefono valido") String telefono, @Length(max = 30, message = "Ingrese una direccion valida") String direccion) {
    }
}
