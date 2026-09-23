package com.sprintmodus.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "eureka.client.enabled=false")
@AutoConfigureWebTestClient
class GatewayCorsTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void preflightFromAngularDevOriginIsAllowed() {
		webTestClient.options().uri("/auth/login")
				.header(HttpHeaders.ORIGIN, "http://localhost:4200")
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.POST.name())
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,content-type")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200");
	}

	@Test
	void preflightFromUnknownOriginIsRejected() {
		webTestClient.options().uri("/auth/login")
				.header(HttpHeaders.ORIGIN, "http://evil.example")
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.POST.name())
				.exchange()
				.expectStatus().isForbidden();
	}
}
