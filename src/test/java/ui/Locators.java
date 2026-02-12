package ui;
import org.openqa.selenium.By;
	public final class Locators {
	    private Locators() {}

	    // ----- Common / Nav -----
	    public static final class Nav {
	        public static final By HOME_BRAND = By.cssSelector("a.navbar-brand"); // PRODUCT STORE
	        public static final By CART_LINK = By.id("cartur");
	    }

	    // ----- Home Page -----
	    public static final class Home {
	        public static By CATEGORY(String categoryName) {
	            return By.xpath("//a[@id='itemc' and normalize-space()='" + categoryName + "']");
	        }
	        public static By PRODUCT_LINK(String name) {
	            return By.xpath("//a[@class='hrefch' and normalize-space()='" + name + "']");
	        }
	        public static final By PRODUCTS_GRID = By.cssSelector("#tbodyid .card");
	        public static final By GRID_CONTAINER = By.cssSelector("#tbodyid");
	    }

	    // ----- Product Detail Page -----
	    public static final class Product {
	        public static final By TITLE = By.cssSelector(".name");
	        public static final By ADD_TO_CART = By.xpath("//a[text()='Add to cart']");
	    }

	    // ----- Cart Page -----
	    public static final class Cart {
	        public static final By ROWS = By.cssSelector("#tbodyid > tr");
	        public static final By PLACE_ORDER = By.xpath("//button[normalize-space()='Place Order']");
	        public static By ROW_DELETE_LINK = By.linkText("Delete");
	        public static By ROW_NAME = By.cssSelector("td:nth-of-type(2)");
	    }
	
	}
