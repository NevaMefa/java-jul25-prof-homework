package ru.otus;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LoginvocationHandler implements InvocationHandler {
    private final Object target;

    public LoginvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method implMethod = null;
        try {
            implMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
            System.out.println("Метод не найден, просто выполняем...");
            return method.invoke(target, args);
        }

        boolean hasAnnotation = implMethod.isAnnotationPresent(Log.class);
        if (hasAnnotation == true) {
            String paramsStr = "";
            if (!(args == null || args.length == 0)) {
                for (int i = 0; i < args.length; i++) {
                    paramsStr = paramsStr + args[i];
                    if (i < args.length - 1) {
                        paramsStr = paramsStr + ", ";
                    }
                }
            }

            String paramLabel;
            if (args != null && args.length == 1) {
                paramLabel = "param";
            } else {
                paramLabel = "params";
            }

            if (paramsStr.equals("")) {
                System.out.println("executed method: " + method.getName());
            } else {
                System.out.println("executed method: " + method.getName() + ", " + paramLabel + ": " + paramsStr);
            }
        }
        return method.invoke(target, args);
    }
}
