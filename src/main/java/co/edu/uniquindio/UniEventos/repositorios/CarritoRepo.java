package co.edu.uniquindio.UniEventos.repositorios;

import co.edu.uniquindio.UniEventos.modelo.documentos.Carrito;
import co.edu.uniquindio.UniEventos.modelo.vo.DetalleCarrito;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepo extends MongoRepository<Carrito, String> {

    @Query ( "{ id : ?0 }")
    Optional<Carrito> buscarCarritoPorId(ObjectId id);

    @Query ( "{ idUsuario : ?0 }")
    Optional<Carrito> buscarCarritoPorIdCliente(ObjectId id);

    @Query ( "{ items.id : ?0}")
    Optional<DetalleCarrito> buscarEventoCarrito(ObjectId objectId);
}
