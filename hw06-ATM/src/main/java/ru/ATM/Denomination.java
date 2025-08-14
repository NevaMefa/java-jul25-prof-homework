package ru.ATM;

import java.util.*;

public enum Denomination {
    D50(50),
    D100(100),
    D500(500),
    D1000(1000),
    D5000(5000);

    private final int value;

    Denomination(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static List<Denomination> getDescending() {
        List<Denomination> list = Arrays.asList(values());
        list.sort((a, b) -> b.getValue() - a.getValue());
        return list;
    }
}
