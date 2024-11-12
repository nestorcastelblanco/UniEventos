package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.dto.EmailDTOs.EmailDTO;
import co.edu.uniquindio.UniEventos.dto.EventoDTOs.*;
import co.edu.uniquindio.UniEventos.modelo.documentos.Cuenta;
import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoEvento;
import co.edu.uniquindio.UniEventos.modelo.enums.TipoCupon;
import co.edu.uniquindio.UniEventos.modelo.enums.TipoEvento;
import co.edu.uniquindio.UniEventos.modelo.vo.Localidad;
import co.edu.uniquindio.UniEventos.repositorios.CuentaRepo;
import co.edu.uniquindio.UniEventos.repositorios.EventoRepo;
import co.edu.uniquindio.UniEventos.servicios.interfaces.EmailServicio;
import co.edu.uniquindio.UniEventos.servicios.interfaces.EventoServicio;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventoServicioImpl implements EventoServicio {

    private final EventoRepo eventoRepo;
    private final EmailServicio emailServicio;
    private final CuentaRepo cuentaRepo;

    public EventoServicioImpl(EventoRepo eventoRepo, EmailServicio emailServicio, CuentaRepo cuentaRepo, CuentaRepo cuentaRepo1) {
        this.eventoRepo = eventoRepo;
        this.emailServicio = emailServicio;
        this.cuentaRepo = cuentaRepo1;
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

        // Establecer el estado inicial como ACTIVO
        nuevoEvento.setEstado(EstadoEvento.ACTIVO);

        // Procesar las localidades
        List<Localidad> localidades = crearEventoDTO.localidades().stream().map(localidadDTO -> {
            Localidad localidad = new Localidad();
            localidad.setNombreLocalidad(localidadDTO.getNombreLocalidad());
            localidad.setPrecio(localidadDTO.getPrecio());
            localidad.setCapacidadMaxima(localidadDTO.getCapacidadMaxima());
            return localidad;
        }).collect(Collectors.toList());

        nuevoEvento.setLocalidades(localidades);

        notificarUsuarios(nuevoEvento);

        // Guardar el nuevo evento en la base de datos
        Evento eventoCreado = eventoRepo.save(nuevoEvento);

        return eventoCreado.getId();
    }

    @Override
    public String editarEvento(EditarEventoDTO evento) throws Exception {

        Evento eventoExistente = obtenerEvento(new ObjectId(evento.id()));

        // Verificar que el evento esté ACTIVO antes de permitir la edición
        if (eventoExistente.getEstado() != EstadoEvento.ACTIVO) {
            throw new Exception("Solo se pueden editar eventos activos.");
        }

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

        Evento eventoExistente = obtenerEvento(new ObjectId(id));

        // Cambiar el estado del evento a INACTIVO en lugar de eliminarlo
        if (eventoExistente.getEstado() == EstadoEvento.INACTIVO) {
            throw new Exception("El evento ya está inactivo.");
        }

        eventoExistente.setEstado(EstadoEvento.INACTIVO);
        eventoRepo.save(eventoExistente);

        return "Eliminado";
    }

    @Override
    public List<EventoDTO> listarEventos() throws Exception {
        List<Evento> eventos = eventoRepo.findAll();
        return eventos.stream()
                .filter(evento -> evento.getEstado() == EstadoEvento.ACTIVO) // Solo listar eventos activos
                .map(evento -> new EventoDTO(
                        evento.getId(),
                        evento.getNombre(),
                        evento.getDireccion(),
                        evento.getCiudad(),
                        evento.getDescripcion(),
                        evento.getTipo(),
                        evento.getEstado(),
                        evento.getImagenPortada(), // Asegúrate de que este método retorne la imagen correcta
                        evento.getFecha(),
                        evento.getLocalidades(),  // Lista de localidades
                        evento.getImagenLocalidades() // Imagen de localidades
                ))
                .collect(Collectors.toList());
    }


    @Override
    public List<Evento> filtrarEventos(FiltroEventoDTO filtroEventoDTO) throws Exception {
        String nombre = filtroEventoDTO.nombre().isEmpty() ? null : filtroEventoDTO.nombre();
        String tipo = filtroEventoDTO.tipo().isEmpty() ? null : filtroEventoDTO.tipo();
        String ciudad = filtroEventoDTO.ciudad().isEmpty() ? null : filtroEventoDTO.ciudad();

        // Se llama al repositorio para filtrar eventos por los campos especificados
        return eventoRepo.filtrarEventosCar(nombre, tipo, ciudad, EstadoEvento.ACTIVO);
    }

    @Override
    public List<String> listarTiposEvento() {
        // Convertir los valores del enum a una lista de strings
        return Arrays.stream(TipoEvento.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public InformacionEventoDTO obtenerInformacionEvento(String id) throws Exception {
        Evento eventoExistente = obtenerEvento(new ObjectId(id));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        // Verificar que el evento esté activo antes de obtener la información
        if (eventoExistente.getEstado() != EstadoEvento.ACTIVO) {
            throw new Exception("El evento no está activo.");
        }

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
                eventoExistente.getLocalidades()
        );
    }

    @Override
    public Evento obtenerEvento(ObjectId id) throws Exception {
        Optional<Evento> eventoOptional = eventoRepo.buscarPorIdEvento(id);

        if (eventoOptional.isEmpty()) {
            throw new Exception("El evento con el id: " + id + " no existe");
        }

        return eventoOptional.get();
    }

    private boolean existeNombreEvento(String nombre) {
        return eventoRepo.buscarPorNombre(nombre).isPresent();
    }

    private void notificarUsuarios(Evento nuevoEvento) throws Exception {
        List<Cuenta> clientes = cuentaRepo.findAll();

        for (Cuenta cliente : clientes) {
            emailServicio.enviarCorreo(new EmailDTO("HAY UN NUEVO EVENTO DISPONIBLE EN UNIEVENTOS", "Entra a nuestra plataforma y descubre el nuevo evento disponible \n" + nuevoEvento.getNombre() +" \n"
                    + nuevoEvento.getCiudad()
                    +"\n" + nuevoEvento.getDescripcion() +"\n" + nuevoEvento.getFecha(), cliente.getEmail()));
        }
    }


    //METODOS DE PRUEBA JUNIT



    @Override
    public String crearEventoPrueba(CrearEventoDTO crearEventoDTO) throws Exception {

        // Validaciones previas
        if (existeNombreEventoPrueba(crearEventoDTO.nombre())) {
            throw new Exception("Ya existe un evento con este nombre");
        }
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenLocalidades());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
        System.out.println("url de la imagen de poster: " + crearEventoDTO.imagenPoster());
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

        // Establecer el estado inicial como ACTIVO
        nuevoEvento.setEstado(EstadoEvento.ACTIVO);

        // Procesar las localidades
        List<Localidad> localidades = crearEventoDTO.localidades().stream().map(localidadDTO -> {
            Localidad localidad = new Localidad();
            localidad.setNombreLocalidad(localidadDTO.getNombreLocalidad());
            localidad.setPrecio(localidadDTO.getPrecio());
            localidad.setCapacidadMaxima(localidadDTO.getCapacidadMaxima());
            return localidad;
        }).collect(Collectors.toList());

        nuevoEvento.setLocalidades(localidades);

        notificarUsuariosPrueba(nuevoEvento);

        // Guardar el nuevo evento en la base de datos
        Evento eventoCreado = eventoRepo.save(nuevoEvento);

        return eventoCreado.getId();
    }

    @Override
    public String editarEventoPrueba(EditarEventoDTO evento) throws Exception {

        Evento eventoExistente = obtenerEvento(new ObjectId(evento.id()));

        // Verificar que el evento esté ACTIVO antes de permitir la edición
        if (eventoExistente.getEstado() != EstadoEvento.ACTIVO) {
            throw new Exception("Solo se pueden editar eventos activos.");
        }

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
    public String eliminarEventoPrueba(String id) throws Exception {

        Evento eventoExistente = obtenerEvento(new ObjectId(id));

        // Cambiar el estado del evento a INACTIVO en lugar de eliminarlo
        if (eventoExistente.getEstado() == EstadoEvento.INACTIVO) {
            throw new Exception("El evento ya está inactivo.");
        }

        eventoExistente.setEstado(EstadoEvento.INACTIVO);
        eventoRepo.save(eventoExistente);

        return "Eliminado";
    }

    @Override
    public List<ItemEventoDTO> listarEventosPrueba() throws Exception {
        List<Evento> eventos = eventoRepo.findAll();
        return eventos.stream()
                .filter(evento -> evento.getEstado() == EstadoEvento.ACTIVO) // Solo listar eventos activos
                .map(evento -> new ItemEventoDTO(
                        evento.getId(),
                        evento.getNombre(),
                        evento.getFecha(),
                        evento.getTipo()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Evento> filtrarEventosPrueba(FiltroEventoDTO filtroEventoDTO) throws Exception {
        // Filtrar eventos activos
        return eventoRepo.filtrarEventos(filtroEventoDTO.nombre(), filtroEventoDTO.tipo(), filtroEventoDTO.ciudad())
                .stream().filter(evento -> evento.getEstado() == EstadoEvento.ACTIVO)
                .collect(Collectors.toList());
    }

    @Override
    public InformacionEventoDTO obtenerInformacionEventoPrueba(String id) throws Exception {
        Evento eventoExistente = obtenerEvento(new ObjectId(id));
        // Verificar que el evento esté activo antes de obtener la información
        if (eventoExistente.getEstado() != EstadoEvento.ACTIVO) {
            throw new Exception("El evento no está activo.");
        }

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
                eventoExistente.getLocalidades()
        );
    }

    @Override
    public Evento obtenerEventoPrueba(ObjectId id) throws Exception {
        Optional<Evento> eventoOptional = eventoRepo.buscarPorIdEvento(id);

        if (eventoOptional.isEmpty()) {
            throw new Exception("El evento con el id: " + id + " no existe");
        }

        return eventoOptional.get();
    }

    public boolean existeNombreEventoPrueba(String nombre) {
        return eventoRepo.buscarPorNombre(nombre).isPresent();
    }

    public void notificarUsuariosPrueba(Evento nuevoEvento) throws Exception {
        List<Cuenta> clientes = cuentaRepo.findAll();

        for (Cuenta cliente : clientes) {
            emailServicio.enviarCorreo(new EmailDTO("HAY UN NUEVO EVENTO DISPONIBLE EN UNIEVENTOS", "Entra a nuestra plataforma y descubre el nuevo evento disponible \n" + nuevoEvento.getNombre() +" \n"
                    + nuevoEvento.getCiudad()
                    +" \n " + nuevoEvento.getDescripcion() +" \n " + nuevoEvento.getFecha(), cliente.getEmail()));
        }
    }

}
