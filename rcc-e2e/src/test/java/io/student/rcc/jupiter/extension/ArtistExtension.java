package io.student.rcc.jupiter.extension;

import io.student.rcc.jupiter.TestData;
import io.student.rcc.jupiter.annotation.Artist;
import io.student.rcc.model.api.ArtistJson;
import io.student.rcc.service.ArtistClient;
import io.student.rcc.service.impl.ArtistDbClient;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import static io.student.rcc.jupiter.factory.TestDataFactory.artist;

public class ArtistExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);
    private final ArtistClient artistClient = new ArtistDbClient();


    @Override
    public void beforeEach(@Nonnull ExtensionContext context) {

        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                Artist.class
        ).ifPresent(
                anno -> {
                    ArtistJson artist = artistClient.create(artist(anno));
                    TestDataExtension.updateContextData(context, testData -> testData.withArtist(artist));
                });

    }
    @Override
    public void afterEach(ExtensionContext context) {
        TestData currentData = context.getStore(TestDataExtension.NAMESPACE)
                .get(TestDataExtension.KEY, TestData.class);

        if (currentData != null && currentData.artist() != null) {
            ArtistJson artist = currentData.artist();

            if (artist.id() != null) {
//                if (artistClient == null) {
//                    artistClient = new ArtistDbClient();
//                }
                try {
                    artistClient.delete(artist);
                    System.out.println("Художник удален по ID: " + artist.id());
                } catch (Exception e) {
                    System.err.println("Ошибка удаления художника: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean supportsParameter(@Nonnull ParameterContext parameterContext, @Nonnull ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(ArtistJson.class);
    }

    @Nonnull
    @Override
    public Object resolveParameter(@Nonnull ParameterContext parameterContext, @Nonnull ExtensionContext extensionContext) throws ParameterResolutionException {
        TestData currentData = extensionContext.getStore(TestDataExtension.NAMESPACE)
                .get(TestDataExtension.KEY, TestData.class);

        return currentData != null ? currentData.artist() : null;
    }


}
