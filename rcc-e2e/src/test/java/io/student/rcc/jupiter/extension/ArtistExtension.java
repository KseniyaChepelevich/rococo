package io.student.rcc.jupiter.extension;

import io.student.rcc.jupiter.TestData;
import io.student.rcc.jupiter.annotation.Artist;
import io.student.rcc.model.api.ArtistJson;
import io.student.rcc.service.ArtistClient;
import io.student.rcc.service.ArtistDbClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.platform.commons.support.AnnotationSupport;

import static io.student.rcc.jupiter.factory.TestDataFactory.artist;

public class ArtistExtension implements BeforeEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);
    private ArtistClient artistClient;


    @Override
    public void beforeEach(ExtensionContext context) {
        if (artistClient == null) {
            artistClient = new ArtistDbClient();
        }
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
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(ArtistJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        TestData currentData = extensionContext.getStore(TestDataExtension.NAMESPACE)
                .get(TestDataExtension.KEY, TestData.class);

        return currentData != null ? currentData.artist() : null;
    }


}
