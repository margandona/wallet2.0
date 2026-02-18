<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Retirar Fondos - AlkeWallet</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <header class="app-header">
            <h1>💸 Retirar Fondos</h1>
            <p class="subtitle">Retira dinero de tu cuenta</p>
        </header>
        
        <main class="card" role="main">
            <form class="form" method="post" action="<%= request.getContextPath() %>/retiro" aria-label="Formulario de retiro">
                <label for="numeroCuentaRet">
                    Número de cuenta <span aria-label="requerido">*</span>
                    <input 
                        type="text" 
                        id="numeroCuentaRet"
                        name="numeroCuenta" 
                        placeholder="Ej: 00001"
                        required
                        aria-required="true">
                </label>
                <label for="montoRet">
                    Monto <span aria-label="requerido">*</span>
                    <input 
                        type="number" 
                        step="0.01" 
                        id="montoRet"
                        name="monto" 
                        placeholder="0.00"
                        required
                        aria-required="true">
                </label>
                <label for="descripcionRet">
                    Descripción
                    <input 
                        type="text" 
                        id="descripcionRet"
                        name="descripcion" 
                        placeholder="Ej: Retiro en cajero">
                </label>
                <div class="actions">
                    <button type="submit">💵 Retirar</button>
                    <a class="muted" href="<%= request.getContextPath() %>/">Volver</a>
                </div>
            </form>

            <%@ include file="partials/messages.jsp" %>

            <% if (request.getAttribute("cuenta") != null) { %>
                <% com.wallet.application.dtos.CuentaDTO cuenta =
                    (com.wallet.application.dtos.CuentaDTO) request.getAttribute("cuenta"); %>
                <section class="alert success" role="region" aria-label="Resultado del retiro">
                    <h2 style="margin-top: 0; font-size: 1.2rem;">✅ Retiro realizado exitosamente</h2>
                    <div style="display: grid; gap: 0.5rem; margin-top: 1rem;">
                        <div>
                            <span class="text-muted">Cuenta:</span> 
                            <strong><%= cuenta.getNumeroCuenta() %></strong>
                        </div>
                        <div>
                            <span class="text-muted">Saldo restante:</span> 
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
