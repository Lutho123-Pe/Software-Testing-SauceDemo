package com.saucedemo.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.function.Function;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    // Configuration for visibility
    private static final int STEP_DELAY_MS = 1000; // 1.0-second delay between steps
    private static final int ACTION_DELAY_MS = 500; // 0.5 second delay for actions

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // ========== ENHANCED WAIT & DELAY METHODS ==========

    // Method to wait for a specific condition with timeout
    protected void waitForCondition(Function<WebDriver, Boolean> condition, int timeoutSeconds, String description) {
        try {
            System.out.println("⏳ Waiting for: " + description);
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(condition);
            System.out.println("✅ Condition met: " + description);
            stepDelay("After condition: " + description);
        } catch (Exception e) {
            System.out.println("❌ Timeout waiting for: " + description);
        }
    }

    // Enhanced step delay with message
    protected void stepDelay(String stepDescription) {
        System.out.println("⏸️  Pausing after: " + stepDescription);
        delay(STEP_DELAY_MS);
    }

    // Enhanced action delay with message
    protected void actionDelay(String actionDescription) {
        System.out.println("🔄 Performing: " + actionDescription);
        delay(ACTION_DELAY_MS);
    }

    // Wait for element to be visible and then click
    protected void waitAndClick(WebElement element, String elementDescription) {
        stepDelay("Before clicking " + elementDescription);
        waitForElementToBeClickable(element);
        element.click();
        actionDelay("Clicked " + elementDescription);
    }

    // Wait for element to be visible and then type
    protected void waitAndType(WebElement element, String text, String elementDescription) {
        stepDelay("Before typing into " + elementDescription);
        waitForElementToBeVisible(element);
        element.clear();
        element.sendKeys(text);
        actionDelay("Typed '" + text + "' into " + elementDescription);
    }

    // Basic delay method
    protected void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ========== BASIC WAIT METHODS ==========

    protected void waitForElementToBeVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected void waitForElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected void click(WebElement element) {
        waitForElementToBeClickable(element);
        element.click();
    }

    protected void type(WebElement element, String text) {
        waitForElementToBeVisible(element);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(WebElement element) {
        waitForElementToBeVisible(element);
        return element.getText();
    }

    protected boolean isElementDisplayed(WebElement element) {
        try {
            waitForElementToBeVisible(element);
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}