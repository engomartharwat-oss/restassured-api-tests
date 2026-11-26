package com.omartharwat.api.tests;

import com.omartharwat.api.clients.PostsClient;
import com.omartharwat.api.models.Post;
import com.omartharwat.api.specs.Specs;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class PostsReadTests {

    private final PostsClient posts = new PostsClient();

    @DataProvider(name = "postIds")
    public Object[][] postIds() {
        return new Object[][]{{1}, {50}, {100}};
    }

    @Test(description = "GET /posts returns 100 posts")
    public void getAllPostsReturns100() {
        posts.getAll().then().statusCode(200).spec(Specs.jsonAndFast()).body("size()", equalTo(100));
    }

    @Test(description = "GET /posts/1 returns the first post with its data")
    public void getPostByIdReturnsCorrectPost() {
        Post post = posts.getById(1).then().statusCode(200).extract().as(Post.class);
        Assert.assertEquals(post.getId(), Integer.valueOf(1));
        Assert.assertEquals(post.getUserId(), Integer.valueOf(1));
        Assert.assertFalse(post.getTitle().isEmpty());
        Assert.assertFalse(post.getBody().isEmpty());
    }

    @Test(dataProvider = "postIds", description = "A post matches the JSON schema (checked for first, middle and last post)")
    public void postMatchesJsonSchema(int id) {
        posts.getById(id).then().statusCode(200).body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));
    }

    @Test(description = "Filter with ?userId=1 returns only that user's 10 posts")
    public void filterPostsByUser() {
        List<Post> result = posts.getByUser(1).then().statusCode(200).extract().jsonPath().getList("", Post.class);
        Assert.assertEquals(result.size(), 10);
        Assert.assertTrue(result.stream().allMatch(p -> p.getUserId() == 1), "All posts belong to user 1");
    }

    @Test(description = "GET /posts/1/comments returns 5 comments with valid emails")
    public void commentsOfPostHaveValidEmails() {
        posts.getComments(1).then().statusCode(200)
                .body("size()", equalTo(5))
                .body("postId", everyItem(equalTo(1)))
                .body("email", everyItem(containsString("@")));
    }

    @Test(description = "Every post has a unique id")
    public void postIdsAreUnique() {
        List<Integer> ids = posts.getAll().then().extract().jsonPath().getList("id");
        Assert.assertEquals(ids.stream().distinct().count(), ids.size());
    }

    @Test(description = "Response headers say JSON with UTF-8")
    public void contentTypeHeaderIsJson() {
        Response response = posts.getById(1);
        Assert.assertTrue(response.getHeader("Content-Type").contains("application/json"));
        Assert.assertTrue(response.getHeader("Content-Type").toLowerCase().contains("charset=utf-8"));
    }
}
