package co.edu.uniquindio.UniEventos;

import co.edu.uniquindio.UniEventos.modelo.documentos.Cuenta;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCuenta;
import co.edu.uniquindio.UniEventos.modelo.enums.Rol;
import co.edu.uniquindio.UniEventos.modelo.vo.Usuario;
import co.edu.uniquindio.UniEventos.repositorios.CuentaRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class CuentaTest {

    @Autowired
    private CuentaRepo cuentaRepo;

    @Test
    public void crearClienteTest(){

        Cuenta cuenta = Cuenta.builder()
                .email("juanito@email.com")
                .password("123456")
                .fechaRegistro(LocalDateTime.now())
                .estadoCuenta(EstadoCuenta.ACTIVO)
                .usuario(Usuario.builder()
                        .cedula("123")
                        .nombre("Juanito")
                        .direccion("Calle 123")
                        .telefono("121212").build()
                )
                .rol(Rol.CLIENTE).build();

        Cuenta cuentaCreada = cuentaRepo.save(cuenta);
        Assertions.assertNotNull(cuentaCreada);

    }


}
