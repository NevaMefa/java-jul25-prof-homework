package ru.otus.processor;

import java.time.Clock;
import java.time.LocalDateTime;
import ru.otus.model.Message;

public class ProcessorEvenSecondException implements Processor {

    private final Clock clock;

    public ProcessorEvenSecondException(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Message process(Message message) {
        int second = LocalDateTime.now(clock).getSecond();
        if (second % 2 == 0) {
            throw new RuntimeException("Чётная секунда: " + second);
        }
        return message;
    }
}
