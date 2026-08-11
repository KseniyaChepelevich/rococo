package io.student.rcc.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rcc.page.MuseumsPage;

import static com.codeborne.selenide.Condition.*;

public class Toast {
    private final SelenideElement self;
    private final SelenideElement message;
    private final SelenideElement closeButton;

    public Toast(SelenideElement self) {
        this.self = self;
        this.message = self.$("div");
        this.closeButton = self.$("[aria-label='Dismiss toast']");
    }

    @Step("Проверить отображение всплывающего уведомления (Toast)")
    public Toast shouldBeVisible() {
        self.shouldBe(visible);
        return this;
    }

    @Step("Проверить, что уведомление содержит текст: '{text}'")
    public Toast shouldContainMessage(String text) {
        message.shouldHave(partialText(text));
        return this;
    }

    @Step("Закрыть всплывающее уведомление")
    public MuseumsPage close() {
        closeButton.click();
        self.should(disappear);
        return new MuseumsPage();
    }
}
