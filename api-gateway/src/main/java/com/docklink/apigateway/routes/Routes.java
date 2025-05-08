package com.docklink.apigateway.routes;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute(){
        return GatewayRouterFunctions.route("user_service")
                .route(RequestPredicates.path("/users/**"), HandlerFunctions.http("http://localhost:8080"))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> postServiceRoute(){
        return GatewayRouterFunctions.route("post_service")
                .route(RequestPredicates.path("/posts/**"), HandlerFunctions.http("http://localhost:8081"))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> appointmentServiceRoute(){
        return GatewayRouterFunctions.route("appointment_service")
                .route(RequestPredicates.path("/appointments/**"), HandlerFunctions.http("http://localhost:8082"))
                .build();
    }
}
