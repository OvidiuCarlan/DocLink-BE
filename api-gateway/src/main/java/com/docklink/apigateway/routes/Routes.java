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
        return GatewayRouterFunctions.route("user_service")
                // Support both with and without /api prefix
                .route(RequestPredicates.path("/api/users/**"),
                        request -> {
                            // Strip the /api prefix before forwarding
                            String path = request.path().substring(4); // Remove "/api"
                            return HandlerFunctions.http("http://localhost:8080" + path).handle(request);
                        })
                .route(RequestPredicates.path("/users/**"),
                        HandlerFunctions.http("http://localhost:8080"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> postServiceRoute(){
        return GatewayRouterFunctions.route("post_service")
                // Support both with and without /api prefix
                .route(RequestPredicates.path("/api/posts/**"),
                        request -> {
                            // Strip the /api prefix before forwarding
                            String path = request.path().substring(4); // Remove "/api"
                            return HandlerFunctions.http("http://localhost:8081" + path).handle(request);
                        })
                .route(RequestPredicates.path("/posts/**"),
                        HandlerFunctions.http("http://localhost:8081"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> appointmentServiceRoute(){
        return GatewayRouterFunctions.route("appointment_service")
                // Support both with and without /api prefix
                .route(RequestPredicates.path("/api/appointments/**"),
                        request -> {
                            // Strip the /api prefix before forwarding
                            String path = request.path().substring(4); // Remove "/api"
                            return HandlerFunctions.http("http://localhost:8082" + path).handle(request);
                        })
                .route(RequestPredicates.path("/appointments/**"),
                        HandlerFunctions.http("http://localhost:8082"))
                .build();
    }
}
