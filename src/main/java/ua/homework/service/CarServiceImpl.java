package ua.homework.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.homework.model.Car;
import ua.homework.repository.CarRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    @Override
    @Transactional
    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    @Override
    @Transactional
    public boolean deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            return false;
        }
        carRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Car> getCarsByPower(Integer power) {
        return carRepository.findByPower(power);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Car> getCarsByProducer(String producer) {
        return carRepository.findByProducerIgnoreCase(producer);
    }
}
