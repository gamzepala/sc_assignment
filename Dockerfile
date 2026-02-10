# This Dockerfile creates a containerized environment for running API and UI tests
# with all necessary dependencies (Java 17, Maven, Playwright browsers)

# Use official Maven image with Java 17
FROM maven:3.9-eclipse-temurin-17

# Set working directory
WORKDIR /app

# Install Playwright system dependencies for headless browser testing
# These are required for Chromium to run in the container
RUN apt-get update && apt-get install -y \
    libnss3 \
    libnspr4 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libcups2 \
    libdrm2 \
    libxkbcommon0 \
    libxcomposite1 \
    libxdamage1 \
    libxfixes3 \
    libxrandr2 \
    libgbm1 \
    libpango-1.0-0 \
    libcairo2 \
    libasound2t64 \
    libatspi2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Copy pom.xml first for better Docker layer caching
# Dependencies only re-download if pom.xml changes
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy project source code
COPY src ./src

# Copy environment configuration
COPY .env.example .env

# Install Playwright browsers
# This downloads Chromium for UI tests (dependencies already installed above)
RUN mvn exec:java -e \
    -Dexec.mainClass=com.microsoft.playwright.CLI \
    -Dexec.args="install chromium"

# Create directories for test outputs
RUN mkdir -p target/cucumber-reports target/surefire-reports logs

# Default command: Run all tests
CMD ["mvn", "clean", "test"]

# Usage examples:
# Build:     docker build -t sc-assignment:latest .
# Run all:   docker run --rm sc-assignment:latest
# API only:  docker run --rm sc-assignment:latest mvn test -Dtest=ApiTestRunner
# UI only:   docker run --rm sc-assignment:latest mvn test -Dtest=UiTestRunner
# With logs: docker run --rm -v $(pwd)/target:/app/target sc-assignment:latest
