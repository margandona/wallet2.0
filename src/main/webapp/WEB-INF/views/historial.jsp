<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Historial de transacciones</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <div class="card">
            <h1>Historial de transacciones</h1>
            <form class="form" method="post" action="<%= request.getContextPath() %>/historial">
                <label>Numero de cuenta
                    <input type="text" name="numeroCuenta" value="<%= request.getAttribute("numeroCuenta") != null ? request.getAttribute("numeroCuenta") : "" %>" required>
                </label>
                
                <fieldset>
                    <legend>Filtros</legend>
                    <label>Tipo de transaccion
                        <select name="tipo">
                            <option value="TODOS" <%= "TODOS".equals(request.getAttribute("tipoFiltro")) ? "selected" : "" %>>Todos</option>
                            <option value="DEPOSITO" <%= "DEPOSITO".equals(request.getAttribute("tipoFiltro")) ? "selected" : "" %>>Deposito</option>
                            <option value="RETIRO" <%= "RETIRO".equals(request.getAttribute("tipoFiltro")) ? "selected" : "" %>>Retiro</option>
                            <option value="TRANSFERENCIA_ENVIADA" <%= "TRANSFERENCIA_ENVIADA".equals(request.getAttribute("tipoFiltro")) ? "selected" : "" %>>Transferencia enviada</option>
                            <option value="TRANSFERENCIA_RECIBIDA" <%= "TRANSFERENCIA_RECIBIDA".equals(request.getAttribute("tipoFiltro")) ? "selected" : "" %>>Transferencia recibida</option>
                        </select>
                    </label>
                    <label>Desde
                        <input type="datetime-local" name="fechaInicio" value="<%= request.getAttribute("fechaInicio") != null ? request.getAttribute("fechaInicio") : "" %>">
                    </label>
                    <label>Hasta
                        <input type="datetime-local" name="fechaFin" value="<%= request.getAttribute("fechaFin") != null ? request.getAttribute("fechaFin") : "" %>">
                    </label>
                </fieldset>

                <label>Resultados por pagina
                    <select name="tamano">
                        <option value="10" <%= request.getAttribute("tamano") != null && (Integer)request.getAttribute("tamano") == 10 ? "selected" : "" %>>10</option>
                        <option value="20" <%= request.getAttribute("tamano") != null && (Integer)request.getAttribute("tamano") == 20 ? "selected" : "" %>>20</option>
                    </select>
                </label>
                
                <input type="hidden" name="pagina" value="1">
                
                <div class="actions">
                    <button type="submit">Consultar</button>
                    <a class="muted" href="<%= request.getContextPath() %>/">Volver</a>
                </div>
            </form>

            <%@ include file="partials/messages.jsp" %>

            <% if (request.getAttribute("transacciones") != null) { %>
                <% 
                    java.util.List<com.wallet.application.dtos.TransaccionDTO> transacciones =
                        (java.util.List<com.wallet.application.dtos.TransaccionDTO>) request.getAttribute("transacciones");
                    Integer paginaActual = (Integer) request.getAttribute("paginaActual");
                    Integer totalPaginas = (Integer) request.getAttribute("totalPaginas");
                    Integer totalTransacciones = (Integer) request.getAttribute("totalTransacciones");
                    Integer tamano = (Integer) request.getAttribute("tamano");
                    String numeroCuenta = (String) request.getAttribute("numeroCuenta");
                    String tipoFiltro = (String) request.getAttribute("tipoFiltro");
                    String fechaInicio = (String) request.getAttribute("fechaInicio");
                    String fechaFin = (String) request.getAttribute("fechaFin");
                %>
                
                <div class="muted">
                    Total: <strong><%= totalTransacciones %></strong> | 
                    Pagina <strong><%= paginaActual %></strong> de <strong><%= totalPaginas %></strong>
                </div>
                
                <table>
                    <thead>
                        <tr>
                            <th>Fecha</th>
                            <th>Tipo</th>
                            <th>Monto</th>
                            <th>Moneda</th>
                            <th>Descripcion</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (transacciones.isEmpty()) { %>
                            <tr>
                                <td colspan="5" style="text-align: center;">No hay transacciones para mostrar</td>
                            </tr>
                        <% } else { %>
                            <% for (com.wallet.application.dtos.TransaccionDTO t : transacciones) { %>
                                <tr>
                                    <td><%= t.getFecha() %></td>
                                    <td><%= t.getTipo() %></td>
                                    <td><%= t.getMonto() %></td>
                                    <td><%= t.getMoneda() %></td>
                                    <td><%= t.getDescripcion() %></td>
                                </tr>
                            <% } %>
                        <% } %>
                    </tbody>
                </table>
                
                <% if (totalPaginas > 1) { %>
                    <div class="actions" style="margin-top: 1rem;">
                        <% if (paginaActual > 1) { %>
                            <form method="post" action="<%= request.getContextPath() %>/historial" style="display: inline;">
                                <input type="hidden" name="numeroCuenta" value="<%= numeroCuenta %>">
                                <input type="hidden" name="pagina" value="<%= paginaActual - 1 %>">
                                <input type="hidden" name="tamano" value="<%= tamano %>">
                                <input type="hidden" name="tipo" value="<%= tipoFiltro != null ? tipoFiltro : "" %>">
                                <input type="hidden" name="fechaInicio" value="<%= fechaInicio != null ? fechaInicio : "" %>">
                                <input type="hidden" name="fechaFin" value="<%= fechaFin != null ? fechaFin : "" %>">
                                <button type="submit">← Anterior</button>
                            </form>
                        <% } %>
                        
                        <% if (paginaActual < totalPaginas) { %>
                            <form method="post" action="<%= request.getContextPath() %>/historial" style="display: inline;">
                                <input type="hidden" name="numeroCuenta" value="<%= numeroCuenta %>">
                                <input type="hidden" name="pagina" value="<%= paginaActual + 1 %>">
                                <input type="hidden" name="tamano" value="<%= tamano %>">
                                <input type="hidden" name="tipo" value="<%= tipoFiltro != null ? tipoFiltro : "" %>">
                                <input type="hidden" name="fechaInicio" value="<%= fechaInicio != null ? fechaInicio : "" %>">
                                <input type="hidden" name="fechaFin" value="<%= fechaFin != null ? fechaFin : "" %>">
                                <button type="submit">Siguiente →</button>
                            </form>
                        <% } %>
                    </div>
                <% } %>
            <% } %>
        </div>
    </div>
</body>
</html>
