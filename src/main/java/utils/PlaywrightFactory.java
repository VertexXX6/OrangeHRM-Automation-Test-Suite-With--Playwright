package utils;

import com.microsoft.playwright.*;

/**
 * PlaywrightFactory: Factory class to create and manage Playwright Browser and Pages
 * This version is more professional and flexible.
 */
public class PlaywrightFactory {

    // Static variables to keep single instance of Playwright and Browser
    private static Playwright pw;
    private static Browser browser;

    /**
     * Create a new Page.
     * Default: Chromium browser, headed mode (setHeadless = false)
     * @return Page object for test automation
     */
    public static Page createPage() {
        // Initialize Playwright if not already initialized
        if (pw == null) {
            pw = Playwright.create();
        }

        // Launch browser if not already launched
        if (browser == null) {
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
            options.setHeadless(false); // Run in headed mode (see browser)
            browser = pw.chromium().launch(options);
        }

        // Create and return a new page
        return browser.newPage();
    }

    /**
     * Close the browser and Playwright instance
     */
    public static void close() {
        if (browser != null) {
            browser.close(); // Close the browser
            browser = null; // Reset browser to allow re-creation later
        }

        if (pw != null) {
            pw.close(); // Close Playwright session
            pw = null; // Reset Playwright instance
        }
    }
}
