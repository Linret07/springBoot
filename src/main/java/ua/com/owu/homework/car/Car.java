package ua.com.owu.homework.car;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("cars")
public class Car {
    @Id
    private String id;
    private String model;
    private String producer;
    private Integer power;
    private byte[] photo;
    private String photoContentType;
    private String photoName;

    public Car() { }

    public Car(String model, String producer, Integer power) {
        this.model = model;
        this.producer = producer;
        this.power = power;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getProducer() { return producer; }
    public void setProducer(String producer) { this.producer = producer; }
    public Integer getPower() { return power; }
    public void setPower(Integer power) { this.power = power; }
    public byte[] getPhoto() { return photo; }
    public void setPhoto(byte[] photo) { this.photo = photo; }
    public String getPhotoContentType() { return photoContentType; }
    public void setPhotoContentType(String photoContentType) { this.photoContentType = photoContentType; }
    public String getPhotoName() { return photoName; }
    public void setPhotoName(String photoName) { this.photoName = photoName; }
}
