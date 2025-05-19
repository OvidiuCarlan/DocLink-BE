package com.docklink.apigateway.routes;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class Routes {

//    @Bean
//    public RouterFunction<ServerResponse> userServiceRoute(){
//        String userServiceUrl = System.getenv("USER_SERVICE_URL") != null ?
//                System.getenv("USER_SERVICE_URL") : "http://localhost:8080";
//        return GatewayRouterFunctions.route("user_service")
//                .route(RequestPredicates.path("/users/**"), HandlerFunctions.http(userServiceUrl))
//                .build();
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> postServiceRoute(){
//        String postServiceUrl = System.getenv("POST_SERVICE_URL") != null ?
//                System.getenv("POST_SERVICE_URL") : "http://localhost:8081";
//        return GatewayRouterFunctions.route("post_service")
//                .route(RequestPredicates.path("/posts/**"), HandlerFunctions.http(postServiceUrl))
//                .build();
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> appointmentServiceRoute(){
//        String appointmentServiceUrl = System.getenv("APPOINTMENT_SERVICE_URL") != null ?
//                System.getenv("APPOINTMENT_SERVICE_URL") : "http://localhost:8082";
//        return GatewayRouterFunctions.route("appointment_service")
//                .route(RequestPredicates.path("/appointments/**"), HandlerFunctions.http(appointmentServiceUrl))
//                .build();
//    }

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute(){
        String userServiceUrl = System.getenv("USER_SERVICE_URL") != null ?
                System.getenv("USER_SERVICE_URL") : "http://172.18.0.8:8080";
        return GatewayRouterFunctions.route("user_service")
                .route(RequestPredicates.path("/users/**"),
                        HandlerFunctions.http(userServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> postServiceRoute(){
        String postServiceUrl = System.getenv("POST_SERVICE_URL") != null ?
                System.getenv("POST_SERVICE_URL") : "http://172.18.0.6:8081";
        return GatewayRouterFunctions.route("post_service")
                .route(RequestPredicates.path("/posts/**"),
                        HandlerFunctions.http(postServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> appointmentServiceRoute(){
        String appointmentServiceUrl = System.getenv("APPOINTMENT_SERVICE_URL") != null ?
                System.getenv("APPOINTMENT_SERVICE_URL") : "http://172.18.0.7:8082";
        return GatewayRouterFunctions.route("appointment_service")
                .route(RequestPredicates.path("/appointments/**"),
                        HandlerFunctions.http(appointmentServiceUrl))
                .build();
    }
}
