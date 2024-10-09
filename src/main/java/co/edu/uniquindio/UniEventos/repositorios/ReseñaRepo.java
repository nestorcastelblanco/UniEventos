package co.edu.uniquindio.UniEventos.repositorios;

import co.edu.uniquindio.UniEventos.modelo.vo.Reseña;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReseñaRepo extends MongoRepository<Reseña, String> {

    // Buscar una reseña por su ID
    @Query("{ id : ?0 }")
    Optional<Reseña> buscarReseñaPorId(String id);

    @Query("{ idEvento : ?0 }")
    List<Reseña> findByIdEvento(String idEvento);

    // Buscar todas las reseñas asociadas a un usuario específico
    @Query("{ idUsuario : ?0 }")
    List<Reseña> findByIdUsuario(String idUsuario);

}
