package com.nhnent.webfluxtest.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Created by haekyu.cho@nhnent.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserHandlerTest {

    @Autowired
    UserRouter userRouter;

    @Test
    public void test() {
        WebTestClient.bindToRouterFunction(userRouter.userRoute())
                     .build()
                     .get()
                     .uri("/users/10?types=PAYCO")
                     .header("clientId", "NCP")
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody(User.class);
    }

    @Test
    public void test2() {
        StepVerifier.create(Flux.just("1", "2")).expectNext("1").expectNext("2").expectComplete();
    }
}
