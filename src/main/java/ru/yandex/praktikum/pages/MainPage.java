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
    private By faqSection = By.className("Home_FourPart__1uthg");

    public MainPage(WebDriver driver) {
        this.driver = driver;
    }

    // Метод для клика на вопрос FAQ
    public void clickQuestion(int index) {
        System.out.println("Кликаем на вопрос #" + index);
        By questionLocator = By.id("accordion__heading-" + index);
        WebElement question = driver.findElement(questionLocator);

        // Прокручиваем к элементу
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", question);

        // Ждем, пока элемент станет кликабельным
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(question));

        // Кликаем
        question.click();

        // Даем время для анимации
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Метод для получения текста ответа с ожиданием
    public String getAnswerText(int index) {
        By answerLocator = By.id("accordion__panel-" + index);

        // Ждем, пока ответ станет видимым
        WebElement answerElement = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(answerLocator));

        // Получаем текст
        String text = answerElement.getText();
        System.out.println("Текст ответа #" + index + ": " + text);
        return text;
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
            System.out.println("Куки приняты");
        } catch (Exception e) {
            // Игнорируем, если кнопка не найдена
            System.out.println("Кнопка куки не найдена");
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

    // Метод для прокрутки к разделу FAQ
    public void scrollToFAQ() {
        WebElement faqElement = driver.findElement(faqSection);
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", faqElement);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}