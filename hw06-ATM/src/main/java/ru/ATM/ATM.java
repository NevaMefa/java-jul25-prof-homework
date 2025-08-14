package ru.ATM;

import java.util.Map;

public interface ATM {
    void deposit(Denomination denom, int count);

    void deposit(Map<Denomination, Integer> banknotes);

    Map<Denomination, Integer> withdraw(int amount);

    int getBalance();

    Map<Denomination, Integer> getInventory();
}
