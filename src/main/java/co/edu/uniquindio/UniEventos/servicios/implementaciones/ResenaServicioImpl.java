package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.repositorios.ResenaRepo;
import co.edu.uniquindio.UniEventos.dto.ResenaDTO;
import co.edu.uniquindio.UniEventos.modelo.vo.Resena;
import co.edu.uniquindio.UniEventos.servicios.interfaces.ResenaServicio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResenaServicioImpl implements ResenaServicio {

    private final ResenaRepo reseñaRepo;

    public ResenaServicioImpl(ResenaRepo reseñaRepo) {
        this.reseñaRepo = reseñaRepo;
    }

    @Override
    public String crearReseña(ResenaDTO resenaDTO) throws Exception {
        // Convertir los Strings idEvento e idUsuario a ObjectId
        String idEvento = resenaDTO.idEvento();
        String idUsuario = resenaDTO.idUsuario();

        // Crear una nueva entidad Resena a partir del DTO
        Resena nuevaResena = new Resena();
        nuevaResena.setIdEvento(idEvento);
        nuevaResena.setIdUsuario(idUsuario);
        nuevaResena.setCalificacion(resenaDTO.calificacion());
        nuevaResena.setComentario(resenaDTO.comentario());
        nuevaResena.setFechaCreacion(resenaDTO.fechaCreacion());

        // Guardar la reseña en la base de datos
        reseñaRepo.save(nuevaResena);
        return "La reseña fue creada con éxito";
    }


    @Override
    public List<ResenaDTO> obtenerReseñasPorEvento(String idEvento) throws Exception {
        // Obtener las resenas del evento por id
        List<Resena> resenas = reseñaRepo.findByIdEvento(idEvento);

        // Convertir las entidades Resena a DTO
        return resenas.stream().map(resena -> new ResenaDTO(
                resena.getIdEvento(),
                resena.getIdUsuario(),
                resena.getCalificacion(),
                resena.getComentario(),
                resena.getFechaCreacion()
        )).collect(Collectors.toList());
    }

    @Override
    public List<ResenaDTO> obtenerReseñasPorUsuario(String idUsuario) throws Exception {
        // Obtener las resenas del usuario por id
        List<Resena> resenas = reseñaRepo.findByIdUsuario(idUsuario);

        // Convertir las entidades Resena a DTO
        return resenas.stream().map(resena -> new ResenaDTO(
                resena.getIdEvento(),
                resena.getIdUsuario(),
                resena.getCalificacion(),
                resena.getComentario(),
                resena.getFechaCreacion()
        )).collect(Collectors.toList());
    }


}


