package ru.ATM;

import java.util.EnumMap;
import java.util.Map;

public class SimpleATM implements ATM {
    private CashStorage storage = new CashStorage();

    @Override
    public void deposit(Denomination denom, int count) {
        storage.add(denom, count);
    }

    @Override
    public Map<Denomination, Integer> withdraw(int amount) {
        int remaining = amount;
        Map<Denomination, Integer> plan = new EnumMap<>(Denomination.class);

        for (Denomination d : Denomination.getDescending()) {
            int denomValue = d.getValue();
            int available = storage.getSnapshot().get(d);
            int need = Math.min(remaining / denomValue, available);
            if (need > 0) {
                plan.put(d, need);
                remaining -= denomValue * need;
            }
        }

        if (remaining != 0) {
            throw new IllegalArgumentException("Нельзя выдать сумму: " + amount);
        }

        for (var entry : plan.entrySet()) {
            storage.remove(entry.getKey(), entry.getValue());
        }

        return plan;
    }

    @Override
    public int getBalance() {
        return storage.getTotalAmount();
    }

    @Override
    public Map<Denomination, Integer> getInventory() {
        return storage.getSnapshot();
    }
}
