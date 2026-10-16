package ua.homework.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.homework.config.Views;
import ua.homework.model.Car;
import ua.homework.service.CarService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/cars")
@Validated
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    @JsonView(Views.Level3.class)
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/{id}")
    @JsonView(Views.Level1.class)
    public ResponseEntity<Car> getCarById(
            @PathVariable @Positive(message = "Id must be greater than 0") Long id) {
        return carService.getCarById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @JsonView(Views.Level1.class)
    public ResponseEntity<Car> createCar(@Valid @RequestBody Car car) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.saveCar(car));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(
            @PathVariable @Positive(message = "Id must be greater than 0") Long id) {
        return carService.deleteCar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/power/{value}")
    @JsonView(Views.Level2.class)
    public ResponseEntity<List<Car>> getCarsByPower(
            @PathVariable("value") @Min(value = 1, message = "Power must be greater than 0") Integer power) {
        return ResponseEntity.ok(carService.getCarsByPower(power));
    }

    @GetMapping("/producer/{value}")
    @JsonView(Views.Level2.class)
    public ResponseEntity<List<Car>> getCarsByProducer(@PathVariable("value") String producer) {
        return ResponseEntity.ok(carService.getCarsByProducer(producer));
    }
}
