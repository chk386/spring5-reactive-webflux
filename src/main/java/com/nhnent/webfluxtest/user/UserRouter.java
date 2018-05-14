package com.nhnent.webfluxtest.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author haekyu.cho@nhnent.com
 * @since 2018-04-12
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
//@formatter:off
public class UserRouter {

    final UserHandler userHandler;
    final UserRedisHandler userRedisHandler;
    final UserMongoHandler userMongoHandler;

    @Bean
    public RouterFunction userRoute() {
        return nest(path("/users"),
                   route(GET("/{userNo}"), userHandler::getUser)
                  .andRoute(POST("/"), userHandler::addUser)
                  .andRoute(PUT("/{userNo}"), userHandler::editUser)
                  .andRoute(DELETE("/{userNo}"), userHandler::deleteUser))
              // view template
              .andRoute(GET("/goUser"), userHandler::goUser)
              // http streaming
              .andRoute(GET("/stream"), userHandler::stream)
              .andRoute(GET("/stream2"), userHandler::stream2)
              // web service
              .andRoute(GET("/webclient"), userHandler::webclient)
              // redis
              .andNest(path("/users/redis"),
                       route(POST("/"), userRedisHandler::addUser)
                      .andRoute(GET("/{userNo}"), userRedisHandler::getUser))
              // mongoRepository
              .andNest(path("/users/mongo"),
                       route(POST("/"), userMongoHandler::addUser)
                      .andRoute(GET("/{userNo}"), userMongoHandler::getUser))
              // redis pub/sub
              .andRoute(GET("/send/{msg}"), userRedisHandler::publish)
              // mongoRepository
              .andNest(path("/users/mongo"),
                       route(POST("/"), userRedisHandler::addUser)
                      .andRoute(GET("/{userNo}"), userRedisHandler::getUser))
              .filter((request, next) -> {
                  log.info("filter : before handler");

                  return next.handle(request);
              })
              // localhost:8080/sse.html
              // localhost:8080/websocket.html
            ;
    }
}
