package io.student.rcc.jupiter.extension;

import io.student.rcc.jupiter.TestData;
import io.student.rcc.jupiter.annotation.Museum;
import io.student.rcc.model.api.CountryJson;
import io.student.rcc.model.api.MuseumJson;
import io.student.rcc.service.MuseumClient;
import io.student.rcc.service.impl.MuseumDbClient;
import io.student.rcc.utils.DataGenerator;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static io.student.rcc.jupiter.factory.TestDataFactory.museum;


public class MuseumExtension implements BeforeEachCallback, AfterEachCallback, AfterAllCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);
    private MuseumClient museumClient;
    private final List<UUID> createdMuseumIds = Collections.synchronizedList(new ArrayList<>());


    @Override
    public void beforeEach(@Nonnull ExtensionContext context) {
        if (museumClient == null) {
            museumClient = new MuseumDbClient();
        }
        createdMuseumIds.clear();
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                Museum.class
        ).ifPresent(
                anno -> {
                    MuseumJson baseMuseum = museum(anno);

                    String finalTitle = anno.title().isEmpty()
                            ? DataGenerator.generateRandomTitle()
                            : anno.title();

                    String finalCity = anno.city().isEmpty()
                            ? DataGenerator.generateRandomCity()
                            : anno.city();

                    String finalCountry = anno.country().isEmpty()
                            ? "Франция"
                            : anno.country();

                    CountryJson staticCountry = new CountryJson(
                            null,
                            finalCountry
                    );

                    String finalDescription = anno.description().isEmpty()
                            ? DataGenerator.generateRandomDescription()
                            : anno.description();

                    String picturePath = anno.photo().isEmpty()
                            ? "src/test/resources/files/luvr.jpg"
                            : anno.photo();

                    String base64Photo = null;
                    try {
                        Path path = Paths.get(picturePath);
                        if (Files.exists(path)) {
                            byte[] fileBytes = Files.readAllBytes(path);
                            base64Photo = Base64.getEncoder().encodeToString(fileBytes);
                        }
                    } catch (IOException e) {
                        System.err.println("Не удалось прочитать картинку для музея: " + e.getMessage());
                    }

                    MuseumJson museumToCreate = new MuseumJson(
                            null,
                            finalCity,
                            finalTitle,
                            finalDescription,
                            base64Photo, // Передаем готовую Base64-строку
                            staticCountry
                    );
                    System.out.println(">>> [MuseumExtension] Тест: " + context.getDisplayName());
                    System.out.println(">>> [MuseumExtension] Создаем музей: " + finalTitle);


                    if (anno.createInDb()) {
                        MuseumJson createdMuseum = museumClient.create(museumToCreate);
                        if (createdMuseum != null && createdMuseum.id() != null) {
                            createdMuseumIds.add(createdMuseum.id());
                        }
                        System.out.println(">>> [MuseumExtension] Музей создан, id: " + createdMuseum.id());
                        TestDataExtension.updateContextData(context, testData -> testData.withMuseum(createdMuseum));
                    } else {

                        TestDataExtension.updateContextData(context, testData -> testData.withMuseum(museumToCreate));
                        System.out.println(">>> [MuseumExtension] TestData обновлен");
                    }
                }
        );

    }

    @Override
    public boolean supportsParameter(@Nonnull ParameterContext parameterContext, @Nonnull ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(MuseumJson.class);
    }

    @Nonnull
    @Override
    public Object resolveParameter(@Nonnull ParameterContext parameterContext, @Nonnull ExtensionContext extensionContext) throws ParameterResolutionException {
        TestData currentData = extensionContext.getStore(TestDataExtension.NAMESPACE)
                .get(TestDataExtension.KEY, TestData.class);

        return currentData != null ? currentData.museum() : null;

    }


    @Override
    public void afterEach(@Nonnull ExtensionContext context) throws Exception {
        if (museumClient == null) {
            museumClient = new MuseumDbClient();
        }

        // 1. Удаляем музеи, которые были созданы через API в beforeEach (по сохраненным ID)
        synchronized (createdMuseumIds) {
            for (UUID id : createdMuseumIds) {
                try {
                    museumClient.findById(id).ifPresent(museum -> {
                        museumClient.delete(museum);
                        System.out.println("Музей удален по ID из beforeEach: " + id);
                    });
                } catch (Exception e) {
                    System.err.println("Ошибка удаления музея по ID " + id + ": " + e.getMessage());
                }
            }
            createdMuseumIds.clear();
        }

        TestData currentData = context.getStore(TestDataExtension.NAMESPACE)
                .get(TestDataExtension.KEY, TestData.class);

        if (currentData != null && currentData.museum() != null) {
            MuseumJson museum = currentData.museum();
            try {
                museumClient.findByTitle(museum.title()).stream()
                        .findFirst() // Берем первый найденный музей (или ничего, если список пуст)
                        .ifPresent(actualMuseum -> {
                            museumClient.delete(actualMuseum);
                            System.out.println("Музей, созданный через UI, удален по названию: " + museum.title());
                        });
            } catch (Exception e) {
                System.err.println("Ошибка удаления UI-музея по названию: " + e.getMessage());
            }
        }
    }

    @Override
    public void afterAll(@Nonnull ExtensionContext context) {
        if (!createdMuseumIds.isEmpty()) {
            System.err.println("Обнаружены неудаленные музеи (" + createdMuseumIds.size() + "). Удаление...");
            if (museumClient == null) {
                museumClient = new MuseumDbClient();
            }
            synchronized (createdMuseumIds) {
                for (UUID id : createdMuseumIds) {
                    try {
                        museumClient.findById(id).ifPresent(museumClient::delete);
                    } catch (Exception e) {
                        System.err.println("Ошибка при очистке в afterAll для ID " + id + ": " + e.getMessage());
                    }
                }
                createdMuseumIds.clear();
            }
        }
    }

}
