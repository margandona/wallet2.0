<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Transferir Fondos - AlkeWallet</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <header class="app-header">
            <h1>💸 Transferir Fondos</h1>
            <p class="subtitle">Transfiere dinero entre cuentas</p>
        </header>
        
        <main class="card" role="main">
            <form class="form" method="post" action="<%= request.getContextPath() %>/transferencia" aria-label="Formulario de transferencia">
                <label for="cuentaOrigen">
                    Cuenta origen <span aria-label="requerido">*</span>
                    <input 
                        type="text" 
                        id="cuentaOrigen"
                        name="cuentaOrigen" 
                        placeholder="Ej: 00001"
                        required
                        aria-required="true">
                </label>
                <label for="cuentaDestino">
                    Cuenta destino <span aria-label="requerido">*</span>
                    <input 
                        type="text" 
                        id="cuentaDestino"
                        name="cuentaDestino" 
                        placeholder="Ej: 00002"
                        required
                        aria-required="true">
                </label>
                <label for="montoTransf">
                    Monto <span aria-label="requerido">*</span>
                    <input 
                        type="number" 
                        step="0.01" 
                        id="montoTransf"
                        name="monto" 
                        placeholder="0.00"
                        required
                        aria-required="true">
                </label>
                <label for="descripcionTransf">
                    Descripción
                    <input 
                        type="text" 
                        id="descripcionTransf"
                        name="descripcion" 
                        placeholder="Ej: Pago de servicios">
                </label>
                <div class="actions">
                    <button type="submit">➡️ Transferir</button>
                    <a class="muted" href="<%= request.getContextPath() %>/">Volver</a>
                </div>
            </form>

            <%@ include file="partials/messages.jsp" %>

            <% if (request.getAttribute("transacciones") != null) { %>
                <section aria-label="Detalles de la transferencia">
                    <h2>✅ Transferencia realizada exitosamente</h2>
                    <table role="table" aria-label="Tabla de transacciones">
                        <thead>
                            <tr>
                                <th scope="col">🔖 Tipo</th>
                                <th scope="col">💵 Monto</th>
                                <th scope="col">💱 Moneda</th>
                                <th scope="col">📝 Descripción</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% java.util.List<com.wallet.application.dtos.TransaccionDTO> transacciones =
                                (java.util.List<com.wallet.application.dtos.TransaccionDTO>) request.getAttribute("transacciones"); %>
                            <% for (com.wallet.application.dtos.TransaccionDTO t : transacciones) { 
                                String badgeClass = t.getTipo().contains("ENVIADA") ? "badge badge-warning" : "badge badge-success";
                            %>
                                <tr>
                                    <td><span class="<%= badgeClass %>"><%= t.getTipo() %></span></td>
                                    <td style="font-weight: 500;"><%= t.getMonto() %></td>
                                    <td><%= t.getMoneda() %></td>
                                    <td><%= t.getDescripcion() %></td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </section>
            <% } %>
        </main>
        
        <footer class="app-footer">
            <p>&copy; 2026 AlkeWallet | <a href="<%= request.getContextPath() %>/">Inicio</a></p>
        </footer>
    </div>
</body>
</html>
