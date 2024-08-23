package co.edu.uniquindio.UniEventos;

import co.edu.uniquindio.UniEventos.modelo.Usuario;
import co.edu.uniquindio.UniEventos.repositorios.UsuarioRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class CuponTest {

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Test
    public void registrarTest(){
        //Creamos el usuario con sus propiedades
        Usuario usuario = Usuario.builder()
                .cedula("1213444")
                .nombre("Pepito perez")
                .email("pepito@email.com")
                .password("121212")
                .direccion("Calle 12 # 12-12")
                .telefono("3012223333")
                .compras(new ArrayList<>())
                .build();


        //Guardamos el usuario en la base de datos
        Usuario registro = usuarioRepo.save( usuario );


        //Verificamos que se haya guardado validando que no sea null
        Assertions.assertNotNull(registro);
    }

}

