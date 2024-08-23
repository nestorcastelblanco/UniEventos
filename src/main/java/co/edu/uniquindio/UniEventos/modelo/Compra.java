package co.edu.uniquindio.UniEventos.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Compra {

    @Id
    @EqualsAndHashCode.Include
    private String codigo;

    private Usuario usuario;
    private int cantBoletas;
    private Boleta boleta;
    private Estado estado;


}
