<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Depositar fondos</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <div class="card">
            <h1>Depositar fondos</h1>
            <form class="form" method="post" action="<%= request.getContextPath() %>/deposito">
                <label>Numero de cuenta
                    <input type="text" name="numeroCuenta" required>
                </label>
                <label>Monto
                    <input type="number" step="0.01" name="monto" required>
                </label>
                <label>Descripcion
                    <input type="text" name="descripcion" placeholder="Deposito">
                </label>
                <div class="actions">
                    <button type="submit">Depositar</button>
                    <a class="muted" href="<%= request.getContextPath() %>/">Volver</a>
                </div>
            </form>

            <%@ include file="partials/messages.jsp" %>

            <% if (request.getAttribute("cuenta") != null) { %>
                <% com.wallet.application.dtos.CuentaDTO cuenta =
                    (com.wallet.application.dtos.CuentaDTO) request.getAttribute("cuenta"); %>
                <div class="alert success">
                    <div>Cuenta: <strong><%= cuenta.getNumeroCuenta() %></strong></div>
                    <div>Saldo: <strong><%= cuenta.getSaldo() %></strong> <%= cuenta.getMoneda() %></div>
                </div>
            <% } %>
        </div>
    </div>
</body>
</html>
