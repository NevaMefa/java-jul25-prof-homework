package ru.ATM;

import java.util.EnumMap;
import java.util.Map;

public class CashStorage {
    private Map<Denomination, Integer> storage = new EnumMap<>(Denomination.class);

    public CashStorage() {
        for (Denomination d : Denomination.values()) {
            storage.put(d, 0);
        }
    }

    public void add(Denomination denom, int count) {
        storage.put(denom, storage.get(denom) + count);
    }

    public void remove(Denomination denom, int count) {
        storage.put(denom, storage.get(denom) - count);
    }

    public int getTotalAmount() {
        int sum = 0;
        for (var entry : storage.entrySet()) {
            sum += entry.getKey().getValue() * entry.getValue();
        }
        return sum;
    }

    public Map<Denomination, Integer> getSnapshot() {
        return new EnumMap<>(storage);
    }
}
