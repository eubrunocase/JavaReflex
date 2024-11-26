package br.com.ucsal.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.com.ucsal.controller.annotation.Inject;
import br.com.ucsal.controller.annotation.Rota;
import br.com.ucsal.model.Produto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.reflections.Reflections;

@WebServlet("/view/*")
public class ProdutoController extends HttpServlet {

    private Map<String, Command> commands = new HashMap<>();

//    @Override
//    public void init() {
//        // Mapeia os comandos
//        commands.put("/editarProduto", new ProdutoEditarServlet());
//        commands.put("/adicionarProduto", new ProdutoAdicionarServlet());
//        commands.put("/excluirProduto", new ProdutoExcluirServlet());
//        commands.put("/listarProdutos", new ProdutoListarServlet());
//        commands.put("/", new ProdutoListarServlet()); // Roteia também a raiz da aplicação para listar produtos
//        // Adicione outros comandos conforme necessário
//        injectDependencies();
//    }

    @Override
    public void init() throws ServletException {
       super.init();
       chargeRoutes();
       injectDependencies();
    }

    private void chargeRoutes() {
        Reflections reflections = new Reflections("br.com.ucsal");
        Set<Class<?>> rotas = reflections.getTypesAnnotatedWith(Rota.class);

        for (Class<?> rotaClass : rotas) {
            Rota rota = rotaClass.getAnnotation(Rota.class);
            String caminho = rota.caminho();

            try {
                Command comando = (Command) rotaClass.getDeclaredConstructor().newInstance();
                commands.put(caminho, comando);
            } catch (Exception e) {
                throw new RuntimeException("ERRO AO INSTANCIAR COMMAND", e);
            }
        }
          for (String caminho : commands.keySet())  {
            System.out.println("ROTA REGISTRADA " + caminho);
        }
    }

    public void injectDependencies() {
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {}
        }
    }

//    public void injectDependencies() {
//        Field[] fields = this.getClass().getDeclaredFields();
//
//        for (Field field : fields) {
//            if (field.isAnnotationPresent(Inject.class)) {
//
//                try {
//                    field.setAccessible(true);
//                    Class<?> fieldType = field.getType();
//                    Produto produto = (Produto) fieldType.getDeclaredConstructor().newInstance();
//                    field.set(this, produto);
//
//                }  catch (Exception e) {
//                     throw new RuntimeException("ERRO AO INSTANCIAR CLASSE " + field.getName(), e);
//                }
//            }
//        }
//    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        System.out.println(path);
        Command command = commands.get(path);

        if (command != null) {
            command.execute(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Página não encontrada");
        }
    }
}


