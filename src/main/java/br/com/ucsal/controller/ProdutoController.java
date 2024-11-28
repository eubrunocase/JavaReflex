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

//    private Map<String, Command> commands = new HashMap<>();

//    @Override
//    public void init() {
//        Reflections reflections = new Reflections("br.com.ucsal");
//        Set<Class<?>> rotas = reflections.getTypesAnnotatedWith(Rota.class);
//
//        for (Class<?> rotaClass : rotas) {
//            Rota rota = rotaClass.getAnnotation(Rota.class);
//            String caminho = rota.caminho();
//            try {
//                Command comando = (Command) rotaClass.getDeclaredConstructor().newInstance();
//                commands.put(caminho, comando);
//                System.out.println("ROTA REGISTRADA: " + caminho);
//            } catch (Exception e) {
//                throw new RuntimeException("Erro ao registrar rota: " + caminho, e);
//            }
//        }
//    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        System.out.println("RECEBENDO REQUISIÇÃO NA ROTA: " + path );

        Map<String,Command>commands = (Map<String, Command>) request.getServletContext().getAttribute("command");
        Command command = commands.get(path);

        if (command != null) {
            command.execute(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Página não encontrada");
        }
    }

}


