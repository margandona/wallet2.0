<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Usuarios</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <div class="card">
            <h1>Usuarios</h1>
            
            <form class="form" method="post" action="<%= request.getContextPath() %>/usuarios/lista">
                <fieldset>
                    <legend>Filtros</legend>
                    <label>Buscar por email
                        <input type="text" name="email" value="<%= request.getAttribute("emailFiltro") != null ? request.getAttribute("emailFiltro") : "" %>" placeholder="ejemplo@correo.com">
                    </label>
                    <label>Estado
                        <select name="estado">
                            <option value="TODOS" <%= "TODOS".equals(request.getAttribute("estadoFiltro")) || request.getAttribute("estadoFiltro") == null ? "selected" : "" %>>Todos</option>
                            <option value="ACTIVOS" <%= "ACTIVOS".equals(request.getAttribute("estadoFiltro")) ? "selected" : "" %>>Solo activos</option>
                            <option value="INACTIVOS" <%= "INACTIVOS".equals(request.getAttribute("estadoFiltro")) ? "selected" : "" %>>Solo inactivos</option>
                        </select>
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
                    <button type="submit">Buscar</button>
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
                
                <div class="muted">
                    Total: <strong><%= totalUsuarios %></strong> | 
                    Pagina <strong><%= paginaActual %></strong> de <strong><%= totalPaginas %></strong>
                </div>
                
                <table>
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Email</th>
                            <th>Documento</th>
                            <th>Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (usuarios.isEmpty()) { %>
                            <tr>
                                <td colspan="4" style="text-align: center;">No se encontraron usuarios</td>
                            </tr>
                        <% } else { %>
                            <% for (com.wallet.application.dtos.UsuarioDTO u : usuarios) { %>
                                <tr>
                                    <td><%= u.getNombreCompleto() %></td>
                                    <td><%= u.getEmail() %></td>
                                    <td><%= u.getTipoDocumento() %> - <%= u.getNumeroDocumento() %></td>
                                    <td><span class="badge"><%= u.isActivo() ? "Activo" : "Inactivo" %></span></td>
                                </tr>
                            <% } %>
                        <% } %>
                    </tbody>
                </table>
                
                <% if (totalPaginas > 1) { %>
                    <div class="actions" style="margin-top: 1rem;">
                        <% if (paginaActual > 1) { %>
                            <form method="post" action="<%= request.getContextPath() %>/usuarios/lista" style="display: inline;">
                                <input type="hidden" name="pagina" value="<%= paginaActual - 1 %>">
                                <input type="hidden" name="tamano" value="<%= tamano %>">
                                <input type="hidden" name="email" value="<%= emailFiltro %>">
                                <input type="hidden" name="estado" value="<%= estadoFiltro %>">
                                <button type="submit">← Anterior</button>
                            </form>
                        <% } %>
                        
                        <% if (paginaActual < totalPaginas) { %>
                            <form method="post" action="<%= request.getContextPath() %>/usuarios/lista" style="display: inline;">
                                <input type="hidden" name="pagina" value="<%= paginaActual + 1 %>">
                                <input type="hidden" name="tamano" value="<%= tamano %>">
                                <input type="hidden" name="email" value="<%= emailFiltro %>">
                                <input type="hidden" name="estado" value="<%= estadoFiltro %>">
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
