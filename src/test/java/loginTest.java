import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import service.CustomListener;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class loginTest {

    private final static SelenideElement logoImg = $x("//*[@id='logo']");
    private final SelenideElement logInButton = $x("//button[@id='login-button']");
    private final SelenideElement userNameInput = $x("//input[@name='username']");
    private final SelenideElement passwordInput = $x("//input[@name='password']");
    private final SelenideElement smsInput = $x("//input[@id='otp-code']");
    private final SelenideElement codeButton = $x("//button[@id='login-otp-button']");
    private final static String baseUrl = "https://idemo.bspb.ru/";
    private final SelenideElement appName = $x("//div[@class='environment print-hidden']");
    private final SelenideElement curAva = $x("//a[@id='user-avatar']");
    private final SelenideElement newAva = $x("//div[@id='avatars']//img[@data-avatar='24.png']");
    private final SelenideElement labelAva = $x("//div[@id='avatars-form']/label");

    @BeforeAll
    static void beforeConfig() {
        Configuration.timeout = 3000; // Умное ожидание появление элемента на странице
        Configuration.browserSize = "1920x1080"; // Умно
        SelenideLogger.addListener(CustomListener.class.getCanonicalName(), new CustomListener()); // добавляем "Слушателя" который будет отлавливать наши шаги
    }

    @BeforeEach
    void before() {
        open(baseUrl);
        logoImg.shouldBe(visible);
    }

    @Test
    @Description("Авторизация в интернет банке БСБП")
    void login() {
        userNameInput.shouldBe(visible).val("demo");
        passwordInput.shouldBe(visible).val("demo");
        logInButton.shouldBe(visible).click();
        smsInput.shouldBe(visible).val("0000");
        codeButton.shouldBe(visible).click();
        logoImg.shouldBe(visible);
        appName.shouldBe(visible, Duration.ofSeconds(10));
        appCheckNmae();
        setNewAva();
        frameOut();


    }

    @Step("Проверка наименования приложения")
        // Анотация бибилиотеки Allure позволяет выделать в отдельный степ (Шаг) какое-то действие/проверку в автотесте
        // также библиотека Allure считает анотации Before, After, Description
    void appCheckNmae() {
        appName.shouldHave(text("bank")); // матчеры интегрированные в селенид. Делает скриншот
//        assertThat("Не соответствует текст", appName.getText(), containsString("bank")); // матчеры как отдельная библиотека. Не делает скриншот
    }

    @Step("Изменить аватар")
        // Анотация бибилиотеки Allure позволяет выделать в отдельный степ (Шаг) какое-то действие/проверку в автотесте
        // также библиотека Allure считает анотации Before, After, Description
    void setNewAva() {
        curAva.click();
        switchTo().frame($x("(//div[@id='contentbar']/iframe)"));
        labelAva.shouldBe(visible);
        labelAva.shouldHave(text("Avatay"));
        newAva.click();
    }

    @Step("Выйти из фрейма")
        // Анотация бибилиотеки Allure позволяет выделать в отдельный степ (Шаг) какое-то действие/проверку в автотесте
        // также библиотека Allure считает анотации Before, After, Description
    void frameOut() {
        switchTo().defaultContent();
        logoImg.shouldBe(visible).click();
    }


    @AfterEach
    void after() {
        closeWebDriver();
    }
}

