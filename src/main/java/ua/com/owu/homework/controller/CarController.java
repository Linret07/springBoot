package ua.com.owu.homework.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ua.com.owu.homework.model.Car;
import ua.com.owu.homework.service.CarService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/cars")
public class CarController {
    private final CarService service;

    public CarController(CarService service) { this.service = service; }

    @GetMapping @JsonView(Car.Views.Public.class)
    public ResponseEntity<List<Car>> getCars() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/power/{power}") @JsonView(Car.Views.Public.class)
    public ResponseEntity<List<Car>> getByPower(@PathVariable @Positive Integer power) { return ResponseEntity.ok(service.findByPower(power)); }
    @GetMapping("/producer/{producer}") @JsonView(Car.Views.Public.class)
    public ResponseEntity<List<Car>> getByProducer(@PathVariable @NotBlank String producer) { return ResponseEntity.ok(service.findByProducer(producer)); }
    @GetMapping("/{id}") @JsonView(Car.Views.Details.class)
    public ResponseEntity<Car> getCar(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }

    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> getPhoto(@PathVariable Long id) {
        CarService.Photo photo = service.getPhoto(id);
        return ResponseEntity.ok().contentType(photo.getContentType()).body(photo.getResource());
    }

    @DeleteMapping("/low-power")
    public Map<String, Object> deleteLowPowerCars() {
        int deleted = service.deleteCarsWithPowerBelow(100);
        Map<String, Object> result = new HashMap<>(); result.put("deleted", deleted); result.put("condition", "power < 100");
        return result;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) @JsonView(Car.Views.Details.class)
    public ResponseEntity<Car> createCar(@RequestParam @NotBlank String model, @RequestParam @NotBlank String producer,
                         @RequestParam @NotNull @Positive Integer power,
                         @RequestParam(required = false) MultipartFile photo) throws IOException {
        return ResponseEntity.ok(service.save(new Car(model, producer, power), photo));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) @JsonView(Car.Views.Details.class)
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestParam @NotBlank String model,
                         @RequestParam @NotBlank String producer, @RequestParam @NotNull @Positive Integer power,
                         @RequestParam(required = false) MultipartFile photo) throws IOException {
        return ResponseEntity.ok(service.update(id, new Car(model, producer, power), photo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
