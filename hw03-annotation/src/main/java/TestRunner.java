import annotation.After;
import annotation.Before;
import annotation.Test;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {

    public static void runTests(String className) {
        int passed = 0;
        int failed = 0;
        int total = 0;

        try {
            Class<?> testClass = Class.forName(className);
            Method[] methods = testClass.getDeclaredMethods();

            List<Method> beforeMethods = new ArrayList<>();
            List<Method> testMethods = new ArrayList<>();
            List<Method> afterMethods = new ArrayList<>();

            for (Method method : methods) {
                if (method.isAnnotationPresent(Before.class)) {
                    beforeMethods.add(method);
                } else if (method.isAnnotationPresent(Test.class)) {
                    testMethods.add(method);
                } else if (method.isAnnotationPresent(After.class)) {
                    afterMethods.add(method);
                }
            }

            for (Method testMethod : testMethods) {
                total++;
                Object testInstance = testClass.getDeclaredConstructor().newInstance();
                try {
                    runAll(testInstance, beforeMethods);
                    testMethod.setAccessible(true);
                    testMethod.invoke(testInstance);
                    System.out.println("[PASSED] " + testMethod.getName());
                    passed++;
                } catch (Exception e) {
                    System.out.println("[FAILED] " + testMethod.getName() + ": " + e.getCause());
                    failed++;
                } finally {
                    runAll(testInstance, afterMethods);
                }
            }

            System.out.println("\n===== TEST RESULTS =====");
            System.out.println("Total: " + total);
            System.out.println("Passed: " + passed);
            System.out.println("Failed: " + failed);

        } catch (Exception e) {
            System.err.println("Error running tests: " + e.getMessage());
        }
    }

    private static void runAll(Object instance, List<Method> methods) {
        for (Method method : methods) {
            try {
                method.setAccessible(true);
                method.invoke(instance);
            } catch (Exception e) {
                System.out.println("Error in " + method.getName() + ": " + e.getCause());
            }
        }
    }
}
