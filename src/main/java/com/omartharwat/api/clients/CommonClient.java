package com.omartharwat.api.clients;

import com.omartharwat.api.specs.Specs;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/** Calls for the smaller resources: comments, todos, albums and photos. */
public class CommonClient {

    public Response commentsByPost(int postId) {
        return given().spec(Specs.request()).queryParam("postId", postId).get("/comments");
    }

    public Response commentsByEmail(String email) {
        return given().spec(Specs.request()).queryParam("email", email).get("/comments");
    }

    public Response todoById(int id) {
        return given().spec(Specs.request()).get("/todos/{id}", id);
    }

    public Response todosByCompleted(boolean completed) {
        return given().spec(Specs.request()).queryParam("completed", completed).get("/todos");
    }

    public Response albumById(int id) {
        return given().spec(Specs.request()).get("/albums/{id}", id);
    }

    public Response photosByAlbum(int albumId) {
        return given().spec(Specs.request()).get("/albums/{id}/photos", albumId);
    }

    public Response get(String path) {
        return given().spec(Specs.request()).get(path);
    }
}
