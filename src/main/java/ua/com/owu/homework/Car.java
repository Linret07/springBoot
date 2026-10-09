package ua.com.owu.homework;

public class Car {
    private Long id;
    private String model;
    private String producer;
    private int power;

    public Car() {
    }

    public Car(Long id, String model, String producer, int power) {
        this.id = id;
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
    public int getPower() { return power; }
    public void setPower(int power) { this.power = power; }
}
