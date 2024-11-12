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

    private String idDetalleOrden;
    private String idEvento;
    private float precio;
    private String nombreLocalidad;
    private int cantidad;

    public static DetalleOrden fromDTO(CrearOrdenDTO.ItemDTO itemDTO) {
        return DetalleOrden.builder()
                .idDetalleOrden(String.valueOf(ObjectId.get()))
                .idEvento(String.valueOf(new ObjectId(itemDTO.idEvento())))
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
