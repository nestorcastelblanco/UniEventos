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
        return carritoCliente.getId();
    }

    @Override
    public String agregarItemCarrito(EventoCarritoDTO eventoCarritoDTO) throws Exception {
        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorIdCliente(eventoCarritoDTO.idCliente());
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
        return "Item agregado";
    }

    @Override
    public String eliminarItemCarrito(EventoEliminarCarritoDTO eventoEliminarCarritoDTO) throws Exception {
        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorId(eventoEliminarCarritoDTO.idCarrito());
        if (carritoCliente.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        Carrito carrito = carritoCliente.get();
        List<DetalleCarrito> lista = carrito.getItems();
        lista.removeIf(i -> i.getId().equals(eventoEliminarCarritoDTO.idDetalle()));
        carritoRepo.save(carrito);

        return "Se eliminó el elemento del carrito";
    }

    @Override
    public void eliminarCarrito(EliminarCarritoDTO eliminarCarritoDTO) throws Exception {
        Optional<Carrito> carrito = carritoRepo.buscarCarritoPorId(eliminarCarritoDTO.idCarrito());
        if (carrito.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        Carrito carritoDelete = carrito.get();
        carritoRepo.delete(carritoDelete);
    }

    @Override
    public void obtenerInformacionCarrito(VistaCarritoDTO vistaCarritoDTO) throws Exception {
        Optional<Carrito> carrito = carritoRepo.buscarCarritoPorId(vistaCarritoDTO.id_carrito());
        if (carrito.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        Carrito carrito_info = carrito.get();
        List<DetalleCarrito> detalles_carrito = carrito_info.getItems();
        LocalDateTime fecha = carrito_info.getFecha();
        String id_carrito = carrito_info.getId();

        // Aquí puedes usar la información obtenida (detalles_carrito, fecha, etc.)
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
                return "Item actualizado";
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
        Evento evento_seleccionado = evento.get();

        List<Localidad> localidades = evento_seleccionado.getLocalidades();

        for (Localidad localidad : localidades) {
            if (localidad.getNombre().equals(nombreLocalidad)) {
                return localidad.getPrecio();
            }
        }

        throw new Exception("No se encontro el evento en el carrito");
    }

    private Carrito obtenerCarritoCliente(String id) throws Exception {
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorIdCliente(id);
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito con el id: " + id + " no existe");
        }
        return carritoOptional.get();
    }

    @Override
    public String vaciarCarrito(String id) throws Exception {
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorIdCliente(id);
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito con el id: " + id + " no existe");
        }

        Carrito carrito = carritoOptional.get();

        List<DetalleCarrito> detalles_carrito = new ArrayList<>();
        carrito.setItems(detalles_carrito);

        carritoRepo.save(carrito);
        return "Se vaceo el carrito";
    }
}
