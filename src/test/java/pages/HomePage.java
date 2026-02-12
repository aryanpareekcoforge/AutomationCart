package pages;

import hooks.Hooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.Locators;

import java.time.Duration;

public class HomePage {

    private static final Logger log = LogManager.getLogger(HomePage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void open() {
        String url = Hooks.getBaseUrl();
        log.info("Opening URL: {}", url);
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.Nav.HOME_BRAND));
    }

    public void openCategory(String category) {
        log.info("Opening category: {}", category);
        wait.until(ExpectedConditions.elementToBeClickable(Locators.Home.CATEGORY(category))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.Home.PRODUCTS_GRID));
    }

    /** <-- This is the missing method the step file calls */
//    public void openProduct(String productName) {
//        log.info("Opening product: {}", productName);
//        wait.until(
//            ExpectedConditions.elementToBeClickable(Locators.Home.PRODUCT_LINK(productName))
//        ).click();
//    }
    
    public void openProduct(String productName) {
        // Defensive: make sure we are in the top-level context
        driver.switchTo().defaultContent();

        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(12));

        // 1) Wait for the product grid to be present/visible (prevents stale right away)
        // If you have a dedicated locator for cards, use it; otherwise use the common container
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .visibilityOfElementLocated(org.openqa.selenium.By.id("tbodyid")));

        // 2) Build a locator for the specific product anchor by its title text
        // Demoblaze cards render product names as links <a class="hrefch">Samsung galaxy s6</a>
        org.openqa.selenium.By productLink = org.openqa.selenium.By.xpath(
                "//a[@class='hrefch' and normalize-space()='" + productName + "']"
        );

        // 3) Retry-on-stale wrapper: re-find and click
        int attempts = 0;
        while (true) {
            try {
                // Re-locate fresh each attempt and wait until it's clickable
                org.openqa.selenium.WebElement link = wait.until(
                        org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(productLink)
                );

                // (Optional) scroll into view to avoid overlay/click intercept issues
                ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript("arguments[0].scrollIntoView({block:'center'});", link);

                link.click();

                // 4) Verify navigation to product page by waiting for product title to appear
                wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                        .urlContains("prod.html"));
                break; // success
            } catch (org.openqa.selenium.StaleElementReferenceException stale) {
                if (++attempts >= 3) {
                    throw stale; // give up after a few quick retries
                }
                // Small backoff and loop; DOM will be re-queried next iteration
                try { Thread.sleep(150); } catch (InterruptedException ignored) {}
            }
        }
    }


    public void goHome() {
        log.info("Clicking Home Brand (PRODUCT STORE)");
        wait.until(ExpectedConditions.elementToBeClickable(Locators.Nav.HOME_BRAND)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.Home.GRID_CONTAINER));
    }

    public void openCart() {
        log.info("Navigating to Cart");
        wait.until(ExpectedConditions.elementToBeClickable(Locators.Nav.CART_LINK)).click();
    }
    
    

    public void deleteProduct(String productName) {
        // Ensure we are on the cart page and tbody exists (neutral; works for empty/non-empty)
        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(12));

        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("cart.html"));
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .presenceOfElementLocated(org.openqa.selenium.By.id("tbodyid")));

        // Robust cart locators for Demoblaze
        final org.openqa.selenium.By ROWS     = org.openqa.selenium.By.cssSelector("#tbodyid > tr");
        final org.openqa.selenium.By ROW_NAME = org.openqa.selenium.By.cssSelector("td:nth-of-type(2)");
        final org.openqa.selenium.By DEL_LINK = org.openqa.selenium.By.linkText("Delete");

        // Current rows and baseline count (your debug showed 1)
        java.util.List<org.openqa.selenium.WebElement> rows = driver.findElements(ROWS);
        int before = rows.size();
        if (before == 0) {
            System.out.println("DeleteProduct DEBUG: No rows to delete.");
            return; // or throw if your test expects at least one
        }

        // Find the row for the requested product (case-insensitive)
        org.openqa.selenium.WebElement targetRow = null;
        for (org.openqa.selenium.WebElement r : rows) {
            String name = r.findElement(ROW_NAME).getText().trim();
            if (name.equalsIgnoreCase(productName.trim())) {
                targetRow = r;
                break;
            }
        }

        if (targetRow == null) {
            System.out.println("DeleteProduct DEBUG: Product not found in cart: " + productName);
            return; // or throw if you expect it to exist
        }

        // Click "Delete" with a small retry for intercept/stale
        int attempts = 0;
        while (true) {
            try {
                targetRow.findElement(DEL_LINK).click();
                break;
            } catch (org.openqa.selenium.StaleElementReferenceException | org.openqa.selenium.ElementClickInterceptedException e) {
                if (++attempts >= 2) throw e;
                try { Thread.sleep(150); } catch (InterruptedException ignored) {}
                // Re-find target row after a tiny pause
                rows = driver.findElements(ROWS);
                for (org.openqa.selenium.WebElement r : rows) {
                    String name = r.findElement(ROW_NAME).getText().trim();
                    if (name.equalsIgnoreCase(productName.trim())) {
                        targetRow = r; break;
                    }
                }
            }
        }

        // Wait until the number of rows DECREASES (e.g., 1 → 0)
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .numberOfElementsToBeLessThan(ROWS, before));

        // Optional debug
        int after = driver.findElements(ROWS).size();
        System.out.println("DeleteProduct DEBUG: rows before=" + before + ", after=" + after);
    }
    }