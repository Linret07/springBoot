package ua.com.owu.homework.controller;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ua.com.owu.homework.model.Car;
import ua.com.owu.homework.service.CarService;

@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService service;

    public CarController(CarService service) { this.service = service; }

    @GetMapping
    public List<Car> getCars() { return service.findAll(); }

    @GetMapping("/{id}")
    public Car getCar(@PathVariable Long id) { return service.findById(id); }

    @DeleteMapping("/low-power")
    public Map<String, Object> deleteLowPowerCars() {
        int deleted = service.deleteCarsWithPowerBelow(100);
        Map<String, Object> response = new HashMap<>();
        response.put("deleted", deleted);
        response.put("condition", "power < 100");
        return response;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Car createCar(@RequestParam String model,
                         @RequestParam String producer,
                         @RequestParam Integer power,
                         @RequestParam(required = false) MultipartFile photo) throws IOException {
        return service.save(toCar(model, producer, power, photo));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Car updateCar(@PathVariable Long id,
                         @RequestParam String model,
                         @RequestParam String producer,
                         @RequestParam Integer power,
                         @RequestParam(required = false) MultipartFile photo) throws IOException {
        return service.update(id, toCar(model, producer, power, photo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Car toCar(String model, String producer, Integer power, MultipartFile photo) throws IOException {
        Car car = new Car(model, producer, power);
        if (photo != null && !photo.isEmpty()) {
            car.setPhoto(photo.getBytes());
            car.setPhotoName(photo.getOriginalFilename());
            car.setPhotoContentType(photo.getContentType());
        }
        return car;
    }
}
