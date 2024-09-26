package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.CrearCarritoDTO;
import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.EventoCarritoDTO;
import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.EventoEliminarCarritoDTO;
import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.VistaCarritoDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Carrito;
import co.edu.uniquindio.UniEventos.modelo.documentos.Cuenta;
import co.edu.uniquindio.UniEventos.modelo.vo.DetalleCarrito;
import co.edu.uniquindio.UniEventos.repositorios.CarritoRepo;
import co.edu.uniquindio.UniEventos.servicios.interfaces.CarritoServicio;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarritoServicioImpl implements CarritoServicio {

    private final CarritoRepo carritoRepo;

    public CarritoServicioImpl(CarritoRepo carritoRepo) {
        this.carritoRepo = carritoRepo;
    }

    @Override
    public String crearCarrito(CrearCarritoDTO carrito) throws Exception {

        Carrito carro = Carrito.builder()
                .fecha(LocalDateTime.now())
                .idUsuario(carrito.idUsuario())
                .build();

        Carrito carritoCliente  = carritoRepo.save(carro);
        return carritoCliente.getId();
    }

    @Override
    public String agregarItemCarrito(EventoCarritoDTO eventoCarritoDTO) throws Exception {

        //id del carrito
        //id evento, numBoletas, nombreLocalidad

        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorIdCliente(eventoCarritoDTO.idCliente());
        Carrito carrito = carritoCliente.get();

        DetalleCarrito detalleCarrito = DetalleCarrito.builder()
                .cantidad(eventoCarritoDTO.numBoletas())
                .nombreLocalidad(eventoCarritoDTO.nombreLocalidad())
                .idEvento(eventoCarritoDTO.idEvento()).build();

        carrito.getItems().add(detalleCarrito);
        carritoRepo.save(carrito);
        return "";
    }

    @Override
    public String eliminarItemCarrito(EventoEliminarCarritoDTO eventoEliminarCarritoDTO) throws Exception {

        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorId(eventoEliminarCarritoDTO.idCarrito());

        Carrito carrito =  carritoCliente.get();
        List<DetalleCarrito> lista = carrito.getItems();

        lista.removeIf( i -> i.getId().equals(eventoEliminarCarritoDTO.idDetalle()) );
        carritoRepo.save(carrito);

        return "";
    }

    @Override
    public String eliminarCarrito() throws Exception {
        return "";
    }

    @Override
    public void obtenerInformacionCarrito(VistaCarritoDTO vistaCarritoDTO) throws Exception {

    }

    private Cuenta obtenerCarritoCliente (String id) throws Exception {

        Optional<Carrito> cuentaOptional = carritoRepo.buscarCarritoPorIdCliente(new ObjectId(id));

        if(cuentaOptional.isEmpty()){
            throw new Exception("La cuenta con el id: " + id + " no existe");
        }

        return null;
    }

}