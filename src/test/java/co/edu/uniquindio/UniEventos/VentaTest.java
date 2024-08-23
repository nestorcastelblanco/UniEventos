package co.edu.uniquindio.UniEventos;

import co.edu.uniquindio.UniEventos.modelo.*;
import co.edu.uniquindio.UniEventos.repositorios.UsuarioRepo;
import co.edu.uniquindio.UniEventos.repositorios.VentaRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;

@SpringBootTest
public class VentaTest {

    @Autowired
    private VentaRepo ventaRepo;

    @Test
    public void registrarTest(){

        ArrayList<Localidad> localidades = new ArrayList<>();

        Localidad localidad = Localidad.builder()
                .nombre("Gramilla VIP")
                .precio(150000)
                .capacidad(250)
                .boletasVendidas(0).build();


        Localidad localidad1 = Localidad.builder()
                .nombre("Gramilla")
                .precio(100000)
                .capacidad(350)
                .boletasVendidas(0).build();


        Localidad localidad2 = Localidad.builder()
                .nombre("Gramilla")
                .precio(85000)
                .capacidad(400)
                .boletasVendidas(0).build();


        localidades.add(localidad);
        localidades.add(localidad1);
        localidades.add(localidad2);

        Evento evento = Evento.builder()
                .nombre("LUIS ALFONSO")
                .dirección("Coliseo del Cafe")
                .ciudad("Armenia, Quindio")
                .descripción("Concierto en vivo de Luis Alfonso")
                .tipo(Tipo.CONCIERTO)
                .imágenes(new ArrayList<>())
                .fecha(LocalDate.now())
                .localidades(localidades).build();

        //Creamos el usuario con sus propiedades
        Venta venta = Venta.builder()
                .fecha(LocalDate.now())
                .valor(125000)
                .evento(evento).build();

        //Guardamos el usuario en la base de datos
        Venta registro = ventaRepo.save( venta );

        //Verificamos que se haya guardado validando que no sea null
        Assertions.assertNotNull(registro);
    }

}

