package api.test;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;
import api.Specifications;
import dto.*;
import lombok.Data;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;

@Data
public class ReqresTest extends Endpoints {

    private final static String URL = "https://reqres.in";
    private final static String API_USERS_PAGE = "api/users";
    private final static String API_REGISTER = "api/register";
    private final static String API_UNKNOWN = "api/unknown";
    private final static String API_USERS_DELETE = "api/users/2";
    private final static String API_USERS_UPDATE = "api/users/2";


    @Test
    public void checkAvatarAndId_Test() {
        //Пишем спецификацию с урлом и ответом
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responceSpecOk200());
        //создаем список из полей ДТО класса
        List<UserData> users = given()
                //прописываем параметры запроса
                .queryParam("page", "2")
                .when()
                //прописываем тип запроса и эндпойнт
                .get(API_USERS_PAGE)
                //выводим логи в консоль
                .then().log().all()
                //экспортируем ответ из json в класс USERDTO только то, что содержится в data
                .extract().body().jsonPath().getList("data", UserData.class);
        //вытаскиваем стримом из ссылки накартинку https://reqres.in/img/faces/7-image.jpg" номер и сравниваем с id
        users.stream().forEach(x -> Assertions.assertThat(x.getAvatar().contains(x.getId().toString())));
        //проверяем ,что емейл заканчивается на @reares.in
        Assertions.assertThat(users.stream().allMatch(x -> x.getEmail().endsWith("@reqres.in")));


    }

    @Test
    public void successRegTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responceSpecOk200());
        //актуальные токен и ид из ответа
        Integer id = 4;
        String token= "QpwL5tke4Pnpja7X4";
        //тело запроса
        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                //прописываем тело пост запроса
                .body(user)
                .when()
                .post(API_REGISTER)
                .then().log().all()
                .extract().as(SuccessReg.class);
        //сравниваем ид и токен с актуалными
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
        //сравниваем текст ошибки с актуальным
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
        //сравниваем список с актуальным списком
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

