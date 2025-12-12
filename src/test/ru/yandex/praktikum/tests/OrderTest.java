package ru.yandex.praktikum.tests;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.praktikum.pages.MainPage;
import ru.yandex.praktikum.pages.OrderPage;
import ru.yandex.praktikum.tests.util.DriverFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class OrderTest {

    private WebDriver driver;
    private MainPage mainPage;
    private OrderPage orderPage;

    private String browser;
    private String name;
    private String surname;
    private String address;
    private String metro;
    private String phone;
    private String date;
    private String period;
    private boolean colorBlack;
    private boolean colorGrey;
    private String comment;

    public OrderTest(String browser, String name, String surname, String address, String metro, String phone,
                     String date, String period, boolean colorBlack, boolean colorGrey, String comment) {
        this.browser = browser;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.metro = metro;
        this.phone = phone;
        this.date = date;
        this.period = period;
        this.colorBlack = colorBlack;
        this.colorGrey = colorGrey;
        this.comment = comment;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"chrome", "Иван", "Иванов", "ул. Ленина, д.1", "Черкизовская", "+79001234567",
                        "15.04.2025", "двое суток", true, false, "Оставьте у двери"},

                {"firefox", "Анна", "Петрова", "пр-т Мира, д.50", "Тверская", "+79876543210",
                        "20.04.2025", "четверо суток", false, true, "Позвоните перед приездом"}
        });
    }

    @Before
    public void setUp() {
        // Используем переданный параметр browser для создания драйвера
        System.out.println("Запуск теста в браузере: " + browser);
        driver = DriverFactory.createDriver(browser);
        mainPage = new MainPage(driver);
        orderPage = new OrderPage(driver);
        mainPage.open();
        mainPage.waitForLoad();
        mainPage.acceptCookies();
    }

    @Test
    public void orderScooter_CheckSuccessMessage_Appears() throws InterruptedException {
        System.out.println("Начинаем тест заказа самоката в браузере " + browser + "...");
        System.out.println("Тестовые данные: " + name + " " + surname);

        try {
            mainPage.clickTopOrderButton();
            System.out.println("Открыта форма заказа");

            orderPage.waitForLoad();
            System.out.println("Первая страница загружена");

            takeScreenshot("after_form_load");

            orderPage.fillFirstPage(name, surname, address, metro, phone);
            System.out.println("Первая страница заполнена");

            takeScreenshot("after_first_page");

            orderPage.waitForSecondPage();
            System.out.println("Вторая страница загружена");

            orderPage.fillSecondPage(date, period, colorBlack, colorGrey, comment);
            System.out.println("Вторая страница заполнена");

            takeScreenshot("after_second_page");

            // Ждем и проверяем состояние
            Thread.sleep(2000);
            takeScreenshot("before_confirmation");

            // Проверяем, появилось ли окно подтверждения
            if (isConfirmationModalVisible()) {
                System.out.println("Окно подтверждения найдено, подтверждаем заказ");
                orderPage.confirmOrder();
                takeScreenshot("after_confirmation");

                // Получаем сообщение об успешном оформлении
                String message = orderPage.getSuccessMessage();
                System.out.println("Получено сообщение: '" + message + "'");

                // Проверяем сообщение
                assertThat("Заказ должен быть успешно оформлен",
                        message.contains("Заказ оформлен"), is(true));
            } else {
                System.out.println("Окно подтверждения НЕ появилось!");
                System.out.println("Возможные причины:");
                System.out.println("1. Кнопка 'Заказать' не сработала");
                System.out.println("2. Есть ошибки валидации");
                System.out.println("3. Проблемы с JavaScript");

                takeScreenshot("no_confirmation_modal");

                // Проверяем состояние формы
                checkFormStateForErrors();

                // Попробуем кликнуть кнопку "Заказать" еще раз
                System.out.println("Пробуем нажать кнопку 'Заказать' еще раз");
                orderPage.clickOrderButtonAgain();
                Thread.sleep(3000);

                if (isConfirmationModalVisible()) {
                    System.out.println("Окно подтверждения появилось после повторного нажатия");
                    orderPage.confirmOrder();
                    takeScreenshot("after_confirmation_retry");

                    String message = orderPage.getSuccessMessage();
                    System.out.println("Получено сообщение: '" + message + "'");

                    assertThat("Заказ должен быть успешно оформлен",
                            message.contains("Заказ оформлен"), is(true));
                } else {
                    // Делаем детальный скриншот для отладки
                    takeScreenshot("form_state_debug");

                    // Проверяем ошибки валидации
                    List<WebElement> errors = driver.findElements(
                            By.xpath("//div[contains(@class, 'Input_Error')]"));

                    if (!errors.isEmpty()) {
                        System.out.println("Найдены ошибки валидации:");
                        for (WebElement error : errors) {
                            System.out.println("- " + error.getText());
                        }
                        throw new AssertionError("Есть ошибки валидации: " +
                                errors.stream().map(e -> e.getText()).collect(Collectors.joining(", ")));
                    } else {
                        throw new AssertionError("Окно подтверждения заказа не появилось после повторного нажатия");
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка в тесте: " + e.getMessage());
            e.printStackTrace();
            takeScreenshot("error_" + System.currentTimeMillis());
            throw e;
        }
    }

    private void checkFormStateForErrors() {
        try {
            System.out.println("Проверяем состояние формы:");

            // Проверяем, есть ли ошибки валидации
            List<WebElement> errors = driver.findElements(
                    By.xpath("//div[contains(@class, 'Input_Error')]"));
            System.out.println("Найдено ошибок валидации: " + errors.size());
            for (WebElement error : errors) {
                System.out.println("Ошибка: " + error.getText());
            }

            // Проверяем, активна ли кнопка "Заказать"
            WebElement orderBtn = driver.findElement(
                    By.xpath("//button[text()='Заказать']"));
            System.out.println("Кнопка 'Заказать' активна: " + orderBtn.isEnabled());
            System.out.println("Кнопка 'Заказать' видима: " + orderBtn.isDisplayed());

        } catch (Exception e) {
            System.out.println("Ошибка при проверке состояния формы: " + e.getMessage());
        }
    }

    private boolean isConfirmationModalVisible() {
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

    private void takeScreenshot(String name) {
        try {
            // Создаем папку для скриншотов, если её нет
            File screenshotsDir = new File("screenshots");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
            }

            File screenshot = ((org.openqa.selenium.TakesScreenshot) driver)
                    .getScreenshotAs(org.openqa.selenium.OutputType.FILE);

            // Используем стандартные средства Java для копирования файла
            Files.copy(
                    screenshot.toPath(),
                    new File("screenshots/" + browser + "_" + name + ".png").toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            System.out.println("Скриншот сохранен: screenshots/" + browser + "_" + name + ".png");
        } catch (Exception e) {
            System.err.println("Не удалось сделать скриншот: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        if (driver != null) {
            System.out.println("Завершение теста в браузере " + browser);
            driver.quit();
        }
    }
}