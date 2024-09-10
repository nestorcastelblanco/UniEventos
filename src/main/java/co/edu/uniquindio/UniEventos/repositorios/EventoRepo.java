package co.edu.uniquindio.UniEventos.repositorios;

import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepo extends MongoRepository<Evento, String> {

    @Query("{ 'nombre' :?0  }")
    Optional<Evento> buscarPorNombre(String nombre);

    // Consulta dinámica usando JSON
    @Query("{'nombre': {$regex: ?0, $options: 'i'}, 'tipo': {$regex: ?1, $options: 'i'}, 'ciudad': {$regex: ?2, $options: 'i'}}")
    List<Evento> filtrarEventos(String nombre, String tipo, String ciudad);
}

