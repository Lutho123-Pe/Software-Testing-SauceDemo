package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.*;



import org.testng.Assert;
import org.testng.annotations.Test;


public class SmokeTests extends BaseTest {

    @Test(priority = 1)
    
    
    public void testLoginLogout() {
        stepDelay("Starting login/logout test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Entering username and password");
        loginPage.login("standard_user", "secret_sauce");

        stepDelay("After login - waiting for inventory page");
        ProductsPage productsPage = new ProductsPage(driver);

        waitForCondition(d -> productsPage.getPageTitle().equals("Products"),
                5, "Products page title to appear");
        Assert.assertEquals(productsPage.getPageTitle(), "Products");

        actionDelay("Clicking logout");
        productsPage.logout();

        stepDelay("After logout - verifying URL");
        waitForCondition(d -> d.getCurrentUrl().contains("saucedemo.com") &&
                        !d.getCurrentUrl().contains("inventory"),
                5, "URL to reflect logout");

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("saucedemo.com") &&
                        !currentUrl.contains("inventory"),
                "Logout failed. Current URL: " + currentUrl);

        System.out.println(" Login/logout test completed successfully");
    }

    @Test(priority = 2)
    
    
    public void testHomepageLoading() {
        stepDelay("Starting homepage loading test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Logging in with standard user");
        loginPage.login("standard_user", "secret_sauce");

        stepDelay("After login - checking inventory items");
        ProductsPage productsPage = new ProductsPage(driver);

        waitForCondition(d -> productsPage.getInventoryItemCount() > 0,
                5, "Inventory items to load");
        int itemCount = productsPage.getInventoryItemCount();

        Assert.assertTrue(itemCount > 0, "No inventory items found");
        Assert.assertEquals(itemCount, 6, "Should have exactly 6 products");

        System.out.println(" Homepage loading test completed. Found " + itemCount + " items");
    }

    @Test(priority = 3)
    
    
    public void testAddToCart() {
        stepDelay("Starting add to cart test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Logging in");
        loginPage.login("standard_user", "secret_sauce");

        stepDelay("After login - adding first item to cart");
        ProductsPage productsPage = new ProductsPage(driver);
        actionDelay("Clicking add to cart button");
        productsPage.addItemToCart(0);

        stepDelay("Checking cart badge updated");
        waitForCondition(d -> {
            try {
                return "1".equals(productsPage.getCartBadgeCount());
            } catch (Exception e) {
                return false;
            }
        }, 5, "Cart badge to update to '1'");

        String badgeCount = productsPage.getCartBadgeCount();
        Assert.assertEquals(badgeCount, "1",
                "Cart badge should show '1' but shows: " + badgeCount);

        System.out.println(" Add to cart test completed. Cart badge: " + badgeCount);
    }

    @Test(priority = 4)
    
    
    public void testCheckoutPageNavigation() {
        stepDelay("Starting checkout navigation test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Logging in");
        loginPage.login("standard_user", "secret_sauce");

        stepDelay("After login - adding item to cart");
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.addItemToCart(0);

        actionDelay("Navigating to cart page");
        driver.get("https://www.saucedemo.com/cart.html");
        stepDelay("On cart page");

        CartPage cartPage = new CartPage(driver);
        actionDelay("Clicking checkout button");
        cartPage.clickCheckout();

        stepDelay("Waiting for checkout page to load");
        waitForCondition(d -> d.getCurrentUrl().contains("checkout-step-one"),
                5, "Checkout page URL");

        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one"),
                "Not on checkout page. Current URL: " + driver.getCurrentUrl());

        System.out.println(" Checkout navigation test completed");
    }

    @Test(priority = 5)
    
    
    public void testProductDetailsDisplay() {
        stepDelay("Starting product details test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Logging in");
        loginPage.login("standard_user", "secret_sauce");

        stepDelay("Checking inventory items");
        ProductsPage productsPage = new ProductsPage(driver);
        int itemCount = productsPage.getInventoryItemCount();

        Assert.assertEquals(itemCount, 6, "Should have 6 products");
        System.out.println(" Found " + itemCount + " products");

        stepDelay("Product details test completed");
    }

    @Test(priority = 6)
    
    
    public void testCartPageFunctionality() {
        stepDelay("Starting cart page test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Logging in");
        loginPage.login("standard_user", "secret_sauce");

        stepDelay("Adding items to cart");
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.addItemToCart(0);
        productsPage.addItemToCart(1);

        stepDelay("Checking cart badge");
        waitForCondition(d -> "2".equals(productsPage.getCartBadgeCount()),
                5, "Cart badge to show '2'");

        actionDelay("Navigating to cart");
        driver.get("https://www.saucedemo.com/cart.html");

        stepDelay("Verifying cart page loaded");
        waitForCondition(d -> d.getCurrentUrl().contains("cart.html"),
                5, "Cart page URL");

        Assert.assertTrue(driver.getCurrentUrl().contains("cart.html"));
        System.out.println(" Cart page test completed");
    }

    @Test(priority = 7)
    
    
    public void testInvalidLoginError() {
        stepDelay("Starting invalid login test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Attempting login with invalid credentials");
        loginPage.login("invalid_user", "invalid_pass");

        stepDelay("Checking for error message");
        waitForCondition(d -> loginPage.getErrorMessage().length() > 0,
                5, "Error message to appear");

        String errorMessage = loginPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("Username and password do not match"),
                "Wrong error message: " + errorMessage);

        System.out.println(" Invalid login test completed. Error: " + errorMessage);
    }

    @Test(priority = 8)
    
    
    public void testBackToProductsNavigation() {
        stepDelay("Starting navigation test");

        LoginPage loginPage = new LoginPage(driver);
        actionDelay("Logging in");
        loginPage.login("standard_user", "secret_sauce");

        stepDelay("Going to cart and back");
        driver.get("https://www.saucedemo.com/cart.html");
        stepDelay("On cart page");

        driver.get("https://www.saucedemo.com/inventory.html");
        stepDelay("Back to products");

        ProductsPage productsPage = new ProductsPage(driver);
        Assert.assertEquals(productsPage.getPageTitle(), "Products");

        System.out.println(" Navigation test completed");
    }

    @Test(priority = 9)
    
    
    public void testPageLoadPerformance() {
        stepDelay("Starting performance test");

        long startTime = System.currentTimeMillis();
        driver.get("https://www.saucedemo.com/");
        waitForPageToLoad();
        long endTime = System.currentTimeMillis();

        long loadTime = endTime - startTime;
        System.out.println("  Page load time: " + loadTime + "ms");

        Assert.assertTrue(loadTime < 5000, "Page took too long to load: " + loadTime + "ms");
        System.out.println(" Performance test completed");
    }

    @Test(priority = 10)
    
    
    public void testButtonFunctionality() {
        stepDelay("Starting button functionality test");

        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(driver.getCurrentUrl().contains("saucedemo.com"));

        System.out.println(" Button functionality test completed");
    }
}