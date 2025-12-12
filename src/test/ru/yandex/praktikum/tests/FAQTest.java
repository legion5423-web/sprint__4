package ru.yandex.praktikum.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.praktikum.pages.MainPage;
import ru.yandex.praktikum.tests.util.DriverFactory;

import java.time.Duration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class FAQTest {

    private WebDriver driver;
    private MainPage mainPage;

    @Before
    public void setUp() {
        driver = DriverFactory.createDriver("chrome");
        mainPage = new MainPage(driver);
        mainPage.open();
        mainPage.waitForLoad();
        mainPage.acceptCookies();
    }

    @Test
    public void checkFAQAnswers_OpenQuestion_ReturnsText() {
        mainPage.clickQuestion(1);

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(driver -> !mainPage.getAnswerText(1).isEmpty());

        String answer = mainPage.getAnswerText(1);
        assertThat("Ответ должен содержать 'Сутки — 400 рублей'", answer, containsString("Сутки — 400 рублей"));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
