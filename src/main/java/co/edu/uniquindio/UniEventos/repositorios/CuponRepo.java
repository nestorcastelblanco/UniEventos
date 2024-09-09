package co.edu.uniquindio.UniEventos.repositorios;

import co.edu.uniquindio.UniEventos.modelo.documentos.Cupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuponRepo extends MongoRepository<Cupon, String> {

    @Query("{ codigo : ?0 }")
    Optional<Cupon> buscarCuponPorCodigo(String codigo);

}
