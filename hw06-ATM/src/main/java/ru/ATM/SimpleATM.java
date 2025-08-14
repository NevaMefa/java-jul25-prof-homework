package ru.ATM;

import java.util.EnumMap;
import java.util.Map;

public class SimpleATM implements ATM {
    private Map<Denomination, Integer> storage = new EnumMap<>(Denomination.class);

    public SimpleATM() {
        for (Denomination d : Denomination.values()) {
            storage.put(d, 0);
        }
    }

    @Override
    public void deposit(Denomination denom, int count) {
        if (count <= 0) throw new IllegalArgumentException("Количество должно быть > 0");
        storage.put(denom, storage.get(denom) + count);
    }

    @Override
    public void deposit(Map<Denomination, Integer> banknotes) {
        for (var entry : banknotes.entrySet()) {
            deposit(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Map<Denomination, Integer> withdraw(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Сумма должна быть > 0");

        int remaining = amount;
        Map<Denomination, Integer> plan = new EnumMap<>(Denomination.class);

        // Жадный алгоритм — от больших к малым
        for (Denomination d : Denomination.getDescending()) {
            int denomValue = d.getValue();
            int available = storage.get(d);
            int need = Math.min(remaining / denomValue, available);
            if (need > 0) {
                plan.put(d, need);
                remaining -= denomValue * need;
            }
        }

        if (remaining != 0) {
            throw new IllegalArgumentException("Нельзя выдать сумму: " + amount);
        }

        // Вычитаем из хранилища
        for (var entry : plan.entrySet()) {
            storage.put(entry.getKey(), storage.get(entry.getKey()) - entry.getValue());
        }

        return plan;
    }

    @Override
    public int getBalance() {
        int sum = 0;
        for (var entry : storage.entrySet()) {
            sum += entry.getKey().getValue() * entry.getValue();
        }
        return sum;
    }

    @Override
    public Map<Denomination, Integer> getInventory() {
        return new EnumMap<>(storage);
    }
}
