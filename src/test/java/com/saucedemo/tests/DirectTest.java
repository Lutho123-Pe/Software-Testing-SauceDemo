package com.saucedemo.tests;

import org.openqa.selenium.WebDriver;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

    public class DirectTest {

        @Test
        public void testDirectChrome() {
            System.out.println("🚀 DIRECT CHROME TEST (No frameworks)");
            System.out.println("======================================");

            WebDriver driver = null;
            try {
                // 1. Setup WebDriverManager
                System.out.println("1. Setting up WebDriverManager...");
                WebDriverManager.chromedriver().setup();
                System.out.println("   ✅ WebDriverManager setup complete");

                // 2. Create ChromeOptions
                System.out.println("2. Creating ChromeOptions...");
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("--start-maximized");
                options.addArguments("--disable-notifications");

                // Try without headless first
                // options.addArguments("--headless"); // DON'T use headless for debugging

                System.out.println("   ✅ ChromeOptions created");

                // 3. Create driver instance
                System.out.println("3. Creating ChromeDriver instance...");
                driver = new ChromeDriver(options);
                System.out.println("   ✅ ChromeDriver created");

                // 4. Navigate to site
                System.out.println("4. Navigating to https://www.saucedemo.com/");
                driver.get("https://www.saucedemo.com/");

                // 5. Get page info
                System.out.println("5. Getting page information...");
                System.out.println("   ✅ Page Title: " + driver.getTitle());
                System.out.println("   ✅ Current URL: " + driver.getCurrentUrl());
                System.out.println("   ✅ Page loaded successfully!");

                // 6. Take a screenshot to prove it worked
                System.out.println("6. Waiting 3 seconds to see the page...");
                Thread.sleep(3000);

                // 7. Try to find an element
                System.out.println("7. Looking for login elements...");
                boolean hasUsernameField = driver.findElements(org.openqa.selenium.By.id("user-name")).size() > 0;
                boolean hasPasswordField = driver.findElements(org.openqa.selenium.By.id("password")).size() > 0;
                boolean hasLoginButton = driver.findElements(org.openqa.selenium.By.id("login-button")).size() > 0;

                System.out.println("   ✅ Username field found: " + hasUsernameField);
                System.out.println("   ✅ Password field found: " + hasPasswordField);
                System.out.println("   ✅ Login button found: " + hasLoginButton);

                if (hasUsernameField && hasPasswordField && hasLoginButton) {
                    System.out.println("\n🎉 SUCCESS! Website is accessible and elements are present!");
                } else {
                    System.out.println("\n⚠️ WARNING: Some elements not found!");
                }

            } catch (Exception e) {
                System.out.println("\n❌ ERROR: " + e.getMessage());
                e.printStackTrace();

                // Print system info for debugging
                System.out.println("\n📋 SYSTEM INFORMATION:");
                System.out.println("   Java Version: " + System.getProperty("java.version"));
                System.out.println("   OS Name: " + System.getProperty("os.name"));
                System.out.println("   OS Version: " + System.getProperty("os.version"));
                System.out.println("   User Home: " + System.getProperty("user.home"));

            } finally {
                // 8. Cleanup
                if (driver != null) {
                    System.out.println("\n8. Closing browser...");
                    driver.quit();
                    System.out.println("   ✅ Browser closed");
                }
                System.out.println("\n======================================");
                System.out.println("TEST COMPLETED");
                System.out.println("======================================");
            }
        }

        @Test
        public void testManualDriverPath() {
            System.out.println("\n🚀 TEST WITH MANUAL DRIVER PATH");
            System.out.println("======================================");

            // Try with manual driver path
            String driverPath = "C:\\Users\\alulutho.tokwe\\Documents\\chromedriver-win64 1\\chromedriver-win64\\chromedriver.exe";

            try {
                System.out.println("1. Setting manual driver path: " + driverPath);
                System.setProperty("webdriver.chrome.driver", driverPath);

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("--start-maximized");

                System.out.println("2. Creating ChromeDriver...");
                WebDriver driver = new ChromeDriver(options);

                System.out.println("3. Navigating to Google (simpler test)...");
                driver.get("https://www.google.com");

                System.out.println("   ✅ Google Title: " + driver.getTitle());
                System.out.println("   ✅ Google URL: " + driver.getCurrentUrl());

                Thread.sleep(2000);
                driver.quit();
                System.out.println("🎉 Manual driver path works!");

            } catch (Exception e) {
                System.out.println("❌ Manual driver failed: " + e.getMessage());
            }
        }
    }

