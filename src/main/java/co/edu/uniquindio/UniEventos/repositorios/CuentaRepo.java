package co.edu.uniquindio.UniEventos.repositorios;

import co.edu.uniquindio.UniEventos.modelo.documentos.Cuenta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepo extends MongoRepository<Cuenta, String> {
}
