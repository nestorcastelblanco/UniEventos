package co.edu.uniquindio.UniEventos.repositorios;


import co.edu.uniquindio.UniEventos.modelo.Boleta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoletaRepo extends MongoRepository<Boleta, String> {
}
