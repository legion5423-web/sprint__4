package ru.yandex.praktikum.tests.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class DriverFactory {

    public static WebDriver createDriver() {
        return createDriver("chrome");
    }

    public static WebDriver createDriver(String browser) {
        WebDriver driver;

        // Всегда используем Chrome для единого окружения
        if (!"chrome".equalsIgnoreCase(browser)) {
            System.out.println("Внимание: Тесты настроены на запуск в Chrome. Браузер '" + browser + "' будет заменен на Chrome.");
        }

        ChromeOptions chromeOptions = new ChromeOptions();

        // Базовые настройки для стабильной работы
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");

        // Настройки для лучшей видимости и стабильности
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
        chromeOptions.addArguments("--disable-infobars");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--disable-notifications");

        // Инициализация драйвера
        try {
            driver = new ChromeDriver(chromeOptions);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось запустить Chrome. Убедитесь, что Chrome установлен: " + e.getMessage(), e);
        }

        // Установим неявное ожидание
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));


        driver.manage().window().maximize();

        return driver;
    }
}