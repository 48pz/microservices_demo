package com.flow.microservices.order_service;

import com.flow.microservices.order_service.stubs.InventoryClientStub;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

import static org.hamcrest.MatcherAssert.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)//random port
class OrderServiceApplicationTests {

	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		mySQLContainer.start();
	}

	@Test
	void shouldSubmitOrder() {
		String submitOrderJson= """
				{
				     "skuCode":"IPhone_16",
				     "price":1400,
				     "quantity":1
				 }
				""";
		InventoryClientStub.stubInventoryCall("IPhone_16", 1);
		String responseBodyString = RestAssured.given().contentType("application/json")
				.body(submitOrderJson)
				.when().post("/api/order")
				.then().statusCode(201)
				.extract().body().asString();

		assertThat(responseBodyString,Matchers.is("Order placed successfully"));
	}

}
