package com.saucedemo.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.saucedemo.utils.DriverManager;
import com.saucedemo.utils.ExtentReportManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener implements ITestListener, ISuiteListener {

    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static ExtentReports extent;

    @Override
    public void onStart(ISuite suite) {
        extent = ExtentReportManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest extentTest = extent.createTest(testName);
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest t = test.get();
        t.fail(result.getThrowable());

        try {
            // Capture screenshot
            WebDriver driver = DriverManager.getDriver();
            if (driver != null) {
                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String screenshotPath = "test-output/screenshots/" +
                        result.getMethod().getMethodName() + "_" + timestamp + ".png";

                File dest = new File(screenshotPath);

                // Create parent directories if they don't exist
                File parentDir = dest.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    boolean dirsCreated = parentDir.mkdirs();
                    if (!dirsCreated) {
                        t.warning("Failed to create screenshot directory");
                        return;
                    }
                }

                // Copy file with proper exception handling
                Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                t.addScreenCaptureFromPath(screenshotPath);
            }
        } catch (IOException e) {
            t.warning("Screenshot capture failed (IOException): " + e.getMessage());
        } catch (Exception e) {
            t.warning("Screenshot capture failed: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().skip(result.getThrowable());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not implemented
    }

    @Override
    public void onStart(ITestContext context) {
        // Not implemented
    }

    @Override
    public void onFinish(ITestContext context) {
        // Not implemented
    }

    @Override
    public void onFinish(ISuite suite) {
        if (extent != null) {
            extent.flush();
        }
    }
}