package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.CrearCarritoDTO;
import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.EventoCarritoDTO;
import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.EventoEliminarCarritoDTO;
import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.VistaCarritoDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Carrito;
import co.edu.uniquindio.UniEventos.modelo.documentos.Cuenta;
import co.edu.uniquindio.UniEventos.repositorios.CarritoRepo;
import co.edu.uniquindio.UniEventos.servicios.interfaces.CarritoServicio;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
<<<<<<< HEAD
    public String agregarItemCarrito(EventoCarritoDTO eventoCarritoDTO) throws Exception {

        //id del carrito
        //id evento, numBoletas, nombreLocalidad

        Carrito carritoCLiente = buscarCLiente

=======
    public String agregarItemCarrito() throws Exception {
        
>>>>>>> f1cd1d4cd5611bd89437153db02d76d02eeb54c6
        return "";
    }

    @Override
    public String eliminarItemCarrito(EventoEliminarCarritoDTO eventoEliminarCarritoDTO) throws Exception {
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

        return cuentaOptional.get();
    }

}
