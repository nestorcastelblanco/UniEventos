package co.edu.uniquindio.UniEventos.repositorios;

import co.edu.uniquindio.UniEventos.modelo.documentos.Orden;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrdenRepo extends MongoRepository<Orden, String> {
}
