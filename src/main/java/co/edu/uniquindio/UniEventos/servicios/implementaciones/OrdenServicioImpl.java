package co.edu.uniquindio.UniEventos.servicios.implementaciones;
import co.edu.uniquindio.UniEventos.modelo.documentos.Cuenta;
import co.edu.uniquindio.UniEventos.modelo.documentos.Cupon;
import co.edu.uniquindio.UniEventos.repositorios.CuentaRepo;
import co.edu.uniquindio.UniEventos.repositorios.CuponRepo;
import co.edu.uniquindio.UniEventos.repositorios.EventoRepo;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;

import java.io.File;
import java.io.FileNotFoundException;
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
import com.mercadopago.resources.payment.Payment;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class OrdenServicioImpl implements OrdenServicio {

    private final OrdenRepo ordenRepo;
    private final EventoServicio eventoServicio;
    private final CuponRepo cuponRepo;
    private final CuentaRepo cuentaRepo;
    private final EventoRepo eventoRepo;

    public OrdenServicioImpl(OrdenRepo ordenRepo, EventoServicio eventoServicio, CuponRepo cuponRepo, CuentaRepo cuentaRepo, EventoRepo eventoRepo, EventoRepo eventoRepo1) {
        this.ordenRepo = ordenRepo;
        this.eventoServicio = eventoServicio;
        this.cuponRepo = cuponRepo;
        this.cuentaRepo = cuentaRepo;
        this.eventoRepo = eventoRepo;
    }

    @Override
    public String crearOrden(CrearOrdenDTO orden) throws Exception {
        if (existeOrden(orden.idCliente())) {
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
        nuevaOrden.setPago(new Pago());
        nuevaOrden.setTotal(calcularTotal(orden.total(), orden.codigoCupon()));
        nuevaOrden.setIdCupon(orden.codigoCupon());
        nuevaOrden.setEstado(EstadoOrden.DISPONIBLE);

        ordenRepo.save(nuevaOrden);
        return "La orden fue creada con éxito.";
    }

    private float calcularTotal(@Min(value = 0, message = "El total debe ser mayor o igual a cero") float total, String codigoCupon) {
        float valor = total;

        // Verifica si el código de cupón es válido
        Optional<Cupon> cuponIngresado = cuponRepo.buscarCuponPorCodigo(codigoCupon);
        if (cuponIngresado.isPresent()) {
            Cupon cupon = cuponIngresado.get();

            // Aplica el descuento del cupón al valor total
            valor = valor - (valor * (cupon.getDescuento() / 100.0f));
        } else {
            System.out.println("Cupón no encontrado o no válido");
        }

        System.out.println("Valor postverificación cupón: " + valor);
        return valor;
    }

    private boolean existeOrden(String id) {
        return ordenRepo.buscarOrdenPorId(id).isPresent();
    }

    @Override
    public String cancelarOrden(String idOrden) throws Exception {
        Orden orden = obtenerOrden(idOrden);
        if (orden.getEstado() == EstadoOrden.PAGADA ) {
            throw new Exception("LA ORDEN NO PUEDE SER CANCELADA, YA FUE PAGADA");
        }
        if (orden.getEstado() == EstadoOrden.CANCELADA){
            throw new Exception("LA ORDEN NO PUEDE SER CANCELADA, YA FUE CANCELADA");
        }
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

    public Orden obtenerOrden(String idOrden) throws Exception {
        Optional<Orden> ordenOptional = ordenRepo.buscarOrdenPorObjectId(new ObjectId(idOrden));

        if (!ordenOptional.isPresent()) {
            throw new Exception("La orden con el id: " + idOrden + " no existe");
        }

        return ordenOptional.get();
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
        Orden orden = obtenerOrden(idOrden);
        List<DetalleOrden> itemsOrden = convertirDetalleCarritoAOrden(orden.getItems());

        return new InformacionOrdenCompraDTO(
                orden.getIdCliente(),
                orden.getFecha(),
                orden.getCodigoPasarela(),
                itemsOrden,
                orden.getPago(),
                orden.getOrdenId(),
                orden.getTotal(),
                orden.getIdCupon(),
                orden.getEstado()
        );
    }

    public String generarQR(String codigoOrden) throws Exception {
        // Texto del QR
        String qrText = "Orden ID: " + codigoOrden;
        // Configuraciones del QR
        int width = 300;
        int height = 300;
        String fileType = "png";

        // Nombre del archivo QR
        String qrFileName = "qr_" + codigoOrden + ".png";
        Path path = Paths.get("src/main/resources/qrCodes/" + qrFileName);

        // Crear el directorio si no existe
        File directory = new File("src/main/resources/qrCodes/");
        if (!directory.exists()) {
            directory.mkdirs();  // Crea los directorios necesarios
        }

        // Crear el QR
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, width, height);

        // Guardar el QR en un archivo
        MatrixToImageWriter.writeToPath(bitMatrix, fileType, path);

        // Devolver la ruta del archivo generado
        return path.toString();
    }

    public void enviarCorreoOrden(ObjectId idOrden, String emailCliente) throws Exception {
        // Generar QR
        String qrFilePath = generarQR(String.valueOf(idOrden));
        EmailServicioImpl emailServicio = new EmailServicioImpl();

        // Contenido del correo (formato de texto)
        String correoContenido = "Hola,\n" +
                "Gracias por tu compra. A continuación te enviamos los detalles de tu orden y el código QR:\n" +
                "Orden ID: " + idOrden + "\n" +
                "Adjunto encontrarás tu código QR para el evento.";

        // Preparar el archivo adjunto (QR generado)
        File archivoAdjunto = new File(qrFilePath);
        if (!archivoAdjunto.exists()) {
            throw new FileNotFoundException("No se encontró el archivo QR: " + qrFilePath);
        }

        // Enviar el correo con el archivo adjunto
        EmailDTO emailDTO = new EmailDTO("Detalles Compra", correoContenido, emailCliente);
        emailServicio.enviarCorreoPago(emailDTO, archivoAdjunto);
    }

    @Override
    public Preference realizarPago(String idOrden) throws Exception {

        // Obtener la orden guardada en la base de datos y los ítems de la orden
        Orden ordenGuardada = obtenerOrden(idOrden);
        List<PreferenceItemRequest> itemsPasarela = new ArrayList<>();

        if ( ordenGuardada.getEstado() == EstadoOrden.CANCELADA) {
            throw new Exception("LA ORDEN SELECCIONADA FUE CANCELADA");
        }

        if ( ordenGuardada.getEstado() == EstadoOrden.PAGADA) {
            throw new Exception("LA ORDEN SELECCIONADA YA FUE PAGADA ");
        }

        // Recorrer los items de la orden y crea los ítems de la pasarela
        for (DetalleOrden item : ordenGuardada.getDetallesOrden()) {

            Evento evento = eventoServicio.obtenerEvento(item.getIdEvento());
            List<Localidad> localidadesEventos = evento.getLocalidades();

            for (Localidad localidades : localidadesEventos){

                if ( item.getNombreLocalidad().equals( localidades.getNombreLocalidad()) ) {
                    if (item.getCantidad() + localidades.getEntradasVendidas() > localidades.getCapacidadMaxima()){
                        throw new Exception("Las entradas ingresadas no pueden ser compradas, ya que se cuenta con el aforo maximo");
                    }else{
                        localidades.setEntradasVendidas(item.getCantidad() + localidades.getEntradasVendidas());
                        eventoRepo.save(evento);
                    }
                }
            }

            // Crear el item de la pasarela
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(evento.getId()).title(evento.getNombre())
                    .pictureUrl(null).categoryId(String.valueOf(evento.getTipo()))
                    .quantity(1)
                    .currencyId("COP").unitPrice(BigDecimal.valueOf(ordenGuardada.getTotal())).build();

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
                .notificationUrl("https://d389-152-202-204-179.ngrok-free.app/api/publico/orden/notificacion-pago").build();

        // Crear la preferencia en la pasarela de MercadoPago
        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);
        // Guardar el código de la pasarela en la orden
        ordenGuardada.setCodigoPasarela(preference.getId());
        ordenRepo.save(ordenGuardada);

        return preference;
    }

    @Override
    public void recibirNotificacionMercadoPago(Map<String, Object> request) {
        try {
            String tipo = (String) request.get("type");
            if ("payment".equals(tipo)) {
                String input = request.get("data").toString();
                String idPago = input.replaceAll("\\D+", "");

                PaymentClient client = new PaymentClient();
                Payment payment = client.get(Long.parseLong(idPago));

                String idOrden = payment.getMetadata().get("id_orden").toString();
                Orden orden = obtenerOrden(idOrden);

                Optional<Cuenta> cuentaCliente = cuentaRepo.findById(orden.getIdCliente());
                Cuenta cuentaPago = cuentaCliente.get();

                Pago pago = crearPago(payment);
                pago.setValorTransaccion(orden.getTotal());
                orden.setEstado(EstadoOrden.PAGADA);
                orden.setPago(pago);
                enviarCorreoOrden(new ObjectId(idOrden), cuentaPago.getEmail());
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
        if(items == null) {
            return null;
        }
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

    //METODOS DE PRUEBA DE JUNIT




    @Override
    public String crearOrdenPrueba(CrearOrdenDTO orden) throws Exception {
        if (existeOrdenPrueba(orden.idCliente())) {
            throw new Exception("Ya existe una orden con este código");
        }

        List<DetalleCarrito> itemsConvertidos = convertirDetalleOrdenACarritoPrueba(orden.items());
        List<DetalleOrden> detallesOrden = DetalleOrden.fromDTOList(orden.items());

        Orden nuevaOrden = new Orden();
        nuevaOrden.setIdCliente(new ObjectId(orden.idCliente()));
        nuevaOrden.setFecha(LocalDateTime.now());
        nuevaOrden.setCodigoPasarela(orden.codigoPasarela());
        nuevaOrden.setItems(itemsConvertidos);
        nuevaOrden.setIdUsuario(new ObjectId(orden.idCliente()));
        nuevaOrden.setDetallesOrden(detallesOrden);
        nuevaOrden.setPago(new Pago());
        nuevaOrden.setTotal(calcularTotalPrueba(orden.total(), orden.codigoCupon()));
        nuevaOrden.setIdCupon(orden.codigoCupon());
        nuevaOrden.setEstado(EstadoOrden.DISPONIBLE);

        ordenRepo.save(nuevaOrden);
        return "La orden fue creada con éxito.";
    }

    private float calcularTotalPrueba(@Min(value = 0, message = "El total debe ser mayor o igual a cero") float total, String codigoCupon) {
        float valor = total;

        // Verifica si el código de cupón es válido
        Optional<Cupon> cuponIngresado = cuponRepo.buscarCuponPorCodigo(codigoCupon);
        if (cuponIngresado.isPresent()) {
            Cupon cupon = cuponIngresado.get();

            // Aplica el descuento del cupón al valor total
            valor = valor - (valor * (cupon.getDescuento() / 100.0f));
        } else {
            System.out.println("Cupón no encontrado o no válido");
        }

        System.out.println("Valor postverificación cupón: " + valor);
        return valor;
    }


    private List<ObjectId> listaIDEventosPrueba(@NotNull(message = "Debe proporcionar al menos un ítem en la orden") List<CrearOrdenDTO.ItemDTO> items) {

        List<ObjectId> idsEventos = new ArrayList<>();
        for (CrearOrdenDTO.ItemDTO item : items) {
            idsEventos.add(new ObjectId(item.idEvento()));
        }
        return idsEventos;
    }


    private boolean existeOrdenPrueba(String id) {
        return ordenRepo.buscarOrdenPorId(id).isPresent();
    }

    @Override
    public String cancelarOrdenPrueba(String idOrden) throws Exception {
        Orden orden = obtenerOrdenPrueba(idOrden);
        orden.setEstado(EstadoOrden.CANCELADA);
        ordenRepo.save(orden);
        return "Orden cancelada";
    }

    @Override
    public List<Orden> ordenesUsuarioPrueba(ObjectId idUsuario) throws Exception {
        List<Orden> ordenes = ordenRepo.findByIdCliente(idUsuario);
        if (ordenes.isEmpty()) {
            throw new Exception("No existen órdenes para el usuario.");
        }
        return ordenes;
    }

    public Orden obtenerOrdenPrueba(String idOrden) throws Exception {
        Optional<Orden> ordenOptional = ordenRepo.buscarOrdenPorId(idOrden);

        if (ordenOptional.isEmpty()) {
            throw new Exception("La orden con el id: " + idOrden + " no existe");
        }

        return ordenOptional.get();
    }

    @Override
    public List<ItemOrdenDTO> obtenerHistorialOrdenesPrueba() throws Exception {
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
    public InformacionOrdenCompraDTO obtenerInformacionOrdenPrueba(String idOrden) throws Exception {
        Orden orden = obtenerOrdenPrueba(idOrden);
        List<DetalleOrden> itemsOrden = convertirDetalleCarritoAOrdenPrueba(orden.getItems());

        return new InformacionOrdenCompraDTO(
                orden.getIdCliente(),
                orden.getFecha(),
                orden.getCodigoPasarela(),
                itemsOrden,
                orden.getPago(),
                orden.getOrdenId(),
                orden.getTotal(),
                orden.getIdCupon(),
                orden.getEstado()
        );
    }

    public String generarQRPrueba(String codigoOrden) throws Exception {
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

    public void enviarCorreoOrdenPrueba(ObjectId idOrden, String emailCliente) throws Exception {
        String qrFilePath = generarQRPrueba(String.valueOf(idOrden));
        EmailServicioImpl emailServicio = new EmailServicioImpl();

        String correoContenido = "<p>Hola,</p>" +
                "<p>Gracias por tu compra. A continuación te enviamos los detalles de tu orden y el código QR:</p>" +
                "<p>Orden ID: " + idOrden + "</p>" +
                "<p>Adjunto encontrarás tu código QR para el evento.</p>";

        emailServicio.enviarCorreo(new EmailDTO("Detalles Compra", correoContenido, emailCliente));
    }
    @Override
    public Preference realizarPagoPrueba(String idOrden) throws Exception {

        // Obtener la orden guardada en la base de datos y los ítems de la orden
        Orden ordenGuardada = obtenerOrdenPrueba(idOrden);
        List<PreferenceItemRequest> itemsPasarela = new ArrayList<>();

        // Recorrer los items de la orden y crea los ítems de la pasarela
        for (DetalleOrden item : ordenGuardada.getDetallesOrden()) {

            Evento evento = eventoServicio.obtenerEvento(item.getIdEvento());
            Localidad localidad = evento.obtenerLocalidad(item.getNombreLocalidad());

            // Crear el item de la pasarela
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(evento.getId()).title(evento.getNombre())
                    .pictureUrl(null).categoryId(String.valueOf(evento.getTipo()))
                    .quantity(1)
                    .currencyId("COP").unitPrice(BigDecimal.valueOf(ordenGuardada.getTotal())).build();

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
                .notificationUrl("https://ae41-189-50-209-152.ngrok-free.app/api/publico/orden/notificacion-pago").build();

        // Crear la preferencia en la pasarela de MercadoPago
        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        // Guardar el código de la pasarela en la orden
        ordenGuardada.setCodigoPasarela(preference.getId());
        ordenRepo.save(ordenGuardada);

        return preference;
    }

    @Override
    public void recibirNotificacionMercadoPagoPrueba(Map<String, Object> request) {
        String tipo = (String) request.get("type");
        if ("payment".equals(tipo)) {
            Map<String, Object> data = (Map<String, Object>) request.get("data");
            String idPago = (String) data.get("id"); // Asegúrate de que este sea un String
            String idOrden = (String) ((Map<String, Object>) data.get("metadata")).get("id_orden");

            Orden orden = ordenRepo.buscarOrdenPorId(idOrden).orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            // Aquí no debe haber conversión a número
            if (idPago == null) {
                throw new RuntimeException("ID de pago no recibido");
            }

            // Asignar ID de pago al objeto Orden
            orden.getPago().setIdPago(idPago); // No hay conversión a número aquí
            orden.getPago().setEstado("CONFIRMADO");
            ordenRepo.save(orden);
        }
    }


    private Pago crearPagoPrueba(Payment payment) {
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

    public List<DetalleOrden> convertirDetalleCarritoAOrdenPrueba(List<DetalleCarrito> items) {
        List<DetalleOrden> detallesOrden = new ArrayList<>();
        if(items == null) {
            return null;
        }
        for (DetalleCarrito item : items) {
            DetalleOrden detalleOrden = new DetalleOrden();
            detalleOrden.setIdDetalleOrden(item.getIdDetalleCarrito());
            detalleOrden.setCantidad(item.getCantidad());
            detalleOrden.setNombreLocalidad(item.getNombreLocalidad());
            detallesOrden.add(detalleOrden);
        }
        return detallesOrden;
    }


    public List<DetalleCarrito> convertirDetalleOrdenACarritoPrueba(@NotNull(message = "Debe proporcionar al menos un ítem en la orden") List<CrearOrdenDTO.ItemDTO> items) {
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