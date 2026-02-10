package com.spritecloud.services;

import com.spritecloud.config.ConfigurationManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for all API service classes.
 * Provides common REST API functionality using RestAssured.
 *
 * <p>Design Decision: Centralizes API configuration, request/response specifications,
 * and common HTTP status codes to ensure consistency across all API services.
 * All service classes inherit from this base to reuse common patterns and reduce duplication.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Automatic base URL configuration from ConfigurationManager</li>
 *   <li>Reusable request and response specifications</li>
 *   <li>Standard HTTP status code constants</li>
 *   <li>Browser-like request headers for realistic API testing</li>
 *   <li>Comprehensive request/response logging</li>
 *   <li>Configuration validation</li>
 * </ul>
 */
public abstract class BaseApiService {

    protected static final Logger logger = LoggerFactory.getLogger(BaseApiService.class);
    protected final ConfigurationManager config;
    protected final String baseUrl;

    /** Endpoint path for authentication operations */
    protected static final String AUTH_ENDPOINT = "/auth/login";
    /** Endpoint path for product operations */
    protected static final String PRODUCTS_ENDPOINT = "/products";
    /** Endpoint path for user operations */
    protected static final String USERS_ENDPOINT = "/users";
    /** Endpoint path for cart operations */
    protected static final String CARTS_ENDPOINT = "/carts";

    /** HTTP status code for successful requests */
    protected static final int HTTP_OK = 200;
    /** HTTP status code for successful resource creation */
    protected static final int HTTP_CREATED = 201;
    /** HTTP status code for successful requests with no content */
    protected static final int HTTP_NO_CONTENT = 204;
    /** HTTP status code for bad request errors */
    protected static final int HTTP_BAD_REQUEST = 400;
    /** HTTP status code for unauthorized access */
    protected static final int HTTP_UNAUTHORIZED = 401;
    /** HTTP status code for resource not found */
    protected static final int HTTP_NOT_FOUND = 404;
    /** HTTP status code for internal server errors */
    protected static final int HTTP_INTERNAL_ERROR = 500;

    /**
     * Constructs a BaseApiService and configures RestAssured.
     * Loads configuration and sets up the base URL for API requests.
     */
    public BaseApiService() {
        this.config = ConfigurationManager.getInstance();
        this.baseUrl = config.getApiBaseUrl();
        configureRestAssured();
    }

    /**
     * Configures RestAssured with base URI and logging settings.
     * Enables automatic logging when validation fails.
     */
    private void configureRestAssured() {
        RestAssured.baseURI = baseUrl;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        logger.info("RestAssured configured with base URL: {}", baseUrl);
    }

    /**
     * Creates a complete request specification with all standard headers.
     * Includes browser-like headers to mimic real user requests.
     *
     * @return configured RequestSpecification with JSON content type and all headers
     */
    protected RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .addHeader("Accept-Language", "en-US,en;q=0.9")
                .addHeader("sec-ch-ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("sec-fetch-site", "same-origin")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("Referer", baseUrl)
                .addHeader("Origin", baseUrl)
                .log(LogDetail.ALL)
                .build();
    }

    /**
     * Creates a response specification expecting HTTP 200 OK with JSON content.
     *
     * @return ResponseSpecification configured for successful responses
     */
    protected ResponseSpecification getSuccessResponseSpec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HTTP_OK)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    /**
     * Creates a response specification expecting HTTP 201 Created with JSON content.
     *
     * @return ResponseSpecification configured for resource creation responses
     */
    protected ResponseSpecification getCreatedResponseSpec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HTTP_CREATED)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    /**
     * Creates a minimal request specification with only base URI and logging.
     * Used when full headers are not needed.
     *
     * @return minimal RequestSpecification
     */
    protected RequestSpecification getMinimalRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .log(LogDetail.ALL)
                .build();
    }

    /**
     * Logs API request information including HTTP method and endpoint.
     *
     * @param method HTTP method (GET, POST, PUT, DELETE, etc.)
     * @param endpoint the API endpoint being called
     */
    protected void logRequest(String method, String endpoint) {
        logger.info("API Request: {} {}{}", method, baseUrl, endpoint);
    }

    /**
     * Logs API response status code and endpoint.
     *
     * @param statusCode HTTP status code received
     * @param endpoint the API endpoint that responded
     */
    protected void logResponse(int statusCode, String endpoint) {
        logger.info("API Response: {} from {}", statusCode, endpoint);
    }

    /**
     * Validates that the service is properly configured with a base URL.
     *
     * @throws IllegalStateException if base URL is not configured
     */
    protected void validateServiceConfiguration() {
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalStateException("Base URL is not configured");
        }
        logger.debug("Service configuration validated");
    }
}
