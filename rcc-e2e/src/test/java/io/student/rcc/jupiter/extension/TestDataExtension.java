package io.student.rcc.jupiter.extension;

import io.student.rcc.jupiter.TestData;
import io.student.rcc.model.api.MuseumJson;
import io.student.rcc.service.MuseumClient;
import io.student.rcc.service.impl.MuseumDbClient;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.extension.*;

import java.util.function.UnaryOperator;

public class TestDataExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(TestDataExtension.class);
    public static final String KEY = "TEST_DATA";
    private final MuseumClient museumClient = new MuseumDbClient();

    @Override
    public void beforeEach(@Nonnull ExtensionContext context) {

        TestData emptyData = new TestData(null, null, null, null, null);
        context.getStore(NAMESPACE).put(KEY, emptyData);
    }

    @Override
    public boolean supportsParameter(@Nonnull ParameterContext parameterContext, @Nonnull ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(TestData.class);
    }

    @Nonnull
    @Override
    public Object resolveParameter(@Nonnull ParameterContext parameterContext, @Nonnull ExtensionContext extensionContext) throws ParameterResolutionException {
        TestData testData = extensionContext.getStore(NAMESPACE).get(KEY, TestData.class);

        if (testData == null) {
            throw new ParameterResolutionException("TestData container not found in ExtensionContext store.");
        }

        return testData;
    }

    public static void updateContextData(@Nonnull ExtensionContext context, @Nonnull UnaryOperator<TestData> updater) {
        ExtensionContext.Store store = context.getStore(NAMESPACE);
        TestData currentData = store.get(KEY, TestData.class);
        if (currentData != null) {
            store.put(KEY, updater.apply(currentData));
        }
    }

    @Override
    public void afterEach(@Nonnull ExtensionContext context) throws Exception {
        // Достаем накопленные за время теста данные по старому ключу KEY
        TestData finalData = context.getStore(NAMESPACE).get(KEY, TestData.class);

        if (finalData != null) {
            // 1. АВТОУДАЛЕНИЕ МУЗЕЕВ
            if (finalData.museum() != null) {
                MuseumJson museum = finalData.museum();

                if (museum.id() != null) {
                    try {
                        museumClient.delete(museum);
                    } catch (Exception e) {
                        System.err.println("Предупреждение: не удалось удалить музей в TestDataExtension (возможно, он уже удален в MuseumExtension): " + e.getMessage());
                    }
                }
            }
        }

        context.getStore(NAMESPACE).remove(KEY);
    }

    public static void addMuseum(@Nonnull ExtensionContext context, @Nonnull MuseumJson museum) {
        updateContextData(context, current -> new TestData(
                current.user(),
                current.userPassword(),
                current.artist(),
                museum,
                current.painting()
        ));
    }


}