package ru.calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Summator {
    private Integer sum = 0;
    private Integer prevValue = 0;
    private Integer prevPrevValue = 0;
    private Integer sumLastThreeValues = 0;
    private Integer someValue = 0;
    private final List<Data> listValues = new ArrayList<>(100_000);

    public void calc(Data data) {
        listValues.add(data);
        if (listValues.size() >= 100_000) {
            listValues.clear();
        }

        int value = data.getValue();
        int rnd = ThreadLocalRandom.current().nextInt();
        sum += value + rnd;

        sumLastThreeValues = value + prevValue + prevPrevValue;

        prevPrevValue = prevValue;
        prevValue = value;

        int temp = (sumLastThreeValues * sumLastThreeValues / (value + 1) - sum);
        for (int i = 0; i < 3; i++) {
            someValue += temp;
            someValue = Math.abs(someValue) + listValues.size();
        }
    }

    public Integer getSum() {
        return sum;
    }

    public Integer getPrevValue() {
        return prevValue;
    }

    public Integer getPrevPrevValue() {
        return prevPrevValue;
    }

    public Integer getSumLastThreeValues() {
        return sumLastThreeValues;
    }

    public Integer getSomeValue() {
        return someValue;
    }
}
