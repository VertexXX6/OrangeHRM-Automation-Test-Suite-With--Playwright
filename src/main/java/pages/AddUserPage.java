package pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;

public class AddUserPage {
    private final Page page;

    // Constructor
    public AddUserPage(Page page) {
        this.page = page;
    }

    private final String userRoleDropdown = "//div[contains(@class, 'oxd-input-group') and .//label[contains(text(), 'User Role')]]//div[contains(@class, 'oxd-select-text') and contains(@class, 'oxd-select-text--active')]";
    private final String employeeNameInput = "//div[contains(@class, 'oxd-input-group') and .//label[contains(text(), 'Employee Name')]]//input[@placeholder='Type for hints...']";
    private final String statusDropdown = "//div[contains(@class, 'oxd-input-group') and .//label[contains(text(), 'Status')]]//div[contains(@class, 'oxd-select-text') and contains(@class, 'oxd-select-text--active')]";
    private final String passwordInput = "//div[contains(@class, 'oxd-input-group') and .//label[text()='Password']]//input[@type='password']";
    private final String confirmPasswordInput = "//div[contains(@class, 'oxd-input-group') and .//label[text()='Confirm Password']]//input[@type='password']";
    private final String saveButton = "//button[@type='submit' and contains(@class, 'oxd-button--secondary') and contains(., 'Save')]";

    // Method to click the Admin menu
    public void clickAdminMenu() {
        // Locators using the provided XPath with corrections
        String adminMenu = "//a[contains(@class, 'oxd-main-menu-item') and contains(., 'Admin')]";
        page.locator(adminMenu).click();
        // Wait for the page to load
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    // Method to click the Add button
    public void clickAddButton() {
        String addButton = "//button[contains(@class, 'oxd-button--secondary') and contains(., 'Add')]";
        page.locator(addButton).click();
        // Wait for the form to load
        page.locator(userRoleDropdown).waitFor(new Locator.WaitForOptions().setTimeout(10000));
    }

    // Method to fill user details
    public void fillUserDetails(String userRole, String employeeName, String status, String username, String password) {
        // Select User Role
        page.locator(userRoleDropdown).click();
        page.locator(String.format("//div[@role='listbox']//span[contains(text(), '%s')]", userRole)).click();

        // Fill Employee Name

        // Type to trigger suggestions
        page.locator(employeeNameInput).click();
        page.locator(employeeNameInput).fill("a");

        // Wait and get actual suggestion
        Locator suggestionList = page.locator("//div[@role='listbox']//span[not(contains(text(), 'Searching'))]");
        suggestionList.first().waitFor(new Locator.WaitForOptions().setTimeout(10000));
        employeeName = suggestionList.first().textContent().trim();
        System.out.println("Selected employee: " + employeeName);
        suggestionList.first().click();



        // Select Status
        page.locator(statusDropdown).click();
        page.locator(String.format("//div[@role='listbox']//span[contains(text(), '%s')]", status)).click();

        // Fill Username
        String usernameInput = "//div[contains(@class, 'oxd-input-group') and .//label[contains(text(), 'Username')]]//input[contains(@class, 'oxd-input')]";
        page.locator(usernameInput).fill(username);

        // Fill Password
        page.locator(passwordInput).fill(password);

        // Fill Confirm Password
        page.locator(confirmPasswordInput).fill(password);
    }

    // Method to click the Save button
    public void save() {
        page.locator(saveButton).click();
        // Optional: Wait for success message
        page.locator("text=Successfully Saved").waitFor(new Locator.WaitForOptions().setTimeout(10000));
    }
}