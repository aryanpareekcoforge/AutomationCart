package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ui.Locators;

import java.time.Duration;

public class ProductPage {

    private static final Logger log = LogManager.getLogger(ProductPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    public String getProductTitle() {
        String title = wait.until(ExpectedConditions
                .visibilityOfElementLocated(Locators.Product.TITLE))
                .getText()
                .trim();
        log.info("Product title detected: {}", title);
        return title;
    }

//    public void addToCartAndAcceptAlert() {
//        log.info("Clicking 'Add to cart'");
//        wait.until(ExpectedConditions.elementToBeClickable(Locators.Product.ADD_TO_CART)).click();
//
//        log.info("Waiting for and accepting the alert");
//        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
//        log.info("Alert text: {}", alert.getText());
//        alert.accept();
//
//        // Optionally: small guard if needed because site sometimes lags
//        // new WebDriverWait(driver, Duration.ofSeconds(3)).until(d -> true);
//    }
    
    public void addToCartAndAcceptAlert() {
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .elementToBeClickable(Locators.Product.ADD_TO_CART)).click();

        org.openqa.selenium.Alert alert = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent());
        alert.accept();

        // tiny guard helps sometimes on slower runs
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }
}