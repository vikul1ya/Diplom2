package ru.practicum.config;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.practicum.model.LoginRequest;
import ru.practicum.model.OrderRequest;
import ru.practicum.model.User;
import ru.practicum.model.AuthResponse;

import static io.restassured.RestAssured.given;

public class ApiClient {


    @Step("Регистрация пользователя")
    public AuthResponse register(User user) {
        return given()
                .contentType("application/json")
                .body(user)
                .when()
                .post(Endpoints.AUTH_REGISTER)
                .then()
                .extract().as(AuthResponse.class);
    }


    @Step("Авторизация пользователя")
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

    @Step("Создание заказа")
    public ValidatableResponse createOrder(String token, OrderRequest order) {
        var request = given().contentType("application/json").body(order);
        if (token != null && !token.trim().isEmpty()) {
            request.header("Authorization", token);
        }
        return request.when().post(Endpoints.ORDERS).then();
    }

    @Step("Получение ингредиентов")
    public ValidatableResponse getIngredients() {
        return given()
                .when()
                .get(Endpoints.INGREDIENTS)
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String token) {
        return given()
                .header("Authorization", token)
                .when()
                .delete(Endpoints.AUTH_USER)
                .then();
    }

    @Step("Авторизация и удаление пользователя")
    public ValidatableResponse deleteUserAfterLogin(User user) {
        var response = this.login(user);
        return this.deleteUser(response.getAccessToken());
    }


}