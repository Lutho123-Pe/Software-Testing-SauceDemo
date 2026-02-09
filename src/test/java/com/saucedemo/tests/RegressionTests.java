package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.*;
import com.saucedemo.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

public class RegressionTests extends BaseTest {

    private LoginPage loginPage;
    private ProductsPage productsPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private String currentTestName;

    @BeforeMethod
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser, Method method) {
        // Call parent setup
        super.setUp(browser);

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
        ExtentReportManager.logInfo(testNameWithBrowser, browser,
                "Starting regression test on " + browser.toUpperCase() + " browser");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Take screenshot for report
        if (driver != null) {
            ExtentReportManager.addScreenshot(currentTestName, browserName, driver,
                    result.getStatus() == ITestResult.FAILURE ? "FAILURE" : "SUCCESS");
        }

        // Log test result
        if (result.getStatus() == ITestResult.SUCCESS) {
            ExtentReportManager.logPass(currentTestName, browserName, "Test passed successfully");
        } else if (result.getStatus() == ITestResult.FAILURE) {
            ExtentReportManager.logFail(currentTestName, browserName,
                    "Test failed: " + result.getThrowable().getMessage());
        } else {
            ExtentReportManager.logInfo(currentTestName, browserName, "Test skipped");
        }

        // Call parent teardown
        super.tearDown();
    }

    @Test(priority = 1)
    public void testInvalidLogin() {
        ExtentReportManager.logInfo(currentTestName, browserName, "Testing invalid login credentials");

        loginPage.login("invalid_user", "invalid_password");

        // Wait for error message
        waitForCondition(d -> !loginPage.getErrorMessage().isEmpty(), 10, "Error message to appear");

        // Verify error message
        if (!loginPage.getErrorMessage().isEmpty()) {
            String errorMessage = loginPage.getErrorMessage();
            Assert.assertTrue(errorMessage.contains("Username and password do not match"),
                    "Expected error message not found. Actual: " + errorMessage);
            ExtentReportManager.logPass(currentTestName, browserName,
                    "Error message displayed correctly: " + errorMessage);
        } else {
            ExtentReportManager.logFail(currentTestName, browserName, "Error message not displayed");
            Assert.fail("Error message should be displayed for invalid login");
        }

        ExtentReportManager.addScreenshot(currentTestName, browserName, driver, "InvalidLogin");
    }

    @Test(priority = 2)
    public void testValidLogin() {
        ExtentReportManager.logInfo(currentTestName, browserName, "Testing valid login credentials");

        loginPage.login("standard_user", "secret_sauce");

        // Wait for either error message or successful login
        waitForCondition(d -> {
            String errorMsg = loginPage.getErrorMessage();
            boolean hasError = errorMsg != null && !errorMsg.isEmpty();
            boolean isProductsPage = productsPage.isProductsPageDisplayed();
            return hasError || isProductsPage;
        }, 10, "Login to complete");

        // Verify login result
        String errorMessage = loginPage.getErrorMessage();
        if (errorMessage != null && !errorMessage.isEmpty()) {
            ExtentReportManager.logFail(currentTestName, browserName,
                    "Login failed with error: " + errorMessage);
            Assert.fail("Login should not fail with valid credentials");
        } else if (productsPage.isProductsPageDisplayed()) {
            ExtentReportManager.logPass(currentTestName, browserName,
                    "Login successful and redirected to products page");
            Assert.assertTrue(driver.getCurrentUrl().contains("inventory"));
        } else {
            ExtentReportManager.logFail(currentTestName, browserName,
                    "Neither error message nor products page appeared");
            Assert.fail("Login did not complete properly");
        }

        ExtentReportManager.addScreenshot(currentTestName, browserName, driver, "ValidLogin");
    }

    @Test(priority = 3)
    public void testCompletePurchaseFlow() {
        ExtentReportManager.logInfo(currentTestName, browserName, "Testing complete purchase flow");

        // Login
        loginPage.login("standard_user", "secret_sauce");
        waitForCondition(d -> productsPage.isProductsPageDisplayed(), 10, "Products page to load");

        // Add items to cart
        productsPage.addItemToCart(0); // First item
        productsPage.addItemToCart(1); // Second item

        // Verify items added
        waitForCondition(d -> "2".equals(productsPage.getCartBadgeCount()), 10, "Cart badge to update");
        Assert.assertEquals(productsPage.getCartBadgeCount(), "2", "Should have 2 items in cart");

        // Go to cart
        productsPage.clickCartIcon();
        waitForCondition(d -> driver.getCurrentUrl().contains("cart.html"), 10, "Cart page to load");

        // Checkout
        cartPage.clickCheckout();
        waitForCondition(d -> driver.getCurrentUrl().contains("checkout-step-one"), 10, "Checkout page to load");

        // Fill checkout information
        checkoutPage.fillInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();

        waitForCondition(d -> driver.getCurrentUrl().contains("checkout-step-two"), 10, "Checkout overview page");

        // Finish checkout
        checkoutPage.finishCheckout();
        waitForCondition(d -> driver.getCurrentUrl().contains("checkout-complete"), 10, "Checkout complete page");

        // Verify success message
        waitForCondition(d -> !checkoutPage.getSuccessMessage().isEmpty(), 10, "Success message to appear");
        String successMessage = checkoutPage.getSuccessMessage();

        if (!successMessage.isEmpty()) {
            Assert.assertEquals(successMessage, "Thank you for your order!",
                    "Expected success message not found. Actual: " + successMessage);
            ExtentReportManager.logPass(currentTestName, browserName,
                    "Purchase completed successfully: " + successMessage);
        } else {
            ExtentReportManager.logFail(currentTestName, browserName,
                    "Purchase failed or success message not displayed");
            Assert.fail("Purchase should have completed successfully");
        }

        ExtentReportManager.addScreenshot(currentTestName, browserName, driver, "PurchaseComplete");
    }

    @Test(priority = 4)
    public void testAddMultipleItemsToCart() {
        ExtentReportManager.logInfo(currentTestName, browserName, "Testing add multiple items to cart");

        // Login
        loginPage.login("standard_user", "secret_sauce");

        // Add multiple items
        productsPage.addItemToCart(0);
        productsPage.addItemToCart(1);
        productsPage.addItemToCart(2);

        // Verify cart count
        waitForCondition(d -> "3".equals(productsPage.getCartBadgeCount()), 10, "Cart badge to update to 3");
        Assert.assertEquals(productsPage.getCartBadgeCount(), "3", "Should have 3 items in cart");

        ExtentReportManager.logPass(currentTestName, browserName,
                "Successfully added 3 items to cart. Cart count: " + productsPage.getCartBadgeCount());
        ExtentReportManager.addScreenshot(currentTestName, browserName, driver, "MultipleItemsAdded");
    }

    @Test(priority = 5)
    public void testLogoutFunctionality() {
        ExtentReportManager.logInfo(currentTestName, browserName, "Testing logout functionality");

        // Login first
        loginPage.login("standard_user", "secret_sauce");
        waitForCondition(d -> productsPage.isProductsPageDisplayed(), 10, "Products page");

        // Logout
        productsPage.logout();
        waitForCondition(d -> driver.getCurrentUrl().contains("saucedemo.com") &&
                !driver.getCurrentUrl().contains("inventory"), 10, "Login page to appear");

        // Verify logout
        Assert.assertTrue(driver.getCurrentUrl().contains("saucedemo.com"));
        Assert.assertFalse(driver.getCurrentUrl().contains("inventory"));

        ExtentReportManager.logPass(currentTestName, browserName, "Logout successful");
        ExtentReportManager.addScreenshot(currentTestName, browserName, driver, "Logout");
    }

    @Test(priority = 6)
    public void testEmptyCredentialsLogin() {
        ExtentReportManager.logInfo(currentTestName, browserName, "Testing login with empty credentials");

        // Test empty username
        loginPage.login("", "secret_sauce");
        waitForCondition(d -> !loginPage.getErrorMessage().isEmpty(), 10, "Error message for empty username");

        String usernameError = loginPage.getErrorMessage();
        Assert.assertTrue(usernameError.contains("Username is required"));
        ExtentReportManager.logInfo(currentTestName, browserName,
                "Empty username error: " + usernameError);

        // Refresh and test empty password
        driver.navigate().refresh();
        loginPage.login("standard_user", "");
        waitForCondition(d -> !loginPage.getErrorMessage().isEmpty(), 10, "Error message for empty password");

        String passwordError = loginPage.getErrorMessage();
        Assert.assertTrue(passwordError.contains("Password is required"));
        ExtentReportManager.logInfo(currentTestName, browserName,
                "Empty password error: " + passwordError);

        ExtentReportManager.addScreenshot(currentTestName, browserName, driver, "EmptyCredentials");
    }
}