package com.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
class ItemResourceTest {

    @Test
    void testGetAllItems() {
        given()
            .when().get("/api/items")
            .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    void testCreateItem() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "name": "Test Item",
                    "description": "A test item",
                    "price": 99.99
                }
                """)
            .when().post("/api/items")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", is("Test Item"));
    }

    @Test
    void testGetItemCount() {
        given()
            .when().get("/api/items/count")
            .then()
                .statusCode(200)
                .body("count", greaterThanOrEqualTo(0));
    }

    @Test
    void testHealthEndpoint() {
        given()
            .when().get("/q/health")
            .then()
                .statusCode(200);
    }

    @Test
    void testInfoEndpoint() {
        given()
            .when().get("/api/info")
            .then()
                .statusCode(200)
                .body("application", is("quarkus-k3s-poc"));
    }
}
