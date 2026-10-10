package ua.com.owu.homework;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarServiceHw9 {
    private final CarRepositoryHw9 carRepository;

    public CarServiceHw9(CarRepositoryHw9 carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> findAll() {
        return carRepository.findAll();
    }

    public Optional<Car> findById(Long id) {
        return carRepository.findById(id);
    }

    public Car save(Car car) {
        return carRepository.save(car);
    }

    public boolean deleteById(Long id) {
        if (!carRepository.existsById(id)) {
            return false;
        }
        carRepository.deleteById(id);
        return true;
    }

    public List<Car> findByPower(int power) {
        return carRepository.findByPower(power);
    }

    public List<Car> findByProducer(String producer) {
        return carRepository.findByProducerIgnoreCase(producer);
    }
}
