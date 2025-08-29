package ru.otus.listener.homework;

import java.util.*;
import ru.otus.listener.Listener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;

public class HistoryListener implements Listener, HistoryReader {

    private final Map<Long, Message> history = new HashMap<>();

    @Override
    public void onUpdated(Message message) {
        ObjectForMessage copyField13 = null;
        if (message.getField13() != null) {
            copyField13 = new ObjectForMessage();
            if (message.getField13().getData() != null) {
                copyField13.setData(new ArrayList<>(message.getField13().getData()));
            }
        }

        Message snapshot = message.toBuilder().field13(copyField13).build();

        history.put(message.getId(), snapshot);
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(history.get(id));
    }
}
