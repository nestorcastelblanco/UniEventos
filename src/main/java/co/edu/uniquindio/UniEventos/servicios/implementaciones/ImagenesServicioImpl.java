package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.servicios.interfaces.ImagenesServicio;
import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
@Service
public class ImagenesServicioImpl implements ImagenesServicio {


    @Override
    public String subirImagen(MultipartFile multipartFile) throws Exception{

        Bucket bucket = StorageClient.getInstance().bucket();

        String fileName = String.format( "%s-%s", UUID.randomUUID(), multipartFile.getOriginalFilename() );

        try {
            // Cargar la imagen en Firebase Storage
            Blob blob = bucket.create(fileName, multipartFile.getInputStream(), multipartFile.getContentType());

            // Retornar la URL de la imagen
            return String.format(
                    "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    bucket.getName(),
                    blob.getName()
            );
        } catch (IOException e) {
            throw new Exception("Error al subir la imagen: " + e.getMessage());
        }
    }

    @Override
    public void eliminarImagen(String nombreImagen) throws Exception{
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.get(nombreImagen);
        blob.delete();
    }


}
