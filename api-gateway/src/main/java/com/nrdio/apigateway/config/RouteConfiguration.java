package com.nrdio.apigateway.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "routes")
@Getter
public class RouteConfiguration {

    @Value("${routes.authorization:http://localhost:8085}")
    String authorizationServerUri;

    @Value("${routes.resource:http://localhost:8086}")
    String resourceServerUri;

}
