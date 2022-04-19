import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;

import generator.AuthFormGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.CreateUserForm;

@Story("POST /api/auth/register - Создание пользователя")
public class CreateNewUserTest {
    private AuthClient authClient;

    @Before
    public void setUp() {
        authClient = new AuthClient();
    }

    @Test
    @DisplayName("Успешное создание УЗ - 200")
    @Description("Проверка успешного создания УЗ. Status = 200 Created")
    public void positiveCreateUser() {
        CreateUserForm createUserForm = new AuthFormGenerator().getRandomCreateUserForm();
        ValidatableResponse response = authClient.createUser(createUserForm);
        checkPositiveResponse(response, createUserForm);
    }

    @Test
    @DisplayName("Запрос без параметра: Email - 403")
    @Description("Проверка создания УЗ без параметра Email. Status = 403 Forbidden")
    public void createUserWithoutEmail() {
        ValidatableResponse response = authClient.createUser(new AuthFormGenerator()
            .getRandomCreateUserFormWithout("email"));
        checkResponseWithoutField(response);
    }

    @Test
    @DisplayName("Запрос без параметра: Password - 403")
    @Description("Проверка создания УЗ без параметра Password. Status = 403 Forbidden")
    public void createUserWithoutPassword() {
        ValidatableResponse response = authClient.createUser(new AuthFormGenerator()
            .getRandomCreateUserFormWithout("password"));
        checkResponseWithoutField(response);
    }

    @Test
    @DisplayName("Запрос без параметра: Name - 403")
    @Description("Проверка создания УЗ без параметра Name. Status = 403 Forbidden")
    public void createUserWithoutName() {
        ValidatableResponse response = authClient.createUser(new AuthFormGenerator()
            .getRandomCreateUserFormWithout("name"));
        checkResponseWithoutField(response);
    }

    @Test
    @DisplayName("Создание существующего пользователя - 403")
    @Description("Проверка создания УЗ с повторяющимися кредами.")
    public void createUserWithDublicateFields() {
        CreateUserForm createUserForm = new AuthFormGenerator().getRandomCreateUserForm();
        authClient.createUser(createUserForm);
        ValidatableResponse response = authClient.createUser(createUserForm);
        checkResponseWithDublicateFields(response);
    }

    @Step("Проверка ответа метода")
    private void checkResponseWithDublicateFields(ValidatableResponse response) {
        String expected = "User already exists";
        Boolean success = response.extract().path("success");
        Assert.assertEquals(SC_FORBIDDEN, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
        Assert.assertEquals(expected, response.extract().path("message"));
    }

    @Step("Проверка ответа метода")
    private void checkPositiveResponse(ValidatableResponse response, CreateUserForm createUserForm) {
        Boolean success = response.extract().path("success");
        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(createUserForm.getEmail().toLowerCase(), response.extract().path("user.email"));
        Assert.assertEquals(createUserForm.getName(), response.extract().path("user.name"));
        Assert.assertNotNull(response.extract().path("accessToken"));
        Assert.assertNotNull(response.extract().path("refreshToken"));
    }

    @Step("Проверка ответа метода")
    private void checkResponseWithoutField(ValidatableResponse response) {
        String expected = "Email, password and name are required fields";
        Boolean success = response.extract().path("success");
        Assert.assertEquals(SC_FORBIDDEN, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
        Assert.assertEquals(expected, response.extract().path("message"));
    }

}
