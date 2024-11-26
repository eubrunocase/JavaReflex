package br.com.ucsal.controller;

import br.com.ucsal.controller.annotation.Inject;
import br.com.ucsal.controller.annotation.Rota;
import br.com.ucsal.controller.annotation.Singleton;
import br.com.ucsal.controller.annotation.Teste;
import br.com.ucsal.controller.managers.InjectionManager;
import br.com.ucsal.persistencia.MemoriaProdutoRepository;
import br.com.ucsal.service.ProdutoService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;



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
                            MemoriaProdutoRepository.getInstancia();   // atention
                            System.out.println("CLASSE ANOTADA COM @SINGLETON INICIALIZADA: " + className);
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









