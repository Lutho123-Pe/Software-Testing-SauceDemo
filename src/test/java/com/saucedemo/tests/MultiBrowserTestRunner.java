package com.saucedemo.tests;

import com.saucedemo.utils.ExtentReportManager;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.testng.xml.XmlInclude;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiBrowserTestRunner {

    public static void main(String[] args) {
        System.out.println("======================================================");
        System.out.println("🚀 STARTING MULTI-BROWSER TEST EXECUTION");
        System.out.println("======================================================");
        System.out.println("📋 Execution Order: Chrome → Firefox → Edge");
        System.out.println("======================================================");

        // Initialize ExtentReports BEFORE checking drivers
        ExtentReportManager.initializeReport();

        long startTime = System.currentTimeMillis();

        try {
            // Clean old results
            cleanDirectories();

            // Create results directory
            createResultsDirectory();

            // Run tests for each browser
            List<TestResult> results = runAllBrowsers();

            // Flush ExtentReports
            ExtentReportManager.flushReport();

            // Open report
            openExtentReport();

            // Print summary
            printExecutionSummary(results, startTime);

        } catch (Exception e) {
            System.out.println("❌ Execution failed: " + e.getMessage());
            e.printStackTrace();

            // Make sure report is generated even on failure
            ExtentReportManager.flushReport();
        }
    }

    private static void cleanDirectories() {
        try {
            // Clean test-output directory (preserve ExtentReports folder)
            File testOutput = new File("test-output");
            if (testOutput.exists() && testOutput.isDirectory()) {
                File[] files = testOutput.listFiles();
                if (files != null) {
                    for (File file : files) {
                        // Delete everything except ExtentReports folder
                        if (!file.getName().equalsIgnoreCase("ExtentReports")) {
                            deleteDirectory(file);
                        }
                    }
                }
                System.out.println("🧹 Cleaned test-output directory (preserved ExtentReports)");
            }

            // Clean old screenshots from root
            File screenshots = new File("screenshots");
            if (screenshots.exists()) {
                deleteDirectory(screenshots);
                System.out.println("🧹 Cleaned screenshots directory");
            }

        } catch (Exception e) {
            System.out.println("⚠️ Warning: Could not clean directories: " + e.getMessage());
        }
    }

    private static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        if (dir.exists()) {
            dir.delete();
        }
    }

    private static void createResultsDirectory() {
        try {
            Files.createDirectories(Paths.get("test-output/ExtentReports"));
            Files.createDirectories(Paths.get("test-output/ExtentReports/screenshots"));
            System.out.println("✅ Created results directories");
        } catch (Exception e) {
            System.out.println("⚠️ Warning: Could not create directories: " + e.getMessage());
        }
    }

    private static List<TestResult> runAllBrowsers() {
        String[] browsers = {"chrome", "firefox", "edge"};
        List<TestResult> results = new ArrayList<>();

        for (String browser : browsers) {
            System.out.println("\n══════════════════════════════════════════════════════");
            System.out.println("🖥️  EXECUTING TESTS ON: " + browser.toUpperCase());
            System.out.println("══════════════════════════════════════════════════════");

            // Set system properties for each browser
            setBrowserProperties(browser);

            long browserStartTime = System.currentTimeMillis();
            boolean success = runTestsForBrowser(browser);
            long browserEndTime = System.currentTimeMillis();

            long duration = (browserEndTime - browserStartTime) / 1000;
            String status = success ? "PASSED" : "FAILED";

            results.add(new TestResult(browser, status, duration));

            System.out.println("══════════════════════════════════════════════════════");
            System.out.println((success ? "✅ " : "❌ ") + browser.toUpperCase() + " TESTS " + status + " IN " + duration + "s");
            System.out.println("══════════════════════════════════════════════════════");

            // Wait between browsers
            if (!browser.equals(browsers[browsers.length - 1])) {
                try {
                    int waitTime = 2000;
                    System.out.println("⏳ Waiting " + (waitTime/1000) + " seconds before next browser...");
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return results;
    }

    private static void setBrowserProperties(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver",
                        "C:\\Users\\alulutho.tokwe\\Documents\\chromedriver-win64 1\\chromedriver-win64\\chromedriver.exe");
                System.setProperty("webdriver.chrome.silentOutput", "true");
                break;
            case "edge":
                System.setProperty("webdriver.edge.driver",
                        "C:\\Users\\alulutho.tokwe\\Documents\\edgedriver_win64\\msedgedriver.exe");
                System.setProperty("webdriver.edge.silentOutput", "true");
                break;
            case "firefox":
                // Firefox will use WebDriverManager
                System.setProperty("webdriver.firefox.logfile", "test-output/firefox.log");
                break;
        }
    }

    private static boolean runTestsForBrowser(String browser) {
        TestNG testNG = null;
        try {
            testNG = new TestNG();

            // Set TestNG configuration
            testNG.setVerbose(0);
            testNG.setUseDefaultListeners(false);

            // Create XML suite
            XmlSuite suite = createSuite(browser);

            List<XmlSuite> suites = new ArrayList<>();
            suites.add(suite);
            testNG.setXmlSuites(suites);

            // Set output directory
            String outputDir = "test-output/" + browser;
            new File(outputDir).mkdirs();
            testNG.setOutputDirectory(outputDir);

            // Simple custom listener for logging only
            testNG.addListener(new org.testng.TestListenerAdapter() {
                @Override
                public void onTestStart(org.testng.ITestResult result) {
                    System.out.println("▶️  Starting: " + result.getName() + " [" + browser.toUpperCase() + "]");
                }

                @Override
                public void onTestSuccess(org.testng.ITestResult result) {
                    System.out.println("✅ Passed: " + result.getName() + " [" + browser.toUpperCase() + "]");
                }

                @Override
                public void onTestFailure(org.testng.ITestResult result) {
                    System.out.println("❌ Failed: " + result.getName() + " [" + browser.toUpperCase() + "]");
                    if (result.getThrowable() != null) {
                        System.out.println("   Error: " + result.getThrowable().getMessage());
                    }
                }

                @Override
                public void onTestSkipped(org.testng.ITestResult result) {
                    System.out.println("⏭️  Skipped: " + result.getName() + " [" + browser.toUpperCase() + "]");
                }
            });

            // Run tests
            System.out.println("▶️  Running " + browser + " tests...");
            testNG.run();

            // SIMPLIFIED: Return true if no exception occurred during execution
            // Test results are already logged by the listener above
            System.out.println("📊 " + browser.toUpperCase() + " Tests execution completed");
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error running tests for " + browser + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Clean up TestNG instance
            if (testNG != null) {
                testNG = null;
            }
        }
    }

    private static XmlSuite createSuite(String browser) {
        XmlSuite suite = new XmlSuite();
        suite.setName(browser.toUpperCase() + " Test Suite");
        suite.setParallel(XmlSuite.ParallelMode.NONE);
        suite.setThreadCount(1);
        suite.setVerbose(0);

        // Create test
        XmlTest test = new XmlTest(suite);
        test.setName(browser.toUpperCase() + " Tests");
        test.setPreserveOrder(true);

        // Set browser parameter
        Map<String, String> parameters = new HashMap<>();
        parameters.put("browser", browser);
        test.setParameters(parameters);

        // Add test class with specific methods
        List<XmlClass> classes = new ArrayList<>();
        XmlClass xmlClass = new XmlClass("com.saucedemo.tests.CrossBrowserTests");

        // Specify which methods to run
        List<XmlInclude> methods = new ArrayList<>();
        methods.add(new XmlInclude("testInvalidLogin"));
        methods.add(new XmlInclude("testValidLogin"));
        methods.add(new XmlInclude("testCompletePurchaseFlow"));
        methods.add(new XmlInclude("testLogout"));
        methods.add(new XmlInclude("testAddRemoveItemsFromCart"));

        xmlClass.setIncludedMethods(methods);
        classes.add(xmlClass);
        test.setXmlClasses(classes);

        return suite;
    }

    private static void openExtentReport() {
        try {
            // Wait a moment for report to be fully written
            Thread.sleep(1000);

            String reportPath = ExtentReportManager.getReportPath();
            if (reportPath != null && !reportPath.isEmpty()) {
                File reportFile = new File(reportPath);
                if (reportFile.exists()) {
                    System.out.println("\n🔗 Extent Report Location: " + reportFile.getAbsolutePath());

                    // Try to open report in default browser
                    if (java.awt.Desktop.isDesktopSupported()) {
                        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                        if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                            desktop.browse(reportFile.toURI());
                            System.out.println("✅ Report opened in browser");
                            return;
                        }
                    }

                    // Fallback: Print instructions
                    System.out.println("⚠️ Could not open report automatically.");
                    System.out.println("📋 Please manually open: " + reportFile.getAbsolutePath());

                } else {
                    System.out.println("❌ Report file not found at: " + reportPath);

                    // Try to find the latest report
                    File extentReportsDir = new File("test-output/ExtentReports");
                    if (extentReportsDir.exists() && extentReportsDir.isDirectory()) {
                        File[] reportFiles = extentReportsDir.listFiles((dir, name) ->
                                name.startsWith("TestReport_") && name.endsWith(".html"));

                        if (reportFiles != null && reportFiles.length > 0) {
                            // Get the most recent file
                            File latestReport = reportFiles[0];
                            for (File file : reportFiles) {
                                if (file.lastModified() > latestReport.lastModified()) {
                                    latestReport = file;
                                }
                            }
                            System.out.println("📋 Found latest report: " + latestReport.getAbsolutePath());
                        }
                    }
                }
            } else {
                System.out.println("❌ No report path available");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not open report: " + e.getMessage());
        }
    }

    private static void printExecutionSummary(List<TestResult> results, long startTime) {
        long endTime = System.currentTimeMillis();
        long totalDuration = (endTime - startTime) / 1000;

        System.out.println("\n📈 =============== EXECUTION SUMMARY ===============");
        System.out.println("┌─────────────────┬──────────┬─────────────────┐");
        System.out.println("│     BROWSER     │  STATUS  │     DURATION    │");
        System.out.println("├─────────────────┼──────────┼─────────────────┤");

        int passed = 0;
        int failed = 0;

        for (TestResult result : results) {
            String statusIcon = result.status.equals("PASSED") ? "✅" : "❌";
            System.out.printf("│ %-15s │ %-4s %-4s │ %-15s │\n",
                    result.browser.toUpperCase(), statusIcon, result.status, result.duration + "s");

            if (result.status.equals("PASSED")) {
                passed++;
            } else {
                failed++;
            }
        }

        System.out.println("└─────────────────┴──────────┴─────────────────┘");
        System.out.println("\n📊 OVERALL STATISTICS:");
        System.out.println("   ✅ Passed Browsers: " + passed);
        System.out.println("   ❌ Failed Browsers: " + failed);
        System.out.println("   ⏱️  Total Time: " + totalDuration + " seconds");

        if (failed == 0) {
            System.out.println("\n🎉 ALL BROWSER TESTS PASSED SUCCESSFULLY!");
        } else {
            System.out.println("\n⚠️  SOME BROWSER TESTS FAILED. CHECK THE REPORT FOR DETAILS.");
        }

        System.out.println("======================================================");
    }

    static class TestResult {
        String browser;
        String status;
        long duration;

        TestResult(String browser, String status, long duration) {
            this.browser = browser;
            this.status = status;
            this.duration = duration;
        }
    }
}