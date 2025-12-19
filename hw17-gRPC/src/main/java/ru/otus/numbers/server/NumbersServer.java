package ru.otus.numbers.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumbersServer {
    private static final Logger logger = LoggerFactory.getLogger(NumbersServer.class);

    public static void main(String[] args) throws Exception {
        logger.info("Starting Numbers Server...");

        Server server = ServerBuilder.forPort(50051)
                .addService(new NumbersServiceImpl())
                .build()
                .start();

        logger.info("Server started on port 50051");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server...");
            server.shutdown();
            logger.info("Server shut down");
        }));

        server.awaitTermination();
    }

    static class NumbersServiceImpl extends ru.otus.numbers.protobuf.NumbersServiceGrpc.NumbersServiceImplBase {
        @Override
        public void getNumberStream(
                ru.otus.numbers.protobuf.NumberRequest request,
                StreamObserver<ru.otus.numbers.protobuf.NumberResponse> responseObserver) {
            int firstValue = request.getFirstValue();
            int lastValue = request.getLastValue();

            logger.info("Received request: firstValue={}, lastValue={}", firstValue, lastValue);

            try {
                for (int i = firstValue + 1; i <= lastValue; i++) {
                    ru.otus.numbers.protobuf.NumberResponse response =
                            ru.otus.numbers.protobuf.NumberResponse.newBuilder()
                                    .setValue(i)
                                    .build();

                    logger.debug("Sending value: {}", i);
                    responseObserver.onNext(response);
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Stream interrupted", e);
                responseObserver.onError(e);
                return;
            }

            responseObserver.onCompleted();
            logger.info("Stream completed for request: {} to {}", firstValue, lastValue);
        }
    }
}
