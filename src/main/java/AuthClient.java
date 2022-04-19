import static io.restassured.RestAssured.given;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import pojo.CreateUserForm;
import pojo.LoginForm;

public class AuthClient extends StellarBurgersRestClient {

    private static final String AUTH_PATH = "/api/auth";

    @Step("Send POST Request /api/auth/register")
    public ValidatableResponse createUser(CreateUserForm body) {
        return given()
            .spec(getBaseSpec())
            .body(body)
            .when()
            .post(AUTH_PATH + "/register")
            .then();
    }

    @Step("Send POST Request /api/auth/login")
    public ValidatableResponse loginUser(LoginForm body) {
        return given()
            .spec(getBaseSpec())
            .body(body)
            .when()
            .post(AUTH_PATH + "/login")
            .then();
    }

    @Step("Send GET Request /api/auth/user")
    public ValidatableResponse getUserInfo(String token) {
        return given()
            .spec(getBaseSpec())
            .header("Authorization",
                "Bearer " + token.substring(7))
            .when()
            .get(AUTH_PATH + "/login")
            .then();
    }

}
