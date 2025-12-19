package ru.otus.numbers.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumbersClient {
    private static final Logger logger = LoggerFactory.getLogger(NumbersClient.class);

    private static int currentValue = 0;
    private static int lastServerValue = 0;
    private static boolean newNumberAvailable = false;
    private static final Object lock = new Object();

    public static void main(String[] args) throws Exception {
        logger.info("numbers Client is starting...");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        try {
            var stub = ru.otus.numbers.protobuf.NumbersServiceGrpc.newStub(channel);
            var request = ru.otus.numbers.protobuf.NumberRequest.newBuilder()
                    .setFirstValue(0)
                    .setLastValue(30)
                    .build();

            ClientStreamObserver streamObserver = new ClientStreamObserver();

            stub.getNumberStream(request, streamObserver);

            runClientLoop();

            Thread.sleep(55000);

        } finally {
            channel.shutdown();
        }
    }

    public static void updateLastServerValue(int value) {
        synchronized (lock) {
            lastServerValue = value;
            newNumberAvailable = true;
        }
    }

    private static void runClientLoop() {
        new Thread(() -> {
            try {
                for (int i = 0; i <= 50; i++) {
                    synchronized (lock) {
                        if (newNumberAvailable) {
                            currentValue = currentValue + lastServerValue + 1;
                            newNumberAvailable = false;
                        }
                    }
                    logger.info("currentValue:{}", currentValue);
                    Thread.sleep(1000);
                }

                logger.info("Client loop completed");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Client loop interrupted", e);
            }
        })
                .start();
    }

    static class ClientStreamObserver implements StreamObserver<ru.otus.numbers.protobuf.NumberResponse> {
        @Override
        public void onNext(ru.otus.numbers.protobuf.NumberResponse response) {
            int receivedValue = response.getValue();
            logger.info("new value:{}", receivedValue);
            NumbersClient.updateLastServerValue(receivedValue);
        }

        @Override
        public void onError(Throwable t) {
            logger.error("Error in stream: ", t);
        }

        @Override
        public void onCompleted() {
            logger.info("request completed");
        }
    }
}
