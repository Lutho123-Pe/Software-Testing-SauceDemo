package com.saucedemo.tests;

import com.saucedemo.utils.ExtentReportManager;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompleteTestRunner {

    public static void main(String[] args) {
        System.out.println("🚀 COMPLETE TEST SUITE EXECUTION 🚀");
        System.out.println("=====================================");
        System.out.println("Running all tests across:");
        System.out.println("✓ Chrome");
        System.out.println("✓ Firefox");
        System.out.println("✓ Edge");
        System.out.println("=====================================");

        // Clean old results
        cleanOldResults();

        // Initialize ExtentReports
        ExtentReportManager.initializeReport();

        // Run tests for all browsers
        boolean success = runTestsForAllBrowsers();

        // Generate report
        ExtentReportManager.flushReport();

        if (success) {
            System.out.println("\n✅ TEST EXECUTION COMPLETED SUCCESSFULLY");
        } else {
            System.out.println("\n⚠️ TEST EXECUTION COMPLETED WITH SOME FAILURES");
        }

        System.out.println("📊 ExtentReport has been generated in: test-output/ExtentReports/");

        // Try to open the report
        openReport();
    }

    private static void cleanOldResults() {
        try {
            File testOutput = new File("test-output");
            if (testOutput.exists()) {
                deleteDirectory(testOutput);
                System.out.println("🧹 Cleaned old test results");
            }

            // Create fresh directories
            new File("test-output/ExtentReports").mkdirs();
            new File("test-output/screenshots").mkdirs();
        } catch (Exception e) {
            System.out.println("⚠️ Could not clean directories: " + e.getMessage());
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
        dir.delete();
    }

    private static boolean runTestsForAllBrowsers() {
        String[] browsers = {"chrome", "firefox", "edge"};
        boolean allSuccess = true;

        for (String browser : browsers) {
            System.out.println("\n🖥️  Running tests on: " + browser.toUpperCase());

            try {
                TestNG testNG = new TestNG();

                // Create XML suite
                XmlSuite suite = createSuite(browser);

                List<XmlSuite> suites = new ArrayList<>();
                suites.add(suite);
                testNG.setXmlSuites(suites);

                // Set output directory
                testNG.setOutputDirectory("test-output/" + browser);

                // Run tests
                testNG.run();

                System.out.println("✅ " + browser.toUpperCase() + " tests completed");
            } catch (Exception e) {
                System.out.println("❌ Error running " + browser + " tests: " + e.getMessage());
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    private static XmlSuite createSuite(String browser) {
        XmlSuite suite = new XmlSuite();
        suite.setName(browser.toUpperCase() + " Test Suite");

        XmlTest test = new XmlTest(suite);
        test.setName(browser.toUpperCase() + " Tests");

        // Set browser parameter
        Map<String, String> parameters = new HashMap<>();
        parameters.put("browser", browser);
        test.setParameters(parameters);

        // Add test classes - FIXED: Use List<XmlClass> instead of List<String>
        List<XmlClass> classes = new ArrayList<>();
        classes.add(new XmlClass("com.saucedemo.tests.AllTestsSuite"));
        // Add other test classes if needed
        // classes.add(new XmlClass("com.saucedemo.tests.RegressionTests"));
        // classes.add(new XmlClass("com.saucedemo.tests.SmokeTests"));

        test.setXmlClasses(classes);

        return suite;
    }

    private static void openReport() {
        try {
            String reportPath = ExtentReportManager.getReportPath();
            if (reportPath != null) {
                File reportFile = new File(reportPath);
                if (reportFile.exists()) {
                    System.out.println("\n🔗 Extent Report: " + reportFile.getAbsolutePath());

                    // Open report in default browser
                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                        Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "", reportFile.getAbsolutePath()});
                    } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                        Runtime.getRuntime().exec(new String[]{"open", reportFile.getAbsolutePath()});
                    } else {
                        Runtime.getRuntime().exec(new String[]{"xdg-open", reportFile.getAbsolutePath()});
                    }

                    Thread.sleep(3000);
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not open report: " + e.getMessage());
        }
    }
}