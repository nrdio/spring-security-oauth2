package com.nrdio.apigateway.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RouteConfiguration.class)
public class RouteLocatorConfiguration {

    @Autowired
    RouteConfiguration routeConfiguration;

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/oauth/token")
                        .filters(filter -> filter.hystrix(config -> config
                                .setFallbackUri("forward:/timeout")))
                        .uri(routeConfiguration.getAuthorizationServerUri()))
                .route(p -> p
                        .path("/oauth/token/revoke")
                        .filters(filter -> filter.hystrix(config -> config
                                .setFallbackUri("forward:/timeout")))
                        .uri(routeConfiguration.getAuthorizationServerUri()))
                .route(p -> p
                        .path("/userinfo")
                        .filters(filter -> filter.hystrix(config -> config
                                .setFallbackUri("forward:/timeout")))
                        .uri(routeConfiguration.getResourceServerUri()))
                .build();
    }
}
