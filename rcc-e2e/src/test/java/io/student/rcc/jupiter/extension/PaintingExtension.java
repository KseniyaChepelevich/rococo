package io.student.rcc.jupiter.extension;

import io.student.rcc.jupiter.TestData;
import io.student.rcc.jupiter.annotation.Painting;
import io.student.rcc.model.api.PaintingJson;
import io.student.rcc.service.PaintingClient;
import io.student.rcc.service.PaintingDbClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.platform.commons.support.AnnotationSupport;

import static io.student.rcc.jupiter.factory.TestDataFactory.painting;

public class PaintingExtension implements BeforeEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingExtension.class);
    private PaintingClient paintingClient;


    @Override
    public void beforeEach(ExtensionContext context) {
        if (paintingClient == null) {
            paintingClient = new PaintingDbClient();
        }
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                Painting.class
        ).ifPresent(
                anno -> {
                    TestData currentData = context.getStore(TestDataExtension.NAMESPACE)
                            .get(TestDataExtension.KEY, TestData.class);

                    if (currentData == null || currentData.artist() == null || currentData.museum() == null) {
                        throw new IllegalStateException("@Painting требует, чтобы @Artist и @Museum были выполнены ранее");
                    }

                    PaintingJson paintingTemplate = painting(anno, currentData.artist(), currentData.museum());
                    PaintingJson createdPainting = paintingClient.create(paintingTemplate);

                    TestDataExtension.updateContextData(context, testData -> testData.withPainting(createdPainting));
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(PaintingJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        TestData currentData = extensionContext.getStore(TestDataExtension.NAMESPACE)
                .get(TestDataExtension.KEY, TestData.class);

        return currentData != null ? currentData.painting() : null;
    }


}
