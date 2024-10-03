package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.dto.EventoDTOs.*;
import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoEvento;
import co.edu.uniquindio.UniEventos.modelo.vo.Localidad;
import co.edu.uniquindio.UniEventos.repositorios.EventoRepo;
import co.edu.uniquindio.UniEventos.servicios.interfaces.EventoServicio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventoServicioImpl implements EventoServicio {

    private final EventoRepo eventoRepo;

    public EventoServicioImpl(EventoRepo eventoRepo) {
        this.eventoRepo = eventoRepo;
    }

    @Override
    public String crearEvento(CrearEventoDTO crearEventoDTO) throws Exception {

        // Validaciones previas
        if (existeNombreEvento(crearEventoDTO.nombre())) {
            throw new Exception("Ya existe un evento con este nombre");
        }

        // Crear una nueva instancia de Evento
        Evento nuevoEvento = new Evento();
        nuevoEvento.setNombre(crearEventoDTO.nombre());
        nuevoEvento.setDireccion(crearEventoDTO.direccion());
        nuevoEvento.setCiudad(crearEventoDTO.ciudad());
        nuevoEvento.setDescripcion(crearEventoDTO.descripcion());
        nuevoEvento.setTipo(crearEventoDTO.tipo());
        nuevoEvento.setFecha(crearEventoDTO.fecha());
        nuevoEvento.setImagenPortada(crearEventoDTO.imagenPoster());
        nuevoEvento.setImagenLocalidades(crearEventoDTO.imagenLocalidades());

        // Procesar las localidades
        List<Localidad> localidades = crearEventoDTO.localidades().stream().map(localidadDTO -> {
            Localidad localidad = new Localidad();
            localidad.setNombre(localidadDTO.getNombre());
            localidad.setPrecio(localidadDTO.getPrecio());
            localidad.setCapacidadMaxima(localidadDTO.getCapacidadMaxima());
            return localidad;
        }).collect(Collectors.toList());;

        nuevoEvento.setLocalidades(localidades);

        // Guardar el nuevo evento en la base de datos
        Evento eventoCreado = eventoRepo.save(nuevoEvento);

        return eventoCreado.getId();
    }

    @Override
    public String editarEvento(EditarEventoDTO evento) throws Exception {

        Evento eventoExistente = obtenerEvento(evento.id());

        eventoExistente.setNombre(evento.nombre());
        eventoExistente.setDescripcion(evento.descripcion());
        eventoExistente.setDireccion(evento.direccion());
        eventoExistente.setCiudad(evento.ciudad());
        eventoExistente.setFecha(evento.fecha());
        eventoExistente.setTipo(evento.tipo());
        eventoExistente.setImagenPortada(evento.imagenPoster());
        eventoExistente.setImagenLocalidades(evento.imagenLocalidades());
        eventoExistente.setLocalidades(evento.localidades());

        eventoRepo.save(eventoExistente);

        return eventoExistente.getId();
    }

    @Override
    public String eliminarEvento(String id) throws Exception {

        Evento eventoExistente = obtenerEvento(id);
        eventoExistente.setEstado(EstadoEvento.INACTIVO);

        eventoRepo.save(eventoExistente);

        return "Eliminado";
    }

    @Override
    public List<ItemEventoDTO> filtrarEventos(FiltroEventoDTO filtroEventoDTO) throws Exception {

        // Obtener los parámetros de filtro del DTO
        String nombre = filtroEventoDTO.getNombre();
        String ciudad = filtroEventoDTO.getCiudad();
        String tipo = filtroEventoDTO.getTipo();
        LocalDate fechaInicio = filtroEventoDTO.getFechaInicio();
        LocalDate fechaFin = filtroEventoDTO.getFechaFin();

        // Realizar la búsqueda aplicando los filtros dinámicamente
        List<Evento> eventosFiltrados = eventoRepo.findAll().stream()
                .filter(evento -> (nombre == null || evento.getNombre().toLowerCase().contains(nombre.toLowerCase())))
                .filter(evento -> (ciudad == null || evento.getCiudad().toLowerCase().equals(ciudad.toLowerCase())))
                .filter(evento -> (tipo == null || evento.getTipo().equalsIgnoreCase(tipo)))
                .filter(evento -> (fechaInicio == null || !evento.getFecha().isBefore(fechaInicio)))
                .filter(evento -> (fechaFin == null || !evento.getFecha().isAfter(fechaFin)))
                .collect(Collectors.toList());

        // Convertir los eventos filtrados a DTOs
        return eventosFiltrados.stream()
                .map(evento -> new ItemEventoDTO(
                        evento.getId(),
                        evento.getNombre(),
                        evento.getFecha(),
                        evento.getTipo()))
                .collect(Collectors.toList());
    }


    @Override
    public List<ItemEventoDTO> listarEventos() throws Exception{
        List<Evento> eventos = eventoRepo.findAll();
        return eventos.stream()
                .map(evento -> new ItemEventoDTO(
                        evento.getId(),
                        evento.getNombre(),
                        evento.getFecha(),
                        evento.getTipo()))
                .collect(Collectors.toList());
    }

    @Override
    public InformacionEventoDTO obtenerInformacionEvento(String id) throws Exception {
        Evento eventoExistente = obtenerEvento(id);
        return new InformacionEventoDTO(
                eventoExistente.getId(),
                eventoExistente.getNombre(),
                eventoExistente.getDescripcion(),
                eventoExistente.getDireccion(),
                eventoExistente.getCiudad(),
                eventoExistente.getFecha(),
                eventoExistente.getTipo(),
                eventoExistente.getImagenPortada(),
                eventoExistente.getImagenLocalidades(),
                eventoExistente.getLocalidades());
    }

    @Override
    public Evento obtenerEvento(String id) throws Exception {
        Optional<Evento> eventoOptional = eventoRepo.findById(id);

        if (eventoOptional.isEmpty()) {
            throw new Exception("El evento con el id: " + id + " no existe");
        }

        return eventoOptional.get();
    }

    private boolean existeNombreEvento(String nombre) {
        return eventoRepo.buscarPorNombre(nombre).isPresent();
    }
}

