package ua.com.owu.homework.hw8;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public List<Car> getAll() { return carService.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getById(@PathVariable("id") Long id) {
        Car car = carService.findById(id);
        return car == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(car);
    }

    @PostMapping
    public ResponseEntity<Car> create(@RequestBody Car car) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.save(car));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return carService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/power/{value}")
    public List<Car> getByPower(@PathVariable("value") int value) {
        return carService.findByPower(value);
    }

    @GetMapping("/producer/{value}")
    public List<Car> getByProducer(@PathVariable("value") String value) {
        return carService.findByProducer(value);
    }
}
