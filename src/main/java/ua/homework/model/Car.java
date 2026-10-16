package ua.homework.model;

import com.fasterxml.jackson.annotation.JsonView;
import ua.homework.config.Views;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Level1.class)
    private Long id;

    @NotBlank(message = "Model name cannot be empty")
    @JsonView(Views.Level3.class)
    private String model;

    @NotBlank(message = "Producer cannot be empty")
    @JsonView(Views.Level3.class)
    private String producer;

    @NotNull(message = "Power is required")
    @Min(value = 1, message = "Power must be greater than 0")
    @JsonView(Views.Level2.class)
    private Integer power;

    public Car() {
    }

    public Car(String model, String producer, Integer power) {
        this.model = model;
        this.producer = producer;
        this.power = power;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getProducer() { return producer; }
    public void setProducer(String producer) { this.producer = producer; }
    public Integer getPower() { return power; }
    public void setPower(Integer power) { this.power = power; }
}
