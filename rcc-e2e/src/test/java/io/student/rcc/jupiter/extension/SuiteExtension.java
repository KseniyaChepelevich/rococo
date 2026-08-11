package io.student.rcc.jupiter.extension;

import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public interface SuiteExtension extends BeforeAllCallback {

    @Override
    default void beforeAll(@Nonnull ExtensionContext context) throws Exception {
        final ExtensionContext rootContext = context.getRoot();
        rootContext.getStore(ExtensionContext.Namespace.GLOBAL)
                .getOrComputeIfAbsent(
                        this.getClass(),
                        key -> {
                            beforeSuite(rootContext);
                            return (ExtensionContext.Store.CloseableResource) () -> afterSuite();
                        }
                );
    }

    default void beforeSuite(@Nonnull ExtensionContext context) {
    }

    default void afterSuite() {
    }
}
