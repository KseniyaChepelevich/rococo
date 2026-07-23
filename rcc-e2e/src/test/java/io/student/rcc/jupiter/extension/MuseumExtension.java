package io.student.rcc.jupiter.extension;

import io.student.rcc.jupiter.TestData;
import io.student.rcc.jupiter.annotation.Museum;
import io.student.rcc.model.api.MuseumJson;
import io.student.rcc.service.MuseumClient;
import io.student.rcc.service.MuseumDbClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.platform.commons.support.AnnotationSupport;

import static io.student.rcc.jupiter.factory.TestDataFactory.museum;

public class MuseumExtension implements BeforeEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);
    private MuseumClient museumClient;


    @Override
    public void beforeEach(ExtensionContext context) {
        if (museumClient == null) {
            museumClient = new MuseumDbClient();
        }
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                Museum.class
        ).ifPresent(
                anno -> {
                    MuseumJson museum = museumClient.create(museum(anno));
                    TestDataExtension.updateContextData(context, testData -> testData.withMuseum(museum));
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(MuseumJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        TestData currentData = extensionContext.getStore(TestDataExtension.NAMESPACE)
                .get(TestDataExtension.KEY, TestData.class);

        return currentData != null ? currentData.museum() : null;
    }


}
