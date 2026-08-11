package io.student.rcc.test.fake;

import io.qameta.allure.Allure;
import io.student.rcc.config.Config;
import io.student.rcc.model.api.CountryJson;
import io.student.rcc.model.api.MuseumJson;
import io.student.rcc.service.impl.MuseumDbClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class

SimpleTest {

    private static final Config CFG = Config.getInstance();
    static MuseumDbClient museumDbClient = new MuseumDbClient();

    @Test
    void simpleTest() {
        Allure.step("Simple test", () -> {
            assertEquals(2, 1 + 1);
        });
    }


    @Test
    @DisplayName("Удаление музея")
    void shouldDeleteMuseum() {
        MuseumJson museumJson = museumDbClient.create(
                new MuseumJson(null, "Санкт-Петербург", "Третьяковская галлерея", "Самый лушчий музей РФ", "src\\test\\resources\\files\\luvr.jpg",
                        new CountryJson(null, "Россия"))
        );
        System.out.println(museumJson);
        museumDbClient.delete(museumJson);
    }
}
