
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.BeforeClass;
import ru.practicum.config.Endpoints;

import static io.restassured.RestAssured.*;

public class BaseTest {

    @BeforeClass
    public static void setup() {
        baseURI = Endpoints.BASE;
        filters(new AllureRestAssured());
    }
}