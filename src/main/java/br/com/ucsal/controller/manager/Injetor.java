package br.com.ucsal.controller.manager;

import br.com.ucsal.controller.annotation.Inject;
import br.com.ucsal.controller.annotation.Service;
import br.com.ucsal.persistencia.HSQLProdutoRepository;
import br.com.ucsal.service.ProdutoService;

import java.lang.reflect.Field;

public class Injetor {

    public static void injectDependencies(Object target) {
        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Service.class)) {
                field.setAccessible(true);

                try {
                    if (field.getType().equals(ProdutoService.class)) {
                        ProdutoService produtoService = new ProdutoService(new HSQLProdutoRepository());
                        field.set(target, produtoService);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
