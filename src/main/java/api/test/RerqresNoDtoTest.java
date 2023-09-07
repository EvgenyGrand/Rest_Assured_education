package api.test;

import api.Specifications;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class RerqresNoDtoTest {

    private final static String URL = "https://reqres.in";
    private final static String API_USERS_PAGE = "api/users";
    private final static String API_REGISTER = "api/register";
    private final static String API_UNKNOWN = "api/unknown";
    private final static String API_USERS_DELETE = "api/users/2";
    private final static String API_USERS_UPDATE = "api/users/2";


    @Test
    public void checkAvatarsnoPojo_Test(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responceSpecOk200());
        //повторяем тест из ReqresTest. но без ДТО файла, извлекаем json в responce
        Response response = given()
                .queryParam("page", "2")
                .when()
                .get(API_USERS_PAGE)
                .then().log().all()
                //page==2
                .body("page", equalTo(2))
                //поля из data !=null
                .body("data.id", notNullValue())
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .extract().response();
        //извлекаем из json список email и id, avatars
        JsonPath jsonPath = response.jsonPath();
        List<String> emails = jsonPath.get("data.email");
        List<Integer> ids = jsonPath.get("data.id");
        List<String> avatars = jsonPath.get("data.avatar");
        Assertions.assertThat(emails.stream().allMatch(i->i.endsWith("@reqres.in")));

    }

    @Test
    public void successRegTestNoPojo(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responceSpecOk200());
        Map<String, String> user = new HashMap<>();
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");
        given()
        .body(user)
                .when()
                .post(API_REGISTER)
                .then().log().all()
                .body("id", equalTo(4))
                .body("token",equalTo("QpwL5tke4Pnpja7X4"));

    }

    @Test
    public void successRegTestNoPojoResponse(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responceSpecOk200());
        Map<String, String> user = new HashMap<>();
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");
       Response response= given()
               .body(user)
               .when()
                .post(API_REGISTER)
                .then().log().all()
                .extract().response();
       JsonPath jsonPath = response.jsonPath();
       int id = jsonPath.get("id");
       String token = jsonPath.get("token");
       Assertions.assertThat(4);
       Assertions.assertThat("QpwL5tke4Pnpja7X4");

    }


}
