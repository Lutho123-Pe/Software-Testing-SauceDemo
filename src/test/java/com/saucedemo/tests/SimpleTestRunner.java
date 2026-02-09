package com.saucedemo.tests;


import org.testng.TestNG;
import java.util.ArrayList;
import java.util.List;

public class SimpleTestRunner {
    public static void main(String[] args) {
        System.out.println("🚀 Starting SauceDemo Test Execution");
        System.out.println("======================================");

        TestNG testNG = new TestNG();

        // Create list of test suites
        List<String> suites = new ArrayList<>();
        suites.add("testng.xml");

        testNG.setTestSuites(suites);

        // Run tests
        testNG.run();

        System.out.println("\n✅ Test execution completed!");
        System.out.println("📊 Reports available in: test-output/");
    }
}
