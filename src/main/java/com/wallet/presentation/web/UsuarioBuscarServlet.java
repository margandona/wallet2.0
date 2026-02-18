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
import java.util.Optional;

/**
 * Web controller for user lookup.
 */
@WebServlet(name = "UsuarioBuscarServlet", urlPatterns = {"/usuarios/buscar"})
public class UsuarioBuscarServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = WebFormUtils.trimmed(request, "email");

        if (WebFormUtils.isBlank(email)) {
            request.setAttribute("error", "Email requerido.");
            forward(request, response);
            return;
        }

        try {
            Optional<UsuarioDTO> usuario = usuarioService.buscarPorEmail(email);
            if (usuario.isPresent()) {
                request.setAttribute("usuario", usuario.get());
                request.setAttribute("success", "Usuario encontrado.");
            } else {
                request.setAttribute("error", "Usuario no encontrado.");
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }

        forward(request, response);
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/usuarios_buscar.jsp");
        dispatcher.forward(request, response);
    }
}
