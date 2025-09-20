import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class OrderTests {

    private WebDriver driver;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.get("http://localhost:9999");
    }

    @Test
    void shouldSubmitRequest() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Иванов");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79991112233");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button_view_extra")).click();

        WebElement successMessage = driver.findElement(By.cssSelector("[data-test-id='order-success']"));
        Assertions.assertTrue(successMessage.isDisplayed());
        Assertions.assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", successMessage.getText().trim());
    }

    @Test
    void shouldShowValidationForName() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("abc");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79991112233");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button_view_extra")).click();



        WebElement errorMessage = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));
        Assertions.assertTrue(errorMessage.isDisplayed());
        Assertions.assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", errorMessage.getText().trim());
    }

    @Test
    void shouldShowValidationForPhone() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Иванов");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("12345");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button_view_extra")).click();



        WebElement errorMessage = driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));
        Assertions.assertTrue(errorMessage.isDisplayed());
        Assertions.assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", errorMessage.getText().trim());
    }

    @Test
    void shouldShowValidationForAgreement() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Иванов");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79991112233");
        // чекбокс не кликаем
        driver.findElement(By.cssSelector("button.button_view_extra")).click();

        WebElement agreementInputInvalid = driver.findElement(By.cssSelector("[data-test-id='agreement'].input_invalid"));
        Assertions.assertTrue(agreementInputInvalid.isDisplayed());
    }

    @Test
    void shouldShowValidationForEmptyForm() {
        driver.findElement(By.cssSelector("button.button_view_extra")).click();



        WebElement errorMessage = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));
        Assertions.assertTrue(errorMessage.isDisplayed());
        Assertions.assertEquals("Поле обязательно для заполнения", errorMessage.getText().trim());
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
