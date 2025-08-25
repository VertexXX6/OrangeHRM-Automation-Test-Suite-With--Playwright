# Admin Tests Automation Framework

This repository contains an automated testing framework for testing admin functionalities using **Playwright** with **TestNG** and **Allure** reporting. The framework is designed to test user management features, including adding and deleting users in a web application.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [Running Tests](#running-tests)
- [GitHub Actions Integration](#github-actions-integration)
- [Allure Reporting](#allure-reporting)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Prerequisites
Before running the tests, ensure you have the following installed:
- **Java**: JDK 11 or higher
- **Maven**: For dependency management
- **Node.js**: Required for Playwright installation
- **Git**: For cloning the repository
- A compatible browser (Chromium, Firefox, or WebKit) installed for Playwright

## Project Structure
```
├── src
│   ├── main
│   │   └── java
│   │       └── utils
│   │           └── PlaywrightFactory.java  # Factory class for Playwright setup
│   ├── test
│   │   ├── java
│   │   │   └── AdminTests.java            # Test cases for admin functionalities
│   │   └── resources
│   │       └── config.properties          # Configuration file for test settings
├── pom.xml                                   # Maven dependencies
├── .github
│   └── workflows
│       └── maven.yml                      # GitHub Actions workflow
└── README.md                                 # This file
```

## Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd <repository-name>
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
     baseUrl=<your-application-url>
     adminUser=<admin-username>
     adminPassword=<admin-password>
     headless=true
     ```
   - Replace `<your-application-url>`, `<admin-username>`, and `<admin-password>` with appropriate values.

## Running Tests
The framework uses **TestNG** to execute tests. Follow these steps to run the tests:

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
   - Screenshots and videos are captured for failed tests and stored in the `screenshots/` directory and Allure reports.

## GitHub Actions Integration
The repository is configured with a **GitHub Actions** workflow to automatically run tests on every push or pull request. The workflow is defined in `.github/workflows/maven.yml`.

### Key Features of the Workflow:
- Runs on `ubuntu-latest`.
- Sets up JDK 11 and Maven.
- Installs Playwright browsers.
- Executes tests using `mvn test`.
- Generates Allure reports and uploads them as artifacts.

To view the workflow status:
1. Go to the **Actions** tab in your GitHub repository.
2. Check the latest workflow run for success/failure details.

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
- **Test Failures**:
  - Review Allure reports for screenshots, videos, and logs.
  - Ensure the application is accessible at the specified `baseUrl`.

## Contributing
Contributions are welcome! To contribute:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m "Add your feature"`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a pull request.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
