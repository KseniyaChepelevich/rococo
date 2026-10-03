package io.student.rcc.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;

public abstract class BaseComponent<T extends BaseComponent<?>> {
    protected final SelenideElement self;

    protected BaseComponent(SelenideElement self) {
        this.self = self;
    }

    @Step("Проверить видимость компонента")
    public T shouldBeVisible() {
        self.shouldBe(visible);
        return (T) this;
    }
}
