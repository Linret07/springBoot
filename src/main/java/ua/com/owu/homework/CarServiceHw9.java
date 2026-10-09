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

    public List<CarHw9> findAll() {
        return carRepository.findAll();
    }

    public Optional<CarHw9> findById(Long id) {
        return carRepository.findById(id);
    }

    public CarHw9 save(CarHw9 car) {
        return carRepository.save(car);
    }

    public boolean deleteById(Long id) {
        if (!carRepository.existsById(id)) {
            return false;
        }
        carRepository.deleteById(id);
        return true;
    }

    public List<CarHw9> findByPower(int power) {
        return carRepository.findByPower(power);
    }

    public List<CarHw9> findByProducer(String producer) {
        return carRepository.findByProducerIgnoreCase(producer);
    }
}
