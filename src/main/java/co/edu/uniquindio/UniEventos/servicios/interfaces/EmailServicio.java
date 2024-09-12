package co.edu.uniquindio.UniEventos.servicios.interfaces;

import co.edu.uniquindio.UniEventos.dto.EmailDTOs.EmailDTO;

public interface EmailServicio {
    void enviarCorreo (EmailDTO emailDTO) throws Exception;
}
