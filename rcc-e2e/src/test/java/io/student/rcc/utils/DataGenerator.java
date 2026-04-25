package io.student.rcc.utils;

import com.github.javafaker.Faker;

public class DataGenerator {
    private static final Faker faker = new Faker();

    public static String generateRandomLogin(){
        return faker.name().username();

    }

    public static String generateFirstname(){
        return faker.name().firstName();
    }

    public static String generateLastname(){
        return faker.name().lastName();
    }

    public static String generateRandomPassword(){
        return faker.internet().password(4, 8);
    }
}
