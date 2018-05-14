package com.nhnent.webfluxtest.configurations;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * @author haekyu.cho@nhnent.com
 * @since 2018-05-14
 */
@Configuration
public class WebsocketConfiguration {

    private final String CHANNEL = "test";

    @Bean
    public HandlerMapping webSocketMapping() {
        RedisPubSubReactiveCommands<String, String> receiverCommands = commands();
        Map<String, WebSocketHandler> map = new HashMap<>();

        map.put("/websocket", session -> {
            // client -> server -> redis publish
            Flux<WebSocketMessage> receiver = session.receive();

            receiver.subscribe(v -> {
                String message = v.getPayloadAsText();
                receiverCommands.publish(CHANNEL, message).subscribe();
            });

            // redis subscriber -> server -> client
            RedisPubSubReactiveCommands<String, String> senderCommands = commands();
            senderCommands.subscribe(CHANNEL).subscribe();

            Flux<WebSocketMessage> sender = senderCommands.observeChannels()
                                                          .map((message) -> session.textMessage(message.getMessage()));

            return session.send(sender);
        });

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(1);

        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    private RedisPubSubReactiveCommands<String, String> commands() {
        return RedisClient.create("redis://localhost:6379").connectPubSub().reactive();
    }
}
