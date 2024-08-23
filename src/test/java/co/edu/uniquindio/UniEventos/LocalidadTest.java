package co.edu.uniquindio.UniEventos;

import co.edu.uniquindio.UniEventos.modelo.Localidad;
import co.edu.uniquindio.UniEventos.repositorios.LocalidadRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class LocalidadTest {

    @Autowired
    private LocalidadRepo localidadRepo;

    @Test
    public void registrarTest(){

        //Creamos la Localidad con sus propiedades
        Localidad localidad = Localidad.builder()
                .nombre("Gramilla VIP")
                .precio(150000)
                .capacidad(250)
                .boletasVendidas(0).build();

        //Guardamos la localidad en la base de datos
        Localidad registro = localidadRepo.save( localidad );


        //Verificamos que se haya guardado validando que no sea null
        Assertions.assertNotNull(registro);
    }

}

