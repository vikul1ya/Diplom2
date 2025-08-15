
import io.qameta.allure.Step;
import org.junit.After;
import org.junit.Test;
import ru.practicum.config.Endpoints;
import ru.practicum.model.AuthResponse;
import ru.practicum.model.User;
import ru.practicum.utils.UserGenerator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserTests extends BaseTest {


    private String createdToken = null;

    @Step("Создать уникального пользователя")
    private io.restassured.response.Response createUser(User user) {
        return given()
                .contentType("application/json")
                .body(user)
                .when()
                .post(Endpoints.AUTH_REGISTER);
    }

    @Step("Создать пользователя и вернуть accessToken")
    private String createUserAndReturnToken(User user) {
        AuthResponse resp = createUser(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract()
                .as(AuthResponse.class);

        String token = resp.getAccessToken();
        if (token == null) {
            throw new IllegalStateException("accessToken is null. Response: " + resp);
        }
        if (!token.toLowerCase().startsWith("bearer ")) {
            token = "Bearer " + token;
        }
        // сохраняем для cleanup
        this.createdToken = token;
        return token;
    }

    @Test
    public void createUniqueUser_shouldReturn200AndSuccessTrue() {
        User user = UserGenerator.uniqueUser();

        createUser(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("accessToken", notNullValue());
    }

    @Test
    public void createAlreadyRegisteredUser_shouldReturn403AndErrorMessage() {
        User user = UserGenerator.uniqueUser();

        // создать первый раз
        createUser(user).then().statusCode(200);

        // повторная регистрация — допускаем 403 или 409 и проверяем, что сообщение информативно
        createUser(user)
                .then()
                .statusCode(anyOf(equalTo(403), equalTo(409)))
                .body("success", equalTo(false))
                .body("message", allOf(notNullValue(), not(isEmptyString())))
                .body("message", containsStringIgnoringCase("already"));
    }

    @Test
    public void createUser_missingRequiredField_shouldReturn403Or400WithMessage() {
        // не указываем пароль (null), используем уникальный email
        User user = new User("no-pass-" + System.currentTimeMillis() + "@example.com", null, "NoPass");

        given()
                .contentType("application/json")
                .body(user)
                .when()
                .post(Endpoints.AUTH_REGISTER)
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(403)))
                .body("success", equalTo(false))
                .body("message", allOf(notNullValue(), not(isEmptyString())));
    }

    @After
    public void cleanup() {
        // Опционально: удаляем созданного пользователя, если endpoint поддерживает удаление авторизованного пользователя
        if (this.createdToken != null) {
            given()
                    .header("Authorization", this.createdToken)
                    .when()
                    .delete(Endpoints.AUTH_USER)
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(202), equalTo(204)));
            this.createdToken = null;
        }
    }
}
