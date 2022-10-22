package service;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.SelenideLog;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.selenide.AllureSelenide;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;

import static io.qameta.allure.util.ResultsUtils.getStatus;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;

public class CustomListener extends AllureSelenide {
    private final AllureLifecycle lifecycle;
    private final boolean includeSelenideLocatorsSteps = true;

    public CustomListener() {
        this(Allure.getLifecycle());
    }

    public CustomListener(final AllureLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    private boolean stepsShouldBeLogged(final LogEvent event) {
        //  other customer Loggers could be configured, they should be logged
        return includeSelenideLocatorsSteps || !(event instanceof SelenideLog);
    }

    /**
     * Метод который описывает вывод в Allure Отчете
     */
    private StepResult selenideLocatorEventToNiceString(LogEvent event) {
        String name = event.toString();
        Matcher matcher;
        if (Patterns.reText.matcher(name).find()) {
            return null;
        } else if (Patterns.reCreate.matcher(name).find()) {
            return null;
        } else if (Patterns.reIs.matcher(name).find()) {
            return null;
        } else if (Patterns.reMatch.matcher(name).find()) {
            return null;
        } else if (Patterns.reScrollIntoView.matcher(name).find()) {
            return null;
        } else if ((matcher = Patterns.reShouldHave.matcher(name)).find()) {
            return new StepResult().setName("Элемент \"" + matcher.group(1) + "\" должен иметь " + matcher.group(2));
        } else if ((matcher = Patterns.reShouldBe.matcher(name)).find()) {
            return new StepResult().setName("Элемент \"" + matcher.group(1) + "\" должен быть " + matcher.group(2));
        } else if ((matcher = Patterns.reShould.matcher(name)).find()) {
            return new StepResult().setName("Элемент \"" + matcher.group(1) + "\" должен " + matcher.group(2));
        } else if ((matcher = Patterns.reShouldNotHave.matcher(name)).find()) {
            return new StepResult().setName("Элемент \"" + matcher.group(1) + "\" не должен иметь " + matcher.group(2));
        } else if ((matcher = Patterns.reShouldNotBe.matcher(name)).find()) {
            return new StepResult().setName("Элемент \"" + matcher.group(1) + "\" не должен быть " + matcher.group(2));
        } else if ((matcher = Patterns.reShouldNot.matcher(name)).find()) {
            return new StepResult().setName("Элемент \"" + matcher.group(1) + "\" не должен " + matcher.group(2));
        } else if ((matcher = Patterns.reOpen.matcher(name)).find()) {
            return new StepResult().setName("Открыть страницу " + matcher.group(1));
        } else if ((matcher = Patterns.reClick.matcher(name)).find()) {
            return new StepResult().setName("Клик по элементу \"" + matcher.group(1) + "\"");
        } else if ((matcher = Patterns.reSetValue.matcher(name)).find()) {
            return new StepResult().setName("Установить значение \"" + matcher.group(2) + "\" для элемента \"" + matcher.group(1) + "\"");
        } else if ((matcher = Patterns.reSendKeys.matcher(name)).find()) {
            return new StepResult().setName("Отправить нажатия \"" + matcher.group(2) + "\" элементу \"" + matcher.group(1) + "\"");
        } else if ((matcher = Patterns.reAssertThat.matcher(name)).find()) {
            return new StepResult().setName("Проверка: " + matcher.group(2));
        } else if ((matcher = Patterns.reInfo.matcher(name)).find()) {
            return new StepResult().setName("(info) " + matcher.group(1) + ": " + matcher.group(2));
        } else {
            return new StepResult().setName(name);
        }
    }

    @Override
    public void beforeEvent(final LogEvent event) {
        if (stepsShouldBeLogged(event)) {
            lifecycle.getCurrentTestCaseOrStep().ifPresent(parentUuid -> {
                final String uuid = UUID.randomUUID().toString();
                StepResult stepResult = selenideLocatorEventToNiceString(event);
                if (stepResult != null) {
                    lifecycle.startStep(parentUuid, uuid, stepResult);
                } else {
                    lifecycle.stopStep(uuid);
                }
            });
        }
    }


    //метод который генерирует скриншот
    @Override
    public void afterEvent(final LogEvent event) {
        if (event.getStatus().equals(LogEvent.EventStatus.FAIL)) {
            lifecycle.getCurrentTestCaseOrStep().flatMap(parentUuid -> getScreenshotBytes()).ifPresent(bytes -> lifecycle.addAttachment("Screenshot", "image/png", "png", bytes));
        }

        StepResult stepR = selenideLocatorEventToNiceString(event);
        if (stepR != null) {
            if (stepsShouldBeLogged(event)) {
                lifecycle.getCurrentTestCaseOrStep().ifPresent(parentUuid -> {
                    switch (event.getStatus()) {
                        case PASS:
                            lifecycle.updateStep(step -> step.setStatus(Status.PASSED));
                            break;
                        case FAIL:
                            lifecycle.updateStep(stepResult -> {
                                stepResult.setStatus(getStatus(event.getError()).orElse(Status.BROKEN));
                                stepResult.setStatusDetails(getStatusDetails(event.getError()).orElse(new StatusDetails()));
                            });
                            break;
                        default:
                            break;
                    }
                    lifecycle.stopStep();
                });
            }
        }
    }

    private static Optional<byte[]> getScreenshotBytes() {
        try {
            return WebDriverRunner.hasWebDriverStarted()
                    ? Optional.of(((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES))
                    : Optional.empty();
        } catch (WebDriverException e) {
            return Optional.empty();
        }
    }
}
