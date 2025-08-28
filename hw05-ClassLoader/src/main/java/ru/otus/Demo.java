package ru.otus;

import java.lang.reflect.Proxy;

public class Demo {
    public static void main(String[] args) {
        TestLoggingInterface real = new TestLogging();

        TestLoggingInterface proxy = (TestLoggingInterface) Proxy.newProxyInstance(
                TestLoggingInterface.class.getClassLoader(),
                new Class<?>[] {TestLoggingInterface.class},
                new LoginvocationHandler(real));

        proxy.calculation(6);
        proxy.calculation(3, 4);
        proxy.calculation(1, 2, "three");
    }
}
