package co.edu.uniquindio.UniEventos.repositorios;


import co.edu.uniquindio.UniEventos.modelo.Venta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepo extends MongoRepository<Venta, String> {
}
