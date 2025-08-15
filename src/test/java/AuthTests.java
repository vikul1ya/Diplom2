
import io.qameta.allure.Step;
import org.junit.Test;
import ru.practicum.config.Endpoints;
import ru.practicum.model.User;
import ru.practicum.utils.UserGenerator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthTests extends BaseTest {

    @Step("Регистрация пользователя (для теста логина)")
    private void register(User user) {
        given()
                .contentType("application/json")
                .body(user)
                .when()
                .post(Endpoints.AUTH_REGISTER)
                .then()
                .statusCode(200);
    }

    @Step("Выполнить логин")
    private io.restassured.response.Response login(User user) {
        return given()
                .contentType("application/json")
                .body(new LoginRequest(user.getEmail(), user.getPassword()))
                .when()
                .post(Endpoints.AUTH_LOGIN);
    }

    @Test
    public void loginWithValidCredentials_shouldReturn200AndToken() {
        User user = UserGenerator.uniqueUser();
        register(user);

        login(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
    }

    @Test
    public void loginWithInvalidCredentials_shouldReturn401AndMessage() {
        LoginRequest bad = new LoginRequest("invalid@example.com", "wrongpass");

        given()
                .contentType("application/json")
                .body(bad)
                .when()
                .post(Endpoints.AUTH_LOGIN)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", allOf(notNullValue(), not(isEmptyString())));
    }

    // Вспомогательный класс для тела логина
    static class LoginRequest {
        private final String email;
        private final String password;
        LoginRequest(String email, String password) { this.email = email; this.password = password; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }
}

