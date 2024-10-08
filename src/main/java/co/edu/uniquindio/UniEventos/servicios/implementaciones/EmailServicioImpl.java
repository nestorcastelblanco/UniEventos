package co.edu.uniquindio.UniEventos.servicios.implementaciones;

import co.edu.uniquindio.UniEventos.servicios.interfaces.EmailServicio;
import co.edu.uniquindio.UniEventos.dto.EmailDTOs.EmailDTO;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailServicioImpl implements EmailServicio {


    @Override
    @Async
    public void enviarCorreo(EmailDTO emailDTO) throws Exception {

        String destinatarioLimpio = emailDTO.destinatario().trim().replace("\"", "");

        Email email = EmailBuilder.startingBlank()
                .from("unieventos.uniquindio@gmail.com")
                .to(destinatarioLimpio)
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())
                .buildEmail();

        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "unieventos.uniquindio@gmail.com", "bngo ktya tpae mzec") // Correo como usuario SMTP
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            mailer.sendMail(email);
        }
    }


    public void enviarCorreoPago(EmailDTO emailDTO, File archivoAdjunto) throws Exception {
        // Limpiar el destinatario para evitar caracteres indeseados
        String destinatarioLimpio = emailDTO.destinatario().trim().replace("\"", "");

        // Crear un DataSource a partir del archivo adjunto
        DataSource archivoDataSource = new FileDataSource(archivoAdjunto);

        // Construir el correo
        Email email = EmailBuilder.startingBlank()
                .from("unieventos.uniquindio@gmail.com")
                .to(destinatarioLimpio)
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())  // Texto plano (puedes cambiar a HTML si lo necesitas)
                .withAttachment(archivoAdjunto.getName(), archivoDataSource)  // Agregar el archivo adjunto como DataSource
                .buildEmail();

        // Configurar el mailer y enviar el correo
        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "unieventos.uniquindio@gmail.com", "bngo ktya tpae mzec")
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            // Enviar el correo
            mailer.sendMail(email);
        }
    }
}
