package ru.yandex.praktikum.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MainPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By header = By.className("Home_Header__iJKdX");
    private By topOrderButton = By.className("Button_Button__ra12g");
    private By bottomOrderButton = By.xpath("(//button[contains(text(),'Заказать')])[2]");
    private By cookieButton = By.id("rcc-confirm-button");
    private By faqSection = By.className("Home_FourPart__1uthg");

    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickQuestion(int index) {
        By questionLocator = By.id("accordion__heading-" + index);
        WebElement question = wait.until(ExpectedConditions.elementToBeClickable(questionLocator));

        scrollToElement(question);
        question.click();


        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("accordion__panel-" + index)));
    }

    public String getAnswerText(int index) {
        By answerLocator = By.id("accordion__panel-" + index);
        WebElement answerElement = wait.until(ExpectedConditions.visibilityOfElementLocated(answerLocator));
        return answerElement.getText();
    }

    public void open() {
        driver.get("https://qa-scooter.praktikum-services.ru/");
    }

    public void waitForLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(header));
    }

    public void acceptCookies() {
        try {
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(cookieButton));
            button.click();
        } catch (Exception e) {

        }
    }

    public void clickTopOrderButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(topOrderButton));
        button.click();
    }

    public void clickBottomOrderButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(bottomOrderButton));
        button.click();
    }

    public void scrollToFAQ() {
        WebElement faqElement = wait.until(ExpectedConditions.visibilityOfElementLocated(faqSection));
        scrollToElement(faqElement);
    }

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
}
//испр