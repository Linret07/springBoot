package ua.com.owu.lessons.example;

import org.springframework.stereotype.Component;

@Component
public class Passport {

    private String series = "fgdfghfjhjhkjlkl";

    public Passport(String series) {
        this.series = series;
    }

    public Passport() {
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    @Override
    public String toString() {
        return "Passport{" +
                "series='" + series + '\'' +
                '}';
    }
}
