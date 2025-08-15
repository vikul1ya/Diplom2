
import io.qameta.allure.Step;
import org.junit.Test;
import ru.practicum.config.Endpoints;
import ru.practicum.model.AuthResponse;
import ru.practicum.model.OrderRequest;
import ru.practicum.model.User;
import ru.practicum.utils.UserGenerator;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderTests extends BaseTest {

    @Step("Получить список id ингредиентов")
    private List<String> getIngredients() {
        List<Map<String, Object>> data = given()
                .when()
                .get(Endpoints.INGREDIENTS)
                .then()
                .statusCode(200)
                .extract()
                .path("data");

        return data.stream()
                .map(m -> (String) m.get("_id"))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Step("Зарегистрировать и получить токен")
    private String registerAndGetToken(User user) {
        AuthResponse resp = given()
                .contentType("application/json")
                .body(user)
                .when()
                .post(Endpoints.AUTH_REGISTER)
                .then()
                .statusCode(200)
                .extract()
                .as(AuthResponse.class);

        String at = resp.getAccessToken();
        if (at == null) {
            throw new IllegalStateException("accessToken is null in register response: " + resp);
        }
        if (!at.toLowerCase().startsWith("bearer ")) {
            at = "Bearer " + at;
        }
        return at;
    }

    @Test
    public void createOrder_withAuth_shouldReturn200() {
        User user = UserGenerator.uniqueUser();
        String token = registerAndGetToken(user);

        List<String> ingredients = getIngredients();
        OrderRequest order = new OrderRequest(ingredients.subList(0, Math.min(2, ingredients.size())));

        given()
                .header("Authorization", token)
                .contentType("application/json")
                .body(order)
                .when()
                .post(Endpoints.ORDERS)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", greaterThan(0));
    }

    @Test
    public void createOrder_withoutAuth_shouldReturn401() {
        List<String> ingredients = getIngredients();
        OrderRequest order = new OrderRequest(ingredients.subList(0, Math.min(2, ingredients.size())));

        given()
                .contentType("application/json")
                .body(order)
                .when()
                .post(Endpoints.ORDERS)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", allOf(notNullValue(), not(isEmptyString())));
    }

    @Test
    public void createOrder_withIngredients_shouldReturn200() {
        User user = UserGenerator.uniqueUser();
        String token = registerAndGetToken(user);

        List<String> ingredients = getIngredients();
        OrderRequest order = new OrderRequest(ingredients.subList(0, Math.min(3, ingredients.size())));

        given()
                .header("Authorization", token)
                .contentType("application/json")
                .body(order)
                .when()
                .post(Endpoints.ORDERS)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    public void createOrder_withoutIngredients_shouldReturn400AndMessage() {
        User user = UserGenerator.uniqueUser();
        String token = registerAndGetToken(user);

        OrderRequest order = new OrderRequest(List.of());

        given()
                .header("Authorization", token)
                .contentType("application/json")
                .body(order)
                .when()
                .post(Endpoints.ORDERS)
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", allOf(notNullValue(), not(isEmptyString())));
    }

    @Test
    public void createOrder_withInvalidIngredientHash_shouldReturn400or500AndMessage() {
        User user = UserGenerator.uniqueUser();
        String token = registerAndGetToken(user);

        // неверный хеш
        OrderRequest order = new OrderRequest(List.of("invalid_ingredient_id_12345"));

        given()
                .header("Authorization", token)
                .contentType("application/json")
                .body(order)
                .when()
                .post(Endpoints.ORDERS)
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(500)))
                .body("success", equalTo(false))
                .body("message", allOf(notNullValue(), not(isEmptyString())));
    }
}
