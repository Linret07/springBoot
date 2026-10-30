package ua.com.owu.homework.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("cars")
public class Car {
    @Id
    private Long id;
    private String model;
    private String producer;
    private Integer power;
    private String photoFileName;
    private String photoContentType;
    private String photoOriginalName;

    public Car() { }

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
    public String getPhotoContentType() { return photoContentType; }
    public void setPhotoContentType(String photoContentType) { this.photoContentType = photoContentType; }
    public String getPhotoFileName() { return photoFileName; }
    public void setPhotoFileName(String photoFileName) { this.photoFileName = photoFileName; }
    public String getPhotoOriginalName() { return photoOriginalName; }
    public void setPhotoOriginalName(String photoOriginalName) { this.photoOriginalName = photoOriginalName; }
}
