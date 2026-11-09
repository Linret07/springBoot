package ua.com.owu.homework.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ua.com.owu.homework.model.Car;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final String recipient;
    private final String sender;

    public MailService(JavaMailSender mailSender,
                       @Value("${app.mail.to}") String recipient,
                       @Value("${app.mail.from}") String sender) {
        this.mailSender = mailSender;
        this.recipient = recipient;
        this.sender = sender;
    }

    public void sendCarNotification(String subject, Car car) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText("id: " + car.getId()
                + "\nmodel: " + car.getModel()
                + "\nproducer: " + car.getProducer()
                + "\npower: " + car.getPower());
        try {
            mailSender.send(message);
        } catch (MailException exception) {
            log.warn("Could not send car notification", exception);
        }
    }
}
