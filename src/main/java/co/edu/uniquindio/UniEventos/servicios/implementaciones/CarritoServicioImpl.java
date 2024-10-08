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
import org.springframework.cglib.core.Local;
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
    public String agregarItemCarrito(EventoCarritoDTO eventoCarritoDTO) throws Exception {
        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorIdCliente(eventoCarritoDTO.idUsuario());
        // Si el carrito no existe para el cliente, lanzamos una excepción
        if (!carritoCliente.isPresent()) {
            throw new Exception("El carrito no existe para el cliente con ID: " + eventoCarritoDTO.idUsuario());
        }
        Optional<Evento> evento = eventoRepo.buscarPorIdEvento(eventoCarritoDTO.idEvento());
        Evento eventoSeleccionado = evento.get();
        // Si el carrito existe, procedemos a agregar el item
        Carrito carrito = carritoCliente.get();

        for (Localidad localidad : eventoSeleccionado.getLocalidades()){
            if (localidad.getNombreLocalidad().equals(eventoCarritoDTO.nombreLocalidad())){
                if (eventoCarritoDTO.numBoletas()+ localidad.getEntradasVendidas() > localidad.getCapacidadMaxima()){
                    throw new Exception("Las entradas ingresadas no pueden ser agregadas al carrito, ya que se cuenta con el aforo maximo");
                }else{
                    localidad.setEntradasVendidas(eventoCarritoDTO.numBoletas() + localidad.getEntradasVendidas());
                    eventoRepo.save(eventoSeleccionado);
                }
            }
        }

        DetalleCarrito detalleCarrito = DetalleCarrito.builder()
                .idDetalleCarrito(new ObjectId())
                .cantidad(eventoCarritoDTO.numBoletas())
                .nombreLocalidad(eventoCarritoDTO.nombreLocalidad())
                .idEvento(eventoCarritoDTO.idEvento()).build();

        // Agregar el detalle al carrito y guardar el cambio en la base de datos
        carrito.getItems().add(detalleCarrito);
        carritoRepo.save(carrito);

        return "Item agregado al carrito correctamente " + "nombre localidad : " +eventoCarritoDTO.nombreLocalidad();
    }


    @Override
    public String eliminarItemCarrito(EventoEliminarCarritoDTO eventoEliminarCarritoDTO) throws Exception {
        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorId(eventoEliminarCarritoDTO.idCarrito());
        if (carritoCliente.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        Carrito carrito = carritoCliente.get();
        List<DetalleCarrito> lista = carrito.getItems();


        List<Evento> eventosSistema = eventoRepo.findAll();
        List<Evento> eventosCarrito = new ArrayList<>();


        for (DetalleCarrito detalleCarrito : lista) {
            eventosCarrito.add(eventoRepo.buscarPorIdEvento(detalleCarrito.getIdEvento()).get());
        }

        for (Evento eventoSistema : eventosSistema){

            for (Localidad eventoSistemaLocalidad : eventoSistema.getLocalidades()){

                for (Evento eventoCarrito : eventosCarrito){

                    for (Localidad localidad : eventoCarrito.getLocalidades()){

                        if (eventoSistemaLocalidad.getNombreLocalidad().equals(localidad.getNombreLocalidad())){

                            for (DetalleCarrito detalleCarrito : carrito.getItems()){

                                if (detalleCarrito.getNombreLocalidad().equals(eventoSistemaLocalidad.getNombreLocalidad())){

                                    eventoSistemaLocalidad.setEntradasVendidas(eventoSistemaLocalidad.getEntradasVendidas() - detalleCarrito.getCantidad());
                                }
                            }
                        }
                    }
                }
            }

        }


        boolean removed = lista.removeIf(i -> i.getIdDetalleCarrito().equals(eventoEliminarCarritoDTO.idDetalle()));

        if (!removed) {
            throw new Exception("El elemento no se encontró en el carrito");
        }

        for ( Evento evento : eventosSistema){
            eventoRepo.save(evento);
        }
        carritoRepo.save(carrito);
        return "Elemento eliminado del carrito";
    }

    @Override
    public void eliminarCarrito(EliminarCarritoDTO eliminarCarritoDTO) throws Exception {
        // Buscar carrito por id
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorId(eliminarCarritoDTO.idCarrito());
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        Carrito carrito = carritoOptional.get();
        List<DetalleCarrito> lista = carrito.getItems();

        // Iterar sobre los detalles del carrito
        for (DetalleCarrito detalleCarrito : lista) {
            Optional<Evento> eventoOptional = eventoRepo.buscarPorIdEvento(detalleCarrito.getIdEvento());
            if (eventoOptional.isEmpty()) {
                throw new Exception("El evento no existe");
            }

            Evento evento = eventoOptional.get();

            // Actualizar las entradas vendidas para las localidades del evento
            for (Localidad localidad : evento.getLocalidades()) {
                if (localidad.getNombreLocalidad().equals(detalleCarrito.getNombreLocalidad())) {
                    localidad.setEntradasVendidas(localidad.getEntradasVendidas() - detalleCarrito.getCantidad());
                    break; // Rompe el ciclo cuando encuentre la localidad correspondiente
                }
            }

            // Guardar el evento con las localidades actualizadas
            eventoRepo.save(evento);
        }

        // Finalmente, eliminar el carrito
        carritoRepo.delete(carrito);

        Carrito nuevoCarritoCLiente = Carrito.builder()
                .items(new ArrayList<>())
                .fecha(LocalDateTime.now())
                .idUsuario(carrito.getIdUsuario()).build();

        carritoRepo.save(nuevoCarritoCLiente);
    }

    @Override
    public VistaCarritoDTO obtenerInformacionCarrito(ObjectId id_carrito) throws Exception {
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorId(id_carrito);
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        Carrito carrito = carritoOptional.get();
        List<DetalleCarrito> detallesCarrito = carrito.getItems();
        LocalDateTime fecha = carrito.getFecha();

        return new VistaCarritoDTO(new ObjectId(carrito.getId()), detallesCarrito, fecha);
    }

    @Override
    public List<CarritoListDTO> listarCarritos() {
        List<Carrito> carritos = carritoRepo.findAll();  // Obtener todos los carritos
        return carritos.stream().map(carrito -> {
            // Mapear cada Carrito a CarritoListDTO
            return new CarritoListDTO(new ObjectId(carrito.getId()), carrito.getFecha(), carrito.getItems());
        }).collect(Collectors.toList());
    }

    @Override
    public String actualizarItemCarrito(ActualizarItemCarritoDTO actualizarItemCarritoDTO) throws Exception {
        // Buscar el carrito del cliente
        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorIdCliente(actualizarItemCarritoDTO.idCliente());
        if (carritoCliente.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        Carrito carrito = carritoCliente.get();

        // Buscar el item en el carrito
        for (DetalleCarrito item : carrito.getItems()) {
            if (item.getIdEvento().equals(actualizarItemCarritoDTO.idEvento())) {

                // Buscar el evento correspondiente
                Optional<Evento> eventoOptional = eventoRepo.buscarPorIdEvento(item.getIdEvento());
                if (eventoOptional.isEmpty()) {
                    throw new Exception("El evento no existe");
                }

                Evento evento = eventoOptional.get();

                // Buscar la localidad correspondiente al evento
                for (Localidad localidad : evento.getLocalidades()) {
                    if (localidad.getNombreLocalidad().equals(item.getNombreLocalidad())) {

                        // Calcular la diferencia de entradas (si se agregan o se quitan)
                        int diferenciaEntradas = actualizarItemCarritoDTO.nuevaCantidad() - item.getCantidad();

                        // Modificar las entradas vendidas en función de la nueva cantidad
                        localidad.setEntradasVendidas(localidad.getEntradasVendidas() + diferenciaEntradas);

                        // Verificar que las entradas vendidas no sean negativas
                        if (localidad.getEntradasVendidas() < 0) {
                            throw new Exception("La cantidad de entradas vendidas no puede ser negativa");
                        }

                        // Actualizar la cantidad en el carrito
                        item.setCantidad(actualizarItemCarritoDTO.nuevaCantidad());

                        // Guardar los cambios en el evento y en el carrito
                        eventoRepo.save(evento);
                        carritoRepo.save(carrito);

                        return "Item actualizado exitosamente";
                    }
                }

                throw new Exception("La localidad del evento no existe");
            }
        }

        throw new Exception("El item no existe en el carrito");
    }

    @Override
    public double calcularTotalCarrito(ObjectId idCliente) throws Exception {
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
        System.out.println("NOMBRE DEL EVENTO + LOCALIDAD "+ evento.get().getNombre() + " " + evento.get().getLocalidades());
        List<Localidad> localidades = evento.get().getLocalidades();
        for (Localidad localidad : localidades) {
            if (localidad.getNombreLocalidad().equals(nombreLocalidad)) {
                return localidad.getPrecio();
            }
        }

        throw new Exception("La localidad no existe para el evento especificado");
    }

    private Carrito obtenerCarritoCliente(ObjectId idCliente) throws Exception {
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorIdCliente(idCliente);
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito con el id: " + idCliente + " no existe");
        }
        return carritoOptional.get();
    }

    @Override
    public String vaciarCarrito(ObjectId idCliente) throws Exception {
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorId(idCliente);
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito con el id: " + idCliente + " no existe");
        }

        Carrito carrito = carritoOptional.get();
        carrito.setItems(new ArrayList<>());

        carritoRepo.save(carrito);
        return "Carrito vaciado exitosamente";
    }





    // METODOS DE JUNIT PARA PRUEBAS UNITARIAS






    @Override
    public String agregarItemCarritoPrueba(EventoCarritoDTO eventoCarritoDTO) throws Exception {
        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorIdCliente(eventoCarritoDTO.idUsuario());
        // Si el carrito no existe para el cliente, lanzamos una excepción
        if (!carritoCliente.isPresent()) {
            throw new Exception("El carrito no existe para el cliente con ID: " + eventoCarritoDTO.idUsuario());
        }

        // Si el carrito existe, procedemos a agregar el item
        Carrito carrito = carritoCliente.get();
        DetalleCarrito detalleCarrito = DetalleCarrito.builder()
                .idDetalleCarrito(new ObjectId())
                .cantidad(eventoCarritoDTO.numBoletas())
                .nombreLocalidad(eventoCarritoDTO.nombreLocalidad())
                .idEvento(eventoCarritoDTO.idEvento()).build();

        // Agregar el detalle al carrito y guardar el cambio en la base de datos
        carrito.getItems().add(detalleCarrito);
        carritoRepo.save(carrito);

        return "Item agregado al carrito correctamente " + "nombre localidad : " +eventoCarritoDTO.nombreLocalidad();
    }


    @Override
    public String eliminarItemCarritoPrueba(EventoEliminarCarritoDTO eventoEliminarCarritoDTO) throws Exception {
        Optional<Carrito> carritoCliente = carritoRepo.buscarCarritoPorId(eventoEliminarCarritoDTO.idCarrito());
        if (carritoCliente.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        Carrito carrito = carritoCliente.get();
        List<DetalleCarrito> lista = carrito.getItems();
        boolean removed = lista.removeIf(i -> i.getIdDetalleCarrito().equals(eventoEliminarCarritoDTO.idDetalle()));

        if (!removed) {
            throw new Exception("El elemento no se encontró en el carrito");
        }

        carritoRepo.save(carrito);
        return "Elemento eliminado del carrito";
    }

    @Override
    public void eliminarCarritoPrueba(EliminarCarritoDTO eliminarCarritoDTO) throws Exception {
        Optional<Carrito> carrito = carritoRepo.buscarCarritoPorId(eliminarCarritoDTO.idCarrito());
        if (carrito.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        carritoRepo.delete(carrito.get());
    }

    @Override
    public VistaCarritoDTO obtenerInformacionCarritoPrueba(ObjectId id_carrito) throws Exception {
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorId(id_carrito);
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito no existe");
        }

        Carrito carrito = carritoOptional.get();
        List<DetalleCarrito> detallesCarrito = carrito.getItems();
        LocalDateTime fecha = carrito.getFecha();

        return new VistaCarritoDTO(new ObjectId(carrito.getId()), detallesCarrito, fecha);
    }

    @Override
    public List<CarritoListDTO> listarCarritosPrueba() {
        List<Carrito> carritos = carritoRepo.findAll();  // Obtener todos los carritos
        return carritos.stream().map(carrito -> {
            // Mapear cada Carrito a CarritoListDTO
            return new CarritoListDTO(new ObjectId(carrito.getId()), carrito.getFecha(), carrito.getItems());
        }).collect(Collectors.toList());
    }

    @Override
    public String actualizarItemCarritoPrueba(ActualizarItemCarritoDTO actualizarItemCarritoDTO) throws Exception {
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
    public double calcularTotalCarritoPrueba(ObjectId idCliente) throws Exception {
        Carrito carrito = obtenerCarritoClientePrueba(idCliente);
        double total = 0;

        for (DetalleCarrito item : carrito.getItems()) {
            total += item.getCantidad() * obtenerPrecioEventoPrueba(item.getIdEvento(), item.getNombreLocalidad());
        }

        return total;
    }

    public double obtenerPrecioEventoPrueba(ObjectId idEvento, String nombreLocalidad) throws Exception {
        Optional<Evento> evento = eventoRepo.buscarPorIdEvento(idEvento);
        if (evento.isEmpty()) {
            throw new Exception("El evento no existe");
        }
        System.out.println("NOMBRE DEL EVENTO + LOCALIDAD "+ evento.get().getNombre() + " " + evento.get().getLocalidades());
        List<Localidad> localidades = evento.get().getLocalidades();
        for (Localidad localidad : localidades) {
            if (localidad.getNombreLocalidad().equals(nombreLocalidad)) {
                return localidad.getPrecio();
            }
        }

        throw new Exception("La localidad no existe para el evento especificado");
    }

    public Carrito obtenerCarritoClientePrueba(ObjectId idCliente) throws Exception {
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorIdCliente(idCliente);
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito con el id: " + idCliente + " no existe");
        }
        return carritoOptional.get();
    }

    @Override
    public String vaciarCarritoPrueba(ObjectId idCliente) throws Exception {
        Optional<Carrito> carritoOptional = carritoRepo.buscarCarritoPorId(idCliente);
        if (carritoOptional.isEmpty()) {
            throw new Exception("El carrito con el id: " + idCliente + " no existe");
        }

        Carrito carrito = carritoOptional.get();
        carrito.setItems(new ArrayList<>());

        carritoRepo.save(carrito);
        return "Carrito vaciado exitosamente";
    }
}
