
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.config.ApiClient;
import ru.practicum.model.AuthResponse;
import ru.practicum.model.OrderRequest;
import ru.practicum.model.User;
import ru.practicum.utils.UserGenerator;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class OrderTests extends BaseTest {
    private ApiClient client;
    private String authToken;
    private List<String> ingredientIds;

    @Before
    public void setUp() {
        client = new ApiClient();
        User user = UserGenerator.generateUniqueUser();
        var auth = registerUser(user);
        authToken = auth.getAccessToken();

        ingredientIds = getIngredientIds();
    }

    @Test
    @Description("Проверяет, что авторизованный пользователь может создать заказ. Ожидается статус 200")
    public void createOrderWithAuth() {
        OrderRequest order = createOrderRequest(2);
        sendOrderWithAuth(order)
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", greaterThan(0));
    }

    @Test
    @Description("Проверяет, что неавторизованный пользователь не может создать заказ. Ожидается 401")
    public void createOrderWithoutAuth() {
        OrderRequest order = createOrderRequest(2);
        sendOrderWithoutAuth(order)
                .statusCode(401)
                .body("success", equalTo(false));
    }

    @Test
    @Description("Проверяет, что создание заказа без ингредиентов возвращает 400")
    public void createOrderWithoutIngredients() {
        OrderRequest order = new OrderRequest(java.util.List.of());
        sendOrderWithAuth(order)
                .statusCode(400)
                .body("success", equalTo(false));
    }

    @Test
    @Description("Проверяет, что заказ с несуществующим хешем ингредиента возвращает 500")
    public void createOrderWithInvalidIngredient() {
        OrderRequest order = new OrderRequest(java.util.List.of("invalid_hash_123"));
        sendOrderWithAuth(order)
                .statusCode(500);
    }

    // Шаги

    @Step("Регистрация пользователя")
    private AuthResponse registerUser(User user) {
        return client.register(user);
    }

    @Step("Получение списка ID ингредиентов")
    private List<String> getIngredientIds() {
        return client.getIngredients()
                .statusCode(200)
                .extract()
                .path("data._id");
    }

    @Step("Создание запроса на заказ: {count} ингредиента(ов)")
    private OrderRequest createOrderRequest(int count) {
        int size = Math.min(count, ingredientIds.size());
        return new OrderRequest(ingredientIds.subList(0, size));
    }

    @Step("Отправка заказа с авторизацией")
    private io.restassured.response.ValidatableResponse sendOrderWithAuth(OrderRequest order) {
        return client.createOrder(authToken, order);
    }

    @Step("Отправка заказа без авторизации")
    private io.restassured.response.ValidatableResponse sendOrderWithoutAuth(OrderRequest order) {
        return client.createOrder(null, order);
    }
}
