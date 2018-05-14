package com.nhnent.exam;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ReactiveExam {

    private static List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);

    public static void main(String[] args) {

        Publisher<Integer> publisher = s -> {
            ExecutorService es = Executors.newSingleThreadExecutor();

            s.onSubscribe(new Subscription() {

                @Override
                public void request(long n) {
                    es.execute(() -> list.forEach(i -> {
                        s.onNext(i);

                        if (i == 3) {
                            s.onError(new RuntimeException("3은 에러야!!"));
                        }
                    }));

                    s.onComplete();
                    es.shutdown();
                }

                @Override
                public void cancel() {

                }
            });
        };

        Subscriber<Integer> subscriber = new Subscriber<>() {

            @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe", s);
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.info("onNext : {}", integer);
            }

            @Override
            public void onError(Throwable t) {
                log.error("onError", t);
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };

        publisher.subscribe(subscriber);
    }
}
