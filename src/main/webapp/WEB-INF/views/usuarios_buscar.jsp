<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Buscar usuario</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <div class="card">
            <h1>Buscar usuario</h1>
            <form class="form" method="post" action="<%= request.getContextPath() %>/usuarios/buscar">
                <label>Email
                    <input type="email" name="email" required>
                </label>
                <div class="actions">
                    <button type="submit">Buscar</button>
                    <a class="muted" href="<%= request.getContextPath() %>/">Volver</a>
                </div>
            </form>

            <%@ include file="partials/messages.jsp" %>

            <% if (request.getAttribute("usuario") != null) { %>
                <% com.wallet.application.dtos.UsuarioDTO usuario =
                    (com.wallet.application.dtos.UsuarioDTO) request.getAttribute("usuario"); %>
                <div class="alert success">
                    <div>ID: <strong><%= usuario.getId() %></strong></div>
                    <div>Nombre: <strong><%= usuario.getNombreCompleto() %></strong></div>
                    <div>Email: <strong><%= usuario.getEmail() %></strong></div>
                    <div>Documento: <strong><%= usuario.getTipoDocumento() %></strong> - <%= usuario.getNumeroDocumento() %></div>
                </div>
            <% } %>
        </div>
    </div>
</body>
</html>
