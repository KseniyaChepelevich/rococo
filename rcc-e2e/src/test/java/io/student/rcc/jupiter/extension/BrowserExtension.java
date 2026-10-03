package io.student.rcc.jupiter.extension;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import io.student.rcc.config.Config;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class BrowserExtension implements
        SuiteExtension,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

    private static final Config CFG = Config.getInstance();

    @Override
    public void beforeSuite(@Nonnull ExtensionContext context) {
        Configuration.baseUrl = CFG.frontUrl();
        Configuration.browser = "chrome";
        Configuration.pageLoadStrategy = "eager";

        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(false)
                .screenshots(false)
        );

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");

        options.addArguments("--disable-gpu"); // Отключаем аппаратное ускорение рендеринга
        options.addArguments("--disable-renderer-backgrounding");
        options.addArguments("--disable-background-timer-throttling");

        Map<String, Object> prefs = new HashMap<>();
        options.setExperimentalOption("prefs", prefs);

        Configuration.browserCapabilities = options;
    }

    @Override
    public void afterSuite() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.closeWebDriver();
        }
    }


    @Override
    public void afterEach(@Nonnull ExtensionContext context) throws Exception {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.clearBrowserCookies();
            Selenide.clearBrowserLocalStorage();
        }
    }

    @Override
    public void handleTestExecutionException(@Nonnull ExtensionContext context, @Nonnull Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(@Nonnull ExtensionContext context, @Nonnull Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(@Nonnull ExtensionContext context, @Nonnull Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    private static void doScreenshot() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Allure.addAttachment(
                    "Screen on fail",
                    new ByteArrayInputStream(
                            ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES)
                    )
            );
        }
    }
}
