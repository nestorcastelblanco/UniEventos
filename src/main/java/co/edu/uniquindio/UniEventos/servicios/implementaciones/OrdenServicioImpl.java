package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.dto.EmailDTOs.EmailDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.CrearOrdenDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.InformacionOrdenCompraDTO;
import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.ItemOrdenDTO;
import co.edu.uniquindio.UniEventos.modelo.documentos.Evento;
import co.edu.uniquindio.UniEventos.modelo.documentos.Orden;
import co.edu.uniquindio.UniEventos.modelo.enums.EstadoOrden;
import co.edu.uniquindio.UniEventos.modelo.vo.DetalleCarrito;
import co.edu.uniquindio.UniEventos.modelo.vo.DetalleOrden;
import co.edu.uniquindio.UniEventos.modelo.vo.Localidad;
import co.edu.uniquindio.UniEventos.modelo.vo.Pago;
import co.edu.uniquindio.UniEventos.repositorios.OrdenRepo;
import co.edu.uniquindio.UniEventos.servicios.interfaces.EventoServicio;
import co.edu.uniquindio.UniEventos.servicios.interfaces.OrdenServicio;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import jakarta.mail.internet.MimeMessage;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class OrdenServicioImpl implements OrdenServicio {

    private final OrdenRepo ordenRepo;
    private final EventoServicio eventoServicio;

    public OrdenServicioImpl(OrdenRepo ordenRepo, EventoServicio eventoServicio) {
        this.ordenRepo = ordenRepo;
        this.eventoServicio = eventoServicio;
    }

    @Override
    public String crearOrden(CrearOrdenDTO orden) throws Exception {
        // Verificar si ya existe una orden con el mismo ID
        if (existeOrden(String.valueOf(orden.id()))) {
            throw new Exception("Ya existe una orden con este código");
        }

        // Convertir de List<DetalleOrden> a List<DetalleCarrito>
        List<DetalleCarrito> itemsConvertidos = convertirDetalleOrdenACarrito(orden.items());

        // Crear una nueva instancia de Orden
        Orden nuevaOrden = new Orden();
        nuevaOrden.setIdCliente(orden.id());
        nuevaOrden.setFecha(LocalDateTime.now());
        nuevaOrden.setCodigoPasarela(orden.codigoPasarela());
        nuevaOrden.setItems(itemsConvertidos);  // Asignar los items convertidos
        nuevaOrden.setPago(orden.pago());
        nuevaOrden.setTotal(orden.total());
        nuevaOrden.setIdCupon(orden.idCupon());
        nuevaOrden.setEstado(EstadoOrden.DISPONIBLE);

        // Guardar la nueva orden en el repositorio y devolver su ID
        Orden ordenCreada = ordenRepo.save(nuevaOrden);
        return "La orden fue creada";
    }



    private boolean existeOrden(String id) {
        return ordenRepo.buscarOrdenPorId(id).isPresent();
    }

    @Override
    public String cancelarOrden(String idOrden) throws Exception {
        Orden orden = obtenerOrden(idOrden);
        orden.setEstado(EstadoOrden.CANCELADA);
        ordenRepo.save(orden);
        return "Orden cancelada";
    }

    @Override
    public List<Orden> ordenesUsuario(ObjectId idUsuario) throws Exception {
        if (ordenRepo.findByIdCliente(idUsuario).isEmpty()) {
            throw new Exception("No existe una orden");
        }
        return ordenRepo.findByIdCliente(idUsuario);
    }

    private Orden obtenerOrden(String idOrden) throws Exception {
        Optional<Orden> ordenOptional = ordenRepo.findById(idOrden);
        if (ordenOptional.isEmpty()) {
            throw new Exception("La orden con el id: " + idOrden + " no existe");
        }
        return ordenOptional.get();
    }

    @Override
    public List<ItemOrdenDTO> obtenerHistorialOrdenes(String idCuenta) throws Exception {
        ObjectId objectId = new ObjectId(idCuenta);
        List<Orden> ordenes = ordenRepo.findByIdCliente(objectId);

        if (ordenes.isEmpty()) {
            throw new Exception("No se encontraron órdenes para la cuenta proporcionada");
        }

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
        Optional<Orden> ordenOptional = ordenRepo.buscarOrdenPorId(idOrden);
        if (ordenOptional.isEmpty()) {
            throw new Exception("La orden con el id: " + idOrden + " no existe");
        }

        Orden orden = obtenerOrden(idOrden);
        // Asegurarse de que getItems() devuelve List<DetalleOrden>
        List<DetalleOrden> itemsOrden = convertirDetalleCarritoAOrden(orden.getItems());

        return new InformacionOrdenCompraDTO(
                orden.getIdCliente(),
                orden.getFecha(),
                orden.getCodigoPasarela(),
                itemsOrden,
                orden.getPago(),
                orden.getId(),
                orden.getTotal(),
                orden.getIdCupon(),
                orden.getEstado()
        );
    }


    public String generarQR(String codigoOrden) throws Exception {
        String qrText = "Orden ID: " + codigoOrden;
        int width = 300;
        int height = 300;
        String fileType = "png";

        String qrFileName = "qr_" + codigoOrden + ".png";
        Path path = Paths.get("src/main/resources/qrCodes/" + qrFileName);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToPath(bitMatrix, fileType, path);

        return path.toString();
    }


    public void enviarCorreoOrden(String idOrden, String emailCliente) throws Exception {
        // Generar QR
        String qrFilePath = generarQR(idOrden);
        EmailServicioImpl emailServicio = new EmailServicioImpl();

        String correoContenido = "<p>Hola,</p>" +
                "<p>Gracias por tu compra. A continuación te enviamos los detalles de tu orden y el código QR:</p>" +
                "<p>Orden ID: " + idOrden + "</p>" +
                "<p>Adjunto encontrarás tu código QR para el evento.</p>";

        emailServicio.enviarCorreo(new EmailDTO("Detalles Compra", correoContenido, emailCliente));


    }


    @Override
    public Preference realizarPago(String idOrden) throws Exception {

        Orden ordenGuardada = obtenerOrden(idOrden);
        List<PreferenceItemRequest> itemsPasarela = new ArrayList<>();
        for (DetalleOrden item : convertirDetalleCarritoAOrden(ordenGuardada.getItems())) {

            Evento evento = eventoServicio.obtenerEvento(item.getId().toString());
            Localidad localidad = evento.obtenerLocalidad(item.getNombreLocalidad());

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
        MercadoPagoConfig.setAccessToken("APP_USR-4368524607273593-100311-0cffd4069075c573924d2bcbc08042b7-518706539");


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

    public List<DetalleOrden> convertirDetalleCarritoAOrden(List<DetalleCarrito> detallesCarrito) {
        List<DetalleOrden> detallesOrden = new ArrayList<>();
        for (DetalleCarrito detalle : detallesCarrito) {
            DetalleOrden detalleOrden = new DetalleOrden();
            detalleOrden.setIdEvento(detalle.getIdEvento());
            detalleOrden.setCantidad(detalle.getCantidad());
            // Asigna otros campos que sean necesarios
            detallesOrden.add(detalleOrden);
        }
        return detallesOrden;
    }

    public List<DetalleCarrito> convertirDetalleOrdenACarrito(List<DetalleOrden> detallesOrden) {
        List<DetalleCarrito> detallesCarrito = new ArrayList<>();
        for (DetalleOrden detalle : detallesOrden) {
            DetalleCarrito detalleCarrito = new DetalleCarrito();
            detalleCarrito.setIdEvento(detalle.getIdEvento());
            detalleCarrito.setCantidad(detalle.getCantidad());
            // Asigna otros campos necesarios
            detallesCarrito.add(detalleCarrito);
        }
        return detallesCarrito;
    }

}
