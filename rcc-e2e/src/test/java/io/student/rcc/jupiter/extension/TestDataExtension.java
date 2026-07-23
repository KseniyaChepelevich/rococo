package io.student.rcc.jupiter.extension;

import io.student.rcc.jupiter.TestData;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

import java.util.function.UnaryOperator;

public class TestDataExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(TestDataExtension.class);
    public static final String KEY = "TEST_DATA";

    @Override
    public void beforeEach(ExtensionContext context) {

        TestData emptyData = new TestData(null, null, null, null, null);
        context.getStore(NAMESPACE).put(KEY, emptyData);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(TestData.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(KEY, TestData.class);
    }

    public static void updateContextData(ExtensionContext context, UnaryOperator<TestData> updater) {
        ExtensionContext.Store store = context.getStore(NAMESPACE);
        TestData currentData = store.get(KEY, TestData.class);
        if (currentData != null) {
            store.put(KEY, updater.apply(currentData));
        }
    }

}
