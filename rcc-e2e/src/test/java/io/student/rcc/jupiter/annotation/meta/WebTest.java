package io.student.rcc.jupiter.annotation.meta;


import io.qameta.allure.junit5.AllureJunit5;
import io.student.rcc.jupiter.extension.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({
        TestDataExtension.class,
        BrowserExtension.class,
        UserExtension.class,
        MuseumExtension.class,
        ArtistExtension.class,
        PaintingExtension.class,
        AllureJunit5.class

})
public @interface WebTest {
}
