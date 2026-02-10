# Test Automation Framework

This repository contains a test automation framework for API and UI testing, built with Java, Cucumber (BDD), RestAssured, and Playwright.

The framework is designed focusing on test strategy, maintainability, and clear separation of responsibilities rather than just making tests pass.

---

## Test Scope

### API Testing
The API tests focus on:
- Authentication and authorization
- CRUD operations
- Positive and negative scenarios

### UI Testing
The UI tests cover:
- Critical user flows
- Basic end-to-end scenarios
- Functional correctness of key screens

---

## Architectural Decisions

### Separation of Concerns
The framework separates responsibilities clearly:
- Test logic (Cucumber step definitions)
- Business/API logic (service layer)
- UI interaction (Page Object Model)
- Shared utilities and configuration

### Page Object Model (UI)
Each UI page has its own page object:
- Locators and actions are centralized
- UI changes require minimal updates
- Test steps remain readable and intention-based

### Service Layer (API)
API calls are handled via dedicated service classes:
- HTTP logic is kept out of step definitions
- Configuration is centralized
- API tests remain focused on behavior and validation

---

## BDD and Collaboration

Cucumber is used to express tests in Given–When–Then format.

This supports:
- Readable test scenarios
- Alignment between requirements and test coverage
- The feature files act as living documentation.

---

## Test Execution Strategy

- API tests are executed in parallel where possible, as they are stateless and fast
- UI tests are executed sequentially to keep execution stable
- Smoke and regression tests are separated using tags

---

## Mock API Server Solution

### The Problem
FakeStoreAPI is protected by Cloudflare, which blocks automated requests from GitHub Actions runners. This caused all API tests to fail with 403 errors in CI/CD, even though they work perfectly on local machines.

### The Solution
I implemented a WireMock mock server that mimics the real FakeStoreAPI behavior. The framework automatically uses the mock server in CI/CD environments and the real API locally.

**How does it work?**
- When running locally: tests hit the real API at `https://fakestoreapi.com`
- When running in GitHub Actions: tests hit a local mock server at `http://localhost:8089`
- The switch happens automatically based on the `MOCK_API` environment variable
- No changes to test code were needed

### Running Tests with Mock Server

```bash
# Use mock server (simulates CI/CD environment)
MOCK_API=true mvn test -Dtest=ApiTestRunner

# Use real API (default behavior)
mvn test -Dtest=ApiTestRunner
```

### What This Achieves

**Solves the CI/CD blocker**: Tests now pass in GitHub Actions without 403 errors

**Maintains test quality**: The mock server returns the same responses as the real API, so tests validate the same behavior

**No test changes needed**: The same test code runs in both environments

**Uses industry standards**: WireMock is a widely-used professional tool for API testing

The mock server includes all endpoints used in the test suite (authentication, products, users, and carts) with realistic response data matching the actual FakeStoreAPI schema.

---

## TestRail Integration

### Professional Test Management

This framework integrates with **TestRail** for  test case management and result reporting. Test cases are managed in TestRail through an automated integration that uses the TestRail API, allowing test case management and result reporting directly from the CI/CD pipeline.

**Key Features:**
- Test cases are automatically managed through TestRail API integration
- Test execution results are reported directly from the pipeline
- Provides traceability between test cases and test runs
- Generates test run reports and metrics in TestRail dashboard
- TestRail instance: https://scassignment.testrail.io

### Running Tests with TestRail Reporting

```bash
# Sync test cases to TestRail
mvn exec:java -Dexec.mainClass="com.spritecloud.testrail.TestRailSync"

# Run tests with TestRail reporting enabled
TESTRAIL_ENABLED=true mvn test -Dtest=ApiTestRunner
```

---

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- Git

### Installation

```bash
git clone https://github.com/gamzepala/sc_assignment.git
cd sc_assignment
mvn clean install -DskipTests
```

### Install Playwright Browsers (UI Tests)

```bash
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

---

## Running Tests

```bash
# Run all tests
mvn clean test

# Run smoke tests (3 critical scenarios)
mvn clean test -Psmoke

# Run regression tests (all 18 scenarios)
mvn clean test -Pregression

# Run API tests only
mvn test -Dtest=ApiTestRunner

# Run UI tests only
mvn test -Dtest=UiTestRunner

# Run only negative test scenarios
mvn test -Dgroups="@NegativeTest"

# Run only E2E tests
mvn test -Dgroups="@E2E"

# Exclude smoke tests
mvn test -DexcludedGroups="@Smoke"

# Run tests in debug mode
mvn test -Dlogback.configurationFile=logback-debug.xml
```

---

## Test Reports

Test reports are generated after execution:

```bash
open target/cucumber-reports/api/cucumber.html     # API test report
open target/cucumber-reports/ui/cucumber.html      # UI test report
```

---

## Docker Support

The project includes a `Dockerfile` for containerized test execution:

```bash
# Build the Docker image
docker build -t sc-assignment-tests .

# Run tests inside Docker
docker run --rm sc-assignment-tests mvn clean test
```

---

## AI-Assisted Development

This project leverages **GitHub Copilot** to accelerate development while maintaining code quality. AI assistance was used in:

### What Was Generated with Copilot

**Project Scaffolding:**
- Initial Java project template and folder structure
- Spring-like dependency setup (Maven configuration)

**Configuration Files:**
- `logback.xml` — Logging configuration with multiple appenders
- `junit-platform.properties` — Parallel test execution settings
- Error handling and retry configurations

**Code Generation & Refactoring:**
- Service layer boilerplate (BaseApiService, service class templates)

**Code Review & Analysis:**
- Suggestions for code structure improvements
- Naming convention corrections

---

## CI/CD Integration

Tests are executed via GitHub Actions on push. The pipeline supports:
- Automated test execution on code changes
- Manual workflow triggering
- Test report artifacts
- Separate jobs for API and UI tests
