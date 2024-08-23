package co.edu.uniquindio.UniEventos.repositorios;


import co.edu.uniquindio.UniEventos.modelo.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepo extends MongoRepository<Usuario, String> {
}
