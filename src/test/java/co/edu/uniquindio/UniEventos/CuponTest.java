package co.edu.uniquindio.UniEventos;

import co.edu.uniquindio.UniEventos.modelo.documentos.Cupon;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCupon;
import co.edu.uniquindio.UniEventos.modelo.enums.TipoCupon;
import co.edu.uniquindio.UniEventos.repositorios.CuponRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
public class CuponTest {

    @Autowired
    private CuponRepo cuponRepo;

    @Test
    public void crearCuponMultipleTest() {

        //Creamos el cupon con sus propiedades
        Cupon cupon = Cupon.builder()
                .codigo("0000")
                .descuento(10)
                .nombre("Cupon de descuento")
                .tipo(TipoCupon.MULTIPLE)
                .estado(EstadoCupon.DISPONIBLE)
                .fechaVencimiento(LocalDate.of(2025, 1, 1).atStartOfDay())
                .build();

        //Guardamos el cupon en la base de datos
        Cupon cuponCreado = cuponRepo.save(cupon);

        //Verificamos que se haya guardado validando que no sea null
        Assertions.assertNotNull(cuponCreado);
    }

}

