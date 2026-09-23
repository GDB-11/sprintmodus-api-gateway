package com.sprintmodus.api_gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;

@SpringBootTest(properties = "eureka.client.enabled=false")
class GatewayRoutingTests {

	@Autowired
	private RouteLocator routeLocator;

	@Test
	void routesPointToTheExpectedServices() {
		Map<String, String> uriByRouteId = routeLocator.getRoutes()
				.collectMap(Route::getId, route -> route.getUri().toString())
				.block();

		assertThat(uriByRouteId).containsExactlyInAnyOrderEntriesOf(Map.of(
				"auth-service", "lb://auth-service",
				"project-service", "lb://project-service",
				"workitem-service", "lb://workitem-service",
				"workitem-service-ws", "lb:ws://workitem-service"));
	}

	@Test
	void everyRouteHasAPathPredicate() {
		Map<String, String> predicates = routeLocator.getRoutes()
				.collectMap(Route::getId, route -> route.getPredicate().toString())
				.block();

		assertThat(predicates.values().stream().collect(Collectors.toList()))
				.allSatisfy(predicate -> assertThat(predicate).contains("Paths"));
	}
}
