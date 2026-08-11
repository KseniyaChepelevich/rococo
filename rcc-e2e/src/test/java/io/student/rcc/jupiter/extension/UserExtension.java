package io.student.rcc.jupiter.extension;

import io.student.rcc.jupiter.TestData;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.model.api.UserJson;
import io.student.rcc.service.UsersClient;
import io.student.rcc.service.impl.UsersDbClient;
import io.student.rcc.utils.DataGenerator;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class UserExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private UsersClient userClient;


    @Override
    public void beforeEach(@Nonnull ExtensionContext context) {
        if (userClient == null) {
            userClient = new UsersDbClient();
        }
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                User.class
        ).ifPresent(
                anno -> {
                    String username = anno.username().isEmpty()
                            ? DataGenerator.generateRandomLogin()
                            : anno.username();

                    UserJson user = userClient.createUser(username, anno.password());

                    TestDataExtension.updateContextData(context, testData -> testData.withUser(user, anno.password()));
                });

    }

    @Override
    public boolean supportsParameter(@Nonnull ParameterContext parameterContext, @Nonnull ExtensionContext extensionContext) {
        return UserJson.class.isAssignableFrom(parameterContext.getParameter().getType())
                && AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), User.class);
    }

    @Nonnull
    @Override
    public Object resolveParameter(@Nonnull ParameterContext parameterContext, @Nonnull ExtensionContext extensionContext) throws ParameterResolutionException {
        TestData currentData = extensionContext.getStore(TestDataExtension.NAMESPACE)
                .get(TestDataExtension.KEY, TestData.class);

        if (currentData == null || currentData.user() == null) {
            throw new ParameterResolutionException("User data not found in ExtensionContext store.");
        }

        return currentData.user();
    }

    @Nonnull
    public static String getCreatedUserPassword(@Nonnull ExtensionContext context) {
        TestData currentData = context.getStore(TestDataExtension.NAMESPACE)
                .get(TestDataExtension.KEY, TestData.class);

        if (currentData == null || currentData.userPassword() == null) {
            throw new IllegalStateException("User password not found in ExtensionContext store. Ensure @User is used.");
        }

        return currentData.userPassword();
    }


    @Override
    public void afterEach(@Nonnull ExtensionContext context) throws Exception {
        TestData currentData = context.getStore(TestDataExtension.NAMESPACE)
                .get(TestDataExtension.KEY, TestData.class);

        if (currentData != null && currentData.user() != null) {
            if (userClient == null) {
                userClient = new UsersDbClient();
            }
            userClient.delete(currentData.user());
        }
    }
}
