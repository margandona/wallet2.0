package com.wallet.presentation.web;

import com.wallet.application.dtos.TransaccionDTO;
import com.wallet.infrastructure.services.TransaccionService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Web controller for transfers.
 */
@WebServlet(name = "TransferenciaServlet", urlPatterns = {"/transferencia"})
public class TransferenciaServlet extends HttpServlet {

    private final TransaccionService transaccionService = new TransaccionService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String cuentaOrigen = WebFormUtils.trimmed(request, "cuentaOrigen");
        String cuentaDestino = WebFormUtils.trimmed(request, "cuentaDestino");
        String montoStr = WebFormUtils.trimmed(request, "monto");
        String descripcion = WebFormUtils.trimmed(request, "descripcion");

        if (WebFormUtils.isBlank(descripcion)) {
            descripcion = "Transferencia";
        }

        if (WebFormUtils.isBlank(cuentaOrigen) || WebFormUtils.isBlank(cuentaDestino)) {
            request.setAttribute("error", "Cuenta origen y destino son requeridas.");
            forward(request, response);
            return;
        }

        if (cuentaOrigen.equals(cuentaDestino)) {
            request.setAttribute("error", "La cuenta origen y destino no pueden ser iguales.");
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
            List<TransaccionDTO> transacciones = transaccionService.transferirPorNumero(
                cuentaOrigen, cuentaDestino, monto, descripcion
            );
            request.setAttribute("success", "Transferencia realizada.");
            request.setAttribute("transacciones", transacciones);
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }

        forward(request, response);
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/transferencia.jsp");
        dispatcher.forward(request, response);
    }
}
