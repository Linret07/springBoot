package ua.homework.config;

public final class Views {

    private Views() {
    }

    public interface Level3 {}
    public interface Level2 extends Level3 {}
    public interface Level1 extends Level2 {}
}
