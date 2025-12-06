package ru.yandex.praktikum.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MainPage {
    private WebDriver driver;

    private By header = By.className("Home_Header__iJKdX");
    private By topOrderButton = By.className("Button_Button__ra12g");
    private By bottomOrderButton = By.xpath("(//button[contains(text(),'Заказать')])[2]");
    private By scooterLogo = By.className("Header_LogoScooter__3lsAR");
    private By yandexLogo = By.className("Header_LogoYandex__3TSOI");
    private By cookieButton = By.id("rcc-confirm-button");

    public MainPage(WebDriver driver) {
        this.driver = driver;
    }

    // Метод для получения локатора вопроса (упрощённый)
    public void clickQuestion(int index) {
        By questionLocator = By.id("accordion__heading-" + index);
        driver.findElement(questionLocator).click();
    }

    public String getAnswerText(int index) {
        By answerLocator = By.id("accordion__panel-" + index);
        return driver.findElement(answerLocator).getText();
    }

    public void open() {
        driver.get("https://qa-scooter.praktikum-services.ru/");
    }

    public void waitForLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(header));
    }

    public void acceptCookies() {
        try {
            WebElement button = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(cookieButton));
            button.click();
        } catch (Exception e) {
            // Игнорируем, если кнопка не найдена
        }
    }

    public void clickTopOrderButton() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(topOrderButton))
                .click();
    }

    public void clickBottomOrderButton() {
        driver.findElement(bottomOrderButton).click();
    }

    public void clickScooterLogo() {
        driver.findElement(scooterLogo).click();
    }

    public void clickYandexLogo() {
        driver.findElement(yandexLogo).click();
    }
}