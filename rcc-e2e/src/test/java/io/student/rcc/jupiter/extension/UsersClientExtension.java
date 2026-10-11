package io.student.rcc.jupiter.extension;

import io.student.rcc.service.UsersClient;
import io.student.rcc.service.impl.UsersApiClient;
import io.student.rcc.service.impl.UsersDbClient;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class UsersClientExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(UsersClient.class)) {
                field.setAccessible(true);
                field.set(testInstance, "api".equals(System.getProperty("client.impl"))
                ? new UsersApiClient()
                        : new UsersDbClient());
            }
        }
    }
}
