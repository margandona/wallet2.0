<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Crear usuario</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <div class="card">
            <h1>Crear usuario</h1>
            <form class="form" method="post" action="<%= request.getContextPath() %>/usuarios/nuevo">
                <label>Nombre
                    <input type="text" name="nombre" required>
                </label>
                <label>Apellido
                    <input type="text" name="apellido" required>
                </label>
                <label>Email
                    <input type="email" name="email" required>
                </label>
                <label>Tipo de documento
                    <select name="tipoDocumento" required>
                        <option value="">Seleccione</option>
                        <option value="DNI">DNI</option>
                        <option value="PASAPORTE">PASAPORTE</option>
                        <option value="CEDULA">CEDULA</option>
                    </select>
                </label>
                <label>Numero de documento
                    <input type="text" name="numeroDocumento" required>
                </label>
                <div class="actions">
                    <button type="submit">Crear</button>
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
                </div>
            <% } %>
        </div>
    </div>
</body>
</html>
