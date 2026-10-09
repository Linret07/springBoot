package ua.com.owu.homework;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepositoryHw9 extends JpaRepository<CarHw9, Long> {
    List<CarHw9> findByPower(int power);

    List<CarHw9> findByProducerIgnoreCase(String producer);
}
