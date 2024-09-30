package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.CrearOrdenDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.InformacionOrdenCompraDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.ItemOrdenDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;
import co.edu.uniquindio.UniEventos.modelo.documentos.Orden;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoOrden;
import co.edu.uniquindio.UniEventos.modelo.vo.DetalleOrden;
import co.edu.uniquindio.UniEventos.modelo.vo.Localidad;
import co.edu.uniquindio.UniEventos.modelo.vo.Pago;
import co.edu.uniquindio.UniEventos.repositorios.OrdenRepo;
import co.edu.uniquindio.UniEventos.servicios.interfaces.OrdenServicio;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.payment.Payment;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import com.mercadopago.resources.preference.Preference;

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
    @Override
    public Preference realizarPago(String idOrden) throws Exception {


        // Obtener la orden guardada en la base de datos y los ítems de la orden
        Orden ordenGuardada = obtenerOrden(idOrden);
        List<PreferenceItemRequest> itemsPasarela = new ArrayList<>();


        // Recorrer los items de la orden y crea los ítems de la pasarela
        for(DetalleOrden item : ordenGuardada.getItems()){


            // Obtener el evento y la localidad del ítem
            Evento evento = eventoServicio.obtenerEvento(item.getCodigoEvento().toString());
            Localidad localidad = evento.obtenerLocalidad(item.getNombreLocalidad());


            // Crear el item de la pasarela
            PreferenceItemRequest itemRequest =
                    PreferenceItemRequest.builder()
                            .id(evento.getId())
                            .title(evento.getNombre())
                            .pictureUrl(evento.getImagenPortada())
                            .categoryId(evento.getTipo().name())
                            .quantity(item.getCantidad())
                            .currencyId("COP")
                            .unitPrice(BigDecimal.valueOf(localidad.getPrecio()))
                            .build();


            itemsPasarela.add(itemRequest);
        }


        // Configurar las credenciales de MercadoPago
        MercadoPagoConfig.setAccessToken("ACCESS_TOKEN");


        // Configurar las urls de retorno de la pasarela (Frontend)
        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("URL PAGO EXITOSO")
                .failure("URL PAGO FALLIDO")
                .pending("URL PAGO PENDIENTE")
                .build();


        // Construir la preferencia de la pasarela con los ítems, metadatos y urls de retorno
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .backUrls(backUrls)
                .items(itemsPasarela)
                .metadata(Map.of("id_orden", ordenGuardada.getId()))
                .notificationUrl("URL NOTIFICACION")
                .build();


        // Crear la preferencia en la pasarela de MercadoPago
        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);


        // Guardar el código de la pasarela en la orden
        ordenGuardada.setCodigoPasarela( preference.getId() );
        ordenRepo.save(ordenGuardada);


        return preference;
    }

    @Override
    public void recibirNotificacionMercadoPago(Map<String, Object> request) {
        try {


            // Obtener el tipo de notificación
            Object tipo = request.get("type");


            // Si la notificación es de un pago entonces obtener el pago y la orden asociada
            if ("payment".equals(tipo)) {


                // Capturamos el JSON que viene en el request y lo convertimos a un String
                String input = request.get("data").toString();


                // Extraemos los números de la cadena, es decir, el id del pago
                String idPago = input.replaceAll("\\D+", "");


                // Se crea el cliente de MercadoPago y se obtiene el pago con el id
                PaymentClient client = new PaymentClient();
                Payment payment = client.get( Long.parseLong(idPago) );


                // Obtener el id de la orden asociada al pago que viene en los metadatos
                String idOrden = payment.getMetadata().get("id_orden").toString();


                // Se obtiene la orden guardada en la base de datos y se le asigna el pago
                Orden orden = obtenerOrden(idOrden);
                Pago pago = crearPago(payment);
                orden.setPago(pago);
                ordenRepo.save(orden);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Pago crearPago(Payment payment) {
        Pago pago = new Pago();
        pago.setId(payment.getId().toString());
        pago.setFecha( payment.getDateCreated().toLocalDateTime() );
        pago.setEstado(payment.getStatus());
        pago.setDetalleEstado(payment.getStatusDetail());
        pago.setTipoPago(payment.getPaymentTypeId());
        pago.setMoneda(payment.getCurrencyId());
        pago.setCodigoAutorizacion(payment.getAuthorizationCode());
        pago.setValorTransaccion(payment.getTransactionAmount().floatValue());
        return pago;
    }
}
