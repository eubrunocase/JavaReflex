package br.com.ucsal.controller.manager;

import br.com.ucsal.controller.annotation.Singleton;
import br.com.ucsal.persistencia.MemoriaProdutoRepository;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ManagerSingleton {

    private static final Map<Class<?>, Object> instances = new HashMap<>();

    public static <T> T getInstance(Class<T> clazz) {
        if (instances.containsKey(clazz)) {
            return (T) instances.get(clazz);
        }

        if (clazz.isAnnotationPresent(Singleton.class)) {
            try {
                Constructor<T> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true); // Permitir acesso a construtor privado
                T instance = constructor.newInstance();
                instances.put(clazz, instance);
                return instance;
            } catch (Exception e) {
                throw new RuntimeException("Erro ao criar a instância do Singleton", e);
            }
        }

        throw new IllegalArgumentException("A classe " + clazz.getName() + " não está anotada com @Singleton.");
    }
}
