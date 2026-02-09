package com.saucedemo.tests;

import com.saucedemo.pages.*;
import com.saucedemo.utils.DriverManager;
import com.saucedemo.utils.ExtentReportManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import java.time.Duration;
import java.lang.reflect.Method;

public class CrossBrowserTests {
    private WebDriver driver;
    private LoginPage loginPage;
    private ProductsPage productsPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private String browserName;
    private static long testStartTime;
    private static long testEndTime;

    // Add delays for visual observation (in milliseconds)
    private static final int ACTION_DELAY = 1000; // 1 second delay between actions
    private static final int STEP_DELAY = 1500;   // 1.5 second delay for important steps

    @BeforeSuite
    public void beforeSuite() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 STARTING CROSS-BROWSER TEST SUITE");
        System.out.println("📅 Test Execution Started: " + new java.util.Date());
        System.out.println("=".repeat(60) + "\n");

        // Initialize ExtentReports
        ExtentReportManager.initializeReport();
    }

    @AfterSuite
    public void afterSuite() {
        // Flush and generate the final report
        ExtentReportManager.flushReport();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ CROSS-BROWSER TEST SUITE COMPLETED");
        System.out.println("📅 Test Execution Ended: " + new java.util.Date());
        System.out.println("📊 Report Generated Successfully");
        System.out.println("=".repeat(60));

        // Open the report automatically
        try {
            Thread.sleep(2000); // Wait a moment before opening
            ExtentReportManager.openReport();
        } catch (Exception e) {
            System.out.println("⚠️ Could not open report automatically: " + e.getMessage());
        }
    }

    @BeforeMethod
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser, Method method) {
        testStartTime = System.currentTimeMillis();
        browserName = browser;

        // Visual separator
        System.out.println("\n" + "═".repeat(50));
        System.out.println("🌐 STARTING TEST ON: " + browser.toUpperCase() + " BROWSER");
        System.out.println("🧪 Test Method: " + method.getName());
        System.out.println("═".repeat(50));

        // Use the centralized driver manager
        driver = DriverManager.getDriver(browser);

        // Navigate to SauceDemo
        navigateToSauceDemo();

        // Initialize page objects
        loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
        cartPage = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);

        // Create test in Extent Report with browser info
        String testName = method.getName() + " [" + browser.toUpperCase() + "]";
        ExtentReportManager.createTest(testName, browser);
        ExtentReportManager.logInfo(testName, browser,
                "Starting test execution on " + browser.toUpperCase() + " browser");

        // Add browser-specific configuration info
        ExtentReportManager.logInfo(testName, browser,
                "Browser: " + browser.toUpperCase() +
                        " | Test: " + method.getName() +
                        " | Timestamp: " + new java.util.Date());

        delay(ACTION_DELAY);
    }

    // Helper method to navigate to SauceDemo
    private void navigateToSauceDemo() {
        System.out.println("🌐 Navigating to SauceDemo...");
        driver.get("https://www.saucedemo.com/");

        // Wait for page to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> driver.getTitle().contains("Swag Labs") ||
                driver.getCurrentUrl().contains("saucedemo"));

        System.out.println("✅ Loaded: " + driver.getTitle() + " | URL: " + driver.getCurrentUrl());
        delay(ACTION_DELAY);
    }

    // Helper method for visual delay
    private void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Helper method for visual step logging
    private void logStep(String stepDescription) {
        System.out.println("🟡 " + stepDescription);
        ExtentReportManager.logInfo(getCurrentTestName(), browserName, stepDescription);
        delay(ACTION_DELAY);
    }

    private void logImportantStep(String stepDescription) {
        System.out.println("🔵 " + stepDescription);
        ExtentReportManager.logInfo(getCurrentTestName(), browserName, "IMPORTANT: " + stepDescription);
        delay(STEP_DELAY);
    }

    private String getCurrentTestName() {
        // Extract current test name from stack trace
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (element.getMethodName().startsWith("test")) {
                return element.getMethodName() + " [" + browserName.toUpperCase() + "]";
            }
        }
        return "Unknown Test [" + browserName.toUpperCase() + "]";
    }

    // Test 1: Invalid Login Test
    @Test(priority = 1)
    public void testInvalidLogin() {
        logImportantStep("Starting invalid login test");

        driver.get("https://www.saucedemo.com/");
        logStep("Entering invalid username: invalid_user");
        loginPage.enterUsername("invalid_user");

        logStep("Entering invalid password: invalid_password");
        loginPage.enterPassword("invalid_password");

        logImportantStep("Clicking login button");
        loginPage.clickLogin();

        // Wait for error message
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> {
            String errorMsg = loginPage.getErrorMessage();
            return errorMsg != null && !errorMsg.isEmpty();
        });

        // Verify error message
        String errorMessage = loginPage.getErrorMessage();
        if (errorMessage != null && !errorMessage.isEmpty()) {
            ExtentReportManager.logPass(getCurrentTestName(), browserName,
                    "Error message displayed: " + errorMessage);
            ExtentReportManager.addScreenshot(getCurrentTestName(), browserName, driver,
                    "Invalid Login Error");
        } else {
            ExtentReportManager.logFail(getCurrentTestName(), browserName,
                    "Error message not displayed");
        }
    }

    // Test 2: Valid Login Test
    @Test(priority = 2)
    public void testValidLogin() {
        logImportantStep("Starting valid login test");

        driver.get("https://www.saucedemo.com/");
        logStep("Entering valid username: standard_user");
        loginPage.enterUsername("standard_user");

        logStep("Entering valid password: secret_sauce");
        loginPage.enterPassword("secret_sauce");

        logImportantStep("Clicking login button");
        loginPage.clickLogin();

        // Wait for either error message or successful login
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> {
            String errorMsg = loginPage.getErrorMessage();
            boolean hasError = errorMsg != null && !errorMsg.isEmpty();
            boolean isProductsPage = productsPage.isProductsPageDisplayed();
            return hasError || isProductsPage;
        });

        // Verify login result
        String errorMessage = loginPage.getErrorMessage();
        if (errorMessage != null && !errorMessage.isEmpty()) {
            ExtentReportManager.logFail(getCurrentTestName(), browserName,
                    "Login failed with error: " + errorMessage);
        } else if (productsPage.isProductsPageDisplayed()) {
            ExtentReportManager.logPass(getCurrentTestName(), browserName,
                    "Login successful - redirected to products page");
            ExtentReportManager.addScreenshot(getCurrentTestName(), browserName, driver,
                    "Successful Login");
        } else {
            ExtentReportManager.logFail(getCurrentTestName(), browserName,
                    "Neither error message nor products page appeared");
        }
    }

    // Test 3: Complete Purchase Flow
    @Test(priority = 3)
    public void testCompletePurchaseFlow() {
        logImportantStep("Starting complete purchase flow test");

        ExtentReportManager.logInfo(getCurrentTestName(), browserName,
                "Testing complete purchase flow on " + browserName);

        // Step 1: Login
        logImportantStep("Step 1: Performing login");
        driver.get("https://www.saucedemo.com/");
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLogin();

        // Verify login success
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> productsPage.isProductsPageDisplayed());

        if (!productsPage.isProductsPageDisplayed()) {
            ExtentReportManager.logFail(getCurrentTestName(), browserName,
                    "Login failed - cannot proceed with purchase flow");
            return;
        }

        ExtentReportManager.logPass(getCurrentTestName(), browserName, "Step 1: Login successful");
        delay(STEP_DELAY);

        // Step 2: Add items to cart
        logImportantStep("Step 2: Adding items to cart");
        productsPage.addItemToCart(0); // First item
        logStep("Added first item to cart");
        productsPage.addItemToCart(1); // Second item
        logStep("Added second item to cart");

        String cartCount = productsPage.getCartItemCount();
        if (cartCount != null && !cartCount.isEmpty() && Integer.parseInt(cartCount) >= 2) {
            ExtentReportManager.logPass(getCurrentTestName(), browserName,
                    "Step 2: Added 2 items to cart. Cart count: " + cartCount);
        } else {
            ExtentReportManager.logFail(getCurrentTestName(), browserName,
                    "Step 2: Failed to add items to cart");
        }
        delay(STEP_DELAY);

        // Step 3: Go to cart
        logImportantStep("Step 3: Navigating to cart");
        productsPage.clickCartIcon();
        wait.until(d -> cartPage.isCartPageDisplayed());

        if (cartPage.isCartPageDisplayed()) {
            ExtentReportManager.logPass(getCurrentTestName(), browserName,
                    "Step 3: Navigated to cart page");
            ExtentReportManager.addScreenshot(getCurrentTestName(), browserName, driver,
                    "Cart Page");
        } else {
            ExtentReportManager.logFail(getCurrentTestName(), browserName,
                    "Step 3: Failed to navigate to cart page");
        }
        delay(STEP_DELAY);

        // Step 4: Proceed to checkout
        logImportantStep("Step 4: Proceeding to checkout");
        cartPage.clickCheckoutButton();
        wait.until(d -> checkoutPage.isCheckoutStepOneDisplayed());

        if (checkoutPage.isCheckoutStepOneDisplayed()) {
            ExtentReportManager.logPass(getCurrentTestName(), browserName,
                    "Step 4: Navigated to checkout information page");
        } else {
            ExtentReportManager.logFail(getCurrentTestName(), browserName,
                    "Step 4: Failed to navigate to checkout information page");
        }
        delay(STEP_DELAY);

        // Step 5: Fill checkout information
        logImportantStep("Step 5: Filling checkout information");
        checkoutPage.enterFirstName("John");
        checkoutPage.enterLastName("Doe");
        checkoutPage.enterPostalCode("12345");
        logStep("Filled: First Name=John, Last Name=Doe, Postal Code=12345");

        checkoutPage.clickContinue();
        wait.until(d -> checkoutPage.isCheckoutStepTwoDisplayed());

        if (checkoutPage.isCheckoutStepTwoDisplayed()) {
            ExtentReportManager.logPass(getCurrentTestName(), browserName,
                    "Step 5: Filled checkout information successfully");
            ExtentReportManager.addScreenshot(getCurrentTestName(), browserName, driver,
                    "Checkout Overview");
        } else {
            ExtentReportManager.logFail(getCurrentTestName(), browserName,
                    "Step 5: Failed to fill checkout information");
        }
        delay(STEP_DELAY);

        // Step 6: Complete purchase
        logImportantStep("Step 6: Completing purchase");
        checkoutPage.finishCheckout();

        wait.until(d -> {
            String successMsg = checkoutPage.getSuccessMessage();
            return successMsg != null && !successMsg.isEmpty();
        });

        String successMessage = checkoutPage.getSuccessMessage();
        if (successMessage != null && !successMessage.isEmpty()) {
            ExtentReportManager.logPass(getCurrentTestName(), browserName,
                    "Step 6: Purchase completed successfully: " + successMessage);
            ExtentReportManager.addScreenshot(getCurrentTestName(), browserName, driver,
                    "Order Confirmation");
        } else {
            ExtentReportManager.logFail(getCurrentTestName(), browserName,
                    "Step 6: Purchase failed or success message not displayed");
        }
    }

    // Test 4: Logout Test
    @Test(priority = 4)
    public void testLogout() {
        logImportantStep("Starting logout test");

        // Login first
        driver.get("https://www.saucedemo.com/");
        logStep("Logging in with standard_user credentials");
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> productsPage.isProductsPageDisplayed());

        // Logout
        logImportantStep("Clicking logout button");
        productsPage.logout();

        // Verify logout success
        wait.until(d -> loginPage.isLoginPageDisplayed());

        if (loginPage.isLoginPageDisplayed()) {
            ExtentReportManager.logPass(getCurrentTestName(), browserName,
                    "Logout successful - returned to login page");
            ExtentReportManager.addScreenshot(getCurrentTestName(), browserName, driver,
                    "Logout Successful");
        } else {
            ExtentReportManager.logFail(getCurrentTestName(), browserName,
                    "Logout failed");
        }
    }

    // Test 5: Add and Remove Items from Cart
    @Test(priority = 5)
    public void testAddRemoveItemsFromCart() {
        logImportantStep("Starting add/remove items from cart test");

        // Login
        driver.get("https://www.saucedemo.com/");
        logStep("Logging in with standard_user");
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> productsPage.isProductsPageDisplayed());

        // Add items
        logImportantStep("Adding items to cart");
        productsPage.addItemToCart(0);
        logStep("Added first item to cart");
        productsPage.addItemToCart(1);
        logStep("Added second item to cart");

        String initialCartCount = productsPage.getCartItemCount();
        ExtentReportManager.logInfo(getCurrentTestName(), browserName,
                "Initial cart count: " + initialCartCount);

        // Go to cart
        logImportantStep("Navigating to cart page");
        productsPage.clickCartIcon();
        wait.until(d -> cartPage.isCartPageDisplayed());

        // Remove first item
        logImportantStep("Removing first item from cart");
        cartPage.removeItem(0);
        delay(STEP_DELAY); // Wait for cart to update

        // Go back to products
        logStep("Returning to products page");
        cartPage.clickContinueShopping();
        wait.until(d -> productsPage.isProductsPageDisplayed());

        String finalCartCount = productsPage.getCartItemCount();

        if (finalCartCount != null && !finalCartCount.isEmpty() && Integer.parseInt(finalCartCount) == 1) {
            ExtentReportManager.logPass(getCurrentTestName(), browserName,
                    "Successfully added and removed items (Initial: " + initialCartCount +
                            ", Final: " + finalCartCount + ")");
            ExtentReportManager.addScreenshot(getCurrentTestName(), browserName, driver,
                    "Add Remove Items Result");
        } else {
            ExtentReportManager.logFail(getCurrentTestName(), browserName,
                    "Add/remove items test failed (Initial: " + initialCartCount +
                            ", Final: " + finalCartCount + ")");
        }
    }

    @AfterMethod
    public void tearDown() {
        testEndTime = System.currentTimeMillis();
        long executionTime = testEndTime - testStartTime;

        // Log execution time
        String timeMessage = String.format("Test execution time: %d ms (%.2f seconds)",
                executionTime, executionTime / 1000.0);
        ExtentReportManager.logInfo(getCurrentTestName(), browserName, timeMessage);
        System.out.println("⏱️  " + timeMessage);

        // Always take a screenshot (not just on failure)
        ExtentReportManager.addScreenshot(getCurrentTestName(), browserName, driver,
                "Test Completion - " + browserName.toUpperCase());

        // Close driver using DriverManager
        DriverManager.quitDriver();

        System.out.println("✅ Test completed on " + browserName.toUpperCase() + " browser");
        System.out.println("─".repeat(50));

        // Add a delay between browsers for better visual separation
        delay(2000);
    }
}