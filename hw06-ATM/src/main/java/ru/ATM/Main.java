package ru.ATM;

public class Main {
    public static void main(String[] args) {
        ATM atm = new SimpleATM();

        atm.deposit(Denomination.D5000, 2);
        atm.deposit(Denomination.D1000, 3);
        atm.deposit(Denomination.D500, 5);
        atm.deposit(Denomination.D100, 10);
        atm.deposit(Denomination.D50, 10);

        System.out.println("Баланс: " + atm.getBalance());
        System.out.println("Инвентарь: " + atm.getInventory());

        System.out.println("--- Выдача 7650 ---");
        System.out.println("Выдано: " + atm.withdraw(7650));
        System.out.println("Баланс: " + atm.getBalance());

        try {
            atm.withdraw(125);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
