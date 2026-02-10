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

## Known Limitations & Considerations

The API used in this assignment is protected by Cloudflare, which may block automated requests in CI environments and return HTTP 403 responses.

In real projects, this would typically be addressed by:
- Using controlled test environments
- API mocking
- Self-hosted runners

For this assignment, local execution is considered the reliable reference.

---

## Project Structure
```
.
├── .github
│   └── workflows
│       └── test-automation.yml
│
├── src
│   ├── main
│   │   └── java
│   │       └── com
│   │           └── spritecloud
│   │               ├── config
│   │               ├── models
│   │               │   ├── api
│   │               │   └── ui
│   │               ├── pages
│   │               ├── services
│   │               └── utils
│   │
│   └── test
│       ├── java
│       │   └── com
│       │       └── spritecloud
│       │           ├── api
│       │           │   ├── hooks
│       │           │   ├── runners
│       │           │   └── steps
│       │           ├── ui
│       │           │   ├── hooks
│       │           │   ├── runners
│       │           │   └── steps
│       │           └── runners
│       │
│       └── resources
│           ├── config
│           └── features
│               ├── api
│               └── ui
│
├── wiki
│   ├── Architecture.md
│   ├── Installation.md
│   ├── Testing.md
│   └── Usage.md
│
├── Dockerfile
├── pom.xml
└── README.md
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
