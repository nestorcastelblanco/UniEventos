package co.edu.uniquindio.UniEventos.repositorios;

import co.edu.uniquindio.UniEventos.modelo.documentos.Cuenta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaRepo extends MongoRepository<Cuenta, String> {

    @Query ( "{ usuario.cedula : ?0 }")
    Optional<Cuenta> buscarCuentaPorCedula(String cedula);

    @Query ( "{ email : ?0 }")
    Optional<Cuenta> buscarCuentaPorCorreo(String correo);

    @Query("{email : ?0, password : ?1}")
    Optional<Cuenta> validarDatosAutenticacion(String email, String password);

}
