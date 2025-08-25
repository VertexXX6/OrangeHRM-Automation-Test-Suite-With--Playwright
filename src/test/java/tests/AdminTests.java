package tests;

import com.microsoft.playwright.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.AddUserPage;
import pages.AdminPage;
import pages.LoginPage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

public class AdminTests {
    private Playwright playwright;
    private Browser browser;
    private Page page;
    private LoginPage loginPage;
    private AdminPage adminPage;
    private String newUsername;
    private String adminUser;
    private String adminPassword;
    private String baseUrl;
    private static final Logger log = LoggerFactory.getLogger(AdminTests.class);

    @BeforeTest
    public void setUp() throws IOException {
        // -------------------------------
        // 1️⃣ Load configuration
        // -------------------------------
        Properties prop = new Properties();
        prop.load(new FileInputStream("src/test/resources/config.properties"));

        baseUrl = prop.getProperty("baseUrl");
        adminUser = prop.getProperty("adminUser");
        adminPassword = prop.getProperty("adminPassword");
        boolean headless = Boolean.parseBoolean(prop.getProperty("headless"));

        // -------------------------------
        // 2️⃣ Setup Playwright
        // -------------------------------
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
        page = browser.newPage();

        // -------------------------------
        // 3️⃣ Initialize Page Objects
        // -------------------------------
        loginPage = new LoginPage(page);
        adminPage = new AdminPage(page);

        // -------------------------------
        // 4️⃣ Navigate to base URL
        // -------------------------------
        page.navigate(baseUrl);

        // Generate a random username for testing
        newUsername = "test.user" + (int)(Math.random() * 10000);
    }

    @Test(priority = 1)
    public void testAddUser() {
        loginPage.login(adminUser, adminPassword);
        adminPage.clickAdminTab();

        int initialRecords = adminPage.getRecordCount();
        log.info("Initial record count: " + initialRecords);

        adminPage.clickAddButton();
        AddUserPage addUserPage = new AddUserPage(page);
        addUserPage.fillUserDetails("Admin", newUsername, "Enabled", newUsername, "Test@1234");
        addUserPage.save();

        int newRecords = adminPage.getRecordCount();
        log.info("Record count after adding user: " + newRecords);

        assertEquals(newRecords, initialRecords + 1, "Record count should increase by 1 after adding a user");
    }

    @Test(priority = 2)
    public void testDeleteUser() {
        int recordsAfterDelete = adminPage.deleteSearchedUserAndGetCount(newUsername);
        log.info("Record count after deletion: " + recordsAfterDelete);

    }


    @AfterTest
    public void tearDown() {
        browser.close();
        playwright.close();
    }
}
