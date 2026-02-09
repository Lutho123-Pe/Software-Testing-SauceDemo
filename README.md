# Sauce Demo Automation with ExtentReports

This project is a comprehensive automated testing framework for the [Sauce Demo](https://www.saucedemo.com/) web application. It utilizes **Selenium WebDriver**, **TestNG**, and **Maven**, with **ExtentReports** for detailed test reporting and screenshot capture.

## Project Overview

The framework is designed to validate the core functionalities of the Sauce Demo site, including login, product management, and the complete checkout flow. It supports cross-browser testing and provides rich visual feedback through automated reports.

### Key Features

*   **Page Object Model (POM):** Enhances maintainability and reduces code duplication.
*   **Cross-Browser Testing:** Supports Google Chrome, Mozilla Firefox, and Microsoft Edge.
*   **Detailed Reporting:** Integrated with ExtentReports for HTML-based test summaries.
*   **Automated Screenshots:** Captures screenshots on test failure for easier debugging.
*   **Data-Driven Testing:** Utilizes TestNG DataProviders for various test scenarios.

## Project Structure

The repository contains both the automation source code and project documentation:

| Directory/File | Description |
| :--- | :--- |
| `src/main/java` | Core framework components (Base classes, Page Objects, Utilities). |
| `src/test/java` | Test suites and test cases. |
| `src/test/resources` | TestNG XML configuration files. |
| `SauceDemo Picturess/` | Manual testing screenshots and evidence. |
| `Documentation/` | Project planning, test strategy, and summary reports. |
| `pom.xml` | Maven configuration and dependency management. |

## Prerequisites

Before running the tests, ensure you have the following installed:

*   **Java Development Kit (JDK):** Version 11 or higher.
*   **Apache Maven:** For dependency management and test execution.
*   **Web Browsers:** Latest versions of Chrome, Firefox, and Edge.
*   **WebDrivers:** Managed automatically via `WebDriverManager`.

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/Lutho123-Pe/Software-Testing-SauceDemo.git
cd Software-Testing-SauceDemo
```

### 2. Build the Project
```bash
mvn clean install
```

### 3. Execute Tests

You can run different test suites using the following Maven commands:

*   **Run All Tests:**
    ```bash
    mvn test -DsuiteXmlFile=src/test/resources/testng-all-tests.xml
    ```
*   **Run Smoke Tests:**
    ```bash
    mvn test -DsuiteXmlFile=src/test/resources/testng-smoke-only.xml
    ```
*   **Run Cross-Browser Tests:**
    ```bash
    mvn test -DsuiteXmlFile=src/test/resources/testng-sequential-browsers.xml
    ```

## Reporting and Results

After the test execution is complete, you can find the results in the `test-output/ExtentReports/` directory.

*   **HTML Report:** Open the latest `TestReport_*.html` file in any web browser to view the summary.
*   **Screenshots:** If any tests fail, screenshots are automatically saved in `test-output/ExtentReports/screenshots/`.

## Documentation

The project includes several key documents for a complete testing overview:
*   **Test Plan & Strategy:** Detailed approach to testing the application.
*   **Defect Report:** Documented issues found during testing (e.g., the `error_user` checkout defect).
*   **Test Summary Report:** Final results and analysis of the testing cycle.
*   **Setup and Execution Guide:** Detailed instructions for environment configuration.

---
*Developed as part of the Software Testing Group Project.*
