package co.edu.uniquindio.UniEventos.repositorios;

import co.edu.uniquindio.UniEventos.modelo.vo.Resena;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResenaRepo extends MongoRepository<Resena, String> {

    // Buscar una reseña por su ID
    @Query("{ id : ?0 }")
    Optional<Resena> buscarReseñaPorId(String id);

    @Query("{ idEvento : ?0 }")
    List<Resena> findByIdEvento(String idEvento);

    // Buscar todas las reseñas asociadas a un usuario específico
    @Query("{ idUsuario : ?0 }")
    List<Resena> findByIdUsuario(String idUsuario);


}
