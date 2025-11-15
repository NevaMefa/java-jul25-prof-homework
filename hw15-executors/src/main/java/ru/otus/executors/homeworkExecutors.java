package ru.otus.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    private int iterationCount = 0;
    private final int maxIterations = 20;

    public static void main(String[] args) throws InterruptedException {
        new homeworkExecutors().go();
    }

    private void go() throws InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {

            executor.submit(this::thread1Action);
            executor.submit(this::thread2Action);

            Thread.sleep(3000);

            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                logger.info("Forcing shutdown...");
                executor.shutdownNow();
            }
        }
    }

    private void thread1Action() {
        lock.lock();
        try {
            while (iterationCount < maxIterations && !Thread.currentThread().isInterrupted()) {
                while (!isThread1Turn) {
                    thread1Turn.await();
                }

                if (iterationCount >= maxIterations) break;

                logger.info("Поток 1: {}", currentNumber);
                updateState();

                isThread1Turn = false;
                thread2Turn.signal();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    private void thread2Action() {
        lock.lock();
        try {
            while (iterationCount < maxIterations && !Thread.currentThread().isInterrupted()) {
                while (isThread1Turn) {
                    thread2Turn.await();
                }

                if (iterationCount >= maxIterations) break;

                logger.info("Поток 2: {}", currentNumber);
                updateState();

                isThread1Turn = true;
                thread1Turn.signal();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    private void updateState() {
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
        iterationCount++;
    }
}
