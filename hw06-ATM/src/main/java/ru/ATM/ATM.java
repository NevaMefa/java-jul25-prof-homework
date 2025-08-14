package ru.ATM;

import java.util.Map;

public interface ATM {
    void deposit(Denomination denom, int count);

    Map<Denomination, Integer> withdraw(int amount);

    int getBalance();

    Map<Denomination, Integer> getInventory();
}
