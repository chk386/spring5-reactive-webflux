package com.nhnent.exam;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author haekyu.cho@nhnent.com
 * @since 2018-04-11
 */
@Slf4j
public class ReactorExam {

    public static void main(String[] args) {
        Scheduler scheduler = Schedulers.newParallel("parallel");

        log.info("start");

        Flux.just(1, 2, 3, 4, 5)
            .log()
            .subscribeOn(scheduler)
            .doOnComplete(() -> {
                log.info("doOnComplete");
                scheduler.dispose();
            })
            .doOnError(t -> log.error("doOnError", t))
            .map(String::valueOf)
            .subscribe(v -> {
                log.info(v);
                if("3".equals(v)) {
                    throw new RuntimeException("");
                }
            });

        log.info("end");
    }
}
