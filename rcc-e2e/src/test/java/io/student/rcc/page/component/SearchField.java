package io.student.rcc.page.component;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import io.student.rcc.page.BasePage;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;

public class SearchField extends BaseComponent<SearchField> {

    public SearchField() {
        super($("#page-content input.input[type='search']"));
    }


    @Step("Выполнить поиск по запросу: '{query}'")
    public <T extends BasePage> T executeSearch(String query, Class<T> nextPageClass) {
        self.clear();
        self.setValue(query);
        self.$x("./following-sibling::button[contains(@class, 'btn-icon')]").click();
        return Selenide.page(nextPageClass);
    }

    @Step("Найти '{query}' и перейти на страницу результатов")
    public <T extends BasePage> T executeSearchByEnter(String query, Class<T> nextPageClass) {
        self.clear();
        self.setValue(query);
        self.sendKeys(Keys.ENTER);
        return Selenide.page(nextPageClass);
    }

}
