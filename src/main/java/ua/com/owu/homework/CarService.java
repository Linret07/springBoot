package ua.com.owu.homework;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CarService {
    private final List<Car> cars = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public synchronized List<Car> findAll() {
        return new ArrayList<>(cars);
    }

    public synchronized Car findById(Long id) {
        return cars.stream()
                .filter(car -> car.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public synchronized Car save(Car car) {
        car.setId(nextId.getAndIncrement());
        cars.add(car);
        return car;
    }

    public synchronized boolean deleteById(Long id) {
        return cars.removeIf(car -> car.getId().equals(id));
    }

    public synchronized List<Car> findByPower(int power) {
        return cars.stream().filter(car -> car.getPower() == power).toList();
    }

    public synchronized List<Car> findByProducer(String producer) {
        return cars.stream()
                .filter(car -> car.getProducer().equalsIgnoreCase(producer))
                .toList();
    }
}
