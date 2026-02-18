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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Web controller for transaction history with pagination and filters.
 */
@WebServlet(name = "HistorialServlet", urlPatterns = {"/historial"})
public class HistorialServlet extends HttpServlet {

    private final TransaccionService transaccionService = new TransaccionService();
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 20;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set default attributes for initial page load
        request.setAttribute("numeroCuenta", "");
        request.setAttribute("paginaActual", 1);
        request.setAttribute("tamano", DEFAULT_PAGE_SIZE);
        request.setAttribute("totalTransacciones", 0);
        request.setAttribute("totalPaginas", 1);
        request.setAttribute("tipoFiltro", "TODOS");
        request.setAttribute("fechaInicio", "");
        request.setAttribute("fechaFin", "");
        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String numeroCuenta = WebFormUtils.trimmed(request, "numeroCuenta");

        // Pagination parameters
        int pagina = parsePagina(request);
        int tamano = parseTamano(request);
        
        // Filter parameters
        String tipoFiltro = WebFormUtils.trimmed(request, "tipo");
        String fechaInicioStr = WebFormUtils.trimmed(request, "fechaInicio");
        String fechaFinStr = WebFormUtils.trimmed(request, "fechaFin");

        // Set default attributes (will be overwritten on success)
        request.setAttribute("numeroCuenta", numeroCuenta != null ? numeroCuenta : "");
        request.setAttribute("paginaActual", pagina);
        request.setAttribute("tamano", tamano);
        request.setAttribute("totalTransacciones", 0);
        request.setAttribute("totalPaginas", 0);
        request.setAttribute("tipoFiltro", tipoFiltro != null ? tipoFiltro : "TODOS");
        request.setAttribute("fechaInicio", fechaInicioStr != null ? fechaInicioStr : "");
        request.setAttribute("fechaFin", fechaFinStr != null ? fechaFinStr : "");

        if (WebFormUtils.isBlank(numeroCuenta)) {
            request.setAttribute("error", "Numero de cuenta requerido.");
            forward(request, response);
            return;
        }

        try {
            List<TransaccionDTO> transacciones = transaccionService.consultarHistorial(numeroCuenta);
            
            // Apply filters
            transacciones = aplicarFiltros(transacciones, tipoFiltro, fechaInicioStr, fechaFinStr);
            
            int totalTransacciones = transacciones.size();
            
            // Apply pagination
            int totalPaginas = (int) Math.ceil((double) totalTransacciones / tamano);
            if (totalPaginas == 0) totalPaginas = 1; // At least 1 page
            int inicio = (pagina - 1) * tamano;
            int fin = Math.min(inicio + tamano, totalTransacciones);
            
            if (inicio < totalTransacciones) {
                transacciones = transacciones.subList(inicio, fin);
            } else {
                transacciones = List.of();
            }
            
            request.setAttribute("transacciones", transacciones);
            request.setAttribute("totalTransacciones", totalTransacciones);
            request.setAttribute("totalPaginas", totalPaginas);
            
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("transacciones", List.of());
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

    private List<TransaccionDTO> aplicarFiltros(List<TransaccionDTO> transacciones, 
                                                 String tipoFiltro, 
                                                 String fechaInicioStr, 
                                                 String fechaFinStr) {
        // Filter by type
        if (!WebFormUtils.isBlank(tipoFiltro) && !tipoFiltro.equals("TODOS")) {
            transacciones = transacciones.stream()
                .filter(t -> t.getTipo().equals(tipoFiltro))
                .collect(Collectors.toList());
        }
        
        // Filter by date range
        if (!WebFormUtils.isBlank(fechaInicioStr)) {
            try {
                LocalDateTime fechaInicio = LocalDateTime.parse(fechaInicioStr + ":00", DATE_FORMAT);
                transacciones = transacciones.stream()
                    .filter(t -> !t.getFecha().isBefore(fechaInicio))
                    .collect(Collectors.toList());
            } catch (Exception e) {
                // Ignore invalid date
            }
        }
        
        if (!WebFormUtils.isBlank(fechaFinStr)) {
            try {
                LocalDateTime fechaFin = LocalDateTime.parse(fechaFinStr + ":59", DATE_FORMAT);
                transacciones = transacciones.stream()
                    .filter(t -> !t.getFecha().isAfter(fechaFin))
                    .collect(Collectors.toList());
            } catch (Exception e) {
                // Ignore invalid date
            }
        }
        
        return transacciones;
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/historial.jsp");
        dispatcher.forward(request, response);
    }
}
