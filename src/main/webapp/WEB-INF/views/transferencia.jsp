<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Transferir fondos</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <div class="card">
            <h1>Transferir fondos</h1>
            <form class="form" method="post" action="<%= request.getContextPath() %>/transferencia">
                <label>Cuenta origen
                    <input type="text" name="cuentaOrigen" required>
                </label>
                <label>Cuenta destino
                    <input type="text" name="cuentaDestino" required>
                </label>
                <label>Monto
                    <input type="number" step="0.01" name="monto" required>
                </label>
                <label>Descripcion
                    <input type="text" name="descripcion" placeholder="Transferencia">
                </label>
                <div class="actions">
                    <button type="submit">Transferir</button>
                    <a class="muted" href="<%= request.getContextPath() %>/">Volver</a>
                </div>
            </form>

            <%@ include file="partials/messages.jsp" %>

            <% if (request.getAttribute("transacciones") != null) { %>
                <table>
                    <thead>
                        <tr>
                            <th>Tipo</th>
                            <th>Monto</th>
                            <th>Moneda</th>
                            <th>Descripcion</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% java.util.List<com.wallet.application.dtos.TransaccionDTO> transacciones =
                            (java.util.List<com.wallet.application.dtos.TransaccionDTO>) request.getAttribute("transacciones"); %>
                        <% for (com.wallet.application.dtos.TransaccionDTO t : transacciones) { %>
                            <tr>
                                <td><%= t.getTipo() %></td>
                                <td><%= t.getMonto() %></td>
                                <td><%= t.getMoneda() %></td>
                                <td><%= t.getDescripcion() %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>
    </div>
</body>
</html>
