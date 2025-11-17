package ru.otus.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class homeworkExecutors {

    private static final Logger logger = LoggerFactory.getLogger(homeworkExecutors.class);

    private final Lock lock = new ReentrantLock();
    private final Condition thread1Turn = lock.newCondition();
    private final Condition thread2Turn = lock.newCondition();

    private boolean isThread1Turn = true;
    private int currentNumber = 1;
    private boolean ascending = true;
    private final int maxNumber = 10;

    public static void main(String[] args) throws InterruptedException {
        new homeworkExecutors().go();
    }

    private void go() throws InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            executor.submit(() -> printNumbers("Поток 1", true));
            executor.submit(() -> printNumbers("Поток 2", false));

            Thread.sleep(10000);
            executor.shutdownNow();
        }
    }

    private void printNumbers(String threadName, boolean isThread1) {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                lock.lock();
                try {
                    // Ждем своей очереди
                    if (isThread1) {
                        while (!isThread1Turn) {
                            thread1Turn.await();
                        }
                    } else {
                        while (isThread1Turn) {
                            thread2Turn.await();
                        }
                    }

                    logger.info("{}: {}", threadName, currentNumber);

                    if (!isThread1Turn) {
                        updateNumber();
                    }

                    if (isThread1) {
                        isThread1Turn = false;
                        thread2Turn.signal();
                    } else {
                        isThread1Turn = true;
                        thread1Turn.signal();
                    }

                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void updateNumber() {
        if (ascending) {
            currentNumber++;
            if (currentNumber > maxNumber) {
                currentNumber = maxNumber - 1;
                ascending = false;
            }
        } else {
            currentNumber--;
            if (currentNumber < 1) {
                currentNumber = 2;
                ascending = true;
            }
        }
    }
}
