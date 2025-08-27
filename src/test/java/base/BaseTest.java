package base;

import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Base class for test setup, teardown, and error handling.
 * Provides common functionality for Playwright initialization, configuration loading,
 * and failure reporting (screenshots, videos, logs).
 */
public abstract class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected String baseUrl;
    protected String adminUser;
    protected String adminPassword;
    protected boolean headless;
    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    /**
     * Loads configuration from config.properties before each test method.
     * Initializes Playwright browser, context, and page.
     * @throws IOException if config file is not found or invalid
     */
    @BeforeMethod
    public void setUpMethod() throws IOException {
        playwright = Playwright.create();
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            prop.load(fis);
        }
        baseUrl = prop.getProperty("baseUrl");
        adminUser = System.getenv("ORANGEHRM_ADMIN_USER") != null ? System.getenv("ORANGEHRM_ADMIN_USER") : prop.getProperty("adminUser");
        adminPassword = System.getenv("ORANGEHRM_ADMIN_PASSWORD") != null ? System.getenv("ORANGEHRM_ADMIN_PASSWORD") : prop.getProperty("adminPassword");
        headless = Boolean.parseBoolean(prop.getProperty("headless"));

        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
        context = browser.newContext(new Browser.NewContextOptions().setRecordVideoDir(Paths.get("videos/")));
        page = context.newPage();
        page.navigate(baseUrl);
    }

    /**
     * Captures screenshots and videos for failed tests and attaches them to Allure reports.
     * Closes page, context, and browser after each test.
     * @param result TestNG test result
     */
    @AfterMethod
    public void tearDownMethod(ITestResult result) {
        try {
            if (ITestResult.FAILURE == result.getStatus()) {
                captureFailure(result);
            }
            if (context != null && !context.pages().isEmpty() && context.pages().getFirst().video() != null) {
                Path videoPath = context.pages().getFirst().video().path();
                Allure.addAttachment("Video", Files.newInputStream(videoPath));
                log.info("Video attached for test: {}", result.getName());
            }
        } catch (Exception e) {
            log.error("Failed to attach video", e);
        } finally {
            if (page != null) page.close();
            if (context != null) context.close();
            if (browser != null) browser.close();
            if (playwright != null) {
                playwright.close();
                playwright = null;
            }
        }

        Allure.addAttachment("Logs", new ByteArrayInputStream(
                ("Test finished with status: " + (result.getStatus() == ITestResult.FAILURE ? "FAIL" : "PASS")).getBytes()));
    }

    /**
     * Captures a screenshot if the test fails and saves it to the screenshots folder.
     * Attaches the screenshot to Allure reports.
     * @param result TestNG test result
     */
    protected void captureFailure(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            try {
                byte[] screenshot = page.screenshot();
                Allure.addAttachment("Failure Screenshot", new ByteArrayInputStream(screenshot));
                log.error("Screenshot captured for failed test: {}", result.getName());
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("screenshots/" + result.getName() + ".png")));
            } catch (Exception e) {
                log.error("Failed to capture screenshot", e);
            }
        }
    }
}