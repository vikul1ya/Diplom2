package ru.practicum.config;

import io.restassured.response.ValidatableResponse;
import ru.practicum.model.OrderRequest;
import ru.practicum.model.User;
import ru.practicum.model.AuthResponse;

import static io.restassured.RestAssured.given;

public class ApiClient {

    public AuthResponse register(User user) {
        return given()
                .contentType("application/json")
                .body(user)
                .when()
                .post(Endpoints.AUTH_REGISTER)
                .then()
                .extract().as(AuthResponse.class);
    }

    public AuthResponse login(User user) {
        LoginRequest login = new LoginRequest(user.getEmail(), user.getPassword());
        return given()
                .contentType("application/json")
                .body(login)
                .when()
                .post(Endpoints.AUTH_LOGIN)
                .then()
                .extract().as(AuthResponse.class);
    }

    public ValidatableResponse createOrder(String token, OrderRequest order) {
        var request = given().contentType("application/json").body(order);
        if (token != null && !token.trim().isEmpty()) {
            request.header("Authorization", token);
        }
        return request.when().post(Endpoints.ORDERS).then();
    }

    public ValidatableResponse getIngredients() {
        return given()
                .when()
                .get(Endpoints.INGREDIENTS)
                .then();
    }

    public ValidatableResponse deleteUser(String token) {
        return given()
                .header("Authorization", token)
                .when()
                .delete(Endpoints.AUTH_USER)
                .then();
    }

    private static class LoginRequest {
        private final String email;
        private final String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }
}