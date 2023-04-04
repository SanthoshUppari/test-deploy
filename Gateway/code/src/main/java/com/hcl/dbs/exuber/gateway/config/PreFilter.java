package com.hcl.dbs.exuber.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class PreFilter implements GlobalFilter {
	
	@Autowired
	Environment environment;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String port = environment.getProperty("local.server.port");
		System.out.println("========>> from pre filter "+port);
		return chain.filter(exchange);
	}

}
