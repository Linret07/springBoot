package ua.com.owu.homework;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarControllerHw9 {
    private final CarServiceHw9 carService;

    public CarControllerHw9(CarServiceHw9 carService) {
        this.carService = carService;
    }

    @GetMapping
    public List<CarHw9> getAll() {
        return carService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarHw9> getById(@PathVariable("id") Long id) {
        return carService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CarHw9> create(@RequestBody CarHw9 car) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.save(car));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return carService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/power/{value}")
    public List<CarHw9> getByPower(@PathVariable("value") int value) {
        return carService.findByPower(value);
    }

    @GetMapping("/producer/{value}")
    public List<CarHw9> getByProducer(@PathVariable("value") String value) {
        return carService.findByProducer(value);
    }
}
