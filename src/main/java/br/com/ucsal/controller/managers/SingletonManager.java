package br.com.ucsal.controller.managers;

import br.com.ucsal.controller.annotation.Singleton;

import java.util.HashMap;
import java.util.Map;

public class SingletonManager {

    private static final Map<Class<?>, Object> instances = new HashMap<>();

    public static <T> T getInstance(Class<T> clazz) {
        if (instances.containsKey(clazz)) {
            return (T) instances.get(clazz);
        }
        if (clazz.isAnnotationPresent(Singleton.class)) {
            try {
                T instance = clazz.getDeclaredConstructor().newInstance();
                instances.put(clazz, instance);
                return instance;
            } catch (Exception e) {
                throw new RuntimeException("Erro ao criar instancia da classe Singleton", e);
            }
        }

        throw new IllegalArgumentException("A classe " + clazz.getName() + "nao esta anotada com Singleton. ");

    }


}
