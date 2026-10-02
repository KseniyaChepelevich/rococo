package io.student.rcc.jupiter.annotation;

import io.student.rcc.jupiter.extension.MuseumExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(MuseumExtension.class)
public @interface Painting {
    String title() default "";

    String description() default "";

    String content() default "";

    String artist() default "";

    String museum() default "";
}


