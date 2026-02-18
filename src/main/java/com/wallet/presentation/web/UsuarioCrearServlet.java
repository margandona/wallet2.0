package com.wallet.presentation.web;

import com.wallet.application.dtos.UsuarioDTO;
import com.wallet.application.dtos.requests.CrearUsuarioRequest;
import com.wallet.infrastructure.services.UsuarioService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Web controller for user creation.
 */
@WebServlet(name = "UsuarioCrearServlet", urlPatterns = {"/usuarios/nuevo"})
public class UsuarioCrearServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombre = WebFormUtils.trimmed(request, "nombre");
        String apellido = WebFormUtils.trimmed(request, "apellido");
        String email = WebFormUtils.trimmed(request, "email");
        String tipoDocumento = WebFormUtils.trimmed(request, "tipoDocumento");
        String numeroDocumento = WebFormUtils.trimmed(request, "numeroDocumento");

        if (WebFormUtils.isBlank(nombre) || WebFormUtils.isBlank(apellido) ||
            WebFormUtils.isBlank(email) || WebFormUtils.isBlank(tipoDocumento) ||
            WebFormUtils.isBlank(numeroDocumento)) {
            request.setAttribute("error", "Todos los campos son requeridos.");
            forward(request, response);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            request.setAttribute("error", "Email invalido.");
            forward(request, response);
            return;
        }

        try {
            CrearUsuarioRequest crearRequest = new CrearUsuarioRequest(
                nombre, apellido, email, tipoDocumento, numeroDocumento
            );
            UsuarioDTO usuario = usuarioService.crearUsuario(crearRequest);
            request.setAttribute("success", "Usuario creado.");
            request.setAttribute("usuario", usuario);
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }

        forward(request, response);
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/usuarios_nuevo.jsp");
        dispatcher.forward(request, response);
    }
}
