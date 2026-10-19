package ua.com.owu.homework.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.com.owu.homework.model.Car;

import java.util.List;

public interface CarRepository extends MongoRepository<Car, Long> {
    List<Car> findByPowerLessThan(Integer power);
}
