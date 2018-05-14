package com.nhnent.webfluxtest.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author haekyu.cho@nhnent.com
 * @since 2018-05-14
 */
@Component
@RequiredArgsConstructor
public class UserMongoHandler {
    final UserMongoRepository userMongoRepository;

    Mono<ServerResponse> addUser(ServerRequest request) {
        return request.bodyToMono(User.class)
                      .flatMap(user -> ServerResponse.ok()
                                                     .body(userMongoRepository.save(user), User.class));
    }

    Mono<ServerResponse> getUser(ServerRequest request) {
        Mono<User> userMono = userMongoRepository.findById(Long.parseLong(request.pathVariable("userNo")));

        return ServerResponse.ok().body(userMono, User.class);
    }
}
