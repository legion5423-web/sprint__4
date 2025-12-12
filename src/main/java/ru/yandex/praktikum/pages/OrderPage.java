package ru.yandex.praktikum.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;

public class OrderPage {
    private WebDriver driver;

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
    private By confirmButton = By.xpath("//button[text()='Да']");
    private By orderSuccessMessage = By.xpath("//div[contains(@class, 'Order_ModalHeader')]");

    public OrderPage(WebDriver driver) {
        this.driver = driver;
    }

    public void waitForLoad() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
        System.out.println("Форма заказа полностью загружена");
    }

    public void waitForSecondPage() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(whenBringDate));
    }

    public void fillFirstPage(String name, String surname, String address, String metro, String phone) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Заполняем имя
        System.out.println("Заполняем имя: " + name);
        WebElement nameElement = wait.until(ExpectedConditions.elementToBeClickable(nameField));
        nameElement.clear();
        nameElement.sendKeys(name);

        // Заполняем фамилию
        System.out.println("Заполняем фамилию: " + surname);
        WebElement surnameElement = wait.until(ExpectedConditions.elementToBeClickable(surnameField));
        surnameElement.clear();
        surnameElement.sendKeys(surname);

        // Заполняем адрес
        System.out.println("Заполняем адрес: " + address);
        try {
            WebElement addressElement = wait.until(ExpectedConditions.elementToBeClickable(addressField));
            addressElement.clear();
            addressElement.sendKeys(address);
        } catch (Exception e) {
            // Альтернативный способ: ищем по любому placeholder с "Адрес"
            List<WebElement> inputs = driver.findElements(By.xpath("//input"));
            for (WebElement input : inputs) {
                String placeholder = input.getAttribute("placeholder");
                if (placeholder != null && placeholder.contains("Адрес")) {
                    input.clear();
                    input.sendKeys(address);
                    break;
                }
            }
        }

        // Заполняем станцию метро
        System.out.println("Заполняем станцию метро: " + metro);
        fillMetroStation(metro);

        // Заполняем телефон
        System.out.println("Заполняем телефон: " + phone);
        WebElement phoneElement = wait.until(ExpectedConditions.elementToBeClickable(phoneField));
        phoneElement.clear();
        phoneElement.sendKeys(phone);

        // Нажимаем кнопку "Далее"
        System.out.println("Нажимаем кнопку 'Далее'");
        WebElement nextButtonElement = driver.findElement(nextButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextButtonElement);
        wait.until(ExpectedConditions.elementToBeClickable(nextButton)).click();
    }

    private void fillMetroStation(String metro) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Кликаем на поле метро
            WebElement metroElement = wait.until(ExpectedConditions.elementToBeClickable(metroField));
            metroElement.click();

            // Вводим текст станции
            metroElement.sendKeys(metro);

            // Ждём появления выпадающего списка
            System.out.println("Ожидаем появления списка станций...");

            // Пробуем несколько вариантов локаторов для станции метро
            selectMetroStation(metro);

        } catch (Exception e) {
            System.out.println("Ошибка при выборе станции метро: " + e.getMessage());

            // Попробуем альтернативный способ
            try {
                WebElement metroElement = driver.findElement(metroField);
                metroElement.click();
                metroElement.sendKeys(metro);

                // Нажимаем Enter после ввода
                Thread.sleep(500); // Краткая пауза
                metroElement.sendKeys(Keys.ENTER);
                System.out.println("Станция выбрана через Enter");
            } catch (Exception e2) {
                System.err.println("Не удалось выбрать станцию метро: " + e2.getMessage());
                throw e2;
            }
        }
    }

    private void selectMetroStation(String metro) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Даём время на появление списка
        Thread.sleep(1000);

        // Пробуем несколько вариантов локаторов:
        List<WebElement> metroOptions = null;

        // Вариант 1: по частичному совпадению текста
        metroOptions = driver.findElements(By.xpath("//*[contains(text(), '" + metro + "')]"));
        if (!metroOptions.isEmpty()) {
            System.out.println("Найдено " + metroOptions.size() + " станций по тексту");
            metroOptions.get(0).click();
            return;
        }

        // Вариант 2: по классу select-search__option
        metroOptions = driver.findElements(By.xpath("//div[@class='select-search__option']"));
        if (!metroOptions.isEmpty()) {
            System.out.println("Найдено " + metroOptions.size() + " станций по классу select-search__option");
            // Ищем нужную станцию по тексту
            for (WebElement option : metroOptions) {
                if (option.getText().contains(metro)) {
                    option.click();
                    return;
                }
            }
            // Если не нашли точное совпадение, выбираем первую
            metroOptions.get(0).click();
            return;
        }

        // Вариант 3: по классу Order_Text__2broi (другой возможный класс)
        metroOptions = driver.findElements(By.xpath("//div[contains(@class, 'Order_Text')]"));
        if (!metroOptions.isEmpty()) {
            System.out.println("Найдено " + metroOptions.size() + " станций по классу Order_Text");
            for (WebElement option : metroOptions) {
                if (option.getText().contains(metro)) {
                    option.click();
                    return;
                }
            }
        }

        // Вариант 4: просто первый элемент li в выпадающем списке
        metroOptions = driver.findElements(By.xpath("//ul[@class='select-search__options']/li"));
        if (!metroOptions.isEmpty()) {
            System.out.println("Найдено " + metroOptions.size() + " станций в списке ul/li");
            metroOptions.get(0).click();
            return;
        }

        // Вариант 5: любой div в выпадающем списке
        metroOptions = driver.findElements(By.xpath("//div[@class='select-search__select']//div"));
        if (!metroOptions.isEmpty()) {
            System.out.println("Найдено " + metroOptions.size() + " div элементов в выпадающем списке");
            for (WebElement option : metroOptions) {
                String text = option.getText();
                if (text != null && text.contains(metro)) {
                    option.click();
                    return;
                }
            }
        }

        // Если ничего не нашли, просто нажимаем Enter
        WebElement metroElement = driver.findElement(metroField);
        metroElement.sendKeys(Keys.ENTER);
        System.out.println("Нажали Enter для выбора станции");
    }

    public void fillSecondPage(String date, String period, boolean black, boolean grey, String comment) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        System.out.println("Заполняем дату: " + date);
        WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(whenBringDate));

        // Очищаем поле тщательно
        dateInput.clear();
        Thread.sleep(300);

        // Вводим дату посимвольно
        for (char c : date.toCharArray()) {
            dateInput.sendKeys(String.valueOf(c));
            Thread.sleep(100);
        }

        // Нажимаем Enter для закрытия календаря
        Thread.sleep(300);
        dateInput.sendKeys(Keys.ENTER);
        Thread.sleep(500);

        System.out.println("Выбираем период аренды: " + period);
        WebElement periodElement = wait.until(ExpectedConditions.elementToBeClickable(rentalPeriod));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", periodElement);
        Thread.sleep(300);
        periodElement.click();

        // Локатор для выбора периода аренды
        By periodOption = By.xpath("//div[@class='Dropdown-option' and text()='" + period + "']");
        WebElement periodOptionElement = wait.until(ExpectedConditions.elementToBeClickable(periodOption));
        Thread.sleep(300);
        periodOptionElement.click();
        Thread.sleep(500);

        System.out.println("Выбираем цвет: черный=" + black + ", серый=" + grey);

        // Прокручиваем к чекбоксам
        if (black) {
            WebElement blackCheckbox = wait.until(ExpectedConditions.elementToBeClickable(colorBlack));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", blackCheckbox);
            Thread.sleep(300);

            // Проверяем, не выбран ли уже
            if (!blackCheckbox.isSelected()) {
                blackCheckbox.click();
            }
            Thread.sleep(300);
        }

        if (grey) {
            WebElement greyCheckbox = wait.until(ExpectedConditions.elementToBeClickable(colorGrey));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", greyCheckbox);
            Thread.sleep(300);

            // Проверяем, не выбран ли уже
            if (!greyCheckbox.isSelected()) {
                greyCheckbox.click();
            }
            Thread.sleep(300);
        }

        if (comment != null && !comment.trim().isEmpty()) {
            System.out.println("Заполняем комментарий: " + comment);
            WebElement commentElement = wait.until(ExpectedConditions.elementToBeClickable(commentField));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", commentElement);
            Thread.sleep(300);
            commentElement.clear();
            commentElement.sendKeys(comment);
            Thread.sleep(300);
        }

        System.out.println("Нажимаем кнопку 'Заказать'");
        WebElement orderButtonElement = wait.until(ExpectedConditions.elementToBeClickable(orderButton));

        // Прокручиваем к кнопке
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", orderButtonElement);
        Thread.sleep(1000); // Даем время для прокрутки

        // Делаем скриншот перед нажатием
        takeScreenshot("before_click_order_button");

        // Нажимаем кнопку с помощью JavaScript (иногда обычный click не работает)
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", orderButtonElement);

        // Также делаем обычный клик на всякий случай
        Thread.sleep(500);
        orderButtonElement.click();

        System.out.println("Кнопка 'Заказать' нажата, ждем 3 секунды...");
        Thread.sleep(3000);
    }

    public void confirmOrder() throws InterruptedException {
        System.out.println("Подтверждаем заказ");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Сначала убедимся, что модальное окно видно
            if (!isConfirmationModalVisible()) {
                System.out.println("Модальное окно не видно, ждем его появления...");
                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(@class, 'Order_Modal') or contains(text(), 'Хотите оформить заказ?')]")));
                    System.out.println("Модальное окно появилось!");
                } catch (Exception e) {
                    System.out.println("Модальное окно так и не появилось");
                    throw new RuntimeException("Не появилось окно подтверждения заказа");
                }
            }

            // Теперь ищем кнопку "Да"
            WebElement confirmButtonElement = null;

            // Вариант 1: ищем в модальном окне
            try {
                confirmButtonElement = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[contains(@class, 'Order_Modal')]//button[text()='Да']")));
                System.out.println("Найдена кнопка 'Да' в модальном окне");
            } catch (Exception e) {
                System.out.println("Кнопка 'Да' не найдена в модальном окне");
            }

            // Вариант 2: просто ищем кнопку "Да" на странице
            if (confirmButtonElement == null) {
                try {
                    confirmButtonElement = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[text()='Да']")));
                    System.out.println("Найдена кнопка 'Да' на странице");
                } catch (Exception e) {
                    System.out.println("Кнопка 'Да' не найдена на странице");
                }
            }

            if (confirmButtonElement != null) {
                System.out.println("Кликаем на кнопку 'Да'");
                takeScreenshot("before_confirm_click");

                // Прокручиваем к кнопке и кликаем
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", confirmButtonElement);
                Thread.sleep(500);

                // Кликаем через JavaScript для надежности
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmButtonElement);

                Thread.sleep(2000);
                takeScreenshot("after_confirm_click");

                System.out.println("Кнопка 'Да' нажата");
            } else {
                System.out.println("Кнопка подтверждения не найдена!");
                takeScreenshot("confirm_button_not_found");

                // Проверяем, может быть окно уже исчезло и заказ оформлен
                if (driver.findElements(By.xpath("//*[contains(text(), 'Заказ оформлен')]")).size() > 0) {
                    System.out.println("Заказ уже оформлен");
                } else {
                    throw new RuntimeException("Не удалось найти кнопку подтверждения заказа");
                }
            }

        } catch (Exception e) {
            System.out.println("Ошибка при подтверждении заказа: " + e.getMessage());
            takeScreenshot("confirm_order_error");
            throw e;
        }
    }

    public String getSuccessMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Пробуем несколько вариантов локаторов для сообщения об успехе
        WebElement message = null;

        try {
            // Вариант 1: текущий локатор
            message = wait.until(ExpectedConditions.visibilityOfElementLocated(orderSuccessMessage));
        } catch (Exception e) {
            System.out.println("Сообщение не найдено по текущему локатору, пробуем другие...");

            // Вариант 2: по другому классу
            try {
                message = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class, 'Order_ModalHeader') and contains(text(), 'Заказ оформлен')]")));
            } catch (Exception e2) {
                System.out.println("Сообщение не найдено по второму локатору");

                // Вариант 3: ищем любое сообщение об оформлении заказа
                List<WebElement> messages = driver.findElements(By.xpath("//*[contains(text(), 'Заказ оформлен')]"));
                if (!messages.isEmpty()) {
                    message = messages.get(0);
                }
            }
        }

        if (message != null) {
            String text = message.getText();
            System.out.println("Текст сообщения: " + text);
            return text;
        } else {
            return "Сообщение об успешном оформлении не найдено";
        }
    }

    public void clickOrderButtonAgain() {
        System.out.println("Нажимаем кнопку 'Заказать' еще раз");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement orderButtonElement = wait.until(ExpectedConditions.elementToBeClickable(orderButton));

            // Прокручиваем к кнопке
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", orderButtonElement);
            Thread.sleep(1000);

            // Делаем скриншот перед повторным нажатием
            takeScreenshot("before_retry_order_button");

            // Пробуем разные способы клика
            System.out.println("Пробуем клик через JavaScript...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", orderButtonElement);

            Thread.sleep(1000);

            // Если окно подтверждения не появилось, пробуем еще раз
            if (!isConfirmationModalVisible()) {
                System.out.println("Окно подтверждения не появилось, пробуем обычный клик...");
                orderButtonElement.click();
            }

            Thread.sleep(2000);

        } catch (Exception e) {
            System.out.println("Ошибка при повторном нажатии кнопки 'Заказать': " + e.getMessage());
            takeScreenshot("retry_order_error");
        }
    }

    private void takeScreenshot(String name) {
        try {
            // Создаем папку для скриншотов, если её нет
            File screenshotsDir = new File("screenshots");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
            }

            File screenshot = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.FILE);

            // Определяем имя браузера для имени файла
            String browserName = "unknown";
            if (driver instanceof ChromeDriver) {
                browserName = "chrome";
            } else if (driver instanceof FirefoxDriver) {
                browserName = "firefox";
            }

            // Используем стандартные средства Java для копирования файла
            Files.copy(
                    screenshot.toPath(),
                    new File("screenshots/" + browserName + "_order_page_" + name + ".png").toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            System.out.println("Скриншот OrderPage сохранен: screenshots/" + browserName + "_order_page_" + name + ".png");
        } catch (Exception e) {
            System.err.println("Не удалось сделать скриншот в OrderPage: " + e.getMessage());
        }
    }

    public boolean isConfirmationModalVisible() {
        try {
            // Проверяем несколько возможных индикаторов модального окна
            boolean modalVisible = driver.findElements(By.xpath("//div[contains(@class, 'Order_Modal')]")).size() > 0;
            boolean yesButtonVisible = driver.findElements(By.xpath("//button[text()='Да']")).size() > 0;
            boolean orderTextVisible = driver.findElements(By.xpath("//*[contains(text(), 'Хотите оформить заказ?')]")).size() > 0;

            System.out.println("Проверка модального окна: modal=" + modalVisible +
                    ", yesButton=" + yesButtonVisible +
                    ", orderText=" + orderTextVisible);

            return modalVisible || yesButtonVisible || orderTextVisible;
        } catch (Exception e) {
            System.out.println("Ошибка при проверке модального окна: " + e.getMessage());
            return false;
        }
    }
}