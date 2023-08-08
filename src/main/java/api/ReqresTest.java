package api;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import io.restassured.http.ContentType;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;


public class ReqresTest {

    private final static String URL = "https://reqres.in";

    @Test
    public void checkAvatarAndIdTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responceSpecOk200());
        List<UserData> users = given()
                .when()
                .get("/api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
        users.stream().forEach(x-> Assertions.assertThat(x.getAvatar().contains(x.getId().toString())));
        Assertions.assertThat(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in")));


        }
    }

