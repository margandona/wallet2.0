<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Lista de Usuarios - AlkeWallet</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <header class="app-header">
            <h1>👥 Lista de Usuarios</h1>
            <p class="subtitle">Gestiona y consulta usuarios del sistema</p>
        </header>
        
        <main class="card" role="main">
            <form class="form" method="post" action="<%= request.getContextPath() %>/usuarios/lista" aria-label="Formulario de filtros de usuarios">
                <fieldset aria-labelledby="filtros-usuarios-legend">
                    <legend id="filtros-usuarios-legend">🔍 Filtros de búsqueda</legend>
                    <label for="emailFiltro">
                        Buscar por email
                        <input 
                            type="text" 
                            id="emailFiltro"
                            name="email" 
                            value="<%= request.getAttribute("emailFiltro") != null ? request.getAttribute("emailFiltro") : "" %>" 
                            placeholder="ejemplo@correo.com"
                            aria-label="Filtrar por email">
                    </label>
                    <label for="estadoFiltro">
                        Estado
                        <select id="estadoFiltro" name="estado" aria-label="Filtrar por estado">
                            <option value="TODOS" <%= "TODOS".equals(request.getAttribute("estadoFiltro")) || request.getAttribute("estadoFiltro") == null ? "selected" : "" %>>Todos</option>
                            <option value="ACTIVOS" <%= "ACTIVOS".equals(request.getAttribute("estadoFiltro")) ? "selected" : "" %>>Solo activos</option>
                            <option value="INACTIVOS" <%= "INACTIVOS".equals(request.getAttribute("estadoFiltro")) ? "selected" : "" %>>Solo inactivos</option>
                        </select>
                    </label>
                </fieldset>

                <label for="tamanoUsuarios">📄 Resultados por página
                    <select id="tamanoUsuarios" name="tamano" aria-label="Seleccionar cantidad de resultados">
                        <option value="10" <%= request.getAttribute("tamano") != null && (Integer)request.getAttribute("tamano") == 10 ? "selected" : "" %>>10</option>
                        <option value="20" <%= request.getAttribute("tamano") != null && (Integer)request.getAttribute("tamano") == 20 ? "selected" : "" %>>20</option>
                        <option value="50" <%= request.getAttribute("tamano") != null && (Integer)request.getAttribute("tamano") == 50 ? "selected" : "" %>>50</option>
                    </select>
                </label>
                
                <input type="hidden" name="pagina" value="1">
                
                <div class="actions">
                    <button type="submit">🔍 Buscar</button>
                    <a class="muted" href="<%= request.getContextPath() %>/">Volver</a>
                </div>
            </form>

            <%@ include file="partials/messages.jsp" %>

            <% if (request.getAttribute("usuarios") != null) { %>
                <% 
                    java.util.List<com.wallet.application.dtos.UsuarioDTO> usuarios =
                        (java.util.List<com.wallet.application.dtos.UsuarioDTO>) request.getAttribute("usuarios");
                    Integer paginaActual = (Integer) request.getAttribute("paginaActual");
                    Integer totalPaginas = (Integer) request.getAttribute("totalPaginas");
                    Integer totalUsuarios = (Integer) request.getAttribute("totalUsuarios");
                    Integer tamano = (Integer) request.getAttribute("tamano");
                    String emailFiltro = (String) request.getAttribute("emailFiltro");
                    String estadoFiltro = (String) request.getAttribute("estadoFiltro");
                    if (emailFiltro == null) emailFiltro = "";
                    if (estadoFiltro == null) estadoFiltro = "TODOS";
                %>
                
                <div class="results-info" role="status" aria-live="polite">
                    <span class="badge">Total: <strong><%= totalUsuarios %></strong> usuarios</span>
                    <span class="text-muted">Página <strong><%= paginaActual %></strong> de <strong><%= totalPaginas %></strong></span>
                </div>
                
                <table role="table" aria-label="Tabla de usuarios">
                    <thead>
                        <tr>
                            <th scope="col">👤 Nombre</th>
                            <th scope="col">📧 Email</th>
                            <th scope="col">🆔 Documento</th>
                            <th scope="col">✅ Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (usuarios.isEmpty()) { %>
                            <tr>
                                <td colspan="4" style="text-align: center; padding: 2rem;">
                                    <span class="text-muted">No se encontraron usuarios</span>
                                </td>
                            </tr>
                        <% } else { %>
                            <% for (com.wallet.application.dtos.UsuarioDTO u : usuarios) { 
                                String estadoBadge = u.isActivo() ? "badge badge-success" : "badge badge-danger";
                            %>
                                <tr>
                                    <td><strong><%= u.getNombreCompleto() %></strong></td>
                                    <td><%= u.getEmail() %></td>
                                    <td>
                                        <span class="badge badge-info"><%= u.getTipoDocumento() %></span> 
                                        <%= u.getNumeroDocumento() %>
                                    </td>
                                    <td>
                                        <span class="<%= estadoBadge %>">
                                            <%= u.isActivo() ? "✓ Activo" : "✗ Inactivo" %>
                                        </span>
                                    </td>
                                </tr>
                            <% } %>
                        <% } %>
                    </tbody>
                </table>
                
                <% if (totalPaginas > 1) { %>
                    <nav class="pagination" aria-label="Navegación de páginas de usuarios">
                        <% if (paginaActual > 1) { %>
                            <form method="post" action="<%= request.getContextPath() %>/usuarios/lista" style="display: inline;">
                                <input type="hidden" name="pagina" value="<%= paginaActual - 1 %>">
                                <input type="hidden" name="tamano" value="<%= tamano %>">
                                <input type="hidden" name="email" value="<%= emailFiltro %>">
                                <input type="hidden" name="estado" value="<%= estadoFiltro %>">
                                <button type="submit" class="btn-secondary" aria-label="Ir a página anterior">← Anterior</button>
                            </form>
                        <% } %>
                        
                        <span class="pagination-info" role="status" aria-live="polite">
                            Página <%= paginaActual %> de <%= totalPaginas %>
                        </span>
                        
                        <% if (paginaActual < totalPaginas) { %>
                            <form method="post" action="<%= request.getContextPath() %>/usuarios/lista" style="display: inline;">
                                <input type="hidden" name="pagina" value="<%= paginaActual + 1 %>">
                                <input type="hidden" name="tamano" value="<%= tamano %>">
                                <input type="hidden" name="email" value="<%= emailFiltro %>">
                                <input type="hidden" name="estado" value="<%= estadoFiltro %>">
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
