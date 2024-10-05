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

    @Query("{ '_id': ?0 }")
    Optional<Carrito> buscarCarritoPorId(ObjectId id);

    @Query("{ 'idUsuario' : ?0 }")
    Optional<Carrito> buscarCarritoPorIdCliente(ObjectId idUsuario);


}
