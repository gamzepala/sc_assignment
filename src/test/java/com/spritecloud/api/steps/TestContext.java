package com.spritecloud.api.steps;

import com.spritecloud.models.api.AuthToken;
import com.spritecloud.models.api.Cart;
import com.spritecloud.models.api.Product;
import com.spritecloud.models.api.User;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

/**
 * Test Context for sharing state between Cucumber step definitions.
 * Uses ThreadLocal to ensure thread safety in parallel execution.
 *
 */
public class TestContext {

    private Response response;
    private Product product;
    private List<Product> productList;
    private User user;
    private List<User> userList;
    private Cart cart;
    private List<Cart> cartList;
    private AuthToken authToken;
    private Integer currentProductId;
    private Integer currentUserId;
    private Integer currentCartId;
    private Map<String, Object> invalidData;

    // Getters and Setters
    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public List<Cart> getCartList() {
        return cartList;
    }

    public void setCartList(List<Cart> cartList) {
        this.cartList = cartList;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public Integer getCurrentProductId() {
        return currentProductId;
    }

    public void setCurrentProductId(Integer currentProductId) {
        this.currentProductId = currentProductId;
    }

    public Integer getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Integer currentUserId) {
        this.currentUserId = currentUserId;
    }

    public Integer getCurrentCartId() {
        return currentCartId;
    }

    public void setCurrentCartId(Integer currentCartId) {
        this.currentCartId = currentCartId;
    }

    public Map<String, Object> getInvalidData() {
        return invalidData;
    }

    public void setInvalidData(Map<String, Object> invalidData) {
        this.invalidData = invalidData;
    }

    /**
     * Resets context for new test scenario
     */
    public void reset() {
        response = null;
        product = null;
        productList = null;
        user = null;
        userList = null;
        cart = null;
        cartList = null;
        authToken = null;
        currentProductId = null;
        currentUserId = null;
        currentCartId = null;
        invalidData = null;
    }
}
