package utils;

import com.microsoft.playwright.*;
public class PlaywrightFactory {
    static Playwright pw;
    static Browser browser;
    public static Page createPage() {
        pw = Playwright.create();
        browser = pw.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        return browser.newPage();
    }
    public static void close() {
        browser.close();
        pw.close();
    }
}
