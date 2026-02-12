package hooks;

import io.cucumber.java.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.InputStream;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Hooks {

    private static final Logger log = LogManager.getLogger(Hooks.class);
    private static WebDriver driver;
    private static final Properties data = new Properties();
    private static String baseUrl = "https://www.demoblaze.com/"; // fallback if file missing
    private static boolean dataLoaded = false;

    public static WebDriver getDriver() {
        return driver;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    private static void loadDataPropertiesOnce() {
        if (dataLoaded) return;
        try (InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("testdata/data.properties")) {
            if (is != null) {
                data.load(is);
                String configured = data.getProperty("url.base");
                if (configured != null && !configured.isBlank()) {
                    baseUrl = configured.trim();
                }
                log.info("Loaded data.properties. Base URL = {}", baseUrl);
            } else {
                log.warn("data.properties NOT found on classpath. Using default Base URL: {}", baseUrl);
            }
        } catch (Exception e) {
            log.error("Failed to load data.properties: {}", e.getMessage(), e);
        } finally {
            dataLoaded = true;
        }
    }

    @Before(order = 0)
    public void setUp(Scenario scenario) {
        loadDataPropertiesOnce();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        // options.addArguments("--headless=new");  // uncomment for headless CI
        // If you use WebDriverManager, setup here (requires dependency):
        // io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver(options);

        log.info("=== Starting Scenario: {} ===", scenario.getName());
        driver.get(baseUrl);
        log.info("Navigated to: {}", baseUrl);
    }

    @After(order = 1)
    public void captureScreenshotOnFailure(Scenario scenario) {
        if (scenario.isFailed() && driver instanceof TakesScreenshot ts) {
            try {
                byte[] bytes = ts.getScreenshotAs(OutputType.BYTES);

                // Attach to Cucumber report (this also shows up in Extent via adapter)
                scenario.attach(bytes, "image/png", "Failure Screenshot");

                // Save to disk
                String tsStr = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                String safe = scenario.getName().replaceAll("[^a-zA-Z0-9-_]", "_");
                Path out = Paths.get("target", "screenshots", safe + "_" + tsStr + ".png");
                Files.createDirectories(out.getParent());
                Files.write(out, bytes);
                log.error("Failure screenshot saved at: {}", out.toAbsolutePath());
            } catch (Exception e) {
                log.error("Could not capture screenshot: {}", e.getMessage(), e);
            }
        }
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        log.info("=== Finished Scenario: {} | Status: {} ===", scenario.getName(), scenario.getStatus());
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}