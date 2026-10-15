package ua.homework;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;


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
    public ResponseEntity<Car> getCarById(@PathVariable @Positive(message = "Id must be greater than 0") Long id) {
        return carService.getCarById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @JsonView(Views.Level1.class)
    public ResponseEntity<Car> createCar(@Valid @RequestBody Car car) {
        Car savedCar = carService.saveCar(car);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable @Positive(message = "Id must be greater than 0") Long id) {
        if (carService.deleteCar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
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
