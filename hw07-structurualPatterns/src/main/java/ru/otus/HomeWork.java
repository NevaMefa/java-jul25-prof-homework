package ru.otus;

import java.time.Clock;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.homework.MessageHistoryListener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;
import ru.otus.processor.Processor;
import ru.otus.processor.ProcessorEvenSecondException;
import ru.otus.processor.ProcessorSwapFields11And12;

public class HomeWork {
    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);
    /*
    Реализовать to do:
      1. Добавить поля field11 - field13 (для field13 используйте класс ObjectForMessage)
      2. Сделать процессор, который поменяет местами значения field11 и field12
      3. Сделать процессор, который будет выбрасывать исключение в четную секунду (сделайте тест с гарантированным результатом)
            Секунда должна определяьться во время выполнения.
            Тест - важная часть задания
            Обязательно посмотрите пример к паттерну Мементо!
      4. Сделать Listener для ведения истории (подумайте, как сделать, чтобы сообщения не портились)
         Уже есть заготовка - класс HistoryListener, надо сделать его реализацию
         Для него уже есть тест, убедитесь, что тест проходит
    */

    public static void main(String[] args) {
        // создаём объект field13
        ObjectForMessage ofm = new ObjectForMessage();
        ofm.setData(List.of("X", "Y", "Z"));

        // создаём сообщение с полями 11-13
        Message msg = new Message.Builder(1)
                .field11("AAA")
                .field12("BBB")
                .field13(ofm)
                .build();

        // создаём процессоры
        Processor swapProcessor = new ProcessorSwapFields11And12();
        Processor exceptionProcessor = new ProcessorEvenSecondException(Clock.systemDefaultZone());

        List<Processor> processors = List.of(swapProcessor, exceptionProcessor);

        // создаём ComplexProcessor
        ComplexProcessor complexProcessor = new ComplexProcessor(processors, ex -> {
            System.out.println("Исключение при обработке: " + ex.getMessage());
        });

        // создаём Listener для истории
        MessageHistoryListener historyListener = new MessageHistoryListener();
        complexProcessor.addListener(historyListener);

        // обрабатываем сообщение
        try {
            Message result = complexProcessor.handle(msg);
            logger.info("Результат обработки: {}", result);
        } catch (RuntimeException e) {
            logger.error("Исключение при обработке: {}", e.getMessage());
        }

        // выводим историю сообщений
        historyListener.getHistory().forEach(m -> logger.info("История сообщения: {}", m));

        // отключаем listener
        complexProcessor.removeListener(historyListener);
    }
}
