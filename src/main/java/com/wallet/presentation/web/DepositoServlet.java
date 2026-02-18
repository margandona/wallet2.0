package com.wallet.presentation.web;

import com.wallet.application.dtos.CuentaDTO;
import com.wallet.application.dtos.TransaccionDTO;
import com.wallet.infrastructure.services.CuentaService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Web controller for deposits.
 */
@WebServlet(name = "DepositoServlet", urlPatterns = {"/deposito"})
public class DepositoServlet extends HttpServlet {

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
        String montoStr = WebFormUtils.trimmed(request, "monto");
        String descripcion = WebFormUtils.trimmed(request, "descripcion");

        if (WebFormUtils.isBlank(descripcion)) {
            descripcion = "Deposito";
        }

        if (WebFormUtils.isBlank(numeroCuenta)) {
            request.setAttribute("error", "Numero de cuenta requerido.");
            forward(request, response);
            return;
        }

        BigDecimal monto;
        try {
            monto = WebFormUtils.parseAmount(montoStr);
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            forward(request, response);
            return;
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            request.setAttribute("error", "Monto debe ser mayor a cero.");
            forward(request, response);
            return;
        }

        try {
            TransaccionDTO transaccion = cuentaService.depositarPorNumero(
                numeroCuenta, monto, descripcion
            );
            CuentaDTO cuenta = cuentaService.consultarSaldo(numeroCuenta);
            request.setAttribute("transaccion", transaccion);
            request.setAttribute("cuenta", cuenta);
            request.setAttribute("success", "Deposito realizado.");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }

        forward(request, response);
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/deposito.jsp");
        dispatcher.forward(request, response);
    }
}
