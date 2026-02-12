package stepsDefinations;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import hooks.Hooks;
import pages.CartPage;
import pages.HomePage;
import pages.ProductPage;

import java.time.Duration;
import java.util.List;

public class AddToCartSteps {

    private static final Logger log = LogManager.getLogger(AddToCartSteps.class);

    private final WebDriver driver;
    private final HomePage home;
    private final ProductPage product;
    private final CartPage cart;

    private String lastOpenedProduct = "";

    public AddToCartSteps() {
        // Assuming you have a WebDriverFactory or Hooks that sets the driver in a ThreadLocal/static holder
        this.driver = Hooks.getDriver(); // <-- replace with your way of getting driver
        this.home   = new HomePage(driver);
        this.product = new ProductPage(driver);
        this.cart    = new CartPage(driver);
    }
    

private void ensureOnCartPage() {
    if (!driver.getCurrentUrl().contains("cart.html")) {
        // Click the Cart link in header
        driver.findElement(By.id("cartur")).click();
        new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(ExpectedConditions.urlContains("cart.html"));
        new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("tbodyid")));
    }
    // Your page-object wait — will also wait for at least one row
    cart.waitForCartToLoad();
}


    @Given("I am on the Demoblaze home page")
    public void i_am_on_the_demoblaze_home_page() {
        home.open();
    }

    @When("I open category {string}")
    public void i_open_category(String category) {
        home.openCategory(category);
    }

    @When("I open product {string}")
    public void i_open_product(String productName) {
        home.openProduct(productName);
    }

    @Then("product page should show the title")
    public void product_page_should_show_the_title() {
        String title = product.getProductTitle();
        log.info("Verifying product page title. Expected: {}, Actual: {}", lastOpenedProduct, title);
        Assert.assertTrue(
                title.equalsIgnoreCase(lastOpenedProduct),
                "Expected title: " + lastOpenedProduct + " but found: " + title
        );
    }

    @When("I add the product to the cart")
    public void i_add_the_product_to_the_cart() {
        String title = product.getProductTitle();
        lastOpenedProduct = title;
        log.info("Adding product to cart: {}", title);
        product.addToCartAndAcceptAlert();
    }

    @When("I go back to home")
    public void i_go_back_to_home() {
        home.open();
    }
    
    @When("I open the cart page")
    public void i_open_the_cart_page() {
        driver.findElement(org.openqa.selenium.By.id("cartur")).click();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(12))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("cart.html"));
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(12))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.id("tbodyid")));
        cart.waitForCartToLoad();

        // DEBUG
        System.out.println("DEBUG after openCart URL: " + driver.getCurrentUrl());
        System.out.println("DEBUG rows on cart: " + driver.findElements(org.openqa.selenium.By.cssSelector("#tbodyid > tr")).size());
    }
    
    
//    @When("I open the cart page")
//    public void i_open_the_cart_page() {
//        // If you have a HomePage method, you can keep it, but we also enforce URL and tbody checks.
//        // home.openCart();
//        driver.findElement(org.openqa.selenium.By.id("cartur")).click();
//
//        // Must be on the real cart page
//        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(12))
//                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("cart.html"));
//
//        // The cart page also has tbodyid, but now URL is correct
//        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(12))
//                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.id("tbodyid")));
//
//        // Now wait for actual cart rows (we expect >0 after adding)
//        cart.waitForCartToLoad();
//
//        // DEBUG (keep temporarily)
//        System.out.println("DEBUG after openCart URL: " + driver.getCurrentUrl());
//        System.out.println("DEBUG rows on cart: " + driver.findElements(org.openqa.selenium.By.cssSelector("#tbodyid > tr")).size());
//    }
//
//    @When("I open the cart page")
//    public void i_open_the_cart_page() {
//        home.openCart();
//        cart.waitForCartToLoad(); // important: row-based wait
//    }

//    @Then("I should see {string} in the cart")
//    public void i_should_see_in_the_cart(String expected) {
//        List<String> items = cart.getProductNamesInCart();
//        log.info("Cart items collected: {}", items);
//        Assert.assertTrue(
//                items.stream().anyMatch(n -> n.equalsIgnoreCase(expected)),
//                "Expected to find '" + expected + "' in cart. Items: " + items
//                
//                
//        );
//    }

    
//    @Then("I should see {string} in the cart")
//    public void i_should_see_in_the_cart(String expected) {
//
//        // Short retry loop (helps if rows load slowly)
//        List<String> items = java.util.List.of();
//        long end = System.currentTimeMillis() + 2500;
//
//        while (System.currentTimeMillis() < end) {
//            items = cart.getProductNamesInCart();
//            if (!items.isEmpty()) break;
//            try { Thread.sleep(250); } catch (InterruptedException ignored) {}
//        }
//
//        // --- DEBUG lines (keep these until test passes) ---
//        System.out.println("DEBUG URL: " + driver.getCurrentUrl());
//        System.out.println("DEBUG tbody present: " + 
//            !driver.findElements(org.openqa.selenium.By.id("tbodyid")).isEmpty());
//        System.out.println("DEBUG rows(#tbodyid > tr): " + 
//            driver.findElements(org.openqa.selenium.By.cssSelector("#tbodyid > tr")).size());
//        // ---------------------------------------------------
//
//        // Final assertion
//        org.testng.Assert.assertTrue(
//            items.stream().anyMatch(n -> n.equalsIgnoreCase(expected)),
//            "Expected to find '" + expected + "' in cart. Items: " + items
//        );
//    }
    @Then("I should see {string} in the cart")
//    public void i_should_see_in_the_cart(String expected) {
//        // small retry
//        java.util.List<String> items = java.util.List.of();
//        long end = System.currentTimeMillis() + 2500;
//        while (System.currentTimeMillis() < end) {
//            items = cart.getProductNamesInCart();
//            if (!items.isEmpty()) break;
//            try { Thread.sleep(250); } catch (InterruptedException ignored) {}
//        }
//
//        System.out.println("DEBUG URL: " + driver.getCurrentUrl());
//        System.out.println("DEBUG tbody present: " + !driver.findElements(org.openqa.selenium.By.id("tbodyid")).isEmpty());
//        System.out.println("DEBUG rows(#tbodyid > tr): " + driver.findElements(org.openqa.selenium.By.cssSelector("#tbodyid > tr")).size());
//
//        org.testng.Assert.assertTrue(
//            items.stream().anyMatch(n -> n.equalsIgnoreCase(expected)),
//            "Expected to find '" + expected + "' in cart. Items: " + items
//        );

public void i_should_see_in_the_cart(String expected) {
    // Make sure we are truly on the cart page before reading anything
    ensureOnCartPage();

    // Short retry loop in case the name cell text renders a bit late
    List<String> items = java.util.List.of();
    long end = System.currentTimeMillis() + 2500;
    while (System.currentTimeMillis() < end) {
        items = cart.getProductNamesInCart();  // uses the CartPage logic we set earlier
        if (!items.isEmpty()) break;
        try { Thread.sleep(250); } catch (InterruptedException ignored) {}
    }

    System.out.println("DEBUG URL: " + driver.getCurrentUrl());
    System.out.println("DEBUG tbody present: " + !driver.findElements(By.id("tbodyid")).isEmpty());
    System.out.println("DEBUG rows(#tbodyid > tr): " + driver.findElements(By.cssSelector("#tbodyid > tr")).size());
    // ---------------------------------

    org.testng.Assert.assertTrue(
        items.stream().anyMatch(n -> n.trim().equalsIgnoreCase(expected.trim())),
        "Expected to find '" + expected + "' in cart. Items: " + items
    );

    }
   
//    @Then("I should see {int} items in the cart")
//    public void i_should_see_items_in_the_cart(Integer expectedCount) {
//        int actual = cart.getItemsCount();
//        log.info("Cart count - Expected: {}, Actual: {}", expectedCount, actual);
//        Assert.assertEquals(actual, expectedCount.intValue(),
//                "Expected " + expectedCount + " items but found " + actual);
//    }
    
    @Then("I should see {int} items in the cart")
    public void i_should_see_items_in_the_cart(Integer expectedCount) {
        // Ensure we are actually on cart.html
        if (!driver.getCurrentUrl().contains("cart.html")) {
            driver.findElement(org.openqa.selenium.By.id("cartur")).click();
        }

        // Wait for exactly what we intend: at least N rows visible
        cart.waitForAtLeastNRows(expectedCount);

        // Now count the rows
        int actual = driver.findElements(org.openqa.selenium.By.cssSelector("#tbodyid > tr")).size();

        // DEBUG (keep temporarily)
        System.out.println("DEBUG URL: " + driver.getCurrentUrl());
        System.out.println("DEBUG rows now: " + actual);

        org.testng.Assert.assertEquals(
                actual, expectedCount.intValue(),
                "Expected " + expectedCount + " items but found " + actual
        );
    }
//
//    @When("I delete product {string} from the cart")
//    public void i_delete_product_from_the_cart(String productName) {
//        home.openCart(); 
//        System.out.println("DEBUG after openCart URL: " + driver.getCurrentUrl());
//        System.out.println("DEBUG tbody present: " + 
//            !driver.findElements(org.openqa.selenium.By.id("tbodyid")).isEmpty());// ensure we're on cart
//        cart.waitForCartToLoad(); // ensure rows are present
//        cart.deleteProduct(productName);
//    }
    

@When("I delete product {string} from the cart")
public void i_delete_product_from_the_cart(String productName) {
    home.openCart();                 // you already have this
    cart.deleteProduct(productName); // make this method robust as below
}


//    @Then("I should not see {string} in the cart")
//    public void i_should_not_see_in_the_cart(String productName) {
//        List<String> items = cart.getProductNamesInCart();
//        log.info("Cart items after delete attempt: {}", items);
//        Assert.assertTrue(
//                items.stream().noneMatch(n -> n.equalsIgnoreCase(productName)),
//                "Expected not to find '" + productName + "' in cart. Items: " + items
//        );
  //  }

@Then("I should not see {string} in the cart")
public void i_should_not_see_in_the_cart(String productName) {
    java.util.List<String> names = cart.getProductNamesInCart();
    org.testng.Assert.assertTrue(
        names.stream().noneMatch(n -> n.equalsIgnoreCase(productName)),
        "Expected NOT to see '" + productName + "' but found: " + names
    );
}
}
