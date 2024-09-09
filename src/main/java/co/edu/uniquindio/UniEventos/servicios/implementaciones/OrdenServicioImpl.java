package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.CrearOrdenDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.InformacionOrdenCompraDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.ItemOrdenDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Orden;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoOrden;
import co.edu.uniquindio.UniEventos.repositorios.OrdenRepo;
import co.edu.uniquindio.UniEventos.servicios.interfaces.OrdenServicio;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrdenServicioImpl implements OrdenServicio {

    private final OrdenRepo ordenRepo;

    public OrdenServicioImpl(OrdenRepo ordenRepo) {
        this.ordenRepo = ordenRepo;
    }


    @Override
    public String crearOrden(CrearOrdenDTO orden) throws Exception {

        // Validar si existe una orden con el mismo ID
        // Se verifica si ya existe una orden en la base de datos con el ID proporcionado en el objeto CrearOrdenDTO.
        if (existeOrden(orden.id())) {
            throw new Exception("Ya existe un cupón con este código");
        }
        // Crear una nueva instancia de Orden
        Orden nuevaOrden = new Orden();

        // Asignar los valores del DTO a la entidad Orden
        nuevaOrden.setIdCliente(orden.idCliente());
        nuevaOrden.setFecha(LocalDateTime.now());
        nuevaOrden.setCodigoPasarela(orden.codigoPasarela());
        nuevaOrden.setItems(orden.items());
        nuevaOrden.setPago(orden.pago());
        nuevaOrden.setTotal(orden.total());
        nuevaOrden.setIdCupon(orden.idCupon());
        nuevaOrden.setEstado(EstadoOrden.DISPONIBLE);

        // Guardar la Orden en la base de datos
        Orden ordenCreada = ordenRepo.save(nuevaOrden);

        // Retornar el ID de la Orden creada
        return ordenCreada.getId();
    }

    //Método auxiliar para verificar si una Orden con el mismo código ya existe
    private boolean existeOrden(String id) {
        return ordenRepo.buscarOrdenPorId(id).isPresent();
    }

    @Override
    public String cancelarOrden(String idOrden) throws Exception {
        // Obtener la orden por su ID
        Orden orden = obtenerOrden(idOrden);

        // Cambiar el estado de la orden a "CANCELADA"
        orden.setEstado(EstadoOrden.CANCELADA);

        // Guardar los cambios en la base de datos
        ordenRepo.save(orden);

        // Retornar un mensaje indicando que la orden ha sido cancelada
        return "Orden cancelada";
    }

    // Método auxiliar para obtener una Orden por su ID
    private Orden obtenerOrden(String idOrden) throws Exception {
        // Buscar la orden en la base de datos utilizando el ID
        Optional<Orden> ordenOptional = ordenRepo.findById(idOrden);

        // Si la orden no existe, lanzar una excepción
        if (ordenOptional.isEmpty()) {
            throw new Exception("La orden con el id: " + idOrden + " no existe");
        }

        // Retornar la orden encontrada
        return ordenOptional.get();
    }

    @Override
    public List<ItemOrdenDTO> obtenerHistorialOrdenes(String idCuenta) throws Exception {

        // Convertir el ID de orden a ObjectId
        ObjectId objectId = new ObjectId(idCuenta);

        // Buscar todas las órdenes asociadas a la cuenta
        List<Orden> ordenes = ordenRepo.findByIdCliente(objectId);
        if (ordenes.isEmpty()) {
            throw new Exception("No se encontraron órdenes para la cuenta proporcionada");
        }
        // Convertir las órdenes en DTOs de historial de órdenes
        return ordenes.stream()
                .map(orden -> new ItemOrdenDTO(
                        orden.getId(),
                        orden.getFecha(),
                        orden.getTotal(),
                        orden.getEstado()))
                .toList();
    }

    @Override
    public InformacionOrdenCompraDTO obtenerInformacionOrden(String idOrden) throws Exception {

        // Buscar la orden por ID
        Optional<Orden> ordenOptional = ordenRepo.buscarOrdenPorId(idOrden);
        if (ordenOptional.isEmpty()) {
            throw new Exception("La orden con el id: " + idOrden + " no existe");
        }


        Orden orden = obtenerOrden(idOrden);
        // Obtener información detallada de la orden
        return new InformacionOrdenCompraDTO(
                orden.getIdCliente(),
                orden.getFecha(),
                orden.getCodigoPasarela(),
                orden.getItems(),
                orden.getPago(),
                orden.getId(),
                orden.getTotal(),
                orden.getIdCupon(),
                orden.getEstado()
        );
    }
}
