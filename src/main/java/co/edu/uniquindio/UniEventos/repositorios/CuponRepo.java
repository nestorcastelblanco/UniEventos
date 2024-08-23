package co.edu.uniquindio.UniEventos.repositorios;


import co.edu.uniquindio.UniEventos.modelo.Cupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuponRepo extends MongoRepository<Cupon, String> {
}
