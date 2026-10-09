package ua.com.owu.homework;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CarHw9 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String model;
    private String producer;
    private int power;

    public CarHw9() {
    }

    public CarHw9(String model, String producer, int power) {
        this.model = model;
        this.producer = producer;
        this.power = power;
    }

    public Long getId() { return id; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getProducer() { return producer; }
    public void setProducer(String producer) { this.producer = producer; }
    public int getPower() { return power; }
    public void setPower(int power) { this.power = power; }
}
