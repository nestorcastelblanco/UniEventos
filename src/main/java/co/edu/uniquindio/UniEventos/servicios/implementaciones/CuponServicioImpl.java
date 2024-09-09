package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.dto.CrearCuponDTO;
import co.edu.uniquindio.UniEventos.dto.EditarCuponDTO;
import co.edu.uniquindio.UniEventos.dto.InformacionCuponDTO;
import co.edu.uniquindio.UniEventos.dto.ItemCuponDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Cupon;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoCupon;
import co.edu.uniquindio.UniEventos.repositorios.CuponRepo;
import co.edu.uniquindio.UniEventos.servicios.interfaces.CuponServicio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CuponServicioImpl implements CuponServicio {

    private final CuponRepo cuponRepo;

    public CuponServicioImpl(CuponRepo cuponRepo) {
        this.cuponRepo = cuponRepo;
    }

    @Override
    public String crearCupon(CrearCuponDTO cupon) throws Exception {

        // Validar si ya existe un cupón con el mismo código
        if (existeCodigo(cupon.codigo())) {
            throw new Exception("Ya existe un cupón con este código");
        }

        // Crear el objeto Cupon y asignar los valores
        Cupon nuevoCupon = new Cupon();
        nuevoCupon.setCodigo(cupon.codigo());
        nuevoCupon.setNombre(cupon.nombre());
        nuevoCupon.setDescuento(cupon.descuento());
        nuevoCupon.setTipo(cupon.tipo());
        nuevoCupon.setFechaVencimiento(cupon.fechaVencimiento());
        nuevoCupon.setEstado(EstadoCupon.DISPONIBLE);

        // Guardar el cupón en la base de datos
        Cupon cuponCreado = cuponRepo.save(nuevoCupon);

        // Retornar el ID del cupón creado
        return cuponCreado.getId();
    }

    //Metodo auxiliar para verificar si un cupón con el mismo código ya existe
    private boolean existeCodigo(String codigo) {
        return cuponRepo.buscarCuponPorCodigo(codigo).isPresent();
    }


    @Override
    public String editarCupon(EditarCuponDTO cupon) throws Exception {

        Cupon cuponExistente = obtenerCupon(cupon.id());

        // Actualizar los campos del cupón con los valores del DTO
        cuponExistente.setNombre(cupon.nombre());
        cuponExistente.setCodigo(cupon.codigo());
        cuponExistente.setDescuento(cupon.descuento());
        cuponExistente.setTipo(cupon.tipo());
        cuponExistente.setEstado(cupon.estado());
        cuponExistente.setFechaVencimiento(cupon.fechaVencimiento());

        cuponRepo.save(cuponExistente);

        return cuponExistente.getId();
    }

    private Cupon obtenerCupon(String id) throws Exception {
        Optional<Cupon> cuponOptional = cuponRepo.findById(id);

        if (cuponOptional.isEmpty()) {
            throw new Exception("El cupón con el id: " + id + " no existe");
        }

        return cuponOptional.get();
    }


    @Override
    public String eliminarCupon(String id) throws Exception {

        Cupon cuponExistente = obtenerCupon(id);
        cuponExistente.setEstado(EstadoCupon.NO_DISPONIBLE);

        cuponRepo.save(cuponExistente);

        return "Eliminado";
    }


    @Override
    public List<ItemCuponDTO> obtenerCupones() throws Exception {
        List<Cupon> cupones = cuponRepo.findAll();

        return cupones.stream().map(cupon -> new ItemCuponDTO(
                cupon.getId(),
                cupon.getNombre(),
                cupon.getCodigo(),
                cupon.getDescuento(),
                cupon.getFechaVencimiento(),
                cupon.getEstado()
        )).collect(Collectors.toList());
    }

    @Override
    public InformacionCuponDTO obtenerInformacionCupon(String id) throws Exception {
        Cupon cupon = obtenerCupon(id);
        return new InformacionCuponDTO(
                cupon.getId(),
                cupon.getNombre(),
                cupon.getCodigo(),
                cupon.getDescuento(),
                cupon.getFechaVencimiento(),
                cupon.getTipo(),
                cupon.getEstado()
        );
    }

    @Override
    public String redimirCupon(String codigo) throws Exception {
        Optional<Cupon> cuponOptional = cuponRepo.buscarCuponPorCodigo(codigo);

        if (cuponOptional.isEmpty()) {
            throw new Exception("El cupón no existe o el código es incorrecto");
        }

        Cupon cupon = cuponOptional.get();
        if (cupon.getEstado() != EstadoCupon.DISPONIBLE) {
            throw new Exception("El cupón no está activo");
        }

        if (cupon.getFechaVencimiento().isBefore(LocalDateTime.now())) {
            throw new Exception("El cupón ha vencido");
        }

        cupon.setEstado(EstadoCupon.NO_DISPONIBLE);
        cuponRepo.save(cupon);
        return "Cupón redimido exitosamente";
    }
}
