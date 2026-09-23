package io.student.rcc.jupiter.annotation;

import io.student.rcc.jupiter.extension.ArtistExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(ArtistExtension.class)
public @interface Artist {
    String biography() default "";

    String name() default "";

    String photo() default "";
}
