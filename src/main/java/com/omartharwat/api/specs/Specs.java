package com.omartharwat.api.specs;

import com.omartharwat.api.config.ApiConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.lessThan;

/** Shared request and response settings so tests stay short. */
public final class Specs {

    private Specs() {
    }

    public static RequestSpecification request() {
        return new RequestSpecBuilder()
                .setBaseUri(ApiConfig.get("base.uri"))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .log(io.restassured.filter.log.LogDetail.URI)
                .build();
    }

    /** Every response must be JSON and fast enough. */
    public static ResponseSpecification jsonAndFast() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan(ApiConfig.getLong("max.response.time.ms")))
                .build();
    }
}
