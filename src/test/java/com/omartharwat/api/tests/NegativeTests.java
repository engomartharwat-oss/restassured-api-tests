package com.omartharwat.api.tests;

import com.omartharwat.api.clients.CommonClient;
import com.omartharwat.api.clients.PostsClient;
import com.omartharwat.api.clients.UsersClient;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class NegativeTests {

    @DataProvider(name = "missingResources")
    public Object[][] missingResources() {
        return new Object[][]{
                {"/posts/9999"},
                {"/users/9999"},
                {"/comments/9999"},
                {"/todos/9999"},
                {"/this-path-does-not-exist"}
        };
    }

    @Test(dataProvider = "missingResources", description = "Unknown ids and paths return 404")
    public void missingResourceReturns404(String path) {
        new CommonClient().get(path).then().statusCode(404);
    }

    @Test(description = "A missing post has an empty JSON object as body")
    public void missingPostHasEmptyBody() {
        String body = new PostsClient().getById(9999).then().statusCode(404).extract().asString().replaceAll("\\s", "");
        Assert.assertEquals(body, "{}");
    }

    @Test(description = "Filtering by an unknown user returns an empty list, not an error")
    public void unknownUserFilterReturnsEmptyList() {
        int size = new PostsClient().getByUser(9999).then().statusCode(200).extract().jsonPath().getList("").size();
        Assert.assertEquals(size, 0);
    }

    @Test(description = "A text id on /users returns 404")
    public void textIdReturns404() {
        new CommonClient().get("/users/abc").then().statusCode(404);
    }

    @Test(description = "A missing user has no todos in the nested route")
    public void unknownUserHasNoTodos() {
        int size = new UsersClient().getTodos(9999).then().statusCode(200).extract().jsonPath().getList("").size();
        Assert.assertEquals(size, 0);
    }
}
