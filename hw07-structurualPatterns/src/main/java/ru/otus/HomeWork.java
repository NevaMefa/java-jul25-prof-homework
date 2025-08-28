package ru.otus;

import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;
import ru.otus.processor.Processor;
import ru.otus.processor.ProcessorSwapFields11And12;

public class HomeWork {

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
        /*
          по аналогии с Demo.class
          из элеменов "to do" создать new ComplexProcessor и обработать сообщение
        */
        // создаём объект field13
        ObjectForMessage ofm = new ObjectForMessage();
        ofm.setData(java.util.List.of("X", "Y", "Z"));

        // создаём сообщение
        Message msg = new Message.Builder(1)
                .field11("AAA")
                .field12("BBB")
                .field13(ofm)
                .build();

        System.out.println("До обработки: " + msg);

        Processor processor = new ProcessorSwapFields11And12();
        Message result = processor.process(msg);

        System.out.println("После обработки: " + result);
    }
}
