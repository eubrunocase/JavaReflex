package br.com.ucsal.controller;

import br.com.ucsal.controller.annotation.Rota;
import br.com.ucsal.controller.annotation.Singleton;
import br.com.ucsal.controller.managers.InjectionManager;
import br.com.ucsal.controller.managers.SingletonManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebListener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

@WebListener
public class InicializadorListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources("");

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());

                if (directory.exists() && directory.isDirectory()) {
                    processDirectory(directory, context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Inicializando recursos na inicialização da aplicação");
    }

    private void processDirectory(File directory, ServletContext context) {
        if (directory != null && directory.exists() && directory.isDirectory()) {

            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    processDirectory(file, context);
                } else if (file.getName().endsWith(".class")) {
                    try {
                        System.out.println("Inicializando classe " + file.getPath());
                        String className = file.getPath()
                                .replace(File.separator, ".")
                                .replaceFirst(".*?.classes.", "")
                                .replace(".class", "");

                        System.out.println("TENTANDO CARREGAR A CLASSE: " + className);
                        Class<?> clazz = Class.forName(className, true, context.getClassLoader());
                        System.out.println("CLASSE CARREGADA: " + file.getPath());

                        if (clazz.isAnnotationPresent(Singleton.class)) {
                            SingletonManager.getInstance(clazz);
                            System.out.println("CLASSE ANOTADA COM @SINGLETON INICIALIZADA: " + className);
                        }

                        Object instance = clazz.getDeclaredConstructor().newInstance();
                        InjectionManager.injectDependencies(instance); // Injentando a dependencia

                        if (clazz.isAnnotationPresent(Rota.class)) {
                            Rota rota = clazz.getAnnotation(Rota.class);
                            Object servlet = clazz.getDeclaredConstructor().newInstance();
                            context.addServlet(clazz.getSimpleName(), (jakarta.servlet.Servlet) servlet)
                                    .addMapping(rota.caminho());

                            System.out.println("ROTA REGISTRADA: " + rota.caminho());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("ERRO AO TENTAR INICIALIZAR CLASSE: " + file.getName());
                    }
                }
            }
        } else {
            System.err.println("DIRETORIO INVALIDO/VAZIO: " + directory.getAbsolutePath());
           }
        }
    }









