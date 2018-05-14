package com.nhnent.exam;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author haekyu.cho@nhnent.com
 * @since 2018-04-10
 */
@Slf4j
public class IterableObservableExam {

    private static List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);

    public static void main(String[] args) {
        iterablePattern();
        observablePattern();
    }

    private static void iterablePattern() {
        Iterator<Integer> iter = list.iterator();

//        while (iter.hasNext()) {
//            log.info("iter : {}", iter.next());
//        }

        for (int i : list) {
            log.info("iterable : {}", i);
        }

        log.info("exit");
    }

    @SuppressWarnings("deprecation")
    private static void observablePattern() {
        ExamObservable observable = new ExamObservable(list);
        observable.addObserver((o, arg) -> log.info("observable : {}", arg));

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(observable);

        log.info("exit");
        executorService.shutdown();
    }

    @SuppressWarnings("deprecation")
    static class ExamObservable extends Observable implements Runnable {

        List<Integer> list;

        private ExamObservable(List<Integer> list) {
            this.list = list;
        }

        @Override
        public void run() {
            list.forEach(i -> {
                setChanged();
                notifyObservers(i);
            });
        }
    }
}
