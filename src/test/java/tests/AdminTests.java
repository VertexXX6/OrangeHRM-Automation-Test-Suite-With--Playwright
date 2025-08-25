package tests;

import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.AddUserPage;
import pages.AdminPage;
import pages.LoginPage;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

public class AdminTests {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private LoginPage loginPage;
    private AdminPage adminPage;
    private String newUsername;
    private String adminUser;
    private String adminPassword;
    private static final Logger log = LoggerFactory.getLogger(AdminTests.class);

    @BeforeTest
    public void setUp() {
        playwright = Playwright.create();
         browser = playwright.chromium().launch(
                 new BrowserType.LaunchOptions().setHeadless(true)
        );

    }

    @BeforeMethod
    public void setUpMethod() throws IOException {
        // Load configuration
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            prop.load(fis);
        }
        String baseUrl = prop.getProperty("baseUrl");
        adminUser = prop.getProperty("adminUser");
        adminPassword = prop.getProperty("adminPassword");
        boolean headless = Boolean.parseBoolean(prop.getProperty("headless"));

        // Initialize browser and page
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
        context = browser.newContext();
        page = context.newPage();

        // Initialize page objects
        loginPage = new LoginPage(page);
        adminPage = new AdminPage(page);

        // Navigate to base URL
        page.navigate(baseUrl);

        // Generate random username
        newUsername = "test.user" + (int)(Math.random() * 10000);
    }
    @Description("Verify Admin can add a new user")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 1)
    public void testAddUser() {
        loginPage.login(adminUser, adminPassword);
        adminPage.clickAdminTab();

        int initialRecords = adminPage.getRecordCount();
        log.info("Initial record count: {}", initialRecords);

        adminPage.clickAddButton();
        AddUserPage addUserPage = new AddUserPage(page);
        addUserPage.fillUserDetails("Admin", newUsername, "Enabled", newUsername, "Test@1234");
        addUserPage.save();

        int newRecords = adminPage.getRecordCount();
        log.info("Record count after adding user: {}", newRecords);

        assertEquals(newRecords, initialRecords + 1, "Record count should increase by 1 after adding a user");
    }
    @Description("Verify Admin can delete an user")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void testDeleteUser() {
        // Setup: Create a user to delete
        loginPage.login(adminUser, adminPassword);
        adminPage.clickAdminTab();
        adminPage.clickAddButton();
        AddUserPage addUserPage = new AddUserPage(page);
        addUserPage.fillUserDetails("Admin", newUsername, "Enabled", newUsername, "Test@1234");
        addUserPage.save();

        // Delete and verify
        int initialRecords = adminPage.getRecordCount();
        int recordsAfterDelete = adminPage.deleteSearchedUserAndGetCount(newUsername);
        log.info("Record count after deletion: {}", recordsAfterDelete);

        assertEquals(recordsAfterDelete, initialRecords - 1, "Record count should decrease by 1 after deleting a user");
    }
    @AfterMethod
    public void tearDownMethod(ITestResult result) {
        captureFailure(result);

        // Attach video to Allure
        try {
            if (context != null && !context.pages().isEmpty()) {
                Path videoPath = context.pages().getFirst().video().path();
                Allure.addAttachment("Video", Files.newInputStream(videoPath));
            }
        } catch (Exception e) {
            log.error("Failed to attach video", e);
        }

        // Attach log
        Allure.addAttachment("Logs", new ByteArrayInputStream(
                ("Test finished with status: " + (result.getStatus() == ITestResult.FAILURE ? "FAIL" : "PASS")).getBytes()));

        if (page != null) page.close();
        if (context != null) context.close();
        if (browser != null) browser.close();
    }

    private void captureFailure(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            try {
                byte[] screenshot = page.screenshot();
                Allure.addAttachment("Failure Screenshot", new ByteArrayInputStream(screenshot));
                log.error("Screenshot captured for failed test: {}", result.getName());
                // Save PNG locally too
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("screenshots/" + result.getName() + ".png")));
            } catch (Exception e) {
                log.error("Failed to capture screenshot", e);
            }
        }
    }

    @AfterTest
    public void tearDown() {
        if (playwright != null) playwright.close();
    }
}