package com.wallet.presentation.web;

import com.wallet.application.dtos.CuentaDTO;
import com.wallet.infrastructure.services.CuentaService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Web controller for balance checks.
 */
@WebServlet(name = "SaldoServlet", urlPatterns = {"/saldo"})
public class SaldoServlet extends HttpServlet {

    private final CuentaService cuentaService = new CuentaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String numeroCuenta = WebFormUtils.trimmed(request, "numeroCuenta");

        if (WebFormUtils.isBlank(numeroCuenta)) {
            request.setAttribute("error", "Numero de cuenta requerido.");
            forward(request, response);
            return;
        }

        try {
            CuentaDTO cuenta = cuentaService.consultarSaldo(numeroCuenta);
            request.setAttribute("cuenta", cuenta);
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }

        forward(request, response);
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/saldo.jsp");
        dispatcher.forward(request, response);
    }
}
