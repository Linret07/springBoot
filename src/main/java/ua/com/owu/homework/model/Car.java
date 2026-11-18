package ua.com.owu.homework.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonView;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Document("cars")
public class Car {
    public static class Views {
        public static class List { }
        public static class Details extends List { }
    }

    @Id
    @JsonView(Views.Details.class)
    private Long id;
    @JsonView(Views.List.class)
    @NotBlank
    private String model;
    @NotBlank
    @JsonView(Views.List.class)
    private String producer;
    @JsonView(Views.Details.class)
    @NotNull
    @Positive
    private Integer power;
    private String photoFileName;
    private String photoUrl;
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
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getPhotoOriginalName() { return photoOriginalName; }
    public void setPhotoOriginalName(String photoOriginalName) { this.photoOriginalName = photoOriginalName; }
}
