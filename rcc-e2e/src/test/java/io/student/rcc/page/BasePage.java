package io.student.rcc.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import io.student.rcc.page.component.Header;
import io.student.rcc.page.component.ItemCard;
import io.student.rcc.page.component.SearchField;
import io.student.rcc.page.component.Toast;
import org.openqa.selenium.JavascriptExecutor;

import static com.codeborne.selenide.Selenide.$;

public abstract class BasePage {
    protected final Header header = new Header();
    protected final SearchField searchField = new SearchField();

    public Header header() {
        return this.header;
    }

    public SearchField search() {
        return this.searchField;
    }

    public ItemCard card() {
        return new ItemCard();
    }

    public Toast toast() {
        return new Toast($(".toast, [class*='toast']"));
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
