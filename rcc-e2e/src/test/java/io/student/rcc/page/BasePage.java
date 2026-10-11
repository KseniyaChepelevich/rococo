package io.student.rcc.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.openqa.selenium.JavascriptExecutor;

import static com.codeborne.selenide.Selenide.$;

public abstract class BasePage<T extends BasePage<?>> {

    private final SelenideElement toast = $(".toast, [data-testid='toast']");


    public T checkToastMessage(String text) {
        toast.should(Condition.text(text));
        return (T) this;
    }


    @Step("Получить текущий URL страницы")
    public String getCurrentUrl() {
        return WebDriverRunner.url();
    }

    @Step("Обновить текущую страницу")
    public void refreshPage() {
        Selenide.refresh();
    }

    @Step("Очистить local storage и куки браузера")
    public void clearBrowserData() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }

    @Step("Дождаться полной загрузки страницы (JQuery/JS)")
    public void waitForPageToLoad() {
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        Selenide.Wait().until(d -> js.executeScript("return document.readyState").equals("complete"));
    }
}
