package com.omartharwat.api.tests;

import com.omartharwat.api.clients.PostsClient;
import com.omartharwat.api.models.Post;
import com.omartharwat.api.specs.Specs;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * JSONPlaceholder is a fake API: it accepts writes and answers as if they worked,
 * but it does not save anything. These tests check the responses (status codes and echoed data).
 */
public class PostsWriteTests {

    private final PostsClient posts = new PostsClient();

    @DataProvider(name = "newPosts")
    public Object[][] newPosts() throws IOException {
        List<Object[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("testdata/new-posts.csv")))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] c = line.split(",", 3);
                rows.add(new Object[]{Integer.parseInt(c[0]), c[1], c[2]});
            }
        }
        return rows.toArray(new Object[0][]);
    }

    @Test(dataProvider = "newPosts", description = "POST /posts returns 201 and echoes the data (data from CSV)")
    public void createPostReturns201(int userId, String title, String body) {
        posts.create(new Post(userId, title, body))
                .then().statusCode(201).spec(Specs.jsonAndFast())
                .body("id", equalTo(101))
                .body("userId", equalTo(userId))
                .body("title", equalTo(title))
                .body("body", equalTo(body));
    }

    @Test(description = "PUT /posts/1 replaces the whole post and returns 200")
    public void replacePostReturns200() {
        Post updated = new Post(1, "Updated title", "Updated body");
        posts.replace(1, updated).then().statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("Updated title"))
                .body("body", equalTo("Updated body"));
    }

    @Test(description = "PATCH /posts/1 changes only the title")
    public void patchPostChangesOnlyTitle() {
        posts.patchTitle(1, "Only the title changed").then().statusCode(200)
                .body("title", equalTo("Only the title changed"))
                .body("body", not(nullValue()))
                .body("userId", equalTo(1));
    }

    @Test(description = "DELETE /posts/1 returns 200 and an empty object")
    public void deletePostReturns200() {
        String body = posts.delete(1).then().statusCode(200).extract().asString().replaceAll("\\s", "");
        Assert.assertEquals(body, "{}");
    }
}
