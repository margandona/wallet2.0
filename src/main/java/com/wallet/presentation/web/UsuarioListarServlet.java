package com.wallet.presentation.web;

import com.wallet.application.dtos.UsuarioDTO;
import com.wallet.infrastructure.services.UsuarioService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Web controller for listing users with pagination and filters.
 */
@WebServlet(name = "UsuarioListarServlet", urlPatterns = {"/usuarios/lista"})
public class UsuarioListarServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 20;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarListado(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarListado(request, response);
    }

    private void procesarListado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Pagination parameters
        int pagina = parsePagina(request);
        int tamano = parseTamano(request);

        // Filter parameters
        String emailFiltro = WebFormUtils.trimmed(request, "email");
        String estadoFiltro = WebFormUtils.trimmed(request, "estado");

        // Set default pagination attributes (will be overwritten on success)
        request.setAttribute("paginaActual", pagina);
        request.setAttribute("tamano", tamano);
        request.setAttribute("totalUsuarios", 0);
        request.setAttribute("totalPaginas", 0);
        request.setAttribute("emailFiltro", emailFiltro != null ? emailFiltro : "");
        request.setAttribute("estadoFiltro", estadoFiltro != null ? estadoFiltro : "TODOS");

        try {
            // Get all users
            List<UsuarioDTO> usuarios = usuarioService.obtenerTodos();

            // Apply filters
            usuarios = aplicarFiltros(usuarios, emailFiltro, estadoFiltro);

            int totalUsuarios = usuarios.size();

            // Apply pagination
            int totalPaginas = (int) Math.ceil((double) totalUsuarios / tamano);
            if (totalPaginas == 0) totalPaginas = 1; // At least 1 page
            int inicio = (pagina - 1) * tamano;
            int fin = Math.min(inicio + tamano, totalUsuarios);

            if (inicio < totalUsuarios) {
                usuarios = usuarios.subList(inicio, fin);
            } else {
                usuarios = List.of();
            }

            request.setAttribute("usuarios", usuarios);
            request.setAttribute("totalUsuarios", totalUsuarios);
            request.setAttribute("totalPaginas", totalPaginas);

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("usuarios", List.of());
        }

        forward(request, response);
    }

    private int parsePagina(HttpServletRequest request) {
        String paginaStr = WebFormUtils.trimmed(request, "pagina");
        if (WebFormUtils.isBlank(paginaStr)) {
            return 1;
        }
        try {
            int pagina = Integer.parseInt(paginaStr);
            return Math.max(1, pagina);
        } catch (Exception e) {
            return 1;
        }
    }

    private int parseTamano(HttpServletRequest request) {
        String tamanoStr = WebFormUtils.trimmed(request, "tamano");
        if (WebFormUtils.isBlank(tamanoStr)) {
            return DEFAULT_PAGE_SIZE;
        }
        try {
            int tamano = Integer.parseInt(tamanoStr);
            return Math.min(MAX_PAGE_SIZE, Math.max(1, tamano));
        } catch (Exception e) {
            return DEFAULT_PAGE_SIZE;
        }
    }

    private List<UsuarioDTO> aplicarFiltros(List<UsuarioDTO> usuarios, String emailFiltro, String estadoFiltro) {
        // Filter by email
        if (!WebFormUtils.isBlank(emailFiltro)) {
            String emailLower = emailFiltro.toLowerCase();
            usuarios = usuarios.stream()
                .filter(u -> u.getEmail().toLowerCase().contains(emailLower))
                .collect(Collectors.toList());
        }

        // Filter by estado (TODOS, ACTIVOS, INACTIVOS)
        if (!WebFormUtils.isBlank(estadoFiltro) && !estadoFiltro.equals("TODOS")) {
            boolean soloActivos = estadoFiltro.equals("ACTIVOS");
            usuarios = usuarios.stream()
                .filter(u -> u.isActivo() == soloActivos)
                .collect(Collectors.toList());
        }

        return usuarios;
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/usuarios_lista.jsp");
        dispatcher.forward(request, response);
    }
}
