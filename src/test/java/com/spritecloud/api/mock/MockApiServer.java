package com.spritecloud.api.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * WireMock-based mock server for FakeStoreAPI endpoints.
 *
 * This mock server is used in CI/CD environments where Cloudflare blocks
 * automated requests. It provides realistic API responses for all endpoints
 * used in the test suite.
 *
 * Architecture Decision:
 * - Mock server runs on port 8089 to avoid conflicts
 * - Conditionally enabled via MOCK_API environment variable
 * - Provides realistic response data matching FakeStoreAPI schema
 * - Supports all CRUD operations tested in the framework
 */
public class MockApiServer {

    private static final Logger logger = LoggerFactory.getLogger(MockApiServer.class);
    private static WireMockServer wireMockServer;
    private static final int MOCK_PORT = 8089;

    /**
     * Starts the WireMock server and configures all API stubs
     */
    public static void start() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            logger.warn("Mock API server is already running on port {}", MOCK_PORT);
            return;
        }

        wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                .port(MOCK_PORT)
                .disableRequestJournal() // Reduce memory usage
        );

        wireMockServer.start();
        configureStubs();
        logger.info("Mock API server started successfully on port {}", MOCK_PORT);
    }

    /**
     * Stops the WireMock server
     */
    public static void stop() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            logger.info("Mock API server stopped");
        }
    }

    /**
     * Checks if mock server is running
     */
    public static boolean isRunning() {
        return wireMockServer != null && wireMockServer.isRunning();
    }

    /**
     * Gets the base URL of the mock server
     */
    public static String getBaseUrl() {
        return "http://localhost:" + MOCK_PORT;
    }

    /**
     * Configures all API endpoint stubs with realistic responses
     */
    private static void configureStubs() {
        configureAuthStubs();
        configureProductStubs();
        configureUserStubs();
        configureCartStubs();
        logger.info("All API stubs configured successfully");
    }

    /**
     * Configure authentication endpoint stubs
     */
    private static void configureAuthStubs() {
        // POST /auth/login - Successful login
        wireMockServer.stubFor(post(urlEqualTo("/auth/login"))
            .withRequestBody(matchingJsonPath("$.username"))
            .withRequestBody(matchingJsonPath("$.password"))
            .willReturn(aResponse()
                .withStatus(201) // FakeStoreAPI returns 201 for successful login
                .withHeader("Content-Type", "application/json")
                .withBody("{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjEsInVzZXIiOiJqb2huZCIsImlhdCI6MTY0NjkyOTUwMH0.test\"}")));

        // POST /auth/login - Invalid credentials
        wireMockServer.stubFor(post(urlEqualTo("/auth/login"))
            .withRequestBody(equalToJson("{\"username\":\"invalid\",\"password\":\"wrong\"}", true, true))
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\":\"username or password is incorrect\"}")));
    }

    /**
     * Configure product endpoint stubs
     */
    private static void configureProductStubs() {
        // GET /products - Get all products (need at least 6 for test validation)
        wireMockServer.stubFor(get(urlEqualTo("/products"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    [
                        {
                            "id": 1,
                            "title": "Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops",
                            "price": 109.95,
                            "description": "Your perfect pack for everyday use",
                            "category": "men's clothing",
                            "image": "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
                            "rating": {"rate": 3.9, "count": 120}
                        },
                        {
                            "id": 2,
                            "title": "Mens Casual Premium Slim Fit T-Shirts",
                            "price": 22.3,
                            "description": "Slim-fitting style",
                            "category": "men's clothing",
                            "image": "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg",
                            "rating": {"rate": 4.1, "count": 259}
                        },
                        {
                            "id": 3,
                            "title": "Mens Cotton Jacket",
                            "price": 55.99,
                            "description": "Great outerwear jackets",
                            "category": "men's clothing",
                            "image": "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg",
                            "rating": {"rate": 4.7, "count": 500}
                        },
                        {
                            "id": 4,
                            "title": "Mens Casual Slim Fit",
                            "price": 15.99,
                            "description": "The color could be slightly different",
                            "category": "men's clothing",
                            "image": "https://fakestoreapi.com/img/71YXzeOuslL._AC_UY879_.jpg",
                            "rating": {"rate": 2.1, "count": 430}
                        },
                        {
                            "id": 5,
                            "title": "John Hardy Women's Legends Naga Gold & Silver Dragon Station Chain Bracelet",
                            "price": 695,
                            "description": "From our Legends Collection",
                            "category": "jewelery",
                            "image": "https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_.jpg",
                            "rating": {"rate": 4.6, "count": 400}
                        },
                        {
                            "id": 6,
                            "title": "Solid Gold Petite Micropave",
                            "price": 168,
                            "description": "Satisfaction Guaranteed",
                            "category": "jewelery",
                            "image": "https://fakestoreapi.com/img/61sbMiUnoGL._AC_UL640_QL65_ML3_.jpg",
                            "rating": {"rate": 3.9, "count": 70}
                        }
                    ]
                    """)));

        // GET /products/1 - Get single product
        wireMockServer.stubFor(get(urlEqualTo("/products/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "id": 1,
                        "title": "Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops",
                        "price": 109.95,
                        "description": "Your perfect pack for everyday use",
                        "category": "men's clothing",
                        "image": "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
                        "rating": {"rate": 3.9, "count": 120}
                    }
                    """)));

        // GET /products/999999 - Non-existent product (FakeStoreAPI returns null with 200)
        wireMockServer.stubFor(get(urlEqualTo("/products/999999"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("null")));

        // GET /products/categories - Get all categories
        wireMockServer.stubFor(get(urlEqualTo("/products/categories"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    ["electronics","jewelery","men's clothing","women's clothing"]
                    """)));
    }

    /**
     * Configure user endpoint stubs
     */
    private static void configureUserStubs() {
        // GET /users/1 - Get single user
        wireMockServer.stubFor(get(urlEqualTo("/users/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "id": 1,
                        "email": "john@gmail.com",
                        "username": "johnd",
                        "password": "m38rmF$",
                        "name": {"firstname": "john", "lastname": "doe"},
                        "address": {
                            "city": "killeen",
                            "street": "7835 new road",
                            "number": 3,
                            "zipcode": "12926-3874",
                            "geolocation": {"lat": "-37.3159", "long": "81.1496"}
                        },
                        "phone": "1-570-236-7033"
                    }
                    """)));

        // DELETE /users/1 - Delete user
        wireMockServer.stubFor(delete(urlEqualTo("/users/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "id": 1,
                        "email": "john@gmail.com",
                        "username": "johnd",
                        "password": "m38rmF$",
                        "name": {"firstname": "john", "lastname": "doe"},
                        "address": {
                            "city": "killeen",
                            "street": "7835 new road",
                            "number": 3,
                            "zipcode": "12926-3874",
                            "geolocation": {"lat": "-37.3159", "long": "81.1496"}
                        },
                        "phone": "1-570-236-7033"
                    }
                    """)));
    }

    /**
     * Configure cart endpoint stubs
     */
    private static void configureCartStubs() {
        // GET /carts/user/1 - Get user's carts
        wireMockServer.stubFor(get(urlEqualTo("/carts/user/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    [
                        {
                            "id": 1,
                            "userId": 1,
                            "date": "2020-03-02T00:00:00.000Z",
                            "products": [
                                {"productId": 1, "quantity": 4},
                                {"productId": 2, "quantity": 1}
                            ]
                        }
                    ]
                    """)));

        // POST /carts - Accept all cart creation requests (FakeStoreAPI is permissive)
        // Returns 201 even for invalid data as FakeStoreAPI doesn't validate strictly
        wireMockServer.stubFor(post(urlEqualTo("/carts"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "id": 21,
                        "userId": 1,
                        "date": "2020-02-03T00:00:00.000Z",
                        "products": [{"productId": 1, "quantity": 2}]
                    }
                    """)));
    }
}
