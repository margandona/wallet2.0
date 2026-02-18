<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Historial de Transacciones - AlkeWallet</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <header class="app-header">
            <h1>📊 Historial de Transacciones</h1>
            <p class="subtitle">Consulta y filtra tu historial de movimientos</p>
        </header>
        
        <main class="card">
            <form class="form" method="post" action="<%= request.getContextPath() %>/historial" aria-label="Formulario de busqueda de transacciones">
                <label for="numeroCuenta">
                    Numero de cuenta <span aria-label="requerido">*</span>
                    <input 
                        type="text" 
                        id="numeroCuenta"
                        name="numeroCuenta" 
                        value="<%= request.getAttribute("numeroCuenta") != null ? request.getAttribute("numeroCuenta") : "" %>" 
                        placeholder="Ej: 00001"
                        required
                        aria-required="true">
                </label>
                
                <fieldset aria-labelledby="filtros-legend">
                    <legend id="filtros-legend">🔍 Filtros de búsqueda</legend>
                    <label for="tipoTransaccion">Tipo de transacción
                        <select id="tipoTransaccion" name="tipo" aria-label="Seleccionar tipo de transacción">
                            <option value="TODOS" <%= "TODOS".equals(request.getAttribute("tipoFiltro")) ? "selected" : "" %>>Todos</option>
                            <option value="DEPOSITO" <%= "DEPOSITO".equals(request.getAttribute("tipoFiltro")) ? "selected" : "" %>>Depósito</option>
                            <option value="RETIRO" <%= "RETIRO".equals(request.getAttribute("tipoFiltro")) ? "selected" : "" %>>Retiro</option>
                            <option value="TRANSFERENCIA_ENVIADA" <%= "TRANSFERENCIA_ENVIADA".equals(request.getAttribute("tipoFiltro")) ? "selected" : "" %>>Transferencia enviada</option>
                            <option value="TRANSFERENCIA_RECIBIDA" <%= "TRANSFERENCIA_RECIBIDA".equals(request.getAttribute("tipoFiltro")) ? "selected" : "" %>>Transferencia recibida</option>
                        </select>
                    </label>
                    <label for="fechaInicio">Desde
                        <input 
                            type="datetime-local" 
                            id="fechaInicio"
                            name="fechaInicio" 
                            value="<%= request.getAttribute("fechaInicio") != null ? request.getAttribute("fechaInicio") : "" %>"
                            aria-label="Fecha de inicio">
                    </label>
                    <label for="fechaFin">Hasta
                        <input 
                            type="datetime-local" 
                            id="fechaFin"
                            name="fechaFin" 
                            value="<%= request.getAttribute("fechaFin") != null ? request.getAttribute("fechaFin") : "" %>"
                            aria-label="Fecha de fin">
                    </label>
                </fieldset>

                <label for="resultadosPorPagina">📄 Resultados por página
                    <select id="resultadosPorPagina" name="tamano" aria-label="Seleccionar cantidad de resultados por página">
                        <option value="10" <%= request.getAttribute("tamano") != null && (Integer)request.getAttribute("tamano") == 10 ? "selected" : "" %>>10</option>
                        <option value="20" <%= request.getAttribute("tamano") != null && (Integer)request.getAttribute("tamano") == 20 ? "selected" : "" %>>20</option>
                        <option value="50" <%= request.getAttribute("tamano") != null && (Integer)request.getAttribute("tamano") == 50 ? "selected" : "" %>>50</option>
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
                
                <div class="results-info" role="status" aria-live="polite">
                    <span class="badge">Total: <strong><%= totalTransacciones %></strong> transacciones</span>
                    <span class="text-muted">Página <strong><%= paginaActual %></strong> de <strong><%= totalPaginas %></strong></span>
                </div>
                
                <table role="table" aria-label="Tabla de transacciones">
                    <thead>
                        <tr>
                            <th scope="col">📅 Fecha</th>
                            <th scope="col">🔖 Tipo</th>
                            <th scope="col">💵 Monto</th>
                            <th scope="col">💱 Moneda</th>
                            <th scope="col">📝 Descripción</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (transacciones.isEmpty()) { %>
                            <tr>
                                <td colspan="5" style="text-align: center; padding: 2rem;">
                                    <span class="text-muted">No hay transacciones para mostrar</span>
                                </td>
                            </tr>
                        <% } else { %>
                            <% for (com.wallet.application.dtos.TransaccionDTO t : transacciones) { 
                                String badgeClass = "badge";
                                if ("DEPOSITO".equals(t.getTipo())) badgeClass = "badge badge-success";
                                else if ("RETIRO".equals(t.getTipo())) badgeClass = "badge badge-warning";
                                else if (t.getTipo().contains("TRANSFERENCIA")) badgeClass = "badge badge-info";
                            %>
                                <tr>
                                    <td><%= t.getFecha() %></td>
                                    <td><span class="<%= badgeClass %>"><%= t.getTipo() %></span></td>
                                    <td style="font-weight: 500;"><%= t.getMonto() %></td>
                                    <td><%= t.getMoneda() %></td>
                                    <td><%= t.getDescripcion() %></td>
                                </tr>
                            <% } %>
                        <% } %>
                    </tbody>
                </table>
                
                <% if (totalPaginas > 1) { %>
                    <nav class="pagination" aria-label="Navegación de páginas de historial">
                        <% if (paginaActual > 1) { %>
                            <form method="post" action="<%= request.getContextPath() %>/historial" style="display: inline;">
                                <input type="hidden" name="numeroCuenta" value="<%= numeroCuenta %>">
                                <input type="hidden" name="pagina" value="<%= paginaActual - 1 %>">
                                <input type="hidden" name="tamano" value="<%= tamano %>">
                                <input type="hidden" name="tipo" value="<%= tipoFiltro != null ? tipoFiltro : "" %>">
                                <input type="hidden" name="fechaInicio" value="<%= fechaInicio != null ? fechaInicio : "" %>">
                                <input type="hidden" name="fechaFin" value="<%= fechaFin != null ? fechaFin : "" %>">
                                <button type="submit" class="btn-secondary" aria-label="Ir a página anterior">← Anterior</button>
                            </form>
                        <% } %>
                        
                        <span class="pagination-info" role="status" aria-live="polite">
                            Página <%= paginaActual %> de <%= totalPaginas %>
                        </span>
                        
                        <% if (paginaActual < totalPaginas) { %>
                            <form method="post" action="<%= request.getContextPath() %>/historial" style="display: inline;">
                                <input type="hidden" name="numeroCuenta" value="<%= numeroCuenta %>">
                                <input type="hidden" name="pagina" value="<%= paginaActual + 1 %>">
                                <input type="hidden" name="tamano" value="<%= tamano %>">
                                <input type="hidden" name="tipo" value="<%= tipoFiltro != null ? tipoFiltro : "" %>">
                                <input type="hidden" name="fechaInicio" value="<%= fechaInicio != null ? fechaInicio : "" %>">
                                <input type="hidden" name="fechaFin" value="<%= fechaFin != null ? fechaFin : "" %>">
                                <button type="submit" class="btn-secondary" aria-label="Ir a página siguiente">Siguiente →</button>
                            </form>
                        <% } %>
                    </nav>
                <% } %>
            <% } %>
        </main>
        
        <footer class="app-footer">
            <p>&copy; 2026 AlkeWallet | <a href="<%= request.getContextPath() %>/">Inicio</a></p>
        </footer>
    </div>
</body>
</html>
