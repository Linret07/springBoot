package ua.com.owu.homework.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import ua.com.owu.homework.model.Car;

public interface CarRepository extends MongoRepository<Car, Long> {
    List<Car> findByPowerLessThan(Integer power);
}
