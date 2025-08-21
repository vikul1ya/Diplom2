
import io.qameta.allure.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.config.ApiClient;
import ru.practicum.config.ErrorMessages;
import ru.practicum.model.User;
import ru.practicum.utils.UserGenerator;

import static org.junit.Assert.*;

public class AuthTests extends BaseTest {
    private ApiClient client;
    private User user;

    @Before
    public void setUp() {
        client = new ApiClient();
        user = UserGenerator.generateUniqueUser();
        client.register(user);
    }

    @After
    public void tearDown() {
        var response = client.login(user);
        String token = response.getAccessToken();
        client.deleteUser(token);
    }

    @Test
    @Description("Проверяет успешный вход с корректными учётными данными")
    public void loginWithValidCredentials() {
        var response = client.login(user);
        assertTrue(response.getSuccess());
        assertNotNull(response.getAccessToken());
    }

    @Test
    @Description("Проверяет, что вход с неверным паролем возвращает ошибку ")
    public void loginWithInvalidPassword() {
        User badUser = new User(user.getEmail(), "wrongpass", user.getName());
        var response = client.login(badUser);
        assertFalse("Вход с неверным паролем должен завершиться ошибкой", response.getSuccess());
        assertNotNull("Должно быть сообщение об ошибке", response.getMessage());
        assertEquals("Сообщение об ошибке не совпадает",
                ErrorMessages.INCORRECT_CREDENTIALS, response.getMessage());
    }

    @Test
    @Description("Проверяет, что вход с несуществующим email возвращает ошибку")
    public void loginWithInvalidEmail() {
        User badUser = new User("invalid@example.com", user.getPassword(), user.getName());
        var response = client.login(badUser);
        assertFalse("Вход с несуществующим email должен завершиться ошибкой", response.getSuccess());
        assertNotNull("Должно быть сообщение об ошибке", response.getMessage());
        assertEquals("Сообщение об ошибке не совпадает",
                ErrorMessages.INCORRECT_CREDENTIALS, response.getMessage());
    }
}

