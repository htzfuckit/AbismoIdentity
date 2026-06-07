package com.htz.abismo.core;

import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {

    private final Map<Class<?>, Object> services = new HashMap<>();

    public <T> void register(Class<T> serviceClass, T instance) {
        services.put(serviceClass, instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> serviceClass) {
        Object service = services.get(serviceClass);
        if (service == null) {
            throw new IllegalArgumentException("Service not registered: " + serviceClass.getName());
        }
        return (T) service;
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrNull(Class<T> serviceClass) {
        return (T) services.get(serviceClass);
    }

    public boolean isRegistered(Class<?> serviceClass) {
        return services.containsKey(serviceClass);
    }
}