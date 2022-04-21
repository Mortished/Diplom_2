import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import generator.AuthClientGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.CreateUserForm;

@Story("POST /api/auth/login - Авторизация пользователя")
public class LoginTest {
    private AuthClient authClient;
    private CreateUserForm createUserForm;
    private String token;

    @Before
    public void setUp() {
        authClient = new AuthClient();
        createUserForm = new AuthClientGenerator().getRandomCreateUserForm();
        authClient.createUser(createUserForm);
    }

    @After
    public void cleanUp() {
        if (token != null) {
            authClient.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Логин с актуальными кредами - 200 ОК")
    @Description("Проверка логина УЗ с актуальными кредами")
    public void loginWithCorrectFields() {
        ValidatableResponse response = authClient.loginUser(new AuthClientGenerator().getLoginFormBy(createUserForm));
        checkPositiveResponse(response);
    }

    @Test
    @DisplayName("Логин c некорректным Email - 401")
    @Description("Проверка логина УЗ с неактуальными кредами")
    public void loginWithIncorrectEmail() {
        ValidatableResponse response = authClient.loginUser(
            new AuthClientGenerator().getLoginFormWithIncorrect(createUserForm,"email"));
        checkResponseWithIncorectFields(response);
    }

    @Test
    @DisplayName("Логин c некорректным password - 401")
    @Description("Проверка логина УЗ с неактуальными кредами")
    public void loginWithIncorrectPassword() {
        ValidatableResponse response = authClient.loginUser(
            new AuthClientGenerator().getLoginFormWithIncorrect(createUserForm,"password"));
        checkResponseWithIncorectFields(response);
    }

    @Test
    @DisplayName("Логин c некорректным email & password - 401")
    @Description("Проверка логина УЗ с неактуальными кредами")
    public void loginWithIncorrectFields() {
        ValidatableResponse response = authClient.loginUser(
            new AuthClientGenerator().getLoginFormBy(new AuthClientGenerator().getRandomCreateUserForm()));
        checkResponseWithIncorectFields(response);
    }

    @Step("Проверка ответа метода")
    private void checkResponseWithIncorectFields(ValidatableResponse response) {
        String expected = "email or password are incorrect";
        Boolean success = response.extract().path("success");
        Assert.assertEquals(SC_UNAUTHORIZED, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
        Assert.assertEquals(expected, response.extract().path("message"));
    }

    @Step("Проверка ответа метода")
    private void checkPositiveResponse(ValidatableResponse response) {
        boolean success = response.extract().path("success");
        token = response.extract().path("accessToken");
        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertNotNull(token);
        Assert.assertNotNull(response.extract().path("refreshToken"));
        Assert.assertEquals(createUserForm.getEmail().toLowerCase(), response.extract().path("user.email"));
        Assert.assertEquals(createUserForm.getName(), response.extract().path("user.name"));
    }

}
