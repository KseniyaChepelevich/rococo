package io.student.rcc.jupiter.extension;

import io.student.rcc.jupiter.TestData;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.model.api.UserJson;
import io.student.rcc.service.UsersClient;
import io.student.rcc.service.UsersDbClient;
import io.student.rcc.utils.DataGenerator;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.platform.commons.support.AnnotationSupport;

public class UserExtension implements BeforeEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private UsersClient userClient;


    @Override
    public void beforeEach(ExtensionContext context) {
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
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return UserJson.class.isAssignableFrom(parameterContext.getParameter().getType())
                && AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), User.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        TestData currentData = extensionContext.getStore(TestDataExtension.NAMESPACE)
                .get(TestDataExtension.KEY, TestData.class);

        return currentData != null ? currentData.user() : null;
    }

    public static String getCreatedUserPassword(ExtensionContext context) {
        TestData currentData = context.getStore(TestDataExtension.NAMESPACE)
                .get(TestDataExtension.KEY, TestData.class);

        return currentData != null ? currentData.userPassword() : null;
    }


}
