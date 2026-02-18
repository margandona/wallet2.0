<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Buscar Usuario - AlkeWallet</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <header class="app-header">
            <h1>🔍 Buscar Usuario</h1>
            <p class="subtitle">Encuentra un usuario por su email</p>
        </header>
        
        <main class="card" role="main">
            <form class="form" method="post" action="<%= request.getContextPath() %>/usuarios/buscar" aria-label="Formulario de búsqueda de usuario">
                <label for="emailBusqueda">
                    Email <span aria-label="requerido">*</span>
                    <input 
                        type="email" 
                        id="emailBusqueda"
                        name="email" 
                        placeholder="ejemplo@correo.com"
                        required
                        aria-required="true">
                </label>
                <div class="actions">
                    <button type="submit">🔍 Buscar</button>
                    <a class="muted" href="<%= request.getContextPath() %>/">Volver</a>
                </div>
            </form>

            <%@ include file="partials/messages.jsp" %>

            <% if (request.getAttribute("usuario") != null) { %>
                <% com.wallet.application.dtos.UsuarioDTO usuario =
                    (com.wallet.application.dtos.UsuarioDTO) request.getAttribute("usuario"); %>
                <section class="alert success" role="region" aria-label="Información del usuario encontrado">
                    <h2 style="margin-top: 0; font-size: 1.2rem;">✅ Usuario encontrado</h2>
                    <div style="display: grid; gap: 0.75rem; margin-top: 1rem;">
                        <div>
                            <span class="text-muted">ID:</span> 
                            <span class="badge badge-info"><%= usuario.getId() %></span>
                        </div>
                        <div>
                            <span class="text-muted">Nombre completo:</span> 
                            <strong style="font-size: 1.1rem;"><%= usuario.getNombreCompleto() %></strong>
                        </div>
                        <div>
                            <span class="text-muted">Email:</span> 
                            <strong><%= usuario.getEmail() %></strong>
                        </div>
                        <div>
                            <span class="text-muted">Documento:</span> 
                            <span class="badge"><%= usuario.getTipoDocumento() %></span> 
                            <strong><%= usuario.getNumeroDocumento() %></strong>
                        </div>
                    </div>
                </section>
            <% } %>
        </main>
        
        <footer class="app-footer">
            <p>&copy; 2026 AlkeWallet | <a href="<%= request.getContextPath() %>/">Inicio</a></p>
        </footer>
    </div>
</body>
</html>
