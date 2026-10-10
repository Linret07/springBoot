package ua.com.owu.homework;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepositoryHw9 extends JpaRepository<Car, Long> {
    List<Car> findByPower(int power);

    List<Car> findByProducerIgnoreCase(String producer);
}
