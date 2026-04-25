package io.student.rcc.jupiter.annotation;

import io.student.rcc.jupiter.extension.UserExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

@Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
@ExtendWith(UserExtension.class)
public @interface User {
    String username();
    String firstname();
    String lastname();
    String avatar();
}
