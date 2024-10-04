package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.*;
import co.edu.uniquindio.UniEventos.modelo.documentos.Carrito;
import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;
import co.edu.uniquindio.UniEventos.modelo.vo.DetalleCarrito;
import co.edu.uniquindio.UniEventos.modelo.vo.Localidad;
import co.edu.uniquindio.UniEventos.repositorios.CarritoRepo;
import co.edu.uniquindio.UniEventos.repositorios.EventoRepo;
import co.edu.uniquindio.UniEventos.servicios.interfaces.CarritoServicio;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarritoServicioImpl implements CarritoServicio {

    private final CarritoRepo carritoRepo;
    private final EventoRepo eventoRepo;

    public CarritoServicioImpl(CarritoRepo carritoRepo, EventoRepo eventoRepo) {
        this.carritoRepo = carritoRepo;
        this.eventoRepo = eventoRepo;
    }

    @Override
    public String crearCarrito(CrearCarritoDTO carrito) throws Exception {
        Carrito carro = Carrito.builder()
                .fecha(LocalDateTime.now())
                .idUsuario(carrito.idUsuario())
                .build();

        Carrito carritoCliente = carritoRepo.save(carro);
        return  "carrito creado";
    }

    @Override
    public String agregarItemCarrito(EventoCarritoDTO eventoCarritoDTO) throws Exception {
        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorIdCliente(String.valueOf(eventoCarritoDTO.idCliente()));
        if (carritoCliente.isEmpty()) {
            throw new Exception("El carrito no existe para el cliente con ID: " + eventoCarritoDTO.idCliente());
        }

        Carrito carrito = carritoCliente.get();
        DetalleCarrito detalleCarrito = DetalleCarrito.builder()
                .cantidad(eventoCarritoDTO.numBoletas())
                .nombreLocalidad(eventoCarritoDTO.nombreLocalidad())
                .idEvento(eventoCarritoDTO.idEvento()).build();

        carrito.getItems().add(detalleCarrito);
        carritoRepo.save(carrito);
        return "Item agregado al carrito correctamente";
    }

    @Override
    public String eliminarItemCarrito(EventoEliminarCarritoDTO eventoEliminarCarritoDTO) throws Exception {
        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorId(String.valueOf(eventoEliminarCarritoDTO.idCarrito()));
        if (carritoCliente.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        Carrito carrito = carritoCliente.get();
        List<DetalleCarrito> lista = carrito.getItems();
        boolean removed = lista.removeIf(i -> i.getId().equals(eventoEliminarCarritoDTO.idDetalle()));

        if (!removed) {
            throw new Exception("El elemento no se encontró en el carrito");
        }

        carritoRepo.save(carrito);
        return "Elemento eliminado del carrito";
    }

    @Override
    public void eliminarCarrito(EliminarCarritoDTO eliminarCarritoDTO) throws Exception {
        Optional<Carrito> carrito = carritoRepo.buscarCarritoPorId(String.valueOf(eliminarCarritoDTO.idCarrito()));
        if (carrito.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        carritoRepo.delete(carrito.get());
    }

    @Override
    public VistaCarritoDTO obtenerInformacionCarrito(VistaCarritoDTO carritoDTO) throws Exception {
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorId(String.valueOf(carritoDTO.id_carrito()));
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        Carrito carrito = carritoOptional.get();
        List<DetalleCarrito> detallesCarrito = carrito.getItems();
        LocalDateTime fecha = carrito.getFecha();

        return new VistaCarritoDTO(carrito.getId(), detallesCarrito, fecha);
    }

    @Override
    public List<CarritoListDTO> listarCarritos() {
        List<Carrito> carritos = carritoRepo.findAll();  // Obtener todos los carritos
        return carritos.stream().map(carrito -> {
            // Mapear cada Carrito a CarritoListDTO
            return new CarritoListDTO(carrito.getId(), carrito.getFecha(), carrito.getItems());
        }).collect(Collectors.toList());
    }

    @Override
    public String actualizarItemCarrito(ActualizarItemCarritoDTO actualizarItemCarritoDTO) throws Exception {
        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorIdCliente(actualizarItemCarritoDTO.idCliente());
        if (carritoCliente.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        Carrito carrito = carritoCliente.get();
        for (DetalleCarrito item : carrito.getItems()) {
            if (item.getIdEvento().equals(actualizarItemCarritoDTO.idEvento())) {
                item.setCantidad(actualizarItemCarritoDTO.nuevaCantidad());
                carritoRepo.save(carrito);
                return "Item actualizado exitosamente";
            }
        }

        throw new Exception("El item no existe en el carrito");
    }

    @Override
    public double calcularTotalCarrito(String idCliente) throws Exception {
        Carrito carrito = obtenerCarritoCliente(idCliente);
        double total = 0;

        for (DetalleCarrito item : carrito.getItems()) {
            total += item.getCantidad() * obtenerPrecioEvento(item.getIdEvento(), item.getNombreLocalidad());
        }

        return total;
    }

    private double obtenerPrecioEvento(ObjectId idEvento, String nombreLocalidad) throws Exception {
        Optional<Evento> evento = eventoRepo.buscarPorIdEvento(idEvento);
        if (evento.isEmpty()) {
            throw new Exception("El evento no existe");
        }

        List<Localidad> localidades = evento.get().getLocalidades();
        for (Localidad localidad : localidades) {
            if (localidad.getNombre().equals(nombreLocalidad)) {
                return localidad.getPrecio();
            }
        }

        throw new Exception("La localidad no existe para el evento especificado");
    }

    private Carrito obtenerCarritoCliente(String idCliente) throws Exception {
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorIdCliente(idCliente);
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito con el id: " + idCliente + " no existe");
        }
        return carritoOptional.get();
    }

    @Override
    public String vaciarCarrito(String idCliente) throws Exception {
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorIdCliente(idCliente);
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito con el id: " + idCliente + " no existe");
        }

        Carrito carrito = carritoOptional.get();
        carrito.setItems(new ArrayList<>());

        carritoRepo.save(carrito);
        return "Carrito vaciado exitosamente";
    }
}
