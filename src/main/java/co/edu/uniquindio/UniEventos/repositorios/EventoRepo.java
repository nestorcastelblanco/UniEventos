package co.edu.uniquindio.UniEventos.repositorios;


import co.edu.uniquindio.UniEventos.modelo.Evento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepo extends MongoRepository<Evento, String> {
}
