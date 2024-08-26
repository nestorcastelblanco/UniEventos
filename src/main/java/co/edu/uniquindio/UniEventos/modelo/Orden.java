package co.edu.uniquindio.UniEventos.modelo;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

public class Orden {
    private ObjectId idCliente;
    private LocalDateTime fecha;
    private String codigoPasarela;
    private List<DetalleOrden> items;
    private Pago pago;
    private String id;
    private float total;
    private ObjectId idCupon;
}
