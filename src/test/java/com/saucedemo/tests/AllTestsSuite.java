package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.*;
import com.saucedemo.utils.ExtentReportManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class AllTestsSuite extends BaseTest {

    private LoginPage loginPage;
    private ProductsPage productsPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private String currentTestName;
    private ExtentTest extentTest;

    // Add delay for visual observation (in milliseconds)
    private static final int VISUAL_DELAY = 1000; // 1 second delay between actions
    private static final int STEP_DELAY = 2000;   // 2 second delay for important steps

    @BeforeSuite
    public void beforeSuite() {
        // Initialize ExtentReports with timestamp
        ExtentReportManager.initializeReport();
        System.out.println("📊 ExtentReports initialized");
    }

    @AfterSuite
    public void afterSuite() {
        // Flush and close ExtentReports
        ExtentReportManager.flushReport();
        System.out.println("📊 ExtentReports generated and saved");

        // Optional: Open report automatically
        try {
            ExtentReportManager.openReport();
            System.out.println("📊 Report opened in browser");
        } catch (Exception e) {
            System.out.println("⚠️ Could not open report automatically: " + e.getMessage());
        }
    }

    @BeforeMethod
    @Parameters("browser")
    public void setupTest(@Optional("chrome") String browser, Method method) {
        // Call parent setup
        super.setUp(browser);

        // Visual feedback - show browser being used
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🚀 STARTING TEST: " + method.getName());
        System.out.println("🌐 BROWSER: " + browser.toUpperCase());
        System.out.println("=".repeat(50));

        // Initialize page objects
        loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
        cartPage = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);

        // Get test name
        currentTestName = method.getName();

        // Create ExtentReport test entry
        String testNameWithBrowser = currentTestName + " [" + browser.toUpperCase() + "]";
        ExtentReportManager.createTest(testNameWithBrowser, browser);
        ExtentReportManager.logInfo(currentTestName, browser,
                "Starting test on " + browser.toUpperCase() + " browser");

        // Add delay for visual observation
        delay(VISUAL_DELAY);
    }

    @AfterMethod
    public void teardownTest(ITestResult result) {
        // Take screenshot for report - check if driver exists
        if (driver != null) {
            String status = result.getStatus() == ITestResult.FAILURE ? "FAILURE" : "SUCCESS";
            ExtentReportManager.addScreenshot(currentTestName, browserName, driver, status);
        }

        // Log test result
        if (result.getStatus() == ITestResult.SUCCESS) {
            ExtentReportManager.logPass(currentTestName, browserName, "Test passed successfully");
            System.out.println("✅ TEST PASSED: " + currentTestName);
        } else if (result.getStatus() == ITestResult.FAILURE) {
            String errorMsg = result.getThrowable().getMessage();
            ExtentReportManager.logFail(currentTestName, browserName,
                    "Test failed: " + errorMsg);
            System.out.println("❌ TEST FAILED: " + currentTestName);
            System.out.println("   Error: " + errorMsg);
        } else {
            ExtentReportManager.logInfo(currentTestName, browserName, "Test skipped");
            System.out.println("⚠️ TEST SKIPPED: " + currentTestName);
        }

        // Add separator for visual clarity
        System.out.println("-".repeat(50));

        // Add delay before next test
        delay(VISUAL_DELAY);

        // Call parent teardown
        super.tearDown();
    }

    // ========== HELPER METHOD FOR VISUAL FEEDBACK ==========
    private void visualStep(String actionDescription) {
        System.out.println("🟡 " + actionDescription);
        ExtentReportManager.logInfo(currentTestName, browserName, actionDescription);
        delay(VISUAL_DELAY);
    }

    private void importantStep(String actionDescription) {
        System.out.println("🔵 " + actionDescription);
        ExtentReportManager.logInfo(currentTestName, browserName, "IMPORTANT: " + actionDescription);
        delay(STEP_DELAY);
    }

    // ========== LOGIN TESTS ==========

    @Test(priority = 1, groups = {"login", "smoke"})
    public void testSuccessfulLogin() {
        ExtentReportManager.logInfo(currentTestName, browserName, "Testing successful login");
        visualStep("Starting successful login test");

        importantStep("Entering username: standard_user");
        importantStep("Entering password: secret_sauce");
        loginPage.login("standard_user", "secret_sauce");

        visualStep("Clicking login button");
        waitForCondition(d -> productsPage.getPageTitle().equals("Products"), 10, "Products page");

        Assert.assertEquals(productsPage.getPageTitle(), "Products");
        visualStep("Successfully logged in and redirected to Products page");
        ExtentReportManager.addScreenshot(currentTestName, browserName, driver, "LoginSuccess");
    }

    @Test(priority = 2, groups = {"login"})
    public void testLockedOutUser() {
        ExtentReportManager.logInfo(currentTestName, browserName, "Testing locked out user");
        visualStep("Starting locked out user test");

        importantStep("Entering username: locked_out_user");
        importantStep("Entering password: secret_sauce");
        loginPage.login("locked_out_user", "secret_sauce");
        waitForCondition(d -> loginPage.getErrorMessage().length() > 0, 10, "Error message");

        Assert.assertTrue(loginPage.getErrorMessage().contains("locked out"));
        visualStep("Verified locked out error message appears");
        ExtentReportManager.addScreenshot(currentTestName, browserName, driver, "LockedOut");
    }

    // ... [Rest of your tests with visualStep and importantStep calls] ...

    @Test(priority = 4, groups = {"inventory", "smoke"})
    public void testInventoryItemsDisplay() {
        ExtentReportManager.logInfo(currentTestName, browserName, "Testing inventory items display");
        visualStep("Starting inventory display test");

        // Login first
        importantStep("Logging in as standard_user");
        loginPage.login("standard_user", "secret_sauce");

        importantStep("Counting displayed products");
        int itemCount = productsPage.getProductCount();
        Assert.assertEquals(itemCount, 6, "Expected 6 items but found " + itemCount);

        visualStep("Verified 6 items displayed correctly");
        ExtentReportManager.addScreenshot(currentTestName, browserName, driver, "InventoryItems");
    }

    @Test(priority = 5, groups = {"inventory", "smoke"})
    public void testAddItemToCart() {
        ExtentReportManager.logInfo(currentTestName, browserName, "Testing add item to cart");
        visualStep("Starting add to cart test");

        // Login first
        importantStep("Logging in as standard_user");
        loginPage.login("standard_user", "secret_sauce");

        importantStep("Clicking 'Add to Cart' for first item");
        productsPage.addItemToCart(0);
        waitForCondition(d -> "1".equals(productsPage.getCartBadgeCount()), 10, "Cart badge update");

        Assert.assertEquals(productsPage.getCartBadgeCount(), "1");
        visualStep("Item successfully added to cart (cart badge shows 1)");
        ExtentReportManager.addScreenshot(currentTestName, browserName, driver, "ItemAdded");
    }

    // ... [Continue adding visualStep and importantStep to all your test methods] ...

    @Test(priority = 8, groups = {"checkout", "smoke"})
    public void testCompleteCheckout() {
        ExtentReportManager.logInfo(currentTestName, browserName, "Testing complete checkout");
        visualStep("Starting complete checkout test");

        // Login and add item
        importantStep("Logging in as standard_user");
        loginPage.login("standard_user", "secret_sauce");

        importantStep("Adding item to cart");
        productsPage.addItemToCart(0);

        // Go to cart
        importantStep("Navigating to cart page");
        driver.get("https://www.saucedemo.com/cart.html");

        // Click checkout
        importantStep("Clicking checkout button");
        cartPage.clickCheckout();
        waitForCondition(d -> driver.getCurrentUrl().contains("checkout-step-one"), 10, "Checkout step 1");

        // Fill information
        importantStep("Filling checkout information: John Doe, 12345");
        checkoutPage.fillInformation("John", "Doe", "12345");

        // Continue
        importantStep("Clicking continue");
        checkoutPage.clickContinue();

        // Finish checkout
        importantStep("Clicking finish");
        checkoutPage.finishCheckout();
        waitForCondition(d -> driver.getCurrentUrl().contains("checkout-complete"), 10, "Checkout complete");

        String successMessage = checkoutPage.getSuccessMessage();
        Assert.assertEquals(successMessage, "Thank you for your order!");
        importantStep("Order completed successfully!");
        ExtentReportManager.addScreenshot(currentTestName, browserName, driver, "CheckoutComplete");
    }

    // ... [Continue with the rest of your tests] ...
}