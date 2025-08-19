

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.BeforeClass;

import static io.restassured.RestAssured.filters;

public class BaseTest {

    @BeforeClass
    public static void setUpBase() {
        RestAssured.baseURI = ru.practicum.config.Endpoints.BASE;
        filters(new AllureRestAssured());
    }
}