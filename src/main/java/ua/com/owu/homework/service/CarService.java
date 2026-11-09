package ua.com.owu.homework.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ua.com.owu.homework.model.Car;
import ua.com.owu.homework.repository.CarRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class CarService {
    private final CarRepository repository;
    private final MailService mailService;
    private final IdSequenceService idSequenceService;
    private final Path uploadDirectory;

    public CarService(CarRepository repository, MailService mailService,
                      IdSequenceService idSequenceService,
                      @Value("${app.upload-dir:uploads}") String uploadDirectory) {
        this.repository = repository;
        this.mailService = mailService;
        this.idSequenceService = idSequenceService;
        this.uploadDirectory = Paths.get(uploadDirectory).toAbsolutePath().normalize();
        try { Files.createDirectories(this.uploadDirectory); }
        catch (IOException exception) { throw new IllegalStateException("Cannot create upload directory", exception); }
    }

    public List<Car> findAll() { return repository.findAll(); }
    public List<Car> findByPower(Integer power) { return repository.findByPower(power); }
    public List<Car> findByProducer(String producer) { return repository.findByProducerIgnoreCase(producer); }

    public Car findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
    }

    public Photo getPhoto(Long id) {
        Car car = findById(id);
        if (car.getPhotoFileName() == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found");
        try {
            Path path = uploadDirectory.resolve(car.getPhotoFileName()).normalize();
            if (!path.startsWith(uploadDirectory)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid photo path");
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found");
            MediaType contentType = car.getPhotoContentType() == null
                    ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(car.getPhotoContentType());
            return new Photo(resource, contentType);
        } catch (MalformedURLException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found", exception);
        }
    }

    public Car save(Car car, MultipartFile photo) throws IOException {
        car.setId(idSequenceService.nextCarId());
        attachPhoto(car, photo);
        Car saved = repository.save(car);
        mailService.sendCarNotification("Car saved", saved);
        return saved;
    }

    public Car update(Long id, Car update, MultipartFile photo) throws IOException {
        Car car = findById(id);
        String oldPhotoFileName = car.getPhotoFileName();
        car.setModel(update.getModel()); car.setProducer(update.getProducer()); car.setPower(update.getPower());
        if (photo != null && !photo.isEmpty()) attachPhoto(car, photo);
        Car saved = repository.save(car);
        if (photo != null && !photo.isEmpty() && oldPhotoFileName != null && !oldPhotoFileName.equals(car.getPhotoFileName())) deletePhotoFile(oldPhotoFileName);
        mailService.sendCarNotification("Car updated", saved);
        return saved;
    }

    public void delete(Long id) {
        Car car = findById(id); repository.delete(car); deletePhotoFile(car.getPhotoFileName());
        mailService.sendCarNotification("Car deleted", car);
    }

    public int deleteCarsWithPowerBelow(Integer power) {
        List<Car> cars = repository.findByPowerLessThan(power); repository.deleteAll(cars);
        cars.forEach(car -> { deletePhotoFile(car.getPhotoFileName()); mailService.sendCarNotification("Car deleted", car); });
        return cars.size();
    }

    private void attachPhoto(Car car, MultipartFile photo) throws IOException {
        if (photo == null || photo.isEmpty()) return;
        String fileName = UUID.randomUUID() + getExtension(photo.getOriginalFilename());
        Path target = uploadDirectory.resolve(fileName).normalize();
        if (!target.startsWith(uploadDirectory)) throw new IOException("Invalid photo file path");
        Files.copy(photo.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        car.setPhotoFileName(fileName); car.setPhotoUrl("/uploads/" + fileName);
        car.setPhotoOriginalName(photo.getOriginalFilename()); car.setPhotoContentType(photo.getContentType());
    }

    private void deletePhotoFile(String fileName) {
        if (fileName == null) return;
        try {
            Path path = uploadDirectory.resolve(fileName).normalize();
            if (path.startsWith(uploadDirectory)) Files.deleteIfExists(path);
        } catch (IOException exception) { throw new IllegalStateException("Could not delete photo file", exception); }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null) return "";
        int dot = originalFilename.lastIndexOf('.'); return dot >= 0 ? originalFilename.substring(dot) : "";
    }

    public static class Photo {
        private final Resource resource; private final MediaType contentType;
        public Photo(Resource resource, MediaType contentType) { this.resource = resource; this.contentType = contentType; }
        public Resource getResource() { return resource; }
        public MediaType getContentType() { return contentType; }
    }
}
