import static org.apache.http.HttpStatus.SC_OK;

import generator.AuthClientGenerator;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.CreateUserForm;

@Story("GET /api/auth/user - Получение информации о пользователе")
public class GetUserInfoTest {

    private AuthClient authClient;
    private CreateUserForm loginDetails;
    private String token;

    @Before
    public void setUp() {
        authClient = new AuthClient();
        loginDetails = new AuthClientGenerator().getRandomCreateUserForm();
        ValidatableResponse response = authClient.createUser(loginDetails);
        token = response.extract().path("accessToken");
    }

    @Test
    @DisplayName("Получение информации об авторизованном пользователе - 200 ОК")
    public void getInfoByAuthUser() {
        ValidatableResponse response = authClient.getUserInfo(token);
        checkPositiveResponse(response);
    }

    @Step("Проверка ответа метода")
    private void checkPositiveResponse(ValidatableResponse response) {
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(loginDetails.getEmail().toLowerCase(), response.extract().path("user.email"));
        Assert.assertEquals(loginDetails.getName(), response.extract().path("user.name"));
    }

}
