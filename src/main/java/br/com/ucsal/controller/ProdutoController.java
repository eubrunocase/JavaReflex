package br.com.ucsal.controller;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import br.com.ucsal.controller.annotation.Rota;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.reflections.Reflections;


@WebServlet("/view/*")
public class ProdutoController extends HttpServlet {

    private Map<String, Command> commands = new HashMap<>();


    @Override
    public void init() {
        Reflections reflections = new Reflections("br.com.ucsal");
        Set<Class<?>> rotas = reflections.getTypesAnnotatedWith(Rota.class);

        for (Class<?> rotaClass : rotas) {
            Rota rota = rotaClass.getAnnotation(Rota.class);
            String caminho = rota.caminho();
            try {
                Command comando = (Command) rotaClass.getDeclaredConstructor().newInstance();
                commands.put(caminho, comando);
                System.out.println("ROTA REGISTRADA: " + caminho);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao registrar rota: " + caminho, e);
            }
        }
    }


    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        System.out.println("Recebendo requisição no caminho: " + path);

        Command command = commands.get(path);

        if (command != null) {
            try {
                command.execute(request, response);
            } catch (Exception e) {
                System.err.println("Erro ao executar o comando para a rota: " + path);
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno ao processar a requisição");
            }
        } else {
            System.out.println("Rota não encontrada: " + path);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Página não encontrada: " + path);
        }
    }


}


