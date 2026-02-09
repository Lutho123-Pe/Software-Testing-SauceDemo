package com.saucedemo.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class DriverManager {

    private static ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    // Get driver for the current thread (with browser parameter)
    public static WebDriver getDriver(String browser) {
        WebDriver driver = driverThreadLocal.get();

        if (driver == null) {
            driver = createDriver(browser);
            driverThreadLocal.set(driver);
        }
        return driver;
    }

    // Get existing driver
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    // Create new driver based on browser type
    private static WebDriver createDriver(String browser) {
        WebDriver driver;

        switch (browser.toLowerCase()) {
            case "chrome":
                driver = getChromeDriver();
                break;
            case "firefox":
                driver = getFirefoxDriver();
                break;
            case "edge":
                driver = getEdgeDriver();
                break;
            case "safari":
                driver = getSafariDriver();
                break;
            default:
                System.out.println("⚠️ Unknown browser: " + browser + ". Defaulting to Chrome.");
                driver = getChromeDriver();
        }

        configureDriver(driver);
        return driver;
    }

    // ========== INDIVIDUAL BROWSER METHODS ==========

    // Initialize Chrome driver WITH OPTIONS
    public static WebDriver getChromeDriver() {
        try {
            System.out.println("🔄 Setting up ChromeDriver...");
            WebDriverManager.chromedriver().setup();

            // IMPORTANT: Add ChromeOptions for newer Chrome versions
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*"); // Critical for Chrome v111+
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-gpu");

            ChromeDriver driver = new ChromeDriver(options);
            setDriver(driver);
            configureDriver(driver);
            System.out.println("✅ ChromeDriver initialized successfully!");
            return driver;

        } catch (Exception e) {
            System.out.println("❌ Failed to initialize ChromeDriver: " + e.getMessage());
            throw new RuntimeException("ChromeDriver initialization failed", e);
        }
    }

    // Initialize Firefox driver
    public static WebDriver getFirefoxDriver() {
        try {
            System.out.println("🔄 Setting up FirefoxDriver...");
            WebDriverManager.firefoxdriver().setup();

            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--start-maximized");

            FirefoxDriver driver = new FirefoxDriver(options);
            setDriver(driver);
            configureDriver(driver);
            System.out.println("✅ FirefoxDriver initialized successfully!");
            return driver;

        } catch (Exception e) {
            System.out.println("❌ Failed to initialize FirefoxDriver: " + e.getMessage());
            throw new RuntimeException("FirefoxDriver initialization failed", e);
        }
    }

    // Initialize Edge driver
    public static WebDriver getEdgeDriver() {
        try {
            System.out.println("🔄 Setting up EdgeDriver...");

            // Set the Edge driver path
            String edgeDriverPath = "C:\\Users\\alulutho.tokwe\\Documents\\edgedriver_win64\\msedgedriver.exe";
            System.setProperty("webdriver.edge.driver", edgeDriverPath);

            // Verify the driver exists
            File driverFile = new File(edgeDriverPath);
            if (!driverFile.exists()) {
                throw new FileNotFoundException("EdgeDriver not found at: " + edgeDriverPath);
            }

            EdgeOptions options = new EdgeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--remote-allow-origins=*"); // Critical for Edge
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-extensions");

            // Edge specific capabilities
            options.setCapability("acceptInsecureCerts", true);
            options.setCapability("ms:edgeOptions", Map.of(
                    "args", Arrays.asList("--start-maximized", "--remote-allow-origins=*")
            ));

            EdgeDriver driver = new EdgeDriver(options);
            setDriver(driver);  // FIXED: Pass the driver instance, not the path
            configureDriver(driver);
            System.out.println("✅ EdgeDriver initialized successfully!");
            return driver;

        } catch (Exception e) {
            System.out.println("❌ Failed to initialize EdgeDriver: " + e.getMessage());
            System.out.println("💡 Please check if EdgeDriver exists at: C:\\Users\\alulutho.tokwe\\Documents\\edgedriver_win64\\msedgedriver.exe");
            throw new RuntimeException("EdgeDriver initialization failed", e);
        }
    }

    // Initialize Safari driver (macOS only)
    public static WebDriver getSafariDriver() {
        SafariDriver driver = new SafariDriver();
        setDriver(driver);
        configureDriver(driver);
        return driver;
    }

    // Set driver for the current thread
    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    // Common driver configuration - UPDATED FOR Selenium 4
    private static void configureDriver(WebDriver driver) {
        driver.manage().window().maximize();

        // Selenium 4 uses Duration instead of TimeUnit
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(10));
    }

    // Quit driver
    public static void quitDriver() {
        WebDriver driver = getDriver();
        if (driver != null) {
            System.out.println("🔄 Closing browser...");
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("⚠️ Error closing browser: " + e.getMessage());
            }
            driverThreadLocal.remove();
            System.out.println("✅ Browser closed successfully!");
        }
    }

    // Capture screenshot
    public static String captureScreenshot(WebDriver driver, String browserName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "screenshot_" + browserName + "_" + timeStamp + ".png";

            // Create screenshots directory if it doesn't exist
            File directory = new File("screenshots");
            if (!directory.exists()) {
                boolean dirCreated = directory.mkdirs();
                if (!dirCreated) {
                    System.out.println("Failed to create screenshots directory");
                    return "";
                }
            }

            String destinationPath = "screenshots/" + fileName;
            File destination = new File(destinationPath);

            // Copy file with proper exception handling
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("📸 Screenshot saved: " + destinationPath);
            return destination.getAbsolutePath();

        } catch (IOException e) {
            System.out.println("❌ Failed to capture screenshot: " + e.getMessage());
            return "";
        } catch (Exception e) {
            System.out.println("❌ Unexpected error capturing screenshot: " + e.getMessage());
            return "";
        }
    }

    // Get current browser name
    public static String getCurrentBrowser() {
        WebDriver driver = getDriver();
        if (driver instanceof ChromeDriver) {
            return "chrome";
        } else if (driver instanceof FirefoxDriver) {
            return "firefox";
        } else if (driver instanceof EdgeDriver) {
            return "edge";
        } else if (driver instanceof SafariDriver) {
            return "safari";
        } else {
            return "unknown";
        }
    }
}