# Admin Tests Automation Framework
![Playwright](https://img.shields.io/badge/Playwright-2E2E5E?style=for-the-badge&logo=playwright&logoColor=white)
![CI/CD](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)

This repository contains an automated testing framework for testing admin functionalities in [OrangeHRM](https://www.orangehrm.com/) using **Playwright**, **TestNG**, and **Allure** reporting. The framework automates critical user management features (e.g., adding and deleting users) using the Page Object Model (POM) for maintainable, scalable code. It’s designed to help QA engineers validate OrangeHRM’s admin module with detailed reporting and CI/CD integration.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [Running Tests](#running-tests)
- [Test Cases](#test-cases)
- [GitHub Actions Integration](#github-actions-integration)
- [Allure Reporting](#allure-reporting)
- [Troubleshooting](#troubleshooting)

## Prerequisites
Before running the tests, ensure you have the following installed:
- **Java**: JDK 11 or higher
- **Maven**: For dependency management
- **Node.js**: Required for Playwright installation
- **Git**: For cloning the repository
- A compatible browser (Chromium, Firefox, or WebKit) installed for Playwright
- An OrangeHRM instance (e.g., [demo site](https://opensource-demo.orangehrmlive.com/))

## Project Structure
```
├── src
│   ├── main
│   │   └── java
│   │       ├── utils
│   │       │   └── PlaywrightFactory.java  # Factory class for Playwright setup
│   │       └── pages
│   │           ├── LoginPage.java         # Page Object for login page
│   │           ├── AdminPage.java         # Page Object for admin dashboard
│   │           ├── AddUserPage.java       # Page Object for adding users
│   ├── test
│   │   ├── java
│   │   │   ├── base
│   │   │   │   └── BaseTest.java         # Base class for test setup and error handling
│   │   │   └── tests
│   │   │       └── AdminTests.java       # Test cases for admin functionalities
│   │   └── resources
│   │       └── config.properties         # Configuration file for test settings
├── pom.xml                                   # Maven dependencies
├── .github
│   └── workflows
│       └── maven.yml                     # GitHub Actions workflow
├── .gitignore                               # Ignored files (e.g., screenshots, allure-results)
├── docs                                     # Documentation (e.g., screenshots)
└── README.md                                # This file
```

- Generated files like `screenshots/`, `allure-results/`, and `videos/` are excluded from Git (see `.gitignore`) and uploaded as artifacts in GitHub Actions.

## Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/VertexXX6/OrangeHRM-Automation-Test-Suite-With-Playwright.git
   cd OrangeHRM-Automation-Test-Suite-With-Playwright
   ```

2. **Install Dependencies**:
   - Ensure Maven is installed, then run:
     ```bash
     mvn clean install
     ```
   - Install Playwright browsers:
     ```bash
     mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
     ```

3. **Configure Test Settings**:
   - Create a `config.properties` file in `src/test/resources/` with the following content:
     ```properties
     baseUrl=https://opensource-demo.orangehrmlive.com/
     adminUser=admin
     adminPassword=admin123
     headless=true
     ```
   - **Security Note**: Do not commit `config.properties` with sensitive data. Use environment variables for `adminUser` and `adminPassword`:
     ```bash
     export ORANGEHRM_ADMIN_USER=admin
     export ORANGEHRM_ADMIN_PASSWORD=admin123
     ```

## Running Tests
The framework uses **TestNG** and a `BaseTest` class to centralize Playwright setup, teardown, and error handling (e.g., screenshots, videos for failed tests).

1. **Run All Tests**:
   ```bash
   mvn test
   ```

2. **Run Specific Test Class**:
   To run only the `AdminTests` class:
   ```bash
   mvn test -Dtest=AdminTests
   ```

3. **Run in Headed Mode**:
   By default, tests run in headless mode. To run in headed mode (browser visible), update `config.properties` to set `headless=false`, or pass it as a system property:
   ```bash
   mvn test -Dheadless=false
   ```

4. **Test Output**:
   - Test logs are generated using SLF4J and can be viewed in the console.
   - Screenshots and videos for failed tests are saved in `screenshots/` and `videos/`, respectively, and attached to Allure reports.

## Test Cases
- **Add User**: Verifies that an admin can add a new user, checking the record count increases by 1.
- **Delete User**: Verifies that an admin can delete a user, ensuring the record count decreases by 1.

## GitHub Actions Integration
The repository is configured with a **GitHub Actions** workflow to automatically run tests on every push or pull request. The workflow is defined in `.github/workflows/maven.yml`.

### Key Features of the Workflow:
- Runs on `ubuntu-latest`.
- Sets up JDK 11 and Maven (fixed to resolve compilation errors with Java 23).
- Installs Playwright browsers.
- Executes tests using `mvn test`.
- Generates and uploads Allure reports and screenshots as artifacts.

To view the workflow status:
1. Go to the **Actions** tab in your GitHub repository.
2. Check the latest workflow run for success/failure details.

## Accessing Test Artifacts
- **Screenshots**: Uploaded as artifacts for failed tests (see the "Artifacts" section in the Actions tab).
- **Allure Reports**: Generated and uploaded after each run, accessible in the Actions tab.

## Allure Reporting
The framework integrates **Allure** for detailed test reporting.

1. **Generate Allure Report**:
   After running tests, generate the report:
   ```bash
   mvn allure:report
   ```

2. **View Allure Report**:
   Open the generated report:
   ```bash
   mvn allure:serve
   ```
   This starts a local server and opens the report in your default browser. Reports include:
   - Test case descriptions and severity.
   - Screenshots and videos for failed tests.
   - Test execution logs.


## Troubleshooting
- **Playwright Browser Issues**:
  - Ensure browsers are installed (`mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"`).
  - Verify Node.js is installed.
- **Configuration Errors**:
  - Check `config.properties` for correct `baseUrl`, `adminUser`, and `adminPassword`.
  - Ensure environment variables
