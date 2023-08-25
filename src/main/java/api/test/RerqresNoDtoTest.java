package api.test;

import api.Specifications;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

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
        JsonPath jsonPath = response.jsonPath();

    }



}
