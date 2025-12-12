package ru.yandex.praktikum.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class OrderPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Локаторы для первой страницы заказа
    private By nameField = By.xpath("//input[@placeholder='* Имя']");
    private By surnameField = By.xpath("//input[@placeholder='* Фамилия']");
    private By addressField = By.xpath("//input[contains(@placeholder, 'Адрес: куда')]");
    private By metroField = By.xpath("//input[@placeholder='* Станция метро']");
    private By phoneField = By.xpath("//input[@placeholder='* Телефон: на него позвонит курьер']");
    private By nextButton = By.xpath("//button[text()='Далее']");

    // Локаторы для второй страницы заказа
    private By whenBringDate = By.xpath("//input[@placeholder='* Когда привезти самокат']");
    private By rentalPeriod = By.className("Dropdown-control");
    private By colorBlack = By.id("black");
    private By colorGrey = By.id("grey");
    private By commentField = By.xpath("//input[@placeholder='Комментарий для курьера']");
    private By orderButton = By.xpath("//button[text()='Заказать']");

    // Локаторы для подтверждения заказа
    private By confirmationModalText = By.xpath("//div[contains(text(), 'Хотите оформить заказ?')]");
    private By modalYesButton = By.xpath("//div[contains(@class, 'Order_Modal')]//button[text()='Да']");
    private By orderSuccessMessage = By.xpath("//div[contains(@class, 'Order_ModalHeader') and contains(text(), 'Заказ оформлен')]");

    private By metroDropdown = By.className("select-search__select");
    private By metroOption = By.className("select-search__option");

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void waitForLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
    }

    public void waitForSecondPage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(whenBringDate));
    }

    public void fillFirstPage(String name, String surname, String address, String metro, String phone) {
        fillField(nameField, name);
        fillField(surnameField, surname);
        fillField(addressField, address);
        selectMetroStation(metro);
        fillField(phoneField, phone);
        clickNextButton();
    }

    public void fillSecondPage(String date, String period, boolean black, boolean grey, String comment) {
        fillDate(date);
        selectRentalPeriod(period);
        selectColor(black, grey);
        if (comment != null && !comment.trim().isEmpty()) {
            fillField(commentField, comment);
        }
        clickOrderButton();
    }

    public void confirmOrder() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationModalText));
        WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(modalYesButton));
        yesButton.click();
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(orderSuccessMessage));
            return message.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getSuccessMessageText() {
        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(orderSuccessMessage));
        return message.getText();
    }

    private void fillField(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.clear();
        element.sendKeys(text);
    }

    private void selectMetroStation(String metro) {
        WebElement metroElement = wait.until(ExpectedConditions.elementToBeClickable(metroField));
        metroElement.click();
        metroElement.sendKeys(metro);

        // Альтернативный локатор - любая опция, содержащая текст станции
        By stationLocator = By.xpath("//*[contains(text(), '" + metro + "')]");
        WebElement station = wait.until(ExpectedConditions.elementToBeClickable(stationLocator));
        station.click();
    }

    private void clickNextButton() {
        WebElement nextButtonElement = wait.until(ExpectedConditions.elementToBeClickable(nextButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextButtonElement);
        nextButtonElement.click();
    }

    private void fillDate(String date) {
        WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(whenBringDate));
        dateInput.clear();
        dateInput.sendKeys(date);
        dateInput.sendKeys(Keys.ENTER); // Закрываем календарь
    }

    private void selectRentalPeriod(String period) {
        WebElement periodElement = wait.until(ExpectedConditions.elementToBeClickable(rentalPeriod));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", periodElement);
        periodElement.click();

        By periodOption = By.xpath("//div[@class='Dropdown-option' and text()='" + period + "']");
        WebElement periodOptionElement = wait.until(ExpectedConditions.elementToBeClickable(periodOption));
        periodOptionElement.click();
    }

    private void selectColor(boolean black, boolean grey) {
        if (black) {
            WebElement blackCheckbox = wait.until(ExpectedConditions.elementToBeClickable(colorBlack));
            if (!blackCheckbox.isSelected()) {
                blackCheckbox.click();
            }
        }

        if (grey) {
            WebElement greyCheckbox = wait.until(ExpectedConditions.elementToBeClickable(colorGrey));
            if (!greyCheckbox.isSelected()) {
                greyCheckbox.click();
            }
        }
    }

    private void clickOrderButton() {
        WebElement orderButtonElement = wait.until(ExpectedConditions.elementToBeClickable(orderButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", orderButtonElement);
        orderButtonElement.click();
    }

    public boolean isConfirmationModalVisible() {
        try {
            return driver.findElement(confirmationModalText).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}