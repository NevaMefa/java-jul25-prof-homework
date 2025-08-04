package ru.calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Summator {
    private int sum = 0;
    private int prevValue = 0;
    private int prevPrevValue = 0;
    private int sumLastThreeValues = 0;
    private int someValue = 0;
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

    public int getSum() {
        return sum;
    }

    public int getPrevValue() {
        return prevValue;
    }

    public int getPrevPrevValue() {
        return prevPrevValue;
    }

    public int getSumLastThreeValues() {
        return sumLastThreeValues;
    }

    public int getSomeValue() {
        return someValue;
    }
}
