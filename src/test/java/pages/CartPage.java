package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ui.Locators;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class CartPage {

    private static final Logger log = LogManager.getLogger(CartPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;
 // Wait until we are truly on the cart page and tbody exists (works for empty or non-empty)
    public void waitForCartPageShell() {
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("cart.html"));
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .presenceOfElementLocated(org.openqa.selenium.By.id("tbodyid")));
    }

    // Wait until the cart has at least 'expected' rows (e.g., 1, 2, 3...)
    public void waitForAtLeastNRows(int expected) {
        waitForCartPageShell();
        org.openqa.selenium.By rows = org.openqa.selenium.By.cssSelector("#tbodyid > tr");
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .numberOfElementsToBeMoreThan(rows, expected - 1));
    }

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    /**
     * Critical: wait for at least one row in the cart table.
     * This avoids the 'Items: []' race where Place Order is visible but rows aren't rendered yet.
     */
//    public void waitForCartToLoad() {
//        // If you want to handle both empty and non-empty carts, use a custom condition.
//        // For the test that expects an item, we wait for at least 1 row.
//        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(Locators.Cart.ROWS, 0));
//    }
    
    public void waitForCartToLoad() {
        // Ensure we are truly on the cart page (avoid product page's tbodyid trap)
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("cart.html"));

        // tbody exists on cart
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.id("tbodyid")));

        // We expect at least one row after adding a product
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.numberOfElementsToBeMoreThan(Locators.Cart.ROWS, 0));
    }


    public List<String> getProductNamesInCart() {
        // Do not wait here; caller should ensure waitForCartToLoad() has been called
        List<WebElement> rows = driver.findElements(Locators.Cart.ROWS);
        List<String> names = rows.stream()
                .map(r -> r.findElement(Locators.Cart.ROW_NAME).getText().trim())
                .collect(Collectors.toList());
        log.info("Items currently in cart: {}", names);
        return names;
    }

    public int getItemsCount() {
        return getProductNamesInCart().size();
    }

//    public void deleteProduct(String productName) {
//        // Ensure rows are present before attempting delete
//        waitForCartToLoad();
//        List<WebElement> rows = driver.findElements(Locators.Cart.ROWS);
//        int before = rows.size();
//
//        for (WebElement row : rows) {
//            String name = row.findElement(Locators.Cart.ROW_NAME).getText().trim();
//            if (name.equalsIgnoreCase(productName)) {
//                log.info("Deleting product: {}", productName);
//                row.findElement(Locators.Cart.ROW_DELETE_LINK).click();
//
//                // Wait until row count decreases
//                new WebDriverWait(driver, Duration.ofSeconds(10))
//                        .until(ExpectedConditions.numberOfElementsToBeLessThan(Locators.Cart.ROWS, before));
//                return;
//            }
//        }
//        log.warn("Product '{}' not found in the cart to delete", productName);
//    } 
    
    public void deleteProduct(String productName) {
        // Ensure we are on cart and tbody exists (neutral wait)
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
            .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("cart.html"));
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
            .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.id("tbodyid")));

        // Locators that match Demoblaze cart DOM
        org.openqa.selenium.By ROWS     = org.openqa.selenium.By.cssSelector("#tbodyid > tr");
        org.openqa.selenium.By ROW_NAME = org.openqa.selenium.By.cssSelector("td:nth-of-type(2)");
        org.openqa.selenium.By DEL_LINK = org.openqa.selenium.By.linkText("Delete");

        // Read current rows and remember count
        java.util.List<org.openqa.selenium.WebElement> rows = driver.findElements(ROWS);
        int before = rows.size(); // (your debug shows before == 1)

        if (before == 0) {
            System.out.println("DeleteProduct DEBUG: No rows to delete.");
            return; // or throw if your test expects at least one
        }

        // Find the row for the requested product (case-insensitive match)
        org.openqa.selenium.WebElement targetRow = null;
        for (org.openqa.selenium.WebElement r : rows) {
            String name = r.findElement(ROW_NAME).getText().trim();
            if (name.equalsIgnoreCase(productName.trim())) {
                targetRow = r;
                break;
            }
        }

        if (targetRow == null) {
            System.out.println("DeleteProduct DEBUG: Product not found in rows: " + productName);
            return; // or throw if you expect it to exist
        }

        // Click "Delete"
        targetRow.findElement(DEL_LINK).click();

        // Wait until the number of rows decreases (e.g., from 1 → 0)
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
            .until(org.openqa.selenium.support.ui.ExpectedConditions.numberOfElementsToBeLessThan(ROWS, before));

        // Optional debug
        int after = driver.findElements(ROWS).size();
        System.out.println("DeleteProduct DEBUG: rows before=" + before + ", after=" + after);
        System.out.println("DEBUG current URL: " + driver.getCurrentUrl());
        System.out.println("DEBUG rows now: " + driver.findElements(org.openqa.selenium.By.cssSelector("#tbodyid > tr")).size());
       
    }
}
