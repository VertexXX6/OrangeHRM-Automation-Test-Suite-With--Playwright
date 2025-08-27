package tests;

import base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.AddUserPage;
import pages.AdminPage;
import pages.LoginPage;

import static org.testng.Assert.assertEquals;

/**
 * Test class for automating admin functionalities in OrangeHRM.
 * Extends BaseTest to inherit setup, teardown, and error handling.
 */
public class AdminTests extends BaseTest {
    private LoginPage loginPage;
    private AdminPage adminPage;
    private String newUsername;
    private static final Logger log = LoggerFactory.getLogger(AdminTests.class);

    /**
     * Initializes page objects and generates a random username before each test.
     */
    @BeforeMethod
    public void setUpTest() {
        loginPage = new LoginPage(page);
        adminPage = new AdminPage(page);
        newUsername = "test.user" + (int)(Math.random() * 10000);
    }

    /**
     * Tests the ability of an admin to add a new user.
     * Verifies the record count increases by 1 after adding a user.
     */
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

    /**
     * Tests the ability of an admin to delete a user.
     * Verifies the record count decreases by 1 after deletion.
     */
    @Description("Verify Admin can delete an user")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void testDeleteUser() {
        loginPage.login(adminUser, adminPassword);
        adminPage.clickAdminTab();
        adminPage.clickAddButton();
        AddUserPage addUserPage = new AddUserPage(page);
        addUserPage.fillUserDetails("Admin", newUsername, "Enabled", newUsername, "Test@1234");
        addUserPage.save();

        int initialRecords = adminPage.getRecordCount();
        int recordsAfterDelete = adminPage.deleteSearchedUserAndGetCount(newUsername);
        log.info("Record count after deletion: {}", recordsAfterDelete);

        assertEquals(recordsAfterDelete, initialRecords - 1, "Record count should decrease by 1 after deleting a user");
    }
}