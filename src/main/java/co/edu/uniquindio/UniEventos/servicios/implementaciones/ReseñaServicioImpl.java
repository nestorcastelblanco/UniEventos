package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.servicios.interfaces.ReseñaServicio;
import co.edu.uniquindio.UniEventos.dto.ReseñaDTO;
import co.edu.uniquindio.UniEventos.modelo.vo.Reseña;
import co.edu.uniquindio.UniEventos.repositorios.ReseñaRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReseñaServicioImpl implements ReseñaServicio {

    private final ReseñaRepo reseñaRepo;

    @Autowired
    public ReseñaServicioImpl(ReseñaRepo reseñaRepo) {
        this.reseñaRepo = reseñaRepo;
    }

    @Override
    public String crearReseña(ReseñaDTO reseñaDTO) throws Exception {
        // Convertir los Strings idEvento e idUsuario a ObjectId
        String idEvento = reseñaDTO.idEvento();
        String idUsuario = reseñaDTO.idUsuario();

        // Crear una nueva entidad Reseña a partir del DTO
        Reseña nuevaReseña = new Reseña();
        nuevaReseña.setIdEvento(idEvento);
        nuevaReseña.setIdUsuario(idUsuario);
        nuevaReseña.setCalificacion(reseñaDTO.calificacion());
        nuevaReseña.setComentario(reseñaDTO.comentario());
        nuevaReseña.setFechaCreacion(reseñaDTO.fechaCreacion());

        // Guardar la reseña en la base de datos
        reseñaRepo.save(nuevaReseña);
        return "La reseña fue creada con éxito";
    }


    @Override
    public List<ReseñaDTO> obtenerReseñasPorEvento(String idEvento) throws Exception {
        // Obtener las reseñas del evento por id
        List<Reseña> reseñas = reseñaRepo.findByIdEvento(idEvento);

        // Convertir las entidades Reseña a DTO
        return reseñas.stream().map(reseña -> new ReseñaDTO(
                reseña.getIdEvento(),
                reseña.getIdUsuario(),
                reseña.getCalificacion(),
                reseña.getComentario(),
                reseña.getFechaCreacion()
        )).collect(Collectors.toList());
    }

    @Override
    public List<ReseñaDTO> obtenerReseñasPorUsuario(String idUsuario) throws Exception {
        // Obtener las reseñas del usuario por id
        List<Reseña> reseñas = reseñaRepo.findByIdUsuario(idUsuario);

        // Convertir las entidades Reseña a DTO
        return reseñas.stream().map(reseña -> new ReseñaDTO(
                reseña.getIdEvento(),
                reseña.getIdUsuario(),
                reseña.getCalificacion(),
                reseña.getComentario(),
                reseña.getFechaCreacion()
        )).collect(Collectors.toList());
    }

}


