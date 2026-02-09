package com.saucedemo.tests;


import com.saucedemo.base.BaseTest;
import org.testng.annotations.Test;

    public class FrameworkTest extends BaseTest {

        @Test
        public void testBaseFramework() {

        }

        private void navigateTo(String url) {
        }

        @Test
        public void testAddToCart() {
            System.out.println("🧪 Testing Add to Cart...");

            // Login first
            navigateTo("https://www.saucedemo.com/");

            driver.findElement(org.openqa.selenium.By.id("user-name")).sendKeys("standard_user");
            driver.findElement(org.openqa.selenium.By.id("password")).sendKeys("secret_sauce");
            driver.findElement(org.openqa.selenium.By.id("login-button")).click();

            delay(2000);

            // Add item to cart
            System.out.println("🛒 Adding item to cart...");
            driver.findElement(org.openqa.selenium.By.className("btn_inventory")).click();

            delay(1000);

            // Check cart badge
            String cartBadge = driver.findElement(org.openqa.selenium.By.className("shopping_cart_badge")).getText();
            System.out.println("📦 Cart badge: " + cartBadge);

            if ("1".equals(cartBadge)) {
                System.out.println("✅ Item added to cart successfully!");
            } else {
                System.out.println("❌ Failed to add item to cart");
            }
        }}

