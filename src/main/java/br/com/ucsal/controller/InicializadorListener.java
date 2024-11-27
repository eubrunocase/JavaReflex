package br.com.ucsal.controller;

import br.com.ucsal.controller.annotation.Inject;
import br.com.ucsal.controller.annotation.Singleton;
import br.com.ucsal.controller.manager.ManagerSingleton;
import br.com.ucsal.model.Produto;
import br.com.ucsal.persistencia.MemoriaProdutoRepository;
import br.com.ucsal.persistencia.PersistenciaFactory;
import br.com.ucsal.persistencia.ProdutoRepository;
import br.com.ucsal.service.ProdutoService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.reflections.Reflections;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@WebListener
public class InicializadorListener implements ServletContextListener {

    private Map<String, Command> commands = new HashMap<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        System.out.println("Inicializando a aplicação...");

        try {
            injetarDependencias();
        } catch (Exception e) {
            System.out.println("Erro ao injetar dependências: " + e.getMessage());
            e.printStackTrace();
        }
        ServletContext context = sce.getServletContext();
        context.setAttribute("commands", commands);
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources("");

            while (resources.hasMoreElements()) {
               URL resource = resources.nextElement();
               File directory = new File(resource.getFile());

               if (directory.exists() && directory.isDirectory()) {
                   ProcessarDiretorios(directory, context);
               }
            }
        } catch (Exception e) {
             e.printStackTrace();
        }
        chargeSingleton();
        System.out.println("Aplicação inicializada com sucesso!");
    }

    private void ProcessarDiretorios (File directory, ServletContext servletContext) {
        if (directory != null && directory.exists() && directory.isDirectory()) {

            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    ProcessarDiretorios(file, servletContext);
                } else if (file.getName().endsWith(".class")) {

                    try {
                        System.out.println("INICIALIZANDO CLASSE " + file.getPath());
                        String className = file.getPath()
                                .replace(File.separator, ".")
                                .replaceFirst(".*?.classes.", "")
                                .replace(".class", "");

                        System.out.println("TENTANDO CARREGAR A CLASSE " + className);
                       // Class<?> clazz = Class.forName(className, true, servletContext.getClassLoader());
                        System.out.println("CLASSE CARREGADA COM SUCESSO " + className);
                    } catch (Exception e) {
                       System.out.println("ERRO AO CARREGAR A CLASSE " + file.getName());
                    }
                }
            }
        }
    }

    private void chargeSingleton () {
        Reflections reflections = new Reflections("br.com.ucsal");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Singleton.class);

        for (Class<?> clazz : classes) {
                try {
                    MemoriaProdutoRepository.getInstancia();
                    System.out.println("CLASSE ANOTADA COM @SINGLETON INICIALIZADA " + clazz.getSimpleName());
                } catch (Exception e) {
                   throw new RuntimeException("ERRO AO CARREGAR A CLASSE @SINGLETON" + clazz.getSimpleName());
                }
        }
    }

    private void injetarDependencias() throws IllegalAccessException {
        Reflections reflections = new Reflections("br.com.ucsal");
        Set<Field> fields = reflections.getFieldsAnnotatedWith(Inject.class);

        for (Field field : fields) {
            field.setAccessible(true);

            Object dependency = resolverDependencia(field.getType());
            if (dependency != null) {
                Class<?> declaringClass = field.getDeclaringClass();
               // Object instance = ManagerSingleton.getInstance(declaringClass);

                if (Modifier.isStatic(field.getModifiers())) {
                    field.set(null, dependency);
                } else {
                    field.set(this, dependency);
                }
                System.out.println("Dependência injetada: " + field.getName() + " na classe " + declaringClass.getSimpleName());
            } else {
                System.out.println("Não foi possível resolver a dependência para o campo: " + field.getName());
            }
        }
    }

    private Object resolverDependencia(Class<?> tipo) {
        if (ProdutoService.class.isAssignableFrom(tipo)) {
            ProdutoRepository<?, ?> repository = PersistenciaFactory.getProdutoRepository(1);
            if (repository == null) {
                throw new IllegalArgumentException("ProdutoRepository não pode ser nulo");
            }
            return new ProdutoService((ProdutoRepository<Produto, Integer>) repository);
        }
        throw new IllegalArgumentException("Não foi possível resolver a dependência para o tipo: " + tipo.getName());
    }
}
