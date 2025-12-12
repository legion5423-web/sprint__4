package ru.yandex.praktikum.tests.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public class DriverFactory {

    public static WebDriver createDriver(String browser) {
        WebDriver driver;
        switch (browser.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                // Закомментируйте headless для отладки
                // chromeOptions.addArguments("--no-sandbox", "--headless", "--disable-dev-shm-usage");
                chromeOptions.addArguments("--no-sandbox", "--disable-dev-shm-usage");
                // Добавим настройки для лучшей видимости
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                chromeOptions.addArguments("--disable-infobars");
                driver = new ChromeDriver(chromeOptions);
                break;

            case "firefox":
                // Укажите путь к geckodriver
                // Убедитесь, что путь указан правильно с расширением .exe для Windows
                System.setProperty("webdriver.gecko.driver", "C:/Users/admin/Downloads/geckodriver-v0.36.0-win64/geckodriver.exe");
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--start-maximized");
                driver = new FirefoxDriver(firefoxOptions);
                break;

            default:
                throw new IllegalArgumentException("Неподдерживаемый браузер: " + browser);
        }

        // Установим неявное ожидание
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.manage().window().maximize();

        return driver;
    }
}