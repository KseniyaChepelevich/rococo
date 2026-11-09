package io.student.rcc.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.student.rcc.jupiter.annotation.ScreenShotTest;
import io.student.rcc.model.allure.ScreenDif;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class ScreenShotTestExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver, TestExecutionExceptionHandler {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ScreenShotTestExtension.class);
    public static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String REWRITE_PROPERTY = "rewriteExpected";

    private static final ThreadLocal<ExtensionContext> CONTEXT_HOLDER = new ThreadLocal<>();


    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        CONTEXT_HOLDER.set(context);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        CONTEXT_HOLDER.remove();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), ScreenShotTest.class) &&
                parameterContext.getParameter().getType().isAssignableFrom(BufferedImage.class);
    }

    @SneakyThrows
    @Override
    public BufferedImage resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        String expectedPath = AnnotationSupport
                .findAnnotation(extensionContext.getRequiredTestMethod(), ScreenShotTest.class)
                .map(ScreenShotTest::value)
                .orElse("files/expected_avatar.png");

        try {
            BufferedImage image = ImageIO.read(new ClassPathResource(expectedPath).getInputStream());
            setExpected(image);
            return image;
        } catch (IOException e) {
            throw new ParameterResolutionException(
                    "Не удалось загрузить картинку: " + expectedPath, e);
        }

    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
       try {
           BufferedImage expected = getExpected();
           BufferedImage actual = getActual();
           BufferedImage diff = getDiff();

           if (expected != null && actual != null && diff != null) {
               ScreenDif screenDif = new ScreenDif(
                       "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(expected)),
                       "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(actual)),
                       "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(diff))
               );

               Allure.addAttachment("Screenshot diff",
                       "application/vnd.allure.image.diff",
                       objectMapper.writeValueAsString(screenDif)
               );
           } else {
               System.out.println("[ScreenShotTest] Пропускаем аттачмент — отсутствуют картинки: " +
                       "expected=" + (expected != null) + ", " +
                       "actual=" + (actual != null) + ", " +
                       "diff=" + (diff != null));
           }

           if (actual != null && shouldRewriteExpected(context)) {
               String expectedPath = getExpectedPath(context);
               rewriteExpectedImage(actual, expectedPath);
           }
       }
        catch (Exception e) {
            System.err.println("[ScreenShotTest] Ошибка при обработке скриншотов: " + e.getMessage());
            e.printStackTrace();
        }

        throw throwable;
    }

    public  static void setExpected(BufferedImage expected) {
        getContext().getStore(NAMESPACE).put("expected", expected);
    }

    public  static  BufferedImage getExpected() {
        return getContext().getStore(NAMESPACE).get("expected", BufferedImage.class);
    }

    public  static void setActual(BufferedImage actual) {
        getContext().getStore(NAMESPACE).put("actual", actual);
    }

    public  static  BufferedImage getActual() {
        return getContext().getStore(NAMESPACE).get("actual", BufferedImage.class);
    }

    public  static void setDiff(BufferedImage diff) {
        getContext().getStore(NAMESPACE).put("diff", diff);
    }

    public  static  BufferedImage getDiff() {
        return getContext().getStore(NAMESPACE).get("diff", BufferedImage.class);
    }



    private boolean shouldRewriteExpected(ExtensionContext context) {
        String sysProp = System.getProperty(REWRITE_PROPERTY);
        if (sysProp != null) {
            return Boolean.parseBoolean(sysProp);
        }

        String envVar = System.getenv("REWRITE_EXPECTED");
        if (envVar != null) {
            return Boolean.parseBoolean(envVar);
        }

        return AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ScreenShotTest.class)
                .map(ScreenShotTest::rewriteExpected)
                .orElse(false);
    }

    private void rewriteExpectedImage(BufferedImage actual, String expectedPath) {
            Path[] basePaths = {
                    Path.of("rcc-e2e", "src", "test", "resources"),
                    Path.of("src", "test", "resources"),
                    Path.of(System.getProperty("user.dir"), "src", "test", "resources"),
                    Path.of(System.getProperty("user.dir"), "rcc-e2e", "src", "test", "resources")
            };

            Path targetFile = null;
            Path chosenBase = null;

            for (Path base : basePaths) {
                Path candidate = base.resolve(expectedPath);
                if (Files.exists(candidate) || Files.exists(base)) {
                    targetFile = candidate;
                    chosenBase = base;
                    break;
                }
            }

            if (targetFile == null) {
                targetFile = Path.of("rcc-e2e", "src", "test", "resources").resolve(expectedPath);
                chosenBase = Path.of("rcc-e2e", "src", "test", "resources");
            }

            try {
                Files.createDirectories(targetFile.getParent());

                ImageIO.write(actual, "png", targetFile.toFile());

                String message = String.format(
                        "[ScreenShotTest] ✅ Перезаписан expected файл:\n" +
                                "   Путь: %s\n" +
                                "   Базовая директория: %s",
                        targetFile.toAbsolutePath(),
                        chosenBase.toAbsolutePath()
                );
                System.out.println(message);

                Allure.addAttachment("Expected image rewritten",
                        "text/plain",
                        "Файл перезаписан:\n" + targetFile.toAbsolutePath());

            } catch (IOException e) {
                System.err.println("[ScreenShotTest] ❌ Ошибка перезаписи файла: " + e.getMessage());
                e.printStackTrace();
            }
    }
    private String getExpectedPath(ExtensionContext context) {
        return AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ScreenShotTest.class)
                .map(ScreenShotTest::value)
                .orElse("files/expected_avatar.png");
    }

    private static ExtensionContext getContext() {
        ExtensionContext context = CONTEXT_HOLDER.get();
        if (context == null) {
            throw new IllegalStateException(
                    "ExtensionContext is null. Убедитесь, что тест помечен @ScreenShotTest " +
                            "или что beforeEach отработал корректно."
            );
        }
        return context;
    }

    private static byte[] imageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
