package io.student.rcc.utils;

import com.github.javafaker.Faker;

public class DataGenerator {
    private static final Faker faker = new Faker();

    public static String generateRandomLogin(){
        return faker.name().username();

    }

    public static String generateRandomBiography(){
        return faker.lorem().sentence(5);
    }

    public static String generateRandomDescription(){
        return generateRandomBiography();
    }

    public static String generateRandomCity(){
        return faker.address().city();
    }

    public static String generateRandomCountry(){
        return faker.address().country();
    }

    public static String generateRandomArtist(){
        return faker.artist().name();
    }

    public static String generateRandomTitle(){
        return faker.name().title();
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
