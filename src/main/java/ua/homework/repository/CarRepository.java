package ua.homework.repository;

import ua.homework.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByPower(Integer power);
    List<Car> findByProducerIgnoreCase(String producer);
}
