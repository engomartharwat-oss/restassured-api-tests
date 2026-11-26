package com.omartharwat.api.tests;

import com.omartharwat.api.clients.UsersClient;
import com.omartharwat.api.models.User;
import com.omartharwat.api.specs.Specs;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class UsersTests {

    private final UsersClient users = new UsersClient();

    @DataProvider(name = "knownUsers")
    public Object[][] knownUsers() {
        return new Object[][]{
                {1, "Leanne Graham", "Bret"},
                {2, "Ervin Howell", "Antonette"},
                {3, "Clementine Bauch", "Samantha"}
        };
    }

    @Test(description = "GET /users returns 10 users")
    public void getAllUsersReturns10() {
        users.getAll().then().statusCode(200).spec(Specs.jsonAndFast()).body("size()", equalTo(10));
    }

    @Test(dataProvider = "knownUsers", description = "Known users have the expected name and username")
    public void userHasExpectedNameAndUsername(int id, String name, String username) {
        User user = users.getById(id).then().statusCode(200).extract().as(User.class);
        Assert.assertEquals(user.getName(), name);
        Assert.assertEquals(user.getUsername(), username);
    }

    @Test(description = "A user matches the JSON schema (nested address and company included)")
    public void userMatchesJsonSchema() {
        users.getById(1).then().statusCode(200).body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }

    @Test(description = "All users have a valid looking email and a unique id")
    public void allUsersHaveEmailAndUniqueId() {
        List<User> all = users.getAll().then().extract().jsonPath().getList("", User.class);
        Assert.assertTrue(all.stream().allMatch(u -> u.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")),
                "Every email should look like name@domain.tld");
        Assert.assertEquals(all.stream().map(User::getId).distinct().count(), all.size());
    }

    @Test(description = "GET /users/1/todos returns 20 todos that belong to user 1")
    public void userTodosBelongToUser() {
        users.getTodos(1).then().statusCode(200)
                .body("size()", equalTo(20))
                .body("userId", everyItem(equalTo(1)));
    }

    @Test(description = "GET /users/1/albums returns 10 albums that belong to user 1")
    public void userAlbumsBelongToUser() {
        users.getAlbums(1).then().statusCode(200)
                .body("size()", equalTo(10))
                .body("userId", everyItem(equalTo(1)));
    }

    @Test(description = "The geo coordinates of user 1 are present")
    public void userAddressHasGeoCoordinates() {
        users.getById(1).then().statusCode(200)
                .body("address.geo.lat", not(emptyOrNullString()))
                .body("address.geo.lng", not(emptyOrNullString()));
    }
}
