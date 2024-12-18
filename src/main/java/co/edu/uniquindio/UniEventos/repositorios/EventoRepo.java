package co.edu.uniquindio.UniEventos.repositorios;

import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoEvento;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepo extends MongoRepository<Evento, String> {

    @Query("{ 'nombre' :?0  }")
    Optional<Evento> buscarPorNombre(String nombre);

    @Query("{ '_id' :?0  }")
    Optional<Evento> buscarPorIdEvento(ObjectId id);

    @Query("{ '_id' :?0  }")
    Optional<Evento> buscarPorIdEvento(String id);

    @Query("{$and: [" + "{ $or: [" + "{ 'nombre': { $regex: ?0, $options: 'i' } }, " + "{ 'tipo': { $regex: ?1, $options: 'i' } }, " +
            "{ 'ciudad': { $regex: ?2, $options: 'i' } }" + "]}, " +
            "{ 'estado': ?3 }" + "]}")
    List<Evento> filtrarEventosCar(String nombre, String tipo, String ciudad, EstadoEvento estado);

    @Query("{$or: [" +
            "{ 'nombre': { $regex: ?0, $options: 'i' } }, " +
            "{ 'tipo': { $regex: ?1, $options: 'i' } }, " +
            "{ 'ciudad': { $regex: ?2, $options: 'i' } }" +
            "]}")
    List<Evento> filtrarEventos(String nombre, String tipo, String ciudad);
}

