package ua.com.owu.lessons.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Autowired
    private Passport passport;

    @Bean
    public User user1() {
        System.out.println("Creating user1");
        return new User(1, "vasya", passport);
    }
    @Bean(name = "u2")
    public User user2() {
        System.out.println("Creating user2");
        return new User(2, "petya");
    }
}
