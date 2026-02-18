<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Depositar Fondos - AlkeWallet</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <header class="app-header">
            <h1>💵 Depositar Fondos</h1>
            <p class="subtitle">Realiza un depósito en tu cuenta</p>
        </header>
        
        <main class="card" role="main">
            <form class="form" method="post" action="<%= request.getContextPath() %>/deposito" aria-label="Formulario de depósito">
                <label for="numeroCuentaDep">
                    Número de cuenta <span aria-label="requerido">*</span>
                    <input 
                        type="text" 
                        id="numeroCuentaDep"
                        name="numeroCuenta" 
                        placeholder="Ej: 00001"
                        required
                        aria-required="true">
                </label>
                <label for="montoDep">
                    Monto <span aria-label="requerido">*</span>
                    <input 
                        type="number" 
                        step="0.01" 
                        id="montoDep"
                        name="monto" 
                        placeholder="0.00"
                        required
                        aria-required="true">
                </label>
                <label for="descripcionDep">
                    Descripción
                    <input 
                        type="text" 
                        id="descripcionDep"
                        name="descripcion" 
                        placeholder="Ej: Depósito inicial">
                </label>
                <div class="actions">
                    <button type="submit">✅ Depositar</button>
                    <a class="muted" href="<%= request.getContextPath() %>/">Volver</a>
                </div>
            </form>

            <%@ include file="partials/messages.jsp" %>

            <% if (request.getAttribute("cuenta") != null) { %>
                <% com.wallet.application.dtos.CuentaDTO cuenta =
                    (com.wallet.application.dtos.CuentaDTO) request.getAttribute("cuenta"); %>
                <section class="alert success" role="region" aria-label="Resultado del depósito">
                    <h2 style="margin-top: 0; font-size: 1.2rem;">✅ Depósito realizado exitosamente</h2>
                    <div style="display: grid; gap: 0.5rem; margin-top: 1rem;">
                        <div>
                            <span class="text-muted">Cuenta:</span> 
                            <strong><%= cuenta.getNumeroCuenta() %></strong>
                        </div>
                        <div>
                            <span class="text-muted">Nuevo saldo:</span> 
                            <strong style="font-size: 1.3rem; color: var(--success);"><%= cuenta.getSaldo() %></strong> 
                            <span class="badge badge-info"><%= cuenta.getMoneda() %></span>
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
