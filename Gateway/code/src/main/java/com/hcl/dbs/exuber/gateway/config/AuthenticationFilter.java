package com.hcl.dbs.exuber.gateway.config;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.utility.Utilites;

import reactor.core.publisher.Mono;

@RefreshScope
@Component
public class AuthenticationFilter implements GatewayFilter {

	public static final List<String> openApiEndpoints = List.of("/auth/register", "/auth/login", "/user/public");

	public Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints.stream()
			.noneMatch(uri -> request.getURI().getPath().contains(uri));

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		if (isSecured.test(request)) {
			try {
				String emailId = Utilites.getEmail();
				System.out.println("emailId ===>> " + emailId);
				request.getHeaders().add("emailId", emailId);
			} catch (Exception e) {
				return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);
			}
		}
		return chain.filter(exchange);
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		return response.setComplete();
	}

	private String getAuthHeader(ServerHttpRequest request) {
		return request.getHeaders().getOrEmpty("Authorization").get(0);
	}

	private boolean isAuthMissing(ServerHttpRequest request) {
		return !request.getHeaders().containsKey("Authorization");
	}

}
