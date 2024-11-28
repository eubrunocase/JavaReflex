package br.com.ucsal.controller;

import br.com.ucsal.controller.annotation.Inject;
import br.com.ucsal.controller.annotation.Service;
import br.com.ucsal.controller.annotation.Singleton;
import br.com.ucsal.controller.manager.Injetor;
import br.com.ucsal.controller.manager.ManagerSingleton;
import br.com.ucsal.persistencia.MemoriaProdutoRepository;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.reflections.Reflections;
import java.io.File;
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

                        Class<?> clazz = Class.forName(className, true, servletContext.getClassLoader());

                        Reflections reflectionsS = new Reflections("br.com.ucsal");
                        Set<?> types = reflectionsS.getTypesAnnotatedWith(Singleton.class);


                            if (clazz.isAnnotationPresent(Singleton.class)) {
                                MemoriaProdutoRepository.getInstancia();
                                System.out.println("Classe anotada com @Singleton inicializada: " + className);
                            }


                            if (clazz.isAnnotationPresent(Inject.class)) {
                                Object instance = clazz.getDeclaredConstructor().newInstance();
                                Injetor.injetarDependencias(instance);
                            }


                    } catch (Exception e) {
                        System.out.println("ERRO AO CARREGAR A CLASSE " + file.getName());
                    }
                }
            }
        }
    }
}
