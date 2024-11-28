package br.com.ucsal.controller.manager;


import br.com.ucsal.controller.annotation.Inject;
import br.com.ucsal.controller.annotation.Service;
import br.com.ucsal.controller.annotation.Singleton;

import java.lang.reflect.Field;
/*
* @Author: Bruno Cazé
*
* @Version: 1.0
*
* @Since: 28/11/2024
*
* Classe Injetor, responsável por conter o método que garante a injeção de dependência correta nas classes necesárias.
* Nesta classe temos um único método, que contém a lógica da injeção de dependência atráves das anotações @Service e @Inject
*
 */
public class Injetor {

    public static void injetarDependencias(Object target) {
        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                if (field.isAnnotationPresent(Inject.class)) {
                    Object dependency;

                    Class<?> fieldType = field.getType();
                    Class<?> implClass = fieldType;

                    if (fieldType.isInterface()) {
                        Service implAnnotation = field.getAnnotation(Service.class);
                        if (implAnnotation != null) {
                            implClass = implAnnotation.value();
                        } else {
                            throw new RuntimeException("Cannot inject dependency for interface " + fieldType.getName() + " without @ImplementedBy annotation");
                        }
                    }

                    if (implClass.isAnnotationPresent(Singleton.class)) {
                        dependency = ManagerSingleton.getInstance(implClass);
                    } else {
                        dependency = implClass.getDeclaredConstructor().newInstance();
                        injetarDependencias(dependency); // Recursive injection
                    }

                    field.setAccessible(true);
                    field.set(target, dependency);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
