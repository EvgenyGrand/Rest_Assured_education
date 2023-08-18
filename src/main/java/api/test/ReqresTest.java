package api.test;
import java.lang.reflect.Type;
import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import api.Specifications;
import dto.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;


public class ReqresTest {

    private final static String URL = "https://reqres.in";
    private final static String API_USERS_PAGE = "api/users";
    private final static String API_REGISTER = "api/register";

    private final static String API_UNKNOWN = "api/unknown";

    private final static String API_USERS_DELETE = "api/users/2";
    private final static String API_USERS_UPDATE = "api/users/2";


    @Test
    public void checkAvatarAndId_Test() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responceSpecOk200());
        List<UserData> users = given()
                .queryParam("page", "2")
                .when()
                .get(API_USERS_PAGE)
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
        users.stream().forEach(x -> Assertions.assertThat(x.getAvatar().contains(x.getId().toString())));
        Assertions.assertThat(users.stream().allMatch(x -> x.getEmail().endsWith("@reqres.in")));


    }

    @Test
    public void successRegTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responceSpecOk200());
        Integer id = 4;
        String token= "QpwL5tke4Pnpja7X4";

        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                .body(user)
                .when()
                .post(API_REGISTER)
                .then().log().all()
                .extract().as(SuccessReg.class);
        Assertions.assertThat(id.equals(successReg.getId()));
        Assertions.assertThat(token.equals(successReg.getToken()));

    }

    @Test
    public void unSuccessRegUser_Test(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responceSpecError400());
        Integer id = 4;
        String token= "QpwL5tke4Pnpja7X4";

        Register user = new Register("sydney@fife", "");
        UnSuccessReg unSuccessReg = given()
                .body(user)
                .when()
                .post(API_REGISTER)
                .then().log().all()
                .extract().as((Type) UnSuccessReg.class);
        Assertions.assertThat("Missing password").containsIgnoringCase(unSuccessReg.getError());

    }
    @Test
    public void sortedYears_Test(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responceSpecOk200());
        List<ColorsData> colors = given()
            .when()
            .get(API_UNKNOWN)
            .then().log().all()
            .extract().body().jsonPath().getList("data", ColorsData.class);
        List<Integer> years = colors.stream().map(ColorsData::getYear).collect(Collectors.toList());
        List<Integer> sortedYears = years.stream().sorted().collect(Collectors.toList());
        Assertions.assertThat(sortedYears).as("равен").isEqualTo(years);

    }

    @Test
    public void deleteUser_Test(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responceSpecUnique(204));
        given()
                .when()
                .delete(API_USERS_DELETE)
                .then().log().headers().log().status();

    }

    @Test
    public void time_Test(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responceSpecOk200());
        UserTime user = new UserTime("morpheus", "zion resident");
        UserTimeResponse response = given()
                .body(user)
                .when()
                .patch(API_USERS_UPDATE)
                .then().log().all()
                .extract().as(UserTimeResponse.class);
        String regex = "(.{5})$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        Assertions.assertThat(currentTime.equals(response.getUpdatedAt().replaceAll(regex, "")));

    }

}

