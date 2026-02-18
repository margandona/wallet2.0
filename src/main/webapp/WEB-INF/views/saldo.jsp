<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Consultar Saldo - AlkeWallet</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <header class="app-header">
            <h1>💰 Consultar Saldo</h1>
            <p class="subtitle">Consulta el saldo disponible en tu cuenta</p>
        </header>
        
        <main class="card" role="main">
            <form class="form" method="post" action="<%= request.getContextPath() %>/saldo" aria-label="Formulario de consulta de saldo">
                <label for="numeroCuenta">
                    Número de cuenta <span aria-label="requerido">*</span>
                    <input 
                        type="text" 
                        id="numeroCuenta"
                        name="numeroCuenta" 
                        placeholder="Ej: 00001" 
                        required
                        aria-required="true">
                </label>
                <div class="actions">
                    <button type="submit">🔍 Consultar</button>
                    <a class="muted" href="<%= request.getContextPath() %>/">Volver</a>
                </div>
            </form>

            <%@ include file="partials/messages.jsp" %>

            <% if (request.getAttribute("cuenta") != null) { %>
                <% com.wallet.application.dtos.CuentaDTO cuenta =
                    (com.wallet.application.dtos.CuentaDTO) request.getAttribute("cuenta"); %>
                <section class="alert success" role="region" aria-label="Información de la cuenta">
                    <h2 style="margin-top: 0; font-size: 1.2rem;">📊 Información de la Cuenta</h2>
                    <div style="display: grid; gap: 0.75rem; margin-top: 1rem;">
                        <div>
                            <span class="text-muted">Número de cuenta:</span><br>
                            <strong style="font-size: 1.1rem;"><%= cuenta.getNumeroCuenta() %></strong>
                        </div>
                        <div>
                            <span class="text-muted">Saldo disponible:</span><br>
                            <strong style="font-size: 1.5rem; color: var(--success);"><%= cuenta.getSaldo() %></strong> 
                            <span class="badge badge-info"><%= cuenta.getMoneda() %></span>
                        </div>
                        <div>
                            <span class="text-muted">Estado:</span><br>
                            <span class="badge <%= cuenta.isActiva() ? "badge-success" : "badge-danger" %>">
                                <%= cuenta.isActiva() ? "✓ Activa" : "✗ Inactiva" %>
                            </span>
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
