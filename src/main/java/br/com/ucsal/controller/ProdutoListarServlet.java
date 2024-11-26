package br.com.ucsal.controller;

import java.io.IOException;
import java.util.List;

import br.com.ucsal.controller.annotation.Inject;
import br.com.ucsal.controller.annotation.Rota;
import br.com.ucsal.controller.annotation.Teste;
import br.com.ucsal.model.Produto;
import br.com.ucsal.persistencia.HSQLProdutoRepository;
import br.com.ucsal.service.ProdutoService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Rota(caminho = "/listarProdutos")
public class ProdutoListarServlet implements Command {

    private static final long serialVersionUID = 1L;

    @Inject
	private ProdutoService produtoService;

//	public ProdutoListarServlet() {
//        produtoService = new ProdutoService(new HSQLProdutoRepository());
//	}

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Produto> produtos = produtoService.listarProdutos();

        request.setAttribute("produtos", produtos);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/produtolista.jsp");
        dispatcher.forward(request, response);
    }


}
