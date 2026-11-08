package ua.com.owu.homework.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ua.com.owu.homework.model.Car;
import ua.com.owu.homework.service.CarService;

@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService service;
    private final Path uploadDirectory;

    public CarController(
            CarService service,
            @Value("${app.upload-dir:uploads}") String uploadDirectory
    ) {
        this.service = service;
        this.uploadDirectory = Paths.get(uploadDirectory).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.uploadDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot create upload directory", exception);
        }
    }

    @GetMapping
    public List<Car> getCars() { return service.findAll(); }

    @GetMapping("/{id}")
    public Car getCar(@PathVariable Long id) { return service.findById(id); }

    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> getPhoto(@PathVariable Long id) {
        Car car = service.findById(id);

        if (car.getPhotoFileName() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path photoPath = uploadDirectory.resolve(car.getPhotoFileName()).normalize();

            if (!photoPath.startsWith(uploadDirectory)) {
                return ResponseEntity.badRequest().build();
            }

            Resource photo = new UrlResource(photoPath.toUri());
            if (!photo.exists() || !photo.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;
            if (car.getPhotoContentType() != null) {
                contentType = MediaType.parseMediaType(car.getPhotoContentType());
            }

            return ResponseEntity.ok().contentType(contentType).body(photo);
        } catch (MalformedURLException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/low-power")
    public Map<String, Object> deleteLowPowerCars() {
        int deleted = service.deleteCarsWithPowerBelow(100);
        Map<String, Object> response = new HashMap<>();
        response.put("deleted", deleted);
        response.put("condition", "power < 100");
        return response;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Car createCar(
            @RequestParam String model,
            @RequestParam String producer,
            @RequestParam Integer power,
            @RequestParam(required = false) MultipartFile photo
    ) throws IOException {
        return service.save(toCar(model, producer, power, photo));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Car updateCar(
            @PathVariable Long id,
            @RequestParam String model,
            @RequestParam String producer,
            @RequestParam Integer power,
            @RequestParam(required = false) MultipartFile photo
    ) throws IOException {
        return service.update(id, toCar(model, producer, power, photo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Car toCar(
            String model,
            String producer,
            Integer power,
            MultipartFile photo
    ) throws IOException {
        Car car = new Car(model, producer, power);

        if (photo != null && !photo.isEmpty()) {
            String storedFileName = UUID.randomUUID() + getExtension(photo.getOriginalFilename());
            Path target = uploadDirectory.resolve(storedFileName).normalize();

            if (!target.startsWith(uploadDirectory)) {
                throw new IOException("Invalid photo file path");
            }

            Files.copy(photo.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            car.setPhotoFileName(storedFileName);
            car.setPhotoUrl("/uploads/" + storedFileName);
            car.setPhotoOriginalName(photo.getOriginalFilename());
            car.setPhotoContentType(photo.getContentType());
        }

        return car;
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }

        int dotIndex = originalFilename.lastIndexOf('.');
        return dotIndex >= 0 ? originalFilename.substring(dotIndex) : "";
    }
}
