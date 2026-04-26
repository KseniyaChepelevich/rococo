package io.student.rcc.jupiter.extension;

import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.model.UserJson;
import io.student.rcc.service.UserClient;
import io.student.rcc.service.UserDBClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import static io.student.rcc.utils.DataGenerator.*;

import java.util.UUID;

public class UserExtension implements BeforeEachCallback, ParameterResolver {
public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
private final UserClient userClient = new UserDBClient();
private final static String DEFAULT_PASSWORD = "4321";

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
                            DEFAULT_PASSWORD
                    );

                    context.getStore(NAMESPACE)
                            .put(context.getUniqueId(), userClient.createUser(user.username(),DEFAULT_PASSWORD));


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
}
