package co.edu.uniquindio.UniEventos.servicios.implementaciones;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.mercadopago.client.payment.*;
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
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.common.Address;
import com.mercadopago.resources.common.Identification;
import com.mercadopago.resources.common.Phone;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.preference.PreferencePayer;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static co.edu.uniquindio.UniEventos.modelo.vo.DetalleOrden.fromDTOList;

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
        if (existeOrden(new ObjectId(orden.idCliente()))) {
            throw new Exception("Ya existe una orden con este código");
        }

        List<DetalleCarrito> itemsConvertidos = convertirDetalleOrdenACarrito(orden.items());
        List<DetalleOrden> detallesOrden = DetalleOrden.fromDTOList(orden.items());

        Orden nuevaOrden = new Orden();
        nuevaOrden.setIdCliente(new ObjectId(orden.idCliente()));
        nuevaOrden.setFecha(LocalDateTime.now());
        nuevaOrden.setCodigoPasarela(orden.codigoPasarela());
        nuevaOrden.setItems(itemsConvertidos);
        nuevaOrden.setIdUsuario(new ObjectId(orden.idCliente()));
        nuevaOrden.setDetallesOrden(detallesOrden);
        nuevaOrden.setPago(
                new Pago(orden.pago().moneda(),
                        orden.pago().tipoPago(),
                        orden.pago().detalleEstado(),
                        orden.pago().codigoAutorizacion(),
                        orden.pago().fecha(),
                        orden.pago().idPago(),
                        orden.pago().valorTransaccion(),
                        orden.pago().estado()
                        ));
        nuevaOrden.setTotal(orden.total());
        nuevaOrden.setIdCupon(new ObjectId(orden.idCupon()));
        nuevaOrden.setEstado(EstadoOrden.DISPONIBLE);

        ordenRepo.save(nuevaOrden);
        return "La orden fue creada con éxito.";
    }

    private List<ObjectId> listaIDEventos(@NotNull(message = "Debe proporcionar al menos un ítem en la orden") List<CrearOrdenDTO.ItemDTO> items) {

        List<ObjectId> idsEventos = new ArrayList<>();
        for (CrearOrdenDTO.ItemDTO item : items) {
            idsEventos.add(new ObjectId(item.idEvento()));
        }
        return idsEventos;
    }


    private boolean existeOrden(ObjectId id) {
        return ordenRepo.buscarOrdenPorId(id).isPresent();
    }

    @Override
    public String cancelarOrden(ObjectId idOrden) throws Exception {
        Orden orden = obtenerOrden(idOrden);
        orden.setEstado(EstadoOrden.CANCELADA);
        ordenRepo.save(orden);
        return "Orden cancelada";
    }

    @Override
    public List<Orden> ordenesUsuario(ObjectId idUsuario) throws Exception {
        List<Orden> ordenes = ordenRepo.findByIdCliente(idUsuario);
        if (ordenes.isEmpty()) {
            throw new Exception("No existen órdenes para el usuario.");
        }
        return ordenes;
    }

    private Orden obtenerOrden(ObjectId idOrden) throws Exception {
        return ordenRepo.buscarOrdenPorId(idOrden)
                .orElseThrow(() -> new Exception("La orden con el id: " + idOrden + " no existe"));
    }

    @Override
    public List<ItemOrdenDTO> obtenerHistorialOrdenes() throws Exception {
        List<Orden> ordenes = ordenRepo.findAll();
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
        Orden orden = obtenerOrden(new ObjectId(idOrden));
        List<DetalleOrden> itemsOrden = convertirDetalleCarritoAOrden(orden.getItems());

        return new InformacionOrdenCompraDTO(
                orden.getIdCliente(),
                orden.getFecha(),
                orden.getCodigoPasarela(),
                itemsOrden,
                orden.getPago(),
                new ObjectId(orden.getId()),
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

    public void enviarCorreoOrden(ObjectId idOrden, String emailCliente) throws Exception {
        String qrFilePath = generarQR(String.valueOf(idOrden));
        EmailServicioImpl emailServicio = new EmailServicioImpl();

        String correoContenido = "<p>Hola,</p>" +
                "<p>Gracias por tu compra. A continuación te enviamos los detalles de tu orden y el código QR:</p>" +
                "<p>Orden ID: " + idOrden + "</p>" +
                "<p>Adjunto encontrarás tu código QR para el evento.</p>";

        emailServicio.enviarCorreo(new EmailDTO("Detalles Compra", correoContenido, emailCliente));
    }
    @Override
    public Preference realizarPago(String idOrden) throws Exception {

        // Obtener la orden guardada en la base de datos y los ítems de la orden
        Orden ordenGuardada = obtenerOrden(new ObjectId(idOrden));
        List<PreferenceItemRequest> itemsPasarela = new ArrayList<>();

        // Recorrer los items de la orden y crea los ítems de la pasarela
        for (DetalleOrden item : ordenGuardada.getDetallesOrden()) {

            Evento evento = eventoServicio.obtenerEvento(item.getIdEvento());
            Localidad localidad = evento.obtenerLocalidad(item.getNombreLocalidad());

            // Crear el item de la pasarela
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(evento.getId()).title(evento.getNombre())
                    .pictureUrl(null).categoryId(String.valueOf(evento.getTipo()))
                    .quantity(item.getCantidad())
                    .currencyId("COP").unitPrice(BigDecimal.valueOf(localidad.getPrecio())).build();

            itemsPasarela.add(itemRequest);
        }

        // Configurar las credenciales de MercadoPago
        MercadoPagoConfig.setAccessToken("APP_USR-8320315241588080-100615-13f8572024d95a653994a5ba03bc7c16-2019618896");

        // Configurar las urls de retorno de la pasarela (Frontend)
        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder().success("URL PAGO EXITOSO")
                .failure("URL PAGO FALLIDO").pending("URL PAGO PENDIENTE").build();

        // Construir la preferencia de la pasarela con los ítems, metadatos y urls de
        // retorno
        PreferenceRequest preferenceRequest = PreferenceRequest.builder().backUrls(backUrls).items(itemsPasarela)
                .metadata(Map.of("id_orden", ordenGuardada.getId()))
                .notificationUrl("https://f049-152-202-207-38.ngrok-free.app").build();

        // Crear la preferencia en la pasarela de MercadoPago
        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        // Guardar el código de la pasarela en la orden
        ordenGuardada.setId(preference.getId());
        ordenRepo.save(ordenGuardada);

        return preference;
    }

    public void recibirNotificacionMercadoPago(Map<String, Object> request) {
        try {
            String tipo = (String) request.get("type");
            if ("payment".equals(tipo)) {
                String input = request.get("data").toString();
                String idPago = input.replaceAll("\\D+", "");

                PaymentClient client = new PaymentClient();
                Payment payment = client.get(Long.parseLong(idPago));

                String idOrden = payment.getMetadata().get("id_orden").toString();
                Orden orden = obtenerOrden(new ObjectId(idOrden));
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
        pago.setIdPago(payment.getId().toString());
        pago.setFecha(payment.getDateCreated().toLocalDateTime());
        pago.setEstado(payment.getStatus());
        pago.setDetalleEstado(payment.getStatusDetail());
        pago.setTipoPago(payment.getPaymentTypeId());
        pago.setMoneda(payment.getCurrencyId());
        pago.setCodigoAutorizacion(payment.getAuthorizationCode());
        return pago;
    }

    private List<DetalleOrden> convertirDetalleCarritoAOrden(List<DetalleCarrito> items) {
        List<DetalleOrden> detallesOrden = new ArrayList<>();
        for (DetalleCarrito item : items) {
            DetalleOrden detalleOrden = new DetalleOrden();
            detalleOrden.setIdDetalleOrden(item.getIdDetalleCarrito());
            detalleOrden.setCantidad(item.getCantidad());
            detalleOrden.setNombreLocalidad(item.getNombreLocalidad());
            detallesOrden.add(detalleOrden);
        }
        return detallesOrden;
    }


    private List<DetalleCarrito> convertirDetalleOrdenACarrito(@NotNull(message = "Debe proporcionar al menos un ítem en la orden") List<CrearOrdenDTO.ItemDTO> items) {
        List<DetalleCarrito> detallesCarrito = new ArrayList<>();
        for (CrearOrdenDTO.ItemDTO item : items) {
            DetalleCarrito detalleCarrito = new DetalleCarrito();
            detalleCarrito.setIdDetalleCarrito(new ObjectId(item.idDetalleCarrito()));
            detalleCarrito.setCantidad(item.cantidad());
            detalleCarrito.setNombreLocalidad(item.nombreLocalidad());
            detallesCarrito.add(detalleCarrito);
        }
        return detallesCarrito;
    }
}
