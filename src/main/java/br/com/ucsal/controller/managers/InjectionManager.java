package br.com.ucsal.controller.managers;

import br.com.ucsal.controller.annotation.Inject;
import br.com.ucsal.persistencia.HSQLProdutoRepository;
import br.com.ucsal.service.ProdutoService;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;


public class InjectionManager {

//    public static void injectDependencies(Object target) {
//        Class<?> clazz = target.getClass();
//        Field[] fields = clazz.getDeclaredFields();
//
//        for (Field field : fields) {
//            if (field.isAnnotationPresent(Inject.class)) {
//                field.setAccessible(true);
//
//                try {
//                    if (field.getType().equals(ProdutoService.class)) {
//                        ProdutoService produtoService = new ProdutoService(new HSQLProdutoRepository());
//                        field.set(target, produtoService);
//                    }
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    public static void injectDependencies (Object object)  {
         Field[] fields = object.getClass().getDeclaredFields();

         for (Field field : fields) {
             if (field.isAnnotationPresent(Inject.class)) {

                 try {
                 field.setAccessible(true);
                 Class<?> fieldType = field.getType();
                 ProdutoService produtoService = (ProdutoService) fieldType.getDeclaredConstructor().newInstance();

                 field.set(object, produtoService);

                  } catch (Exception e) {
                      throw new RuntimeException("Erro ao instanciar classe " + field.getName(), e);
                  }
             }
         }
    }


}
