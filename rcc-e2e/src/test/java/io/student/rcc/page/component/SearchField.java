package io.student.rcc.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rcc.page.BasePage;

import static com.codeborne.selenide.Selenide.$;

public class SearchField {
    private final SelenideElement searchInputField = $("#page-content input.input[type='search']");
    private final SelenideElement searchButton = $("#page-content input.input[type='search'] + button.btn-icon");


    @Step("Выполнить поиск по запросу: '{query}'")
    public <T extends BasePage> T executeSearch(String query, T nextPage) {
        searchInputField.clear();
        searchInputField.setValue(query);
        searchButton.click();
        return nextPage;
    }


}
