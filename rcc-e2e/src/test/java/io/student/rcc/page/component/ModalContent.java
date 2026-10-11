package io.student.rcc.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class ModalContent extends BaseComponent<ModalContent> {

    private SelenideElement self;

    public ModalContent() {
        super($("[data-testid=\"modal-component\"]"));
    }

    @Step("Проверить, что заголовок модального окна содержит текст: '{expectedTitle}'")
    public ModalContent shouldHaveTitle(String expectedTitle) {
        self.$("header").shouldHave(text(expectedTitle));
        return this;
    }

    @Step("Добавить изображение: '{expectedTitle}'")
    public ModalContent addPicture(String expectedTitle) {
        self.$("header").shouldHave(text(expectedTitle));
        return this;
    }

}
