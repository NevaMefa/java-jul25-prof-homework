package ru.otus.appcontainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

@SuppressWarnings({"squid:S1068", "unchecked"})
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);

        // 1) создаём инстанс конфигурации (безарг. конструктор)
        final Object configInstance = instantiateConfig(configClass);

        // 2) собираем все методы с @AppComponent и сортируем по order
        final List<Method> componentMethods = Arrays.stream(configClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparingInt(
                        m -> m.getAnnotation(AppComponent.class).order()))
                .collect(Collectors.toList());

        // 3) создаём компоненты по порядку
        for (Method method : componentMethods) {
            AppComponent meta = method.getAnnotation(AppComponent.class);
            String beanName = meta.name();

            if (appComponentsByName.containsKey(beanName)) {
                throw new IllegalStateException("Component with name '" + beanName + "' already exists");
            }

            // Готовим зависимости по типам параметров метода
            Object[] args = resolveMethodArgs(method.getParameterTypes());

            try {
                method.setAccessible(true);
                Object bean = method.invoke(configInstance, args);
                if (bean == null) {
                    throw new IllegalStateException("Factory method " + method.getName() + " returned null");
                }
                appComponents.add(bean);
                appComponentsByName.put(beanName, bean);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Can't create component '" + beanName + "'", e);
            }
        }
    }

    private Object instantiateConfig(Class<?> configClass) {
        try {
            Constructor<?> ctor = configClass.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Config class must have a no-args constructor: " + configClass.getName(), e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Can't instantiate config: " + configClass.getName(), e);
        }
    }

    private Object[] resolveMethodArgs(Class<?>[] paramTypes) {
        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            args[i] = findComponentByType(paramTypes[i]);
            if (args[i] == null) {
                throw new IllegalStateException(
                        "Unsatisfied dependency: no component for type " + paramTypes[i].getName());
            }
        }
        return args;
    }

    private Object findComponentByType(Class<?> type) {
        // Возвращаем первый компонент, чей класс совместим с требуемым типом
        for (Object component : appComponents) {
            if (type.isAssignableFrom(component.getClass())) {
                return component;
            }
        }
        return null;
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        for (Object component : appComponents) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                return (C) component;
            }
        }
        throw new NoSuchElementException("No component found for type: " + componentClass.getName());
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        Object bean = appComponentsByName.get(componentName);
        if (bean == null) {
            throw new NoSuchElementException("No component found with name: " + componentName);
        }
        return (C) bean;
    }
}
