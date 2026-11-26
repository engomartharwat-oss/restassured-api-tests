package com.omartharwat.api.clients;

import com.omartharwat.api.models.Post;
import com.omartharwat.api.specs.Specs;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/** One method per API call. Tests call the client and only check the response. */
public class PostsClient {

    private static final String POSTS = "/posts";

    public Response getAll() {
        return given().spec(Specs.request()).get(POSTS);
    }

    public Response getById(int id) {
        return given().spec(Specs.request()).get(POSTS + "/{id}", id);
    }

    public Response getByUser(int userId) {
        return given().spec(Specs.request()).queryParam("userId", userId).get(POSTS);
    }

    public Response getComments(int postId) {
        return given().spec(Specs.request()).get(POSTS + "/{id}/comments", postId);
    }

    public Response create(Post post) {
        return given().spec(Specs.request()).body(post).post(POSTS);
    }

    public Response replace(int id, Post post) {
        return given().spec(Specs.request()).body(post).put(POSTS + "/{id}", id);
    }

    public Response patchTitle(int id, String newTitle) {
        return given().spec(Specs.request()).body("{\"title\":\"" + newTitle + "\"}").patch(POSTS + "/{id}", id);
    }

    public Response delete(int id) {
        return given().spec(Specs.request()).delete(POSTS + "/{id}", id);
    }
}
