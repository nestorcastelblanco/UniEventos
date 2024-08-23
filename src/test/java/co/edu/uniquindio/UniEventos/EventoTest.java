package co.edu.uniquindio.UniEventos;

import co.edu.uniquindio.UniEventos.modelo.Evento;
import co.edu.uniquindio.UniEventos.modelo.Localidad;
import co.edu.uniquindio.UniEventos.modelo.Tipo;
import co.edu.uniquindio.UniEventos.modelo.Usuario;
import co.edu.uniquindio.UniEventos.repositorios.EventoRepo;
import co.edu.uniquindio.UniEventos.repositorios.UsuarioRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;

@SpringBootTest
public class EventoTest {

    @Autowired
    private EventoRepo eventoRepo;

    @Test
    public void registrarTest(){
        //Creamos el Evento con sus propiedades

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

        //Guardamos el usuario en la base de datos
        Evento registro = eventoRepo.save( evento );

        //Verificamos que se haya guardado validando que no sea null
        Assertions.assertNotNull(registro);
    }

}

