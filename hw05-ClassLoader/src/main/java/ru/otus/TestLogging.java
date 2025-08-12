package ru.otus;

public class TestLogging implements TestLoggingInterface {

    @Log
    @Override
    public void calculation(int param) {
        System.out.println("inside calculation(int)");
    }

    @Override
    public void calculation(int param1, int param2) {
        System.out.println("inside calculation(int,int)");
    }

    @Log
    @Override
    public void calculation(int param1, int param2, String param3) {
        System.out.println("inside calculation(int,int,String)");
    }
}
