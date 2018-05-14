package com.nhnent.webfluxtest.user;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

/**
 * @author haekyu.cho@nhnent.com
 * @since 2018-04-16
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserRedisHandler {

    final ReactiveRedisTemplate<String, User> reactiveRedisTemplate;

    Mono<ServerResponse> addUser(ServerRequest request) {

        return request.bodyToMono(User.class)
                      .flatMap(user -> ServerResponse.ok()
                                                     .body(reactiveRedisTemplate.opsForValue()
                                                                                .set("user:" + user.getUserNo(), user),
                                                           Boolean.class));
    }

    Mono<ServerResponse> getUser(ServerRequest request) {
        Mono<User> result = reactiveRedisTemplate.opsForValue().get("user:" + request.pathVariable("userNo"));

        return ServerResponse.ok().body(result, User.class);
    }

    // test채널로 발행(publish)
    Mono<ServerResponse> publish(ServerRequest request) {
        RedisClient redisClient = RedisClient.create("redis://localhost:6379");

        RedisPubSubReactiveCommands<String, String> commands = redisClient.connectPubSub().reactive();
        Mono<Long> mono = commands.publish("test", request.pathVariable("msg"));

        return mono.flatMap(v -> {
            String responseMessage = "접속되어 있는 subscriber count : " + v.toString();
            return ServerResponse.ok().syncBody(responseMessage);
        });
    }

    @PostConstruct
    public void subscribe() {
        RedisPubSubReactiveCommands<String, String> commands = RedisClient.create("redis://localhost:6379")
                                                                          .connectPubSub()
                                                                          .reactive();

        // test채널로 구독(subscriber)
        commands.subscribe("test").subscribe();
        commands.observeChannels()
                .doOnNext(channelMessage -> log.info("[Channel : {}], 구독 메세지: {}",
                                                     channelMessage.getChannel(),
                                                     channelMessage.getMessage()))
                .subscribe();
    }
}
