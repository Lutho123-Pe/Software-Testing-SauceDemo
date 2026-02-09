package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class ProductsPage extends BasePage {

    // Page Elements
    @FindBy(className = "inventory_item")
    private List<WebElement> productItems;

    @FindBy(className = "shopping_cart_badge")
    private WebElement cartBadge;

    @FindBy(className = "title")
    private WebElement pageTitle;

    @FindBy(id = "react-burger-menu-btn")
    private WebElement burgerMenu;

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    @FindBy(className = "shopping_cart_link")
    private WebElement cartIcon;

    @FindBy(className = "product_sort_container")
    private WebElement sortDropdown;

    @FindBy(className = "inventory_item_name")
    private List<WebElement> productNames;

    @FindBy(className = "inventory_item_price")
    private List<WebElement> productPrices;

    @FindBy(className = "inventory_item_desc")
    private List<WebElement> productDescriptions;

    // Constructor
    public ProductsPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ========== ADD THIS MISSING METHOD ==========

    /**
     * Get the cart item count (same as getCartBadgeCount())
     * This is an alias for getCartBadgeCount() for compatibility
     */
    public String getCartItemCount() {
        return getCartBadgeCount();
    }

    // ========== EXISTING METHODS ==========

    public String getCartBadgeCount() {
        try {
            waitForElementToBeVisible(cartBadge);
            return cartBadge.getText();
        } catch (Exception e) {
            return "0"; // Cart badge might not be visible if cart is empty
        }
    }

    public void clickCartIcon() {
        click(cartIcon);
    }

    public boolean isProductsPageDisplayed() {
        try {
            waitForElementToBeVisible(pageTitle);
            return pageTitle.getText().equals("Products");
        } catch (Exception e) {
            return false;
        }
    }

    public String getPageTitle() {
        waitForElementToBeVisible(pageTitle);
        return pageTitle.getText();
    }

    public int getProductCount() {
        waitForCondition(d -> productItems.size() > 0, 5, "Products to load");
        return productItems.size();
    }

    public int getInventoryItemCount() {
        return getProductCount();
    }

    public void addItemToCart(int index) {
        if (index < productItems.size()) {
            WebElement item = productItems.get(index);
            WebElement addToCartButton = item.findElement(By.className("btn_inventory"));

            // Check if it's already added
            String buttonText = addToCartButton.getText();
            if (buttonText.equalsIgnoreCase("Add to cart")) {
                click(addToCartButton);
            }
        }
    }

    public void addItemToCart(String itemName) {
        for (WebElement item : productItems) {
            WebElement itemNameElement = item.findElement(By.className("inventory_item_name"));
            if (itemNameElement.getText().equals(itemName)) {
                WebElement addToCartButton = item.findElement(By.className("btn_inventory"));
                String buttonText = addToCartButton.getText();
                if (buttonText.equalsIgnoreCase("Add to cart")) {
                    click(addToCartButton);
                }
                break;
            }
        }
    }

    public void addFirstItemToCart() {
        addItemToCart(0);
    }

    public void addSecondItemToCart() {
        addItemToCart(1);
    }

    public void removeItemFromCart(int index) {
        if (index < productItems.size()) {
            WebElement item = productItems.get(index);
            try {
                WebElement removeButton = item.findElement(By.xpath(".//button[contains(text(), 'Remove')]"));
                click(removeButton);
            } catch (Exception e) {
                // Item might not be in cart
                System.out.println("Item at index " + index + " is not in cart");
            }
        }
    }

    public void removeItemFromCart(String itemName) {
        for (WebElement item : productItems) {
            WebElement itemNameElement = item.findElement(By.className("inventory_item_name"));
            if (itemNameElement.getText().equals(itemName)) {
                try {
                    WebElement removeButton = item.findElement(By.xpath(".//button[contains(text(), 'Remove')]"));
                    click(removeButton);
                } catch (Exception e) {
                    System.out.println("Item '" + itemName + "' is not in cart");
                }
                break;
            }
        }
    }

    public void logout() {
        click(burgerMenu);
        waitForElementToBeClickable(logoutLink);
        click(logoutLink);
    }

    public void goToCart() {
        clickCartIcon();
    }

    // ========== INVENTORY PAGE COMPATIBILITY METHODS ==========

    /**
     * Alias for getCartBadgeCount() - for InventoryPage compatibility
     */
    public String getCartBadge() {
        return getCartBadgeCount();
    }

    /**
     * Check if cart has items
     */
    public boolean hasItemsInCart() {
        return !getCartBadgeCount().equals("0");
    }

    /**
     * Get cart count as integer
     */
    public int getCartCount() {
        try {
            return Integer.parseInt(getCartBadgeCount());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}