package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.dto.CarritoDTOs.CrearCarritoDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Carrito;
import co.edu.uniquindio.UniEventos.repositorios.CarritoRepo;
import co.edu.uniquindio.UniEventos.servicios.interfaces.CarritoServicio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .fecha(carrito.fecha())
                .id(carrito.id())
                .items(carrito.items())
                .idUsuario(carrito.idUsuario())
                .build();

        Carrito carritoCliente  = carritoRepo.save(carro);
        return carritoCliente.getId();
    }

    @Override
    public String agregarItemCarrito() throws Exception {

    }

    @Override
    public String eliminarItemCarrito() throws Exception {
        return "";
    }

    @Override
    public String eliminarCarrito() throws Exception {
        return "";
    }

    @Override
    public void obtenerInformacionCarrito() throws Exception {

    }


}
