package ua.com.owu.homework.service;

import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;
import ua.com.owu.homework.model.Car;
import ua.com.owu.homework.repository.CarRepository;

import java.util.List;

@Service
public class CarService {
    private static final Logger log = LoggerFactory.getLogger(CarService.class);
    private final CarRepository repository;
    private final JavaMailSender mailSender;
    private final String notificationEmail;
    private final String senderEmail;

    public CarService(CarRepository repository, JavaMailSender mailSender,
                      @Value("${app.mail.to}") String notificationEmail,
                      @Value("${app.mail.from}") String senderEmail) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.notificationEmail = notificationEmail;
        this.senderEmail = senderEmail;
    }

    public List<Car> findAll() { return repository.findAll(); }

    public Car findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
    }

    public Car save(Car car) {
        if (car.getId() == null) {
            car.setId(repository.findAll().stream()
                    .map(Car::getId)
                    .filter(java.util.Objects::nonNull)
                    .max(Long::compareTo)
                    .orElse(0L) + 1);
        }
        Car saved = repository.save(car);
        sendNotification("Car saved", saved);
        return saved;
    }

    public Car update(Long id, Car update) {
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

    public void delete(Long id) {
        Car car = findById(id);
        repository.delete(car);
        sendNotification("Car deleted", car);
    }

    public int deleteCarsWithPowerBelow(Integer power) {
        List<Car> cars = repository.findByPowerLessThan(power);
        repository.deleteAll(cars);
        cars.forEach(car -> sendNotification("Car deleted", car));
        return cars.size();
    }

    private void sendNotification(String subject, Car car) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(notificationEmail);
        message.setSubject(subject);
        message.setText("id: " + car.getId()
                + "\nmodel: " + car.getModel()
                + "\nproducer: " + car.getProducer()
                + "\npower: " + car.getPower());
        try {
            mailSender.send(message);
        } catch (MailException exception) {
            log.warn("Car was saved, but notification email could not be sent", exception);
        }
    }
}
