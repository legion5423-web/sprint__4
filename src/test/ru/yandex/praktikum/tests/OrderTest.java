package ru.yandex.praktikum.tests;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import ru.yandex.praktikum.pages.MainPage;
import ru.yandex.praktikum.pages.OrderPage;
import ru.yandex.praktikum.tests.util.DriverFactory;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class OrderTest {
// Вопрос: тесты падают при нажатии кнопки "Заказать", так и должно быть? (я вертел и так и этак, и в результате переусложнил)
    private WebDriver driver;
    private MainPage mainPage;
    private OrderPage orderPage;

    private String orderButtonType; // "top" или "bottom"
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

    public OrderTest(String orderButtonType, String name, String surname, String address, String metro, String phone,
                     String date, String period, boolean colorBlack, boolean colorGrey, String comment) {
        this.orderButtonType = orderButtonType;
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
                // Тест через верхнюю кнопку "Заказать"
                {"top", "Иван", "Иванов", "ул. Ленина, д.1", "Черкизовская", "+79001234567",
                        "15.04.2025", "двое суток", true, false, "Оставьте у двери"},

                // Тест через нижнюю кнопку "Заказать"
                {"bottom", "Анна", "Петрова", "пр-т Мира, д.50", "Тверская", "+79876543210",
                        "20.04.2025", "четверо суток", false, true, "Позвоните перед приездом"}
        });
    }

    @Before
    public void setUp() {

        driver = DriverFactory.createDriver("chrome");
        mainPage = new MainPage(driver);
        orderPage = new OrderPage(driver);
        mainPage.open();
        mainPage.waitForLoad();
        mainPage.acceptCookies();
    }

    @Test
    public void orderScooter_CheckSuccessMessage_Appears() {
        // Открываем форму заказа через нужную кнопку
        if ("top".equals(orderButtonType)) {
            mainPage.clickTopOrderButton();
        } else {
            mainPage.clickBottomOrderButton();
        }

        orderPage.waitForLoad();
        orderPage.fillFirstPage(name, surname, address, metro, phone);
        orderPage.waitForSecondPage();
        orderPage.fillSecondPage(date, period, colorBlack, colorGrey, comment);

        // Проверяем, что появилось окно подтверждения
        Assert.assertTrue("Должно появиться окно подтверждения заказа",
                orderPage.isConfirmationModalVisible());

        // Подтверждаем заказ
        orderPage.confirmOrder();

        // Проверяем, что появилось сообщение об успешном оформлении
        Assert.assertTrue("Должно появиться сообщение об успешном оформлении заказа",
                orderPage.isSuccessMessageDisplayed());

        // Проверяем текст сообщения
        String message = orderPage.getSuccessMessageText();
        assertThat("Сообщение должно содержать 'Заказ оформлен'",
                message, containsString("Заказ оформлен"));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
//испр?