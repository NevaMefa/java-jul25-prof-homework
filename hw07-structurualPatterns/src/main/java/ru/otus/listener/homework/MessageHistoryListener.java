package ru.otus.listener.homework;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ru.otus.listener.Listener;
import ru.otus.model.Message;

public class MessageHistoryListener implements Listener {

    private final List<Message> history = new ArrayList<>();

    @Override
    public void onUpdated(Message message) {

        history.add(message.toBuilder().build());
    }

    public Optional<Message> findMessageById(long id) {
        return history.stream().filter(msg -> msg.getId() == id).findFirst();
    }

    public List<Message> getHistory() {
        return new ArrayList<>(history);
    }
}
