package com.saucedemo.base;

import com.saucedemo.utils.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;
import java.util.function.Function;

public class BaseTest {

    protected WebDriver driver;
    protected String browserName;
    protected org.openqa.selenium.support.ui.WebDriverWait wait;

    // Add SauceDemo URL constant
    protected static final String SAUCE_DEMO_URL = "https://www.saucedemo.com/";

    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser) {
        browserName = browser;
        driver = DriverManager.getDriver(browser);

        // ADD THIS LINE: Navigate to SauceDemo
        navigateToSauceDemo();

        wait = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        // Optional: Take screenshot before quitting (for debugging)
        takeScreenshotOnFailure();

        DriverManager.quitDriver();
    }

    // NEW METHOD: Navigate to SauceDemo
    protected void navigateToSauceDemo() {
        try {
            System.out.println("🌐 Navigating to SauceDemo...");
            driver.get(SAUCE_DEMO_URL);

            // Verify we reached the correct page
            String currentUrl = driver.getCurrentUrl();
            String pageTitle = driver.getTitle();

            System.out.println("✅ Successfully navigated to:");
            System.out.println("   URL: " + currentUrl);
            System.out.println("   Title: " + pageTitle);

            // Wait for page to load completely
            waitForPageToLoad();

        } catch (Exception e) {
            System.out.println("❌ Failed to navigate to SauceDemo: " + e.getMessage());
            throw new RuntimeException("Navigation to SauceDemo failed", e);
        }
    }

    // NEW METHOD: Optional screenshot on failure
    protected void takeScreenshotOnFailure() {
        // This can be enhanced to only take screenshots on test failure
        // For now, we'll take one at the end of each test
        String screenshotPath = DriverManager.captureScreenshot(driver, browserName);
        if (!screenshotPath.isEmpty()) {
            System.out.println("📸 Test completed. Screenshot saved: " + screenshotPath);
        }
    }

    // Utility methods
    protected void waitForCondition(Function<WebDriver, Boolean> condition, int timeoutSeconds, String description) {
        try {
            System.out.println("⏳ Waiting for: " + description);
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(condition);
            System.out.println("✅ Condition met: " + description);
        } catch (Exception e) {
            System.out.println("❌ Timeout waiting for: " + description);

            // Take screenshot when condition times out
            DriverManager.captureScreenshot(driver, browserName + "_timeout");

            throw e;
        }
    }

    protected void stepDelay(String stepDescription) {
        System.out.println("⏸️  Pausing after: " + stepDescription);
        delay(1000);
    }

    protected void actionDelay(String actionDescription) {
        System.out.println("🔄 Performing: " + actionDescription);
        delay(500);
    }

    protected void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void waitForPageToLoad() {
        try {
            // Wait for document.readyState to be "complete"
            wait.until(driver -> {
                Object result = ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript("return document.readyState");
                return result != null && result.equals("complete");
            });
            System.out.println("✅ Page loaded completely");

            // Additional short delay for any animations
            Thread.sleep(500);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("⚠️ Page load check encountered an error: " + e.getMessage());
        }
    }

    // NEW METHOD: Refresh the page
    protected void refreshPage() {
        System.out.println("🔄 Refreshing page...");
        driver.navigate().refresh();
        waitForPageToLoad();
    }

    // NEW METHOD: Navigate to a specific URL
    protected void navigateToURL(String url) {
        System.out.println("🌐 Navigating to: " + url);
        driver.get(url);
        waitForPageToLoad();
    }
}