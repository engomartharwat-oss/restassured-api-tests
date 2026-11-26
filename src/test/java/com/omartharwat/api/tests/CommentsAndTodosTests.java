package com.omartharwat.api.tests;

import com.omartharwat.api.clients.CommonClient;
import com.omartharwat.api.models.Comment;
import com.omartharwat.api.models.Todo;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;

public class CommentsAndTodosTests {

    private final CommonClient api = new CommonClient();

    @Test(description = "GET /comments?postId=1 returns the 5 comments of post 1")
    public void commentsFilteredByPost() {
        List<Comment> comments = api.commentsByPost(1).then().statusCode(200)
                .extract().jsonPath().getList("", Comment.class);
        Assert.assertEquals(comments.size(), 5);
        Assert.assertTrue(comments.stream().allMatch(c -> c.getPostId() == 1));
    }

    @Test(description = "GET /comments filtered by email returns only that email")
    public void commentsFilteredByEmail() {
        api.commentsByEmail("Eliseo@gardner.biz").then().statusCode(200)
                .body("size()", greaterThan(0))
                .body("email", everyItem(equalTo("Eliseo@gardner.biz")));
    }

    @Test(description = "GET /todos/1 returns the first todo, not completed")
    public void todoByIdReturnsFirstTodo() {
        Todo todo = api.todoById(1).then().statusCode(200).extract().as(Todo.class);
        Assert.assertEquals(todo.getId(), Integer.valueOf(1));
        Assert.assertEquals(todo.getUserId(), Integer.valueOf(1));
        Assert.assertEquals(todo.getTitle(), "delectus aut autem");
        Assert.assertFalse(todo.getCompleted());
    }

    @Test(description = "GET /todos?completed=true returns only completed todos")
    public void completedTodosFilter() {
        api.todosByCompleted(true).then().statusCode(200)
                .body("size()", greaterThan(0))
                .body("completed", everyItem(equalTo(true)));
    }

    @Test(description = "GET /todos?completed=false returns only open todos")
    public void openTodosFilter() {
        api.todosByCompleted(false).then().statusCode(200)
                .body("size()", greaterThan(0))
                .body("completed", everyItem(equalTo(false)));
    }

    @Test(description = "GET /albums/1/photos returns 50 photos with URLs")
    public void albumPhotosHaveUrls() {
        api.photosByAlbum(1).then().statusCode(200)
                .body("size()", equalTo(50))
                .body("albumId", everyItem(equalTo(1)))
                .body("url", everyItem(startsWith("http")));
    }

    @Test(description = "GET /albums/1 returns the album with a title")
    public void albumByIdHasTitle() {
        api.albumById(1).then().statusCode(200)
                .body("id", equalTo(1))
                .body("title", not(emptyOrNullString()));
    }
}
