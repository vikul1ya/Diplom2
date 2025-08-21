
import io.qameta.allure.Description;
import org.junit.Test;
import ru.practicum.config.ApiClient;
import ru.practicum.config.ErrorMessages;
import ru.practicum.model.User;
import ru.practicum.utils.UserGenerator;

import static org.junit.Assert.*;

public class UserTests extends BaseTest {
    private final ApiClient client = new ApiClient();

    @Test
    @Description("Проверяет успешную регистрацию нового пользователя. Ожидается статус 200")
    public void registerNewUser() {
        User user = UserGenerator.generateUniqueUser();

        var response = client.register(user);

        assertTrue("Ожидался success = true", response.getSuccess());
        assertNotNull("AccessToken должен быть в ответе", response.getAccessToken());
        assertEquals("Email в ответе должен совпадать с отправленным",
                user.getEmail(), response.getUser().getEmail());
        assertEquals("Имя в ответе должно совпадать",
                user.getName(), response.getUser().getName());

        // Удаляем пользователя
        client.deleteUserAfterLogin(user);
    }

    @Test
    @Description("Проверяет, что повторная регистрация существующего пользователя возвращает ошибку 403 и соответствующее сообщение.")
    public void registerExistingUser() {
        User user = UserGenerator.generateUniqueUser();

        // Регистрируем первый раз — должно пройти
        var firstResponse = client.register(user);
        assertTrue("Первая регистрация должна пройти успешно", firstResponse.getSuccess());

        // Повторная регистрация — должна завершиться ошибкой
        var secondResponse = client.register(user);

        assertFalse("Повторная регистрация должна вернуть success = false", secondResponse.getSuccess());
        assertNotNull("Должно быть сообщение об ошибке", secondResponse.getMessage());
        assertEquals("Сообщение об ошибке должно быть точным",
                ErrorMessages.USER_ALREADY_EXISTS, secondResponse.getMessage());
    }

    @Test
    @Description("Проверяет, что регистрация без email завершается ошибкой 403 с корректным сообщением.")
    public void registerUserWithoutEmail() {
        User user = new User(null, "P@ssw0rd123", "TestUser");

        var response = client.register(user);

        assertFalse("Регистрация без email должна завершиться ошибкой", response.getSuccess());
        assertNotNull("Должно быть сообщение об ошибке", response.getMessage());
        assertEquals("Сообщение об ошибке не совпадает",
                ErrorMessages.REQUIRED_FIELDS_MASSAGE, response.getMessage());
    }

    @Test
    @Description("Проверяет, что регистрация без пароля завершается ошибкой 403 с корректным сообщением.")
    public void registerUserWithoutPassword() {
        User user = new User("testuser@example.com", null, "TestUser");

        var response = client.register(user);

        assertFalse("Регистрация без пароля должна завершиться ошибкой", response.getSuccess());
        assertNotNull("Должно быть сообщение об ошибке", response.getMessage());
        assertEquals("Сообщение об ошибке не совпадает",
                ErrorMessages.REQUIRED_FIELDS_MASSAGE, response.getMessage());
    }

    @Test
    @Description("Проверяет, что регистрация без имени завершается ошибкой 403 с корректным сообщением.")
    public void registerUserWithoutName() {
        User user = new User("testuser@example.com", "P@ssw0rd123", null);

        var response = client.register(user);

        assertFalse("Регистрация без имени должна завершиться ошибкой", response.getSuccess());
        assertNotNull("Должно быть сообщение об ошибке", response.getMessage());
        assertEquals("Сообщение об ошибке не совпадает",
                ErrorMessages.REQUIRED_FIELDS_MASSAGE, response.getMessage());
    }
}