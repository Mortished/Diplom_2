import static org.apache.http.HttpStatus.SC_OK;

import generator.AuthClientGenerator;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.CreateOrderForm;

@Story("POST /api/orders - Создание заказа")
public class CreateOrderTest {

    private OrdersClient ordersClient;
    private String token;
    private final String[] INGREDIENTS= {"61c0c5a71d1f82001bdaaa6d","61c0c5a71d1f82001bdaaa6f"};
    private final String BURGER_NAME = "Флюоресцентный бессмертный бургер";

    @Before
    public void setUp() {
        ordersClient = new OrdersClient();
        token = new AuthClient().createUser(new AuthClientGenerator().getRandomCreateUserForm())
            .extract()
            .path("accessToken");
    }

    @Test
    @DisplayName("Создание заказа неавторизованным пользователем - 200 ОК")
    public void createOrderPositive() {
        ValidatableResponse response = ordersClient.createOrder(new CreateOrderForm(INGREDIENTS));
        checkResponse(response);
    }

    @Test
    @DisplayName("Создание заказа авторизованным пользователем - 200 ОК")
    public void createOrderWithAuth() {
        ValidatableResponse response = ordersClient.createOrder(token, new CreateOrderForm(INGREDIENTS));
        checkResponseWithAuth(response);
    }

    @Step("Проверка ответа метода")
    private void checkResponseWithAuth(ValidatableResponse response) {
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(BURGER_NAME, response.extract().path("name"));
    }

    @Step("Проверка ответа метода")
    private void checkResponse(ValidatableResponse response) {
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(BURGER_NAME, response.extract().path("name"));
        Assert.assertNotNull(response.extract().path("order.number"));
    }

}
