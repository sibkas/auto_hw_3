import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class OrderTests {

    private WebDriver driver;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup(); // автоматически скачивает нужный chromedriver под систему
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless"); // запускает браузер в headless режиме
        driver = new ChromeDriver(options);
    }

    @Test
    void shouldSubmitRequest() {
        driver.get("http://localhost:9999");

        driver.findElement(By.name("name")).sendKeys("Иван Иванов");
        driver.findElement(By.name("phone")).sendKeys("+79991112233");
        driver.findElement(By.cssSelector("span.checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button.button_view_extra")).click();

        String expected = "Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.";
        String actual = driver.findElement(By.cssSelector("[data-test-id='order-success']")).getText().trim();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void shouldShowValidationForName() {
        driver.get("http://localhost:9999");

        driver.findElement(By.name("name")).sendKeys("abc"); // неверное имя, например латиница
        driver.findElement(By.name("phone")).sendKeys("+79991112233"); // валидный телефон
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button.button_view_extra")).click();

        // Проверяем, что поле имя подсвечено классом input_invalid
        String classAttr = driver.findElement(By.cssSelector("[data-test-id='name']")).getAttribute("class");
        Assertions.assertTrue(classAttr.contains("input_invalid"));

        // Проверяем текст ошибки
        String errorText = driver.findElement(By.cssSelector("[data-test-id='name'] .input__sub")).getText().trim();
        Assertions.assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", errorText);
    }

    @Test
    void shouldShowValidationForPhone() {
        driver.get("http://localhost:9999");

        driver.findElement(By.name("name")).sendKeys("Иван Иванов");
        driver.findElement(By.name("phone")).sendKeys("12345"); // неверный телефон
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button.button_view_extra")).click();

        String classAttr = driver.findElement(By.cssSelector("[data-test-id='phone']")).getAttribute("class");
        Assertions.assertTrue(classAttr.contains("input_invalid"));

        String errorText = driver.findElement(By.cssSelector("[data-test-id='phone'] .input__sub")).getText().trim();
        Assertions.assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", errorText);
    }

    @Test
    void shouldShowValidationForAgreement() {
        driver.get("http://localhost:9999");

        driver.findElement(By.name("name")).sendKeys("Иван Иванов");
        driver.findElement(By.name("phone")).sendKeys("+79991112233");
        // не кликаем на чекбокс
        driver.findElement(By.cssSelector("button.button.button_view_extra")).click();

        String classAttr = driver.findElement(By.cssSelector("[data-test-id='agreement']")).getAttribute("class");
        Assertions.assertTrue(classAttr.contains("input_invalid"));
    }



    @Test
    void shouldShowValidationForEmptyForm() {
        driver.get("http://localhost:9999");
        driver.findElement(By.cssSelector("button.button.button_view_extra")).click();

        String nameClass = driver.findElement(By.cssSelector("[data-test-id='name']")).getAttribute("class");
        Assertions.assertTrue(nameClass.contains("input_invalid"));
        String nameError = driver.findElement(By.cssSelector("[data-test-id='name'] .input__sub")).getText().trim();
        Assertions.assertEquals("Поле обязательно для заполнения", nameError);
    }

    @Test
    void shouldShowValidationForEmptyName() {
        driver.get("http://localhost:9999");
        driver.findElement(By.name("phone")).sendKeys("+79991112233");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button.button_view_extra")).click();

        String nameClass = driver.findElement(By.cssSelector("[data-test-id='name']")).getAttribute("class");
        Assertions.assertTrue(nameClass.contains("input_invalid"));
        String nameError = driver.findElement(By.cssSelector("[data-test-id='name'] .input__sub")).getText().trim();
        Assertions.assertEquals("Поле обязательно для заполнения", nameError);
    }

    @Test
    void shouldShowValidationForEmptyPhone() {
        driver.get("http://localhost:9999");
        driver.findElement(By.name("name")).sendKeys("Иван Иванов");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button.button_view_extra")).click();

        String phoneClass = driver.findElement(By.cssSelector("[data-test-id='phone']")).getAttribute("class");
        Assertions.assertTrue(phoneClass.contains("input_invalid"));
        String phoneError = driver.findElement(By.cssSelector("[data-test-id='phone'] .input__sub")).getText().trim();
        Assertions.assertEquals("Поле обязательно для заполнения", phoneError);
    }




    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
