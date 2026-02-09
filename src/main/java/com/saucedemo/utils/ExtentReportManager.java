package com.saucedemo.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static Map<String, ExtentTest> extentTestMap = new HashMap<>();
    private static Map<String, ExtentTest> testMap = new HashMap<>();
    private static String reportPath;
    private static final String SCREENSHOT_DIR = "test-output/ExtentReports/screenshots/";

    private ExtentReportManager() {}

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            initializeReport();
        }
        return extent;
    }

    public static synchronized void initializeReport() {
        if (extent == null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            reportPath = "test-output/ExtentReports/TestReport_" + timestamp + ".html";

            // Create directories if they don't exist
            new File("test-output/ExtentReports").mkdirs();
            new File(SCREENSHOT_DIR).mkdirs();

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);

            // Enhanced configuration
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle("SauceDemo Cross-Browser Automation Report");
            spark.config().setReportName("Test Execution Report - All Browsers");
            spark.config().setEncoding("utf-8");
            spark.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
            spark.config().setCSS(".nav-wrapper { background-color: #4CAF50 !important; }");
            spark.config().setCSS(".test-name { color: #2196F3; }");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            // Enhanced System Information
            extent.setSystemInfo("Organization", "SauceDemo");
            extent.setSystemInfo("Project", "Automation Testing Suite");
            extent.setSystemInfo("Automation Framework", "Selenium WebDriver 4");
            extent.setSystemInfo("Testing Framework", "TestNG");
            extent.setSystemInfo("Build Tool", "Maven");
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("OS Version", System.getProperty("os.version"));
            extent.setSystemInfo("OS Architecture", System.getProperty("os.arch"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
            extent.setSystemInfo("Browsers Tested", "Chrome, Firefox, Edge");
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("Test Mode", "Cross-Browser");

            System.out.println("📊 Extent Report initialized: " + reportPath);
        }
    }

    public static synchronized void createTest(String testName, String browser) {
        ExtentReports extent = getInstance();
        ExtentTest test = extent.createTest(testName);
        test.assignCategory(browser.toUpperCase());
        test.assignDevice(browser.toUpperCase());

        // Add browser icon emoji
        String browserIcon = getBrowserIcon(browser);
        test.info("Browser: " + browserIcon + " " + browser.toUpperCase());

        testMap.put(testName, test);
        extentTestMap.put(testName, test);

        System.out.println("📝 Created test entry: " + testName + " [" + browser.toUpperCase() + "]");
    }

    public static synchronized void createTest(String testName) {
        createTest(testName, "Chrome");
    }

    private static String getBrowserIcon(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome": return "🔵";
            case "firefox": return "🦊";
            case "edge": return "🌐";
            case "safari": return "🦁";
            default: return "💻";
        }
    }

    public static synchronized ExtentTest getTest(String testName) {
        return testMap.get(testName);
    }

    public static synchronized ExtentTest getTest() {
        return testMap.values().stream().findFirst().orElse(null);
    }

    public static synchronized void logInfo(String testName, String browser, String message) {
        ExtentTest test = getTest(testName);
        if (test != null) {
            test.info(message);
            System.out.println("ℹ️  [" + browser.toUpperCase() + "] " + testName + " - INFO: " + message);
        }
    }

    public static synchronized void logPass(String testName, String browser, String message) {
        ExtentTest test = getTest(testName);
        if (test != null) {
            test.pass("✅ " + message);
            System.out.println("✅ [" + browser.toUpperCase() + "] " + testName + " - PASS: " + message);
        }
    }

    public static synchronized void logFail(String testName, String browser, String message) {
        ExtentTest test = getTest(testName);
        if (test != null) {
            test.fail("❌ " + message);
            System.out.println("❌ [" + browser.toUpperCase() + "] " + testName + " - FAIL: " + message);
        }
    }

    public static synchronized void logWarning(String testName, String browser, String message) {
        ExtentTest test = getTest(testName);
        if (test != null) {
            test.warning("⚠️ " + message);
            System.out.println("⚠️  [" + browser.toUpperCase() + "] " + testName + " - WARNING: " + message);
        }
    }

    public static synchronized void addScreenshot(String testName, String browser, WebDriver driver, String description) {
        try {
            if (driver == null) {
                logWarning(testName, browser, "Driver is null, cannot capture screenshot: " + description);
                return;
            }

            // Capture screenshot
            String screenshotPath = captureScreenshot(driver, testName + "_" + browser + "_" +
                    new SimpleDateFormat("HHmmss").format(new Date()));

            if (!screenshotPath.isEmpty()) {
                ExtentTest test = getTest(testName);
                if (test != null) {
                    // Add screenshot to report with description
                    test.info(description,
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());

                    System.out.println("📸 [" + browser.toUpperCase() + "] Screenshot captured: " + description);
                }
            }
        } catch (Exception e) {
            logWarning(testName, browser, "Failed to capture screenshot: " + e.getMessage());
        }
    }

    private static String captureScreenshot(WebDriver driver, String fileName) {
        try {
            if (!(driver instanceof TakesScreenshot)) {
                return "";
            }

            // Take screenshot
            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Create unique filename
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String safeFileName = fileName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String screenshotName = safeFileName + "_" + timestamp + ".png";
            String destPath = SCREENSHOT_DIR + screenshotName;

            // Copy file
            File destFile = new File(destPath);
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Return relative path for ExtentReports
            return "screenshots/" + screenshotName;

        } catch (Exception e) {
            System.out.println("❌ Error capturing screenshot: " + e.getMessage());
            return "";
        }
    }

    public static synchronized String getReportPath() {
        return reportPath;
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();

            // Print report summary
            printReportSummary();

            System.out.println("\n📊 ================================");
            System.out.println("📊 EXTENT REPORT GENERATED");
            System.out.println("📊 Location: " + new File(reportPath).getAbsolutePath());
            System.out.println("📊 ================================\n");
        }
    }

    private static void printReportSummary() {
        long totalTests = testMap.size();
        long chromeTests = testMap.keySet().stream()
                .filter(k -> k.contains("[CHROME]") || k.toLowerCase().contains("chrome"))
                .count();
        long firefoxTests = testMap.keySet().stream()
                .filter(k -> k.contains("[FIREFOX]") || k.toLowerCase().contains("firefox"))
                .count();
        long edgeTests = testMap.keySet().stream()
                .filter(k -> k.contains("[EDGE]") || k.toLowerCase().contains("edge"))
                .count();

        System.out.println("\n📈 TEST EXECUTION SUMMARY:");
        System.out.println("📈 Total Tests: " + totalTests);
        System.out.println("📈 Chrome Tests: " + chromeTests);
        System.out.println("📈 Firefox Tests: " + firefoxTests);
        System.out.println("📈 Edge Tests: " + edgeTests);
    }

    // Alias method for compatibility
    public static synchronized void flushReport() {
        flush();
    }

    public static synchronized void openReport() {
        try {
            if (reportPath != null && new File(reportPath).exists()) {
                File htmlFile = new File(reportPath);
                Desktop.getDesktop().browse(htmlFile.toURI());
                System.out.println("🌐 Opened Extent Report in browser");
            } else {
                System.out.println("⚠️  Report file not found: " + reportPath);
            }
        } catch (Exception e) {
            System.out.println("⚠️  Could not open report automatically: " + e.getMessage());
            System.out.println("📋 You can manually open: " + new File(reportPath).getAbsolutePath());
        }
    }

    // New method: Add test execution time
    public static synchronized void addExecutionTime(String testName, long startTime, long endTime) {
        ExtentTest test = getTest(testName);
        if (test != null) {
            long executionTime = endTime - startTime;
            String timeMessage = String.format("⏱️  Execution Time: %d ms (%.2f seconds)",
                    executionTime, executionTime / 1000.0);
            test.info(timeMessage);
        }
    }

    // New method: Mark test as skipped
    public static synchronized void logSkip(String testName, String browser, String reason) {
        ExtentTest test = getTest(testName);
        if (test != null) {
            test.skip("⏭️  " + reason);
            System.out.println("⏭️  [" + browser.toUpperCase() + "] " + testName + " - SKIPPED: " + reason);
        }
    }

    // New method: Clear test map (useful for suite teardown)
    public static synchronized void clearTests() {
        testMap.clear();
        extentTestMap.clear();
        System.out.println("🧹 Cleared all test entries from ExtentReportManager");
    }
}