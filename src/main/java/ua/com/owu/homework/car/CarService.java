package ua.com.owu.homework.car;

import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CarService {
    private final CarRepository repository;
    private final JavaMailSender mailSender;
    private final String notificationEmail;

    public CarService(CarRepository repository, JavaMailSender mailSender,
                      @Value("${app.mail.to}") String notificationEmail) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.notificationEmail = notificationEmail;
    }

    public List<Car> findAll() { return repository.findAll(); }

    public Car findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
    }

    public Car save(Car car) {
        Car saved = repository.save(car);
        sendNotification("Car saved", saved);
        return saved;
    }

    public Car update(String id, Car update) {
        Car car = findById(id);
        car.setModel(update.getModel());
        car.setProducer(update.getProducer());
        car.setPower(update.getPower());
        if (update.getPhoto() != null) {
            car.setPhoto(update.getPhoto());
            car.setPhotoName(update.getPhotoName());
            car.setPhotoContentType(update.getPhotoContentType());
        }
        return save(car);
    }

    public void delete(String id) {
        Car car = findById(id);
        repository.delete(car);
        sendNotification("Car deleted", car);
    }

    private void sendNotification(String subject, Car car) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificationEmail);
        message.setSubject(subject);
        message.setText("id: " + car.getId()
                + "\nmodel: " + car.getModel()
                + "\nproducer: " + car.getProducer()
                + "\npower: " + car.getPower());
        mailSender.send(message);
    }
}
