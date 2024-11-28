package br.com.ucsal.controller.manager;


import br.com.ucsal.controller.annotation.Inject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class Injetor {

    private static Map<Class<?>, Object> instancia = new HashMap<>();


    public static void injetarDependencias(Object objeto) {
        Field[] campos = objeto.getClass().getDeclaredFields();
        for (Field campo : campos) {
            if (campo.isAnnotationPresent(Inject.class)) {
                Object instancia = obterInstancia(campo.getType());
                campo.setAccessible(true);
                try {
                    campo.set(objeto, instancia);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Falha ao injetar dependência: " + campo.getName(), e);
                }
            }
        }
    }

    public static Object obterInstancia(Class<?> classe) {
        if (!instancia.containsKey(classe)) {
            try {
                instancia.put(classe, classe.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                throw new RuntimeException("Falha ao criar instância da classe: " + classe.getName(), e);
            }
        }
        return instancia.get(classe);
}


}
