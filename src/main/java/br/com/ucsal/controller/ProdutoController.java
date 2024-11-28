package br.com.ucsal.controller;

import java.io.IOException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/*
* @Author: Bruno Cazé
*
* @Version: 1.0
*
* @Since: 28/11/2024
*
* Classe ProdutoController, anotada com @WebServlet. Esta é uma única classe Servlet do projeto.
* Seguindo o padrão de projeto Command, está classe age como um Servlet Centralizado. Roteando as requisições Http recebidas,
* e enviando para os Comandos corretos.
*
* Nessa última versão, contém apenas o metodo service. Responsável por receber e armazenar as requisições
*
 */
@WebServlet("/view/*")
public class ProdutoController extends HttpServlet {

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


