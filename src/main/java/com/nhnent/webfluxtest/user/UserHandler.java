package com.nhnent.webfluxtest.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author haekyu.cho@nhnent.com
 * @since 2018-04-12
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UserHandler {

    Mono<ServerResponse> getUser(ServerRequest request) {
        long userNo = Long.parseLong(request.pathVariable("userNo"));
        String type = request.queryParam("type").orElseThrow(RuntimeException::new);
        String clientId = request.headers().header("clientId").stream().findFirst().orElseThrow(RuntimeException::new);

        log.info("userNo : {}, types : {}, clientId : {}", userNo, type, clientId);

        User user = new User(10L, "홍길동", new String[]{"payco", "naver", "kakao"});

        return ServerResponse.ok().body(Mono.just(user), User.class);
    }

    Mono<ServerResponse> addUser(ServerRequest request) {
        Mono<User> userMono = request.bodyToMono(User.class);

        return ServerResponse.ok().body(userMono, User.class);
    }

    Mono<ServerResponse> editUser(ServerRequest request) {
        Long userNo = Long.parseLong(request.pathVariable("userNo"));

        return ServerResponse.ok().body(Mono.just(userNo), Long.class);
    }

    Mono<ServerResponse> deleteUser(ServerRequest request) {
        Long userNo = Long.parseLong(request.pathVariable("userNo"));

        return ServerResponse.ok().body(Mono.just(userNo), Long.class);
    }

    @SuppressWarnings("unused")
    Mono<ServerResponse> goUser(ServerRequest request) {
        User user = new User(10L, "홍길동", new String[]{"payco", "naver", "kakao"});
        Map<String, Object> model = new HashMap<>();
        model.put("userInfo", user);

        return ServerResponse.ok().render("user", model);
    }

    @SuppressWarnings("unused")
    Mono<ServerResponse> stream(ServerRequest request) {

        return ServerResponse.ok()
                             .contentType(MediaType.TEXT_EVENT_STREAM)
                             .body(stream1().mergeWith(stream2()), String.class);
    }

    @SuppressWarnings("unused")
    Mono<ServerResponse> stream2(ServerRequest request) {
        Flux<String> flux = Flux.create(fluxSink -> addUserMono().subscribe(v -> {
            fluxSink.next(v.toString() + "번 회원 등록 완료");

            process1(v).subscribe(v1 -> fluxSink.next(v1.toString() + " 회원 등록후 process1 처리 완료"));
            process2(v).subscribe(v2 -> fluxSink.next(v2.toString() + " 회원 등록후 process2 처리 완료"));
        }));

        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(flux, String.class);
    }

    Mono<ServerResponse> webclient(ServerRequest request) {
        String searchText = request.queryParam("searchText").orElse("");
        Mono<String> stringMono = WebClient.create("http://sandbox-api.e-ncp.com")
                                           .get()
                                           .uri("/addresses/search?keyword={keyword}", searchText)
                                           .header("clientId", "f7IuuZPHwmdYXu+n2npI6w==")
                                           .header("platform", "PC")
                                           .retrieve()
                                           .bodyToMono(String.class);

        return ServerResponse.ok().body(stringMono, String.class);
    }

    private Flux<String> stream1() {
        return Flux.interval(Duration.ofMillis(200)).take(50).map(i -> "stream1 -> " + String.valueOf(i));
    }

    private Flux<String> stream2() {
        return Flux.interval(Duration.ofMillis(300)).take(30).map(i -> "stream2 -> " + String.valueOf(i));
    }

    // 회원 테이블 등록
    private Mono<Long> addUserMono() {
        return Mono.just(1005L).delayElement(Duration.ofMillis(100));
    }

    // 회원 등록후 process1
    private Mono<Long> process1(long userNo) {
        return Mono.just(userNo + 200L).delayElement(Duration.ofMillis(1000));
    }

    // 회원 등록후 process2
    private Mono<Long> process2(long userNo) {
        return Mono.just(userNo + 100L).delayElement(Duration.ofMillis(1300));
    }
}
