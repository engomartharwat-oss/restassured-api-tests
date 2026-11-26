package com.omartharwat.api.clients;

import com.omartharwat.api.specs.Specs;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UsersClient {

    private static final String USERS = "/users";

    public Response getAll() {
        return given().spec(Specs.request()).get(USERS);
    }

    public Response getById(int id) {
        return given().spec(Specs.request()).get(USERS + "/{id}", id);
    }

    public Response getTodos(int userId) {
        return given().spec(Specs.request()).get(USERS + "/{id}/todos", userId);
    }

    public Response getAlbums(int userId) {
        return given().spec(Specs.request()).get(USERS + "/{id}/albums", userId);
    }
}
