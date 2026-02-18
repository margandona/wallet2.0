<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Crear Usuario - AlkeWallet</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <header class="app-header">
            <h1>👤 Crear Usuario</h1>
            <p class="subtitle">Registra un nuevo usuario en el sistema</p>
        </header>
        
        <main class="card" role="main">
            <form class="form" method="post" action="<%= request.getContextPath() %>/usuarios/nuevo" aria-label="Formulario de creación de usuario">
                <label for="nombre">
                    Nombre <span aria-label="requerido">*</span>
                    <input 
                        type="text" 
                        id="nombre"
                        name="nombre" 
                        placeholder="Ej: Juan"
                        required
                        aria-required="true">
                </label>
                <label for="apellido">
                    Apellido <span aria-label="requerido">*</span>
                    <input 
                        type="text" 
                        id="apellido"
                        name="apellido" 
                        placeholder="Ej: Pérez"
                        required
                        aria-required="true">
                </label>
                <label for="email">
                    Email <span aria-label="requerido">*</span>
                    <input 
                        type="email" 
                        id="email"
                        name="email" 
                        placeholder="ejemplo@correo.com"
                        required
                        aria-required="true">
                </label>
                <label for="tipoDocumento">
                    Tipo de documento <span aria-label="requerido">*</span>
                    <select id="tipoDocumento" name="tipoDocumento" required aria-required="true">
                        <option value="">— Seleccione —</option>
                        <option value="DNI">DNI</option>
                        <option value="PASAPORTE">PASAPORTE</option>
                        <option value="CEDULA">CÉDULA</option>
                    </select>
                </label>
                <label for="numeroDocumento">
                    Número de documento <span aria-label="requerido">*</span>
                    <input 
                        type="text" 
                        id="numeroDocumento"
                        name="numeroDocumento" 
                        placeholder="Ej: 12345678"
                        required
                        aria-required="true">
                </label>
                <div class="actions">
                    <button type="submit">✅ Crear Usuario</button>
                    <a class="muted" href="<%= request.getContextPath() %>/">Volver</a>
                </div>
            </form>

            <%@ include file="partials/messages.jsp" %>

            <% if (request.getAttribute("usuario") != null) { %>
                <% com.wallet.application.dtos.UsuarioDTO usuario =
                    (com.wallet.application.dtos.UsuarioDTO) request.getAttribute("usuario"); %>
                <section class="alert success" role="region" aria-label="Usuario creado exitosamente">
                    <h2 style="margin-top: 0; font-size: 1.2rem;">✅ Usuario creado exitosamente</h2>
                    <div style="display: grid; gap: 0.5rem; margin-top: 1rem;">
                        <div>
                            <span class="text-muted">ID:</span> 
                            <span class="badge badge-info"><%= usuario.getId() %></span>
                        </div>
                        <div>
                            <span class="text-muted">Nombre completo:</span> 
                            <strong><%= usuario.getNombreCompleto() %></strong>
                        </div>
                        <div>
                            <span class="text-muted">Email:</span> 
                            <strong><%= usuario.getEmail() %></strong>
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
