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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CarService {
    private static final Logger log = LoggerFactory.getLogger(CarService.class);
    private final CarRepository repository;
    private final JavaMailSender mailSender;
    private final String notificationEmail;
    private final String senderEmail;
    private final Path uploadDirectory;

    public CarService(CarRepository repository, JavaMailSender mailSender,
                      @Value("${app.mail.to}") String notificationEmail,
                      @Value("${app.mail.from}") String senderEmail,
                      @Value("${app.upload-dir:uploads}") String uploadDirectory) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.notificationEmail = notificationEmail;
        this.senderEmail = senderEmail;
        this.uploadDirectory = Paths.get(uploadDirectory).toAbsolutePath().normalize();
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
        String oldPhotoFileName = car.getPhotoFileName();
        car.setModel(update.getModel());
        car.setProducer(update.getProducer());
        car.setPower(update.getPower());
        if (update.getPhotoFileName() != null) {
            car.setPhotoFileName(update.getPhotoFileName());
            car.setPhotoUrl(update.getPhotoUrl());
            car.setPhotoOriginalName(update.getPhotoOriginalName());
            car.setPhotoContentType(update.getPhotoContentType());
        }
        Car saved = save(car);
        if (update.getPhotoFileName() != null && oldPhotoFileName != null
                && !oldPhotoFileName.equals(update.getPhotoFileName())) {
            deletePhotoFile(oldPhotoFileName);
        }
        return saved;
    }

    public void delete(Long id) {
        Car car = findById(id);
        repository.delete(car);
        deletePhotoFile(car.getPhotoFileName());
        sendNotification("Car deleted", car);
    }

    public int deleteCarsWithPowerBelow(Integer power) {
        List<Car> cars = repository.findByPowerLessThan(power);
        repository.deleteAll(cars);
        cars.forEach(car -> {
            deletePhotoFile(car.getPhotoFileName());
            sendNotification("Car deleted", car);
        });
        return cars.size();
    }

    private void deletePhotoFile(String fileName) {
        if (fileName == null) {
            return;
        }

        try {
            Path photoPath = uploadDirectory.resolve(fileName).normalize();
            if (photoPath.startsWith(uploadDirectory)) {
                Files.deleteIfExists(photoPath);
            }
        } catch (IOException exception) {
            log.warn("Could not delete photo file: {}", fileName, exception);
        }
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
