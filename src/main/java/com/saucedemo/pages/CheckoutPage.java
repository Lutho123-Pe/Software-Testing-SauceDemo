package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CheckoutPage extends BasePage {

    // Page Elements
    @FindBy(id = "first-name")
    private WebElement firstNameField;

    @FindBy(id = "last-name")
    private WebElement lastNameField;

    @FindBy(id = "postal-code")
    private WebElement postalCodeField;

    @FindBy(id = "continue")
    private WebElement continueButton;

    @FindBy(id = "finish")
    private WebElement finishButton;

    @FindBy(className = "complete-header")
    private WebElement successMessage;

    // Constructor
    public CheckoutPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // Fill checkout information
    public void fillInformation(String firstName, String lastName, String postalCode) {
        type(firstNameField, firstName);
        type(lastNameField, lastName);
        type(postalCodeField, postalCode);
    }

    // Click continue button
    public void clickContinue() {
        click(continueButton);
    }

    // Finish checkout
    public void finishCheckout() {
        click(finishButton);
    }

    // Get success message
    public String getSuccessMessage() {
        try {
            waitForElementToBeVisible(successMessage);
            return successMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }

    // Check if checkout step one is displayed
    public boolean isCheckoutStepOneDisplayed() {
        try {
            return driver.getCurrentUrl().contains("checkout-step-one");
        } catch (Exception e) {
            return false;
        }
    }

    // Check if checkout step two is displayed
    public boolean isCheckoutStepTwoDisplayed() {
        try {
            return driver.getCurrentUrl().contains("checkout-step-two");
        } catch (Exception e) {
            return false;
        }
    }

    // Additional methods for form fields
    public void enterFirstName(String firstName) {
        type(firstNameField, firstName);
    }

    public void enterLastName(String lastName) {
        type(lastNameField, lastName);
    }

    public void enterPostalCode(String postalCode) {
        type(postalCodeField, postalCode);
    }
}