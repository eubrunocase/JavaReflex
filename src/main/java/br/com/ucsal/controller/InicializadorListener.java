package br.com.ucsal.controller;

import br.com.ucsal.controller.annotation.Inject;
import br.com.ucsal.controller.annotation.Rota;
import br.com.ucsal.controller.annotation.Singleton;
import br.com.ucsal.controller.manager.Injetor;
import br.com.ucsal.controller.manager.ManagerSingleton;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/*
*  @Author: Bruno Cazé
*
*  @Version: 1.0
*
* @Since: 28/11/2024
*
* Classe inicializadora anotada com @WebListener, nessa classe temos a inicialização da aplicação.
* Temos dois metodos que serão discutidos com mais detalhe logo abaixo, onde os mesmos são os responsáveis por garantir
* a inicialização correta, seguindo as regras.
 */
@WebListener
public class InicializadorListener implements ServletContextListener {


    private Map<String, Command> commands = new HashMap<>();

    /*
    * METODO "contextInitialized"
    *
    * Responsável por iniciar a aplicação, nele temos uma chamada do metodo processarDiretorios que veremos abaixo.
    * No momento de startar a aplicação, teremos a execução desse metodo para que sejam carregados todos os contextos.
    *
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        System.out.println("Inicializando a aplicação...");

        ServletContext context = sce.getServletContext();

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

    /*
    * METODO "processarDiretorios"
    *
    * Este é um metodo onde vemos um exemplo claro do uso da reflexão, onde a utilizamos para que fizesse o carregamento e
    * leitura de todos os diretórios e classes presentes no projeto.
    *
    * Aqui temos o carregamento de classes, carga dinâmica de rotas, inicialização do Singleton.
    * Detalhe para a carga das rotas, onde utilizamos da reflexão para leitura das classes de comando,
    * e no mesmo momento é implementada a injeção de dependência do ProdutoService nas classes de comando, através do uso da anotação @Inject.
    *
     */
    private void ProcessarDiretorios (File directory, ServletContext context) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    ProcessarDiretorios(file, context);
                } else if (file.getName().endsWith(".class")) {

                    try {
                        System.out.println("INICIALIZANDO CLASSE " + file.getPath());
                        String className = file.getPath()
                                .replace(File.separator, ".")
                                .replaceFirst(".*?.classes.", "")
                                .replace(".class", "");

                        System.out.println("TENTANDO CARREGAR A CLASSE " + className);
                        System.out.println("CLASSE CARREGADA COM SUCESSO " + className);

                        Class<?> clazz = Class.forName(className, true, context.getClassLoader());
                        System.out.println(clazz.isAnnotationPresent(Inject.class));

                        if (clazz.isAnnotationPresent(Singleton.class)) {
                                ManagerSingleton.getInstance(clazz);
                                System.out.println("CLASSE ANOTADA COM @SINGLETON INICIALIZADA: " + className);
                            }
                        if (clazz.isAnnotationPresent(Rota.class)) {
                            Rota rota = clazz.getAnnotation(Rota.class);
                            Command servlet = (Command) clazz.getDeclaredConstructor().newInstance();
                            commands.put(rota.caminho(),servlet);
                            Injetor.injetarDependencias(servlet);
                            System.out.println("@ROTA REGISTRADA NO CAMINHO " + rota.caminho());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ERRO AO CARREGAR A CLASSE " + file.getName());
                    }
                }
            }
            context.setAttribute("command",commands);
        } else {
            System.err.println("Diretório inválido ou vazio: " + directory.getAbsolutePath());
        }
    }
}
