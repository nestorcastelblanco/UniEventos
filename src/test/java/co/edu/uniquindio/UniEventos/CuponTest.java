package co.edu.uniquindio.UniEventos;

import co.edu.uniquindio.UniEventos.modelo.Cupon;
import co.edu.uniquindio.UniEventos.modelo.TipoCupon;
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
    public void registrarTest(){
        //Creamos el cupon con sus propiedades

        Cupon cupon = Cupon.builder()
                .nombre("UNIQ")
                .fechaInicio(LocalDate.of(2024,8,24))
                .fechaFin(LocalDate.of(2024,12,24))
                .tipoCupon(TipoCupon.INDIVIDUAL)
                .descuento((float) 0.15).build();

        //Guardamos el cupon en la base de datos
        Cupon registro = cuponRepo.save(cupon);


        //Verificamos que se haya guardado validando que no sea null
        Assertions.assertNotNull(registro);
    }

}

