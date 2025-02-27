package com.mobigen.lecture.gatewayserver.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomGlobalFilter extends AbstractGatewayFilterFactory<CustomGlobalFilter.Config> {

    public static class Config {
    }

    public CustomGlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // PRE
            StringBuilder preSb = new StringBuilder();
            preSb.append("\n[Global Filter] Request >> IP: " + request.getRemoteAddress().getAddress() + ", URI: " + request.getURI());

            // POST
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {

                preSb.append("\n[Global Filter] Response << IP: " + request.getRemoteAddress().getAddress() +
                        ", URI: " + request.getURI() +
                        ", Code: " + response.getStatusCode());

                log.info(preSb.toString());
                preSb.setLength(0);
            }));
        });
    }

}