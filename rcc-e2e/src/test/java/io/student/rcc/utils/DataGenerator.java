package io.student.rcc.utils;

import com.github.javafaker.Faker;
import jakarta.annotation.Nonnull;

import java.util.Locale;

public class DataGenerator {
    private static final Faker faker = new Faker(new Locale("ru"));

    @Nonnull
    public static String generateRandomLogin(){
        return faker.name().username();

    }

    @Nonnull
    public static String generateRandomBiography(){
        return faker.lorem().sentence(5);
    }

    @Nonnull
    public static String generateRandomDescription(){
        return generateRandomBiography();
    }

    @Nonnull
    public static String generateRandomCity(){
        return faker.address().city();
    }

    @Nonnull
    public static String generateRandomCountry(){
        return faker.address().country();
    }

    @Nonnull
    public static String generateRandomArtist(){
        return faker.artist().name();
    }

    @Nonnull
    public static String generateRandomTitle(){
        return faker.lorem().word();
    }

    @Nonnull
    public static String generateFirstname(){
        return faker.name().firstName();
    }

    @Nonnull
    public static String generateLastname(){
        return faker.name().lastName();
    }

    @Nonnull
    public static String generateRandomPassword(){
        return faker.internet().password(4, 8);
    }

    @Nonnull
    public static String generateRandomString256() {return faker.lorem().characters(256);}

    @Nonnull
    public static String generateRandomString255() {return faker.lorem().characters(255);}

    @Nonnull
    public static String generateRandomString10() {return faker.lorem().characters(10);}

    @Nonnull
    public static String generateRandomString9() {return faker.lorem().characters(9);}

    @Nonnull
    public static String generateRandomString1000() {return faker.lorem().characters(1000);}

    @Nonnull
    public static String generateRandomString1001() {return faker.lorem().characters(1001);}
}
