package io.student.rcc.jupiter.extension;

import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.model.UserJson;
import io.student.rcc.service.UsersClient;
import io.student.rcc.service.UsersDBClient;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import static io.student.rcc.utils.DataGenerator.*;

import java.util.UUID;

public class UserExtension implements BeforeEachCallback, ParameterResolver, AfterEachCallback {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private final UsersClient userClient = new UsersDBClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                User.class
        ).ifPresent(
                anno -> {
                    UserJson user = new UserJson(
                            UUID.randomUUID(),
                            generateRandomLogin(),
                            generateFirstname(),
                            generateLastname(),
                            null,
                            anno.password(),
                            anno.enabled()
                    );

                    context.getStore(NAMESPACE)
                            .put(context.getUniqueId(), userClient.createUser(user));
                }
        );

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(UserJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), UserJson.class);
    }


    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        // 1. Удаление данных из БД
        UserJson user = context.getStore(NAMESPACE)
                .get(context.getUniqueId(), UserJson.class);

        if (user != null) {
            // Предполагается, что вы добавите этот метод в UsersClient
            userClient.deleteUser(user);
        }
    }

}
