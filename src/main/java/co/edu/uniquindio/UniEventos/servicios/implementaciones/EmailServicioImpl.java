package co.edu.uniquindio.UniEventos.servicios.implementaciones;


import co.edu.uniquindio.UniEventos.dto.EmailDTOs.EmailRecuperacionDTO;
import co.edu.uniquindio.UniEventos.servicios.interfaces.EmailServicio;
import co.edu.uniquindio.UniEventos.dto.EmailDTOs.EmailDTO;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    @Override
    @Async
    public void enviarCorreoRecuperacion(String correo_destino) throws Exception {

        String destinatarioLimpio = correo_destino.trim().replace("\"", "");

        Email email = EmailBuilder.startingBlank()
                .from("unieventos.uniquindio@gmail.com")
                .to(destinatarioLimpio)
                .withSubject("Codigo de recuperacion de contraseña de Unieventos")
                .withPlainText("000000")
                .buildEmail();

        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "unieventos.uniquindio@gmail.com", "bngo ktya tpae mzec") // Correo como usuario SMTP
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            mailer.sendMail(email);
        }
    }
}
