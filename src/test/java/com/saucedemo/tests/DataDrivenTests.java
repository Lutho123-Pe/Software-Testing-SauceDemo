package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.LoginPage;


import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class DataDrivenTests extends BaseTest {

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        return new Object[][] {
            {"standard_user", "secret_sauce", true},
            {"locked_out_user", "secret_sauce", false},
            {"problem_user", "secret_sauce", true},
            {"performance_glitch_user", "secret_sauce", true},
            {"error_user", "secret_sauce", true},
            {"visual_user", "secret_sauce", true}
        };
    }

    @Test(dataProvider = "loginData")
    
    public void testLoginDataDriven(String username, String password, boolean shouldSucceed) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);
        if (shouldSucceed) {
            Assert.assertTrue(driver.getCurrentUrl().contains("inventory"));
        } else {
            Assert.assertTrue(loginPage.getErrorMessage().length() > 0);
        }
    }
}
