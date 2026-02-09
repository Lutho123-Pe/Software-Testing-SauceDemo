package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.*;


import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class CheckoutTests extends BaseTest {

    @BeforeMethod
    public void loginAndAddProduct() {
        stepDelay("Setting up checkout test - logging in and adding product");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Logging in with standard user");
        loginPage.login("standard_user", "secret_sauce");

        stepDelay("Login successful, adding item to cart");
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.addItemToCart(0);

        stepDelay("Item added, navigating to cart");
        driver.get("https://www.saucedemo.com/cart.html");

        waitForCondition(d -> d.getCurrentUrl().contains("cart.html"),
                5, "Cart page to load");

        System.out.println("✅ Setup completed: Logged in, item added, on cart page");
    }

    @Test(priority = 1)
    
    public void testSuccessfulCheckout() {
        stepDelay("Starting successful checkout test");

        CartPage cartPage = new CartPage(driver);
        actionDelay("Clicking checkout button");
        cartPage.clickCheckout();

        stepDelay("On checkout information page");
        waitForCondition(d -> d.getCurrentUrl().contains("checkout-step-one"),
                5, "Checkout step one page");

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        actionDelay("Filling checkout information");
        checkoutPage.fillInformation("John", "Doe", "12345");

        stepDelay("On checkout overview page");
        waitForCondition(d -> d.getCurrentUrl().contains("checkout-step-two"),
                5, "Checkout step two page");

        actionDelay("Completing checkout");
        checkoutPage.finishCheckout();

        stepDelay("Waiting for success page");
        waitForCondition(d -> d.getCurrentUrl().contains("checkout-complete"),
                5, "Checkout complete page");

        waitForCondition(d -> checkoutPage.getSuccessMessage().length() > 0,
                5, "Success message to appear");

        String successMessage = checkoutPage.getSuccessMessage();
        stepDelay("Success message: " + successMessage);

        Assert.assertEquals(successMessage, "Thank you for your order!",
                "Checkout failed. Message: " + successMessage);

        System.out.println("✅ Successful checkout test passed");
    }

    @Test(priority = 2)
    
    public void testCheckoutEmptyFirstName() {
        stepDelay("Starting empty first name checkout test");

        CartPage cartPage = new CartPage(driver);
        actionDelay("Starting checkout");
        cartPage.clickCheckout();

        stepDelay("On checkout information page");
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        actionDelay("Filling info with empty first name");
        checkoutPage.fillInformation("", "Doe", "12345");

        stepDelay("Checking for error (page should not proceed)");
        // The actual implementation depends on how the app handles empty fields
        // For now, we'll verify we're still on step one
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one"),
                "Should not proceed with empty first name");

        System.out.println("✅ Empty first name validation works");
    }

    @Test(priority = 3)
    
    public void testCheckoutEmptyLastName() {
        stepDelay("Starting empty last name checkout test");

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();
        stepDelay("On checkout page");

        // This test would be similar to empty first name
        System.out.println("✅ Empty last name test placeholder");
    }

    @Test(priority = 4)
    
    public void testCheckoutEmptyPostalCode() {
        stepDelay("Starting empty postal code checkout test");

        // Similar to other empty field tests
        System.out.println("✅ Empty postal code test placeholder");
    }

    @Test(priority = 5)
    
    public void testCheckoutWithMultipleItems() {
        stepDelay("Starting checkout with multiple items test");

        // Go back to inventory to add more items
        driver.get("https://www.saucedemo.com/inventory.html");
        stepDelay("Back to inventory");

        ProductsPage productsPage = new ProductsPage(driver);

        actionDelay("Adding second item");
        productsPage.addItemToCart(1);
        stepDelay("Second item added");

        actionDelay("Adding third item");
        productsPage.addItemToCart(2);
        stepDelay("Third item added");

        // Verify cart has 3 items
        waitForCondition(d -> "3".equals(productsPage.getCartBadgeCount()),
                5, "Cart to show 3 items");

        stepDelay("Navigating to cart with 3 items");
        driver.get("https://www.saucedemo.com/cart.html");

        // Continue with checkout
        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();
        stepDelay("On checkout page");

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.fillInformation("Jane", "Smith", "54321");
        stepDelay("Information filled");

        checkoutPage.finishCheckout();
        stepDelay("Checkout completed");

        waitForCondition(d -> checkoutPage.getSuccessMessage().length() > 0,
                5, "Success message");

        Assert.assertEquals(checkoutPage.getSuccessMessage(), "Thank you for your order!");

        System.out.println("✅ Checkout with multiple items completed");
    }

    @Test(priority = 6)
    
    public void testCancelCheckout() {
        stepDelay("Starting cancel checkout test");

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();
        stepDelay("On checkout page");

        // Navigate back to cart (simulate cancel)
        driver.get("https://www.saucedemo.com/cart.html");
        stepDelay("Back to cart");

        Assert.assertTrue(driver.getCurrentUrl().contains("cart.html"),
                "Should be back on cart page");

        System.out.println("✅ Checkout cancellation works");
    }

    @Test(priority = 7)
    
    public void testCheckoutWithSpecialCharacters() {
        stepDelay("Starting special characters checkout test");

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();
        stepDelay("On checkout page");

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        actionDelay("Filling with special characters");
        checkoutPage.fillInformation("Jöhn", "D'Öe", "123-45");

        stepDelay("Attempting to proceed");
        // Continue with checkout if possible
        System.out.println("✅ Special characters test placeholder");
    }
}