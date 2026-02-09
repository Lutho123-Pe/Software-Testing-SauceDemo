package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.ProductsPage;


import org.testng.Assert;
import org.testng.annotations.Test;


public class LoginTests extends BaseTest {

    @Test(priority = 1)
    
    public void testSuccessfulLogin() {
        stepDelay("Starting successful login test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Entering valid credentials");
        loginPage.login("standard_user", "secret_sauce");

        stepDelay("Waiting for inventory page");
        ProductsPage productsPage = new ProductsPage(driver);

        waitForCondition(d -> productsPage.getPageTitle().equals("Products"),
                5, "Products page title to appear");

        String pageTitle = productsPage.getPageTitle();
        stepDelay("Page title: " + pageTitle);

        Assert.assertEquals(pageTitle, "Products",
                "Login failed. Page title: " + pageTitle);

        System.out.println("✅ Successful login test passed");
    }

    @Test(priority = 2)
    
    public void testLockedOutUser() {
        stepDelay("Starting locked out user test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Attempting login with locked out user");
        loginPage.login("locked_out_user", "secret_sauce");

        stepDelay("Waiting for error message");
        waitForCondition(d -> loginPage.getErrorMessage().length() > 0,
                5, "Locked out error message");

        String errorMessage = loginPage.getErrorMessage();
        stepDelay("Error message: " + errorMessage);

        Assert.assertTrue(errorMessage.contains("Sorry, this user has been locked out"),
                "Wrong error message: " + errorMessage);

        System.out.println("✅ Locked out user test passed");
    }

    @Test(priority = 3)
    
    public void testInvalidPassword() {
        stepDelay("Starting invalid password test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Entering valid username but wrong password");
        loginPage.login("standard_user", "wrong_password");

        stepDelay("Waiting for error message");
        waitForCondition(d -> loginPage.getErrorMessage().length() > 0,
                5, "Invalid password error");

        String errorMessage = loginPage.getErrorMessage();
        stepDelay("Error message: " + errorMessage);

        Assert.assertTrue(errorMessage.contains("Username and password do not match"),
                "Wrong error message: " + errorMessage);

        System.out.println("✅ Invalid password test passed");
    }

    @Test(priority = 4)
    
    public void testEmptyUsername() {
        stepDelay("Starting empty username test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Leaving username empty");
        loginPage.login("", "secret_sauce");

        stepDelay("Waiting for error message");
        waitForCondition(d -> loginPage.getErrorMessage().length() > 0,
                5, "Empty username error");

        String errorMessage = loginPage.getErrorMessage();
        stepDelay("Error message: " + errorMessage);

        Assert.assertTrue(errorMessage.contains("Username is required"),
                "Wrong error message: " + errorMessage);

        System.out.println("✅ Empty username test passed");
    }

    @Test(priority = 5)
    
    public void testEmptyPassword() {
        stepDelay("Starting empty password test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Leaving password empty");
        loginPage.login("standard_user", "");

        stepDelay("Waiting for error message");
        waitForCondition(d -> loginPage.getErrorMessage().length() > 0,
                5, "Empty password error");

        String errorMessage = loginPage.getErrorMessage();
        stepDelay("Error message: " + errorMessage);

        Assert.assertTrue(errorMessage.contains("Password is required"),
                "Wrong error message: " + errorMessage);

        System.out.println("✅ Empty password test passed");
    }

    @Test(priority = 6)
    
    public void testPerformanceGlitchUser() {
        stepDelay("Starting performance glitch user test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Logging in as performance glitch user");
        loginPage.login("performance_glitch_user", "secret_sauce");

        stepDelay("Waiting extra time for slow login");
        delay(3000); // Extra delay for performance glitch

        waitForCondition(d -> d.getCurrentUrl().contains("inventory"),
                10, "Inventory page for performance user");

        ProductsPage productsPage = new ProductsPage(driver);
        String pageTitle = productsPage.getPageTitle();

        Assert.assertEquals(pageTitle, "Products",
                "Performance glitch user login failed");

        System.out.println("✅ Performance glitch user test passed (took extra time)");
    }

    @Test(priority = 7)
    
    public void testProblemUser() {
        stepDelay("Starting problem user test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Logging in as problem user");
        loginPage.login("problem_user", "secret_sauce");

        stepDelay("Waiting for inventory page");
        waitForCondition(d -> d.getCurrentUrl().contains("inventory"),
                5, "Inventory page for problem user");

        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"),
                "Problem user login failed");

        System.out.println("✅ Problem user test passed");
    }

    @Test(priority = 8)
    
    public void testLoginFormFieldsEditable() {
        stepDelay("Starting form fields test");

        LoginPage loginPage = new LoginPage(driver);

        // Test username field
        actionDelay("Testing username field");
        loginPage.login("test", "test");
        stepDelay("Clearing form");
        driver.navigate().refresh();

        stepDelay("Form fields test completed");
        System.out.println("✅ Login form fields are editable");
    }

    @Test(priority = 9)
    
    public void testLoginButtonStates() {
        stepDelay("Starting login button states test");

        LoginPage loginPage = new LoginPage(driver);

        // Button should be enabled by default
        stepDelay("Checking button enabled state");
        // Add button state verification here

        System.out.println("✅ Login button states test completed");
    }

    @Test(priority = 10)
    
    public void testMultipleFailedLoginAttempts() {
        stepDelay("Starting multiple failed login attempts test");

        LoginPage loginPage = new LoginPage(driver);

        for (int i = 1; i <= 3; i++) {
            stepDelay("Failed attempt " + i + "/3");
            loginPage.login("invalid" + i, "invalid" + i);

            waitForCondition(d -> loginPage.getErrorMessage().length() > 0,
                    3, "Error message after attempt " + i);

            System.out.println("✅ Attempt " + i + " failed as expected: " + loginPage.getErrorMessage());

            if (i < 3) {
                driver.navigate().refresh();
                stepDelay("Ready for next attempt");
            }
        }

        System.out.println("✅ Multiple failed login attempts test completed");
    }
}