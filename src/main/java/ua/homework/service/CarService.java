package ua.homework.service;

import ua.homework.model.Car;

import java.util.List;
import java.util.Optional;

public interface CarService {
    List<Car> getAllCars();
    Optional<Car> getCarById(Long id);
    Car saveCar(Car car);
    boolean deleteCar(Long id);
    List<Car> getCarsByPower(Integer power);
    List<Car> getCarsByProducer(String producer);
}
