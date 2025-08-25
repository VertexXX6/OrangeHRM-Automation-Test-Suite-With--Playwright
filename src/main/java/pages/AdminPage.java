package pages;

import com.microsoft.playwright.*;

import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AdminPage {
    private final Page page;
    private static final Logger log = LoggerFactory.getLogger(AdminPage.class);
    public AdminPage(Page page) {
        this.page = page;
    }

    private Locator adminTab() {
        return page.locator("a:has-text('Admin')");
    }

    private Locator addButton() {
        return page.locator("button:has-text('Add')");
    }

    private Locator searchInput() {
        return page.locator("//label[contains(text(),'Username')]/parent::div/following-sibling::div//input");
    }

    private Locator searchButton() {
        return page.locator("//button[normalize-space()='Search']");
    }

    private Locator resetButton() {
        return page.locator("//button[normalize-space()='Reset']");
    }

    private Locator recordText() {
        return page.locator("span:has-text('Records Found')");
    }

    private Locator deleteIcon() {
        return page.locator(".oxd-table-body .oxd-table-row .oxd-icon.bi-trash");
    }

    private Locator confirmDeleteButton() {
        return page.locator("button:has-text('Yes, Delete')");
    }

    // ------------------------------
    // Public Actions
    // ------------------------------
    public void clickAdminTab() {
        adminTab().click();
    }

    public void clickAddButton() {
        addButton().click();
    }

    public int getRecordCount() {
        Locator locator = recordText();
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)  // اتأكد إنه ظاهر
                .setTimeout(10000));                      // timeout 10s لو مش ظهر
        String text = locator.textContent().trim();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }
    public int deleteSearchedUserAndGetCount(String username) {
        // 1️⃣ اعملي سيرش على اليوزر
        searchInput().fill(username);
        searchButton().click();

        // 2️⃣ انتظري الـ rows تظهر قبل الحذف
        Locator rows = page.locator(".oxd-table-body .oxd-table-row");
        rows.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

        // 3️⃣ احذفي اليوزر
        deleteIcon().first().click();
        confirmDeleteButton().click();

        // 4️⃣ Reset عشان ترجعي للعرض الكامل
        resetButton().click();

        // 5️⃣ انتظري الـ "Records Found" يظهر بعد الريسيت
        Locator recordLabel = page.locator("span:has-text('Records Found')");
        recordLabel.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

        // 6️⃣ ارجعي عدد الريكورد النهائي
        String finalText = recordLabel.textContent().trim();
        if (finalText.isEmpty()) {
            throw new RuntimeException("Record count text is empty after deletion. Check the locator or wait timing.");
        }

        return Integer.parseInt(finalText.replaceAll("[^0-9]", ""));
    }




}
