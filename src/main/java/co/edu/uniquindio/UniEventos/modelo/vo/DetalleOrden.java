package co.edu.uniquindio.UniEventos.modelo.vo;

import co.edu.uniquindio.UniEventos.dto.OrdenDTOs.CrearOrdenDTO;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class DetalleOrden {

    private ObjectId idDetalleOrden;
    private ObjectId idEvento;
    private float precio;
    private String nombreLocalidad;
    private int cantidad;

    public static DetalleOrden fromDTO(CrearOrdenDTO.ItemDTO itemDTO) {
        return DetalleOrden.builder()
                .idDetalleOrden(ObjectId.get()) // Asigna un nuevo ObjectId para idDetalleOrden
                .idEvento(new ObjectId(itemDTO.idEvento())) // Convierte el String a ObjectId
                .precio(itemDTO.precio())
                .nombreLocalidad(itemDTO.nombreLocalidad())
                .cantidad(itemDTO.cantidad())
                .build();
    }

    public static List<DetalleOrden> fromDTOList(List<CrearOrdenDTO.ItemDTO> itemDTOList) {
        List<DetalleOrden> detalleOrdenList = new ArrayList<>();

        for (CrearOrdenDTO.ItemDTO itemDTO : itemDTOList) {
            DetalleOrden detalleOrden = fromDTO(itemDTO);
            detalleOrdenList.add(detalleOrden);
        }

        return detalleOrdenList;
    }
}
