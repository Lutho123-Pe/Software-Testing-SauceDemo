package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends BasePage {

    // Page Elements
    @FindBy(id = "user-name")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = ".error-message-container h3")
    private WebElement errorMessage;

    // Constructor
    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ========== NEW METHODS TO ADD ==========

    // Enter username
    public void enterUsername(String username) {
        type(usernameField, username);
    }

    // Enter password
    public void enterPassword(String password) {
        type(passwordField, password);
    }

    // Click login button
    public void clickLogin() {
        click(loginButton);
    }

    // ========== EXISTING METHODS ==========

    // Login method (if you already have it)
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    // Get error message
    public String getErrorMessage() {
        try {
            waitForElementToBeVisible(errorMessage);
            return errorMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }

    // Check if on login page
    public boolean isLoginPageDisplayed() {
        try {
            return loginButton.isDisplayed() &&
                    usernameField.isDisplayed() &&
                    passwordField.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Clear login form
    public void clearLoginForm() {
        usernameField.clear();
        passwordField.clear();
    }
}