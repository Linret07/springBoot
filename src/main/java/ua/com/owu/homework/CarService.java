package ua.com.owu.homework;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class CarService {
    private final List<Car> cars = new CopyOnWriteArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public List<Car> findAll() {
        return List.copyOf(cars);
    }

    public Car findById(Long id) {
        return cars.stream()
                .filter(car -> car.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Car save(Car car) {
        car.setId(nextId.getAndIncrement());
        cars.add(car);
        return car;
    }

    public boolean deleteById(Long id) {
        return cars.removeIf(car -> car.getId().equals(id));
    }

    public List<Car> findByPower(int power) {
        return cars.stream().filter(car -> car.getPower() == power).toList();
    }

    public List<Car> findByProducer(String producer) {
        return cars.stream()
                .filter(car -> car.getProducer().equalsIgnoreCase(producer))
                .toList();
    }
}
